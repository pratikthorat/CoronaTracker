package com.pratikthorat.coronatracker.ui.faceMask;

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

public class MaskFragment extends Fragment {

    private MaskViewModel mViewModel;

    public static MaskFragment newInstance() {
        return new MaskFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.mask_fragment, container, false);
        WebView webView = root.findViewById(R.id.webViewMask);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        AdView adView = root.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/faceMaskTips", webView, getActivity());
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MaskViewModel.class);
        // TODO: Use the ViewModel
    }

}
