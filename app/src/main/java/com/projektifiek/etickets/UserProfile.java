package com.projektifiek.etickets;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends AppCompatActivity {

    private static final String CHANNEL_ID = "001";
    private Database database;
    private SQLiteDatabase usersDB;

    public String usersToken = "";

    TextView _userFullname;
    TextView _userEmail;
    TextView _userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        _userFullname = (TextView) findViewById(R.id.user_fullname);
        _userEmail = (TextView) findViewById(R.id.user_email);
        _userToken = (TextView) findViewById(R.id.user_token);

        database = new Database(UserProfile.this, "usersDB", null, 1);

        Log.w("myApp", "Created database: " + database.getDatabaseName());

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        _userToken.setText(usersToken);

        getUserData(usersToken);
    }


    public void getUserData(String token) {

        usersDB = database.getReadableDatabase();

        String query = "SELECT fullname, email, token FROM users";

        Cursor cursor = usersDB.rawQuery(query, null);

        String fullnameDB = "", emailDB = "", tokenDB = "";

        if (cursor.moveToFirst()) {
            do {
                tokenDB = cursor.getString(2);

                if (tokenDB.equals(token)) {
                    fullnameDB = cursor.getString(0);
                    emailDB = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }

        _userFullname.setText(fullnameDB);
        _userEmail.setText(emailDB);
    }


    public void deleteUser(View view) {

        // TODO: delete user and logout from the app, go to LoginActivity



        // Notify user
        notification();
    }


    public void notification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Your account has been deleted.")
                .setContentText("Your account has been deleted. You cannot enter the app with that account.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your account has been deleted. You cannot enter the app with that account."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 0;
        notificationManager.notify(notificationId, builder.build());
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
