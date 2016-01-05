package acc.healthapp.api;

import java.util.List;

import acc.healthapp.model.Request;

public interface GetAllRequestCallback {

    void getAllRequests(List<Request> requests);
}
