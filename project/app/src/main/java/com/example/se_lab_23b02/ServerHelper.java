package com.example.se_lab_23b02;

import android.os.Environment;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHelper {

    private static ServerHelper INSTANCE;
    TestingClient testingClient;
    FragmentActivity activity;

    private String addQuestionaireURL = "snq4xkkw69.execute-api.eu-north-1.amazonaws.com/development/databaseentrys/questionaire";
    private String getInterventionSourceListURL = "snq4xkkw69.execute-api.eu-north-1.amazonaws.com/development/s3/getinterventionsourcelist";

    private ServerHelper() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //create Interface
        testingClient = retrofit.create(TestingClient.class);
    }
    public static ServerHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerHelper();
        }
        return INSTANCE;
    }

    public void addQuestionaire(JSONObject toSave){

        Call<Void> call = testingClient.postQuestionnaire(addQuestionaireURL ,toSave);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Success", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(activity, "Fail", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }







    public void getDataFromS3(){
        Retrofit retrofit2 = new Retrofit.Builder()
                .baseUrl("https://selabsapp2023.s3.amazonaws.com/Bulgarian%20Split%20Squads.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIATIJB6MIQNCVTXGNA%2F20230522%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Date=20230522T160343Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEJj%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCmV1LW5vcnRoLTEiRjBEAiA06RsYcoh9%2BubLWR6cc6oIA2wxEKQWCsYO7hTBnDQxdAIgc2vMplSXljAUVgCQbPjQyWktoHIdPWZfgxPAp4JxhY4qjAMIwf%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARAAGgwyMjM5NDYzNjc1MjAiDMaZvjjixEL0s%2FovPyrgAvn%2FfgPNpHjmpPs1a8gWCptwOcpSZMwqKfocGmhpZBrbdZGj3yVWNbWr5vTZkT8gNb6U0VGRQbSjnFztSnj5ss2wug3OXxwaK%2FgFUdiQhDBJE7nqLFol5xZCfHarOV1dds4%2BDaWwrMlvfZ7vz5gW%2BPXSyXvCiGGUN32Rxw3dLjl3BbmAhziaRMXdFfmX8yhSMADVJuoP9hjNm53BTxqTxqzZQFI6UxpYyz5A20xyVIoNh3HcS1EA8BYajWmH1sQTmRuT3gbyEjnUvyKCDRycyOu0VfqGqNxuB39QooqOg4%2FXUxwUzVMnQd2TVlEuBWsKsO%2B6sI4vVx5KCGO3q85HmqWgPe7Ya3nXgRSCGS%2Fs9SPFEzIAge%2BjA%2BuWW%2BA5lkAIlN2AK5W%2BgN7fdOE0RDaICy4i95DO6Snq92%2FFyvedZ8c2DoIiWhfYQhEiut4wNrc6GbXL90KyVPL5KFxHw%2BF3I4ow3aSuowY6nwFy5zuUa4tNu%2BHuFoTYzhEQVGKp6iuz4ySp4aQNu94DVK9HZlhpUM13r2MwuY1fzVj2E5ET1SvyyQWzibMuSqe9T8erh8KxCFnhOmxkqwlVQd18d7K6DnO9dNWHFsZW3q2zhfhgylFWnbc9NIrcFpovomn1EBoKx9ZWreqEX6GWft9He2x1wddcM5JG83vqgY4cccW099qISM65SY9%2BWGY%3D&X-Amz-Signature=d2478e3200b02dd91c07f1973a643b85f41ead2615fefde34aea2a18ea636406")
                .build();


        testingClient = retrofit2.create(TestingClient.class);

        String fileUrl = "";
        Call<ResponseBody> call = testingClient.downloadFile(fileUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.body());
                boolean success = writeResponseBodyToDisk(response.body());

                Toast.makeText(activity, "download was successful:" + success, Toast.LENGTH_LONG).show();


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(activity, "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "Bulgarian Split Squads.jpg"
            );

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;


                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}
