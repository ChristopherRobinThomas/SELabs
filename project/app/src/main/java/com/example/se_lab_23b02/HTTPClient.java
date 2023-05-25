package com.example.se_lab_23b02;


import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface HTTPClient {

    @POST
    Call<ResponseBody> postQuestionnaire(@Url String postURL, @Body JSONObject questionnaire);

    @Streaming
    @GET
    Call<ResponseBody> downloadFile(@Url String fileUrl);

}
