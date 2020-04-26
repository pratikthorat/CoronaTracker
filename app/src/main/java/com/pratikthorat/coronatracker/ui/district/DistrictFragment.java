package com.pratikthorat.coronatracker.ui.district;

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
import com.pratikthorat.coronatracker.ui.home.HomeViewModel;

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
        WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/districtcounts",webView,getActivity());
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(DistrictViewModel.class);
        // TODO: Use the ViewModel
    }

}
