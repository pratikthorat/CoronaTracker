package com.pratikthorat.coronatracker.ui.protective;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pratikthorat.coronatracker.R;
import com.pratikthorat.coronatracker.Util.WebViewUtility;

public class ProtectiveFragment extends Fragment {

    private ProtectiveViewModel galleryViewModel;
    WebView webView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(ProtectiveViewModel.class);
        View root = inflater.inflate(R.layout.fragment_protective, container, false);
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        webView = root.findViewById(R.id.webViewProtective);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/coronavirusinfo", webView, getActivity());

        return root;
    }
}
