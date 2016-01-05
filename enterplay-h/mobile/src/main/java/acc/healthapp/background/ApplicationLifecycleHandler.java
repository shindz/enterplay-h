package acc.healthapp.background;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ApplicationLifecycleHandler implements Application.ActivityLifecycleCallbacks {

    public static final String TAG = ApplicationLifecycleHandler.class.getName();
    /**
     * Note: This implementation is based on this blog
     *
     * http://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html
     *
     * When an application transitions between two Activities there is a brief period during which
     * the first Activity is paused and the second Activity has not yet resumed, thus this delay
     * is use for notifying the listeners
     */
    public static final long APPLICATION_STATE_DELAY = 500;
    private Handler handler = new Handler();
    private List<BackgroundListener> listeners = new CopyOnWriteArrayList<>();

    private boolean foreground = false, paused = true;

    /**
     * Determines if the app is currently visible or not
     * @return True if the application is not in the background
     */
    public boolean isApplicationInForeground() {
        return foreground;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (wasBackground) {
            Log.i(TAG, "Application is in foreground");

            for (BackgroundListener listener : listeners) {
                listener.onForeground();
            }
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        paused = true;

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (foreground && paused) {
                        foreground = false;

                        Log.i(TAG, "Application is in the Background");

                        for (BackgroundListener listener : listeners) {
                            listener.onBackground();
                        }
                    }


                }
            }, APPLICATION_STATE_DELAY);

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    /**
     * Adds a listener to get notified when the application goes to the background/foreground.
     * @param listener Background listener object.
     */
    public void addListener(BackgroundListener listener){
        listeners.add(listener);
    }

    /**
     * Removes a listener to get notified when the application goes to the background/foreground.
     * @param listener Background listener object.
     */
    public void removeListener(BackgroundListener listener) {
        listeners.remove(listener);
    }
}
