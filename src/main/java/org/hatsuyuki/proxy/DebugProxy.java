package org.hatsuyuki.proxy;

import org.jsoup.Connection;

public class DebugProxy extends Proxy {
    private final String debugHost;
    private final int debugPort;

    public DebugProxy(String debugHost, int debugPort) {
        this.debugHost = debugHost;
        this.debugPort = debugPort;
    }

    @Override
    public Response request(Connection jsoupConnection) throws Exception {
        return new Response(jsoupConnection.proxy(debugHost, debugPort).execute());
    }
}
