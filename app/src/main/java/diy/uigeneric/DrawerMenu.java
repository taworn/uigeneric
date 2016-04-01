package diy.uigeneric;

import java.util.ArrayList;
import java.util.List;

/**
 * Navigation drawer menu structure.
 */
public class DrawerMenu {

    public List<Item> list;

    public DrawerMenu() {
        super();
        this.list = new ArrayList<>();
    }

    public static class Item {
        public String name = null;
        public int icon = 0;
        private Type type;

        public Item(String name) {
            this.type = Type.NORMAL;
            this.name = name;
            this.icon = 0;
        }

        public Item(String name, int icon) {
            this.type = Type.NORMAL;
            this.name = name;
            this.icon = icon;
        }

        public Item(String name, boolean grayed) {
            this.type = grayed ? Type.GRAYED : Type.NORMAL;
            this.name = name;
            this.icon = 0;
        }

        public Type getType() {
            return type;
        }

        public enum Type {
            NORMAL,
            GRAYED,
            RESERVED
        }
    }

}
