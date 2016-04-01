package diy.uigeneric;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class TestData {

    GenericDataSource source = null;

    private long insert(Bitmap icon, String name) {
        Generic item = new Generic();
        item.setIcon(icon);
        item.setName(name);
        return source.insert(item);
    }

    private long insert(Bitmap icon, String name, String detail) {
        Generic item = new Generic();
        item.setIcon(icon);
        item.setName(name);
        item.setDetail(detail);
        return source.insert(item);
    }

    private long insert(Bitmap icon, String name, int category) {
        Generic item = new Generic();
        item.setIcon(icon);
        item.setName(name);
        item.setCategory(category);
        return source.insert(item);
    }

    private long insert(Bitmap icon, String name, int category, String detail) {
        Generic item = new Generic();
        item.setIcon(icon);
        item.setName(name);
        item.setCategory(category);
        item.setDetail(detail);
        return source.insert(item);
    }

    public void prepare(Context context) {
        source = new GenericDataSource(context);
        source.open();

        Bitmap icon0 = fromDrawableResource(context, R.drawable.ic_action_help);
        Bitmap icon1 = fromDrawableResource(context, R.drawable.ic_action_important);
        Bitmap icon2 = fromDrawableResource(context, R.drawable.ic_action_collection);
        String test0 = context.getResources().getString(R.string.test_item_detail_0);
        String test1 = context.getResources().getString(R.string.test_item_detail_1);
        String test2 = context.getResources().getString(R.string.test_item_detail_2);

        insert(icon0, "AAA", test0);
        insert(null, "Aaa", test1);
        insert(null, "aaa");

        insert(icon0, "IiiI", Generic.CATEGORY_IMPORTANTS, test0);
        insert(icon1, "iIIi", Generic.CATEGORY_IMPORTANTS, test1);
        insert(icon2, "_", Generic.CATEGORY_IMPORTANTS);

        insert(icon0, "gen", Generic.CATEGORY_SENT, test0);
        insert(icon1, "Gene", Generic.CATEGORY_SENT, test1);
        insert(icon2, "- - - - -", Generic.CATEGORY_DRAFTS);

        insert(icon1, "55555");
        insert(icon2, "0-9");

        insert(null, "Generic 1", test2);
        insert(null, "Generic 2", test2);
        insert(null, "Generic 3", test2);
        insert(null, "Generic 4", test2);
        insert(null, "Generic 5", test2);
        insert(null, "Generic 6", test2);
        insert(null, "Generic 7", test2);
        insert(null, "Generic 8", test2);
        insert(null, "Generic 9", test2);
        insert(null, "Generic 10", test2);
        insert(null, "Generic 11", test2);
        insert(null, "Generic 12", test2);

        insert(null, "Power");
        insert(null, "power less");
        insert(null, "Power more", test0);
        insert(null, "Power moderate", test1);
        insert(null, "Power max", test2);
        insert(null, "Power none");

        insert(icon0, "HELP HELp HElp Help help                           -_-", test2);

        insert(icon0, "saved", Generic.CATEGORY_ARCHIVED, test0);
        insert(icon1, "archived", Generic.CATEGORY_ARCHIVED, test1);
        insert(icon2, "test archived", Generic.CATEGORY_ARCHIVED, test2);
        insert(null, "max archived", Generic.CATEGORY_ARCHIVED);
        insert(null, "none archived", Generic.CATEGORY_ARCHIVED);

        insert(icon1, "$$$");
        insert(null, "---", test2);

        insert(icon0, "Zzz", "Zzz Zzz Zzz");

        source.close();
    }

    private Bitmap fromDrawableResource(Context context, int resource) {
        return BitmapFactory.decodeResource(context.getResources(), resource);
    }

}