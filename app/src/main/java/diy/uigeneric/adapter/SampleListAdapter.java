package diy.uigeneric.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import diy.generic.IndirectList;
import diy.uigeneric.R;
import diy.uigeneric.data.Sample;

/**
 * Sample list adapter.
 */
public class SampleListAdapter extends RecyclerView.Adapter<SampleListAdapter.ViewHolder> {

    private Context context = null;
    private IndirectList<Sample> list = null;
    private SimpleDateFormat formatter = null;
    private boolean deleted = false;

    public SampleListAdapter(Context context, IndirectList<Sample> list, boolean deleted) {
        super();
        this.context = context;
        this.list = list;
        this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        this.deleted = deleted;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sample_edit, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sample item = (Sample) list.get(position).item;
        if (item.getIcon() != null) {
            Drawable drawable = new BitmapDrawable(context.getResources(), item.getIcon());
            holder.imageIcon.setImageDrawable(drawable);
        }
        else
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_white_24dp));
        holder.textName.setText(item.getName());
        /*
        if (deleted)
            holder.textCategory.setText(formatter.format(item.getDeleted()));
        else
            holder.textCategory.setText(Sample.categoryToString[item.getCategory()].toString());
         */
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public View getView(int i, View view, ViewGroup parent) {
        /*
        Sample item = (Sample) list.get(i).item;
        ViewHolder holder;

        if (view == null) {
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.adapter_sample_edit, parent, false);
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
            holder.textCategory.setText(formatter.formatter(item.object.getDeleted()));
        else
            holder.textCategory.setText(Generic.categoryToString[item.object.getCategory()].toString());
        */
        return view;
    }

    public void selectedItem(int i, boolean value) {
        IndirectList.Item<Sample> item = (IndirectList.Item<Sample>) list.get(i);
        item.selected = value;
        notifyDataSetChanged();
    }

    public void toggleSelection(int i) {
        IndirectList.Item<Sample> item = (IndirectList.Item<Sample>) list.get(i);
        item.selected = !item.selected;
        notifyDataSetChanged();
    }

    public void removeSelection() {
        for (int i = 0; i < list.size(); i++) {
            IndirectList.Item<Sample> item = (IndirectList.Item<Sample>) list.get(i);
            item.selected = false;
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            IndirectList.Item<Sample> item = (IndirectList.Item<Sample>) list.get(i);
            if (item.selected)
                count++;
        }
        return count;
    }

    public List<Long> getSelectedItems() {
        List<Long> selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            IndirectList.Item<Sample> item = (IndirectList.Item<Sample>) list.get(i);
            if (item.selected)
                selectedList.add(item.item.getId());
        }
        return selectedList;
    }

    public void changeLayout(boolean deleted) {
        this.deleted = deleted;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewGroup layoutItem;
        public ImageView imageIcon;
        public TextView textName;
        public TextView textCategory;

        public ViewHolder(View view) {
            super(view);
            layoutItem = (ViewGroup) view.findViewById(R.id.layout_item);
            imageIcon = (ImageView) view.findViewById(R.id.image_icon);
            textName = (TextView) view.findViewById(R.id.text_name);
            textCategory = (TextView) view.findViewById(R.id.text_category);
        }
    }

}
