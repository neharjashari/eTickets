package com.projektifiek.etickets;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class LoginActivity extends AppCompatActivity{

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    public String usersToken = "";

    EditText _emailText;
    EditText _passwordText;
    Button _loginButton;
    TextView _signupLink;

    private Database database;
    private SQLiteDatabase usersDB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        database = new Database(LoginActivity.this, "usersDB", null, 1);

        Log.w("myApp", "Created database: " + database.getDatabaseName());

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (InvalidKeySpecException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignUp.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }


    public void login() throws InvalidKeySpecException, NoSuchAlgorithmException {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        Log.w("myApp", "Valid data");

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();


        // Authentication logic here.
        String passwordDB = searchPassword(email);
        if (passwordDB == "not found") {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            //onLoginSuccess();
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
            return;
        }

        // Validate password using HASH function
        if(!validatePassword(password, passwordDB)) {
            new android.os.Handler().postDelayed(
                    new Runnable() {
                        public void run() {
                            // On complete call either onLoginSuccess or onLoginFailed
                            //onLoginSuccess();
                            onLoginFailed();
                            progressDialog.dismiss();
                        }
                    }, 3000);
            return;
        }

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onLoginSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();
                    }
                }, 3000);


        // Gets the logged user's usersToken
        usersToken = getUsersToken(email);

        Log.d("Users Token: ", usersToken);


        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("usersToken", usersToken);
        startActivity(intent);
    }


    public String searchPassword(String userEmail) {

        usersDB = database.getReadableDatabase();

        String query = "SELECT email, password FROM users";

        Cursor cursor = usersDB.rawQuery(query, null);

        String emailDB, response;
        response = "not found";

        if (cursor.moveToFirst()) {
            do {
                emailDB = cursor.getString(0);

                if (emailDB.equals(userEmail)) {
                    response = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }

        return response;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed. The user with that email or password doesn't exists!", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 6) {
            _passwordText.setError("at least 6 characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    public String getUsersToken(String userEmail) {

        usersDB = database.getReadableDatabase();

        String query = "SELECT email, token FROM users";

        Cursor cursor = usersDB.rawQuery(query, null);

        String emailDB, response;
        response = "not found";

        if (cursor.moveToFirst()) {
            do {
                emailDB = cursor.getString(0);

                if (emailDB.equals(userEmail)) {
                    response = cursor.getString(1);
                    break;
                }
            } while (cursor.moveToNext());
        }

        return response;
    }



    // HASH validation
    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException
    {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }



}