package com.pratikthorat.coronatracker.ui.helpline;

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

public class HelplineFragment extends Fragment {

    private HelplineViewModel mViewModel;
    WebView webView;
    public static HelplineFragment newInstance() {
        return new HelplineFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(HelplineViewModel.class);
        View root = inflater.inflate(R.layout.helpline_fragment, container, false);
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        webView = root.findViewById(R.id.webViewHelpline);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/helplinenumbers", webView, getActivity());
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HelplineViewModel.class);
        // TODO: Use the ViewModel
    }

}
