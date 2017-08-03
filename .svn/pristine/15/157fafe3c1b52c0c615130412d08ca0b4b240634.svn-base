package com.iems5722.group5;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.iid.FirebaseInstanceId;
import android.util.Log;
import com.iems5722.group5.AsyncTasks.SendTokenAsyncTask;

/**
 * Created by AlexLiu on 19/3/17.
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "MyFirebaseIIDService";


    // This function will be invoked when Android assigns a token to the app
    @Override public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        SignInActivity.token = refreshedToken;
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token) {
        // Submit Token to your server (e.g. using HTTP)
        // (Implement your own logic ...)
        if (SignInActivity.guser_id != 0 && !"".equals(SignInActivity.token)) {
            new SendTokenAsyncTask(String.valueOf(SignInActivity.guser_id), token).
                    execute(getString(R.string.URL_SUBMIT_TOKEN));
        }
    }
}
