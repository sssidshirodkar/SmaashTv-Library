package com.smaaash.tv.background;

import com.smaaash.tv.apis.base.Error;

/**
 * Callback for the interactor to send the response to the presenter
 * <p>
 * Created by saghayam on 4/13/2016.
 */
public interface Callback<T> {

    void onSuccess(T callback);

    void onError(Error callback);

//    T callback;
//
//    public Callback(){
//
//    }
//
//    public void onSuccess(T callback){
//        this.callback = callback;
//    }
//
//    public void onError(){
//
//    }
}
