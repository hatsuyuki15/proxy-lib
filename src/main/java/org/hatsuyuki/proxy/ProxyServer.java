package org.hatsuyuki.proxy;

import com.google.common.base.Stopwatch;
import com.google.common.base.Throwables;
import org.hatsuyuki.proxy.utils.Json;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

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
                // input
                InputStream in = client.getInputStream();
                String jsonRequest = IOUtils.toString(in, ENCODING);
                Request request = Json.parse(jsonRequest, Request.class);
                client.shutdownInput();

                // process
                Response response;
                if (request == null) {
                    response = new Response();
                    response.statusCode(-1);
                    response.statusMessage("Invalid request");
                    response.body(jsonRequest);
                } else {
                    try {
                        Stopwatch stopwatch = Stopwatch.createStarted();
                        response = pipeline.forward(request);
                        LOGGER.debug("req={} proxy={} reqTime={} totalTime={}", request.url(), response.metadata().get("ip"), response.metadata().get("requestTime"), stopwatch.elapsed(TimeUnit.SECONDS));
                    } catch (Exception e) {
                        response = new Response();
                        response.statusCode(-1);
                        response.statusMessage(e.getMessage());
                        response.body(Throwables.getStackTraceAsString(e));
                        LOGGER.error("Exception when handling req=" + request.url(), e);
                    }
                }

                // output
                OutputStream out = client.getOutputStream();
                String jsonResponse = Json.toString(response);
                IOUtils.write(jsonResponse, out, ENCODING);
                client.shutdownOutput();
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                try { client.close(); } catch (IOException ignored) { }
            }
        }
    }
}
