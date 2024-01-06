package main

import (
	"flag"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"net/http"
	"strings"

	"github.com/golang/glog"
	"github.com/grpc-ecosystem/grpc-gateway/v2/runtime"
	gwCalculator "github.com/hemicharly/grpc-spring-boot-3/generated/calculator"
	"golang.org/x/net/context"
	"google.golang.org/grpc"
)

const (
	grpcServiceCalculator = "192.168.1.77:58081"
)

func newGatewayCalculator(ctx context.Context, opts ...runtime.ServeMuxOption) (http.Handler, error) {
	var (
		getEndpoint  = flag.String("get", grpcServiceCalculator, "endpoint of YourService")
		postEndpoint = flag.String("post", grpcServiceCalculator, "endpoint of YourService")
	)

	mux := runtime.NewServeMux(opts...)
	dialOpts := []grpc.DialOption{grpc.WithTransportCredentials(insecure.NewCredentials())}
	err := gwCalculator.RegisterCalculatorServiceHandlerFromEndpoint(ctx, mux, *getEndpoint, dialOpts)
	if err != nil {
		return nil, err
	}

	err = gwCalculator.RegisterCalculatorServiceHandlerFromEndpoint(ctx, mux, *postEndpoint, dialOpts)
	if err != nil {
		return nil, err
	}

	return mux, nil
}

func preflightHandler(w http.ResponseWriter, r *http.Request) {
	headers := []string{"Content-Type", "Accept"}
	w.Header().Set("Access-Control-Allow-Headers", strings.Join(headers, ","))
	methods := []string{"GET", "HEAD", "POST", "PUT", "DELETE"}
	w.Header().Set("Access-Control-Allow-Methods", strings.Join(methods, ","))
	glog.Infof("preflight request for %s", r.URL.Path)
	return
}

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

	gatewayCalculator, err := newGatewayCalculator(ctx, opts...)
	if err != nil {
		return err
	}
	mux.Handle("/", gatewayCalculator)

	return http.ListenAndServe(address, allowCORS(mux))
}

func main() {
	flag.Parse()
	log.Println("Serving gRPC-Gateway on http://0.0.0.0:8085")
	log.Fatalln(Run(":8085"))
}
