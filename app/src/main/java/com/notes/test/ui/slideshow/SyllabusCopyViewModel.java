package com.notes.test.ui.slideshow;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SyllabusCopyViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public SyllabusCopyViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Syllabus copy feature comming soon....!");
    }

    public LiveData<String> getText() {
        return mText;
    }
}