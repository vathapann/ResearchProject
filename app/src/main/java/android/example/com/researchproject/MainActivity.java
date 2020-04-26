package android.example.com.researchproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
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

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private static final int IMAGE_REQUEST = 1; // for camera intent
    private static final int PICK_IMAGE_REQUEST = 2; // for select picture from gallery
    String currentImagePath = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_setting)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
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


    public void selectPicture(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // If the result of camera
        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK){

            //Bundle extras = data.getExtras();
           // Bitmap bitmap = (Bitmap) extras.get("data");
           // mImageView.setImageBitmap(bitmap);

            ImageView imageView = findViewById(R.id.imageView);
            Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);

            imageView.setImageBitmap(bitmap);
            //imageView.animate().rotation(90).setDuration(1);


        }
        // if the result of image picker
        else if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST ) {
            try
            {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
              //  List<Classifier.Recognition> results = analyse(selectedImage);
               // TV1.setText(results.get(0).toString());
               // setPicture(selectedImage);

                ImageView imageView = findViewById(R.id.imageView);
                //Bitmap bitmap = BitmapFactory.decodeFile(currentImagePath);

                imageView.setImageBitmap(selectedImage);
               // imageView.animate().rotation(90).setDuration(1);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    //----- No need to use viewPicture activity to display image
/*
    public void DisplayImage(View view){
        Intent intent = new Intent(this, DisplayImage.class);
    intent.putExtra("image_paths",currentImagePath);
    startActivity(intent);
    }
    */



// function to upload the image from camera or from image picker to the server
    public void uploadPicture(View view) {
        Log.i("uploadImage button: ", "Clicked");
    }
}
