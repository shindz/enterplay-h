package acc.healthapp.api;

import android.os.AsyncTask;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import acc.healthapp.model.Request;
import acc.healthapp.model.RequestStatus;
import acc.healthapp.notification.GcmServerSideSender;
import acc.healthapp.notification.NotificationsConstants;

public class HealthAppApi {

    public HealthAppApi() {

    }

    public void sendRequest(final Request request) {

        final ParseObject parseObject = request.convert();

        parseObject.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {

                if (e == null) {
                    broadcastUpdate(NotificationsConstants.NURSE_REQUEST_TOPIC, parseObject);
                }
            }
        });
    }

    public void updateRequest(final Request request) {
        updateRequest(request, null);
    }

    public void updateRequest(final Request request,
                              final RequestUpdateResultCallback callback) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Request.PARSE_OBJECT_KEY);
        query.getInBackground(request.getObjectID(), new GetCallback<ParseObject>() {
            public void done(final ParseObject parseRequest, ParseException e) {
                if (e == null) {
                    request.update(parseRequest);
                    parseRequest.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {
                                broadcastUpdate(NotificationsConstants.PATIENT_REQUEST_TOPIC,
                                        parseRequest);


                                if (callback != null) callback.completed();
                            } else {
                                if (callback != null) callback.error();
                            }
                        }
                    });
                }
            }
        });
    }

    public final void getUserRequests(final String username, final GetAllRequestCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Request.PARSE_OBJECT_KEY);
        query.whereEqualTo(Request.USERNAME_KEY, username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    List<Request> requests = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        Log.d("HealthAppApi", parseObject.toString());
                        requests.add(new Request(parseObject));
                    }

                    callback.getAllRequests(requests);
                }
            }
        });
    }

    public final void getAllOpenRequests(final GetAllRequestCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Request.PARSE_OBJECT_KEY);
        query.whereNotEqualTo(Request.REQUEST_STATUS_KEY, RequestStatus.COMPLETED);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {

                    List<Request> requests = new ArrayList<>();

                    for (ParseObject parseObject : list) {
                        Log.d("HealthAppApi", parseObject.toString());
                        requests.add(new Request(parseObject));
                    }

                    callback.getAllRequests(requests);
                }
            }
        });
    }

    public final void getNewRequest(final String objectID, final GetAllRequestCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Request.PARSE_OBJECT_KEY);
        query.getInBackground(objectID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    List<Request> requests = new ArrayList<>();
                    requests.add(new Request(object));

                    callback.getAllRequests(requests);
                }
            }
        });
    }

    private void broadcastUpdate(final String topicID, final ParseObject parseObject) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                try {

                    Map<String, String> data = new HashMap<>();
                    data.put("objectID", parseObject.getObjectId());

                    GcmServerSideSender serverSideSender = new GcmServerSideSender();
                    serverSideSender.sendHttpJSONMessageWithTopic(topicID, data);

                } catch (IOException exception) {
                    exception.printStackTrace();
                }

                return null;
            }
        }.execute();
    }
}
