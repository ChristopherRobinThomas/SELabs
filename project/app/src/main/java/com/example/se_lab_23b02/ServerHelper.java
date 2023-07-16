package com.example.se_lab_23b02;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Debug;
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
import java.nio.file.Files;

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
    private final String loginUser = "development/useradministration/loginuser";
    private final String createNewUser = "development/useradministration/createnewuser";
    private final String deleteAllData = "development/useradministration/deletealluserdata";
    private final String synchronizeQuestionnaires = "development/databaseentrys/questionaire/allquestionnaires";
    private final String synchronizeDigitSpanTasks = "development/databaseentrys/digitspantask/alldigitspantasks";
    private final String synchronizeDailyGoals = "development/databaseentrys/dailygoal/alldailygoals";
    private final String synchronizeSensorreadings = "development/databaseentrys/sensorrecordings/allsensorrecordings";



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

        Call<ResponseBody> call = httpClient.postRequest(addQuestionnaireURL, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

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

    public void synchronize (Context context) throws JSONException{
        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        Log.i("sHelper", "Started Synchronisation of all Data");
        synchronizeQuestionnaire(context);
        synchronizeDigitSpanTask(context);
        synchronizeDailyGoals(context);
        synchronizeSensorreadings(context);
    }

    public void synchronizeQuestionnaire(Context context) throws JSONException {
        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = JSONObjectCreator.createAllQuestionnaireObject(context);
        Call<ResponseBody> call = httpClient.postRequest(synchronizeQuestionnaires, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    JSONArray jarray;
                    try {
                        jarray = new JSONArray(response.body().string());
                        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
                        dsHelper.deleteFileFromInternalStorage("questionnaire.txt", context);
                        for (int i = 0; i < jarray.length(); i++){
                            JSONObject questionnaire = new JSONObject(jarray.get(i).toString().replaceAll(" ", ""));
                            JSONObject tempLeerzeichenData = new JSONObject(jarray.get(i).toString());
                            String timestamp = tempLeerzeichenData.getString("timestamp");
                            JSONArray q4 = tempLeerzeichenData.getJSONArray("Q4");
                            JSONArray q5 = tempLeerzeichenData.getJSONArray("Q5");
                            JSONArray q9 = tempLeerzeichenData.getJSONArray("Q9");
                            timestamp = timestamp.replace(".000000", "");
                            String user_id = questionnaire.getString("user_id");
                            questionnaire.remove("user_id");
                            questionnaire.put("timestamp", timestamp);
                            questionnaire.put("Q4", q4);
                            questionnaire.put("Q5", q5);
                            questionnaire.put("Q9", q9);
                            questionnaire.put("user_id", user_id);
                            dsHelper.writeDataToInternalStorage("questionnaire.txt", questionnaire +"\n", context, true);
                        }


                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i("sHelper", "Successfully synchronized Questionnaire");
                } else {
                    Log.e("sHelper", "Failed to synchronize Questionnaire, Response: " + response);
                    Toast.makeText(activity, "Synchronization failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - Q, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void synchronizeDigitSpanTask(Context context) throws JSONException {
        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = JSONObjectCreator.createAllDigitSpanTaskObject(context);
        Call<ResponseBody> call = httpClient.postRequest(synchronizeDigitSpanTasks, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    JSONArray jarray;
                    try {
                        jarray = new JSONArray(response.body().string());
                        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
                        dsHelper.deleteFileFromInternalStorage("DigitSpanTask.txt", context);
                        for (int i = 0; i < jarray.length(); i++){
                            JSONObject digitSpanTask = new JSONObject(jarray.get(i).toString());
                            int initSpanLengtToday = digitSpanTask.getInt("InitSpanLengtToday");
                            int initSpanLengthTomorrow = digitSpanTask.getInt("InitSpanLengthTomorrow");
                            int maxKnownNumbers = digitSpanTask.getInt("MaxKnownNumbers");
                            int numberOfSuccessfulRounds = digitSpanTask.getInt("numberOfSuccessfulRounds");
                            String timestamp = digitSpanTask.getString("timestamp");
                            timestamp = timestamp.replace(".000000", "");
                            String user_id = digitSpanTask.getString("user_id");

                            JSONObject newEntry = new JSONObject();
                            newEntry.put("InitSpanLengtToday", initSpanLengtToday);
                            newEntry.put("InitSpanLengthTomorrow", initSpanLengthTomorrow);
                            newEntry.put("MaxKnownNumbers", maxKnownNumbers);
                            newEntry.put("numberOfSuccessfulRounds", numberOfSuccessfulRounds);
                            newEntry.put("timestamp", timestamp);
                            newEntry.put("user_id", user_id);

                            dsHelper.writeDataToInternalStorage("DigitSpanTask.txt", newEntry +"\n", context, true);
                        }


                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i("sHelper", "Successfully synchronized DigitSpanTask");
                } else {
                    Log.e("sHelper", "Failed to synchronize DigitSpanTask, Response: " + response);
                    Toast.makeText(activity, "Synchronization failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - Q, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void synchronizeDailyGoals(Context context) throws JSONException {
        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = JSONObjectCreator.createAllDailyGoalObject(context);
        Call<ResponseBody> call = httpClient.postRequest(synchronizeDailyGoals, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    JSONArray jarray;
                    try {
                        jarray = new JSONArray(response.body().string());
                        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
                        dsHelper.deleteFileFromInternalStorage("DailyGoal.txt", context);
                        for (int i = 0; i < jarray.length(); i++){
                            JSONObject dailyTasks = new JSONObject(jarray.get(i).toString());
                            JSONObject newEntry = new JSONObject();
                            newEntry.put("Date", dailyTasks.get("Date").toString().replace(".000000", ""));
                            newEntry.put("Daily_Task", dailyTasks.get("Daily_Task"));
                            newEntry.put("progress", dailyTasks.get("progress"));
                            newEntry.put("user_id", dailyTasks.get("user_id"));
                            dsHelper.writeDataToInternalStorage("DailyGoal.txt", newEntry + "\n", context, true);
                        }
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i("sHelper", "Successfully synchronized DailyGoal");
                } else {
                    Log.e("sHelper", "Failed to synchronize DailyGoal, Response: " + response);
                    Toast.makeText(activity, "Synchronization failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - Q, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void synchronizeSensorreadings (Context context) throws  JSONException {
        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = JSONObjectCreator.createAllSensorreadingsObject(context);
        Call<ResponseBody> call = httpClient.postRequest(synchronizeSensorreadings, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    JSONArray jarray;
                    try {
                        assert response.body() != null;
                        jarray = new JSONArray(response.body().string());
                        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
                        dsHelper.deleteFileFromInternalStorage("sensorrecordings.txt", context);
                        for (int i = 0; i < jarray.length(); i++){
                            JSONObject sensordata = new JSONObject(jarray.get(i).toString());
                            JSONObject newEntry = new JSONObject();
                            newEntry.put("timestamp", sensordata.get("timestamp").toString().replace(".000000", ""));

                            String acceleration = sensordata.get("acceleration").toString();
                            acceleration.substring(1, acceleration.length()-1);
                            JSONArray accA = new JSONArray(acceleration);
                            newEntry.put("acceleration", accA);

                            String stepcounter =  sensordata.get("stepcounter").toString();
                            stepcounter.substring(1, stepcounter.length()-1);
                            JSONArray accS = new JSONArray(stepcounter);
                            newEntry.put("stepcounter", accS);

                            String distance = sensordata.get("distance").toString();
                            System.out.println(distance);
                            distance.substring(1, distance.length()-1);

                            JSONArray accD = new JSONArray(distance);
                            newEntry.put("distance", accD);

                            newEntry.put("user_id", sensordata.get("user_id"));
                            dsHelper.writeDataToInternalStorage("sensorrecordings.txt", newEntry + "\n", context, true);
                        }
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i("sHelper", "Successfully synchronized Sensordata");
                } else {
                    Log.e("sHelper", "Failed to synchronize Sensordata, Response: " + response);
                    Toast.makeText(activity, "Synchronization failed", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - Sensordata, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });
    }



    /**
     * Adds an DigitSpanTask to the RDS Database using retrofit Interface
     *
     * @param toSave  the JSON Object which will be send to the Server. Is created inside the DigitSpanTaskActivity
     * @param context needs Context to get UUID from Shared Preferences
     */
    public void addDigitSpanTask(JSONObject toSave, Context context) throws JSONException {
        JSONObject body = JSONObjectCreator.addInformationToJSONObject(toSave, context);
        Call<ResponseBody> call = httpClient.postRequest(addDigitSpanTaskURL, body);

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
                    try {
                        dsHelper.saveSettingAsSharedPref("user_id", readResponseObject(response), context);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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

    /**
     * Creates Call with new User Information in HTTP Post Body and saves the response (which should be an UUID) to the Shared Preferences
     *
     * @param context         needs Context to write UUID to Shared Preferences
     * @param failedClass     Class to which should be switched if the login failed
     * @param successfulClass Class to which should be switched if the login is successful
     * @param onlyNewUser     Class to which should be switched if the User which is created is new
     */
    public void createNewUser(User user, Context context, Class failedClass, Class successfulClass, Class onlyNewUser) throws JSONException {

        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection available", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = JSONObjectCreator.createNewUserObject(user.getName(), user.getPassword(), user.getPassword());
        Call<ResponseBody> call = httpClient.postRequest(createNewUser, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        String array = response.body().string();
                        JSONArray jarray = new JSONArray(array);
                        String result = jarray.getString(0);

                    switch (result) {
                        case "-1":
                            Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
                            Log.e("sHelper", "Tried to create User - failed: No UUID could be created. SQL Error:" + jarray.get(1));
                            context.startActivity(new Intent(context, failedClass));
                            break;
                        case "-3":
                            Toast.makeText(activity, "Username and password do not match or you do not have an account yet" , Toast.LENGTH_LONG).show();
                            Log.e("sHelper", "Tried to create User - failed: Username already in use or Username and Password are not matching");
                            context.startActivity(new Intent(context, failedClass));
                            break;
                        default:
                            Toast.makeText(activity, "Successfully logged in User", Toast.LENGTH_LONG).show();
                            Log.i("sHelper", "Successfully created new User");
                            DataSavingHelper ds = DataSavingHelper.getInstance();
                            String res = result;
                            user.setGlobalId(res);
                            switch ((int)user.getLocalId())
                            {
                                case 1:
                                    ds.saveSettingAsSharedPref("UserOne", user.toString(),context);
                                    break;
                                case 2:
                                    ds.saveSettingAsSharedPref("UserTwo", user.toString(),context);
                                    break;
                                case 3:
                                    ds.saveSettingAsSharedPref("UserThree", user.toString(),context);
                                    break;
                                case 4:
                                    ds.saveSettingAsSharedPref("UserFour", user.toString(),context);
                                    break;
                                default: break;
                            }
                            ds.saveSettingAsSharedPref("ActiveUser", user.toString(),context);
                            TimeStamp.setStandardTimers(context);

                            if (jarray.getString(1).equals("login")) {
                                context.startActivity(new Intent(context, successfulClass));
                            }else if (jarray.getString(1).equals("create")){
                                context.startActivity(new Intent(context, onlyNewUser));
                            }
                    }
                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e("sHelper", "Failed to create new User, Response: " + response);
                    context.startActivity(new Intent(context, failedClass));
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - createNewUser, Throwable: " + t);
                context.startActivity(new Intent(context, failedClass));
            }
        });
    }

    /**
     * Creates Call with loginInformation in HTTP Post Body and saves the response (which should be an UUID) to the Shared Preferences
     *
     * @param context         needs Context to write UUID to Shared Preferences
     * @param failedClass     Class to which should be switched if the login failed
     * @param successfulClass Class to which should be switched if the login is successful
     */
    public void loginUser(User user, Context context, Class failedClass, Class successfulClass) throws JSONException {

        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        DataSavingHelper dsHelper = DataSavingHelper.getInstance();
        JSONObject body = JSONObjectCreator.createLoginUserObject(user.getName(), user.getPassword());
        Call<ResponseBody> call = httpClient.postRequest(loginUser, body);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {

                    String result;
                    try {
                        result = readResponseObject(response);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if ("-1".equals(result)) {
                        Log.i("sHelper", "Login failed");
                        Toast.makeText(activity, "Login failed, please try again", Toast.LENGTH_LONG).show();
                    } else {
                        Log.i("sHelper", "Successfully logged User in");
                        Toast.makeText(activity, "Login successful", Toast.LENGTH_LONG).show();
                        try {
                            user.setGlobalId(readResponseObject(response));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        context.startActivity(new Intent(context, successfulClass));
                    }
                } else {
                    Log.i("sHelper", "Failed to login User, Response: " + response);
                    Toast.makeText(activity, "Server Authentication Error", Toast.LENGTH_LONG).show();
                    context.startActivity(new Intent(context, failedClass));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - loginUser, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();
                context.startActivity(new Intent(context, failedClass));
            }
        });
    }

    public void deleteEverything(Context context) throws JSONException {
        if (checkInternetAvailability(context) < 0) {
            Log.i("sHelper", "Checked for Internet, no Internet available");
            Toast.makeText(activity, "No Internet connection", Toast.LENGTH_LONG).show();
            return;
        }

        JSONObject body = JSONObjectCreator.createDeleteObject(context);
        Call<ResponseBody> call = httpClient.postRequest(deleteAllData, body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.i("sHelper", "Successfully deleted all Data");
                    Toast.makeText(activity, "Successfully deleted all Data online", Toast.LENGTH_LONG).show();

                } else {
                    Log.i("sHelper", "Failed to delete all Data, Response: " + response);
                    Toast.makeText(activity, "Failed to get delete all Data", Toast.LENGTH_LONG).show();

                }
            }
            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                Log.e("sHelper", "Failed to execute Call - deleteAllData, Throwable: " + t);
                Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show();

            }
        });
    }

    //set Activity to display Toast, can be removed in the final delivery
    public void setActivity(FragmentActivity activity) {
        this.activity = activity;
    }


    //reads UniqueID fromResponseBodyObject
    private String readResponseObject(Response<ResponseBody> response) throws IOException {
        assert response.body() != null;
        return response.body().string();
    }
}