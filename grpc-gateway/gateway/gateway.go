package gateway

import (
	"flag"
	"github.com/grpc-ecosystem/grpc-gateway/v2/runtime"
	gwCalculator "github.com/hemicharly/grpc-spring-boot-3/generated/calculator"
	gwHelloworld "github.com/hemicharly/grpc-spring-boot-3/generated/helloworld"
	"golang.org/x/net/context"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
	"log"
	"net/http"
	"os"
)

/*TODO: Add new gateway service here*/

var (
	calculatorEndpoint = flag.String("calculator_endpoint", os.Getenv("GRPC_SERVER_HOST")+":"+os.Getenv("GRPC_SERVER_PORT"), "Endpoint of CalculatorServer")
	helloEndpoint      = flag.String("hello_endpoint", os.Getenv("GRPC_SERVER_HOST")+":"+os.Getenv("GRPC_SERVER_PORT"), "Endpoint of HelloServer")
)

func NewGateway(ctx context.Context) (http.Handler, error) {
	mux := runtime.NewServeMux()
	dialOpts := []grpc.DialOption{grpc.WithTransportCredentials(insecure.NewCredentials())}

	if err := gwCalculator.RegisterCalculatorServiceHandlerFromEndpoint(ctx, mux, *calculatorEndpoint, dialOpts); err != nil {
		log.Printf("Setting up the gateway calculator: %s", err.Error())
		return nil, err
	}

	if err := gwHelloworld.RegisterGreeterHandlerFromEndpoint(ctx, mux, *helloEndpoint, dialOpts); err != nil {
		log.Printf("Setting up the gateway hello world: %s", err.Error())
		return nil, err
	}

	return mux, nil
}
