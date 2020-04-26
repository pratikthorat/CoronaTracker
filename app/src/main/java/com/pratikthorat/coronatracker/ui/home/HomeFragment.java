package com.pratikthorat.coronatracker.ui.home;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Toast;

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
            webViewHome = root.findViewById(R.id.webView_home);
            WebViewUtility.startWebView("http://fightcovid.live/corvis/pages/dashboard", webViewHome, getActivity());

        }else{
            home.setCustomNotification("0");
            replaceFragment(new NotificationFragment());
        }
        return root;
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
       // getActivity().setTitle("Notifications");
        ((ActivityHome) getActivity())
                .setActionBarTitle("Notifications");
        fragmentTransaction.commit();
    }
}
