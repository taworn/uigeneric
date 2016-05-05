package diy.restlite;

import android.content.Context;
import android.os.AsyncTask;
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

    private int connectTimeout = 15000;      // connection wait timeout
    private int readTimeout = 10000;         // no read data timeout
    private HttpAsyncTask asyncTask = null;  // HTTP async task

    // transfer data in async execute()
    private class Transfer {
        public String uri;
        public String request;
        public Map<String, String> params;
        public String cookie;
        public ResultListener listener;
    }

    // result from execute()
    public class Result {
        public int errorCode;
        public JSONObject json;
        public List<String> cookie;
    }

    // result listener
    public interface ResultListener {
        /**
         * Calls when HTTP execute finished.
         */
        void finish(Result result);
    }

    // error codes
    public static final int ERROR_ALREADY_STARTED = 1;
    public static final int ERROR_UNREACH = 2;
    public static final int ERROR_TIMEOUT = 3;
    public static final int ERROR_RESPONSE = 4;
    public static final int ERROR_JSON = 5;
    public static final int ERROR_UNKNOWN = 99;
    public static final int ERROR_CANCEL = -1;
    public static final int ERROR_CUSTOM = -99;

    /**
     * Constructs.
     */
    public HttpRestLite() {
        super();
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public boolean isStarted() {
        return asyncTask != null;
    }

    public boolean isCancelled() {
        return asyncTask != null && asyncTask.isCancelled();
    }

    /**
     * Cancels the HTTP operation.
     */
    public void cancel() {
        if (asyncTask != null) {
            asyncTask.cancel();
            asyncTask = null;
        }
    }

    /**
     * Calls HTTP data.
     */
    public Result execute(@NonNull String uri, @Nullable String request,
                          @Nullable Map<String, String> params, @Nullable String cookie) {
        // prepares result
        Result result = new Result();
        result.errorCode = 0;
        result.json = null;
        result.cookie = null;

        try {
            // executes HTTP tasks
            if (request == null || request.equals(""))
                request = "GET";
            Log.d(TAG, "executes " + request + " " + uri);
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
            if (request.equals("GET") && !parameters.equals("")) {
                if (uri.contains("?"))
                    uri += "&" + parameters;
                else
                    uri += "?" + parameters;
            }
            Log.d(TAG, "starting " + uri);

            URL url = new URL(uri);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            try {
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

                // sends
                Log.d(TAG, "sends HTTP request");
                if (!request.equals("GET") && parameters.getBytes().length > 0) {
                    OutputStream output = connection.getOutputStream();
                    DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                    writer.writeBytes(parameters);
                    writer.flush();
                    writer.close();
                    output.close();
                }

                // receives
                Log.d(TAG, "receives HTTP response");
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

                    // gets result as JSON
                    Log.d(TAG, "parses result as JSON");
                    result.json = new JSONObject(builder.toString());

                    // gets cookie, too
                    Map<String, List<String>> header = connection.getHeaderFields();
                    result.cookie = header.get("Set-Cookie");
                    return result;
                }
                else {
                    result.errorCode = ERROR_RESPONSE;
                    Log.d(TAG, "response error is " + response);
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
        return result;
    }

    /**
     * Calls HTTP data async.
     */
    public void execute(@NonNull String uri, @Nullable String request,
                        @Nullable Map<String, String> params, @Nullable String cookie,
                        @Nullable ResultListener listener) {
        Transfer transfer = new Transfer();
        transfer.uri = uri;
        transfer.request = request;
        transfer.params = params;
        transfer.cookie = cookie;
        transfer.listener = listener;
        asyncTask = new HttpAsyncTask();
        asyncTask.execute(transfer);
    }

    private class HttpAsyncTask extends AsyncTask<Transfer, Void, Result> {

        private ResultListener listener = null;

        public void cancel() {
            super.cancel(true);
            listener = null;
        }

        @Override
        protected Result doInBackground(Transfer... transfers) {
            if (transfers.length <= 0)
                return null;

            // unpacks transfer object
            Transfer transfer = transfers[0];
            String uri = transfer.uri;
            String request = transfer.request;
            Map<String, String> params = transfer.params;
            String cookie = transfer.cookie;
            listener = transfer.listener;

            // prepares result
            Result result = new Result();
            result.errorCode = 0;
            result.json = null;
            result.cookie = null;

            try {
                // executes HTTP tasks
                if (request == null || request.equals(""))
                    request = "GET";
                Log.d(TAG, "executes " + request + " " + uri);
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
                if (request.equals("GET") && !parameters.equals("")) {
                    if (uri.contains("?"))
                        uri += "&" + parameters;
                    else
                        uri += "?" + parameters;
                }
                Log.d(TAG, "starting " + uri);

                URL url = new URL(uri);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                try {
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
                    if (isCancelled()) {
                        result.errorCode = ERROR_CANCEL;
                        return result;
                    }

                    // sends
                    Log.d(TAG, "sends HTTP request");
                    if (!request.equals("GET") && parameters.getBytes().length > 0) {
                        OutputStream output = connection.getOutputStream();
                        DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                        writer.writeBytes(parameters);
                        writer.flush();
                        writer.close();
                        output.close();
                    }
                    if (isCancelled()) {
                        result.errorCode = ERROR_CANCEL;
                        return result;
                    }

                    // receives
                    Log.d(TAG, "receives HTTP response");
                    int response = connection.getResponseCode();
                    if (response == 200) {
                        Log.d(TAG, "response is " + response);
                        InputStream stream = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                            if (isCancelled()) {
                                result.errorCode = ERROR_CANCEL;
                                return result;
                            }
                        }

                        // gets result as JSON
                        Log.d(TAG, "parses result as JSON");
                        result.json = new JSONObject(builder.toString());

                        // gets cookie, too
                        Map<String, List<String>> header = connection.getHeaderFields();
                        result.cookie = header.get("Set-Cookie");
                        return result;
                    }
                    else {
                        result.errorCode = ERROR_RESPONSE;
                        Log.d(TAG, "response error is " + response);
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
            if (isCancelled()) {
                result.errorCode = ERROR_CANCEL;
            }
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            Log.d(TAG, "HTTP return the result errorCode: " + result.errorCode);
            asyncTask = null;
            if (listener != null)
                listener.finish(result);
        }

        @Override
        protected void onCancelled(Result result) {
            Log.d(TAG, "HTTP has been cancelled");
            asyncTask = null;
            if (listener != null)
                listener.finish(result);
        }

    }

    public static String getErrorMessage(Context context, int code) {
        switch (code) {
            case ERROR_ALREADY_STARTED:
                return context.getString(R.string.restlite_ERROR_ALREADY_STARTED);

            case ERROR_UNREACH:
                return context.getString(R.string.restlite_ERROR_UNREACH);

            case ERROR_TIMEOUT:
                return context.getString(R.string.restlite_ERROR_TIMEOUT);

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
