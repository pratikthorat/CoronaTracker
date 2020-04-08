package com.pratikthorat.coronatracker;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kosalgeek.android.photoutil.CameraPhoto;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import static android.graphics.Typeface.createFromAsset;

public class ImageUpload extends AppCompatActivity {
    public static final int REQUEST_CAMERA = 1897;
    private static final int REQUEST_READ_PERMISSION = 786;
    static String str = "";
    final int CAMERA_REQUEST = 1445;
    private final String TAG = this.getClass().getName();
    ImageView uploadWithCamera, displayImage;
    Button upload;
    //InterstitialAd mInterstitialAd;
    CameraPhoto cameraPhoto;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.BLACK);

        }
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        TextView author = findViewById(R.id.author);
        Typeface robotothin = createFromAsset(getAssets(),
                "fonts/Roboto-Black.ttf"); //use this.getAssets if you are calling from an Activity
        author.setTypeface(robotothin);

        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.apply_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Upload your Selfie ");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        // abar.setDisplayHomeAsUpEnabled(true);
        abar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //abar.setIcon(R.color.transparent);
        //abar.setHomeButtonEnabled(true);

        TextView alt = findViewById(R.id.alerttext);
        String usern = "SuperHero";
        alt.setText("Hello! " + usern + ", " + alt.getText());

        cameraPhoto = new CameraPhoto(getApplicationContext());

        uploadWithCamera = findViewById(R.id.imageViewCamera);
        upload = findViewById(R.id.imageViewUpload);
        displayImage = findViewById(R.id.imageViewDisplay);

        uploadWithCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permission for CAMERA
                if (ActivityCompat.checkSelfPermission(ImageUpload.this, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Check Permissions Now
                    // Callback onRequestPermissionsResult interceptado na Activity MainActivity
                    ActivityCompat.requestPermissions(ImageUpload.this,
                            new String[]{android.Manifest.permission.CAMERA},
                            ImageUpload.REQUEST_CAMERA);
                } else {
                    // permission has been granted, continue as usual
                    Toast.makeText(ImageUpload.this, "Camera waiting for your selfi pose", Toast.LENGTH_SHORT).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 0);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                    Toast.makeText(ImageUpload.this, "Camera waiting for your selfi pose", Toast.LENGTH_SHORT).show();
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    alertpermission("camera");
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /*
        private void requestNewInterstitial(){
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }*/
    public void alertpermission(String what) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(ImageUpload.this);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
            if (pref.getString("userName", null) == null || pref.getString("userName", null) == "") {
                builder.setTitle("Hello, Super Hero!");
            } else {
                String text = pref.getString("userName", null);
                if (text.indexOf(' ') > -1) { // Check if there is more than one word.
                    builder.setTitle("Hello, " + text.substring(0, text.indexOf(' ')) + " !"); // Extract first word.
                } else {
                    builder.setTitle("Hello, " + text + " !"); // Text is the first word itself.
                }
            }
            builder.setMessage("You had denied permission to access " + what + " last time I asked you. To enable image upload feature again, Please goto \n settings\n->apps/installed apps\n->select Corona Tracker app\n->permissions\n->Enable " + what + " permission.\nThen rerun app")
                    .setCancelable(false)
                    .setNegativeButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ImageUpload.super.finish();
                                }
                            }
                    )
                    .setPositiveButton("goto app setting",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                    //  ImageUpload.super.finish();
                                }
                            }
                    )
                    .setIcon(android.R.drawable.ic_dialog_alert);
            AlertDialog alert = builder.create();
            alert.show();
        } catch (Exception e) {
            Log.v("Home", "############################You are not online!!!!" + e.getMessage());
            Toast.makeText(ImageUpload.this, "Something went wrong. Please try again later!", Toast.LENGTH_SHORT).show();
        }


    }

    private boolean MyStartActivity(Intent aIntent) {
        try {
            startActivity(aIntent);
            return true;
        } catch (ActivityNotFoundException e) {
            return false;
        }
    }

    public void uploadaction(View v) {
        if (bitmap != null) {
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                byte[] imageBytes = bos.toByteArray();
                String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                SharedPreferences pref = getApplicationContext().getSharedPreferences("LoginDetails", MODE_PRIVATE);
                String user = pref.getString("userName", "");
                //Toast.makeText(ImageUpload.this, user, Toast.LENGTH_LONG).show();

                sendPostRequest(user, encodedImage);
            } catch (Exception ex) {
                Log.e(TAG, ex.getMessage());
                Toast.makeText(ImageUpload.this, "Why did you disturbing me :<\nPlease select valid JPEG image before!!!", Toast.LENGTH_LONG).show();
                ImageView imgvw = findViewById(R.id.lbtn);
                imgvw.setVisibility(View.GONE);
                upload = findViewById(R.id.imageViewUpload);
                upload.setVisibility(View.VISIBLE);
            }
            ImageView imgvw = findViewById(R.id.lbtn);
            imgvw.setVisibility(View.VISIBLE);
            upload = findViewById(R.id.imageViewUpload);
            upload.setVisibility(View.INVISIBLE);
        } else {
            Toast.makeText(ImageUpload.this, "Why did you disturbing me :<\nPlease select valid JPEG image before!!!", Toast.LENGTH_LONG).show();
            ImageView imgvw = findViewById(R.id.lbtn);
            imgvw.setVisibility(View.GONE);
            upload = findViewById(R.id.imageViewUpload);
            upload.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                //  String path = cameraPhoto.getPhotoPath();
                // File file = new File(path);
                // int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
                // Toast.makeText(this, "FileSize=" + file_size, Toast.LENGTH_SHORT).show();
                try {
                    //  bitmap = ImageLoader.init().from(path).requestSize(240, 300).getBitmap();
                    bitmap = (Bitmap) data.getExtras().get("data");
                    // final int THUMBSIZE = 64;
                    bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                            240, 320);
                    // bitmap = Bitmap.createScaledBitmap(bitmap, 240, 300, true);
                    displayImage.setImageBitmap(bitmap);

                } catch (Exception e) {
                    Toast.makeText(ImageUpload.this, "Something went wrong!\nOut of my understanding. Please Contact Developer", Toast.LENGTH_LONG).show();
                }
                //  Log.d(TAG, path);
            }
        }
    }


    private void sendPostRequest(final String givenUsername, final String encodedImage) {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String> {
            @Override
            protected String doInBackground(String... params) {

                String paramUsername = params[0];
                String paramEncodedImage = params[1];


                try {

                    String lng, lat;

                    SharedPreferences sharedpreferences = getSharedPreferences("CoronaData", Context.MODE_PRIVATE);
                    if (sharedpreferences.getString("lat", "").isEmpty()) {
                        lat = "0";
                        lng = "0";
                    } else {
                        lat = sharedpreferences.getString("lat", "");
                        lng = sharedpreferences.getString("lng", "");
                    }
                    String version = "0";
                    try {
                        PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
                        version = pInfo.versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    String link;

                    link = "http://fightcovid.live/corvis/pages/getLatLongOrImage";

                    String data = URLEncoder.encode("phoneNumber", "UTF-8") + "=" + URLEncoder.encode(paramUsername, "UTF-8");
                    data += "&" + URLEncoder.encode("userLatitude", "UTF-8") + "=" + URLEncoder.encode(lat, "UTF-8");
                    data += "&" + URLEncoder.encode("userLongitude", "UTF-8") + "=" + URLEncoder.encode(lng, "UTF-8");
                    data += "&" + URLEncoder.encode("imageUploadPath", "UTF-8") + "=" + URLEncoder.encode(paramEncodedImage, "UTF-8");
                    data += "&" + URLEncoder.encode("appVersion", "UTF-8") + "=" + URLEncoder.encode(version, "UTF-8");


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
                    Log.e("MyError", e.getMessage());
                    return "Exception: " + e.getMessage();
                }
            }

            protected void onPreExecute() {

                Log.i("thread", "Started...");
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                //Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                if (result.contains("1")) {
                    Toast.makeText(ImageUpload.this, "IMAGE UPLOADED\nThanks You for your \ncooperation and support :)", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    finish();
                    startActivity(intent);
                } else {
                    Toast.makeText(ImageUpload.this, result, Toast.LENGTH_SHORT).show();
                    Log.d("MyError", result);
                    ImageView imgvw = findViewById(R.id.lbtn);
                    imgvw.setVisibility(View.GONE);
                    upload = findViewById(R.id.imageViewUpload);
                    upload.setVisibility(View.VISIBLE);
                }
            }
        }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(givenUsername, encodedImage);
    }

    @Override
    public void onBackPressed() {
        //Do nothing
    }
}

