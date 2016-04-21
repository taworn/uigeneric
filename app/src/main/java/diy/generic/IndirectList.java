package diy.generic;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * An indirect to list.
 */
public class IndirectList<ItemType> {

    private List<ItemType> list;
    private List<ItemType> indexList;

    public IndirectList() {
        super();
        this.list = new ArrayList<>();
        this.indexList = new ArrayList<>();
    }

    public void set(@NonNull List<ItemType> l) {
        list = l;
        indexList = new ArrayList<>(list.size());
        for (ItemType i : list) {
            indexList.add(i);
        }
    }

    public ItemType get(int i) {
        return indexList.get(i);
    }

    public int size() {
        return indexList.size();
    }

}
