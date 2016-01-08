package acc.healthapp;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.telly.mrvector.MrVector;


import acc.healthapp.fragments.FragmentDummy;
import acc.healthapp.fragments.HomeFragment;
import acc.healthapp.fragments.PatientDialogHelper;
import acc.healthapp.fragments.ReqFragment;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestStatus;
import acc.healthapp.notification.NotificationID;
import acc.healthapp.notification.NotificationsConstants;
import acc.healthapp.tabs.SlidingTabLayout;
import java.util.List;


public class MainDashActivity extends MainDashBaseActivity {

    private ViewPager mPager;
    private SlidingTabLayout mTabs;
    private String patientUserName;
    private String nurseUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        topicsID.add(NotificationsConstants.PATIENT_REQUEST_TOPIC);
        // topicID = NotificationsConstants.PATIENT_REQUEST_TOPIC;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dash);
        setupTabs();

        this.nurseUsername = getIntent().getExtras().getString(NURSE_USER_NAME_KEY);
        this.patientUserName = getIntent().getExtras().getString(PATIENT_USER_NAME_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    protected void requestNotificationReceived(Request request,
                                               final String actionType,
                                               final Integer notificationID) {

        // verify if its a in-progress status
        if (request.getRequestStatus().toInt().intValue() == RequestStatus.IN_PROGRESS) {
            PatientDialogHelper dialogHelper = new PatientDialogHelper();
            dialogHelper.renderDialog(this, request);
        }

        List<Fragment> allFragments = getSupportFragmentManager().getFragments();
        if (allFragments != null) {

            for (Fragment fragment : allFragments) {

                if (fragment instanceof ReqFragment) {
                    ReqFragment requestFragment = (ReqFragment) fragment;

                    if (requestFragment != null) {
                        requestFragment.updateData();
                    }
                }

            }
        }
    }

    protected void generateNotification(Request request) {

        final int notificationID = NotificationID.getID();

        Intent intent = new Intent(this, MainDashActivity.class);
        intent.putExtra(NotificationsConstants.OBJECT_ID, request.getObjectID());
        intent.putExtra(NotificationsConstants.NOTIFICATION_ID, notificationID);

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        StringBuilder titleBuilder = new StringBuilder();
        titleBuilder.append("Your ");
        titleBuilder.append(request.getRequestType().toString().toLowerCase());
        titleBuilder.append(" is on the way");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(titleBuilder.toString())
                .setSmallIcon(R.drawable.messages_icon)
                .setContentText(request.getMessage())
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationID, builder.build());
    }

    private void setupTabs() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setOffscreenPageLimit(5);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        //Pronob Changes
        mPager.requestDisallowInterceptTouchEvent(true);
        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPager.setCurrentItem(mPager.getCurrentItem());
                return true;
            }
        });
        //Pronob Changes
        mTabs = (SlidingTabLayout) findViewById(R.id.tabs);
        mTabs.setCustomTabView(R.layout.custom_tab_view, R.id.tabText);
        //make sure all tabs take the full horizontal screen space and divide it equally amongst themselves
        mTabs.setDistributeEvenly(true);
        mTabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //color of the tab indicator
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.colorPrimaryLight));
        mTabs.setViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_activity_using_tab_library, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onFragmentInteraction(String id) {

    }

    class MyPagerAdapter extends FragmentPagerAdapter {

        private String[] tabText = getResources().getStringArray(R.array.tabs);
        private Integer[] iconResourceArray = { R.drawable.homeicon, R.drawable.entertainmenticon,
                R.drawable.requestsicon,R.drawable.scheduleicon,R.drawable.contacticon};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            tabText = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                HomeFragment homeFragment = HomeFragment.getInstance(position);
                return homeFragment;
            }else if (position == 2) {
                ReqFragment reqFragment =
                        ReqFragment.getInstance(position, patientUserName, nurseUsername, iconDecorator);
                return reqFragment;
            } else {
                FragmentDummy fragmentDummy = FragmentDummy.getInstance(position);
                return fragmentDummy;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {

            //use the MrVector library to inflate vector drawable inside tab
            Drawable drawable = MrVector.inflate(getResources(), iconResourceArray[position]);
            //set the size of drawable to 36 pixels
            drawable.setBounds(0, 0, 60, 60);
            ImageSpan imageSpan = new ImageSpan(drawable,ImageSpan.ALIGN_BOTTOM);
            //to make our tabs icon only, set the Text as blank string with white space
            SpannableString spannableString = new SpannableString("  " + tabText[position]);
            spannableString.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("Main Dash","Called");
        Log.d("Main Dash",""+data);
        if (requestCode == HomeFragment.RC_SIGN_IN) {
            List<Fragment> allFragments = getSupportFragmentManager().getFragments();
            if (allFragments != null) {

                for (Fragment fragment : allFragments) {

                    if (fragment instanceof HomeFragment) {
                        HomeFragment homefragment = (HomeFragment) fragment;

                        if (homefragment != null) {
                            Log.d("Fragment","Not Null");
                            homefragment.onActivityResult(requestCode, resultCode, data);
                        }
                    }

                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
