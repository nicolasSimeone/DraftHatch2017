package nupa.drafthatch;


import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;


public class OtherProfileInfoFragment extends Fragment {

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
    private int User_id;
    static String userDistanceSetting;
    static String userCategoriesSetting;




    private ImageView imageView;
    RoundImage roundedImage;

    static String name;
    static String email;
    static Uri photoUrl;
    static String uid;
    public OtherProfileInfoFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_other_profile_info, container, false);

        username= getActivity().getIntent().getExtras().getString("username");
        userDistanceSetting=getActivity().getIntent().getExtras().getString("distanceSetting");
        userCategoriesSetting = getActivity().getIntent().getExtras().getString("categories");



        imageView = (ImageView)view.findViewById(R.id.imageViewShow);


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
                                Picasso.with(getActivity().getBaseContext()).load(Uri.parse(dataSnapshot.child("image").getValue().toString())).transform(new CircleTransform()).into(imageView);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {

                }
            }
        };



        /*TextView userEmail=(TextView)view.findViewById(R.id.txtEmail);
        userEmail.setText(email);
        TextView usernameT=(TextView)view.findViewById(R.id.txtUserName);
        if(name==null){
            usernameT.setText(email);
        }else {
            usernameT.setText(name);
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

}
