package com.projektifiek.etickets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Environment;
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

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class TicketEvent extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_event);

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
//        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        edtValue.setText(boughtTicketTitle + boughtTicketId + boughtTicketDateCreated);

        generateQrCode();

        tvMessage.setVisibility(TextView.VISIBLE);
    }

    public void generateQrCode() {
        inputValue = edtValue.getText().toString().trim();

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
