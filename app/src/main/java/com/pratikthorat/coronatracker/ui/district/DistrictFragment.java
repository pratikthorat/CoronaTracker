package com.pratikthorat.coronatracker.ui.district;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pratikthorat.coronatracker.R;
import com.pratikthorat.coronatracker.Util.WebViewUtility;

public class DistrictFragment extends Fragment {

    private DistrictViewModel mViewModel;

    WebView webView;
    public static DistrictFragment newInstance() {
        return new DistrictFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(DistrictViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        webView = root.findViewById(R.id.webView_home);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/districtcounts", webView, getActivity());
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DistrictViewModel.class);
        // TODO: Use the ViewModel
    }

}
