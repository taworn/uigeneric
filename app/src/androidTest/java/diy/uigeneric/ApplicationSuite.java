package diy.uigeneric;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import diy.uigeneric.data.SampleDataSourceTest;
import diy.uigeneric.sample.SampleEditActivityTest;
import diy.uigeneric.sample.SampleListActivityTest;
import diy.uigeneric.sample.SampleViewActivityTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        SampleDataSourceTest.class,
        SampleEditActivityTest.class,
        SampleViewActivityTest.class,
        SampleListActivityTest.class,
        ApplicationTest.class,
})
public class ApplicationSuite {
}
