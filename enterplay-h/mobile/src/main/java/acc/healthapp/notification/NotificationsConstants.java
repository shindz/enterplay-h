package acc.healthapp.notification;

public class NotificationsConstants {

    /**
     * Topic that is use to receive requests on the nurse application
     */
    public final static String NURSE_REQUEST_TOPIC = "nurse-requests";

    /**
     * Topic that is use to receive responses from the nurse request
     */
    public final static String PATIENT_REQUEST_TOPIC = "patient-requests";

    public final static String NEW_NOTIFICATION = "unique_name";

    /**
     * Actions for the notifications
     */
    public final static String ACCEPT_ACTION = "request.accept";
    public final static String LATER_ACTION = "request.later";

    /**
     * Passed in the notification for a unique ID of a request
     */
    public final static String OBJECT_ID = "objectID";
    public final static String NOTIFICATION_ID = "notificationID";

}
