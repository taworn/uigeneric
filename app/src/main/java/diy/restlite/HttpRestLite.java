package diy.restlite;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
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

    private String url;                  // URL to send REST
    private String request;              // request method
    private boolean isStarted;           // started flag
    private boolean isCancelled;         // last execute is cancel
    private ProgressDialog dialog;       // optional to display progress dialog, if set
    private int readTimeout = 10000;     // not read any data timeout
    private int connectTimeout = 15000;  // connection wait timeout

    // transfer data in async execute()
    private class HttpTransfer {
        public Map<String, String> params;
        public String cookie;
        public ResultListener listener;
    }

    // result from execute()
    public class HttpResult {
        public int errorCode;
        public JSONObject json;
        public List<String> cookie;
    }

    public interface ResultListener {
        /**
         * Called when HTTP execute finished.
         */
        void finish(HttpResult result);
    }

    // error codes
    public static final int ERROR_ALREADY_STARTED = -1;
    public static final int ERROR_UNREACH = -2;
    public static final int ERROR_TIMEOUT = -3;
    public static final int ERROR_CANCEL = -4;
    public static final int ERROR_RESPONSE = -5;
    public static final int ERROR_JSON = -6;
    public static final int ERROR_UNKNOWN = -99;

    /**
     * Constructs a REST passing.
     */
    public HttpRestLite() {
        super();
        this.url = "";
        this.request = "GET";
        this.isStarted = false;
        this.dialog = null;
        Log.d(TAG, "initialized REST without URL");
    }

    /**
     * Constructs a REST passing.
     */
    public HttpRestLite(@NonNull String url, @Nullable String request) {
        super();
        this.url = url;
        this.request = request;
        this.isStarted = false;
        this.dialog = null;
        if (this.request == null || this.request.equals(""))
            this.request = "GET";
        Log.d(TAG, "initialized REST with " + request + " " + url);
    }

    /**
     * Sends data directly.
     */
    public HttpResult execute(@Nullable Map<String, String> params, @Nullable String cookie) {
        HttpResult result = new HttpResult();
        result.errorCode = 0;
        result.json = null;
        result.cookie = null;

        // checks if no pending execute()
        if (isStarted) {
            result.errorCode = ERROR_ALREADY_STARTED;
            return result;
        }

        // sets initial flags
        isStarted = true;
        isCancelled = false;

        try {
            // executes HTTP tasks
            Log.d(TAG, "executes " + request + " " + url);
            if (params != null)
                for (String name : params.keySet())
                    Log.d(TAG, "param: " + name + "=" + params.get(name));
            if (cookie != null)
                Log.d(TAG, "cookie: " + cookie);
            String parameters = "";
            if (params != null) {
                for (String name : params.keySet()) {
                    if (parameters.length() > 0) parameters += "&";
                    parameters += name + "=" + URLEncoder.encode(params.get(name), "UTF-8");
                }
            }
            String target = url;
            if (request.equals("GET") && !parameters.equals("")) {
                if (target.contains("?"))
                    target += "&" + parameters;
                else
                    target += "?" + parameters;
            }
            Log.d(TAG, "starting " + target);

            URL url = new URL(target);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
                // prepares
                connection.setRequestMethod(request);
                connection.setReadTimeout(readTimeout);
                connection.setConnectTimeout(connectTimeout);
                connection.setDoInput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                if (request.equals("GET")) {
                    connection.setUseCaches(true);
                }
                else {
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content-Length", "" + parameters.getBytes().length);
                    if (parameters.getBytes().length > 0)
                        connection.setDoOutput(true);
                }
                if (cookie != null)
                    connection.setRequestProperty("Cookie", cookie);
                if (isCancelled) {
                    result.errorCode = ERROR_CANCEL;
                    return result;
                }

                // sends
                if (!request.equals("GET") && parameters.getBytes().length > 0) {
                    OutputStream output = connection.getOutputStream();
                    DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                    writer.writeBytes(parameters);
                    writer.flush();
                    writer.close();
                    output.close();
                }
                if (isCancelled) {
                    result.errorCode = ERROR_CANCEL;
                    return result;
                }

                // receives
                int response = connection.getResponseCode();
                if (response == 200) {
                    Log.d(TAG, "response is " + response);
                    InputStream stream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                        if (isCancelled) {
                            result.errorCode = ERROR_CANCEL;
                            return result;
                        }
                    }

                    // gets result as JSON
                    result.json = new JSONObject(builder.toString());

                    // gets cookie, too
                    Map<String, List<String>> header = connection.getHeaderFields();
                    result.cookie = header.get("Set-Cookie");
                }
                else {
                    Log.d(TAG, "response error is " + response);
                    result.errorCode = ERROR_RESPONSE;
                    return result;
                }
            }
            catch (ProtocolException e) {
                e.printStackTrace();
                result.errorCode = ERROR_UNREACH;
            }
            finally {
                connection.disconnect();
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result.errorCode = ERROR_JSON;
        }
        catch (ConnectException e) {
            e.printStackTrace();
            result.errorCode = ERROR_UNREACH;
        }
        catch (IOException e) {
            e.printStackTrace();
            result.errorCode = ERROR_TIMEOUT;
        }
        finally {
            isCancelled = false;
            isStarted = false;
        }
        return result;
    }

    /**
     * Sends data async.
     */
    public HttpResult execute(@Nullable Map<String, String> params, @Nullable String cookie, @Nullable ResultListener listener) {
        if (listener == null) {
            return execute(params, cookie);
        }
        else {
            final HttpTransfer transfer = new HttpTransfer();
            transfer.params = params;
            transfer.cookie = cookie;
            transfer.listener = listener;

            Thread thread = new Thread() {
                @Override
                public void run() {
                    HttpRestLite.HttpResult result = execute(transfer.params, transfer.cookie);
                    transfer.listener.finish(result);
                    if (dialog != null)
                        dialog.dismiss();
                }
            };
            thread.run();
            return null;
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(@NonNull String url) {
        this.url = url;
    }

    public void setUrl(@NonNull String url, @Nullable String request) {
        setUrl(url);
        setRequest(request);
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(@Nullable String request) {
        this.request = request;
        if (this.request == null || this.request.equals(""))
            this.request = "GET";
    }

    public boolean isStarted() {
        return isStarted;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public boolean cancel() {
        if (isStarted) {
            isStarted = false;
            isCancelled = true;
            if (dialog != null)
                dialog.dismiss();
            return true;
        }
        else
            return false;
    }

    public void setDialog(@Nullable ProgressDialog dialog) {
        this.dialog = dialog;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public static String getErrorMessage(Context context, int code) {
        switch (code) {
            case ERROR_ALREADY_STARTED:
                return context.getString(R.string.restlite_ERROR_ALREADY_STARTED);

            case ERROR_UNREACH:
                return context.getString(R.string.restlite_ERROR_UNREACH);

            case ERROR_TIMEOUT:
                return context.getString(R.string.restlite_ERROR_TIMEOUT);

            case ERROR_CANCEL:
                return context.getString(R.string.restlite_ERROR_CANCEL);

            case ERROR_RESPONSE:
                return context.getString(R.string.restlite_ERROR_RESPONSE);

            case ERROR_JSON:
                return context.getString(R.string.restlite_ERROR_JSON);

            case ERROR_UNKNOWN:
            default:
                return context.getString(R.string.restlite_ERROR_UNKNOWN);
        }
    }

}
