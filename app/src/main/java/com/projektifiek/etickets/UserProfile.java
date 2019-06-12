package com.projektifiek.etickets;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UserProfile extends AppCompatActivity {

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
    }
}
