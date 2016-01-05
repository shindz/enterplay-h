package acc.healthapp;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.parse.Parse;
import com.parse.ParseInstallation;

public class Application extends MultiDexApplication {

    private static Application applicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationInstance = this;

        Parse.enableLocalDatastore(this);

        Parse.initialize(this,
                "CqVyJn4zPfzBqyOqA2q9AA8tQlCxsyDsZI0X6wF2",
                "I0AaALUZWL0wgX9zUknvCHqCvjPU43pJXtsUe5aM");

        Parse.setLogLevel(Parse.LOG_LEVEL_VERBOSE);

        ParseInstallation.getCurrentInstallation().saveInBackground();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);
    }

    public static Application getApplicationInstance() {
        return applicationInstance;
    }

}