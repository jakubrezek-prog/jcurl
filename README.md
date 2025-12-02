# jcurl 

**jcurl** is a lightweight, self-contained `curl`-like command-line tool written in Java.  
It’s designed for environments where you already have Java installed (like Docker containers or Kubernetes pods) but not `curl` or `wget`.

Run HTTP(S) requests directly from Java:

```bash
java -jar jcurl.jar -X GET "https://httpbin.org/get" -H "Accept: application/json"
```

## Supported curl options
```bash
java -jar jcurl.jar  -h

Usage: jcurl [-hsikvV] [-d=<data>] [-u=<basicAuth>] [-X=<method>]
[-H=<headerPairs>[,<headerPairs>...]]... URL
Lightweight curl-like CLI for REST API debugging in Java.
URL                  Target URL
-d, --data=<data>        HTTP request body data, or @file to read from file
-h, --help               Show this help message and exit.
-H, --header=<headerPairs>[,<headerPairs>...]
HTTP headers
-i, --include            Include response headers
-k, --insecure           Ignore SSL certificate errors
-s, --silent             Silent mode (default)
    --pretty             Pretty-print JSON responses
-u, --user=<basicAuth>   Basic auth username:password
-v, --verbose            Verbose output
-V, --version            Print version information and exit.
-X, --request=<method>   HTTP method
```
## Usage Examples

### Simple GET request
```bash
java -jar jcurl.jar https://jsonplaceholder.typicode.com/posts/1
```

### POST with JSON body
```bash
java -jar jcurl.jar -X POST   -H "Content-Type: application/json"  -d '{"name": "John Doe", "email": "john@example.com"}'   https://jsonplaceholder.typicode.com/posts
```

### Sending Data from a File

You can use the `-d @file` syntax to read the request body from a file:

```bash
echo '{"name":"John"}' > user.json
java -jar jcurl.jar -X POST -H "Content-Type: application/json" -d @user.json https://httpbin.org/post
```

### Include headers and verbose output
```bash
java -jar jcurl.jar -v -i https://httpbin.org/get
```

### Ignore SSL certificate errors

```bash
java -jar jcurl.jar -k https://self-signed.badssl.com/
```

### Basic authentication

```bash
java -jar jcurl.jar -u user:passwd https://httpbin.org/basic-auth/user/passwd
```

### Pretty print json output

```bash
java -jar jcurl.jar https://api.github.com/repos/octocat/hello-world --pretty
```

## Build from source

Requires Java 21 and Maven 3.9+.
```bash
git clone https://github.com/jakubrezek-prog/jcurl.git
cd jcurl
mvn clean package
```

## Wrapper scripts
To simplify usage  [jcurl.bat](jcurl.bat) (Windows) and [jcurl.sh](jcurl.sh) (Linux/macOS) scripts were added
