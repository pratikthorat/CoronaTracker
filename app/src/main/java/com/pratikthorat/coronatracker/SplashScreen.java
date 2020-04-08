package com.pratikthorat.coronatracker;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.pratikthorat.coronatracker.Util.AppStatus;

import static android.graphics.Typeface.createFromAsset;

public class SplashScreen extends Activity {
    public static final int ACCESS_NETWORK_STATE = 1797;
    private static final String TAG = "error";
    public static int flag = 0;
    private static int SPLASH_TIME_OUT = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        if (ActivityCompat.checkSelfPermission(SplashScreen.this, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            // Callback onRequestPermissionsResult interceptado na Activity MainActivity
            ActivityCompat.requestPermissions(SplashScreen.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    SplashScreen.ACCESS_NETWORK_STATE);
        } else {
            // permission has been granted, continue as usual
            flag = 1;

        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.WHITE);

        }
        TextView author = findViewById(R.id.author);
        Typeface robotothin = createFromAsset(getAssets(),
                "fonts/Roboto-Black.ttf"); //use this.getAssets if you are calling from an Activity
        author.setTypeface(robotothin);
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity

                if (AppStatus.getInstance(getApplicationContext()).isOnline()) {
                    try {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                        Intent i = new Intent(SplashScreen.this, Login.class);
                        i.putExtra("username", pref.getString("username", null));
                        finish();
                        startActivity(i);
                    } catch (Exception ex) {
                        Toast.makeText(getApplicationContext(), "Call Pratik Thorat and tell him this error: " + ex.toString(), Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {

                    //    Toast.makeText(getApplicationContext(),"No Internet Connection Available!!!!",Toast.LENGTH_SHORT).show();
                    try {
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashScreen.this);
                        if (pref.getString("fullname", null) == null) {
                            builder.setTitle("Hello, SuperHero!");
                        } else {
                            String text = pref.getString("fullname", null);
                            if (text.indexOf(' ') > -1) { // Check if there is more than one word.
                                builder.setTitle("Hello, " + text.substring(0, text.indexOf(' ')) + " !"); // Extract first word.
                            } else {
                                builder.setTitle("Hello, " + text + " !"); // Text is the first word itself.
                            }
                        }
                        builder.setMessage("I am unable to connect with Sangamner Nagarparishad server, Please cross check your internet connectivity and try again! Closing....")
                                .setCancelable(false)
                                .setNegativeButton("CLOSE",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                SplashScreen.this.finish();
                                            }
                                        }
                                )
                                /* .setPositiveButton("goto settings",
                                         new DialogInterface.OnClickListener() {
                                             public void onClick(DialogInterface dialog, int id) {
                                                 Intent i = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
                                                 SplashScreen.this.finish();
                                                 startActivity(i);
                                             }
                                         }
                                         )*/
                                .setIcon(android.R.drawable.ic_dialog_alert);
                        AlertDialog alert = builder.create();
                        alert.show();
                    } catch (Exception e) {
                        Log.d(TAG, "Show Dialog: " + e.getMessage());
                    }
                    Log.v("Home", "############################You are not online!!!!");
                }
                // close this activity
                // finish();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ACCESS_NETWORK_STATE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    flag = 1;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                    Toast.makeText(getApplicationContext(), "Permission denied by user", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(SplashScreen.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
