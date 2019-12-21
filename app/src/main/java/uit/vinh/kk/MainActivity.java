package uit.vinh.kk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import uit.vinh.kk.Classifier.Recognition;
//import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ArrayList<DataModel> dataModels;
    ListView listView;
    private static CustomAdapter adapter;
    private String TAG = "debug";
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
    LinearLayout browseImageLinearLayout, infoLinearLayout, loadDataLinearLayout;
    FloatingActionButton floatingActionButtonNew, floatingActionButtonBrowse, floatingActionButtonInfo, floatingActionButtonLoadData;
    Animation fabOpen, fabClose, rotateBackward, rotateForward;
    boolean isOpen = false;
    private static final int RC_CAMERA = 3000;

    private Button btnImport, btnClassify;
    private ImageView imgImport;
    private static final Logger LOGGER = new Logger();
    // private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);
    private static final float TEXT_SIZE_DIP = 10;
    private Bitmap rgbFrameBitmap = null;

    private long lastProcessingTimeMs;
    private Integer sensorOrientation = 0;
    private Classifier classifier;
    // private BorderedText borderedText;
    protected int previewWidth = 0;
    protected int previewHeight = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // temp
        rgbFrameBitmap = Bitmap.createBitmap(640,480,Bitmap.Config.ARGB_8888);

        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initial components
        setContentView(R.layout.activity_main);


        btnClassify = findViewById(R.id.btnClassify);
        imgImport = findViewById(R.id.imgImport);
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


        browseImageLinearLayout = findViewById(R.id.browseimage_linear_layout);
        infoLinearLayout = findViewById(R.id.info_linear_layout);
        loadDataLinearLayout = findViewById(R.id.loaddata_linear_layout);

        floatingActionButtonNew =findViewById(R.id.floating_action_button);
        floatingActionButtonBrowse = findViewById(R.id.browseimage_floating_action_button);
        floatingActionButtonInfo = findViewById(R.id.info_floating_action_button);
        floatingActionButtonLoadData = findViewById(R.id.loaddata_floating_action_button);

        // set action listener
        btnClassify.setOnClickListener(this);
        floatingActionButtonNew.setOnClickListener(this);
        floatingActionButtonBrowse.setOnClickListener(this);
        floatingActionButtonInfo.setOnClickListener(this);
        floatingActionButtonLoadData.setOnClickListener(this);

        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);

        if(listView == null){
            Log.d(TAG, "onCreate 1: Null cmnr");
        }
        listView=findViewById(R.id.list);

        if(listView == null){
            Log.d(TAG, "onCreate 2: Null cmnr");
        }
        Log.d(TAG, "Here 6");

        dataModels= new ArrayList<>();
        ArrayList<Form> listForm = loadXMLData("//storage//emulated//0//patientData");
        for(int i = 0; i < listForm.size(); i++){
            Log.d(TAG, i + " loadXMLData: " + listForm.get(i).toString());
            String tempname = listForm.get(i).getName();
            String tempdob = listForm.get(i).getDateOfBirth();
            String tempmedhis = listForm.get(i).getMedicalHistory();
            String temppersonalid = listForm.get(i).getPersonalID();
            dataModels.add(new DataModel(tempname,temppersonalid,tempdob,tempdob + " - " + tempmedhis));
        }




        Log.d(TAG, "Here 7");
        dataModels.add(new DataModel("Apple Pie", "Android 1.0", "1","September 23, 2008"));
        dataModels.add(new DataModel("Banana Bread", "Android 1.1", "2","February 9, 2009"));

        Log.d(TAG, "Here 3");
        adapter= new CustomAdapter(dataModels,getApplicationContext());
        Log.d(TAG, "Here 4");
        listView.setAdapter(adapter);
        Log.d(TAG, "Here 5");
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//                DataModel dataModel= dataModels.get(position);
//
//                Snackbar.make(view, dataModel.getName()+"\n"+dataModel.getType()+" API: "+dataModel.getVersion_number(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//            }
//        });
    }

    private void animateFAB(){
        if(isOpen){
            floatingActionButtonNew.startAnimation(rotateBackward);

            browseImageLinearLayout.startAnimation(fabClose);
            infoLinearLayout.startAnimation(fabClose);
            loadDataLinearLayout.startAnimation(fabClose);

            floatingActionButtonBrowse.setClickable(false);
            floatingActionButtonInfo.setClickable(false);
            floatingActionButtonLoadData.setClickable(false);
            isOpen = false;
        }
        else
        {
            floatingActionButtonNew.startAnimation(rotateForward);

            browseImageLinearLayout.startAnimation(fabOpen);
            infoLinearLayout.startAnimation(fabOpen);
            loadDataLinearLayout.startAnimation(fabOpen);

            floatingActionButtonBrowse.setClickable(true);
            floatingActionButtonInfo.setClickable(true);
            floatingActionButtonLoadData.setClickable(true);
            isOpen = true;
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.btnClassify){
            processImage();
        }
        else if(v.getId() == R.id.browseimage_floating_action_button){
            ImagePicker.create(MainActivity.this).start();
        }
        else if (v.getId() == R.id.info_floating_action_button){
            Log.d("clicked", "onClick: info button Clicked");
        }
        else if (v.getId() == R.id.floating_action_button){
            animateFAB();
        }
        else if (v.getId() == R.id.loaddata_floating_action_button){
            Log.d(TAG, "onClick: load data");


        }

    }


    private ArrayList<Form> loadXMLData(String pathFile){
        ArrayList<String> userData = new ArrayList<String>();
        ArrayList<Form> listForm = new ArrayList<Form>();
        FileInputStream fis = null;
        try {
            // fis = getApplicationContext().openFileInput(xmlpathFile);
            fis = new FileInputStream(new File(pathFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);

        char[] inputBuffer = new char[0];
        try {
            inputBuffer = new char[fis.available()];
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            isr.read(inputBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = new String(inputBuffer);
        try {
            isr.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        XmlPullParserFactory factory = null;
        try {
            factory = XmlPullParserFactory.newInstance();
        }
        catch (XmlPullParserException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        factory.setNamespaceAware(true);
        XmlPullParser xpp = null;
        try {
            xpp = factory.newPullParser();
        }
        catch (XmlPullParserException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        try {
            xpp.setInput(new StringReader(data));
        }
        catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        int eventType = 0;
        try {
            eventType = xpp.getEventType();
        }
        catch (XmlPullParserException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }


        Form newForm = null;
        while (eventType != XmlPullParser.END_DOCUMENT){
            if (xpp.getName() != null && xpp.getName().equals("patient")){
                Log.d(TAG, "here 1");
                if(eventType == XmlPullParser.START_TAG){
                    newForm = new Form();
                }
                else if (eventType == XmlPullParser.END_TAG){
                    listForm.add(newForm);
                    newForm = null;
                }
            }

            else if(eventType == XmlPullParser.START_TAG)
            {
                String tagName = xpp.getName();
                Log.d(TAG, "tagName==" + tagName);
                try {
                    eventType = xpp.next();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                }
                if(eventType == XmlPullParser.TEXT){
                    switch (tagName){
                        case "personalID":
                            newForm.setPersonalID(xpp.getText());
                            break;
                        case "name":
                            newForm.setName(xpp.getText());
                            break;
                        case "dob":
                            newForm.setDateOfBirth(xpp.getText());
                            break;
                        case "medhis":
                            newForm.setMedicalHistory(xpp.getText());
                            break;
                    }
                }
            }
            try {
                eventType = xpp.next();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            }
        }
        return listForm;



//        while (eventType != XmlPullParser.END_DOCUMENT){
//            if (eventType == XmlPullParser.START_DOCUMENT) {
//                System.out.println("Start document");
//
//            }
//            else if (eventType == XmlPullParser.START_TAG) {
//                System.out.println("Start tag "+xpp.getName());
//            }
//            else if (eventType == XmlPullParser.END_TAG) {
//                System.out.println("End tag "+xpp.getName());
//            }
//            else if(eventType == XmlPullParser.TEXT) {
//                userData.add(xpp.getText());
//            }
//            try {
//                eventType = xpp.next();
//            }
//            catch (XmlPullParserException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        for(int i = 0; i< userData.size();i++){
//            Log.d(TAG, userData.get(i));
//        }
//        String userName = userData.get(0);
//        String password = userData.get(1);
//
//        Log.d("read file result", userName);
//        Log.d("read file result", password);


    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image images = ImagePicker.getFirstImageOrNull(data);
            Log.i("IMAGE",images.getPath());
            imgImport.setImageURI(Uri.parse(images.getPath()));

            BitmapFactory.Options optionstemp = new BitmapFactory.Options();
            optionstemp.inPreferredConfig = Bitmap.Config.ARGB_4444;
            rgbFrameBitmap = BitmapFactory.decodeFile(images.getPath(), optionstemp);
            if (rgbFrameBitmap == null ){
                Log.d("debug", "bitmap null");
            }
            Log.d("debug", "converted bitmap");
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void processImage() {
        // rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
        if (rgbFrameBitmap == null ){
            Log.d("debug", "bitmap null");
        }
        else {
            Log.d("debug", "bitmap not null");
            Log.d("width:",String.valueOf(rgbFrameBitmap.getWidth()) );
            Log.d("height:",String.valueOf(rgbFrameBitmap.getHeight()));
        }

        if (classifier == null){
            Log.d("debug", "classifier null");
        }
        final List<Classifier.Recognition> results =
                classifier.recognizeImage(rgbFrameBitmap, sensorOrientation);
        showResultsInBottomSheet(results);
    }

    protected void showResultsInBottomSheet(List<Recognition> results) {
        if (results != null && results.size() >= 5) {
            Recognition recognition = results.get(0);
            if (recognition != null) {
                if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
                if (recognition.getConfidence() != null)
                    recognitionValueTextView.setText(
                            String.format("%.2f", (100 * recognition.getConfidence())) + "%");
            }

            Recognition recognition1 = results.get(1);
            if (recognition1 != null) {
                if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
                if (recognition1.getConfidence() != null)
                    recognition1ValueTextView.setText(
                            String.format("%.2f", (100 * recognition1.getConfidence())) + "%");
            }

            Recognition recognition2 = results.get(2);
            if (recognition2 != null) {
                if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
                if (recognition2.getConfidence() != null)
                    recognition2ValueTextView.setText(
                            String.format("%.2f", (100 * recognition2.getConfidence())) + "%");
            }

            Recognition recognition3 = results.get(3);
            if (recognition3 != null) {
                if (recognition3.getTitle() != null) recognition3TextView.setText(recognition3.getTitle());
                if (recognition3.getConfidence() != null)
                    recognition3ValueTextView.setText(
                            String.format("%.2f", (100 * recognition3.getConfidence())) + "%");
            }

            Recognition recognition4 = results.get(4);
            if (recognition4 != null) {
                if (recognition4.getTitle() != null) recognition4TextView.setText(recognition4.getTitle());
                if (recognition4.getConfidence() != null)
                    recognition4ValueTextView.setText(
                            String.format("%.2f", (100 * recognition4.getConfidence())) + "%");
            }
        }
    }

}
