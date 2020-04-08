package com.pratikthorat.coronatracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pratikthorat.coronatracker.Database.CollectorRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class PushNotificationHandlerService extends FirebaseMessagingService implements LocationListener {
    static final String TAG = "FirebaseNotified";
    private static final int REQUEST_PERMISSIONS = 100;
    static boolean isGPSOn = false;
    //  private static final String TAG = "MyLocationService";
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    CollectorRepository collectorRepository;
    boolean boolean_permission;

    public PushNotificationHandlerService() {
    }

    public static boolean isLocationServicesAvailable(Context context) {
        int locationMode = 0;
        String locationProviders;
        boolean isAvailable = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            isAvailable = (locationMode != Settings.Secure.LOCATION_MODE_OFF);
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            isAvailable = !TextUtils.isEmpty(locationProviders);
        }

        boolean coarsePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
        boolean finePermissionCheck = (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);

        return isAvailable && (coarsePermissionCheck || finePermissionCheck);
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

    boolean checkBackgroundPermission() {
        if (Build.VERSION.SDK_INT < 29) {
            return true;
        }
        boolean backgroundLocationPermissionApproved =
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        return backgroundLocationPermissionApproved;
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

    @Override
    public void onNewToken(String mToken) {
        super.onNewToken(mToken);
        Log.e("TOKEN", mToken);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
        if (pref.getString("firebaseToken", "").equals("")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("firebaseToken", mToken);
            editor.commit();
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //if (/* Check if data needs to be processed by long running job */ true) {
            // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            //scheduleJob();
            // } else {
            // Handle message within 10 seconds
            //handleNow();
            //}

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        SharedPreferences pref = getApplicationContext().getSharedPreferences("LocationDetails", MODE_PRIVATE);

        String lng, lat;
        SharedPreferences sharedpreferences = getSharedPreferences("CoronaData", Context.MODE_PRIVATE);
        fn_getlocation();
        /*if (Looper.myLooper() == null) {
            Looper.prepare();
        }*/
        if (!isLocationServicesAvailable(getApplicationContext())) {
            Log.e(TAG, "Location service disabled.Please enable!");
            lat = "-1";
            lng = "-1";
        } else if (sharedpreferences.getString("lat", "").isEmpty()) {
            lat = "";
            lng = "";
        } else {
            lat = sharedpreferences.getString("lat", "");
            lng = sharedpreferences.getString("lng", "");
        }

        if (isMockLocation()) {
            Log.e(TAG, "Getting mock Location!");
            lat = "-2";
            lng = "-2";
        }


        String verseurl, setBigContentTitle, setSummaryText, setContentTitle, setContentText, type;

        if (remoteMessage.getData().get("verseurl") != null
                && remoteMessage.getData().get("setBigContentTitle") != null
                && remoteMessage.getData().get("setSummaryText") != null
                && remoteMessage.getData().get("setContentTitle") != null
                && remoteMessage.getData().get("setContentText") != null
                && remoteMessage.getData().get("type") != null) {
            verseurl = remoteMessage.getData().get("verseurl");
            setBigContentTitle = remoteMessage.getData().get("setBigContentTitle");
            setSummaryText = remoteMessage.getData().get("setSummaryText");
            setContentTitle = remoteMessage.getData().get("setContentTitle");
            setContentText = remoteMessage.getData().get("setContentText");
            type = remoteMessage.getData().get("type");
        } else {
            verseurl = "Click to send a self Pic";
            setBigContentTitle = "Be a Super Hero! Save us";
            setSummaryText = "Lets Fight Corona";
            setContentTitle = "Lets Fight Corona";
            setContentText = "Click to send your selfie";
            type = "default";
        }
        Log.e(TAG, "verseurl - " + verseurl);
        Log.e(TAG, "setBigContentTitle - " + setBigContentTitle);
        Log.e(TAG, "setSummaryText - " + setSummaryText);
        Log.e(TAG, "setContentTitle - " + setContentTitle);
        Log.e(TAG, "setContentText - " + setContentText);
        Log.e(TAG, "Type - " + type);


        if (type.equals("default")) {
            if (!checkBackgroundPermission()) {
                Log.e(TAG, "Background permission not unabled!");
                lat = "-3";
                lng = "-3";

                sendNotificationForBackgroundPermission(verseurl, setBigContentTitle, setSummaryText, setContentTitle, setContentText, type);
            }
            if (location == null) {
                Log.e(TAG, "Location null");
                lat = "-1";
                lng = "-1";
                // sendNotificationForBackgroundPermission(verseurl,setBigContentTitle,setSummaryText,setContentTitle,setContentText,type);
            }

            Log.e(TAG, "sending post request");
            SharedPreferences prefLogin = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
            String userName = prefLogin.getString("userName", "");
            String version = "0";
            try {
                PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                version = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            if (!userName.isEmpty()) {
                Log.e(TAG, "sending post request.. got userName ");
                sendPostRequest(userName, lat, lng, version);
            }
            Log.e(TAG, "Location: " + lng + ", " + lat + " from" + sharedpreferences.getString("from", ""));
        } else if (type.equals("selfie")) {
            sendNotification(verseurl, setBigContentTitle, setSummaryText, setContentTitle, setContentText, type);
        } else if (type.equals("custom")) {
            sendNotificationWithoutIntent(verseurl, setBigContentTitle, setSummaryText, setContentTitle, setContentText, type);
        } else if (type.equals("update")) {
            sendUpdateNotification(verseurl, setBigContentTitle, setSummaryText, setContentTitle, setContentText, type);
        } else {
            //do nothing
            Log.e(TAG, "doing nothing");
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    private void sendNotification(String verseurl, String setBigContentTitle, String setSummaryText, String setContentTitle, String setContentText, String type) {

        Log.e(TAG, "opening image load");
        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), alarmSound);
        if (mp != null)
            mp.start();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), ImageUpload.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(verseurl);
        bigText.setBigContentTitle(setBigContentTitle);
        bigText.setSummaryText(setSummaryText);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(setContentTitle);
        mBuilder.setContentText(setContentText);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(true);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_002";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Selfie",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }

    private void sendUpdateNotification(String verseurl, String setBigContentTitle, String setSummaryText, String setContentTitle, String setContentText, String type) {

        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), alarmSound);
        if (mp != null)
            mp.start();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_004");
        Intent openURL = new Intent(android.content.Intent.ACTION_VIEW);
        Intent ii = new Intent(Intent.ACTION_VIEW, Uri.parse(verseurl));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Important! Click to update application now");
        bigText.setBigContentTitle(setBigContentTitle);
        bigText.setSummaryText(setSummaryText);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(setContentTitle);
        mBuilder.setContentText("Important! Click to update application now");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(true);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_004";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Update",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(4, mBuilder.build());
    }


    private void sendNotificationForBackgroundPermission(String verseurl, String setBigContentTitle, String setSummaryText, String setContentTitle, String setContentText, String type) {
        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), alarmSound);
        if (mp != null)
            mp.start();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_003");
        //Intent ii = new Intent(this.getApplicationContext(), MainActivity.class);
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        if (Build.VERSION.SDK_INT < 29) {
            bigText.bigText("Location permission disabled. Please enable app location permission and allow us to track you.");
        } else {
            bigText.bigText("Location permission disabled. Please change app location permission to 'ALLOW ALL THE TIME' and allow us to track you.");
        }
        bigText.setBigContentTitle(setBigContentTitle);
        bigText.setSummaryText(setSummaryText);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(setContentTitle);
        if (Build.VERSION.SDK_INT < 29) {
            mBuilder.setContentText("Location permission disabled. Please enable app location permission and allow us to track you.");
        } else {
            mBuilder.setContentText("Location permission disabled. Please change app location permission to 'ALLOW ALL THE TIME' and allow us to track you.");
        }
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);
        mBuilder.setOngoing(true);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_003";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Permission",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(3, mBuilder.build());
    }

    private void sendNotificationWithoutIntent(String verseurl, String setBigContentTitle, String setSummaryText, String setContentTitle, String setContentText, String type) {

        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), alarmSound);
        if (mp != null)
            mp.start();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_002");
        Intent intent = new Intent(this, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa", Locale.getDefault());
        String time = sdf.format(new Date());
        collectorRepository = new CollectorRepository(getApplicationContext());
        collectorRepository.insertRecord(setBigContentTitle, verseurl, time);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(verseurl);
        bigText.setBigContentTitle(setBigContentTitle);
        bigText.setSummaryText(setSummaryText);

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(setContentTitle);
        mBuilder.setContentText(setContentText);
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setAutoCancel(true);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "notify_002";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Corona Information",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(1, mBuilder.build());
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
                Log.e(TAG, "Permission granted");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this, Looper.getMainLooper());
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

    private void sendPostRequest(final String givenUsername, final String givenLatitude, final String givenLongitude, final String givenVersion) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];
                String paramLatitude = params[1];
                String paramLongitude = params[2];
                String paramVersion = params[3];
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                String firebaseToken = pref.getString("firebaseToken", "");

                String link = "";
                try {
                    link = "http://fightcovid.live/corvis/Pages/getLatLongOrImage";
                    String data = URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(paramUsername, "UTF-8");
                    data += "&" + URLEncoder.encode("userLongitude", "UTF-8") + "=" + URLEncoder.encode(paramLongitude, "UTF-8");
                    data += "&" + URLEncoder.encode("userLatitude", "UTF-8") + "=" + URLEncoder.encode(paramLatitude, "UTF-8");
                    data += "&" + URLEncoder.encode("appVersion", "UTF-8") + "=" + URLEncoder.encode(paramVersion, "UTF-8");
                    data += "&" + URLEncoder.encode("userToken", "UTF-8") + "=" + URLEncoder.encode(firebaseToken, "UTF-8");

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
                // Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                Log.e(TAG, result);
                if (result.startsWith("1")) {
                    //Toast.makeText(getApplicationContext(), "Location sent", Toast.LENGTH_SHORT).show();
                } else if (result.equals("0")) {

                } else {

                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenUsername, givenLatitude, givenLongitude, givenVersion);
    }


}
