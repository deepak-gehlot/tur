package tutorialance.widevision.com.tutorialance.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class NetworkUtils
{
    public static void asyncQuery(final Request.Builder requestBuilder, final AsyncCallback<String> listener)
    {
        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.setConnectTimeout(30, TimeUnit.SECONDS);
        httpClient.setWriteTimeout(30, TimeUnit.SECONDS);
        httpClient.setReadTimeout(30, TimeUnit.SECONDS);
        httpClient.newCall(requestBuilder.build()).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, final IOException e)
            {
//                Log.e("Request Failed", "Failed to get response for get request");
                if (listener != null)
                {
                    mainThreadHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listener.onOperationCompleted(null, e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                InputStream responseStream = response.body().byteStream();
                final String responseString = readInputStream(responseStream);
//                Log.e("Request Passed", "Received response: " + responseString);

                if (listener != null)
                {
                    mainThreadHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            listener.onOperationCompleted(responseString, null);
                        }
                    });
                }
            }
        });

    }

    /**
     * @param builder
     * @param imageScaleDownFactor Optional factor greater than zero that scales the requested bitmap down in order to conserve memory.
     * @param onFinishedCallback
     */
    public static void asyncGetImage(final Request.Builder builder, final int imageScaleDownFactor, final AsyncCallback<Bitmap> onFinishedCallback)
    {
        final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.newCall(builder.build()).enqueue(new Callback()
        {
            @Override
            public void onFailure(Request request, final IOException e)
            {
//                Log.e("Request Failed", "Failed to get response for get request");

                if (onFinishedCallback != null)
                {
                    mainThreadHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onFinishedCallback.onOperationCompleted(null, e);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Response response) throws IOException
            {
                InputStream responseStream = response.body().byteStream();

                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inSampleSize = imageScaleDownFactor;
                bitmapOptions.inPurgeable = true;
                bitmapOptions.inInputShareable = true;

                final Bitmap outputBitmap = BitmapFactory.decodeStream(responseStream, null, bitmapOptions);
                if (onFinishedCallback != null)
                {
                    mainThreadHandler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            onFinishedCallback.onOperationCompleted(outputBitmap, null);
                        }
                    });
                }
            }
        });
    }

    public static void asyncGetImage(final Request.Builder builder, final AsyncCallback<Bitmap> onFinishedCallback)
    {
        asyncGetImage(builder, 1, onFinishedCallback);
    }


    public static boolean isConnectedToInternet(Context mContext)
    {
        if (mContext != null)
        {
            ConnectivityManager connMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connMgr != null)
            {
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isConnected())
                {
                    return true;
                }
            }
        }

        return false;
    }

    public static String readInputStream(InputStream inputStream)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder out = new StringBuilder();
        String line = "";

        try
        {
            while ((line = reader.readLine()) != null)
            {
                out.append(line);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                reader.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return out.toString();
    }
}