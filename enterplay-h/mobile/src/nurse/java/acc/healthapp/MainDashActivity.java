package acc.healthapp;

import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.Date;

import acc.healthapp.api.RequestUpdateResultCallback;
import acc.healthapp.fragments.NurseDialogHelper;
import acc.healthapp.fragments.OpenRequestsListFragment;
import acc.healthapp.fragments.RequestIconDecorator;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestStatus;
import acc.healthapp.notification.NotificationID;
import acc.healthapp.notification.NotificationsConstants;

public class MainDashActivity extends MainDashBaseActivity {

    private LinearLayout mainView;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    private OpenRequestsListFragment listFragment;
    private NurseDialogHelper dialogHelper;

    private String userName;

    // Key for the string that's delivered in the action's intent
    private static final String EXTRA_VOICE_REPLY = "extra_voice_reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        topicsID.add(NotificationsConstants.NURSE_REQUEST_TOPIC);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_dash);

        this.mainView = (LinearLayout) findViewById(R.id.content_frame);

        // This is the patient assigned to this nurse
        this.userName = getIntent().getExtras().getString(PATIENT_USER_NAME_KEY);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
        setSupportActionBar(toolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_opened,
                R.string.drawer_closed
        ) {

            public void onDrawerClosed(View view) {
                //super.onDrawerClosed(view);
                //supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                //super.onDrawerOpened(drawerView);
                //supportInvalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // super.onDrawerSlide(drawerView, slideOffset);
                mainView.setTranslationX(slideOffset * drawerView.getWidth());

            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Feeds");

        mDrawerToggle.syncState();
        dialogHelper = new NurseDialogHelper(api, iconDecorator);

        setupFeeds();
        setupDrawerActions();
    }



    protected void onResume() {
        super.onResume();
    }

    protected void requestNotificationReceived(Request request,
                                               final String actionType,
                                               final Integer notificationID) {

        if (actionType.equalsIgnoreCase(NotificationsConstants.NEW_NOTIFICATION)) {

            if (!isApplicationInBackground()) {
                showRequestDialog(request);
            }

        } else if (actionType.equalsIgnoreCase(NotificationsConstants.ACCEPT_ACTION)) {
            // update the request to complete
            request.updateStatus(new RequestStatus(RequestStatus.IN_PROGRESS));
            api.updateRequest(request, new RequestUpdateResultCallback() {
                @Override
                public void completed() {
                    if (listFragment != null) {
                        listFragment.updateData();
                    }
                }

                @Override
                public void error() {

                }
            });
            // dismiss the notification
            if (notificationID != null) {
                dismissNotification(notificationID);
            }
        } else if (actionType.equalsIgnoreCase(NotificationsConstants.LATER_ACTION)) {
            // Nothing todo for now
            if (notificationID != null) {
                dismissNotification(notificationID);
            }
        }

    }

    protected void generateNotification(Request request) {
        final int notificationID = NotificationID.getID();

        Intent intent = new Intent(this, MainDashActivity.class);
        intent.putExtra(NotificationsConstants.OBJECT_ID, request.getObjectID());
        intent.putExtra(NotificationsConstants.NOTIFICATION_ID, notificationID);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // This is the accept action intent
        PendingIntent acceptPendingIntent =
                generatePendingIntent(NotificationsConstants.ACCEPT_ACTION,
                        request.getObjectID(),
                        notificationID);

        // This is the later action intent
        PendingIntent laterPendingIntent =
                generatePendingIntent(NotificationsConstants.LATER_ACTION,
                        request.getObjectID(),
                        notificationID);

        String replyLabel = "accept";

        RemoteInput remoteInput = new RemoteInput.Builder(EXTRA_VOICE_REPLY)
                .setLabel(replyLabel)
                .build();

        Bitmap bg = BitmapFactory.decodeResource(getResources(),
                iconDecorator.getWearNotificationBackground(request));

        Date date = request.getDateCreated();
        long epoch = date.getTime();

        CharSequence timePassedString =
                DateUtils.getRelativeTimeSpanString(epoch,
                        System.currentTimeMillis(),
                        DateUtils.SECOND_IN_MILLIS);

        StringBuilder wearableMessage = new StringBuilder("Room 121.");
        wearableMessage.append(request.getUsername());
        wearableMessage.append(" ");
        wearableMessage.append(timePassedString);

        // Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(
                        iconDecorator.getNotificationIconDrawable(request, RequestIconDecorator.ACCEPT),
                        getString(R.string.accept_notification), acceptPendingIntent)
                        .build();

        NotificationCompat.Action later =
                new NotificationCompat.Action.Builder(
                        iconDecorator.getNotificationIconDrawable(request, RequestIconDecorator.LATER),
                        getString(R.string.later_notification), acceptPendingIntent)
                        .build();



        NotificationCompat.WearableExtender wearableExtender =
                new NotificationCompat.WearableExtender()
                        .setHintHideIcon(false)
                        .setBackground(bg)
                        .addAction(later)
                        .addAction(action);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(request.getRequestType().toString())
                .setSmallIcon(R.drawable.messages_icon)
                .setContentText(wearableMessage)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.later_notification,
                        getString(R.string.later_notification),
                        laterPendingIntent)
                .addAction(R.drawable.accept_notification,
                        getString(R.string.accept_notification),
                        acceptPendingIntent)
                .extend(wearableExtender);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationID, builder.build());
    }

    private PendingIntent generatePendingIntent(final String filter,
                                                final String objectID,
                                                final int notificationID) {
        Intent acceptIntent = new Intent(filter);
        acceptIntent.putExtra(NotificationsConstants.OBJECT_ID, objectID);
        acceptIntent.putExtra(NotificationsConstants.NOTIFICATION_ID, notificationID);

        PendingIntent acceptPendingIntent =
                PendingIntent.getBroadcast(this, 0, acceptIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        return acceptPendingIntent;
    }

    private void setupFeeds() {
        if (listFragment == null) {
            listFragment = OpenRequestsListFragment.newInstance(userName,
                                                                dialogHelper,
                                                                iconDecorator,
                                                                R.color.nurse_request_list_background_color);
        }

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, listFragment)
                .commit();
    }

    private void setupDrawerActions() {
        LinearLayout emergencyButton = (LinearLayout) findViewById(R.id.feeds);
        emergencyButton.setBackgroundColor(getResources().
                getColor(R.color.left_drawer_selected_background_color));
        final View highLightView = findViewById(R.id.feeds_activehighlight);
        emergencyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setBackgroundColor(getResources().
                        getColor(R.color.left_drawer_selected_background_color));
                highLightView.setVisibility(View.VISIBLE);
                mDrawerLayout.closeDrawers();

            }
        });
    }

    private void showRequestDialog(final Request request) {
        dialogHelper.renderDialog(this, request)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        listFragment.updateData();
                    }
                });
    }

}
