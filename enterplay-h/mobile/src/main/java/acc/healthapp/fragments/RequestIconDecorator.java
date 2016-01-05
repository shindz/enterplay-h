package acc.healthapp.fragments;

import android.content.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import acc.healthapp.R;
import acc.healthapp.model.Request;
import acc.healthapp.model.RequestCurrentPriority;
import acc.healthapp.model.RequestType;

public class RequestIconDecorator {

    public final static String ACCEPT = "accept";
    public final static String LATER = "later";

    private Map<String, Integer> dialogDrawables = new HashMap<>();
    private Map<String, Integer> notificationIconDrawables = new HashMap<>();
    private Map<RequestCurrentPriority, Integer> notificationBackgroundDrawables = new HashMap<>();

    public RequestIconDecorator() {
        generatedIcons();
        generateNotificationIcons();
    }

    public int getDrawableForRequest(final Request request) {

        long minutesPassed = getMinutesPassed(request);

        if (request.getPriority() == Request.LOW_PRIORITY) {

            if (minutesPassed > 10) {
                return dialogDrawables.get(request.getRequestType().toString() + "_high");
            } else if ( minutesPassed < 5 ) {
                return dialogDrawables.get(request.getRequestType().toString() + "_med");
            } else {
                return dialogDrawables.get(request.getRequestType().toString() + "_low");
            }

        } else {
            if (minutesPassed > 5) {
                return dialogDrawables.get(request.getRequestType().toString() + "_high");
            } else if ( minutesPassed < 3 ) {
                return dialogDrawables.get(request.getRequestType().toString() + "_med");
            } else {
                return dialogDrawables.get(request.getRequestType().toString() + "_med");
            }
        }
    }

    public int getNotificationIconDrawable(final Request request, final String type) {

        RequestCurrentPriority currentPriority = request.getRequestCurrentPriority();

        int drawableID = -1;

        switch (currentPriority) {
            case HIGH:
                drawableID = notificationIconDrawables.get(getNoticationAction(type, "low"));
                break;
            case MEDIUM:
                drawableID = notificationIconDrawables.get(getNoticationAction(type, "med"));
                break;
            case LOW:
                drawableID = notificationIconDrawables.get(getNoticationAction(type, "high"));
                break;
        }
        return drawableID;
    }

    public int getWearNotificationBackground(final Request request) {
        return notificationBackgroundDrawables.get(request.getRequestCurrentPriority());
    }

    private final String getNoticationAction(final String type,
                                             final String requestCurrentPriority) {
        return type + "_" + requestCurrentPriority;
    }

    private long getMinutesPassed(final Request request) {
        Date startDate = request.getDateCreated();
        Date endDate = new Date();

        long duration  = endDate.getTime() - startDate.getTime();
        return TimeUnit.MILLISECONDS.toMinutes(duration);
    }

    private void generatedIcons() {

        dialogDrawables.put(RequestType.ASSISTANCE_STR + "_low", R.drawable.assistance_low);
        dialogDrawables.put(RequestType.ASSISTANCE_STR + "_med", R.drawable.assistance_med);
        dialogDrawables.put(RequestType.ASSISTANCE_STR + "_high", R.drawable.assistance_high);

        dialogDrawables.put(RequestType.PILLOW_STR + "_low", R.drawable.pillow_low);
        dialogDrawables.put(RequestType.PILLOW_STR + "_med", R.drawable.pillow_med);
        dialogDrawables.put(RequestType.PILLOW_STR + "_high", R.drawable.pillow_high);

        dialogDrawables.put(RequestType.WATER_STR + "_low", R.drawable.water_low);
        dialogDrawables.put(RequestType.WATER_STR + "_med", R.drawable.water_med);
        dialogDrawables.put(RequestType.WATER_STR + "_high", R.drawable.water_high);

        dialogDrawables.put(RequestType.TOILET_STR + "_low", R.drawable.toilet_low);
        dialogDrawables.put(RequestType.TOILET_STR + "_med", R.drawable.toilet_med);
        dialogDrawables.put(RequestType.TOILET_STR + "_high", R.drawable.toilet_high);

        dialogDrawables.put(RequestType.OTHER_STR + "_low", R.drawable.other_low);
        dialogDrawables.put(RequestType.OTHER_STR + "_med", R.drawable.other_med);
        dialogDrawables.put(RequestType.OTHER_STR + "_high", R.drawable.other_high);
    }

    private void generateNotificationIcons() {

        notificationIconDrawables.put(ACCEPT + "_low", R.drawable.accept_low);
        notificationIconDrawables.put(ACCEPT + "_med", R.drawable.accept_med);
        notificationIconDrawables.put(ACCEPT + "_high", R.drawable.accept_high);

        notificationIconDrawables.put(LATER + "_low", R.drawable.later_low);
        notificationIconDrawables.put(LATER + "_med", R.drawable.later_med);
        notificationIconDrawables.put(LATER + "_high", R.drawable.later_high);

        notificationBackgroundDrawables.put(RequestCurrentPriority.HIGH, R.drawable.wear_notification_background_high);
        notificationBackgroundDrawables.put(RequestCurrentPriority.MEDIUM, R.drawable.wear_notification_background_med);
        notificationBackgroundDrawables.put(RequestCurrentPriority.LOW, R.drawable.wear_notification_background_low);
    }
}
