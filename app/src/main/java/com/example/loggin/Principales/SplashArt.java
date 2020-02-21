package com.example.loggin.Principales;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.example.loggin.R;

public class SplashArt extends AppCompatActivity {

    private Handler mWaitHandler = new Handler();
    ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_art);

        image = (ImageView) findViewById(R.id.logo);
        rotarImagen(image);
        comprobarNoche();
        mWaitHandler.postDelayed(new Runnable() {

            @Override
            public void run() {

                //The following code will execute after the 5 seconds.

                try {
                    //Go to next page i.e, start the next activity.
                    Intent intent = new Intent(getApplicationContext(), Login.class);

                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                    //Let's Finish Splash Activity since we don't want to show this when user press back button.
                    finish();
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }, 4000);  // Give a 5 seconds delay.

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Remove all the callbacks otherwise navigation will execute even after activity is killed or closed.
        mWaitHandler.removeCallbacksAndMessages(null);
    }

    private void rotarImagen(View view) {
        RotateAnimation animation = new RotateAnimation(0, 360,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        animation.setDuration(2000);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.REVERSE);
        view.startAnimation(animation);
    }

    public boolean comprobarNoche(){
        boolean res;
        SharedPreferences modonoche = getSharedPreferences("noche", Context.MODE_PRIVATE);
        Boolean modo_actual= modonoche.getBoolean("noche",false);

        if (modo_actual==true){
            res=true;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            res=false;
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        return res;
    }



}
