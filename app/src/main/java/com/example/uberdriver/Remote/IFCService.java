package com.example.uberdriver.Remote;

import com.example.uberdriver.Model.FCMResponse;
import com.example.uberdriver.Model.FCMSendData;
import com.example.uberdriver.Model.Sender;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCService  {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAxaZ9MZI:APA91bG3hDX7po9NeUSMTJOVihwuEM2tRIIyAkOEJI85tJ_w1Ll1BkvP9WpcIXOFimVI6bOe9e7XolTL0byG43wns9ein56-rLnc7fssUQeB2OqV4Yc34Ix6tRWpBWmXaPAQGKhF6FQh"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);

}
