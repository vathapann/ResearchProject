package android.example.com.researchproject.ui.dashboard;

import android.content.Context;
import android.example.com.researchproject.BuildConfig;
import android.example.com.researchproject.MainActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
//-----
import android.content.Intent;
import android.provider.MediaStore;
import android.content.pm.PackageManager;

import android.example.com.researchproject.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CAMERA_SERVICE;
import static androidx.core.content.FileProvider.getUriForFile;

public class DashboardFragment extends Fragment {

    private DashboardViewModel dashboardViewModel;
    static final int REQUEST_CAPTURE_IMAGE = 1;

// saving media files
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    Uri fileUri ;


    public ImageView mImageView ;
    String pathToFile;
    String imageFilePath;
    Context context;
    FragmentActivity listener;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity){
            this.listener = (FragmentActivity) context;
        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //this.getActivity().setContentView(R.layout.fragment_dashboard);
        // Getting application context
        //context = this.getContext();

    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =
                ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        mImageView = root.findViewById(R.id.imageView);

//        root.findViewById(R.id.CameraButton).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //dispatchTakePictureIntent();
//                openCameraIntent();
//
//            }
//        });

        dashboardViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);

            }
        });
        return root;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // Setup any handles to view objects here
        // EditText etFoo = (EditText) view.findViewById(R.id.etFoo);

        //mImageView = view.findViewById(R.id.imageView);
        view.findViewById(R.id.CameraButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dispatchTakePictureIntent();
                openCameraIntent();

            }
        });
    }





    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }





    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
              getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);



        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.d("storageDir 1" , storageDir.toString());

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }








    // intent default camera app
//    private void openCameraIntent() {
//        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//        if(pictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
//        }
//    }




//
//    private void openCameraIntent() {
//        Intent pictureIntent = new Intent(
//                MediaStore.ACTION_IMAGE_CAPTURE);
//        if(pictureIntent.resolveActivity(Objects.requireNonNull(this.getActivity()).getPackageManager()) != null){
//            //Create a file to store the image
//            File photoFile = null;
//            try {
//                photoFile = createImageFile();
//               // photoFile = createPhotoFile();
//            } catch (IOException ex) {
//                // Error occurred while creating the File
//
//            }
//            if (photoFile != null) {
//              Log.d("context test1" , this.getContext().toString());
//                Log.d("context test2" , getContext().toString());
//                Log.d("context test3" , getActivity().toString());
//                Log.d("context test4" , listener.toString());
//                Log.d("context test5" , BuildConfig.APPLICATION_ID+".provider");
//               // Log.d("context test6" , ${applicationId}.provider);
//
//                Uri photoURI = getUriForFile(getContext(), "com.example.asd.fileprovider" ,photoFile);
//                Log.d("UriPhoto" , photoURI.toString());
//
//                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
//                        photoURI);
//                startActivityForResult(pictureIntent,
//                        REQUEST_CAPTURE_IMAGE);
//                Log.d("Display Picture" , pictureIntent.toString());
//            }
//        }
//
//    }


    private void dispatchTakePictureIntent() {
       // PackageManager pm = getActivity().getPackageManager();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            photoFile = createPhotoFile();
            if (photoFile != null) {
                pathToFile = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(getActivity(), "fssdfs", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }


            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            getActivity().startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);


            // }
            // }

            private File createPhotoFile () {
                String name = new SimpleDateFormat("yyyymm_HHmmss").format(new Date());
                File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
                File image = null;
                try {
                    image = File.createTempFile(name, ".jpg", storageDir);

                } catch (IOException e) {
                    Log.d("mylog", "Excep: " + e.toString());
                }
                return image;

            }


            //----------


            /** Create a file Uri for saving an image or video */
            private static Uri getOutputMediaFileUri ( int type){
                return Uri.fromFile(getOutputMediaFile(type));
            }


            @Override
            public void onActivityResult ( int requestCode, int resultCode,
            Intent data){
                if (requestCode == REQUEST_CAPTURE_IMAGE &&
                        resultCode == RESULT_OK) {
                    if (data != null && data.getExtras() != null) {
                        Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                        mImageView.setImageBitmap(imageBitmap);
                    }
                }
            }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bitmap bitmap = BitmapFactory.decodeFile(pathToFile);
//            image.setImageBitmap(bitmap);
//


//            Bundle extras = data.getExtras();
//           // getOutputMediaFile();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            image.setImageURI(fileUri);
//            //image = (ImageView) root.findViewById(R.id.imageView)
//            image.setImageBitmap(imageBitmap);
//            System.out.printf("hi");

            //}
            //}

        }

