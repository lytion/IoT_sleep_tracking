package com.devdivision.iotsleeptracking.ui.bedroom;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BedroomViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BedroomViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}