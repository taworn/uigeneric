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
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Locale;

import diy.generic.IndirectList;
import diy.uigeneric.R;
import diy.uigeneric.data.Sample;

/**
 * Sample list adapter.
 */
public class SampleListAdapter extends RecyclerView.Adapter<SampleListAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imageIcon;
        public TextView textName;
        public TextView textCategory;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            imageIcon = (ImageView) view.findViewById(R.id.image_icon);
            textName = (TextView) view.findViewById(R.id.text_name);
            textCategory = (TextView) view.findViewById(R.id.text_category);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, this.getLayoutPosition());
        }

    }

    private Context context = null;
    private IndirectList<Sample> list = null;
    private SimpleDateFormat formatter = null;
    private boolean deleted = false;
    private OnItemClickListener listener = null;

    public SampleListAdapter(Context context, @NonNull IndirectList<Sample> list, boolean deleted, @NonNull OnItemClickListener listener) {
        super();
        this.context = context;
        this.list = list;
        this.formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        this.deleted = deleted;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_sample_item, parent, false);
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
            holder.imageIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_account_circle_black_24dp));

        holder.textName.setText(item.getName());

        String categoryName = Sample.categoryToString(context, item.getCategory());
        if (categoryName == null)
            categoryName = Integer.toString(item.getCategory());
        holder.textCategory.setText(categoryName);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
