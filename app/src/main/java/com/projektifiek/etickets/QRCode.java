package com.projektifiek.etickets;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;

public class QRCode extends AppCompatActivity {

    String TAG = "GenerateQRCode";
//    EditText edtValue;
    TextView edtValue;
    ImageView qrImage;
//    Button start, save;
    String inputValue;
    String savePath = Environment.getExternalStorageDirectory().getPath() + "/QRCode/";
    Bitmap bitmap;
    QRGEncoder qrgEncoder;

    TextView tvMessage;

    public String usersToken = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        edtValue = (TextView) findViewById(R.id.edt_value);

        tvMessage = (TextView) findViewById(R.id.tvMessage);

//        start = (Button) findViewById(R.id.start);
//        save = (Button) findViewById(R.id.save);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
//        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();

        generateQrCode();

        tvMessage.setVisibility(TextView.VISIBLE);

//        start.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                inputValue = edtValue.getText().toString().trim();
//                if (inputValue.length() > 0) {
//                    WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
//                    Display display = manager.getDefaultDisplay();
//                    Point point = new Point();
//                    display.getSize(point);
//                    int width = point.x;
//                    int height = point.y;
//                    int smallerDimension = width < height ? width : height;
//                    smallerDimension = smallerDimension * 3 / 4;
//
//                    qrgEncoder = new QRGEncoder(
//                            inputValue, null,
//                            QRGContents.Type.TEXT,
//                            smallerDimension);
//                    try {
//                        bitmap = qrgEncoder.encodeAsBitmap();
//                        qrImage.setImageBitmap(bitmap);
//                    } catch (WriterException e) {
//                        Log.v(TAG, e.toString());
//                    }
//                } else {
//                    edtValue.setError("Required");
//                }
//            }
//        });
//
//        save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                boolean save;
//                String result;
//                try {
//                    save = QRGSaver.save(savePath, edtValue.getText().toString().trim(), bitmap, QRGContents.ImageType.IMAGE_JPEG);
//                    result = save ? "Image Saved" : "Image Not Saved";
//                    Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

    }


    public void generateQrCode() {
        inputValue = edtValue.getText().toString().trim() + randomAlphaNumeric(5);
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


    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    public void openMainActivity(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("usersToken", usersToken);
        startActivity(intent);
    }
}
