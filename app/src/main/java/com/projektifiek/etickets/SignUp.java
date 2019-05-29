    package com.projektifiek.etickets;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    SQLiteDatabase usersDB = null;

    Button btnSignUp;
    EditText fullnameEditText, emailEditText, passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        fullnameEditText = (EditText) findViewById(R.id.input_fullname);
        emailEditText = (EditText) findViewById(R.id.input_email);
        passwordEditText = (EditText) findViewById(R.id.input_password);
        btnSignUp = (Button) findViewById(R.id.btn_signup);

        createDatabase();
    }


    public void openSignIn(View view) {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }


    public void createAccount(View view) {
        // Get the contact name and email entered
        String fullname = fullnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String token = createToken();

        try {
            // Execute SQL statement to insert new data
            usersDB.execSQL("INSERT INTO users (fullname, email, password, token) VALUES ('" +
                    fullname + "', '" + email + "', '" + password + "', '" + token + "', unique ("+email+"));");

            Toast.makeText(this, "Account was created.", Toast.LENGTH_SHORT).show();

            Intent intentLogin = new Intent(SignUp.this, LoginActivity.class);
            startActivity(intentLogin);

        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "You already have an accout with this email.", Toast.LENGTH_LONG).show();
        }
    }


    public void createDatabase() {

        try {

            // Opens a current database or creates it
            // Pass the database name, designate that only this app can use it
            // and a DatabaseErrorHandler in the case of database corruption
            usersDB = this.openOrCreateDatabase("eticketsDB", MODE_PRIVATE, null);

            // Execute an SQL statement that isn't select
            usersDB.execSQL("CREATE TABLE IF NOT EXISTS users " +
                    "(id integer primary key, fullname VARCHAR, email VARCHAR, password VARCHAR, token VARCHAR);");

            // The database on the file system
            File database = getApplicationContext().getDatabasePath("eticketsDB.db");

            // Check if the database exists
            if (database.exists()) {
                Toast.makeText(this, "Database Available", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Database Missing", Toast.LENGTH_SHORT).show();
            }

        } catch(Exception e){

            Log.e("CONTACTS ERROR", "Error Creating Database");

        }

        // Make buttons clickable since the database was created
        btnSignUp.setClickable(true);

    }


    @Override
    protected void onDestroy() {
        usersDB.close();
        super.onDestroy();
    }


    // This function generates unique tokens for every user
    public String createToken() {
        String uuid = UUID.randomUUID().toString();
        uuid.replace("-", "");
        return uuid;
    }


}
