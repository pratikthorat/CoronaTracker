package com.pratikthorat.coronatracker.ui.notification;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pratikthorat.coronatracker.Database.Collector;
import com.pratikthorat.coronatracker.Database.CollectorRepository;
import com.pratikthorat.coronatracker.MyRecyclerViewAdapter;
import com.pratikthorat.coronatracker.Notification;
import com.pratikthorat.coronatracker.R;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {
    private NotificationViewModel notificationViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationViewModel =
                ViewModelProviders.of(this).get(NotificationViewModel.class);
        View root = inflater.inflate(R.layout.notification_fragment, container, false);
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.Planets, android.R.layout.simple_list_item_1);
        RecyclerView recyclerView = root.findViewById(R.id.notificationList);
        TextView noNotificationLabel = root.findViewById(R.id.noNotf);

        populateList(recyclerView, noNotificationLabel);
        return root;
    }

    private void populateList(final RecyclerView recyclerView, final TextView noNotificationLabel) {
        new AsyncTask<Void, Void, List<Collector>>() {
            @Override
            protected List<Collector> doInBackground(Void... voids) {
                CollectorRepository collectorRepository = new CollectorRepository(getActivity());
                return collectorRepository.getAllRecords();
            }

            MyRecyclerViewAdapter adapter;
            final ArrayList<Notification> notice = new ArrayList<>();
            @Override
            protected void onPostExecute(List<Collector> collectors) {
                for (Collector col : collectors) {
                    notice.add(0, new Notification(col.getTitle(), col.getMessage(), col.getTime()));
                }
                // set up the RecyclerView

                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapter = new MyRecyclerViewAdapter(getActivity(), notice);
                //adapter.setClickListener(getApplicationContext());
                recyclerView.setAdapter(adapter);
                if (collectors.size() == 0)
                    noNotificationLabel.setVisibility(View.VISIBLE);
            }
        }.execute();
    }
}