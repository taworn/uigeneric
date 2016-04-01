package diy.generic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An indirect to list.
 */
public class IndirectList<ItemType> {

    public static class Item<ItemType> {
        public ItemType item;
        public boolean selected;
    }

    private List<ItemType> list;
    private List<Item> indexList;

    public IndirectList() {
        super();
        this.list = new ArrayList<>();
        this.indexList = new ArrayList<>();
    }

    public void set(@NonNull List<ItemType> l) {
        list = l;
        indexList = new ArrayList<>(list.size());
        for (ItemType i : list) {
            Item<ItemType> ii = new Item<>();
            ii.item = i;
            ii.selected = false;
            indexList.add(ii);
        }
    }

    public Item get(int i) {
        return indexList.get(i);
    }

    public int size() {
        return indexList.size();
    }

}
