package acc.healthapp;


import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import acc.healthapp.api.GetAllRequestCallback;
import acc.healthapp.fragments.OpenRequestsListFragment;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestCurrentPriority;
import acc.healthapp.notification.NotificationsConstants;

public class MainDashActivity extends MainDashBaseActivity {

    private OpenRequestsListFragment listFragment;
    private String userName;
    private TextView openRequestsTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        topicsID.add(NotificationsConstants.PATIENT_REQUEST_TOPIC);
        topicsID.add(NotificationsConstants.NURSE_REQUEST_TOPIC);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_dashboard);

        this.openRequestsTitleTextView =
                (TextView) findViewById(R.id.dashboard_open_status_title);

        this.userName = getIntent().getExtras().getString(PATIENT_USER_NAME_KEY);

        setupOpenRequestsList();

    }

    protected void onResume() {
        super.onResume();

        updateOpenStatusTitle();
    }

    protected void requestNotificationReceived(Request request,
                                               final String actionType,
                                               final Integer notificationID) {
        updateOpenStatusTitle();

        if (listFragment != null) {
            listFragment.updateData();
        }
    }

    protected void generateNotification(Request request){}

    private void setupOpenRequestsList() {

        listFragment =
                OpenRequestsListFragment.newInstance(userName,
                        null, iconDecorator,
                        R.color.dashboard_right_background_color,
                        RequestCurrentPriority.HIGH);


        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.dashboard_open_status, listFragment)
                .commit();
    }

    private void updateOpenStatusTitle() {
        api.getAllOpenRequests(new GetAllRequestCallback() {
            @Override
            public void getAllRequests(List<Request> requests) {
                final StringBuilder message = new StringBuilder(Integer.toString(requests.size()));
                message.append(" Open Requests");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openRequestsTitleTextView.setText(message.toString());
                    }
                });
            }
        });
    }
}
