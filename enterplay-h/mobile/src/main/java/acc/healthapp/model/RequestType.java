package acc.healthapp.model;


public class RequestType
{
    public static final int PILLOW = 1;
    public static final int WATER = 2;
    public static final int ASSISTANCE = 3;
    public static final int TOILET = 4;
    public static final int OTHER = 5;

    public static final String PILLOW_STR = "Pillow";
    public static final String WATER_STR = "Water";
    public static final String ASSISTANCE_STR = "Assistance";
    public static final String TOILET_STR = "Toilet";
    public static final String OTHER_STR = "Other";

    private int typeOfRequest;

    public RequestType(int requestType) {
        this.typeOfRequest = requestType;
    }

    public Number toInt() { return typeOfRequest; }

    public static RequestType fromInt(int requestType) {
        return new RequestType(requestType);
    }

    public String toString() {

        String statusString;
        switch (typeOfRequest) {
            case PILLOW:
                statusString = PILLOW_STR;
                break;

            case WATER:
                statusString = WATER_STR;
                break;

            case ASSISTANCE:
                statusString = ASSISTANCE_STR;
                break;

            case TOILET:
                statusString = TOILET_STR;
                break;

            case OTHER:
                statusString = OTHER_STR;
                break;

            default:
                statusString = "Unknown";
        }

        return statusString;
    }
}
