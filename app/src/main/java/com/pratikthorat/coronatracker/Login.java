package com.pratikthorat.coronatracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.graphics.Typeface.createFromAsset;

public class Login extends Activity implements LocationListener {
    private static final String TAG = Login.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS = 100;
    static boolean isGPSOn = false;
    EditText username_et;
    boolean isGPSEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    boolean boolean_permission;
    TextView loginMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);

        }
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        Bundle extras = getIntent().getExtras();
        setContentView(R.layout.login);
        loginMessage = findViewById(R.id.loginMessage);
        loginMessage.setVisibility(View.GONE);
        if (ActivityCompat.checkSelfPermission(Login.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }

        }
        if (Build.VERSION.SDK_INT >= 29) {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (!backgroundLocationPermissionApproved) {
                ActivityCompat.requestPermissions(this, new String[]{
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        23);
            }
        }
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
        if (pref.getString("userName", null) == null || pref.getString("firebaseToken", null) == null) {
            TextView tv = findViewById(R.id.tv1);
            Typeface roboto = createFromAsset(getAssets(),
                    "fonts/Roboto-Black.ttf"); //use this.getAssets if you are calling from an Activity
            tv.setTypeface(roboto);
            //TextView customer = (TextView) findViewById(R.id.customer);
            TextView author = findViewById(R.id.author);
            Typeface robotothin = createFromAsset(getAssets(),
                    "fonts/Roboto-Black.ttf"); //use this.getAssets if you are calling from an Activity
            author.setTypeface(robotothin);
        } else {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtra("user", pref.getString("userName", null));
            finish();
            startActivity(intent);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void loginaction(View view) {
        Context context = getApplicationContext();
        username_et = findViewById(R.id.username_et);
        loginMessage.setVisibility(View.GONE);
        String username = username_et.getText().toString().trim();

        if (ActivityCompat.checkSelfPermission(Login.this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(Login.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                ActivityCompat.requestPermissions(Login.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        } else {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (backgroundLocationPermissionApproved || Build.VERSION.SDK_INT < 29) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
                final FirebaseInstanceId instance = FirebaseInstanceId.getInstance();
                String token = "";
                if (instance != null) {
                    token = instance.getToken();
                }
                if (username.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please fill mobile number and then try to login!", Toast.LENGTH_LONG).show();

                } else if (token.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Notification token not received! Please try again later or contact developers", Toast.LENGTH_LONG).show();
                } else {
                    String lng, lat;

                    fn_getlocation();
                    SharedPreferences loc = getSharedPreferences("CoronaData", Context.MODE_PRIVATE);
        /*if (Looper.myLooper() == null) {
            Looper.prepare();
        }*/
                    if (loc.getString("lat", "").isEmpty()) {
                        lat = "";
                        lng = "";
                    } else {
                        lat = loc.getString("lat", "");
                        lng = loc.getString("lng", "");
                    }
                    fn_getlocation();

                    if (loc.getString("lat", "").isEmpty()) {
                        lat = "0";
                        lng = "0";
                    } else {
                        lat = loc.getString("lat", "");
                        lng = loc.getString("lng", "");
                    }
                    if (isMockLocation()) {
                        Toast.makeText(getApplicationContext(), "Please turn of Mock location to continue!", Toast.LENGTH_LONG).show();
                    } else if (lat == "0" || lng == "0") {
                        Toast.makeText(getApplicationContext(), "Please move to GPS available location to continue!", Toast.LENGTH_LONG).show();
                        fn_getlocation();
                    } else {
                        sendPostRequest(username, lat, lng, token);
                        Button btn = findViewById(R.id.login);
                        btn.setVisibility(View.GONE);
                        ImageView imgvw = findViewById(R.id.lbtn);
                        imgvw.setVisibility(View.VISIBLE);
                        int duration = Toast.LENGTH_SHORT;
                    }
                }
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
        }
    }

    boolean isMockLocation() {
        boolean isMock = false;
        if (android.os.Build.VERSION.SDK_INT >= 18) {
            if (location != null)
                isMock = location.isFromMockProvider();
        } else {
            isMock = !Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }
        return isMock;
    }

    private void sendPostRequest(final String givenUsername, final String givenLatitude, final String givenLongitude, final String givenToken) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];
                String paramLatitude = params[1];
                String paramLongitude = params[2];
                String paramToken = params[3];
                String unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                Log.e(TAG, "deviceId: " + unique_id);
                Log.e(TAG, "TOKEN: " + givenToken);
                String link = "";
                try {
                    link = "http://fightcovid.live/corvis/Pages/verifyUser";
                    String data = URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(paramUsername, "UTF-8");
                    data += "&" + URLEncoder.encode("userLatitude", "UTF-8") + "=" + URLEncoder.encode(paramLatitude, "UTF-8");
                    data += "&" + URLEncoder.encode("userLongitude", "UTF-8") + "=" + URLEncoder.encode(paramLongitude, "UTF-8");
                    data += "&" + URLEncoder.encode("userToken", "UTF-8") + "=" + URLEncoder.encode(paramToken, "UTF-8");
                    data += "&" + URLEncoder.encode("deviceId", "UTF-8") + "=" + URLEncoder.encode(unique_id, "UTF-8");

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
                if (result.startsWith("1")) {
                    //String[] ary = result.split("=");
                    ImageView imgvw = findViewById(R.id.lbtn);
                    imgvw.setVisibility(View.GONE);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("userName", givenUsername);
                    editor.putString("firebaseToken", givenToken);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtra("user", result);
                    startActivity(intent);
                    finish();
                } else if (result.equals("2")) {
                    Toast.makeText(getApplicationContext(), "User profile deactivated, Login Unsuccessful!", Toast.LENGTH_LONG).show();
                    username_et = findViewById(R.id.username_et);
                    username_et.setText("");
                    Button btn = findViewById(R.id.login);
                    btn.setVisibility(View.VISIBLE);
                    ImageView imgvw = findViewById(R.id.lbtn);
                    imgvw.setVisibility(View.GONE);
                    loginMessage.setVisibility(View.VISIBLE);
                    loginMessage.setText("User profile deactivated\nLogin Unsuccessful!");
                } else if (result.equals("3")) {
                    Toast.makeText(getApplicationContext(), "Mobile device Unauthorized! Please use registered mobile device", Toast.LENGTH_LONG).show();
                    username_et = findViewById(R.id.username_et);
                    username_et.setText("");
                    Button btn = findViewById(R.id.login);
                    btn.setVisibility(View.VISIBLE);
                    ImageView imgvw = findViewById(R.id.lbtn);
                    imgvw.setVisibility(View.GONE);
                    loginMessage.setVisibility(View.VISIBLE);
                    loginMessage.setText("Mobile device Unauthorized!\nPlease use registered mobile device");
                } else if (result.equals("0")) {
                    Toast.makeText(getApplicationContext(), "Invalid Moblie Number, Login Unsuccessful!", Toast.LENGTH_LONG).show();
                    username_et = findViewById(R.id.username_et);
                    username_et.setText("");
                    Button btn = findViewById(R.id.login);
                    btn.setVisibility(View.VISIBLE);
                    ImageView imgvw = findViewById(R.id.lbtn);
                    imgvw.setVisibility(View.GONE);
                    loginMessage.setVisibility(View.VISIBLE);
                    loginMessage.setText("Invalid Moblie Number\nLogin Unsuccessful!");
                } else {
                    Toast.makeText(getApplicationContext(), "Internet Connection Error, Please try again!", Toast.LENGTH_LONG).show();
                    username_et = findViewById(R.id.username_et);
                    username_et.setText("");
                    username_et.setHint("username/email");
                    Button btn = findViewById(R.id.login);
                    btn.setVisibility(View.VISIBLE);
                    ImageView imgvw = findViewById(R.id.lbtn);
                    imgvw.setVisibility(View.GONE);
                    loginMessage.setVisibility(View.VISIBLE);
                    loginMessage.setText("Server connection error\nPlease try again!");
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenUsername, givenLatitude, givenLongitude, givenToken);
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void fn_getlocation() {
        // Toast.makeText(SensorService.this,"in fn_getlocation",Toast.LENGTH_SHORT).show();
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!isGPSEnable) {
            //Toast.makeText(this,"nothing enabled",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Location access not present");
            sharedpreferences = getApplicationContext().getSharedPreferences("CoronaData", Context.MODE_PRIVATE); // 0 - for private mode
            editor = sharedpreferences.edit();
            editor.putString("lat", "0");
            editor.putString("lng", "0");
            editor.putString("from", "");
            editor.commit();
            isGPSOn = false;
        } else {
            if (isGPSEnable) {
                isGPSOn = true;
                //Toast.makeText(this,"gps enabled",Toast.LENGTH_SHORT).show();
                Log.e(TAG, "gps enabled");
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE); //setAccuracyは内部では、https://stackoverflow.com/a/17874592/1709287の用にHorizontalAccuracyの設定に変換されている。
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setAltitudeRequired(false);
                criteria.setSpeedRequired(true);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                //API level 9 and up
                criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
                criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);
                Log.e(TAG, "Permission granted");
                locationManager.requestLocationUpdates(0, 0, criteria, this, null);
                if (locationManager != null) {
                    Log.e(TAG, "location manager not null");
                    //Toast.makeText(this,"loc manager not null",Toast.LENGTH_SHORT).show();
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.e(TAG, "location not null");
                        //     Toast.makeText(this,"loc not null",Toast.LENGTH_SHORT).show();
                        Log.e("latitude", location.getLatitude() + "");
                        Log.e("longitude", location.getLongitude() + "");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        sharedpreferences = getApplicationContext().getSharedPreferences("CoronaData", Context.MODE_PRIVATE); // 0 - for private mode
                        editor = sharedpreferences.edit();
                        editor.putString("lat", String.valueOf(latitude));
                        editor.putString("lng", String.valueOf(longitude));
                        editor.putString("from", "gps");
                        editor.commit();
                        //       Toast.makeText(SensorService.this,"Location :"+"http://maps.google.com/?q="+sharedpreferences.getString("lat","")+",lng"+sharedpreferences.getString("lng",""),Toast.LENGTH_SHORT).show();

                    } else {
                        //   Toast.makeText(this,"loc null gps",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(Login.this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Location Permission Denied. Please enable location permission from Application settings to continue Login", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 23: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(Login.this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //  Toast.makeText(this, "Background location access Permission Denied. Please change app location permission to 'ALLOW ALL THE TIME' from Application settings to continue Login", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case 22: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(Login.this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                        username_et = findViewById(R.id.username_et);
                        String username = username_et.getText().toString().trim();

                        String lng, lat;

                        fn_getlocation();
                        SharedPreferences loc = getSharedPreferences("CoronaData", Context.MODE_PRIVATE);
        /*if (Looper.myLooper() == null) {
            Looper.prepare();
        }*/
                        if (loc.getString("lat", "").isEmpty()) {
                            lat = "";
                            lng = "";
                        } else {
                            lat = loc.getString("lat", "");
                            lng = loc.getString("lng", "");
                        }
                        fn_getlocation();

                        if (loc.getString("lat", "").isEmpty()) {
                            lat = "0";
                            lng = "0";
                        } else {
                            lat = loc.getString("lat", "");
                            lng = loc.getString("lng", "");
                        }
                        if (isMockLocation()) {
                            Toast.makeText(getApplicationContext(), "Please turn of Mock location to continue!", Toast.LENGTH_LONG).show();
                        } else if (lat == "0" || lng == "0") {
                            Toast.makeText(getApplicationContext(), "Please move to GPS available location to continue!", Toast.LENGTH_LONG).show();
                            fn_getlocation();
                        } else {
                            sendPostRequest(username, lat, lng, FirebaseInstanceId.getInstance().getToken());
                            Button btn = findViewById(R.id.login);
                            btn.setVisibility(View.GONE);
                            ImageView imgvw = findViewById(R.id.lbtn);
                            imgvw.setVisibility(View.VISIBLE);
                            int duration = Toast.LENGTH_SHORT;
                        }
                    }
                } else {
                    Toast.makeText(this, "Background Location Permission Denied. Please change app location permission to 'ALLOW ALL THE TIME' from Application settings to continue Login", Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
