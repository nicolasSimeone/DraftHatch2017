package nupa.drafthatch;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Calendar;

import static android.app.Activity.RESULT_OK;


public class ProfileInfoFragment extends Fragment {

    private int PICK_IMAGE_REQUEST = 1;
    public static final String UPLOAD_URL = "http://www.nupa.com.ar/upload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "MY MESSAGE";
    public static  String File = "";
    //CAMERA
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    public static boolean flag = false;

    //FIREBASE
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private int CAMERA_REQUEST_CODE = 0;
    private StorageReference mStorage;
    private DatabaseReference mDatabase;


//
private Bitmap bitmap;
    private Bitmap bitmapp;
    private Bitmap toyImageScaled;
    private Bitmap toyImageScaledd;
    private Uri filePath;
    Calendar photoLastModifiedDate  ;
//
    static String username;

    static String userDistanceSetting;
    static String userCategoriesSetting;
    private int User_Id;
    private ImageView imageView;
    RoundImage roundedImage;

    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;


    public ProfileInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user!=null){

            name = user.getDisplayName();
            email = user.getEmail();
            photoUrl= user.getPhotoUrl();
            uid = user.getUid();

        }


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_info, container, false);

        TextView userEmail=(TextView)view.findViewById(R.id.txtEmail);
        final TextView username2 = (TextView)view.findViewById(R.id.txtNombreUsario2) ;
        final TextView usernameT=(TextView)view.findViewById(R.id.txtUserName);
        final TextView fechaNac = (TextView)view.findViewById(R.id.txtFechaNacimiento);

        userEmail.setText(email);

        username= getActivity().getIntent().getExtras().getString("username");
        userDistanceSetting=getActivity().getIntent().getExtras().getString("distanceSetting");
        userCategoriesSetting = getActivity().getIntent().getExtras().getString("categories");
        User_Id=getActivity().getIntent().getIntExtra("User_Id", 0);


        imageView = (ImageView)view.findViewById(R.id.imageViewShow);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(intent, "Select a picture for your profile"), CAMERA_REQUEST_CODE);
                }
            }
        });

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    mStorage = FirebaseStorage.getInstance().getReference();
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                  //  mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                        mDatabase.child(firebaseAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            String imageUrl = dataSnapshot.child("image").getValue().toString();
                            if (!imageUrl.equals("default") || TextUtils.isEmpty(imageUrl))
                                //Picasso.with(getActivity().getBaseContext()).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).into(imageView);
                            Picasso.with(getActivity().getBaseContext()).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).transform(new CircleTransform()).resize(600,300).into(imageView);

                            if(name==null) {
                                String user_name = dataSnapshot.child("name").getValue().toString();
                                String user_lastName = dataSnapshot.child("apellido").getValue().toString();
                                String user_birthday = dataSnapshot.child("fecha_nac").getValue().toString();
                                usernameT.setText(user_name + " " + user_lastName);
                                username2.setText(user_name + " " + user_lastName);
                                fechaNac.setText(user_birthday);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {

                }
            }
        };




        if(name!=null){
            usernameT.setText(name);
            username2.setText(name);
        }

        /*if(name==null) {
            usernameT.setText(email);
            username2.setText(email);
        }else{
            usernameT.setText(name);
            username2.setText(name);
        }*/



        getImage(username);
        return view;
    }
    private void getImage(String username) {

        class GetImage extends AsyncTask<String,Void,Bitmap> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(), "Loading Profile...", null,true,true);
            }

            @Override
            protected void onPostExecute(Bitmap b) {
                super.onPostExecute(b);
                loading.dismiss();
if (!(b == null)) {
    toyImageScaled = Bitmap.createScaledBitmap(b, 200, 200, false);
    roundedImage = new RoundImage(toyImageScaled);
    imageView.setImageDrawable(roundedImage);
}
            }

            @Override
            protected Bitmap doInBackground(String... params) {
                String id = params[0];
                String add = "http://www.nupa.com.ar/downloadImage.php?id="+id;
                URL url = null;
                Bitmap image = null;
                try {
                    url = new URL(add);
                    image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return image;
            }
        }

        GetImage gi = new GetImage();
        gi.execute(username);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {

            if (mAuth.getCurrentUser() == null)
                return;

            final Uri uri = data.getData();
            if (uri == null) {

                return;
            }
            if (mAuth.getCurrentUser() == null)
                return;

            if (mStorage == null)
                mStorage = FirebaseStorage.getInstance().getReference();
            if (mDatabase == null)
                mDatabase = FirebaseDatabase.getInstance().getReference().child("users");

            final StorageReference filepath = mStorage.child("Photos").child(getRandomString());/*uri.getLastPathSegment()*/
            final DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
            currentUserDB.child("image").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String image = dataSnapshot.getValue().toString();

                    if (!image.equals("default") && !image.isEmpty()) {
                        Task<Void> task = FirebaseStorage.getInstance().getReferenceFromUrl(image).delete();
                        task.addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(getActivity().getBaseContext(), "Deleted image succesfully", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(getActivity().getBaseContext(), "Deleted image failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    currentUserDB.child("image").removeEventListener(this);
                    Activity  c = getActivity();
                    filepath.putFile(uri).addOnSuccessListener(c, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            Toast.makeText(getActivity().getBaseContext(), "Finished", Toast.LENGTH_SHORT).show();
                             Context c = getActivity().getApplicationContext();
                           // Picasso.with(c).load(uri).fit().centerCrop().into(imageView);
                            Picasso.with(c).load(uri).transform(new CircleTransform()).into(imageView);

                           DatabaseReference currentUserDB = mDatabase.child(mAuth.getCurrentUser().getUid());
                            currentUserDB.child("image").setValue(downloadUri.toString());
                        }
                    }).addOnFailureListener((Activity) c, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }
    public String getRandomString() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }
}
