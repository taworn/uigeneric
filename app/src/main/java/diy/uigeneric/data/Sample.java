package diy.uigeneric.data;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Sample data.
 * <p>
 * This is a template that serve as data object.  It just composes of data
 * which hiding and methods get/set for accessing data.
 * </p>
 */
public class Sample implements Comparator<Sample> {

    // category string constants
    public static final CharSequence[] categoryToString = {
            "Data",
            "Priority Data",
            "Importants",
            "Sent",
            "Drafts",
            "Archived",
    };

    // category constants
    public static int CATEGORY_DATA = 0;
    public static int CATEGORY_PRIORITY_DATA = 1;
    public static int CATEGORY_IMPORTANTS = 2;
    public static int CATEGORY_SENT = 3;
    public static int CATEGORY_DRAFTS = 4;
    public static int CATEGORY_ARCHIVED = 5;
    public static int CATEGORY_MIN = 0;
    public static int CATEGORY_MAX = 5;

    private long id;  // primary key

    private Bitmap icon;       // icon
    private String name;       // name, it trim() before set
    private String detail;     // detail, can have multiple lines
    private int category;      // category
    private Calendar deleted;  // deleted date/time when deleted, otherwise, it is null

    /**
     * Constructs a new data.
     */
    public Sample() {
        this.id = 0;
        this.icon = null;
        this.name = "";
        this.detail = "";
        this.category = 0;
        this.deleted = null;
    }

    /**
     * Constructs a data with primary key.  The primary key cannot be change.
     */
    public Sample(long id) {
        this.id = id;
        this.icon = null;
        this.name = "";
        this.detail = "";
        this.category = 0;
        this.deleted = null;
    }

    public long getId() {
        return id;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap value) {
        icon = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        String s = value == null ? "" : value.trim();
        if (s.length() <= 0)
            throw new IllegalArgumentException("Item name's length cannot be zero.");
        name = s;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String value) {
        detail = value == null ? "" : value;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int value) {
        if (value < CATEGORY_MIN || value > CATEGORY_MAX)
            throw new IllegalArgumentException("Item category must in range.");
        category = value;
    }

    public Calendar getDeleted() {
        return deleted;
    }

    public void setDeleted(Calendar value) {
        if (value != null) {
            deleted = new GregorianCalendar(Locale.US);
            deleted.setTimeInMillis(value.getTimeInMillis());
        }
        else
            deleted = null;
    }

    public void setDeleted(boolean value) {
        if (value)
            deleted = new GregorianCalendar(Locale.US);
        else
            deleted = null;
    }

    @Override
    public boolean equals(@NonNull Object o) {
        return o instanceof Sample && ((Sample) o).getId() == id;
    }

    @Override
    public int compare(@NonNull Sample l, @NonNull Sample r) {
        long result = l.getId() - r.getId();
        return result < 0 ? -1 : result > 0 ? 1 : 0;
    }

}
