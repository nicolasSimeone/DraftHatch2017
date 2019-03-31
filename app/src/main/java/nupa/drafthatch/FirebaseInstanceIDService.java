package nupa.drafthatch;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/*
to handle the creation, rotation, and updating of registration tokens. This is required for sending to specific devices or for creating device groups.
*/
public class FirebaseInstanceIDService extends FirebaseInstanceIdService {
    public FirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d("Token 1", token);
    }
}
