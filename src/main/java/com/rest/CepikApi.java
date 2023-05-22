package com.rest;

import com.google.gson.JsonObject;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface CepikApi {

    String BASE_URL = "https://api.cepik.gov.pl";

    @Headers({"Accept:application/json", "Content-Type:application/json"})
    @GET("/pojazdy?")
    Call<JsonObject> getAllPojazdy(@Query("wojewodztwo") String wojewodztwo, @Query("data-od") String dataod, @Query("data-do") String datado);

}
