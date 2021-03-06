package diy.uigeneric.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.amjjd.alphanum.AlphanumericComparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import diy.restlite.HttpRestLite;

/**
 * A SampleServerIndirectList is an implementation of list with sort feature.  The functions are load,
 * reload, sort, search and delete.  This class is convenient to use as list in Android UI.
 */
public class SampleServerIndirectList {

    private static final String TAG = SampleServerIndirectList.class.getSimpleName();

    public static final int SORT_AS_IS = 0;
    public static final int SORT_NAME = 1;
    public static final int SORT_NAME_IGNORE_CASE = 2;
    public static final int SORT_NAME_NATURAL = 3;

    private final Comparator<Integer> compareAsIs = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return list.get(l).compare(list.get(l), list.get(r));
        }
    };
    private final Comparator<Integer> compareAsIsReverse = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return list.get(r).compare(list.get(r), list.get(l));
        }
    };
    private final Comparator<Integer> compareName = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return list.get(l).getName().compareTo(list.get(r).getName());
        }
    };
    private final Comparator<Integer> compareNameReverse = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return list.get(r).getName().compareTo(list.get(l).getName());
        }
    };
    private final Comparator<Integer> compareNameIgnoreCase = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return list.get(l).getName().compareToIgnoreCase(list.get(r).getName());
        }
    };
    private final Comparator<Integer> compareNameIgnoreCaseReverse = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return list.get(r).getName().compareToIgnoreCase(list.get(l).getName());
        }
    };
    private final Comparator<Integer> compareNameNatural = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return comparator.compare(list.get(l).getName(), list.get(r).getName());
        }
    };
    private final Comparator<Integer> compareNameNaturalReverse = new Comparator<Integer>() {
        @Override
        public int compare(Integer l, Integer r) {
            return comparator.compare(list.get(r).getName(), list.get(l).getName());
        }
    };
    private final Comparator<String> comparator = new AlphanumericComparator(Collator.getInstance(Locale.US));

    private List<Sample> list;        // a list of data
    private List<Integer> indexList;  // an integer list used by indices

    // variables to load data
    private Boolean deleted;
    private Integer category;
    private String query;
    private int sortBy;
    private boolean sortReverse;

    // base server address
    private String serverAddress;
    private HttpRestLite rest;

    /**
     * Constructs an indirect list.
     */
    public SampleServerIndirectList(@NonNull Context context) {
        super();
        this.list = new ArrayList<>();
        this.indexList = new ArrayList<>();
        this.deleted = null;
        this.category = null;
        this.query = null;
        this.sortBy = SORT_AS_IS;
        this.sortReverse = false;

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        serverAddress = pref.getString("server", "");
        if (serverAddress.length() > 0 && serverAddress.charAt(serverAddress.length() - 1) != '/')
            serverAddress = serverAddress.concat("/");

        rest = new HttpRestLite();
    }

    /**
     * Gets an item with index list.
     */
    public Sample get(int i) {
        return list.get(indexList.get(i));
    }

    /**
     * Gets size of index list.
     */
    public int size() {
        return indexList.size();
    }

    /**
     * Finds elements by id.
     *
     * @return Returns position if found, otherwise, it is -1.
     */
    public int find(long id) {
        int i = 0;
        int l = indexList.size();
        while (i < l) {
            Sample item = list.get(indexList.get(i));
            if (item.getId() != id)
                i++;
            else
                return i;
        }
        return -1;
    }

    /**
     * Checks a sample is visible or not.
     */
    public boolean isVisible(Sample sample) {
        boolean b = true;
        if (deleted != null)
            b = sample.getDeleted() == null == !deleted;
        if (category != null)
            b = sample.getCategory() == category;
        if (query != null)
            b = sample.getName().contains(query);
        return b;
    }

    /**
     * Adds a sample, if need.
     */
    public void add(Sample sample) {
        if (isVisible(sample)) {
            list.add(sample);
            indexList.add(list.size() - 1);
            sort(sortBy, sortReverse);
        }
    }

    /**
     * Edits a sample.
     */
    public void edit(int i, Sample sample) {
        if (isVisible(sample)) {
            list.set(indexList.get(i), sample);
            sort(sortBy, sortReverse);
        }
    }

    /**
     * Loads data.
     */
    public HttpRestLite.Result load(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String query,
                                    int sortBy, boolean sortReverse) {
        Map<String, String> params = collectParameters(deleted, category, query, null);
        this.deleted = deleted;
        this.category = category;
        this.query = query;
        this.sortBy = sortBy;
        this.sortReverse = sortReverse;
        return call(serverAddress + "api/sample/list.php", "POST", params);
    }

    /**
     * Loads data async.
     */
    public void load(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String query,
                     int sortBy, boolean sortReverse, @NonNull HttpRestLite.ResultListener listener) {
        Map<String, String> params = collectParameters(deleted, category, query, null);
        this.deleted = deleted;
        this.category = category;
        this.query = query;
        this.sortBy = sortBy;
        this.sortReverse = sortReverse;
        call(serverAddress + "api/sample/list.php", "POST", params, listener);
    }

    /**
     * Loads data.
     */
    public HttpRestLite.Result load(@Nullable Boolean deleted, @Nullable Integer category) {
        return load(deleted, category, query, sortBy, sortReverse);
    }

    /**
     * Loads data async.
     */
    public void load(@Nullable Boolean deleted, @Nullable Integer category, @NonNull HttpRestLite.ResultListener listener) {
        load(deleted, category, query, sortBy, sortReverse, listener);
    }

    /**
     * Reloads data.
     */
    public HttpRestLite.Result reload() {
        return load(deleted, category, query, sortBy, sortReverse);
    }

    /**
     * Reloads data async.
     */
    public void reload(@NonNull HttpRestLite.ResultListener listener) {
        load(deleted, category, query, sortBy, sortReverse, listener);
    }

    /**
     * Searches data.
     */
    public HttpRestLite.Result search(@Nullable String query) {
        return load(deleted, category, query, sortBy, sortReverse);
    }

    /**
     * Searches data async.
     */
    public void search(@Nullable String query, @NonNull HttpRestLite.ResultListener listener) {
        load(deleted, category, query, sortBy, sortReverse, listener);
    }

    /**
     * Sorts data.
     */
    public void sort(int sortBy, boolean sortReverse) {
        switch (sortBy) {
            default:
            case SORT_AS_IS:
                if (!sortReverse)
                    Collections.sort(indexList, compareAsIs);
                else
                    Collections.sort(indexList, compareAsIsReverse);
                break;

            case SORT_NAME:
                if (!sortReverse)
                    Collections.sort(indexList, compareName);
                else
                    Collections.sort(indexList, compareNameReverse);
                break;

            case SORT_NAME_IGNORE_CASE:
                if (!sortReverse)
                    Collections.sort(indexList, compareNameIgnoreCase);
                else
                    Collections.sort(indexList, compareNameIgnoreCaseReverse);
                break;

            case SORT_NAME_NATURAL:
                if (!sortReverse)
                    Collections.sort(indexList, compareNameNatural);
                else
                    Collections.sort(indexList, compareNameNaturalReverse);
                break;
        }
        this.sortBy = sortBy;
        this.sortReverse = sortReverse;
    }

    /**
     * Sorts data.
     */
    public void sort() {
        sort(sortBy, sortReverse);
    }

    /**
     * Deletes, moves to trash, data.
     */
    public HttpRestLite.Result delete(@NonNull List<Long> idList) {
        Map<String, String> params = collectParameters(deleted, category, query, idList);
        return call(serverAddress + "api/sample/delete.php", "POST", params);
    }

    /**
     * Deletes, moves to trash, data async.
     */
    public void delete(@NonNull List<Long> idList, @NonNull HttpRestLite.ResultListener listener) {
        Map<String, String> params = collectParameters(deleted, category, query, idList);
        call(serverAddress + "api/sample/delete.php", "POST", params, listener);
    }

    /**
     * Restores, moves out of trash, data.
     */
    public HttpRestLite.Result restore(@NonNull List<Long> idList) {
        Map<String, String> params = collectParameters(deleted, category, query, idList);
        return call(serverAddress + "api/sample/restore.php", "POST", params);
    }

    /**
     * Restores, moves out of trash, data async.
     */
    public void restore(@NonNull List<Long> idList, @NonNull HttpRestLite.ResultListener listener) {
        Map<String, String> params = collectParameters(deleted, category, query, idList);
        call(serverAddress + "api/sample/restore.php", "POST", params, listener);
    }

    /**
     * Removes data.
     */
    public HttpRestLite.Result remove(@NonNull List<Long> idList) {
        Map<String, String> params = collectParameters(deleted, category, query, idList);
        return call(serverAddress + "api/sample/remove.php", "DELETE", params);
    }

    /**
     * Removes data async.
     */
    public void remove(@NonNull List<Long> idList, @NonNull HttpRestLite.ResultListener listener) {
        Map<String, String> params = collectParameters(deleted, category, query, idList);
        call(serverAddress + "api/sample/remove.php", "DELETE", params, listener);
    }

    /**
     * Removes ALL data.
     */
    public HttpRestLite.Result removeAll() {
        Map<String, String> params = collectParameters(deleted, category, query, null);
        return call(serverAddress + "api/sample/remove-all.php", "DELETE", params);
    }

    /**
     * Removes ALL data async.
     */
    public void removeAll(@NonNull HttpRestLite.ResultListener listener) {
        Map<String, String> params = collectParameters(deleted, category, query, null);
        call(serverAddress + "api/sample/remove-all.php", "DELETE", params, listener);
    }

    /**
     * Cancels the HTTP operation.
     */
    public void cancel() {
        rest.cancel();
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public Integer getCategory() {
        return category;
    }

    public String getQuery() {
        return query;
    }

    public int getSortBy() {
        return sortBy;
    }

    public void setSortBy(int value) {
        if (sortBy != value) {
            sort(value, sortReverse);
        }
    }

    public boolean getSortReverse() {
        return sortReverse;
    }

    public void setSortReverse(boolean value) {
        if (sortReverse != value) {
            Collections.reverse(indexList);
            sortReverse = value;
        }
    }

    private Map<String, String> collectParameters(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String query, @Nullable List<Long> idList) {
        Map<String, String> params = new HashMap<>();
        if (deleted != null)
            params.put("deleted", !deleted ? "0" : "1");
        if (category != null)
            params.put("category", category.toString());
        if (query != null)
            params.put("query", query);
        if (idList != null)
            for (int i = 0; i < idList.size(); i++) {
                params.put("list[" + i + "]", idList.get(i).toString());
            }
        return params;
    }

    private HttpRestLite.Result call(@NonNull String url, @NonNull String request, @NonNull Map<String, String> params) {
        HttpRestLite.Result result = rest.execute(url, request, params, null);
        if (result.errorCode == 0) {
            loading(result, sortBy, sortReverse);
        }
        return result;
    }

    private void call(@NonNull String url, @NonNull String request, @NonNull Map<String, String> params, @NonNull final HttpRestLite.ResultListener listener) {
        rest.execute(url, request, params, null, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                if (result.errorCode == 0) {
                    loading(result, sortBy, sortReverse);
                }
                listener.finish(result);
            }
        });
    }

    private void loading(HttpRestLite.Result result, int sortBy, boolean sortReverse) {
        List<Sample> list = new ArrayList<>();
        try {
            if (result.json.has("ok") && result.json.getBoolean("ok")) {
                JSONArray items = result.json.getJSONArray("items");
                //Log.d(TAG, items.toString(4));
                int l = items.length();
                for (int i = 0; i < l; i++) {
                    JSONObject item = items.getJSONObject(i);
                    Sample sample = new Sample(item.getInt("id"));
                    sample.setIcon(SampleServerDataSource.iconFromString(item.getString("icon")));
                    sample.setName(item.getString("name"));
                    sample.setCategory(item.getInt("category"));
                    sample.setDeleted(item.isNull("deleted") ? null : SampleDataSource.dateFromLong(item.getLong("deleted")));
                    list.add(sample);
                    Log.d(TAG, "data load item: " + sample.getId() + "/" + sample.getName());
                }
                Log.d(TAG, "data load total: " + l);
                this.list = list;

                l = list.size();
                indexList = new ArrayList<>(l);
                for (int i = 0; i < l; i++) {
                    indexList.add(i);
                }
                sort(sortBy, sortReverse);
            }
            else {
                result.errorCode = HttpRestLite.ERROR_CUSTOM;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result.errorCode = HttpRestLite.ERROR_JSON;
        }
    }

}
