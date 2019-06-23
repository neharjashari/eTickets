package com.projektifiek.etickets;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    TextView dateCreatedTextView;
    TextView authorTextView;
    TextView priceTextView;
    TextView contentTextView;
    Button btnBuyTicket;
    ImageView photoImageView;

    String ticketId = "";
    String title = "";
    String author = "";
    String dateCreated = "";
    String content = "";
    String price = "";

    public String usersToken = "";
    public boolean isCreatedByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        titleTextView = (TextView) findViewById(R.id.titleTextView);
        dateCreatedTextView = (TextView) findViewById(R.id.dateCreatedTextView);
        authorTextView = (TextView) findViewById(R.id.authorTextView);
        priceTextView = (TextView) findViewById(R.id.priceTextView);
        contentTextView = (TextView) findViewById(R.id.contentTextView);
        btnBuyTicket = (Button) findViewById(R.id.btnBuyTicket);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        ticketId = intentGetToken.getStringExtra("id");
        isCreatedByUser = intentGetToken.getBooleanExtra("isCreatedByUser",false);
//        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Ticket ID: " + ticketId, Toast.LENGTH_SHORT).show();


        // If the event is created by the current user then set the BuyTicket button to Invisible
        if (isCreatedByUser == true) {
            btnBuyTicket.setVisibility(View.INVISIBLE);
            photoImageView.setImageResource(R.drawable.eventicon9);
        }

        Request request = new
                Request.Builder()
                .url("http://192.168.179.1:8000/event/" + usersToken + "/" + ticketId)
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

                        ticketId = jsonObject.getString("id");
                        title = jsonObject.getString("title");
                        author = jsonObject.getString("author");
                        dateCreated = jsonObject.getString("date_created");
                        content = jsonObject.getString("content");
                        price = jsonObject.getString("price");

                        Log.w("JSON: ", "The JSON values saved.");
                        Log.w("JSON: ", "Values: { " + title + ", " + author + ", " + dateCreated + ", " + content + ", " + price + " }");


                        // You have to move the portion of the background task that updates the UI onto the main thread.
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // Stuff that updates the UI (the variables are set as final, it doesn't work in any other way)
                                titleTextView.setText(title);
                                dateCreatedTextView.setText("Date Created: " + dateCreated);
                                authorTextView.setText("Author: " + author);
                                priceTextView.setText("Price: " + price + " €");
                                contentTextView.setText(content);
                            }
                        });

                        Log.w("JSON: ", "Values set to TextViews.");

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
        intent.putExtra("id", "1");
        intent.putExtra("title", title);
        intent.putExtra("author", author);
        intent.putExtra("date_created", dateCreated);
        intent.putExtra("content", content);
        intent.putExtra("price", price);
        startActivity(intent);
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
        Toast.makeText(this, "Selected Item: " +item.getTitle(), Toast.LENGTH_SHORT).show();
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
