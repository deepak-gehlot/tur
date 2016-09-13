package tutorialance.widevision.com.tutorialance.webservices;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import tutorialance.widevision.com.tutorialance.Listener.MyListener;
import tutorialance.widevision.com.tutorialance.util.Constant;
import tutorialance.widevision.com.tutorialance.util.NetworkUtils;

public class JsonParser {

    static InputStream is = null;

    static String json = "";


    // constructor
    public JsonParser() {

    }

    public static void asyncQuery(String url, final MyListener myListener) {
        Request.Builder request = new Request.Builder();
        request.url(url).build();

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        httpClient.setReadTimeout(30, TimeUnit.SECONDS);
        httpClient.newCall(request.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                myListener.onResult("", e);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream responseStream = response.body().byteStream();
                final String responseString = NetworkUtils.readInputStream(responseStream);
                Log.e("Request Passed", "Received response: " + responseString);
                myListener.onResult(responseString, null);
            }
        });

    }
}
