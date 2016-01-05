package acc.healthapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

import acc.healthapp.api.GetAllRequestCallback;
import acc.healthapp.api.HealthAppApi;
import acc.healthapp.background.BackgroundManager;
import acc.healthapp.fragments.OpenRequestsListFragment;
import acc.healthapp.fragments.RequestIconDecorator;
import acc.healthapp.model.Request;
import acc.healthapp.notification.GcmHelper;
import acc.healthapp.notification.NotificationsConstants;

public abstract class MainDashBaseActivity extends ActionBarActivity implements
        OpenRequestsListFragment.OnFragmentInteractionListener {

    public static final String NURSE_USER_NAME_KEY = "username";

    public static final String PATIENT_USER_NAME_KEY = "patientUserName";

    // Topics ID
    protected List<String> topicsID = new ArrayList<>();

    // HealthAPI
    protected HealthAppApi api;
    // GCM Helper
    private GcmHelper gcmHelper;
    // Icon decorator
    RequestIconDecorator iconDecorator;

    // Background Manager
    BackgroundManager backgroundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        backgroundManager = new BackgroundManager(Application.getApplicationInstance());

        iconDecorator = new RequestIconDecorator();

        // creates the health API
        api = new HealthAppApi();

        getGcmInBackground(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mMessageReceiver, new IntentFilter(NotificationsConstants.NEW_NOTIFICATION));
        registerReceiver(mMessageReceiver, new IntentFilter(NotificationsConstants.ACCEPT_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        backgroundManager.terminate();

        unregisterReceiver(mMessageReceiver);
    }

    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    // Abstract Methods
    protected abstract void requestNotificationReceived(Request request,
                                                        final String actionType,
                                                        final Integer notificationID);

    protected abstract void generateNotification(Request request);

    protected boolean isApplicationInBackground() {
        return backgroundManager.isApplicationInBackground();
    }

    protected void dismissNotification(final int notificationID) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.cancel(notificationID);
    }

    private void getGcmInBackground(final Context context) {
        gcmHelper = new GcmHelper(context);

        for (String topic : topicsID) {
                gcmHelper.registerWithGCM(getString(R.string.gcm_defaultSenderId), topic);
        }

    }

    private void generateBroadcast(final String objectID,
                                   final String actionType,
                                   final Integer notificationID) {

        HealthAppApi healthAppApi = new HealthAppApi();
        healthAppApi.getNewRequest(objectID, new GetAllRequestCallback() {
            @Override
            public void getAllRequests(List<Request> requests) {
                if (requests.size() > 0) {
                    Request request = requests.get(0);

                    if (actionType.equalsIgnoreCase(NotificationsConstants.NEW_NOTIFICATION)) {

                        if (isApplicationInBackground()) {
                            generateNotification(request);
                        }
                    }

                    requestNotificationReceived(request, actionType, notificationID);
                }
            }
        });

    }

    //This is the handler that will manager to process the broadcast intent
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            final String action = intent.getAction();

            if (extras != null && action != null) {
                if (extras.containsKey(NotificationsConstants.OBJECT_ID)) {
                    final String objectID = extras.getString(NotificationsConstants.OBJECT_ID);
                    final String from = extras.getString("from");

                    for (String currentTopic : topicsID) {
                        final String thisTopicID = GcmHelper.getTopicName(currentTopic);


                        // The case when there is a new notification
                        if (from != null && from.equalsIgnoreCase(thisTopicID)) {
                            generateBroadcast(objectID, action, null);
                        }  // The case when the broadcast could be from a notification action
                        else if (from == null && !
                                action.equalsIgnoreCase(NotificationsConstants.NEW_NOTIFICATION)) {
                            final int notificationID =
                                    extras.getInt(NotificationsConstants.NOTIFICATION_ID);
                            generateBroadcast(objectID, action, notificationID);
                        }
                    }
                }
            }
        }
    };
}
