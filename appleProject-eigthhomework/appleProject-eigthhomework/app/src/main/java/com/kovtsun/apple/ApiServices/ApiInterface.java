package com.kovtsun.apple.ApiServices;

import com.kovtsun.apple.WeatherGson.Example;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {
    @GET("/v1/current.json")
    Call<Example> getCurrentWeather(@Query("key") String key, @Query("q") String q);
}
