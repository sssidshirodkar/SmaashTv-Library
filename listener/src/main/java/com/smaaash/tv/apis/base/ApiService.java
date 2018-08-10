package com.smaaash.tv.apis.base;

import org.json.JSONObject;

import models.EpisodeDetailResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 * Created by Siddhesh on 18-07-2018.
 */

public interface ApiService {

    @POST("/dev/api/acrdata")
    Call<ResponseBody> uploadPhoneState(@Body JSONObject object);

    @GET
    Call<EpisodeDetailResponse> getEpisodeDetails(@Url String url);

}
