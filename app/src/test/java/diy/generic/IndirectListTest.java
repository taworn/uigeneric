package diy.generic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IndirectListTest {

    @Test
    public void add() throws Exception {
        List<String> l = new ArrayList<>();
        l.add("A");
        l.add("B");
        l.add("C");

        IndirectList<String> il = new IndirectList<>();
        il.set(l);
        assertEquals(il.get(0).item, "A");
        assertEquals(il.get(1).item, "B");
        assertEquals(il.get(2).item, "C");
        assertEquals(il.size(), 3);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void addError() throws Exception {
        List<String> l = new ArrayList<>();
        IndirectList<String> il = new IndirectList<>();
        il.set(l);
        assertEquals(il.size(), 0);
        assertEquals(il.get(0), null);
    }

}
