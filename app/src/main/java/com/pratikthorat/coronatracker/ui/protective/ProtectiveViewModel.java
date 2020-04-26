package com.pratikthorat.coronatracker.ui.protective;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

public class ProtectiveViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ProtectiveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}