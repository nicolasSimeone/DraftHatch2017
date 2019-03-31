package nupa.drafthatch;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import nupa.drafthatch.R;

public class ActivityRegisterFirebase extends AppCompatActivity {

    private EditText mNameField, nApellido, mEmailFiedl,mPasswordField, mConfirmPassword, nFechaNac;
    private CheckBox checkTerminos;

    private Button mRegisterButton;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private TextView mTextLogin;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Calendar myCalendar = Calendar.getInstance();


    String registrationID;
    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_firebase);

        Intent intent=getIntent();
        registrationID=intent.getStringExtra("registration_id");

        mAuth = FirebaseAuth.getInstance();
        mNameField = (EditText) findViewById(R.id.etxt_name);
        mEmailFiedl = (EditText) findViewById(R.id.etxt_email);
        mPasswordField = (EditText) findViewById(R.id.etxt_password);
        mConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);
        mRegisterButton = (Button) findViewById(R.id.btn_register);
        nApellido = (EditText)findViewById(R.id.etApellido);
        nFechaNac = (EditText)findViewById(R.id.etFechaNac);
        checkTerminos = (CheckBox) findViewById(R.id.checkTerminosCondiciones);
        mProgress = new ProgressDialog(this);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRegister();
            }
        });
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                  //  Intent intent = new Intent(ActivityRegisterFirebase.this, ActivityLoginEmailPass.class);
                   // startActivity(intent);
                  //  finish();
                }
            }
        };

        nFechaNac.setInputType(InputType.TYPE_NULL);

        nFechaNac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(ActivityRegisterFirebase.this, dateAge, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DATE)).show();
            }
            DatePickerDialog.OnDateSetListener dateAge = new DatePickerDialog.OnDateSetListener(){

                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth) ;

                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "dd-MM-yyyy", Locale.getDefault());
                    nFechaNac.setText(dateFormat.format(myCalendar.getTime()).toString());
                }
            };

        });

    }

    public boolean checkAge(){
        Calendar today = Calendar.getInstance();

        if (today.get(Calendar.YEAR)- myCalendar.get(Calendar.YEAR) < 18){
            Log.d("Age", "Sos menor de 18");
            return false;
        } else {
            if (today.get(Calendar.YEAR) - myCalendar.get(Calendar.YEAR) == 18) {
                if (today.get(Calendar.MONTH) == myCalendar.get(Calendar.MONTH)) {
                    if (today.get(Calendar.DAY_OF_MONTH) - myCalendar.get(Calendar.DAY_OF_MONTH) <= 0) {
                        Log.d("Age2", "Sos menor de 18");
                        return false;
                    }else
                    {
                        Log.d("Age3", "Sos mayor de 18");
                        return true;
                    }
                }if(today.get(Calendar.MONTH) - myCalendar.get(Calendar.MONTH)> 0){
                        Log.d("Age4", "Sos mayor de 18");
                    return true;
                }else{
                    if(today.get(Calendar.MONTH) - myCalendar.get(Calendar.MONTH)< 0){
                        Log.d("Age5", "Sos menor de 18");
                        return false;
                    }
                }
            } else {
                Log.d("Age6", "Sos mayor de 18");
                return true;
            }
        }

        return false;
    }
    private void startRegister() {
        final String name = mNameField.getText().toString().trim();
        final String email = mEmailFiedl.getText().toString().trim();
        final String password = mPasswordField.getText().toString().trim();
        final String lastName = nApellido.getText().toString().trim();
        final String fechaNac = nFechaNac.getText().toString();
        final String confirmPass = mConfirmPassword.getText().toString();

        boolean isOk = false;

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(lastName) || TextUtils.isEmpty(fechaNac) || TextUtils.isEmpty(email)
                || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPass)){
            Toast.makeText(ActivityRegisterFirebase.this, "Por favor complete todo los campos", Toast.LENGTH_SHORT).show();
        } else {
            if (!password.equals(confirmPass)){
                Toast.makeText(ActivityRegisterFirebase.this, "Las contraseñas no son iguales", Toast.LENGTH_SHORT).show();
            }
            else{
                if (!checkAge()){
                    Toast.makeText(ActivityRegisterFirebase.this, "Debe ser mayor de 18 años para usar Around", Toast.LENGTH_SHORT).show();
                }else {
                    if (!checkTerminos.isChecked()){
                        Toast.makeText(ActivityRegisterFirebase.this, "Debe aceptar los terminos y condiciones", Toast.LENGTH_SHORT).show();
                    }else {
                        isOk = true;
                    }
                }
            }
        }




        //if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && checkAge()) {
        if (isOk) {
            mProgress.setMessage("Registrando, espere por favor...");
            mProgress.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            mProgress.dismiss();
                            if (task.isSuccessful()) {
                                mAuth.signInWithEmailAndPassword(email, password);
                                //Toast.makeText(ActivityRegister.this, user_id, Toast.LENGTH_SHORT).show();

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                                currentUserDB.child("name").setValue(name);
                                currentUserDB.child("email").setValue(mAuth.getCurrentUser().getEmail());
                                currentUserDB.child("image").setValue("default");
                                currentUserDB.child("registration_id").setValue(FirebaseInstanceId.getInstance().getToken());
                                currentUserDB.child("distance").setValue("100");
                                //currentUserDB.child("categories").setValue("Deportes,Musica,Activismo,Mascotas,Salidas,Flash");
                                currentUserDB.child("categories").setValue("0,0,0,0,0,Flash");
                                //currentUserDB.child("firstLogin").setValue("0");
                                currentUserDB.child("reputation").setValue("0");
                                currentUserDB.child("karma").setValue("0");
                                currentUserDB.child("nickname").setValue(name);
                                currentUserDB.child("apellido").setValue(lastName);
                                currentUserDB.child("fecha_nac").setValue(fechaNac);
                                currentUserDB.child("flag_notification").setValue("1");
                                Intent intent = new Intent(ActivityRegisterFirebase.this, loginMenu.class);
                                startActivity(intent);
                                finish();
                            } else
                                Toast.makeText(ActivityRegisterFirebase.this, "Email or Password incorrectos", Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

}
