package com.example.se_lab_23b02;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerHelper {
    private static ServerHelper INSTANCE;
    private HTTPClient httpClient;
    private FragmentActivity activity;
    private final String addQuestionnaireURL = "development/databaseentrys/questionaire";
    private final String addDigitSpanTaskURL = "development/databaseentrys/digitspantask";
    private final String getUniqueID = "development/databaseentrys/getuniqueuserid";
    private final String getInterventionSourceListURL = "development/s3/getinterventionsourcelist";


    private ServerHelper() {
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://snq4xkkw69.execute-api.eu-north-1.amazonaws.com/").addConverterFactory(GsonConverterFactory.create()).build();

        //create Interface
        httpClient = retrofit.create(HTTPClient.class);
    }

    public static ServerHelper getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ServerHelper();
        }
        return INSTANCE;
    }

    /**
     * Checks if an Internet Connection is available
     *
     * @param context needs Context to get ConnectivityManager
     * @return 1 if ConnectionType is WIFI, 2 if ConnectionType is Mobile, -1 if no Connection is available
     */
    public static int checkInternetAvailability(Context context) {
        final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            final Network n = cm.getActiveNetwork();
            if (n != null) {
                final NetworkCapabilities nc = cm.getNetworkCapabilities(n);

                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return 1;
                if (nc.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return 2;
            }
        }
        return -1;
    }

    /**
     * Adds an Questionnaire to the RDS Database using the retrofit Interface
     *
     * @param toSave  JSONObject with all the Data for the Questionnaire
     * @param context needs Context to get user_id from SharedPreferences
     */
    public void addQuestionnaire(JSONObject toSave, Context context) throws JSONException {
        JSONObject body = JSONObjectCreator.addInformationToJSONObject(toSave, context); /* Adds Information only needed online*/

        Call<ResponseBody> call = httpClient.postQuestionnaire(addQuestionnaireURL, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    JSONObject responseBody;
                    try {
                        assert response.body() != null;
                        responseBody = new JSONObject(response.body().string());
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    System.out.println(responseBody);
                    System.out.println(responseBody.getClass());
                    try {
                        System.out.println(responseBody.getJSONObject("nameValuePairs"));
                        System.out.println(responseBody.getJSONObject("nameValuePairs").getJSONObject("Q1").getJSONArray("values").get(0));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }


                    //Wird immer nur ein Questionnaire Objekt gespeichert? Und wie realisieren wir, dass sich gemerkt wird, welches Objekt gespeichert wurde und welches noch hochgeladen werden muss.
                    //Problem, dass die Antwort nicht einfach zurückgegeben werden kann, da call.enqueue Asynchron ist.


                    Log.i("sHelper", "Questionnaire successfully added");
                    Toast.makeText(activity, "Successfully delivered Questionnaire", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("sHelper", "Failed to add Questionnaire, Response: " + response);
                    Toast.makeText(activity, "Failed to deliver Questionnaire", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - Q, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Adds an DigitSpanTask to the RDS Database using retrofit Interfache
     *
     * @param toSave  the JSON Object which will be send to the Server. Is created inside the DigitSpanTaskActivity
     * @param context needs Context to get UUID from Shared Preferences
     */
    public void addDigitSpanTask(JSONObject toSave, Context context) throws JSONException {
        JSONObject body = JSONObjectCreator.addInformationToJSONObject(toSave, context);
        Call<ResponseBody> call = httpClient.postDigitSpanTask(addDigitSpanTaskURL, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("sHelper", "DigitSpanTask successfully added");
                    Toast.makeText(activity, "Successfully delivered DSTask", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("sHelper", "Failed to add DigitSpanTask, Response:" + response);
                    Toast.makeText(activity, "Failed to deliver DSTask", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - DST, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });

    }

    /**
     * Asks for an UUID provided by RDS Database. UUID is 36 Chars long
     *
     * @param context needs Context to write UUID to Shared Preferences
     */
    public void getUniqueID(Context context) {
        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        Call<ResponseBody> call = httpClient.getUniqueID(getUniqueID);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    dsHelper.saveSettingAsSharedPref("user_id", readResponseObject(response), context);
                    Log.i("sHelper", "Successfully got UUID");
                    Toast.makeText(activity, "Successfully got UUID", Toast.LENGTH_LONG).show();

                } else {
                    Log.i("sHelper", "Failed to get UUID, Response: " + response);
                    Toast.makeText(activity, "Failed to get UUID", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - UUID, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void createNewUser(String userName, String password, String passwordConfirm, Context context) throws JSONException {
        JSONObjectCreator.createNewUserObject(userName, password, passwordConfirm);
    }

    public void loginUser(String userName, String password, Context context){}

    //set Activity to display Toast, can be removed in the final delivery
    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }

    //reads UniqueID fromResponseBodyObject
    private String readResponseObject(Response<ResponseBody> response) {

        JSONArray json;
        try {
            json = new JSONArray(response.body().string());
        } catch (JSONException | IOException e) {
            throw new RuntimeException(e);
        }

        try {
            System.out.println(json.get(0).toString());
            return json.get(0).toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    //not yet functional:
    public void getDataFromS3() {
        Retrofit retrofit2 = new Retrofit.Builder().baseUrl("https://selabsapp2023.s3.amazonaws.com/Bulgarian%20Split%20Squads.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=ASIATIJB6MIQNCVTXGNA%2F20230522%2Feu-north-1%2Fs3%2Faws4_request&X-Amz-Date=20230522T160343Z&X-Amz-Expires=3600&X-Amz-SignedHeaders=host&X-Amz-Security-Token=IQoJb3JpZ2luX2VjEJj%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEaCmV1LW5vcnRoLTEiRjBEAiA06RsYcoh9%2BubLWR6cc6oIA2wxEKQWCsYO7hTBnDQxdAIgc2vMplSXljAUVgCQbPjQyWktoHIdPWZfgxPAp4JxhY4qjAMIwf%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FARAAGgwyMjM5NDYzNjc1MjAiDMaZvjjixEL0s%2FovPyrgAvn%2FfgPNpHjmpPs1a8gWCptwOcpSZMwqKfocGmhpZBrbdZGj3yVWNbWr5vTZkT8gNb6U0VGRQbSjnFztSnj5ss2wug3OXxwaK%2FgFUdiQhDBJE7nqLFol5xZCfHarOV1dds4%2BDaWwrMlvfZ7vz5gW%2BPXSyXvCiGGUN32Rxw3dLjl3BbmAhziaRMXdFfmX8yhSMADVJuoP9hjNm53BTxqTxqzZQFI6UxpYyz5A20xyVIoNh3HcS1EA8BYajWmH1sQTmRuT3gbyEjnUvyKCDRycyOu0VfqGqNxuB39QooqOg4%2FXUxwUzVMnQd2TVlEuBWsKsO%2B6sI4vVx5KCGO3q85HmqWgPe7Ya3nXgRSCGS%2Fs9SPFEzIAge%2BjA%2BuWW%2BA5lkAIlN2AK5W%2BgN7fdOE0RDaICy4i95DO6Snq92%2FFyvedZ8c2DoIiWhfYQhEiut4wNrc6GbXL90KyVPL5KFxHw%2BF3I4ow3aSuowY6nwFy5zuUa4tNu%2BHuFoTYzhEQVGKp6iuz4ySp4aQNu94DVK9HZlhpUM13r2MwuY1fzVj2E5ET1SvyyQWzibMuSqe9T8erh8KxCFnhOmxkqwlVQd18d7K6DnO9dNWHFsZW3q2zhfhgylFWnbc9NIrcFpovomn1EBoKx9ZWreqEX6GWft9He2x1wddcM5JG83vqgY4cccW099qISM65SY9%2BWGY%3D&X-Amz-Signature=d2478e3200b02dd91c07f1973a643b85f41ead2615fefde34aea2a18ea636406").build();


        httpClient = retrofit2.create(HTTPClient.class);

        String fileUrl = "";
        Call<ResponseBody> call = httpClient.downloadFile(fileUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                System.out.println(response.body());
                assert response.body() != null;
                boolean success = writeResponseBodyToDisk(response.body());

                Toast.makeText(activity, "download was successful:" + success, Toast.LENGTH_LONG).show();


            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Toast.makeText(activity, "fail", Toast.LENGTH_LONG).show();
            }
        });
    }

    //needs to be replaced with DBSavingsHelperFunction. Ignore for now:
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Bulgarian Split Squads.jpg");

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
