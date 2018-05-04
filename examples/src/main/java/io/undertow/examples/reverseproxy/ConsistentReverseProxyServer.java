package io.undertow.examples.reverseproxy;

import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.proxy.ConsistentHashLoadBalancingProxyClient;
import io.undertow.server.handlers.proxy.ProxyHandler;
import io.undertow.util.Headers;

import java.net.URI;
import java.net.URISyntaxException;

public class ConsistentReverseProxyServer {

    public static void main(final String[] args) {
        try {
            final Undertow server1 = Undertow.builder()
                    .addHttpListener(8081, "localhost")
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(HttpServerExchange exchange) throws Exception {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                            exchange.getResponseSender().send("Server1");
                        }
                    })
                    .build();

            server1.start();

            final Undertow server2 = Undertow.builder()
                    .addHttpListener(8082, "localhost")
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(HttpServerExchange exchange) throws Exception {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                            exchange.getResponseSender().send("Server2");
                        }
                    })
                    .build();
            server2.start();

            final Undertow server3 = Undertow.builder()
                    .addHttpListener(8083, "localhost")
                    .setHandler(new HttpHandler() {
                        @Override
                        public void handleRequest(HttpServerExchange exchange) throws Exception {
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                            exchange.getResponseSender().send("Server3");
                        }
                    })
                    .build();

            server3.start();

            ConsistentHashLoadBalancingProxyClient loadBalancer = new ConsistentHashLoadBalancingProxyClient()
                    .addHost(new URI("http://localhost:8081"), "node1")
                    .addHost(new URI("http://localhost:8082"), "node2")
                    .addHost(new URI("http://localhost:8083"), "node3")
                    .setConnectionsPerThread(20);

            Undertow reverseProxy = Undertow.builder()
                    .addHttpListener(8080, "localhost")
                    .setIoThreads(4)
                    .setHandler(ProxyHandler.builder().setProxyClient(loadBalancer).setMaxRequestTime( 30000).build())
                    .build();
            reverseProxy.start();

        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
