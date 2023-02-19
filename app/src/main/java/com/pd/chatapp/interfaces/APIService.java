package com.pd.chatapp.interfaces;

import com.pd.chatapp.Notifications.MyResponse;
import com.pd.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAANQrnDBM:APA91bFwwYClQdQXDSH1H_sJbXJNlo9T2ul-8-k1ZlEt32XTZgaBHCZ_pbcOeFwgjAElr4_ejIErIh2zp1XgbD3rN92oUN2kC8pTSGtmSrLgouctCJ95e73IDtAdyg5scWdaNcACRVGD"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
