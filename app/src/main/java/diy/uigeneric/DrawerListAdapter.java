package diy.uigeneric;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Navigation drawer menu adapter.
 */
public class DrawerListAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private DrawerMenu menu;

    private OnProfileListener profileListener;

    public DrawerListAdapter(Context context, DrawerMenu menu) {
        super();
        this.context = context;
        this.inflater = null;
        this.menu = menu;
        this.profileListener = null;
    }

    @Override
    public int getCount() {
        return menu.list.size() + 1;
    }

    @Override
    public Object getItem(int i) {
        if (i > 0)
            return menu.list.get(i - 1);
        else
            return new DrawerMenu.Item("");
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        DrawerMenu.Item item = (DrawerMenu.Item) getItem(position);
        if (position > 0) {
            if (item.getType() != DrawerMenu.Item.Type.GRAYED)
                return true;
            else
                return false;
        }
        else
            return false;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        DrawerMenu.Item item = (DrawerMenu.Item) getItem(position);
        if (position > 0) {
            if (item.getType() != DrawerMenu.Item.Type.GRAYED) {
                if (item.icon != 0)
                    return 0;
                else
                    return 1;
            }
            else
                return 2;
        }
        else
            return 3;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {
        DrawerMenu.Item item = (DrawerMenu.Item) getItem(i);
        ViewHolder holder;

        if (view == null) {
            if (inflater == null)
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (i > 0) {
                if (item.getType() != DrawerMenu.Item.Type.GRAYED) {
                    if (item.icon != 0)
                        view = inflater.inflate(R.layout.adapter_menu_item_with_icon, parent, false);
                    else
                        view = inflater.inflate(R.layout.adapter_menu_item, parent, false);
                }
                else
                    view = inflater.inflate(R.layout.adapter_menu_item_grayed, parent, false);
            }
            else
                view = inflater.inflate(R.layout.adapter_menu_top_header, parent, false);
            holder = new ViewHolder();
            holder.textName = (TextView) view.findViewById(R.id.text_name);
            holder.imageIcon = (ImageView) view.findViewById(R.id.image_icon);
            view.setTag(holder);
        }
        else
            holder = (ViewHolder) view.getTag();

        if (i > 0) {
            holder.textName.setText(item.name);
            if (item.icon != 0 && holder.imageIcon != null)
                holder.imageIcon.setImageDrawable(context.getResources().getDrawable(item.icon));
        }
        else {
            final ImageView imageIconProfile = (ImageView) view.findViewById(R.id.image_icon_profile);
            if (imageIconProfile != null && imageIconProfile.getTag() == null) {
                imageIconProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (profileListener != null)
                            profileListener.call(imageIconProfile.getTag());
                    }
                });
                imageIconProfile.setTag(1);
            }
        }

        return view;
    }

    public OnProfileListener getOnProfileListener() {
        return profileListener;
    }

    public void setOnProfileListener(OnProfileListener profileListener) {
        this.profileListener = profileListener;
    }

    public interface OnProfileListener {
        /// Call on profile's data has click.
        public void call(Object object);
    }

    private class ViewHolder {
        public TextView textName;
        public ImageView imageIcon;
    }

}
