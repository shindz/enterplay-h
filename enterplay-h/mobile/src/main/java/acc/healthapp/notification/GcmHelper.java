package acc.healthapp.notification;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

import acc.healthapp.R;

public class GcmHelper
{
    private Context context;
    private static final String TOPIC_URI = "/topics/";

    public GcmHelper(final Context context) {
        this.context = context;
    }

    public void registerWithGCM(final String senderID, final String topic) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {

                    InstanceID instanceID = InstanceID.getInstance(context);
                    String token =
                            instanceID.getToken(senderID,
                                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                    Log.d("MainDashActivity(token)", token);

                    registerToTopic(token, topic);

                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                return null;
            }
        }.execute();
    }

    public void registerToTopic(final String token, final String topic) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(context);
        pubSub.subscribe(token, TOPIC_URI + topic, null);
    }

    public static String getTopicName(final String topic) {
        StringBuilder builder = new StringBuilder(TOPIC_URI);
        builder.append(topic);
        return builder.toString();
    }

}
