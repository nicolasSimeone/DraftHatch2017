package nupa.drafthatch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class loginMenu extends ActionBarActivity  {


    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    int User_Id;

    private ProgressDialog dialog;
    static String userCategoriesSetting = "0";
    static String userDistanceSetting = "0";
    ImageView loading;
    Thread splashTread;

    //NEW LOGIN FIREBASE
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String first_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_menu);
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);

        facebookSDKInitialize();
        AppEventsLogger.activateApp(this);

        Intent intent=getIntent();
        first_login = intent.getStringExtra("first_login");

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        dialog = new ProgressDialog(this);
        dialog.setMessage("Cargando..");
        dialog.show();



        StartAnimations();




    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onStart(){
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        Animation anim2 = AnimationUtils.loadAnimation(this, R.anim.alpha2);
        anim2.reset();

        ImageView iv = (ImageView) findViewById(R.id.splash);
        iv.clearAnimation();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();

        l.startAnimation(anim);
        l.startAnimation(anim2);
        iv.startAnimation(anim);

        // anim = AnimationUtils.loadAnimation(this, R.anim.translate);
       // anim.reset();
  splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid().toString()).child("firstLogin");
                    mDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String message = dataSnapshot.getValue(String.class);
                            if (TextUtils.isEmpty(message)){
                                Intent intent2 = new Intent(getApplicationContext(), user_settings_categories.class);
                                startActivity(intent2);
                                finish();
                            }

                            if(!TextUtils.isEmpty(message)){
                                Intent intent1 = new Intent(getApplicationContext(), MainScreen.class);
                                startActivity(intent1);
                                finish();
                            }

                            dialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    // Splash screen pause time
                    while (waited < 5000) {
                        sleep(100);
                        waited += 100;
                    }

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    //loginMenu.this.finish();
                  //  dialog.dismiss();

                }

            }
        };
        splashTread.start();

    }

    protected void facebookSDKInitialize() {
        FacebookSdk.sdkInitialize(getApplicationContext());
    }


}

