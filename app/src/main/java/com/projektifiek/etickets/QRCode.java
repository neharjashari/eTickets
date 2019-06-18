package com.projektifiek.etickets;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Environment;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

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

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRCode extends AppCompatActivity {

    private static final String CHANNEL_ID = "001";
    String TAG = "GenerateQRCode";
    TextView edtValue;
    ImageView qrImage;
    String inputValue;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    TextView tvMessage;

    public String usersToken = "";
    public String boughtTicketId = "";
    public String boughtTicketTitle = "";
    public String boughtTicketAuthor = "";
    public String boughtTicketDateCreated = "";
    public String boughtTicketContent = "";
    public String boughtTicketPrice = "";


    String URL = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        edtValue = (TextView) findViewById(R.id.edt_value);

        tvMessage = (TextView) findViewById(R.id.tvMessage);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        boughtTicketId = intentGetToken.getStringExtra("id");
        boughtTicketTitle = intentGetToken.getStringExtra("title");
        boughtTicketAuthor = intentGetToken.getStringExtra("author");
        boughtTicketDateCreated = intentGetToken.getStringExtra("date_created");
        boughtTicketContent = intentGetToken.getStringExtra("content");
        boughtTicketPrice = intentGetToken.getStringExtra("price");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        edtValue.setText(boughtTicketTitle + boughtTicketId + boughtTicketDateCreated);

        generateQrCode();

        tvMessage.setVisibility(TextView.VISIBLE);

        notification();

        URL = "http://192.168.179.1:8000/event/" + usersToken + "/tickets";

    }


    public void notification() {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("The QR code for you event has been generated.")
                .setContentText("Use this QR code as a ticket for the event.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Use this QR code as a ticket for the event."))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        int notificationId = 0;
        notificationManager.notify(notificationId, builder.build());
    }


    public void generateQrCode() {
        inputValue = edtValue.getText().toString().trim();
        Toast.makeText(this, "QR code generated for: \"" + inputValue + "\"", Toast.LENGTH_LONG).show();

        if (inputValue.length() > 0) {
            WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
            Display display = manager.getDefaultDisplay();
            Point point = new Point();
            display.getSize(point);
            int width = point.x;
            int height = point.y;
            int smallerDimension = width < height ? width : height;
            smallerDimension = smallerDimension * 3 / 4;

            qrgEncoder = new QRGEncoder(
                    inputValue, null,
                    QRGContents.Type.TEXT,
                    smallerDimension);
            try {
                bitmap = qrgEncoder.encodeAsBitmap();
                qrImage.setImageBitmap(bitmap);
            } catch (WriterException e) {
                Log.v(TAG, e.toString());
            }
        } else {
            edtValue.setError("Required");
        }
    }


//    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
//    public static String randomAlphaNumeric(int count) {
//        StringBuilder builder = new StringBuilder();
//        while (count-- != 0) {
//            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
//            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
//        }
//        return builder.toString();
//    }

    public void openMainActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("usersToken", usersToken);
        startActivity(intent);
    }



    public void saveQrCode() {
        boolean save;
        String result;
        try {
            save = QRGSaver.save(savePath, edtValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
            result = save ? "Image Saved" : "Image Not Saved";
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    // Add to bought tickets in Back End

    public void addToBackEnd(View view) {
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

//                _tvResult.setText(result);
            }
        }.execute();

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
        jsonObject.accumulate("id", boughtTicketId);
        jsonObject.accumulate("title", boughtTicketTitle);
        jsonObject.accumulate("author", boughtTicketAuthor);
        jsonObject.accumulate("date_created", boughtTicketDateCreated);
        jsonObject.accumulate("content", boughtTicketContent);
        jsonObject.accumulate("price", boughtTicketPrice);

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
