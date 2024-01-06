package main

import (
	"errors"
	"flag"
	"fmt"
	"github.com/hemicharly/grpc-spring-boot-3/gateway"
	"golang.org/x/net/context"
	"log"
	"net/http"
)

var (
	port = flag.Int("p", 8085, "Port of the service")
)

func main() {
	flag.Parse()

	ctx, cancel := context.WithCancel(context.Background())
	defer cancel()

	mux, err := gateway.NewGateway(ctx)
	if err != nil {
		log.Printf("Setting up the gateway: %s", err.Error())
		return
	}

	srvAddr := fmt.Sprintf(":%d", *port)

	s := &http.Server{
		Addr:    srvAddr,
		Handler: mux,
	}

	go func() {
		<-ctx.Done()
		log.Printf("Shutting down the http server")
		if err := s.Shutdown(context.Background()); err != nil {
			log.Printf("Failed to shutdown http server: %v", err)
		}
	}()

	log.Printf("Starting listening at %s", srvAddr)
	if err := s.ListenAndServe(); !errors.Is(err, http.ErrServerClosed) {
		log.Printf("Failed to listen and serve: %v", err)
	}
}
