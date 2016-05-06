package diy.uigeneric;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import diy.uigeneric.data.SampleServerDataSourceTest;
import diy.uigeneric.data.SampleServerIndirectListTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SampleServerDataSourceTest.class,
        SampleServerIndirectListTest.class,
})
public class ApplicationSuiteServer {
    /*
     * PLEASE READ!
     *
     * - Run main program first.
     * - Open Settings page.
     * - Set base server address.
     * - Run this test programs.
     */
}
