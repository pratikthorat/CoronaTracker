package com.pratikthorat.coronatracker;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pratikthorat.coronatracker.Database.Collector;
import com.pratikthorat.coronatracker.Database.CollectorRepository;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Typeface.createFromAsset;

public class ListActivity extends AppCompatActivity {

    MyRecyclerViewAdapter adapter;
    TextView view;
    ArrayList<Notification> notice = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
        view = findViewById(R.id.noNotice);
        final ActionBar abar = getSupportActionBar();
        View viewActionBar = getLayoutInflater().inflate(R.layout.apply_actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(//Center the textview in the ActionBar !
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER);
        TextView author = findViewById(R.id.author);
        Typeface robotothin = createFromAsset(getAssets(),
                "fonts/Roboto-Black.ttf"); //use this.getAssets if you are calling from an Activity
        author.setTypeface(robotothin);
        TextView textviewTitle = viewActionBar.findViewById(R.id.actionbar_textview);
        textviewTitle.setText("Notifications");
        abar.setCustomView(viewActionBar, params);
        abar.setDisplayShowCustomEnabled(true);
        abar.setDisplayShowTitleEnabled(false);
        //abar.setDisplayHomeAsUpEnabled(true);
        abar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));
        //abar.setIcon(R.color.transparent);
        abar.setHomeButtonEnabled(true);
        populateList();

    }

    /*@Override
    public void onItemClick(View view, int position) {

    }*/

    private void populateList() {
        new AsyncTask<Void, Void, List<Collector>>() {
            @Override
            protected List<Collector> doInBackground(Void... voids) {
                CollectorRepository collectorRepository = new CollectorRepository(getApplicationContext());
                return collectorRepository.getAllRecords();
            }

            @Override
            protected void onPostExecute(List<Collector> collectors) {
                for (Collector col : collectors) {
                    notice.add(0, new Notification(col.getTitle(), col.getMessage(), col.getTime()));
                }
                // set up the RecyclerView
                RecyclerView recyclerView = findViewById(R.id.notificationList);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                adapter = new MyRecyclerViewAdapter(getApplicationContext(), notice);
                //adapter.setClickListener(getApplicationContext());
                recyclerView.setAdapter(adapter);
                if (collectors.size() == 0)
                    view.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}
