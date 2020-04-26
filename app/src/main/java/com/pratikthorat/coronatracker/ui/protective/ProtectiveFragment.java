package com.pratikthorat.coronatracker.ui.protective;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

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
        webView = root.findViewById(R.id.webViewProtective);
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/coronavirusinfo",webView,getActivity());

        return root;
    }
}
