package diy.uigeneric;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Generic list list adapter.
 */
public class GenericListAdapter extends BaseAdapter {

    private Context context = null;
    private LayoutInflater inflater = null;
    private GenericListIndirect list = null;
    private SimpleDateFormat format = null;
    private boolean deleted = false;

    public GenericListAdapter(Context context, GenericListIndirect list, boolean deleted) {
        super();
        this.context = context;
        this.inflater = null;
        this.list = list;
        this.format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        this.deleted = deleted;
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
        return list.get(i).object.getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        GenericListIndirect.Item item = (GenericListIndirect.Item) getItem(i);
        ViewHolder holder;

        if (view == null) {
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.generic_adapter_item, parent, false);
            holder = new ViewHolder();
            holder.layoutItem = (ViewGroup) view.findViewById(R.id.layout_item);
            holder.imageIcon = (ImageView) view.findViewById(R.id.image_icon);
            holder.textName = (TextView) view.findViewById(R.id.text_name);
            holder.textCategory = (TextView) view.findViewById(R.id.text_category);
            view.setTag(holder);
        }
        else
            holder = (ViewHolder) view.getTag();

        if (Build.VERSION.SDK_INT < 16) {
            if (!item.selected)
                holder.layoutItem.setBackgroundDrawable(null);
            else
                holder.layoutItem.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.generic_listview_item_selected));
        }
        else {
            if (!item.selected)
                holder.layoutItem.setBackground(null);
            else
                holder.layoutItem.setBackground(context.getResources().getDrawable(R.drawable.generic_listview_item_selected));
        }
        if (item.object.getIcon() != null) {
            Drawable drawable = new BitmapDrawable(context.getResources(), item.object.getIcon());
            holder.imageIcon.setImageDrawable(drawable);
        }
        else
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher));
        holder.textName.setText(item.object.getName());
        if (deleted)
            holder.textCategory.setText(format.format(item.object.getDeleted()));
        else
            holder.textCategory.setText(Generic.categoryToString[item.object.getCategory()].toString());

        return view;
    }

    public void selectedItem(int i, boolean value) {
        GenericListIndirect.Item item = (GenericListIndirect.Item) getItem(i);
        item.selected = value;
        notifyDataSetChanged();
    }

    public void toggleSelection(int i) {
        GenericListIndirect.Item item = (GenericListIndirect.Item) getItem(i);
        item.selected = !item.selected;
        notifyDataSetChanged();
    }

    public void removeSelection() {
        for (int i = 0; i < list.size(); i++) {
            GenericListIndirect.Item item = list.get(i);
            item.selected = false;
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            GenericListIndirect.Item item = list.get(i);
            if (item.selected)
                count++;
        }
        return count;
    }

    public List<Long> getSelectedItems() {
        List<Long> selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            GenericListIndirect.Item item = this.list.get(i);
            if (item.selected)
                selectedList.add(item.object.getId());
        }
        return selectedList;
    }

    public void changeLayout(boolean deleted) {
        this.deleted = deleted;
    }

    private class ViewHolder {
        public ViewGroup layoutItem;
        public ImageView imageIcon;
        public TextView textName;
        public TextView textCategory;
    }

}
