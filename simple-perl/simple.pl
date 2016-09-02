#!/usr/bin/perl

use strict;
use base qw(Net::Server::HTTP); # On Debian, apt-get install libnet-server-perl
use JSON;
use Data::Dumper;
use CGI;

my $json_content_type = "application/json";
my $plaintext_content_type = "text/plain";

# Start the server in the foreground on given port (default to 8080)
# (Okapi's exec will have forked a new process for us, and will kill it)
my $port = $ARGV[0] || "8080";
die "Need one argument, a port number (not '$port') " 
  unless ( $port !~ /d+/ ) ;
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

  if (!$path) {
    err($cgi,"404 NOTFOUND","Not found (no path given)");
    return;
  }
  if ( $path eq "/hello" && $cgi->request_method() eq "GET" ) {
      response($cgi, "200 OK", $plaintext_content_type, "Hello, world\n");
      return;
    } 
  if ( $path eq "/hello" && $cgi->request_method() eq "POST" ) {
      hello_post_handler($cgi);
      return;
    } 
  # Fall through with other than GET or POST
  err($cgi,"404 NOTFOUND","Not found");
  return;
}

# Produce a HTTP response
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
  if ( $ENV{"HTTP_TRANSFER_ENCODING"} ne "chunked" ) {
    return $cgi->param("POSTDATA");
  }
  my $buf = "";
  while(1) {
    my $len = <STDIN>;
    chomp($len);
    $len =~ s/[^0-9]//g;
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
  }
}


# Handle a POST request to /hello
# 
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




