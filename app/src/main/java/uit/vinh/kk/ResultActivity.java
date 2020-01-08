package uit.vinh.kk;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private  Uri uri;
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
    Button buttonSave;
    private Bitmap rgbFrameBitmap = null;
    private Classifier classifier;
    private Integer sensorOrientation = 0;
    static {
        if(!OpenCVLoader.initDebug())
            Log.d("...", "OpenCv load fail!");
        else
            Log.d("...", "OpenCv success.");
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // initialize array that holds image data
        intValues = new int[DIM_IMG_SIZE_X * DIM_IMG_SIZE_Y];
        setContentView(R.layout.activity_classificationresult);
        photoView = findViewById(R.id.photoview);
        buttonSave = findViewById(R.id.buttonSaveData);
        buttonSave.setOnClickListener(this);
        rgbFrameBitmap = Bitmap.createBitmap(640,480,Bitmap.Config.ARGB_8888);
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
        uri = (Uri)getIntent().getParcelableExtra("imageURI");
        photoView.setImageURI(uri);

        // put function classify here
        Classify();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back_menuitem, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                ResultActivity.super.onBackPressed();
                return true;
            case R.id.contrastenhance:
                Bitmap contrastEnhnaceBitmap = contrastEnhance(((BitmapDrawable)photoView.getDrawable()).getBitmap());
                photoView.setImageBitmap(contrastEnhnaceBitmap);
                break;
            case R.id.originalimage:
                photoView.setImageURI(uri);
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap contrastEnhance(Bitmap bitmapsrc){
        // convert Color bitmapsrc to RGB
        Mat image  = new Mat();
        Mat matsrc = new Mat();
        Mat matdest = new Mat();
        Mat gaussianBlurSrc = new Mat();
        Bitmap bpm32 = bitmapsrc.copy(Bitmap.Config.ARGB_8888,true);
        Utils.bitmapToMat(bpm32,matsrc);
        Imgproc.cvtColor(matsrc,image, Imgproc.COLOR_BGR2RGB);
        org.opencv.core.Size s = new Size(0,0);
        Imgproc.GaussianBlur(matsrc,gaussianBlurSrc,s,10);
        Core.addWeighted(matsrc,4,gaussianBlurSrc,-4,128,matdest);
        Utils.matToBitmap(matdest,bitmapsrc);
        return bitmapsrc;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonSaveData){
            // gửi 1 tín hiệu cho biết chuyển từ màn hình result
            ProgressDialog progress = new ProgressDialog(this);
            progress.setTitle("Processing");
            progress.setMessage("...");
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();


            Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
            Uri URI_OriginalImage = (Uri)getIntent().getParcelableExtra("imageURI");
            intent.putExtra("URIOriginalImage", URI_OriginalImage);
            intent.putExtra("Save As", CONSTANTS.SAVE_AS_MODE_NEW);
            //getIntent().getSerializableExtra("Save As");

            //getIntent().getParcelableExtra("OriginalImage");
            progress.dismiss();
            startActivity(intent);
        }
    }

    // converts bitmap to byte array which is passed in the tflite graph
    private void convertBitmapToByteBuffer(Bitmap bitmap) {
        if (imgData == null) {
            return;
        }
        imgData.rewind();
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        // loop through all pixels
        int pixel = 0;
        for (int i = 0; i < DIM_IMG_SIZE_X; ++i) {
            for (int j = 0; j < DIM_IMG_SIZE_Y; ++j) {
                final int val = intValues[pixel++];
                // get rgb values from intValues where each int holds the rgb values for a pixel.
                // if quantized, convert each rgb value to a byte, otherwise to a float
                if(quant){
                    imgData.put((byte) ((val >> 16) & 0xFF));
                    imgData.put((byte) ((val >> 8) & 0xFF));
                    imgData.put((byte) (val & 0xFF));
                } else {
                    imgData.putFloat((((val >> 16) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val >> 8) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                    imgData.putFloat((((val) & 0xFF)-IMAGE_MEAN)/IMAGE_STD);
                }
            }
        }
    }

    private void Classify(){
        // get current bitmap from photoView
        Bitmap bitmap_orig = ((BitmapDrawable)photoView.getDrawable()).getBitmap();

        // convert bitmap to byte array
        convertBitmapToByteBuffer(bitmap_orig);
        final List<Classifier.Recognition> results =
                classifier.recognizeImage(bitmap_orig, sensorOrientation);
        Log.d("classify result", results.get(0).toString() + results.get(1).toString() + results.get(2).toString() + results.get(3).toString() + results.get(4).toString());
        showResultsInBottomSheet(results);
    }

    protected void showResultsInBottomSheet(List<Classifier.Recognition> results) {
        if (results != null && results.size() >= 5) {
            Classifier.Recognition recognition = results.get(0);
            if (recognition != null) {
                if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
                if (recognition.getConfidence() != null)
                    recognitionValueTextView.setText(
                            String.format("%.2f", (100 * recognition.getConfidence())) + "%");
            }

            Classifier.Recognition recognition1 = results.get(1);
            if (recognition1 != null) {
                if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
                if (recognition1.getConfidence() != null)
                    recognition1ValueTextView.setText(
                            String.format("%.2f", (100 * recognition1.getConfidence())) + "%");
            }

            Classifier.Recognition recognition2 = results.get(2);
            if (recognition2 != null) {
                if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
                if (recognition2.getConfidence() != null)
                    recognition2ValueTextView.setText(
                            String.format("%.2f", (100 * recognition2.getConfidence())) + "%");
            }

            Classifier.Recognition recognition3 = results.get(3);
            if (recognition3 != null) {
                if (recognition3.getTitle() != null) recognition3TextView.setText(recognition3.getTitle());
                if (recognition3.getConfidence() != null)
                    recognition3ValueTextView.setText(
                            String.format("%.2f", (100 * recognition3.getConfidence())) + "%");
            }

            Classifier.Recognition recognition4 = results.get(4);
            if (recognition4 != null) {
                if (recognition4.getTitle() != null) recognition4TextView.setText(recognition4.getTitle());
                if (recognition4.getConfidence() != null)
                    recognition4ValueTextView.setText(
                            String.format("%.2f", (100 * recognition4.getConfidence())) + "%");
            }
        }
    }
}
