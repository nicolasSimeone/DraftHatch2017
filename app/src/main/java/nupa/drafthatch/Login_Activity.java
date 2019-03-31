package nupa.drafthatch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

public class Login_Activity extends AppCompatActivity implements View.OnClickListener{

    Button bLogin;
    EditText etUserEmail , etPassword;
    TextView registerLink, resetPassword;
    UserLocalStore userLocalStore;
    String PROJECT_NUMBER="828378326359";
    String registrationID;
    static String username = "";
    CallbackManager callbackManager;

    //NEW LOGIN FIREBASE
    private static final String TAG = "Login_Activity";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progressDialog, progressDialogFacebook;
    //END NEW LOGIN


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_login_);

        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);


        progressDialog = new ProgressDialog(this);
        progressDialogFacebook = new ProgressDialog(this);

        etUserEmail =   (EditText) findViewById(R.id.etUseremail);
        etPassword =   (EditText) findViewById(R.id.etPassword);
        bLogin =   (Button) findViewById(R.id.bLogin);
        registerLink = (TextView) findViewById(R.id.tvRegisterLink);
        resetPassword = (TextView) findViewById(R.id.tvResetPassword);


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
                    Intent intent = new Intent(getApplicationContext(),loginMenu.class);
                    startActivity(intent);
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // ...

        final LoginButton loginButton=(LoginButton)findViewById(R.id.bLoginWithFacebook);
        loginButton.setReadPermissions("email", "public_profile");
        getLoginDetails(loginButton);
        loginButton.setBackgroundResource(R.drawable.facebook_login_button);
        loginButton.setText("");
        loginButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null,null);

        ImageView imgFacebook = (ImageView)findViewById(R.id.imgFacebook);
        imgFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginButton.performClick();
            }
        });

        GCMClientManager pushClientManager = new GCMClientManager(this, PROJECT_NUMBER);
        pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
            @Override
            public void onSuccess(String registrationId, boolean isNewRegistration) {

                Log.d("Registration id", registrationId);
                //send this registrationId to your server
                registrationID=registrationId;
            }
            @Override
            public void onFailure(String ex) {
                super.onFailure(ex);
            }
        });

        etUserEmail.setText("Usuario@ejemplo.com");

        etUserEmail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    etUserEmail.setText("");
                }
                return false;
            }
        });

        etPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    etPassword.setText("");

                }

                return false;
            }
        });


        registerLink.setOnClickListener(this);
        resetPassword.setOnClickListener(this);
        userLocalStore = new UserLocalStore(this);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });




    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    private void doLogin() {
        String email = etUserEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            progressDialog.setMessage("Ingresando , espere por favor");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(Login_Activity.this, "Login succesful", Toast.LENGTH_SHORT).show();
                                sendRegistrationID();
                                Intent intent = new Intent(getApplicationContext(),loginMenu.class);
                                startActivity(intent);
                            } else
                                Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    @Override
    public void onClick(View v) {
        //Switch saca el ID del VIEW method , para reconocer diferentes OnclickListener
        switch (v.getId()) {

            case R.id.tvRegisterLink:
                Intent register=new Intent(getApplicationContext(),ActivityRegisterFirebase.class);
                register.putExtra("registration_id", registrationID);
                startActivity(register);
                break;
            case R.id.tvResetPassword:
                Intent reset=new Intent(getApplicationContext(),ResetPasswordActivity.class);
                startActivity(reset);
                break;
        }
    }




    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager= CallbackManager.Factory.create();
    }


    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager,new FacebookCallback<LoginResult>(){

        @Override
            public void onSuccess(LoginResult login_result) {
            progressDialogFacebook.setMessage("Ingresando , espere por favor");
            progressDialogFacebook.show();
            getUserInfo(login_result);
            handleFacebookAccessToken(login_result.getAccessToken());
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    protected void getUserInfo(LoginResult login_result){

        GraphRequest data_request = GraphRequest.newMeRequest(
                login_result.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback(){
                    @Override
                    public void onCompleted(JSONObject json_object, GraphResponse response) {
                      /*  Intent intent = new Intent(getApplicationContext(),loginMenu.class);
                        try {
                            intent.putExtra("username",json_object.get("name").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        */
                    }

                });
        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,email,picture.width(120).height(120)");
        data_request.setParameters(permission_param);
        data_request.executeAsync();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e("data",data.toString());
    }
    private void handleFacebookAccessToken(final AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if(!task.isSuccessful()) {
                            try {
                                throw task.getException();
                            } catch(FirebaseAuthWeakPasswordException e) {
                                Toast.makeText(getApplicationContext(), "FirebaseAuthWeakPasswordException",
                                        Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthInvalidCredentialsException e) {
                                Toast.makeText(getApplicationContext(),"FirebaseAuthInvalidCredentialsException" ,
                                        Toast.LENGTH_SHORT).show();
                            } catch(FirebaseAuthUserCollisionException e) {
                                Toast.makeText(getApplicationContext(),"FirebaseAuthUserCollisionException",
                                        Toast.LENGTH_SHORT).show();
                            } catch(Exception e) {
                                Log.e(TAG, e.getMessage());
                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }else{

                            DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid()).child("firstLogin");
                            mDatabase2.addValueEventListener(new ValueEventListener() {


                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                 String message = dataSnapshot.getValue(String.class);

                                    if(message == null){
                                        RegisterFacebookUserInFireBaseDatabase();

                                    }else{

                                        progressDialogFacebook.dismiss();
                                        sendRegistrationID();
                                        Intent intent = new Intent(Login_Activity.this, loginMenu.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }


                    }
                });


    }
    private void RegisterFacebookUserInFireBaseDatabase() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());


        currentUserDB.child("name").setValue(mAuth.getCurrentUser().getDisplayName());
        currentUserDB.child("image").setValue("default");
        currentUserDB.child("email").setValue(mAuth.getCurrentUser().getEmail());
        currentUserDB.child("registration_id").setValue(FirebaseInstanceId.getInstance().getToken());
        currentUserDB.child("distance").setValue("100");
       // currentUserDB.child("categories").setValue("Deportes,Musica,Activismo,Mascotas,Salidas,Flash");
        currentUserDB.child("categories").setValue("0,0,0,0,0,Flash");
       // currentUserDB.child("firstLogin").setValue("0");
        currentUserDB.child("reputation").setValue("0");
        currentUserDB.child("karma").setValue("0");
        currentUserDB.child("nickname").setValue(mAuth.getCurrentUser().getDisplayName());
        currentUserDB.child("flag_notification").setValue("1");
        //currentUserDB.child("birthday").setValue("ToBeDefined");

        progressDialogFacebook.dismiss();
        Intent intent = new Intent(Login_Activity.this, loginMenu.class);
        intent.putExtra("first_login","0");

        startActivity(intent);
        finish();


    }

    private void sendRegistrationID (){
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
        currentUserDB.child("registration_id").setValue(FirebaseInstanceId.getInstance().getToken());

    }

}
