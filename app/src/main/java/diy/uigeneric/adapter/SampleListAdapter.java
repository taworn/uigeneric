package diy.uigeneric.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import diy.uigeneric.R;
import diy.uigeneric.data.Sample;
import diy.uigeneric.data.SampleIndirectList;

/**
 * Sample list adapter.
 */
public class SampleListAdapter extends RecyclerView.Adapter<SampleListAdapter.ViewHolder> {

    public interface OnItemClickListener {

        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public LinearLayout layout;
        public ImageView imageIcon;
        public TextView textName;
        public TextView textCategory;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
            layout = (LinearLayout) view.findViewById(R.id.layout);
            imageIcon = (ImageView) view.findViewById(R.id.image_icon);
            textName = (TextView) view.findViewById(R.id.text_name);
            textCategory = (TextView) view.findViewById(R.id.text_category);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getLayoutPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            listener.onLongClick(view, getLayoutPosition());
            return true;
        }

    }

    private Context context = null;
    private SampleIndirectList list = null;
    private SimpleDateFormat formatter = null;
    private OnItemClickListener listener = null;

    public SampleListAdapter(Context context, @NonNull SampleIndirectList list, @NonNull OnItemClickListener listener) {
        super();
        this.context = context;
        this.list = list;
        this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sample_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Sample item = list.get(position);

        if (list.getSelected().get(position)) {
            holder.layout.setBackgroundResource(R.color.list_selected);
        }

        if (item.getIcon() != null) {
            Drawable drawable = new BitmapDrawable(context.getResources(), item.getIcon());
            holder.imageIcon.setImageDrawable(drawable);
        }
        else
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));

        holder.textName.setText(item.getName());

        String categoryName = Sample.categoryToString(context, item.getCategory());
        if (categoryName == null)
            categoryName = Integer.toString(item.getCategory());
        if (item.getDeleted() != null) {
            categoryName += " - " + formatter.format(item.getDeleted().getTime());
            categoryName += " " + context.getResources().getString(R.string.sample_deleted);
        }
        holder.textCategory.setText(categoryName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void selectedItem(int i, boolean value) {
        list.getSelected().set(i, value);
        notifyItemChanged(i);
    }

    public void toggleSelection(int i) {
        list.getSelected().set(i, !list.getSelected().get(i));
        notifyItemChanged(i);
    }

    public void removeSelection() {
        for (int i = 0; i < list.size(); i++) {
            list.getSelected().set(i, false);
        }
        notifyItemRangeChanged(0, list.size());
    }

    public int getSelectedCount() {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.getSelected().get(i))
                count++;
        }
        return count;
    }

    public List<Long> getSelectedItems() {
        List<Long> selectedList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            if (list.getSelected().get(i))
                selectedList.add(list.get(i).getId());
        }
        return selectedList;
    }

}
