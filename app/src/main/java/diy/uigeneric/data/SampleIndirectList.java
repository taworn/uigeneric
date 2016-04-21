package diy.uigeneric.data;

import android.content.Context;

import java.util.Comparator;
import java.util.List;

import diy.generic.IndirectList;

public class SampleIndirectList extends IndirectList<Sample> {

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
    /*
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
    private static final Comparator<String> comparator = new NaturalComparator(Collator.getInstance(Locale.ENGLISH));
    */

    public SampleIndirectList() {
        super();
    }

    public void load(Context context, Boolean deleted, Integer category, String query, int sortBy, boolean sortReverse) {
        SampleDataSource source = new SampleDataSource(context);
        source.open();
        List<Sample> list = source.list(deleted, category, query, null);
        source.close();

        set(list);
        //sort(sortBy, sortReverse);
    }

    /*
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
    }
    */

}
