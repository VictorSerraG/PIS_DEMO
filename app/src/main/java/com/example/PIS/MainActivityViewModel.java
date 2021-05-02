package com.example.PIS;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.ArrayList;

public class MainActivityViewModel extends ViewModel {

    //private final MutableLiveData<ArrayList<Nota>> mNota;
    private final MutableLiveData<String> mToast;

    public MainActivityViewModel(){

        //mNota = new MutableLiveData<>();
        mToast = new MutableLiveData<>();
        //DatabaseAdapter da= new DatabaseAdapter(this);
        //da.getCollection();

    }
}
