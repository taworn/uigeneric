package diy.uigeneric;

import android.content.Context;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Provides indexList based on indexes.
 */
public class GenericListIndirect {

    public static final int SORT_AS_IS = 0;
    public static final int SORT_NAME = 1;
    public static final int SORT_NAME_IGNORE_CASE = 2;
    public static final int SORT_NAME_NATURAL = 3;

    private static final Comparator<Item> compareAsIs = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.object.compare(lhs.object, rhs.object);
        }
    };
    private static final Comparator<Item> compareAsIsReverse = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return rhs.object.compare(rhs.object, lhs.object);
        }
    };
    private static final Comparator<Item> compareName = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.object.getName().compareTo(rhs.object.getName());
        }
    };
    private static final Comparator<Item> compareNameReverse = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return rhs.object.getName().compareTo(lhs.object.getName());
        }
    };
    private static final Comparator<Item> compareNameIgnoreCase = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return lhs.object.getName().compareToIgnoreCase(rhs.object.getName());
        }
    };
    private static final Comparator<Item> compareNameIgnoreCaseReverse = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return rhs.object.getName().compareToIgnoreCase(lhs.object.getName());
        }
    };
    private static final Comparator<Item> compareNameNatural = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return comparator.compare(lhs.object.getName(), rhs.object.getName());
        }
    };
    private static final Comparator<Item> compareNameNaturalReverse = new Comparator<Item>() {
        @Override
        public int compare(Item lhs, Item rhs) {
            return comparator.compare(rhs.object.getName(), lhs.object.getName());
        }
    };
    private static final Comparator<String> comparator = new NaturalComparator(Collator.getInstance(Locale.ENGLISH));

    public static class Item {
        public Generic object;
        public boolean selected;

        public Item(Generic object) {
            this.object = object;
            this.selected = false;
        }
    }

    private List<Generic> list;
    private List<Item> indexList;

    public GenericListIndirect() {
        super();
        this.list = new ArrayList<>();
        this.indexList = new ArrayList<>();
    }

    public int size() {
        return indexList.size();
    }

    public Item get(int i) {
        return indexList.get(i);
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
    }

    public void load(Context context, Boolean deleted, Integer category, String query, int sortBy, boolean sortReverse) {
        GenericDataSource source = new GenericDataSource(context);
        source.open();
        list = source.list(deleted, category, query, null);
        source.close();

        indexList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++)
            indexList.add(new Item(list.get(i)));

        sort(sortBy, sortReverse);
    }

}
