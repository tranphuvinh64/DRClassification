package uit.vinh.kk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.esafirm.imagepicker.model.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView imageView;
    Button buttonSave;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classificationresult);
        imageView = findViewById(R.id.imageview_fundus);
        buttonSave = findViewById(R.id.buttonSaveData);
        buttonSave.setOnClickListener(this);
//        Toolbar toolbar = findViewById(R.id.displaypatientinfo);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Drawable drawable= getResources().getDrawable(android.R.drawable.ic_menu_search);
//        getSupportActionBar().setHomeAsUpIndicator(drawable);

        Log.d("debug", "Here result activity");
        Image imageTest = (Image)getIntent().getParcelableExtra("imagetest");
        //convert to base64 string
        File file = new File("/storage/emulated/0/Download/63_left.jpg");
        FileInputStream imageInFile = null;
        try {
            imageInFile = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte imageData[] = new byte[(int) file.length()];
        try {
            imageInFile.read(imageData);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converting Image byte array into Base64 String
        String encodedImage = Base64.encodeToString(imageData, Base64.DEFAULT);
        //String imageDataString = Base64.encodeToString(imageData,Base64.DEFAULT);
        Log.d("encode string: ", encodedImage);
        Log.d("len of string: ", ""+encodedImage.length());
        imageView.setImageURI(Uri.parse(imageTest.getPath()));
        Log.d("recieved image type", imageTest.getClass().getName());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back_menuitem, menu);
        //getMenuInflater().inflate(R.menu.menu_back_forward, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                ResultActivity.super.onBackPressed();
                //backtoHomeScreen();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void backtoHomeScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSaveData){
            // gửi 1 tín hiệu cho biết chuyển từ màn hình result

            Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
            startActivity(intent);
        }
    }
}
