package com.projektifiek.etickets;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {

    ListView lvEvent;
    OkHttpClient client = new OkHttpClient();
    EventAdapter adapteri;

    public String usersToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
//        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        lvEvent = findViewById(R.id.lvEvent);
        adapteri = new EventAdapter(MainActivity.this);
        lvEvent.setAdapter(adapteri);

        lvEvent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                Toast.makeText(MainActivity.this, adapteri.data.get(position).getTitle(), Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                intent.putExtra("usersToken", usersToken);
                intent.putExtra("id", adapteri.data.get(position).getId());
                startActivity(intent);
            }
        });

        Request request = new
                Request.Builder()
                .url("http://192.168.179.1:8000/events")
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
                            String id = jsonObject.getString("id");
                            String title = jsonObject.getString("title");
                            String author = jsonObject.getString("author");
                            String dateCreated = jsonObject.getString("date_created");
                            String content = jsonObject.getString("content");
                            String price = jsonObject.getString("price");

                            Event objEvent = new Event(id,title,author,dateCreated,content,price);
                            adapteri.data.add(objEvent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
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
            case R.id.menu_exit_the_app:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
