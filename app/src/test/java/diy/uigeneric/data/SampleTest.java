package diy.uigeneric.data;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SampleTest {

    @Test
    public void test() throws Exception {
        Sample item = new Sample();

        assertEquals(item.getId(), 0);
        assertEquals(item.getIcon(), null);
        assertEquals(item.getName(), "");
        assertEquals(item.getDetail(), "");
        assertEquals(item.getCategory(), 0);
        assertEquals(item.getDeleted(), null);

        item.setIcon(null);
        item.setName("A");
        item.setDetail("Aaa");
        item.setCategory(Sample.CATEGORY_ARCHIVED);
        item.setDeleted(true);

        assertEquals(item.getIcon(), null);
        assertEquals(item.getName(), "A");
        assertEquals(item.getDetail(), "Aaa");
        assertEquals(item.getCategory(), Sample.CATEGORY_ARCHIVED);
        assertNotNull(item.getDeleted());
    }

    @Test
    public void equals() throws Exception {
        Sample item0 = new Sample();
        Sample item1 = new Sample();
        assertTrue(item0.equals(item1));
        assertEquals(item0.compare(item0, item1), 0);
    }

}
