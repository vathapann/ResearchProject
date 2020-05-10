package android.example.com.researchproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.example.com.researchproject.ui.camera.CameraFragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.example.com.researchproject.util.Constants.ERROR_DIALOG_REQUEST;
import static android.example.com.researchproject.util.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static android.example.com.researchproject.util.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class MainActivity extends AppCompatActivity  {
    private ImageView mImageView;
    private static final int IMAGE_REQUEST = 1; // for camera intent
    private static final int PICK_IMAGE_REQUEST = 2; // for select picture from gallery
    String currentImagePath = null;
    private static final String TAG = "MainActivity";


    // Firebase
    StorageReference mStorageRef;
    public Uri imgUri;
    private StorageTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageRef = FirebaseStorage.getInstance().getReference("Images");

        if(savedInstanceState == null){
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_setting,  R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        Toast.makeText(this , "Saved Last Instance is null", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this , "Saved Last Instance is non null", Toast.LENGTH_LONG).show();
        };
    checkCurrentUser();

    }

    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Toast.makeText(MainActivity.this, "User is signed in", Toast.LENGTH_LONG).show();
        } else {
            // No user is signed in
            Toast.makeText(MainActivity.this, "No user is signed in", Toast.LENGTH_LONG).show();
            Intent FirebaseUIActivity = new Intent(MainActivity.this, android.example.com.researchproject.FirebaseUIActivity.class);
            startActivity(FirebaseUIActivity);
        }
        // [END check_current_user]
    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Toast.makeText(getApplicationContext(), "Main onStop called", Toast.LENGTH_LONG).show();
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Toast.makeText(getApplicationContext(), "Main onResume called", Toast.LENGTH_LONG).show();
//
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        Toast.makeText(getApplicationContext(), "Main onPause called", Toast.LENGTH_LONG).show();
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the result of camera
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){
            // data.getData() is null

    //Toast.makeText(MainActivity.this,data.getData().toString(),Toast.LENGTH_LONG).show();
         //   Log.i("data.getData(): ", data.getData().toString());

            ImageView imageView = findViewById(R.id.imageView);

            imageView.setImageURI(Uri.fromFile(new File(currentImagePath)));
           // Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);
            //imageView.setImageBitmap(bitmap);

            //imageView.animate().rotation(90).setDuration(1);
            //   galleryAddPic(); // add picture that was taken to be available to the gallery app


        }
        // if the result of image picker
        else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST && data!=null && data.getData()!=null ) {
            try
            {
                imgUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imgUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //  List<Classifier.Recognition> results = analyse(selectedImage);
                // TV1.setText(results.get(0).toString());
                // setPicture(selectedImage);

                ImageView imageView = findViewById(R.id.imageView);
                //Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);

                imageView.setImageBitmap(selectedImage);
                // imageView.animate().rotation(90).setDuration(1);

                // or
//                imgUri = data.getData();
//            imageView.setImageURI(imgUri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
//        else if(requestCode == PICK_IMAGE_REQUEST && resultCode ==  RESULT_OK && data != null && data.getData() != null){
//
//        }
//
    }


    public void takePicture(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) !=null){
            File imageFile = null;
            Log.i("1", "imageFile null");
            try {
                imageFile = getImageFile();
                Log.i("2", "imageFile created");
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(imageFile != null){
                Log.i("3", "imageFile is not null");
                Uri imageUri = FileProvider.getUriForFile(this,"com.example.android.fileprovider",imageFile);
                Log.i("4", "imageUri created");
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                Log.i("5", "cameraIntent put extra");
                startActivityForResult(cameraIntent,IMAGE_REQUEST);
                Log.i("6", "activity started");
            }
        }
    }

    private File getImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        Log.i("timeStamp", timeStamp);
        String imageName = "jpg_"+ timeStamp+"_"; //image name
        Log.i("imageName", imageName);

         File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStoragePublicDirectory(
              //  Environment.DIRECTORY_PICTURES);
        Log.i("storageDir", storageDir.toString());

        // new method
/*
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        Log.i("Testting!!!!! 11" ,mediaStorageDir.toString() );
        File imageFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
        currentImagePath = imageFile.getAbsolutePath();
        //
*/


        //create image file
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        currentImagePath =  imageFile.getAbsolutePath();
        Log.i("currentImagePath", currentImagePath.toString());
        Log.i("Image File: ", imageFile.toString());
        Log.i("Testting!!!!!" , Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString());
        galleryAddPic(); // add picture that was taken to be available to the gallery app

        return imageFile;
    }

// This method calls the method recognizeImage to analyze the picture. The results are stored in a list.

   /*
    public List<Classifier.Recognition> analyse(Bitmap bitmap) {
        bitmap = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, false);
        final List<Classifier.Recognition> results = classifier.recognizeImage(bitmap);
        return results;
    }

    */





    //----- No need to use viewPicture activity to display image
/*
    public void DisplayImage(View view){
        Intent intent = new Intent(this, DisplayImage.class);
    intent.putExtra("image_paths",currentImagePath);
    startActivity(intent);
    }
    */


    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentImagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        Log.i("Save to Gallery: ", mediaScanIntent.toString());
    }

    // Browse Images from gallery
    public void selectPicture(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }

// function to upload the image from camera or from image picker to the server
    public void uploadPicture(View view) {
        Log.i("uploadImage button: ", "Clicked");

        if(uploadTask!=null && uploadTask.isInProgress()){
            Toast.makeText(MainActivity.this, "Upload is in progress", Toast.LENGTH_LONG).show();
        }else {
            FileUploader();
        }
    }


    private void FileUploader(){

        StorageReference Ref = mStorageRef.child(System.currentTimeMillis()+ "."+ getExtension(imgUri));
        Log.i("getExtension: ", getExtension(imgUri).toString());

        uploadTask = Ref.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        // Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(MainActivity.this,"Image Uploaded success",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }



    private String getExtension(Uri uri){
        ContentResolver ContentResolv = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(ContentResolv.getType(uri));
    }
}