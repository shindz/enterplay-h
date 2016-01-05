package acc.healthapp.notification;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.gcm.GcmListenerService;

public class HealthAppGcmReceiver extends GcmListenerService {

    public HealthAppGcmReceiver() {
    }

    @Override
    public void onMessageReceived(String from, Bundle data) {

        final String objectID = data.getString("objectID");

        if (objectID != null) {
            sendNotification(objectID, from);
        }
    }

    @Override
    public void onDeletedMessages()  {}

    @Override
    public void onMessageSent(String msgId) {
    }

    @Override
    public void onSendError(String msgId, String error) {
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(final String objectID, final String from) {

        Intent newIntent = new Intent(NotificationsConstants.NEW_NOTIFICATION);

        //put whatever data you want to send, if any
        newIntent.putExtra("objectID", objectID);
        newIntent.putExtra("from", from);

        //send broadcast
        getApplicationContext().sendBroadcast(newIntent);
    }

}
