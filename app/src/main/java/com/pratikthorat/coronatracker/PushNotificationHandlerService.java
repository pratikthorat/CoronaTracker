package com.pratikthorat.coronatracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

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


public class PushNotificationHandlerService extends FirebaseMessagingService {
    static final String TAG = "FirebaseNotified";
    CollectorRepository collectorRepository;

    public PushNotificationHandlerService() {
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
            verseurl = "Thank you for your support!";
            setBigContentTitle = "Be a Super Hero! Save humanity";
            setSummaryText = "Lets Fight Corona";
            setContentTitle = "Lets Fight Corona";
            setContentText = "Thank you for your support!";
            type = "default";
        }
        Log.e(TAG, "verseurl - " + verseurl);
        Log.e(TAG, "setBigContentTitle - " + setBigContentTitle);
        Log.e(TAG, "setSummaryText - " + setSummaryText);
        Log.e(TAG, "setContentTitle - " + setContentTitle);
        Log.e(TAG, "setContentText - " + setContentText);
        Log.e(TAG, "Type - " + type);


        if (type.equals("default")) {

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
                sendPostRequest(userName, version);
            }
        } else if (type.equals("selfie")) {
            //sendNotification(verseurl, setBigContentTitle, setSummaryText, setContentTitle, setContentText, type);
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

    private void sendNotificationWithoutIntent(String verseurl, String setBigContentTitle, String setSummaryText, String setContentTitle, String setContentText, String type) {

        Uri alarmSound =
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), alarmSound);
        if (mp != null)
            mp.start();

        NotificationManager mNotificationManager;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_002");

        Intent intent = new Intent(this, ActivityHome.class);
        intent.putExtra("isCustomNotification","1");

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

    private void sendPostRequest(final String givenUsername, final String givenVersion) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];
                String paramVersion = params[1];
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                String firebaseToken = pref.getString("firebaseToken", "");

                String link = "";
                try {
                    link = "http://fightcovid.live/corvis/Pages/getLatLongOrImage";
                    String data = URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(paramUsername, "UTF-8");
                    data += "&" + URLEncoder.encode("userLongitude", "UTF-8") + "=" + URLEncoder.encode("-9", "UTF-8");
                    data += "&" + URLEncoder.encode("userLatitude", "UTF-8") + "=" + URLEncoder.encode("-9", "UTF-8");
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
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenUsername, givenVersion);
    }
}
