package diy.restlite;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import diy.uigeneric.R;

/**
 * An HTTP REST with minimal passing parameters.
 */
public class HttpRestLite {

    private static final String TAG = HttpRestLite.class.getSimpleName();

    private String url;               // URL to send REST
    private String request;           // request method
    private boolean isStarted;        // started flag
    private HttpAsyncTask asyncTask;  // HTTP async task
    private ProgressDialog dialog;    // optional to display progress dialog, if set

    public class HttpResult {
        public JSONObject data;
        public List<String> cookie;
    }

    public interface ResultListener {
        /**
         * @param result
         */
        void success(HttpResult result);

        /**
         * @param code
         */
        void failed(int code);
    }

    // error codes
    public static final int EUNKNOWN = -1;
    public static final int ETIMEOUT = -2;
    public static final int EUNREACH = -3;

    private ResultListener listener;
    private JSONObject result;
    private int errorCode;

    public HttpRestLite(@NonNull String url, @Nullable String request, @NonNull ResultListener listener) {
        super();
        this.url = url;
        this.request = request;
        this.isStarted = false;
        this.asyncTask = null;
        this.dialog = null;
        this.listener = listener;
        if (this.request == null || this.request.equals(""))
            this.request = "GET";
        Log.d(TAG, "initialized REST with " + request + " " + url);
    }

    public void setDialog(@Nullable ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public boolean execute(@Nullable Map<String, String> params, @Nullable String cookie) {
        if (!isStarted) {
            try {
                // executes HTTP tasks
                Log.d(TAG, "execute " + request + " " + url);
                if (params != null)
                    for (String name : params.keySet())
                        Log.d(TAG, "param: " + name + "=" + params.get(name));
                if (cookie != null)
                    Log.d(TAG, "cookie: " + cookie);

                HttpTransfer data = new HttpTransfer();
                data.url = url;
                data.request = request;
                data.params = params;
                data.cookie = cookie;

                asyncTask = new HttpAsyncTask();
                asyncTask.execute(data);
                isStarted = true;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        else
            return false;
    }

    public boolean isStart() {
        return isStarted;
    }

    public boolean cancel() {
        if (isStarted) {
            isStarted = false;
            if (dialog != null)
                dialog.dismiss();
            return asyncTask.cancel(true);
        }
        else
            return false;
    }

    public boolean isCancel() {
        return asyncTask != null && asyncTask.isCancelled();
    }

    private class HttpTransfer {
        public String url;
        public String request;
        public Map<String, String> params;
        public String cookie;
    }

    private class HttpAsyncTask extends AsyncTask<HttpTransfer, Void, HttpResult> {

        @Override
        protected HttpResult doInBackground(HttpTransfer... data) {
            if (data.length <= 0)
                return null;
            HttpTransfer datum = data[0];
            HttpResult result = null;
            errorCode = 0;
            try {
                String parameters = "";
                if (datum.params != null) {
                    for (String name : datum.params.keySet()) {
                        if (parameters.length() > 0) parameters += "&";
                        parameters += name + "=" + URLEncoder.encode(datum.params.get(name), "UTF-8");
                    }
                }
                String target = datum.url;
                if (datum.request.equals("GET") && !parameters.equals("")) {
                    if (target.contains("?"))
                        target += "&" + parameters;
                    else
                        target += "?" + parameters;
                }
                Log.d(TAG, "starting " + target);

                URL url = new URL(target);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
                    connection.setRequestMethod(datum.request);
                    connection.setReadTimeout(10000);
                    connection.setConnectTimeout(15000);
                    connection.setDoInput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    if (datum.request.equals("GET")) {
                        connection.setUseCaches(true);
                    }
                    else {
                        connection.setUseCaches(false);
                        connection.setRequestProperty("Content-Length", "" + parameters.getBytes().length);
                        if (parameters.getBytes().length > 0)
                            connection.setDoOutput(true);
                    }
                    if (datum.cookie != null)
                        connection.setRequestProperty("Cookie", datum.cookie);

                    if (!datum.request.equals("GET") && parameters.getBytes().length > 0) {
                        OutputStream output = connection.getOutputStream();
                        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                        writer.writeBytes(parameters);
                        writer.flush();
                        writer.close();
                        output.close();
                    }

                    int response = connection.getResponseCode();
                    if (response == 200) {
                        Log.d(TAG, "response is " + response);
                        InputStream stream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }

                        Map<String, List<String>> header = connection.getHeaderFields();
                        result = new HttpResult();
                        result.data = new JSONObject(builder.toString());
                        result.cookie = header.get("Set-Cookie");
                    }
                    else {
                        Log.d(TAG, "response error is " + response);
                    }
                }
                finally {
                    connection.disconnect();
                }
            }
            catch (ConnectException e) {
                e.printStackTrace();
                errorCode = EUNREACH;
            }
            catch (IOException e) {
                e.printStackTrace();
                errorCode = ETIMEOUT;
            }
            catch (Exception e) {
                e.printStackTrace();
                errorCode = EUNKNOWN;
            }

            if (isCancelled())
                return null;
            return result;
        }

        @Override
        protected void onPostExecute(HttpResult result) {
            if (dialog != null)
                dialog.dismiss();
            if (!isCancelled()) {
                if (result != null)
                    listener.success(result);
                else
                    listener.failed(errorCode);
            }
            isStarted = false;
        }
    }

    public static String getErrorMessage(Context context, int code) {
        switch (code) {
            case 0: // no error
                return context.getString(R.string.restlite_NOERRORS);
            case EUNKNOWN:
                return context.getString(R.string.restlite_EUNKNOWN);
            case ETIMEOUT:
                return context.getString(R.string.restlite_ETIMEOUT);
            case EUNREACH:
                return context.getString(R.string.restlite_EUNKNOWN);
            default:
                return context.getString(R.string.restlite_EUNKNOWN);
        }
    }

}
