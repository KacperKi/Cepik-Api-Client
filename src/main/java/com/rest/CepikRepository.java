package com.rest;

import com.google.gson.JsonObject;
import org.json.JSONObject;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.List;

public class CepikRepository {

    private Retrofit mRetrofit;
    private CepikApi cepikApi;

    CepikRepository() {
        mRetrofit = RetrofitSingleton.getTabletApiClient();
        cepikApi = mRetrofit.create(CepikApi.class);
    }


    public JsonObject getPojazdy(String wojewodztwo, String dataod, String datado){
        try {
            Response<JsonObject> response= cepikApi.getAllPojazdy(wojewodztwo, dataod, datado).execute();
            if (response.isSuccessful()) {
                System.out.println("getAll()-success, code: " + response.code() + ", body: " + response.body());
                return response.body();
            } else {
                System.out.println("getAll()-error, code: " + response.errorBody().string());
            }
        } catch (IOException e) {
            System.out.println(e);

        }
        return null;
    }



}
