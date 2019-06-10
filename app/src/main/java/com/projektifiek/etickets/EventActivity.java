package com.projektifiek.etickets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class EventActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient();

    TextView titleTextView;
    TextView dataCreatedTextView;
    TextView authorTextView;
    TextView priceTextView;
    TextView contentTextView;

    public String usersToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        dataCreatedTextView = (TextView) findViewById(R.id.dateCreatedTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        String id = "1";

        Request request = new
                Request.Builder()
                .url("http://192.168.179.1:8000/event/" + usersToken + "/" + id)
                .get()
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.i("exception", e.toString());
            }

            @Override
            public void onResponse(Call call, Response response)
                    throws IOException {
                if(response.isSuccessful())
                {
                    Log.w("JSON: ", "Successful json response.");

                    String strJsonResponse =
                            response.body().string();

                    Log.w("JSON: ", strJsonResponse);

                    try {

                        JSONObject jsonObject = new JSONObject(strJsonResponse);

                        int id = jsonObject.getInt("id");
                        String title = jsonObject.getString("title");
                        String author = jsonObject.getString("author");
                        String dateCreated = jsonObject.getString("date_created");
                        String content = jsonObject.getString("content");
                        String price = jsonObject.getString("price");
//
//                            price = price.substring(0,10);

//                            Event objEvent = new Event(id,title,author,dateCreated,content,price);
//                            adapteri.data.add(objEvent);

                        titleTextView.setText(title);
                        dataCreatedTextView.setText("Date Created: " + dateCreated);
                        authorTextView.setText("Author: " + author);
                        priceTextView.setText("Price: " + price + " €");
                        contentTextView.setText(content);

                        } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

    }

    public void openBuyTicketActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), BuyTicket.class);
        intent.putExtra("usersToken", usersToken);
        startActivity(intent);
    }


}
