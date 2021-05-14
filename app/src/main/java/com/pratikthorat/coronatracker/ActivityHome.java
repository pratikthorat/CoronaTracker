package com.pratikthorat.coronatracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.pratikthorat.coronatracker.Util.WebViewUtility;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ActivityHome extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = ActivityHome.class.getSimpleName();
    private String isCustomNotification = "";

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);

        SharedPreferences prefGuest = getApplicationContext().getSharedPreferences("GuestDetails", MODE_PRIVATE);
        if (prefGuest.getString("androidId", null) == null) {
            final FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
            String token = "";
            if (instance != null) {
                token = instance.getToken();
            }
            if (!(token == null || token.isEmpty())) { //token available
                String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                sendPostRequest(unique_id, token);
            } else {
                Toast.makeText(getApplicationContext(), "Notification service unavailable!", Toast.LENGTH_LONG).show();
            }
        }
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.apply_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        // textviewTitle.setText("Hey SuperHero, " + userName + "!");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(true);
        //abar.setDisplayHomeAsUpEnabled(true);
        abar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_protective, R.id.nav_mask, R.id.nav_news, R.id.nav_district, R.id.nav_helpline,
                R.id.nav_notification, R.id.nav_login, R.id.nav_support)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        NavigationView navigationView2 = findViewById(R.id.nav_view);
        View headerView = navigationView2.getHeaderView(0);
        TextView userMobile = headerView.findViewById(R.id.mobileNo);


        isCustomNotification = getIntent().getStringExtra("isCustomNotification");
        String userName;
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
        userName = pref.getString("userName", null);
        if ("".equals(userName) || userName == null) {
            userName = "Covid Yodha";
        } else {
            //Hide login menu
            hideItem();
        }
        userMobile.setText(userName);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public String isCustomNotification() {
        return isCustomNotification;
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public void setCustomNotification(String flag) {
        isCustomNotification = flag;
    }

    public void hideItem() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView = findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_login).setVisible(false);
    }

    @Override
    public void onDestroy() {
        WebViewUtility.destroyDialogBox();
        super.onDestroy();
    }

    @Override
    public void onPause() {
        WebViewUtility.destroyDialogBox();
        super.onPause();
    }

    @Override
    public void onStop() {
        WebViewUtility.destroyDialogBox();
        super.onStop();
    }

    private void sendPostRequest(final String givenAndroidId, final String givenToken) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                Log.e(TAG, "deviceId: " + givenAndroidId);
                Log.e(TAG, "TOKEN: " + givenToken);
                String link = "";
                try {
                    link = "http://fightcovid.live/corvis/pages/signupUser";
                    String data = URLEncoder.encode("userToken", "UTF-8") + "=" + URLEncoder.encode(givenToken, "UTF-8");
                    data += "&" + URLEncoder.encode("deviceId", "UTF-8") + "=" + URLEncoder.encode(givenAndroidId, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    return "Exception: " + e.getMessage();
                }
            }

            protected void onPreExecute() {

                Log.i("thread", "Started...");
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                Log.e(TAG, result);
                //  Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                if (result.startsWith("1") || result.toLowerCase().contains("true")) {
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("GuestDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("androidId", givenAndroidId);
                    editor.putString("firebaseToken", givenToken);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Notifications are now enabled!", Toast.LENGTH_LONG).show();
                } else {
                    // Toast.makeText(getApplicationContext(), "Notifications disabled!" + result, Toast.LENGTH_LONG).show();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("GuestDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("androidId", givenAndroidId);
                    editor.putString("firebaseToken", givenToken);
                    editor.commit();
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenAndroidId, givenToken);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_support:
                Intent intent = new Intent(Intent.ACTION_SEND);
                String[] recipients = {"support@fightcovid.live"};//Add multiple recipients here
                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Feedback of fightCOVID application"); //Add Mail Subject
                intent.putExtra(Intent.EXTRA_TEXT, "Hi Team," +
                        "");//Add mail body
                //intent.putExtra(Intent.EXTRA_CC, "mailcc@gmail.com");//Add CC emailid's if any
                intent.putExtra(Intent.EXTRA_BCC, "pratik.thorat7@gmail.com");//Add BCC email id if any
                intent.setType("text/html");
                intent.setPackage("com.google.android.gm");//Added Gmail Package to forcefully open Gmail App
                startActivity(Intent.createChooser(intent, "Send mail"));
                finish();
                return true;
            default:
                //Toast.makeText(getApplicationContext(), "Something else CLicked!", Toast.LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
        }

    }

}
