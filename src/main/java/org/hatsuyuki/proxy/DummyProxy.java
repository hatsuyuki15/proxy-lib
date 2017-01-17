package org.hatsuyuki.proxy;

import org.hatsuyuki.proxy.exception.NetworkError;
import org.jsoup.Connection;

/**
 * Created by Hatsuyuki on 12/27/2016.
 */
public class DummyProxy extends AbstractProxy {
    private Pipeline pipeline = new LocalRequester();

    @Override
    public Response request(Connection jsoupConnection) throws Exception {
        Request request = new Request(jsoupConnection);
        request.source = "local";
        Response response = pipeline.forward(request);

        if (response.statusCode != 200) {
            throw new NetworkError(String.format("HTTP Code = %s | Message = %s", response.statusCode, response.statusMessage));
        }

        return response;
    }
}
