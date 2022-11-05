package com.chikakraft.onedrive.responses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("user_login_register.php")//has to be an endpoint
    Call<Users> login_register(
            @Query("user_phone") String user_phone,
            @Query("user_auth_token") String user_auth_token,
            @Query("fcm_token") String fcm_token
    );
}
