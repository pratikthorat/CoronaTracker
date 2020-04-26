package com.pratikthorat.coronatracker;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.graphics.Typeface.createFromAsset;

public class MainActivity extends AppCompatActivity {

    private final static int ALL_PERMISSIONS_RESULT = 101;
    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {

            switch (item.getItemId()) {
                case R.id.action_favorite:
                    Log.e("LISTACTIVITY", item.getTitle().toString());
                    finish();
                    Intent ii = new Intent("ListActivity");
                    startActivity(ii);

                    return true;

                default:
                    // If we got here, the user's action was not recognized.
                    // Invoke the superclass to handle it.
                    return super.onOptionsItemSelected(item);
            }
        } catch (Exception ex) {
            Log.e("TAG", ex.getMessage());
            return true;
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cameraswitch, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        TextView author = findViewById(R.id.author);
        Typeface robotothin = createFromAsset(getAssets(),
                "fonts/Roboto-Black.ttf"); //use this.getAssets if you are calling from an Activity
        author.setTypeface(robotothin);


        boolean backgroundLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (backgroundLocationPermissionApproved || Build.VERSION.SDK_INT < 29) {
            // App can access location both in the foreground and in the background.
            // Start your service that doesn't have a foreground service type
            // defined.
        } else {
            // App can only access location in the foreground. Display a dialog
            // warning the user that your app must have all-the-time access to
            // location in order to function properly. Then, request background
            // location.
            if (Build.VERSION.SDK_INT >= 29) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        22);
            }
        }


        String uid = "";
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.get("username") != null) {
                uid = extras.getString("username");
            } else if (extras.get("user") != null) {
                uid = extras.getString("user");
            }
            //The key argument here must match that used in the other activity
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
            String userName = pref.getString("userName", null);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.apply_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Hey SuperHero, " + userName + "!");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        //abar.setDisplayHomeAsUpEnabled(true);
        abar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 22: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Background Location Permission Denied. Please change app location permission to 'ALLOW ALL THE TIME' from Application settings to continue Login", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
