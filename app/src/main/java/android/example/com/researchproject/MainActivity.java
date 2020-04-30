package android.example.com.researchproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView mImageView;
    private static final int IMAGE_REQUEST = 1; // for camera intent
    private static final int PICK_IMAGE_REQUEST = 2; // for select picture from gallery
    String currentImagePath = null;
    private static final String TAG = "MainActivity";

    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;

    // map

    private boolean mLocationPermissionGranted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_camera, R.id.navigation_setting,  R.id.navigation_map)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

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
            //   galleryAddPic(); // add picture that was taken to be available to the gallery app


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


    public void selectPicture(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST);
    }


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

// function to upload the image from camera or from image picker to the server
    public void uploadPicture(View view) {
        Log.i("uploadImage button: ", "Clicked");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //MapsInitializer.initialize(getContext());
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.i("Location Manager: ", locationManager.toString());

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.i("Location", location.toString());
                mMap.clear();

                LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                Log.i("userLocation", location.toString());
                mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(Build.VERSION.SDK_INT < 23){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);

            }
        }

    }


    // MaP

//
//
//    private boolean checkMapServices(){
//        if(isServicesOK()){
//            if(isMapsEnabled()){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public boolean isMapsEnabled(){
//        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
//
//        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
//            buildAlertMessageNoGps();
//            return false;
//        }
//        return true;
//    }
//
//
//
//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//           // getChatrooms();
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    public boolean isServicesOK(){
//        Log.d(TAG, "isServicesOK: checking google services version");
//
//        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);
//
//        if(available == ConnectionResult.SUCCESS){
//            //everything is fine and the user can make map requests
//            Log.d(TAG, "isServicesOK: Google Play Services is working");
//            return true;
//        }
//        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
//            //an error occured but we can resolve it
//            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
//            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available, ERROR_DIALOG_REQUEST);
//            dialog.show();
//        }else{
//            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }
//
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLocationPermissionGranted = true;
//                }
//            }
//        }
//    }
//
//    private void buildAlertMessageNoGps() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
//                .setCancelable(false)
//                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
//                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
//                    }
//                });
//        final AlertDialog alert = builder.create();
//        alert.show();
//    }

}
