package org.hatsuyuki.proxy;

import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Hatsuyuki.
 */
public class RealProxy extends Proxy {
    private static final String ENCODING = "UTF-8";
    private String host;
    private int port;
    private String clientID;
    private int DEFAULT_TIMEOUT = 60000;

    public RealProxy(String host, int port, String clientID) throws Exception {
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

    @Override
    public Response request(Connection jsoupConnection) throws Exception {
        Socket socket = null;
        try {
            int timeout = jsoupConnection.request().timeout();
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

            if (response == null) {
                throw new Exception("Unexpected exception. RESPONSE is null while RAW RESPONSE is '" + jsonResponse + "'");
            } else {
                // add pointer to the corresponding request
                response.request(request);
                return response;
            }
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
