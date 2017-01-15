package org.hatsuyuki.proxy;

import org.apache.commons.io.IOUtils;
import org.hatsuyuki.Json;
import org.jsoup.Connection;

import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Hatsuyuki.
 */
public class Proxy extends AbstractProxy {
    private static final String ENCODING = "UTF-8";
    private String host;
    private int port;
    private String clientID;

    private int timeout = 600 * 1000; // default timeout is 10 minutes

    public Proxy(String host, int port, String clientID) throws Exception {
        this.host = host;
        this.port = port;
        this.clientID = clientID;
        if (!checkConnection()) {
            throw new Exception("Failed to connect to proxy server");
        }
    }

    private boolean checkConnection() {
        try {
            Socket socket = new Socket(host, port);
            socket.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Response request(Connection jsoupConnection)throws Exception {
        Socket socket = null;
        try {
            socket = new Socket();
            socket.setSoTimeout(timeout);
            socket.connect(new InetSocketAddress(host, port), timeout);

            Request request = new Request(jsoupConnection);
            request.source = this.clientID;

            OutputStream out = socket.getOutputStream();
            String jsonRequest = Json.toString(request);
            IOUtils.write(jsonRequest, out, ENCODING);
            socket.shutdownOutput();

            InputStream in = socket.getInputStream();
            String jsonResponse = IOUtils.toString(in, ENCODING);
            Response response = Json.parse(jsonResponse, Response.class);
            socket.shutdownInput();
            socket.close();

            return response;
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
