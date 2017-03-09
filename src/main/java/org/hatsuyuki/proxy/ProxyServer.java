package org.hatsuyuki.proxy;

import org.hatsuyuki.Json;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Hatsuyuki.
 */
public class ProxyServer extends Thread {
    private int port;
    private Pipeline pipeline;
    private final String ENCODING = "UTF-8";
    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProxyServer(int port, Pipeline pipeline) {
        this.port = port;
        this.pipeline = pipeline;
    }

    @Override
    public void run() {
        try {
            ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket client = server.accept();
                Handler handler = new Handler(client);
                handler.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class Handler extends Thread {
        private Socket client;

        public Handler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                InputStream in = client.getInputStream();
                String jsonRequest = IOUtils.toString(in, ENCODING);
                Request request = Json.parse(jsonRequest, Request.class);
                client.shutdownInput();

                if (request != null) {
                    Response response = pipeline.forward(request);

                    OutputStream out = client.getOutputStream();
                    String jsonResponse = Json.toString(response);
                    IOUtils.write(jsonResponse, out, ENCODING);
                    client.shutdownOutput();
                }
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                try { client.close(); } catch (IOException ignored) { }
            }
        }
    }
}
