package com.projektifiek.etickets;

import android.content.Intent;
import android.os.Handler;
import androidx.core.app.NotificationCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class OpeningScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opening_screen);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Executed after timer is finished (Opens MainActivity)
                Intent intent = new Intent(OpeningScreen.this, LoginActivity.class);
                Animation mLoadAnimation = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                mLoadAnimation.setDuration(3000);
                startActivity(intent);

                // Kills this Activity
                finish();

            }
        }, SPLASH_SCREEN_DELAY);

    }
}
