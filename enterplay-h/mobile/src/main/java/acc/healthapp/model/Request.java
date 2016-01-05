package acc.healthapp.model;

import com.parse.ParseObject;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Request {

    // Request Priority
    public static final int HIGH_PRIORITY = 0x01;
    public static final int LOW_PRIORITY = 0x02;

    public static final String REQUEST_STATUS_KEY = "requestStatus";

    // Request Keys in Parse
    public static final String USERNAME_KEY = "userName";
    private static final String TEXT_KEY = "text";
    private static final String PRIORITY_KEY = "priority";
    private static final String REQUEST_TYPE_KEY = "requestType";
    private static final String NURSE_USERNAME_KEY = "nurseUserName";

    public static final String PARSE_OBJECT_KEY = "PatientRequest";

    private String userName;
    private String text;
    private int priority;
    private RequestType requestType;
    private Date dateCreated;
    private String nurseUserName;
    private RequestStatus requestStatus;

    private String objectID;

    public Request(String userName,
                   String text,
                   int priority,
                   RequestType requestType,
                   String nurseUserName) {
        this.userName = userName;
        this.text = text;
        this.priority = priority;
        this.requestType = requestType;
        this.nurseUserName = nurseUserName;

        // A new request is in the "Open" state
        this.requestStatus = new RequestStatus(RequestStatus.OPEN);
    }

    public Request(final ParseObject parseObject) {
        this.userName = (String) parseObject.get(USERNAME_KEY);
        this.text = (String) parseObject.get(TEXT_KEY);
        this.priority = (Integer) parseObject.get(PRIORITY_KEY);

        this.requestType =
                RequestType.fromInt((Integer) parseObject.get(REQUEST_TYPE_KEY));
        this.dateCreated = parseObject.getCreatedAt();

        this.nurseUserName = (String) parseObject.get(NURSE_USERNAME_KEY);

        this.requestStatus =
                RequestStatus.fromInt((Integer) parseObject.get(REQUEST_STATUS_KEY));

        this.objectID = parseObject.getObjectId();

    }

    public int getPriority() { return priority; }

    public final ParseObject convert() {
        ParseObject requestObject = new ParseObject(PARSE_OBJECT_KEY);

        requestObject.put(USERNAME_KEY, userName);
        requestObject.put(TEXT_KEY, text);
        requestObject.put(PRIORITY_KEY, priority);
        requestObject.put(REQUEST_TYPE_KEY, requestType.toInt());
        requestObject.put(NURSE_USERNAME_KEY, nurseUserName);
        requestObject.put(REQUEST_STATUS_KEY, requestStatus.toInt());

        return requestObject;
    }

    public ParseObject update(ParseObject requestObject) {
        requestObject.put(USERNAME_KEY, userName);
        requestObject.put(TEXT_KEY, text);
        requestObject.put(PRIORITY_KEY, priority);
        requestObject.put(REQUEST_TYPE_KEY, requestType.toInt());
        requestObject.put(NURSE_USERNAME_KEY, nurseUserName);
        requestObject.put(REQUEST_STATUS_KEY, requestStatus.toInt());

        return requestObject;
    }

    public final Date getDateCreated() { return dateCreated; }

    public final RequestType getRequestType() { return requestType; }

    public RequestCurrentPriority getRequestCurrentPriority() {

        Date startDate = dateCreated;
        Date endDate = new Date();

        long duration  = endDate.getTime() - startDate.getTime();
        long minutesPassed = TimeUnit.MILLISECONDS.toMinutes(duration);

        if (priority == LOW_PRIORITY) {

            if (minutesPassed > 10) {
                return RequestCurrentPriority.HIGH;
            } else if ( minutesPassed < 5 ) {
                return RequestCurrentPriority.MEDIUM;
            } else {
                return RequestCurrentPriority.LOW;
            }

        } else {
            if (minutesPassed > 5) {
                return RequestCurrentPriority.HIGH;
            } else if ( minutesPassed < 3 ) {
                return RequestCurrentPriority.MEDIUM;
            } else {
                return RequestCurrentPriority.LOW;
            }
        }
    }

    public RequestStatus getRequestStatus() { return requestStatus; }

    public String getMessage() { return text; }

    public String getObjectID() { return objectID; }

    public void updateStatus(final RequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getUsername() { return userName; }

    public String getNurseUsername() { return nurseUserName; }

    @Override
    public String toString() {
        return this.text;
    }

}
