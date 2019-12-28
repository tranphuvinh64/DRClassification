package uit.vinh.kk;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

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
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import uit.vinh.kk.Classifier.Recognition;
//import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {
    static DatabaseHelper formDatabase ;
    ArrayList<DataModel> dataModels;
    //private static ArrayList<Form> listForm = loadXMLData("//storage//emulated//0//patientData");
    private static ArrayList<Form> listForm;
    ListView listView;
    private static CustomAdapter adapter;
    private static String TAG = "debug";
    Toolbar searchToolbar;
    LinearLayout browseImageLinearLayout, infoLinearLayout, useCameraLinearLayout;
    FrameLayout mainLayout;
    View blurView;
    FloatingActionButton floatingActionButtonNew, floatingActionButtonBrowse, floatingActionButtonInfo;
    Animation fabOpen, fabClose, rotateBackward, rotateForward;
    boolean isOpen = false;
    private static final int RC_CAMERA = 3000;
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

        //getSupportActionBar().hide();
        browseImageLinearLayout = findViewById(R.id.browseimage_linear_layout);
        infoLinearLayout = findViewById(R.id.info_linear_layout);
        useCameraLinearLayout = findViewById(R.id.usecamera_linear_layout);
        mainLayout = findViewById(R.id.main);
        mainLayout.setOnClickListener(this);

        blurView = findViewById(R.id.shadowView);
        blurView.setOnClickListener(this);
        //loadDataLinearLayout = findViewById(R.id.loaddata_linear_layout);

        floatingActionButtonNew =findViewById(R.id.floating_action_button);
        floatingActionButtonBrowse = findViewById(R.id.browseimage_floating_action_button);
        floatingActionButtonInfo = findViewById(R.id.info_floating_action_button);


        // set action listener
        floatingActionButtonNew.setOnClickListener(this);
        floatingActionButtonBrowse.setOnClickListener(this);
        floatingActionButtonInfo.setOnClickListener(this);


        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);


        listView=findViewById(R.id.list);
        dataModels= new ArrayList<>();

        // create database
        formDatabase = new DatabaseHelper(this);
        listForm = loadSQLiteData();

        for(int i = 0; i < listForm.size(); i++){
            // Log.d(TAG, i + " loadXMLData: " + listForm.get(i).toString());
            String tempname = listForm.get(i).getName();
            String tempidForm = listForm.get(i).getID();
            String tempmedhis = listForm.get(i).getMedicalHistory();
            String temppersonalid = listForm.get(i).getPersonalID();
            String tempdob = listForm.get(i).getDateOfBirth();
            String tempresult = listForm.get(i).getClassificationResult();
            int len_secondline = Math.min((tempdob + " - " + tempmedhis).length(),CONSTANTS.MAX_LENGTH_STRING);
            dataModels.add(new DataModel(tempname,temppersonalid,tempidForm,(tempdob + " - " + tempmedhis).substring(0,len_secondline),tempresult));
        }

        adapter= new CustomAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DataModel dataModel= dataModels.get(position);
                Form selectedForm = findFormByID(dataModel.getIdForm());
                //chuyển sang màn hình displaypatientinfo
                //gửi dữ liệu cho màn hình mới
                if(selectedForm!=null){
                    Intent intent = new Intent(getApplicationContext(), DisplayPatientInfoActivity.class);
                    intent.putExtra("patientinfo", selectedForm);
                    getIntent().getSerializableExtra("patientinfo");
                    startActivity(intent);
                }
            }
        });
        //loadSQLiteData();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.menu_search){
            Log.d(TAG, "icon search clicked");

            //setSupportActionBar(actionbar);

            getSupportActionBar().hide();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_search, menu);
        MenuItem searchViewItem = menu.findItem(R.id.menu_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private Form findFormByID(String idForm){
        for(int i = 0; i < listForm.size(); i++){
            if(listForm.get(i).getID() == idForm){
                return listForm.get(i);
            }
        }
        return null;
    }

    private void animateFAB(){
        if(isOpen){
            floatingActionButtonNew.startAnimation(rotateBackward);
            browseImageLinearLayout.startAnimation(fabClose);
            useCameraLinearLayout.startAnimation(fabClose);
            infoLinearLayout.startAnimation(fabClose);
            //loadDataLinearLayout.startAnimation(fabClose);
            floatingActionButtonBrowse.setClickable(false);
            floatingActionButtonInfo.setClickable(false);
            View v = findViewById( R.id.shadowView);
            v.setVisibility(View.GONE);
            listView.setEnabled(true);
            Log.d(TAG, "animateFAB: set clickable 1");
            isOpen = false;
        }
        else
        {
            floatingActionButtonNew.startAnimation(rotateForward);
            browseImageLinearLayout.startAnimation(fabOpen);
            useCameraLinearLayout.startAnimation(fabOpen);
            infoLinearLayout.startAnimation(fabOpen);
            //loadDataLinearLayout.startAnimation(fabOpen);
            floatingActionButtonBrowse.setClickable(true);
            floatingActionButtonInfo.setClickable(true);
            View v = findViewById( R.id.shadowView);
            v.setVisibility(View.VISIBLE);
            listView.setEnabled(false);
            Log.d(TAG, "animateFAB: set clickable 2");
            isOpen = true;
        }
    }

    @Override
    public void onClick(View v){
//        if(v.getId() == R.id.btnClassify){
//            processImage();
//        }
        if(v.getId() == R.id.browseimage_floating_action_button){
            ImagePicker.create(MainActivity.this).start();
            animateFAB();
        }
        else if (v.getId() == R.id.info_floating_action_button){
            animateFAB();
//            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code



//            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(takePicture, 0);//zero can be replaced with any action code (called requestCode)
        }
        else if (v.getId() == R.id.floating_action_button){
            animateFAB();
        }
        else if (v.getId() == R.id.shadowView){
            Log.d(TAG, "onClick: main layout clicked");
            animateFAB();
        }


    }


    private static ArrayList<Form> loadXMLData(String pathFile){
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
                        case "ID":
                            newForm.setID(xpp.getText());
                            break;
                        case "today":
                            newForm.setToday(xpp.getText());
                            break;
                        case "name":
                            newForm.setName(xpp.getText());
                            break;
                        case "dob":
                            newForm.setDateOfBirth(xpp.getText());
                            break;
                        case "sex":
                            newForm.setSex(xpp.getText());
                            break;
                        case "personalID":
                            newForm.setPersonalID(xpp.getText());
                            break;
                        case "result":
                            newForm.setClassificationResult(xpp.getText());
                            break;
                        case "systolic":
                            newForm.setBloodPressure_Systolic(xpp.getText());
                            break;
                        case "diastolic":
                            newForm.setBloodPressure_Diastolic(xpp.getText());
                            break;
                        case "sugar":
                            newForm.setBloodSugar(xpp.getText());
                            break;
                        case "hba1c":
                            newForm.setHba1c(xpp.getText());
                            break;
                        case "medhis":
                            newForm.setMedicalHistory(xpp.getText());
                            break;
                        case "note":
                            newForm.setNote(xpp.getText());
                            break;
                        case "hdl":
                            newForm.setCholesterolHDL(xpp.getText());
                            break;
                        case "ldl":
                            newForm.setCholesterolLDL(xpp.getText());
                            break;
//                        case "image":  // mở khi xử lý chỉn chu, vì string rất dàu
//                            newForm.setImage(xpp.getText());
//                            break;
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
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            Image images = ImagePicker.getFirstImageOrNull(data);


            // gửi ảnh qua result activity
            if (images!=null){
                Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                intent.putExtra("imagetest", images);
                getIntent().getSerializableExtra("imagetest");
                startActivity(intent);
            }

            Log.i("IMAGE",images.getPath());
            Log.d(TAG, images.getClass().getName());
            //imgImport.setImageURI(Uri.parse(images.getPath()));

            BitmapFactory.Options optionstemp = new BitmapFactory.Options();
            optionstemp.inPreferredConfig = Bitmap.Config.ARGB_4444;
            rgbFrameBitmap = BitmapFactory.decodeFile(images.getPath(), optionstemp);
            if (rgbFrameBitmap == null ){
                Log.d("debug", "bitmap null");
            }
            Log.d("debug", "converted bitmap");
            Log.d("image type", rgbFrameBitmap.getClass().getName());
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void processImage() {
//        // rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);
//        if (rgbFrameBitmap == null ){
//            Log.d("debug", "bitmap null");
//        }
//        else {
//            Log.d("debug", "bitmap not null");
//            Log.d("width:",String.valueOf(rgbFrameBitmap.getWidth()) );
//            Log.d("height:",String.valueOf(rgbFrameBitmap.getHeight()));
//        }
//
//        if (classifier == null){
//            Log.d("debug", "classifier null");
//        }
//        final List<Classifier.Recognition> results =
//                classifier.recognizeImage(rgbFrameBitmap, sensorOrientation);
//        //showResultsInBottomSheet(results);
    }

    protected void showResultsInBottomSheet(List<Recognition> results) {
//        if (results != null && results.size() >= 5) {
//            Recognition recognition = results.get(0);
//            if (recognition != null) {
//                if (recognition.getTitle() != null) recognitionTextView.setText(recognition.getTitle());
//                if (recognition.getConfidence() != null)
//                    recognitionValueTextView.setText(
//                            String.format("%.2f", (100 * recognition.getConfidence())) + "%");
//            }
//
//            Recognition recognition1 = results.get(1);
//            if (recognition1 != null) {
//                if (recognition1.getTitle() != null) recognition1TextView.setText(recognition1.getTitle());
//                if (recognition1.getConfidence() != null)
//                    recognition1ValueTextView.setText(
//                            String.format("%.2f", (100 * recognition1.getConfidence())) + "%");
//            }
//
//            Recognition recognition2 = results.get(2);
//            if (recognition2 != null) {
//                if (recognition2.getTitle() != null) recognition2TextView.setText(recognition2.getTitle());
//                if (recognition2.getConfidence() != null)
//                    recognition2ValueTextView.setText(
//                            String.format("%.2f", (100 * recognition2.getConfidence())) + "%");
//            }
//
//            Recognition recognition3 = results.get(3);
//            if (recognition3 != null) {
//                if (recognition3.getTitle() != null) recognition3TextView.setText(recognition3.getTitle());
//                if (recognition3.getConfidence() != null)
//                    recognition3ValueTextView.setText(
//                            String.format("%.2f", (100 * recognition3.getConfidence())) + "%");
//            }
//
//            Recognition recognition4 = results.get(4);
//            if (recognition4 != null) {
//                if (recognition4.getTitle() != null) recognition4TextView.setText(recognition4.getTitle());
//                if (recognition4.getConfidence() != null)
//                    recognition4ValueTextView.setText(
//                            String.format("%.2f", (100 * recognition4.getConfidence())) + "%");
//            }
//        }
    }

    private static ArrayList<Form> loadSQLiteData(){
        Cursor res = formDatabase.loadData();
        ArrayList<Form> listForm = new ArrayList<>();
        if (res.getCount() != 0){
            while(res.moveToNext()){
                // để giá trị constant cho các cột, đọc vào dễ hiểu
                Form showForm = new Form();
                showForm.setID(res.getString(CONSTANTS.COLUMN_ID_INDEX));
                showForm.setToday(res.getString(CONSTANTS.COLUMN_TODAY_INDEX));
                showForm.setName(res.getString(CONSTANTS.COLUMN_PATIENT_NAME_INDEX));
                showForm.setDateOfBirth(res.getString(CONSTANTS.COLUMN_DOB_INDEX));
                showForm.setSex(res.getString(CONSTANTS.COLUMN_SEX_INDEX));
                showForm.setPersonalID(res.getString(CONSTANTS.COLUMN_PERSONALID_INDEX));
                showForm.setClassificationResult(res.getString(CONSTANTS.COLUMN_RESULT_INDEX));
                showForm.setBloodPressure_Systolic(res.getString(CONSTANTS.COLUMN_SYSTOLIC_INDEX));
                showForm.setBloodPressure_Diastolic(res.getString(CONSTANTS.COLUMN_DIASTOLIC_INDEX));
                showForm.setBloodSugar(res.getString(CONSTANTS.COLUMN_BLOODSUGAR_INDEX));
                showForm.setHba1c(res.getString(CONSTANTS.COLUMN_HBA1C_INDEX));
                showForm.setCholesterolLDL(res.getString(CONSTANTS.COLUMN_CHOLESTEROL_LDL_INDEX));
                showForm.setCholesterolHDL(res.getString(CONSTANTS.COLUMN_CHOLESTEROL_HDL_INDEX));
                showForm.setMedicalHistory(res.getString(CONSTANTS.COLUMN_MEDICAL_HISTORY_INDEX));
                showForm.setNote(res.getString(CONSTANTS.COLUMN_NOTE_INDEX));



                listForm.add(showForm);
                Log.d(TAG, "loadSQLite data: " + showForm.toString());
            }
        }
        return listForm;
    }
}
