package de.einfachtanken;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.AsyncTask;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.http.client.methods.HttpGet;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

/**
 * HTTP GET Request
 * Perform the request in a background task
 */
public class HttpGetTask extends AsyncTask<Void, Void, String>
{

    protected boolean mError = false;
    protected Throwable mThrowable = null;
    private String mUrl = "";
    private DownloadListener mListener = null;

    /**
     * Constructor
     *
     * @param listener Get notified when the download finished
     * @param activity Activity that started the download
     * @param url      URL for GET request
     */
    public HttpGetTask(DownloadListener listener, String url)
    {
        mUrl = url;
        mError = false;
        mListener = listener;
    }

    String download(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    @Override
    protected String doInBackground(Void... params)
    {
        mError = false;
        String result = "";

        try {
            result = download(mUrl);
        } catch(Exception e) {
            mError = true;
            mThrowable = e;
            return "";
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result)
    {
        if(mListener == null) return;
        if (mError || result == null || result.length() == 0)
        {
            mListener.onFailed(mThrowable);
            return;
        }

        mListener.onFileDownloaded(result);

    }
}
