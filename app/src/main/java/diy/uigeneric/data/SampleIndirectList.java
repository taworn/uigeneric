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

    private static final Comparator<Sample> compareAsIs = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return l.compare(l, r);
        }
    };
    private static final Comparator<Sample> compareAsIsReverse = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return r.compare(r, l);
        }
    };
    private static final Comparator<Sample> compareName = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return l.getName().compareTo(r.getName());
        }
    };
    private static final Comparator<Sample> compareNameReverse = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return r.getName().compareTo(l.getName());
        }
    };
    private static final Comparator<Sample> compareNameIgnoreCase = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return l.getName().compareToIgnoreCase(r.getName());
        }
    };
    private static final Comparator<Sample> compareNameIgnoreCaseReverse = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return r.getName().compareToIgnoreCase(l.getName());
        }
    };
    private static final Comparator<Sample> compareNameNatural = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return comparator.compare(l.getName(), r.getName());
        }
    };
    private static final Comparator<Sample> compareNameNaturalReverse = new Comparator<Sample>() {
        @Override
        public int compare(Sample l, Sample r) {
            return comparator.compare(r.getName(), l.getName());
        }
    };
    private static final Comparator<String> comparator = new AlphanumericComparator(Collator.getInstance(Locale.ENGLISH));

    private List<Sample> list;
    private List<Sample> indexList;

    private Boolean deleted;
    private Integer category;
    private String query;
    private int sortBy;
    private boolean sortReverse;

    public SampleIndirectList() {
        super();
        this.list = new ArrayList<>();
        this.indexList = new ArrayList<>();
        sortBy = SORT_AS_IS;
        sortReverse = false;
    }

    public Sample get(int i) {
        return indexList.get(i);
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

        indexList = new ArrayList<>(list.size());
        for (Sample i : list) {
            indexList.add(i);
        }
        sort(sortBy, sortReverse);
    }

    public void reload(Context context) {
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        list = source.list(deleted, category, query, null);
        source.close();

        indexList = new ArrayList<>(list.size());
        for (Sample i : list) {
            indexList.add(i);
        }
        sort(sortBy, sortReverse);
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
