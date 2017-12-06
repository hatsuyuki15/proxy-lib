package org.hatsuyuki.proxy;

import org.jsoup.Connection;

public class DebugProxy extends Proxy {
    private final RemoteRequester debugger;

    public DebugProxy(String debugHost, int debugPort) {
        this.debugger = new RemoteRequester(debugHost, debugPort);
    }

    @Override
    public Response request(Request request) throws Exception {
        return debugger.forward(request);
    }
}
