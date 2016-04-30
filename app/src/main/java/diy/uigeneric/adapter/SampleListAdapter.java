package diy.uigeneric.adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
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

        /**
         * Calls when an item has clicked.
         */
        void onClick(View view, int position);

        /**
         * Calls when an item has long clicked.
         */
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
    private SparseBooleanArray selected = null;
    private SimpleDateFormat formatter = null;
    private OnItemClickListener listener = null;

    public SampleListAdapter(Context context, @NonNull SampleIndirectList list, @NonNull OnItemClickListener listener) {
        super();
        this.context = context;
        this.list = list;
        this.selected = new SparseBooleanArray();
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

        if (!getSelected(position)) {
            if (Build.VERSION.SDK_INT < 16)
                holder.layout.setBackgroundDrawable(null);
            else
                holder.layout.setBackground(null);
        }
        else {
            holder.layout.setBackgroundResource(R.color.list_selected);
        }

        if (item.getIcon() != null) {
            Drawable drawable = new BitmapDrawable(context.getResources(), item.getIcon());
            holder.imageIcon.setImageDrawable(drawable);
        }
        else
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));

        holder.textName.setText(item.getName());

        String[] categoryList = context.getResources().getStringArray(R.array.sample_category);
        String categoryName = Integer.toString(item.getCategory());
        if (item.getCategory() >= 0 && item.getCategory() < categoryList.length)
            categoryName = categoryList[item.getCategory()];
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

    public int getSelectedItemCount() {
        return selected.size();
    }

    public boolean getSelected(int position) {
        return selected.get(position, false);
    }

    public List<Long> getSelectedIdList() {
        List<Long> list = new ArrayList<>();
        for (int i = 0; i < selected.size(); i++) {
            int position = selected.keyAt(i);
            list.add(this.list.get(position).getId());
        }
        return list;
    }

    public void toggleSelection(int position) {
        if (selected.get(position, false))
            selected.delete(position);
        else
            selected.put(position, true);
        notifyItemChanged(position);
    }

    public void clearSelections() {
        selected.clear();
        notifyItemRangeChanged(0, getItemCount());
    }

}
