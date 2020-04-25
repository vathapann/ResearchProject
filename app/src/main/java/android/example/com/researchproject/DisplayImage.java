package android.example.com.researchproject;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

public class DisplayImage extends AppCompatActivity {
ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imageView = findViewById(R.id.imageView3);
        Bitmap bitmap = BitmapFactory.decodeFile(getIntent().getStringExtra("image_paths"));
        imageView.setImageBitmap(bitmap);
    }
}
