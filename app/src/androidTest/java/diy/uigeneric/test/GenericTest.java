package diy.uigeneric.test;

import android.test.AndroidTestCase;

import diy.uigeneric.Generic;

public class GenericTest extends AndroidTestCase {

    public GenericTest() {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void test() throws Throwable {
        Generic generic0 = new Generic();
        generic0.setName(" A z ");
        assertFalse("[  A z ] =x [ A z]", generic0.getName().equals(" A z"));
        assertFalse("[  A z ] =x [A z ]", generic0.getName().equals("A z "));
        assertFalse("[  A z ] =x [Az]", generic0.getName().equals("Az"));
        assertTrue("[  A z ] => [A z]", generic0.getName().equals("A z"));

        Generic generic1 = new Generic(1);
        assertFalse("1 =x 0", generic1.getId() == 0);
        assertTrue("1 => 1", generic1.getId() == 1);
        generic1.setName(" A z ");
        assertFalse("[  A z ] =x [ A z]", generic1.getName().equals(" A z"));
        assertFalse("[  A z ] =x [A z ]", generic1.getName().equals("A z "));
        assertFalse("[  A z ] =x [Az]", generic1.getName().equals("Az"));
        assertTrue("[  A z ] => [A z]", generic1.getName().equals("A z"));
    }

}
