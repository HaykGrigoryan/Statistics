package com.constantlab.statistics.background.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Created by Hayk on 01/02/2018.
 */

public class SyncResultReceiver<T> extends ResultReceiver {
    public static final int RESULT_CODE_OK = 1100;
    public static final int RESULT_CODE_ERROR = 666;
    public static final String PARAM_EXCEPTION = "exception";
    public static final String PARAM_RESULT = "result";
    public static final String PARAM_MESSAGE = "message";
    private ResultReceiverCallBack mReceiver;

    public SyncResultReceiver(Handler handler) {
        super(handler);
    }
    public void setReceiver(ResultReceiverCallBack<T> receiver) {
        mReceiver = receiver;
    }
    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {

        if (mReceiver != null) {
            if(resultCode == RESULT_CODE_OK){
                mReceiver.onSuccess(resultData.getSerializable(PARAM_MESSAGE));
            } else {
                mReceiver.onError((Exception) resultData.getSerializable(PARAM_EXCEPTION));

            }
        }
    }

    public interface ResultReceiverCallBack<T>{
        public void onSuccess(T data);
        public void onError(Exception exception);
    }
}