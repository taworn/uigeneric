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
 * reload, sort and search.  This class is convenient to use as list in Android UI.
 */
public class SampleServerIndirectList {

    private static final String TAG = SampleServerIndirectList.class.getSimpleName();

    public interface ResultListener {
        /**
         * Calls when HTTP execute finished.
         */
        void finish(int errorCode);
    }

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

    private void loading(HttpRestLite.Result result, int sortBy, boolean sortReverse) {
        list = new ArrayList<>();
        try {
            if (result.json.has("ok") && result.json.getBoolean("ok")) {
                JSONArray items = result.json.getJSONArray("items");
                //Log.d(TAG, items.toString(4));
                int l = items.length();
                for (int i = 0; i < l; i++) {
                    JSONObject item = items.getJSONObject(i);
                    Sample sample = new Sample(item.getInt("id"));
                    sample.setName(item.getString("name"));
                    sample.setCategory(item.getInt("category"));
                    sample.setDeleted(item.isNull("deleted") ? null : SampleDataSource.dateFromLong(item.getLong("deleted")));
                    list.add(sample);
                    Log.d(TAG, "data load item: " + sample.getId() + "/" + sample.getName());
                }
                Log.d(TAG, "data load total: " + l);

                l = list.size();
                indexList = new ArrayList<>(l);
                for (int i = 0; i < l; i++) {
                    indexList.add(i);
                }
                sort(sortBy, sortReverse);
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
            result.errorCode = HttpRestLite.ERROR_JSON;
        }
    }

    /**
     * Loads data.
     *
     * @param deleted     Deleted flags to use, can be null.
     * @param category    Category to load, can be null.
     * @param query       Query to search, can be null.
     * @param sortBy      How to sort data.
     * @param sortReverse Sort by ascending or descending.
     */
    public HttpRestLite.Result load(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String query,
                                    int sortBy, boolean sortReverse) {
        // prepares parameters
        Map<String, String> params = new HashMap<>();
        if (deleted != null)
            params.put("deleted", !deleted ? "0" : "1");
        if (category != null)
            params.put("category", category.toString());
        if (query != null)
            params.put("search", query);

        // saves load() parameters
        this.deleted = deleted;
        this.category = category;
        this.query = query;

        // calls REST
        HttpRestLite rest = new HttpRestLite(serverAddress + "/api/sample/list.php", "POST");
        HttpRestLite.Result result = rest.execute(params, null);
        if (result.errorCode == 0) {
            loading(result, sortBy, sortReverse);
        }
        return result;
    }

    /**
     * Loads data async.
     *
     * @param deleted     Deleted flags to use, can be null.
     * @param category    Category to load, can be null.
     * @param query       Query to search, can be null.
     * @param sortBy      How to sort data.
     * @param sortReverse Sort by ascending or descending.
     * @param listener    Callback when data is done.
     */
    public HttpRestLite load(@Nullable Boolean deleted, @Nullable Integer category, @Nullable String query,
                             final int sortBy, final boolean sortReverse, @NonNull final ResultListener listener) {
        // prepares parameters
        Map<String, String> params = new HashMap<>();
        if (deleted != null)
            params.put("deleted", !deleted ? "0" : "1");
        if (category != null)
            params.put("category", category.toString());
        if (query != null)
            params.put("search", query);

        // saves load() parameters
        this.deleted = deleted;
        this.category = category;
        this.query = query;

        // calls REST
        final HttpRestLite rest = new HttpRestLite(serverAddress + "/api/sample/list.php", "POST");
        rest.execute(params, null, new HttpRestLite.ResultListener() {
            @Override
            public void finish(HttpRestLite.Result result) {
                if (result.errorCode == 0) {
                    loading(result, sortBy, sortReverse);
                }
                listener.finish(result.errorCode);
            }
        });
        return rest;
    }

    public HttpRestLite.Result load(@Nullable Boolean deleted, @Nullable Integer category) {
        return load(deleted, category, query, sortBy, sortReverse);
    }

    public HttpRestLite load(@Nullable Boolean deleted, @Nullable Integer category, @NonNull ResultListener listener) {
        return load(deleted, category, query, sortBy, sortReverse, listener);
    }

    public HttpRestLite.Result reload() {
        return load(deleted, category, query, sortBy, sortReverse);
    }

    public HttpRestLite reload(@NonNull ResultListener listener) {
        return load(deleted, category, query, sortBy, sortReverse, listener);
    }

    public HttpRestLite.Result search(@Nullable String query) {
        return load(deleted, category, query, sortBy, sortReverse);
    }

    public HttpRestLite search(@Nullable String query, @NonNull ResultListener listener) {
        return load(deleted, category, query, sortBy, sortReverse, listener);
    }

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

    public void sort() {
        sort(sortBy, sortReverse);
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

}
