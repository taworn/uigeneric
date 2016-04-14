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
import java.util.Locale;

import diy.generic.IndirectList;
import diy.uigeneric.R;
import diy.uigeneric.data.Sample;

/**
 * Sample list adapter.
 */
public class SampleListAdapter extends RecyclerView.Adapter<SampleListAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageIcon;
        public TextView textName;
        public TextView textCategory;

        public ViewHolder(View view) {
            super(view);
            imageIcon = (ImageView) view.findViewById(R.id.image_icon);
            textName = (TextView) view.findViewById(R.id.text_name);
            textCategory = (TextView) view.findViewById(R.id.text_category);
        }
    }

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
        holder.textCategory.setText("Category: " + item.getCategory());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
