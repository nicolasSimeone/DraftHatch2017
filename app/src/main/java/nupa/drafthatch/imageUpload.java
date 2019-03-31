package nupa.drafthatch;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import javax.microedition.khronos.opengles.GL10;

public class imageUpload extends ActionBarActivity implements View.OnClickListener {

    public static final String UPLOAD_URL = "http://www.nupa.com.ar/upload.php";
    public static final String UPLOAD_KEY = "image";
    public static final String TAG = "MY MESSAGE";
    public static String File = "";
    private int PICK_IMAGE_REQUEST = 1;
    private Button buttonChoose;
    private Button buttonUpload;
    private Button buttonView;
    private Button buttonTakePhoto;
    private ImageView imageView;
    private ImageView imageViewDefault;
    private Bitmap bitmap;
    private Bitmap bitmapp;
    private Bitmap toyImageScaled;
    private Bitmap toyImageScaledd;
    private Uri filePath;
    Calendar photoLastModifiedDate;
    RoundImage roundedImage;
    //CAMERA
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    public static boolean flag = false;
    //
    private String username;
    static String userDistanceSetting;
    static String userCategoriesSetting;
    private int User_Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        username = getIntent().getExtras().getString("username");
        userDistanceSetting = getIntent().getExtras().getString("distanceSetting");
        userCategoriesSetting = getIntent().getExtras().getString("categories");
        User_Id = getIntent().getIntExtra("User_Id", 0);


        setContentView(R.layout.activity_image_upload);
        buttonChoose = (Button) findViewById(R.id.buttonChoose);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonUpload.setEnabled(false);
        // buttonView = (Button) findViewById(R.id.buttonViewImage);
        buttonTakePhoto = (Button) findViewById(R.id.btnCapture);
        imageView = (ImageView) findViewById(R.id.imageViewShow);
        imageViewDefault = (ImageView) findViewById(R.id.imageViewShow);
        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        //  buttonView.setOnClickListener(this);
        buttonTakePhoto.setOnClickListener(this);
        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Hatchs/";
        File newdir = new File(dir);
        newdir.mkdirs();
        Button capture = (Button) findViewById(R.id.btnCapture);
        capture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                count++;
                photoLastModifiedDate = Calendar.getInstance();
                File = dir + photoLastModifiedDate.get(Calendar.DATE) + "-" + (photoLastModifiedDate.get(Calendar.MONTH) + 1) + "-" + photoLastModifiedDate.get(Calendar.YEAR) + " " + photoLastModifiedDate.get(Calendar.HOUR_OF_DAY) + ":" + photoLastModifiedDate.get(Calendar.MINUTE) + ":" + photoLastModifiedDate.get(Calendar.SECOND) + ".jpg";
                File newfile = new File(File);
                try {
                    newfile.createNewFile();
                } catch (IOException e) {
                }
                Uri outputFileUri = Uri.fromFile(newfile);
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
                startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);
            }
        });

    }


    private void showFileChooser() {


        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                // Compress image to lower quality scale 1 - 300
                int nh = (int) (bitmap.getHeight() * (512.0 / bitmap.getWidth()));
                toyImageScaledd = Bitmap.createScaledBitmap(bitmap, 200, 200, false);
                roundedImage = new RoundImage(toyImageScaledd);
                imageView.setImageDrawable(roundedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            flag = false;
            buttonUpload.setEnabled(true);
        }
        if (requestCode == TAKE_PHOTO_CODE && resultCode == RESULT_OK) {
            //SET IMAGEPREVIEW AND Compress IF REQUIRED
            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Hatchs/";
            //tring file = dir + photoLastModifiedDate.get(Calendar.DATE)+"-"+(photoLastModifiedDate.get(Calendar.MONTH)+1) +"-"+ photoLastModifiedDate.get(Calendar.YEAR) +" " + photoLastModifiedDate.get(Calendar.HOUR_OF_DAY) +":"+ photoLastModifiedDate.get(Calendar.MINUTE) + photoLastModifiedDate.get(Calendar.SECOND) +".jpg";
            // String file = dir + count + ".jpg";
            File newfile = new File(File);
            Uri outputFileUri = Uri.fromFile(newfile);
            try {
                bitmapp = MediaStore.Images.Media.getBitmap(getContentResolver(), outputFileUri);
                if (bitmapp.getHeight() > GL10.GL_MAX_TEXTURE_SIZE) {
                    // Compress image to lower quality scale 1 - 300
                    int nh = (int) (bitmapp.getHeight() * (512.0 / bitmapp.getWidth()));
                    toyImageScaled = Bitmap.createScaledBitmap(bitmapp, 200, 200, false);
                    roundedImage = new RoundImage(toyImageScaled);
                    imageView.setImageDrawable(roundedImage);
                } else {
                    // Compress image to lower quality scale 1 - 300
                    int nh = (int) (bitmapp.getHeight() * (512.0 / bitmapp.getWidth()));
                    toyImageScaled = Bitmap.createScaledBitmap(bitmapp, 200, 200, false);
                    roundedImage = new RoundImage(toyImageScaled);
                    imageView.setImageDrawable(roundedImage);
                }
                //REFRESHING THE ANDROID PHONE PHOTO IS BEGUN
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(newfile)));
                //REFRESHING THE ANDROID PHONE PHOTO IS COMPLETE
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        buttonUpload.setEnabled(true);
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage() {
        class UploadImage extends AsyncTask<Bitmap, Void, String> {
            ProgressDialog loading;
            RequestHandler rh = new RequestHandler();

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(imageUpload.this, "Uploading Image", "Please wait...", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
                Intent intentProfile = new Intent(getBaseContext(), ProfileActivity.class);
                intentProfile.putExtra("username", username);
                intentProfile.putExtra("distanceSetting", userDistanceSetting);
                intentProfile.putExtra("categories", userCategoriesSetting);
                intentProfile.putExtra("User_Id", User_Id);
                startActivity(intentProfile);
                finish();
            }

            @Override
            protected String doInBackground(Bitmap... params) {
                Bitmap bitmap = params[0];

                String uploadImage = getStringImage(bitmap);
                HashMap<String, String> data = new HashMap<>();
                data.put(UPLOAD_KEY, uploadImage);
                data.put("userid", String.valueOf(User_Id));

                String result = rh.sendPostRequest(UPLOAD_URL, data);

                return result;


            }
        }

        UploadImage ui = new UploadImage();
        //ESTE CONDICIONAL SEPARA  FALSE SI SE ELIGIO DESDE GALERIA O TRUE SI SE CAPTURO CON CAMARA, ya que los BITMAPS son diferentes
        if (flag == false) {
            ui.execute(toyImageScaledd);
        } else {
            ui.execute(toyImageScaled);
        }
    }


    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            showFileChooser();
        }
        if (v == buttonUpload) {
            if (imageView.getDrawable() == null) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(imageUpload.this);
                dialogBuilder.setMessage("Por favor cargar una foto previamente via Camara o Galeria");
                dialogBuilder.setPositiveButton("Ok", null);
                dialogBuilder.show();
            }

               /* if (imageView == imageViewDefault) ;
                {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(imageUpload.this);
                    dialogBuilder.setMessage("Por favor cargar una foto previamente via Camara o Galeria");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }
                */

            else {
                uploadImage();
            }

    }

       // if(v == buttonView){
       //     viewImage();
       // }

    }
    private void viewImage() {
        startActivity(new Intent(this, download_image.class));
    }



}