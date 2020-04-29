package com.pratikthorat.coronatracker.ui.faceMask;

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
