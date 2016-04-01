package diy.uigeneric;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import diy.uigeneric.data.SampleDataSourceTest;
import diy.uigeneric.sample.SampleEditActivityTest;
import diy.uigeneric.sample.SampleListActivityTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SampleDataSourceTest.class,
        SampleEditActivityTest.class,
        SampleListActivityTest.class,
        ApplicationTest.class,
})
public class ApplicationSuite {
}
