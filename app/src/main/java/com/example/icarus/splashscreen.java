package com.example.icarus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class splashscreen extends AppCompatActivity {
    private Handler mWaitHandler = new Handler();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //The following code will execute after the 5 seconds.

                try {

                    //Go to next page i.e, start the next activity.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    //Let's Finish Splash Activity since we don't want to show this when user press back button.
                    finish();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 5000);  // Give a 5 seconds delay.
    }
}

/*
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}
*/
