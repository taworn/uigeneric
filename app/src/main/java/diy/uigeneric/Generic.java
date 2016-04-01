package diy.uigeneric;

import android.graphics.Bitmap;

import java.util.Comparator;
import java.util.Date;

/**
 * Generic data.
 * <p>
 * This is a template that serve as data object.  It just composes of data
 * which hiding and methods get/set for accessing data.
 * </p>
 */
public class Generic implements Comparator<Generic> {

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

    private Bitmap icon;    // icon
    private String name;    // name, it trim() before set
    private int category;   // category
    private Date deleted;   // delete flag and date/time when deleted
    private String detail;  // detail, can have multiple lines

    /**
     * Constructs a new data.
     */
    public Generic() {
        this.id = 0;
        this.icon = null;
        this.name = "";
        this.category = 0;
        this.deleted = null;
        this.detail = "";
    }

    /**
     * Constructs a data with primary key.  The primary key cannot be change.
     */
    public Generic(long id) {
        this.id = id;
        this.icon = null;
        this.name = "";
        this.category = 0;
        this.deleted = null;
        this.detail = "";
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
        String string = value.trim();
        if (string.length() <= 0)
            throw new IllegalArgumentException("Item name's length cannot be zero.");
        name = string;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int value) {
        if (value < CATEGORY_MIN || value > CATEGORY_MAX)
            throw new IllegalArgumentException("Item category must in range.");
        category = value;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date value) {
        deleted = value;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String value) {
        detail = value;
    }

    public int compare(Generic left, Generic right) {
        long result = left.getId() - right.getId();
        return result < 0 ? -1 : result > 0 ? 1 : 0;
    }

    public boolean equals(Object object) {
        return object instanceof Generic && ((Generic) object).getId() == id;
    }

}
