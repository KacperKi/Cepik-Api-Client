package com.rest;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitSingleton {
    private static Retrofit sTabletApiRetrofit = null;

    public static Retrofit getTabletApiClient() {
        if (sTabletApiRetrofit == null) {
            sTabletApiRetrofit = new Retrofit.Builder()
                    .baseUrl(CepikApi.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return sTabletApiRetrofit;
    }
}

