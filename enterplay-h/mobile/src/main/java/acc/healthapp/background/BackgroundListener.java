package acc.healthapp.background;

public interface BackgroundListener {

    /**
     * Called when application goes to the background.
     */
    public void onBackground();

    /**
     * Called when the application goes to the foreground.
     */
    public void onForeground();

}
