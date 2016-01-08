package acc.healthapp.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;


import acc.healthapp.AppDetail;
import acc.healthapp.GridElementAdapter;
import acc.healthapp.R;
import de.hdodenhof.circleimageview.CircleImageView;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks{
    private static final String TAG = "HomeFragment";
    public static final int RC_SIGN_IN = 0;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;
    /* Circle image to display user profile image */
    private CircleImageView mProfileImage;
    /* Text views to display name and email id */
    private TextView mTextViewName, mTextViewEmail;

    private PackageManager manager;
    private List<AppDetail> apps;
    private HorizontalGridView mHorizontalGridView;
    private ProgressBar progress;

    public static HomeFragment getInstance(int position) {
        HomeFragment homeFragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        homeFragment.setArguments(args);

        return homeFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        //Progress bar
        progress = (ProgressBar) layout.findViewById(R.id.progressBar2);
        progress.setVisibility(View.GONE);
        //Person Image
        mProfileImage = (CircleImageView) layout.findViewById(R.id.user_account_picture_profile);
        //Person name
        mTextViewName = (TextView) layout.findViewById(R.id.textViewNameValue);
        //Person email
        mTextViewEmail = (TextView) layout.findViewById(R.id.textViewEmailValue);
        //Launcher apps
        mHorizontalGridView = (HorizontalGridView) layout.findViewById(R.id.gridView);
//        mHorizontalGridView.setOnTouchListener(new ListView.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getAction();
//                switch (action) {
//                    case MotionEvent.ACTION_DOWN:
//                        // Disallow ScrollView to intercept touch events.
//                        v.getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//                        // Allow ScrollView to intercept touch events.
//                        v.getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                        break;
//                }
//
//                // Handle HorizontalScrollView touch events.
//                v.onTouchEvent(event);
//                return true;
//            }
//        });
        // [START configure_signin]
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity().getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

        //Add onclick to profile image
        mProfileImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                signInWithGplus();

            }
        });

        loadHGridView();
        return layout;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(getActivity(), RC_SIGN_IN);
                    mIsResolving = true;
                    progress.setVisibility(View.VISIBLE);
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                    progress.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Retrieve some profile information to personalize our app for the user.
        Log.d("Home Fragement", "Connected");
        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        getProfileInformation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode,
                                 Intent intent) {

        if (requestCode == RC_SIGN_IN) {
            if (responseCode != Activity.RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;

            if (!mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
                progress.setVisibility(View.VISIBLE);
            }
        }
    }

    /*
        Function which connects to google api
     */
    private void signInWithGplus(){
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    /**
     * Sign-out from google
     * */
    private void signOutFromGplus() {
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mGoogleApiClient.connect();
        }
    }

    /**
     * Fetching user's information name, email, profile pic
     * */
    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi
                        .getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                //set the person name
                mTextViewName.setText(personName);

                String personPhotoUrl = currentPerson.getImage().getUrl();
                String personGooglePlusProfile = currentPerson.getUrl();
                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                //set the person email
                mTextViewEmail.setText(email);

                //Get the profile oic of the user
                progress.setVisibility(View.VISIBLE);
                new GetImageFromUrl().execute(personPhotoUrl);

                Log.e(TAG, "Name: " + personName + ", plusProfile: "
                        + personGooglePlusProfile + ", email: " + email
                        + ", Image: " + personPhotoUrl + " user id:"
                        + currentPerson.getId());






            } else {
                Toast.makeText(getActivity().getApplicationContext(), "Person information is null",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Async task for downloading profile pic
    public class GetImageFromUrl extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap map = null;
            for (String url : urls) {
                map = downloadImage(url);
            }
            return map;
        }

        // Sets the Bitmap returned by doInBackground
        @Override
        protected void onPostExecute(Bitmap result) {
            progress.setVisibility(View.GONE);
            mProfileImage.setImageBitmap(result);
        }

        // Creates Bitmap from InputStream and returns it
        private Bitmap downloadImage(String url) {
            Bitmap bitmap = null;
            InputStream stream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 1;

            try {
                stream = getHttpConnection(url);
                bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
                stream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return bitmap;
        }

        // Makes HttpURLConnection and returns InputStream
        private InputStream getHttpConnection(String urlString)
                throws IOException {
            InputStream stream = null;
            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();

            try {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.setRequestMethod("GET");
                httpConnection.connect();

                if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    stream = httpConnection.getInputStream();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return stream;
        }
    }

    /*
        Function which will list out all installed google apps accept the launcher app
     */
    private void loadApps(){
        manager = getActivity().getPackageManager();
        apps = new ArrayList<AppDetail>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> availableActivities = manager.queryIntentActivities(i, 0);

        for(ResolveInfo ri:availableActivities){
            AppDetail app = new AppDetail();
            app.label = ri.loadLabel(manager);
            String packageName = ri.activityInfo.packageName;
            Log.d("packageName",""+packageName);
            app.name = packageName;
            app.icon = ri.activityInfo.loadIcon(manager);
            if(packageName.contains("com.google") && !packageName.contains("launcher"))
            apps.add(app);
        }
    }

    /*
        Function to populate the Horizotal Grid View for App display
     */
    private void loadHGridView(){
        loadApps();
        GridElementAdapter adapter = new GridElementAdapter(getActivity(),apps);

        mHorizontalGridView.setAdapter(adapter);
    }
}
