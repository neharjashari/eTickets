package com.projektifiek.etickets;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.OkHttpClient;

public class CreateEvent extends AppCompatActivity {

    private static final String CHANNEL_ID = "001";
    OkHttpClient client = new OkHttpClient();

    EditText _title;
    EditText _author;
    EditText _content;
    EditText _price;
    TextView _tvResult;

    String usersToken = "";
    String URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        _title = (EditText) findViewById(R.id.input_title);
        _author = (EditText) findViewById(R.id.input_author);
        _content = (EditText) findViewById(R.id.input_content);
        _price = (EditText) findViewById(R.id.input_price);

        _tvResult = (TextView) findViewById(R.id.tvResult);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        URL = "http://192.168.179.1:8000/event/" + usersToken;

    }


    public void createEvent(View view) {

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Please write the input in the correct form!", Toast.LENGTH_LONG).show();
            return;
        }

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground (String...urls){
                // params comes from the execute() call: params[0] is the url.
                try {
                    try {
                        return HttpPost(URL);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return "Error!";
                    }
                } catch (IOException e) {
                    return "Unable to retrieve web page. URL may be invalid.";
                }
            }
            // onPostExecute displays the results of the AsyncTask.
            @Override
            protected void onPostExecute (String result){
//                _tvResult.setText(result);
                Intent intentAfterCreateEvent = new Intent(getApplicationContext(), MainActivity.class);
                intentAfterCreateEvent.putExtra("usersToken", usersToken);
                startActivity(intentAfterCreateEvent);

                notification();
            }
        }.execute();

    }


    public void notification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Your event has been created")
                .setContentText("Everyone can buy tickets for this event and you can see the info for it anytime in the Your Events page.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your event has been created in the app. You can see it anytime in the Your Events page."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 0;
        notificationManager.notify(notificationId, builder.build());
    }


    private String HttpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buildJsonObject();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";

    }


    private JSONObject buildJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("id", generateRandomNumber());
        jsonObject.accumulate("title", _title.getText().toString());
        jsonObject.accumulate("author",  _author.getText().toString());
        jsonObject.accumulate("date_created", getDateCreated());
        jsonObject.accumulate("content",  _content.getText().toString());
        jsonObject.accumulate("price",  _price.getText().toString());

        return jsonObject;
    }


    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i(CreateEvent.class.toString(), jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }


    public String getDateCreated() {

        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        Date date = new Date();
        String dateCreated = dateFormat.format(date);

        return dateCreated;
    }


    public String generateRandomNumber() {
        Random rand = new Random();

        // Obtain a number between [0 - 999].
        int n = rand.nextInt(1000);
        String randNum = String.valueOf(n);

        return randNum;
    }


    // Validate the data written from the user
    public boolean validate() {
        boolean valid = true;

        String titleInput = _title.getText().toString();
        String authorInput = _author.getText().toString();
        String contentInput = _content.getText().toString();

        if (titleInput.isEmpty() || titleInput.length() < 3) {
            _title.setError("at least 3 characters");
            valid = false;
        } else {
            _title.setError(null);
        }

        if (authorInput.isEmpty() || authorInput.length() < 3) {
            _author.setError("at least 3 characters");
            valid = false;
        } else {
            _author.setError(null);
        }

        if (contentInput.isEmpty() || contentInput.length() < 10) {
            _content.setError("at least 10 characters");
            valid = false;
        } else {
            _content.setError(null);
        }

        return valid;
    }



    /*MENU*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_event:
                Intent intentAddEvent = new Intent(getApplicationContext(), CreateEvent.class);
                intentAddEvent.putExtra("usersToken", usersToken);
                startActivity(intentAddEvent);
                return true;
            case R.id.menu_home:
                Intent intentOpenMainActivity = new Intent(getApplicationContext(), MainActivity.class);
                intentOpenMainActivity.putExtra("usersToken", usersToken);
                startActivity(intentOpenMainActivity);
                return true;
            case R.id.menu_user_profile:
                Intent intentOpenUserProfile = new Intent(getApplicationContext(), UserProfile.class);
                intentOpenUserProfile.putExtra("usersToken", usersToken);
                startActivity(intentOpenUserProfile);
                return true;
            case R.id.menu_events:
                Intent intentOpenUserEvents = new Intent(getApplicationContext(), UserEvents.class);
                intentOpenUserEvents.putExtra("usersToken", usersToken);
                startActivity(intentOpenUserEvents);
                return true;
            case R.id.menu_tickets:
                Intent intentOpenUserTickets = new Intent(getApplicationContext(), UserTickets.class);
                intentOpenUserTickets.putExtra("usersToken", usersToken);
                startActivity(intentOpenUserTickets);
                return true;
            case R.id.menu_settings:
//                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
////                intentSettings.putExtra("usersToken", usersToken);
//                startActivity(intentSettings);
                return true;
            case R.id.menu_exit_the_app:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}


