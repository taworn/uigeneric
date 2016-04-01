package diy.generic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Generic list adapter.
 */
public class ListAdapter<ItemType> extends BaseAdapter {

    private Context context = null;
    private LayoutInflater inflater = null;
    private IndirectList<ItemType> list = null;

    public ListAdapter(Context context, IndirectList<ItemType> list) {
        super();
        this.context = context;
        this.inflater = null;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }

    public void selectedItem(int i, boolean value) {
        IndirectList.Item<ItemType> item = (IndirectList.Item<ItemType>) list.get(i);
        item.selected = value;
        notifyDataSetChanged();
    }

    public void toggleSelection(int i) {
        IndirectList.Item<ItemType> item = (IndirectList.Item<ItemType>) list.get(i);
        item.selected = !item.selected;
        notifyDataSetChanged();
    }

    public void removeSelection() {
        for (int i = 0; i < list.size(); i++) {
            IndirectList.Item<ItemType> item = (IndirectList.Item<ItemType>) list.get(i);
            item.selected = false;
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            IndirectList.Item<ItemType> item = (IndirectList.Item<ItemType>) list.get(i);
            if (item.selected)
                count++;
        }
        return count;
    }

    /*
    public List<Long> getSelectedItems() {
        List<Long> selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            IndirectList.Item<ItemType> item = (IndirectList.Item<ItemType>) list.get(i);
            if (item.selected)
                selectedList.add(item.item.getId());
        }
        return selectedList;
    }
    */

}
