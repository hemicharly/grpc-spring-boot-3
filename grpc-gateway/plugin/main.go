package main

import (
	"context"
	"errors"
	"fmt"
	"net/http"

	"github.com/hemicharly/grpc-spring-boot-3/gateway"
)

func init() {
	fmt.Println("KrakenD GRPC plugin loaded!!!")
}

var ClientRegisterer = registerer("grpc-register-server")

type registerer string

var logger Logger = nil

func (registerer) RegisterLogger(v interface{}) {
	l, ok := v.(Logger)
	if !ok {
		return
	}
	logger = l
	logger.Debug(fmt.Sprintf("[PLUGIN: %s] Logger loaded", ClientRegisterer))
}

func (r registerer) RegisterClients(f func(
	name string,
	handler func(context.Context, map[string]interface{}) (http.Handler, error),
)) {
	f(string(r), func(ctx context.Context, extra map[string]interface{}) (http.Handler, error) {
		name, ok := extra["name"].(string)

		if !ok {
			return nil, errors.New("wrong config")
		}
		if name != string(r) {
			return nil, fmt.Errorf("unknown register %s", name)
		}

		return gateway.NewGateway(ctx)
	})
}

func main() {}

type Logger interface {
	Debug(v ...interface{})
	Info(v ...interface{})
	Warning(v ...interface{})
	Error(v ...interface{})
	Critical(v ...interface{})
	Fatal(v ...interface{})
}
