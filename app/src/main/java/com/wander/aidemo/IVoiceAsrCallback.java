package com.wander.aidemo;

import android.os.Bundle;

public interface IVoiceAsrCallback {
    String RESULTS_RECOGNITION = "results_recognition";
    int ERROR_NETWORK = 2001;
    int ERROR_TIMEOUT = 1001;
    int ERROR_MIC = 3001;
    int ERROR_NO_RESULT = 7001;
    int ERROR_INIT = 10009;
    int ERROR_BUSY = 8001;

    void onReadyForSpeech(Bundle var1);
    void onBeginningOfSpeech();
    void onRmsChanged(float var1);
    void onBufferReceived(byte[] var1);
    void onEndOfSpeech();
    void onError(int var1);
    void onResults(Bundle bundle);
    void onPartialResults(Bundle bundle);
    void onIntent(String command);
}
