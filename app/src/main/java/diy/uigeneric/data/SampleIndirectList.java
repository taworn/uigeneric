package diy.uigeneric.data;

import android.content.Context;

import com.amjjd.alphanum.AlphanumericComparator;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SampleIndirectList {

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
    private final Comparator<String> comparator = new AlphanumericComparator(Collator.getInstance(Locale.ENGLISH));

    private List<Sample> list;
    private List<Integer> indexList;

    private Boolean deleted;
    private Integer category;
    private String query;
    private int sortBy;
    private boolean sortReverse;

    public SampleIndirectList() {
        super();
        this.list = new ArrayList<>();
        this.indexList = new ArrayList<>();
        this.sortBy = SORT_AS_IS;
        this.sortReverse = false;
    }

    public Sample get(int i) {
        return list.get(indexList.get(i));
    }

    public int size() {
        return indexList.size();
    }

    public void load(Context context, Boolean deleted, Integer category, String query, int sortBy, boolean sortReverse) {
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        list = source.list(deleted, category, query, null);
        source.close();
        this.deleted = deleted;
        this.category = category;
        this.query = query;

        int l = list.size();
        indexList = new ArrayList<>(l);
        for (int i = 0; i < l; i++) {
            indexList.add(i);
        }

        sort(sortBy, sortReverse);
    }

    public void load(Context context, Boolean deleted, Integer category) {
        load(context, deleted, category, query, sortBy, sortReverse);
    }

    public void reload(Context context) {
        load(context, deleted, category, query, sortBy, sortReverse);
    }

    public void search(Context context, String query) {
        load(context, deleted, category, query, sortBy, sortReverse);
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
