package com.pratikthorat.coronatracker.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.pratikthorat.coronatracker.ActivityHome;
import com.pratikthorat.coronatracker.R;
import com.pratikthorat.coronatracker.Util.WebViewUtility;
import com.pratikthorat.coronatracker.ui.notification.NotificationFragment;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    WebView webViewHome;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =ViewModelProviders.of(this).get(HomeViewModel.class);


        ActivityHome home=(ActivityHome) getActivity();
        View root = null;
        String isCustomFragment = home.isCustomNotification();
        //Toast.makeText(getActivity(), "isCustomFragment::" + isCustomFragment, Toast.LENGTH_LONG).show();
       // root = inflater.inflate(R.layout.fragment_home, container, false);
        if(!"1".equals(isCustomFragment) || isCustomFragment==null) {
            root = inflater.inflate(R.layout.fragment_home, container, false);
            AdView adView = root.findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.loadAd(adRequest);
            webViewHome = root.findViewById(R.id.webView_home);
            WebSettings webSettings = webViewHome.getSettings();
            webSettings.setJavaScriptEnabled(true);
            WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/dashboard", webViewHome, getActivity());

        }else{
            home.setCustomNotification("0");
            replaceFragment(new NotificationFragment());
        }
        return root;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
       // getActivity().setTitle("Notifications");
        ((ActivityHome) getActivity())
                .setActionBarTitle("Notifications");
        fragmentTransaction.commit();
    }

}
