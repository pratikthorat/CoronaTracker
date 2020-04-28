package com.pratikthorat.coronatracker.ui.login;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pratikthorat.coronatracker.ActivityHome;
import com.pratikthorat.coronatracker.Login;
import com.pratikthorat.coronatracker.R;
import com.pratikthorat.coronatracker.ui.home.HomeFragment;

public class LoginFragment extends Fragment {

    private LoginViewModel mViewModel;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);

        replaceFragment(new HomeFragment());
        ((ActivityHome)getActivity()).hideItem();
        Intent i = new Intent(getActivity(), Login.class);
        startActivity(i);

        getActivity().overridePendingTransition(0, 0);

        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        // TODO: Use the ViewModel
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.nav_host_fragment, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        // getActivity().setTitle("Notifications");
        ((ActivityHome) getActivity())
                .setActionBarTitle("Home");
        fragmentTransaction.commit();
    }

}
