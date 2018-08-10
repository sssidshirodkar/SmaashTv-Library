package com.smaaash.tv.apis.base;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Siddhesh on 18-07-2018.
 */

public class RestClient<T> {

    Class<T> apiService;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
    private static Gson gson = new GsonBuilder().create();
    private static Retrofit.Builder restBuilder = new Retrofit.Builder()
            .baseUrl(ApiBuilder.getBaseUrl())
            .addConverterFactory(GsonConverterFactory.create(gson));

    public RestClient(Class<T> apiService) {
        this.apiService = apiService;
    }

    public T getApiService() {
//        httpClient.addInterceptor(new Interceptor() {
//            @Override
//            public Response intercept(Interceptor.Chain chain) throws IOException {
//                Request original = chain.request();
//
//                Request.Builder requestBuilder = original.newBuilder()
//                        .header("Content-Type", "application/json")
//                        .method(original.method(), original.body());
//
//                Request request = requestBuilder.build();
//                return chain.proceed(request);
//            }
//        });

        OkHttpClient client = httpClient.build();
        Retrofit retrofit = restBuilder.client(client).build();
        return retrofit.create(apiService);
    }

    public T getApiService2(){
        Retrofit.Builder restBuilder = new Retrofit.Builder()
                .baseUrl("https://uol56erc77.execute-api.ap-south-1.amazonaws.com")
                .addConverterFactory(GsonConverterFactory.create(gson));

        OkHttpClient client = new OkHttpClient.Builder().build();
        Retrofit retrofit = restBuilder.client(client).build();
        return retrofit.create(apiService);
    }

}
