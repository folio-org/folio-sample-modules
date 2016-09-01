#!/usr/bin/perl
{
package MyWebServer;

use HTTP::Server::Simple::CGI;  # On Debian, apt-get install libhttp-server-simple-perl
use base qw(HTTP::Server::Simple::CGI);
use JSON;
use Data::Dumper;

my $json_content_type = "application/json";
my $plaintext_content_type = "text/plain";

sub print_banner {
  print STDERR "Welcome to the Perl Okapi module\n";
}

sub handle_request {
  my $self = shift;
  my $cgi  = shift;

  my $path = $cgi->path_info();
  my $handler = $dispatch{$path};

  if (!$path) {
    err($cgi,404,"Not found (no path given)");
    return;
  }
  if ( $path eq "/hello" && $cgi->request_method() eq "GET" ) {
      hello_get_handler($cgi);
      return;
    } 
  if ( $path eq "/hello" && $cgi->request_method() eq "POST" ) {
      hello_post_handler($cgi);
      return;
    } 
  # Fall through with other than GET or POST
  err($cgi,404,"Not found");
  return;
}

sub err {
  my $cgi  = shift;
  my $code = shift;
  my $msg = shift;
  print STDERR "Returning error $code: $msg\n";
  print "HTTP/1.0 $code ERROR\r\n";
  print $cgi->header(
    -type => $plaintext_content_type
  );
  print $msg;
}

sub hello_get_handler {
  my $cgi  = shift;
  print STDERR "Get handler\n";
  print "HTTP/1.0 200 OK\r\n";
  print $cgi->header(-type => $plaintext_content_type);
  print "Hello, world\n";
}

sub hello_post_handler {
  my $cgi  = shift;
  print STDERR $cgi->request_method() . " for " . $cgi->path_info() . "\n";
  print STDERR $cgi->content_type() . ":" .$cgi->param("POSTDATA"). "\n";
  my $typ = $cgi->content_type();
  if ($typ ne $json_content_type) {
    err($cgi,400,"Invalid content type '$typ'. Needs to be '$json_content_type'");
    return;
  }
  my $reqdata = $cgi->param("POSTDATA");
  if ( !$reqdata ) {
    err($cgi,400,"Received No content");
  }
  my $json = decode_json($reqdata);
  print STDERR Dumper($json);
  $json->{ 'greeting' } = "Hello, world";
  print STDERR Dumper($json);
  print "HTTP/1.0 200 OK\r\n";
  print $cgi->header(-type => $json_content_type);
  print encode_json($json);  
}



# Run the server in the foreground on 8080
# (Okapi's exec will have forked a new process for us, and will kill it)
my $port = $ARGV[0];
die "Need one argument, a port number (not '$port') " 
  unless ( $port !~ /d+/ ) ;
print STDERR "Okapi Perl module listening on port $port\n";
my $server = MyWebServer->new($port);
$server->port($port);
$server->protocol("HTTP/1.1");
$server->run();
}