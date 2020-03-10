package uit.vinh.kk;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.content.res.AssetManager;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.util.Util;
import com.github.chrisbanes.photoview.PhotoView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog dialogLoadActivity = null;
    private float scale = 1;
    static Bitmap bitmapOriginalImage = null;
    static Bitmap bitmapContrastEnhance = null;
    private String imagePath;
    // presets for rgb conversion
    private static final int RESULTS_TO_SHOW = 3;
    private static final int IMAGE_MEAN = 128;
    private static final float IMAGE_STD = 128.0f;
    private boolean quant = false;
    // input image dimensions for the Inception Model
    private int DIM_IMG_SIZE_X = 224;
    private int DIM_IMG_SIZE_Y = 224;
    private int DIM_PIXEL_SIZE = 3;
    // holds the selected image data as bytes
    private ByteBuffer imgData = null;
    // int array to hold image data
    private int[] intValues;
    protected TextView recognitionTextView,
            recognition1TextView,
            recognition2TextView,
            recognition3TextView,
            recognition4TextView,
            recognitionValueTextView,
            recognition1ValueTextView,
            recognition2ValueTextView,
            recognition3ValueTextView,
            recognition4ValueTextView;
    private PhotoView photoView;
    private Button buttonSave;
    private Bitmap rgbFrameBitmap = null;
    private Classifier classifier;
    private Integer sensorOrientation;

    static {
        if (!OpenCVLoader.initDebug())
            Log.d("...", "OpenCv load fail!");
        else
            Log.d("...", "OpenCv success.");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize array that holds image data
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        setContentView(R.layout.activity_classificationresult);

        photoView = findViewById(R.id.photoview);
        buttonSave = findViewById(R.id.buttonSaveData);
        buttonSave.setOnClickListener(this);
        rgbFrameBitmap = Bitmap.createBitmap(640, 480, Bitmap.Config.ARGB_8888);
        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        recognitionTextView = findViewById(R.id.detected_item);
        recognitionValueTextView = findViewById(R.id.detected_item_value);
        recognition1TextView = findViewById(R.id.detected_item1);
        recognition1ValueTextView = findViewById(R.id.detected_item1_value);
        recognition2TextView = findViewById(R.id.detected_item2);
        recognition2ValueTextView = findViewById(R.id.detected_item2_value);
        recognition3TextView = findViewById(R.id.detected_item3);
        recognition3ValueTextView = findViewById(R.id.detected_item3_value);
        recognition4TextView = findViewById(R.id.detected_item4);
        recognition4ValueTextView = findViewById(R.id.detected_item4_value);
        recognitionTextView.setText("Level 0");
        recognition1TextView.setText("Level 1");
        recognition2TextView.setText("Level 2");
        recognition3TextView.setText("Level 3");
        recognition4TextView.setText("Level 4");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imagePath = (String) getIntent().getSerializableExtra("ImagePath");


        dialogLoadActivity = ProgressDialog.show(this, "Please wait", "Processing...", true);
        new Thread() {
            @Override
            public void run() {
                try {
                    bitmapOriginalImage = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse("file://" + imagePath));
                    if (bitmapOriginalImage != null) {
                        if (bitmapOriginalImage.getHeight() > CONSTANTS.MAX_HEIGHT || bitmapOriginalImage.getWidth() > CONSTANTS.MAX_WIDTH) {
                            Log.d("debug", "onCreate: original size " + bitmapOriginalImage.getWidth() + "---" + bitmapOriginalImage.getHeight());
                            scale = (float) 1.0;
                            for (float i = 0; i <= 1; i = i + (float) 0.05) {
                                if (bitmapOriginalImage.getWidth() * i <= CONSTANTS.MAX_WIDTH && bitmapOriginalImage.getHeight() * i <= CONSTANTS.MAX_HEIGHT) {
                                    scale = i;
                                }
                            }
                            Log.d("debug", "onCreate: selected scale == " + scale);
                            bitmapOriginalImage = Bitmap.createScaledBitmap(bitmapOriginalImage, 224, 224, false);

                            Log.d("debug", "run: bitmap contrast enhance is " + bitmapContrastEnhance);
                        }
                        bitmapOriginalImage = Bitmap.createScaledBitmap(bitmapOriginalImage, 224, 224, false);
                        bitmapContrastEnhance = contrastEnhance(bitmapOriginalImage);
                        Log.d("debug", "onCreate: bitmap is not null");
                        Log.d("debug", "onCreate: new bitmaporiginal size " + bitmapContrastEnhance.getWidth() + "---" + bitmapContrastEnhance.getHeight());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {

                    // code runs in a thread
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            photoView.setImageBitmap(bitmapOriginalImage);
                            Classify();
                            dialogLoadActivity.dismiss();
                        }
                    });
                } catch (final Exception ex) {

                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back_menuitem, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ResultActivity.super.onBackPressed();
                return true;
            case R.id.contrastenhance:
                if (bitmapOriginalImage != null) {
                    photoView.setImageBitmap(bitmapContrastEnhance);
                }

                break;
            case R.id.originalimage:
                if (bitmapOriginalImage != null) {
                    photoView.setImageBitmap(bitmapOriginalImage);
                }
                //photoView.setImageURI(uri);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap contrastEnhance(Bitmap bitmapsrc) {
        Mat image = new Mat();
        Mat matsrc = new Mat();
        Mat matdest = new Mat();
        Mat gaussianBlurSrc = new Mat();
        Bitmap bpm32 = bitmapsrc.copy(Bitmap.Config.ARGB_8888, true);
        Utils.bitmapToMat(bpm32, matsrc);
        Imgproc.cvtColor(matsrc, image, Imgproc.COLOR_BGR2RGB);
        org.opencv.core.Size s = new Size(0, 0);
        Imgproc.GaussianBlur(image, gaussianBlurSrc, new Size(0,0), 10);
        Core.addWeighted(image, 4, gaussianBlurSrc, -4, 128, matdest);
        Bitmap bitmapdest = Bitmap.createBitmap(matdest.cols(), matdest.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matdest, bitmapdest);
        return bitmapdest;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonSaveData) {
            Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
            String temp = (String) getIntent().getSerializableExtra("ImagePath");

            intent.putExtra("ImagePath", temp);
            intent.putExtra("scalevalue", scale);
            intent.putExtra("Save As", CONSTANTS.SAVE_AS_MODE_NEW);
            startActivity(intent);

        }
    }


    private void Classify() {
        // get current bitmap from photoView
        Bitmap bitmap_orig = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        Bitmap bitmap_Enhance = contrastEnhance(bitmap_orig);
        Log.d("vindeptrai", "Classify: bitmap_orig  == " + bitmap_Enhance);
        bitmap_Enhance = Bitmap.createScaledBitmap(bitmap_Enhance, (int) (224), (int) (224), false);
        Log.d("vindeptrai", "Classify: bitmap_origin height == " + bitmap_Enhance.getHeight());
        Log.d("vindeptrai", "Classify: bitmap_origin width == " + bitmap_Enhance.getWidth());
        // trường hợp nếu photoView không có ảnh: -> 5 giá trị xác suất đều là 0%


        final List<Classifier.Recognition> results =
                classifier.recognizeImage(bitmap_Enhance);
        Log.d("classify result", results.get(0).toString() + results.get(1).toString() + results.get(2).toString() + results.get(3).toString() + results.get(4).toString());
        showResultsInBottomSheet(results);
    }

    protected void showResultsInBottomSheet(List<Classifier.Recognition> results) {
        if (results != null && results.size() >= 5) {

            Classifier.Recognition recognition = results.get(0);
            if (recognition != null) {
                if (recognition.getTitle() != null)
                    recognitionTextView.setText(recognition.getTitle());
                if (recognition.getConfidence() != null){
                    if (recognition.getConfidence() >= CONSTANTS.THRESHOLD){
//                        recognitionValueTextView.setText("Yes");
                        recognitionValueTextView.setText(recognition.getConfidence().toString());
                    }
                    else{
//                        recognitionValueTextView.setText("No");
                        recognitionValueTextView.setText(recognition.getConfidence().toString());
                    }
                }
            }

            Classifier.Recognition recognition1 = results.get(1);
            if (recognition1 != null) {
                if (recognition1.getTitle() != null)
                    recognition1TextView.setText(recognition1.getTitle());
                if (recognition1.getConfidence() != null){
                    if (recognition1.getConfidence() >= CONSTANTS.THRESHOLD){
//                        recognition1ValueTextView.setText("Yes");
                        recognition1ValueTextView.setText(recognition1.getConfidence().toString());
                    }
                    else{
//                        recognition1ValueTextView.setText("No");
                        recognition1ValueTextView.setText(recognition1.getConfidence().toString());
                    }
                }
            }

            Classifier.Recognition recognition2 = results.get(2);
            if (recognition2 != null) {
                if (recognition2.getTitle() != null)
                    recognition2TextView.setText(recognition2.getTitle());
                if (recognition2.getConfidence() != null){
                    if (recognition2.getConfidence() > CONSTANTS.THRESHOLD){
//                        recognition2ValueTextView.setText("Yes");
                        recognition2ValueTextView.setText(recognition2.getConfidence().toString());
                    }
                    else{
//                        recognition2ValueTextView.setText("No");
                        recognition2ValueTextView.setText(recognition2.getConfidence().toString());
                    }
                }
            }

            Classifier.Recognition recognition3 = results.get(3);
            if (recognition3 != null) {
                if (recognition3.getTitle() != null)
                    recognition3TextView.setText(recognition3.getTitle());
                if (recognition3.getConfidence() != null){
                    if (recognition3.getConfidence() >= CONSTANTS.THRESHOLD){
//                        recognition3ValueTextView.setText("Yes");
                        recognition3ValueTextView.setText(recognition3.getConfidence().toString());
                    }
                    else{
//                        recognition3ValueTextView.setText("No");
                        recognition3ValueTextView.setText(recognition3.getConfidence().toString());
                    }
                }
            }

            Classifier.Recognition recognition4 = results.get(4);
            if (recognition4 != null) {
                if (recognition4.getTitle() != null)
                    recognition4TextView.setText(recognition4.getTitle());
                if (recognition4.getConfidence() != null){
                    if (recognition4.getConfidence() >= CONSTANTS.THRESHOLD){
//                        recognition4ValueTextView.setText("Yes");
                        recognition4ValueTextView.setText(recognition4.getConfidence().toString());
                    }
                    else{
//                        recognition4ValueTextView.setText("No");
                        recognition4ValueTextView.setText(recognition4.getConfidence().toString());
                    }
                }
            }
        }
    }
}
