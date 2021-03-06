package com.projektifiek.etickets;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class SignUp extends AppCompatActivity {

    private static final String CHANNEL_ID = "001";
    private Database database;
    private SQLiteDatabase usersDB;

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

        database = new Database(SignUp.this, "usersDB", null, 1);
        Log.d("DB", database.getDatabaseName());

        usersDB = database.getWritableDatabase();
    }


    public void openSignIn(View view) {
        Intent intent = new Intent(SignUp.this, LoginActivity.class);
        startActivity(intent);
    }


    // Save account and user's info in the DB
    public void createAccount(View view) {
        // Get the contact name and email entered
        String fullname = fullnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String token = createToken();

        if (!validate()) {
            Toast.makeText(getBaseContext(), "Login failed. Please edit the fields correctly", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            // Generate HASH
            String generatedSecuredPasswordHash = generateStrongPasswordHash(password);

            ContentValues cv = new ContentValues();
            cv.put("fullname", fullname);
            cv.put("email", email);
            cv.put("password", generatedSecuredPasswordHash);
            cv.put("token", token);

            Log.d("DB", generatedSecuredPasswordHash);

            long id = usersDB.insert("users", null, cv);

//            Toast.makeText(this, "Account ID: " + String.valueOf(id), Toast.LENGTH_LONG).show();

            // Send notification
            notification();

            Intent intentLogin = new Intent(SignUp.this, LoginActivity.class);
            startActivity(intentLogin);

        } catch (SQLiteConstraintException e) {
            Toast.makeText(this, "You already have an accout with this email.", Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Toast.makeText(SignUp.this, "Error: Creating Account.", Toast.LENGTH_SHORT).show();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            Toast.makeText(SignUp.this, "Error: Creating Account.", Toast.LENGTH_SHORT).show();
        }

    }


    // The function to send notifications from the app, in this case to notify the user that their account has been created.
    public void notification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Your account has been created")
                .setContentText("Your account for eTickets application has been created. Now you can enter the app.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your account for eTickets application has been created. Now you can enter the app."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 0;
        notificationManager.notify(notificationId, builder.build());
    }


    public void readDataFromDB(View view) {
        Cursor cursor = usersDB.query("users", null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Toast.makeText(SignUp.this, cursor.getString(cursor.getColumnIndex("fullname")), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onDestroy() {
        usersDB.close();
        super.onDestroy();
    }


    // This function generates unique tokens for every user, it uses the UUID from Java libraries
    public String createToken() {
        String uuid = UUID.randomUUID().toString();
//        uuid.replace("-", "");
        return uuid;
    }


    // Validate the data written from the user
    public boolean validate() {
        boolean valid = true;

        String name = fullnameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            fullnameEditText.setError("at least 3 characters");
            valid = false;
        } else {
            fullnameEditText.setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("enter a valid email address");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            passwordEditText.setError("at least 6 characters");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }

        return valid;
    }



    //  Advanced password security using PBKDF2WithHmacSHA1 algorithm
    // (https://howtodoinjava.com/security/how-to-generate-secure-password-hash-md5-sha-pbkdf2-bcrypt-examples/) //

    // HASH functions
    private static String generateStrongPasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0)
        {
            return String.format("%0"  +paddingLength + "d", 0) + hex;
        }else{
            return hex;
        }
    }

}
