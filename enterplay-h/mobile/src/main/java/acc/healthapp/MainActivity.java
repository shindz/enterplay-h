package acc.healthapp;

import android.content.Intent;
import android.content.IntentSender;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import java.util.List;

import acc.healthapp.api.GetAllRequestCallback;
import acc.healthapp.api.HealthAppApi;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestColor;
import acc.healthapp.model.RequestType;


public class MainActivity extends ActionBarActivity  implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    private Button kickOffButton;
    private EditText patientUsernameButton;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onStart() {
        super.onStart();
        // mGoogleApiClient.connect();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.sign_in_button).setOnClickListener(this);

        // Future extension
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope("https://www.googleapis.com/auth/calendar.readonly"))
                .build();
        // mGoogleApiClient.connect();

        Intent intent = new Intent(this, MainDashActivity.class);

        intent.putExtra(MainDashActivity.NURSE_USER_NAME_KEY, getString(R.string.nurse_default_name));
        intent.putExtra(MainDashActivity.PATIENT_USER_NAME_KEY, getString(R.string.patient_default_name));
        startActivity(intent);
        finish();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            // mGoogleApiClient.connect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    public void onButtonClicked(View view) {
        Intent intent = new Intent(this, MainDashActivity.class);

        intent.putExtra(MainDashActivity.NURSE_USER_NAME_KEY, getString(R.string.nurse_default_name));
        intent.putExtra(MainDashActivity.PATIENT_USER_NAME_KEY, getString(R.string.patient_default_name));
        startActivity(intent);
        finish();
    }

    private void setupButton() {
        kickOffButton = (Button) findViewById(R.id.kick_off_button);
        kickOffButton.setEnabled(false);

        patientUsernameButton = (EditText) findViewById(R.id.patient_name_edit_text);
        patientUsernameButton.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                enableStartButton();
            }
        });
    }

    private void enableStartButton() {
        final boolean startApp = patientUsernameButton.getText().toString().length() > 3;
        kickOffButton.setEnabled(startApp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sign_in_button) {
            onSignInClicked();
        }
    }

    private void onSignInClicked() {
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    @Override
    public void onConnectionSuspended(int var) {

    }

    @Override
    public void onConnected(Bundle bundle) {

        mShouldResolve = false;

        Intent intent = new Intent(this, MainDashActivity.class);

        intent.putExtra(MainDashActivity.NURSE_USER_NAME_KEY, getString(R.string.nurse_default_name));
        intent.putExtra(MainDashActivity.PATIENT_USER_NAME_KEY, getString(R.string.patient_default_name));
        startActivity(intent);
        // finish();

    }


}
