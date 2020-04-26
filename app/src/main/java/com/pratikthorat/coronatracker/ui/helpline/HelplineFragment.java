package com.pratikthorat.coronatracker.ui.helpline;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.pratikthorat.coronatracker.R;
import com.pratikthorat.coronatracker.Util.WebViewUtility;
import com.pratikthorat.coronatracker.ui.district.DistrictViewModel;

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
        webView = root.findViewById(R.id.webViewHelpline);
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/helplinenumbers",webView,getActivity());
        return root;    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(HelplineViewModel.class);
        // TODO: Use the ViewModel
    }

}
