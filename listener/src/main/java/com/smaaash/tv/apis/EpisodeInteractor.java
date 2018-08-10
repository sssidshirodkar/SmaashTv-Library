package com.smaaash.tv.apis;

import com.smaaash.tv.apis.base.AnyError;
import com.smaaash.tv.apis.base.ApiBuilder;
import com.smaaash.tv.apis.base.ApiService;
import com.smaaash.tv.apis.base.Error;
import com.smaaash.tv.apis.base.RestClient;
import com.smaaash.tv.background.Callback;
import com.smaaash.tv.background.Interactor;
import com.smaaash.tv.utils.Utils;

import java.io.IOException;

import models.EpisodeDetailResponse;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Siddhesh on 18-07-2018.
 */

public class EpisodeInteractor implements Interactor {

    private Callback callback;
    private String channelId, userId;

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        try {
            RestClient restClient = new RestClient(ApiService.class);
            ApiService service = (ApiService) restClient.getApiService();

            Call<EpisodeDetailResponse> call = service.getEpisodeDetails(ApiBuilder.getEpisodeDetail(channelId, userId, Utils.getCurrentISOTime()));
            Response<EpisodeDetailResponse> response = call.execute();

            if (response != null && response.isSuccessful()) {
                onSuccess(response.body());
            } else {
                onError(new AnyError("ERROR", 0));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess(Object object) {
        callback.onSuccess(object);
    }

    @Override
    public void onError(Error object) {
        callback.onError(object);
    }
}
