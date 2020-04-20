package android.example.com.researchproject;

import android.content.Intent;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ImageView mImageView;
    private static final int IMAGE_REQUEST = 1;
    String currentImagePath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
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

        //create image file
        File imageFile = File.createTempFile(imageName,".jpg",storageDir);
        currentImagePath =  imageFile.getAbsolutePath();
        Log.i("currentImagePath", currentImagePath.toString());
        Log.i("Image File: ", imageFile.toString());

        return imageFile;
    }
}
