package com.covid.database.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InfectedUserService {

    @POST("prod/data")
    Call<InfectedUserResponse> saveInfectedUser(@Body InfectedUserRequest infectedUserRequest);
}
