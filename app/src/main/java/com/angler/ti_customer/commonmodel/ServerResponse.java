package com.angler.ti_customer.commonmodel;

/**
 * Created by mahalingam on 03-11-2017.
 * ServerResponse
 */

public class ServerResponse {

    private CommonResponse Response;
    private User_info user_info;
    private Dashboard_info Dashboard_info;


    public CommonResponse getResponse() {
        return Response;
    }

    public User_info getUser() {
        return user_info;
    }

    public Dashboard_info getDashboard() {
        return Dashboard_info;
    }

}
