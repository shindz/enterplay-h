package acc.healthapp.model;


public class RequestStatus {
    public static final int OPEN = 1;
    public static final int IN_PROGRESS = 2;
    public static final int COMPLETED = 3;

    private int requestStatus;

    public RequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
    }

    public Number toInt() { return requestStatus; }

    public static RequestStatus fromInt(int requestType) {
        return new RequestStatus(requestType);
    }

    public String toString() {
        if (requestStatus == OPEN) {
            return "Open";
        } else if (requestStatus == IN_PROGRESS) {
            return "In Progress";
        } else {
            return "Completed";
        }
    }
}
