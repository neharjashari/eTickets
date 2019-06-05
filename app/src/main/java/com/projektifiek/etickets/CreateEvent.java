package com.projektifiek.etickets;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

import okhttp3.OkHttpClient;

public class CreateEvent extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();

    EditText _title;
    EditText _author;
    EditText _content;
    EditText _photoUrl;
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
        _photoUrl = (EditText) findViewById(R.id.input_photoUrl);

        _tvResult = (TextView) findViewById(R.id.tvResult);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        URL = "http://192.168.179.1:8000/event/" + usersToken;

    }


    public void createEvent(View view) {
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
                _tvResult.setText(result);
            }
        }.execute();

        Intent intent = new Intent(CreateEvent.this, MainActivity.class);
        intent.putExtra("usersToken", usersToken);
        startActivity(intent);
    }


    private String HttpPost(String myUrl) throws IOException, JSONException {
        String result = "";

        URL url = new URL(myUrl);

        // 1. create HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        // 2. build JSON object
        JSONObject jsonObject = buidJsonObject();

        // 3. add JSON content to POST request body
        setPostRequestContent(conn, jsonObject);

        // 4. make POST request to the given URL
        conn.connect();

        // 5. return response message
        return conn.getResponseMessage()+"";

    }


    private JSONObject buidJsonObject() throws JSONException {

        JSONObject jsonObject = new JSONObject();
        jsonObject.accumulate("id", generateRandomNumber());
        jsonObject.accumulate("title", _title.getText().toString());
        jsonObject.accumulate("author",  _author.getText().toString());
        jsonObject.accumulate("date_created", getDateCreated());
        jsonObject.accumulate("content",  _content.getText().toString());
        jsonObject.accumulate("photo",  _photoUrl.getText().toString());

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

}


