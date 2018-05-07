package io.undertow.server.handlers.proxy;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderValues;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConsistentHashLoadBalancingProxyClient extends LoadBalancingProxyClient {

    public synchronized ConsistentHashLoadBalancingProxyClient addHost(final URI host, String jvmRoute) {
        super.addHost(host, jvmRoute, null);
        return this;
    }

    public ConsistentHashLoadBalancingProxyClient setConnectionsPerThread(int connectionsPerThread) {
        super.connectionsPerThread = connectionsPerThread;
        return this;
    }

    public ConsistentHashLoadBalancingProxyClient setMaxQueueSize(int maxQueueSize) {
        super.maxQueueSize = maxQueueSize;
        return this;
    }

    public ConsistentHashLoadBalancingProxyClient setSoftMaxConnectionsPerThread(int softMaxConnectionsPerThread) {
        super.softMaxConnectionsPerThread = softMaxConnectionsPerThread;
        return this;
    }

    public ConsistentHashLoadBalancingProxyClient setTtl(int ttl) {
        super.ttl = ttl;
        return this;
    }

    protected Host findStickyHost(HttpServerExchange exchange) {
        HeaderValues hv = exchange.getRequestHeaders().get("x-chash-key");
        if (hv != null) {
            String consistentHashKey = hv.getFirst();
            if (consistentHashKey != null) {
                List<String> sortedRoutes = new ArrayList<String>(routes.keySet());
                Collections.sort(sortedRoutes);
                try {
                    int index = KetamaHash.md5HashingAlg(consistentHashKey) % sortedRoutes.size();
                    //System.out.println(String.format("%s %s", chash, index));
                    //adding or removing servers will cause remapping of requests
                    return routes.get(sortedRoutes.get(index));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.findStickyHost(exchange);
    }
}
