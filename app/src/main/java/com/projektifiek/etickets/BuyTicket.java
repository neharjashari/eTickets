package com.projektifiek.etickets;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.cardform.view.CardForm;

public class BuyTicket extends AppCompatActivity {

    public String usersToken = "";
    public String boughtTicketId = "";
    public String boughtTicketTitle = "";
    public String boughtTicketAuthor = "";
    public String boughtTicketDateCreated = "";
    public String boughtTicketContent = "";
    public String boughtTicketPrice = "";

    CardForm cardForm;
    Button buy;
    AlertDialog.Builder alertBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_ticket);

        Intent intentGetToken = getIntent();
        usersToken = intentGetToken.getStringExtra("usersToken");
        boughtTicketId = intentGetToken.getStringExtra("id");
        boughtTicketTitle = intentGetToken.getStringExtra("title");
        boughtTicketAuthor = intentGetToken.getStringExtra("author");
        boughtTicketDateCreated = intentGetToken.getStringExtra("date_created");
        boughtTicketContent = intentGetToken.getStringExtra("content");
        boughtTicketPrice = intentGetToken.getStringExtra("price");
        Toast.makeText(this, "Users Token: " + usersToken, Toast.LENGTH_LONG).show();


        cardForm = findViewById(R.id.card_form);
        buy = findViewById(R.id.btnBuy);
        cardForm.cardRequired(true)
                .expirationRequired(true)
                .cvvRequired(true)
                .postalCodeRequired(true)
                .mobileNumberRequired(true)
                .mobileNumberExplanation("SMS is required on this number")
                .setup(BuyTicket.this);
        cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cardForm.isValid()) {
                    alertBuilder = new AlertDialog.Builder(BuyTicket.this);
                    alertBuilder.setTitle("Confirm before purchase");
                    alertBuilder.setMessage("Card number: " + cardForm.getCardNumber() + "\n" +
                            "Card expiry date: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                            "Card CVV: " + cardForm.getCvv() + "\n" +
                            "Postal code: " + cardForm.getPostalCode() + "\n" +
                            "Phone number: " + cardForm.getMobileNumber());
                    alertBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            Toast.makeText(BuyTicket.this, "Thank you for purchase", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(getApplicationContext(), QRCode.class);
                            intent.putExtra("usersToken", usersToken);
                            intent.putExtra("id", "1");
                            intent.putExtra("title", boughtTicketTitle);
                            intent.putExtra("author", boughtTicketAuthor);
                            intent.putExtra("date_created", boughtTicketDateCreated);
                            intent.putExtra("content", boughtTicketContent);
                            intent.putExtra("price", boughtTicketPrice);
                            startActivity(intent);
                        }
                    });
                    alertBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog = alertBuilder.create();
                    alertDialog.show();

                    // TODO: NOTIFICATIONS
                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(BuyTicket.this);
                    mBuilder.setSmallIcon(R.drawable.logo);
                    mBuilder.setContentTitle("Notification Alert - eTickets!");
                    mBuilder.setContentText("You have successfully purchased the ticket for this event.");
                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // notificationID allows you to update the notification later on.
                    mNotificationManager.notify(001, mBuilder.build());

                } else {
                    Toast.makeText(BuyTicket.this, "Please complete the form", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
