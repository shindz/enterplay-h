package acc.healthapp.background;

import android.app.Application;
import android.util.Log;

import java.lang.ref.WeakReference;

public class BackgroundManager implements BackgroundListener {

    private static final String TAG = BackgroundManager.class.getName();
    private WeakReference<ApplicationLifecycleHandler> mApplicationLifecycle;
    private boolean mApplicationInBackground = true;

    /**
     * Default constructor.
     * @param application The mobile application object.
     */
    public BackgroundManager(Application application) {
        // Creates the application lifecycle, registers (self) to get notified on application
        // changes and then register the lifecycle handler with the application.
        mApplicationLifecycle = new WeakReference<>(new ApplicationLifecycleHandler());
        mApplicationLifecycle.get().addListener(this);

        application.registerActivityLifecycleCallbacks(mApplicationLifecycle.get());

        mApplicationInBackground = false;
    }

    /**
     * TODO: Add comments once we have solid requirements.
     */
    public void onBackground() {
        Log.d(TAG, "Going to background");
        mApplicationInBackground = true;
    }

    /**
     * TODO: Add comments once we have solid requirements.
     */
    public void onForeground() {
        Log.d(TAG, "Going to foreground");
        mApplicationInBackground = false;
    }

    /**
     * Removes this instance as the application listener.
     */
    public void terminate() {
        mApplicationLifecycle.get().removeListener(this);
        Log.d(TAG, "Removed as background listener");
    }

    /**
     * This method returns true if the application is on the background, false otherwise.
     * @return True when the application is on the background, false otherwise.
     */
    public boolean isApplicationInBackground() {
        return mApplicationInBackground;
    }
}
