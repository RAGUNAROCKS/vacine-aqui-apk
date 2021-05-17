package com.example.vacineaqui.databaseNode;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RetrofitInterface {

    @POST("/login")
    Call<PostoDeVacina> executeLogin(@Body HashMap<String, String> map);

    @POST("/filometro")
    Call<Void> executeFilometro(@Body HashMap<String, String> map);

    @GET("/findall")
    Call<List<PostoDeVacina>> executeFindAll();

}
