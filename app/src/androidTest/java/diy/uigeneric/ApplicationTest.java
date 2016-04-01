package diy.uigeneric;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

public class ApplicationTest extends ApplicationTestCase<Application> {

    public ApplicationTest() {
        super(Application.class);
        Log.d("ApplicationTest", "init");
    }

}
