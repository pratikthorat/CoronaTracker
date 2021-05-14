package com.pratikthorat.coronatracker.ui.protective;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


public class ProtectiveViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ProtectiveViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}