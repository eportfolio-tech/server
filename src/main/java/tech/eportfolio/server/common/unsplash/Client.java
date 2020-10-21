package tech.eportfolio.server.common.unsplash;

import okhttp3.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class Client {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String BASE_URL = "https://api.unsplash.com";
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final OkHttpClient client = new OkHttpClient();
    @Value("${unsplash.access.key}")
    private String accessKey;

    public JSONObject randomImage() throws IOException, JSONException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + "/photos/random").newBuilder();
        String url = urlBuilder.build().toString();
        Request request = new Request.Builder().
                addHeader("Accept-Version", "v1")
                .addHeader("Authorization", String.format("%s %s", "Client-ID", accessKey))
                .url(url).build();
        Call call = client.newCall(request);
        Response response = call.execute();
        return new JSONObject(response.body().string());
    }
}
