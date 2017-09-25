#!/usr/bin/perl
#
# A simple Okapi module.
# Serves GET and POST requests to /hello and to /simple
# The /hello requests are minimal examples of receiving a request and responding
# The /simple requests are a little bit more complex, they show how a further
# request can be made to Okapi - in this case to the /hello, but it could be
# anywhere.
#
# The module does not do any fancy logging, but outputs some comments on STDERR


use strict;
use warnings;
use base qw(Net::Server::HTTP); # On Debian, apt-get install libnet-server-perl
use JSON;   #   libjson-perl
use Data::Dumper;
use CGI;
use LWP::UserAgent;  # libwww-perl

my $json_content_type = "application/json";
my $plaintext_content_type = "text/plain";

# Start the server in the foreground on given port (default to 8080)
# (Okapi's exec will have forked a new process for us, and will kill it)
my $port = @ARGV ? $ARGV[0] : 8080;
die "Invalid port number: $port"
  if $port !~ /^\d+$/;
print STDERR "Okapi Perl module listening on port $port\n";
main->run(
  port  => $port,
  ipv   => 'IPv4',
);

# This gets called for each request
sub process_http_request {
  my $self = shift;
  #my $cgi  = shift;
  my $cgi = CGI->new;

  my $path = $cgi->path_info();
  my $meth = $cgi->request_method();
  print STDERR "\nsimple.pl received a $meth request for $path\n";

  if (!$path) {
    err($cgi,"404 NOTFOUND","Not found (no path given)");
    return;
  }
  if ( $path eq "/hello" && $meth eq "GET" ) {
      response($cgi, "200 OK", $plaintext_content_type, "Hello, world\n");
      return;
    }
  if ( $path eq "/hello" && $meth eq "POST" ) {
      hello_post_handler($cgi);
      return;
    }
  if ( $path eq "/simple" && $meth eq "GET" ) {
      simple_get_handler($cgi);
      return;
    }
  if ( $path eq "/simple" && $meth eq "POST" ) {
      simple_post_handler($cgi);
      return;
    }
  # Fall through with other than GET or POST
  err($cgi,"404 NOTFOUND","Not found: $path");
  return;
}

############
# Handlers #
############

# Handle a POST request to /hello
# Return the same JSON but with a greeting added to it
sub hello_post_handler {
  my $cgi  = shift;
  my $typ = $cgi->content_type();
  if ($typ ne $json_content_type) {
    err($cgi,400,"Invalid content type '$typ'. Needs to be '$json_content_type'");
    return;
  }
  my $reqdata = postdata($cgi);
  if ( !$reqdata ) {
    err($cgi,400,"Received No content");
  }
  my $json = decode_json($reqdata);
  $json->{ 'greeting' } = "Hello, world";
  response($cgi,"200 OK", $json_content_type, encode_json($json) );
}

# A more complex get handler.
# Makes a request to hello, properly through Okapi.
sub simple_get_handler {
  my $cgi  = shift;
  my $req = httprequest($cgi,"GET", "/hello");
  if (!$req) { #something wrong, probably no X-Okapi-Url
    return; # req has set up the error response
  }
  my $ua = new LWP::UserAgent;
  my $resp = $ua->request($req);
  my $content = $resp->decoded_content();
  chomp($content);
  my $reply = "Simple here. Hello module said '$content'.\n";
  response($cgi, "200 OK", $plaintext_content_type, $reply);
}

# A more complex POST handler. Gets its input data, and posts that to /hello
# Builds a nice response out of what /hello says.
sub simple_post_handler {
  my $cgi  = shift;
  my $typ = $cgi->content_type();
  if ($typ ne $json_content_type) {
    err($cgi,400,"Invalid content type '$typ'. Needs to be '$json_content_type'");
    return;
  }
  my $reqdata = postdata($cgi);
  if ( !$reqdata ) {
    err($cgi,400,"Received No content");
  }
  my $req = httprequest($cgi,"POST", "/hello",$reqdata);
  if (!$req) { #something wrong, probably no X-Okapi-Url
    return; # req has set up the error response
  }
  my $ua = new LWP::UserAgent;
  my $resp = $ua->request($req);
  my $content = $resp->decoded_content();
  chomp($content);
  my $reply = "Simple here. Hello module said '$content'.\n";
  my $json = decode_json($content);
  $json->{ 'simplemessage' } = "Simple module did call the hello module";
  response($cgi,"200 OK", $json_content_type, encode_json($json) );
}


###########
# Helpers #
###########

# Helper to produce a HTTP response
sub response {
  my $cgi  = shift;
  my $code = shift;
  my $contenttype = shift;
  my $result = shift;
  print "HTTP/1.0 $code\r\n";
  print $cgi->header(
    -type => $json_content_type,
  );
  print $result;
}

# Helper to produce an error response
sub err {
  my $cgi  = shift;
  my $code = shift;
  my $msg = shift;
  print STDERR "Returning error $code: $msg\n";
  print "HTTP/1.0 $code\r\n";
  print $cgi->header(
    -type => $plaintext_content_type
  );
  print $msg;
}


# Helper to get the POSTed data
# Either from $cgi, or read from STDIN, if chunked
sub postdata {
  my $cgi  = shift;
  my $enc = $ENV{"HTTP_TRANSFER_ENCODING"} || "";
  if ( $enc ne "chunked" ) {
    my $body = $cgi->param("POSTDATA");
    print STDERR "Received POST data in one go: $body\n";
    return $body;
  }
  my $buf = "";
  while(1) {
    my $len = <STDIN>;
    chomp($len);
    $len =~ s/[^0-9a-fA-F]//g;
    if (! $len ) {
      return $buf;
    }
    my $declen=hex($len);
    my $chunk;
    my $nbytes = read( STDIN, $chunk, $declen);
    if ( $nbytes != $declen ){
      print STDERR "Oops, got $nbytes bytes instead of $declen\n";
      return $buf;
    }
    $buf .= $chunk;
    print STDERR ".. .. Received a chunk of $declen bytes: $chunk\n";
  }
}



# A helper to make a HTTP request
# Takes the base URL from the incoming X-Okapi-Url header
# Appends the path
# Copies all X-Okapi- headers into the request
sub httprequest {
  my ($cgi, $method, $path, $content) = @_;
  my $okapiurl = $ENV{"HTTP_X_OKAPI_URL"} || "";
  if (!$okapiurl) {
    err($cgi,500,"X-Okapi-Url not defined");
    return "";
  }
  print STDERR "simple: okapi is at $okapiurl\n";
  my $url = "$okapiurl/hello";
  my $req = new HTTP::Request $method=> $url;

  # Copy all X-Okapi- headers over to the request
  for my $k ( keys(%ENV) ) {
    if ( $k =~ /^HTTP_(X_OKAPI_.*$)/i ) {
      my $hdr = $1;
      $hdr =~ s/_/-/g;
      print STDERR "Got Found a header $hdr : " . $ENV{$k} . "\n";
      $req->header($hdr => $ENV{$k});
    }
  }
  if (defined $content) {
    $req->content($content);
    $req->content_type($json_content_type);
  }
  return $req;
}

