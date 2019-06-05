package com.projektifiek.etickets;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserEvents extends AppCompatActivity {

    ListView lvUserEvents;
    OkHttpClient client = new OkHttpClient();
    EventAdapter adapteri;

    public String usersToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_events);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        lvUserEvents = findViewById(R.id.lvUserEvents);
        adapteri = new EventAdapter(UserEvents.this);
        lvUserEvents.setAdapter(adapteri);

        lvUserEvents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

        Request request = new
                Request.Builder()
                .url("http://192.168.179.1:8000/event/" + usersToken)
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
                        JSONArray objJsonArray =
                                new JSONArray(strJsonResponse);
                        for(int i=0;i<objJsonArray.length();i++)
                        {
                            JSONObject jsonObject =
                                    objJsonArray.getJSONObject(i);
                            int id = jsonObject.getInt("id");
                            String title = jsonObject.getString("title");
                            String author = jsonObject.getString("author");
                            String dateCreated = jsonObject.getString("date_created");
                            String content = jsonObject.getString("content");
                            String photoUrl = jsonObject.getString("photo");
//                            photot = photot.substring(0,10);

                            Event objEvent = new Event(id,title,author,dateCreated,content,photoUrl);
                            adapteri.data.add(objEvent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    finally {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapteri.notifyDataSetChanged();
                            }
                        });
                    }
                }
            }
        });

    }


}
