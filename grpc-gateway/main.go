package main

import (
	"flag"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"net/http"
	"path"
	"strings"

	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/v2/runtime"
	gwHello "github.com/hemicharly/grpc-spring-boot-3/generated/helloworld"
	"golang.org/x/net/context"
	"google.golang.org/grpc"
)

const (
	grpcPort = "10000"
)

var (
	getEndpoint  = flag.String("get", "localhost:"+grpcPort, "endpoint of YourService")
	postEndpoint = flag.String("post", "localhost:"+grpcPort, "endpoint of YourService")

	swaggerDir = flag.String("swagger_dir", "generated/helloword", "path to the directory which contains swagger definitions")
)

func newGateway(ctx context.Context, opts ...runtime.ServeMuxOption) (http.Handler, error) {
	mux := runtime.NewServeMux(opts...)
	dialOpts := []grpc.DialOption{grpc.WithTransportCredentials(insecure.NewCredentials())}
	err := gwHello.RegisterGreeterHandlerFromEndpoint(ctx, mux, *getEndpoint, dialOpts)
	if err != nil {
		return nil, err
	}

	err = gwHello.RegisterGreeterHandlerFromEndpoint(ctx, mux, *postEndpoint, dialOpts)
	if err != nil {
		return nil, err
	}

	return mux, nil
}

func serveSwagger(w http.ResponseWriter, r *http.Request) {
	if !strings.HasSuffix(r.URL.Path, ".swagger.json") {
		glog.Errorf("Swagger JSON not Found: %s", r.URL.Path)
		http.NotFound(w, r)
		return
	}

	glog.Infof("Serving %s", r.URL.Path)
	p := strings.TrimPrefix(r.URL.Path, "/swagger/")
	p = path.Join(*swaggerDir, p)
	http.ServeFile(w, r, p)
}

func preflightHandler(w http.ResponseWriter, r *http.Request) {
	headers := []string{"Content-Type", "Accept"}
	w.Header().Set("Access-Control-Allow-Headers", strings.Join(headers, ","))
	methods := []string{"GET", "HEAD", "POST", "PUT", "DELETE"}
	w.Header().Set("Access-Control-Allow-Methods", strings.Join(methods, ","))
	glog.Infof("preflight request for %s", r.URL.Path)
	return
}

// allowCORS allows Cross Origin Resoruce Sharing from any origin.
// Don't do this without consideration in production systems.
func allowCORS(h http.Handler) http.Handler {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		if origin := r.Header.Get("Origin"); origin != "" {
			w.Header().Set("Access-Control-Allow-Origin", origin)
			if r.Method == "OPTIONS" && r.Header.Get("Access-Control-Request-Method") != "" {
				preflightHandler(w, r)
				return
			}
		}
		h.ServeHTTP(w, r)
	})
}

func Run(address string, opts ...runtime.ServeMuxOption) error {
	ctx := context.Background()
	ctx, cancel := context.WithCancel(ctx)
	defer cancel()

	mux := http.NewServeMux()

	mux.HandleFunc("/swagger/", serveSwagger)

	gateway, err := newGateway(ctx, opts...)
	if err != nil {
		return err
	}
	mux.Handle("/", gateway)

	return http.ListenAndServe(address, allowCORS(mux))
}

func main() {
	flag.Parse()
	log.Println("Serving gRPC-Gateway on http://0.0.0.0:8085")
	log.Fatalln(Run(":8085"))
}
