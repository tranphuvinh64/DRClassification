package uit.vinh.kk;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.model.Image;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
//import org.tensorflow.lite.Interpreter;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {
    // will hold uri of image obtained from camera
    private Uri imageUri;
    private String imagePath;
    static DatabaseHelper formDatabase ;
    private ArrayList<DataModel> dataModels;
    //private static ArrayList<Form> listForm = loadXMLData("//storage//emulated//0//patientData");
    private static ArrayList<Form> listForm;
    private ListView listView;
    private static CustomAdapter adapter;
    private static String TAG = "debug";
    private LinearLayout browseImageLinearLayout, infoLinearLayout, useCameraLinearLayout;
    private FrameLayout mainLayout;
    private View blurView;
    private FloatingActionButton floatingActionButtonNew, floatingActionButtonBrowse, floatingActionButtonInfo, floatinngActionButtonUseCamera;
    private Animation fabOpen, fabClose, rotateBackward, rotateForward;
    private boolean isOpen = false;
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
        // initial components
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this.getApplicationContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CONSTANTS.REQUEST_PERMISSION);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CONSTANTS.REQUEST_PERMISSION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        CONSTANTS.REQUEST_PERMISSION);
            }

        // temp
        rgbFrameBitmap = Bitmap.createBitmap(640,480,Bitmap.Config.ARGB_8888);


        try {
            classifier = new Classifier(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        floatinngActionButtonUseCamera = findViewById(R.id.usecamera_floating_action_button );


        // set action listener
        floatingActionButtonNew.setOnClickListener(this);
        floatingActionButtonBrowse.setOnClickListener(this);
        floatingActionButtonInfo.setOnClickListener(this);
        floatinngActionButtonUseCamera.setOnClickListener(this);


        fabOpen = AnimationUtils.loadAnimation(this,R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(this,R.anim.fab_close);
        rotateBackward = AnimationUtils.loadAnimation(this,R.anim.rotate_backward);
        rotateForward = AnimationUtils.loadAnimation(this,R.anim.rotate_forward);


        listView=findViewById(R.id.list);


        // create database
        formDatabase = new DatabaseHelper(this);

        listForm = loadSQLiteData();
        dataModels = GetDataModel(listForm);

        adapter= new CustomAdapter(dataModels,getApplicationContext());
        listView.setAdapter(adapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemLongClick: long click at position " + position);
                return false;
            }
        });
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

        registerForContextMenu(listView);
        // create folder to save image
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            Log.d("MyApp", "No SDCARD");
        } else {
            File directory = new File(CONSTANTS.FOLDER_PATH_STORE_IMG);
            directory.mkdirs();
            Log.d(TAG, "onCreate: create folder at " + Environment.getExternalStorageDirectory() + File.separator + CONSTANTS.STORE_IMG_FOLDER_NAME);
            File nomediaFile = new File(CONSTANTS.FOLDER_PATH_STORE_IMG + File.separator+ ".nomedia");
            try {
                boolean result = nomediaFile.createNewFile();
                if (result == true ){
                    Log.d(TAG, "onCreate: create nomedia file at " + CONSTANTS.FOLDER_PATH_STORE_IMG + File.separator+ ".nomedia");

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getItemId() == R.id.menu_option_delete){
            Log.d(TAG, "onContextItemSelected: Deleted at "+ info.position);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle("Are you sure?");
            builder.setMessage("You are going to delete a patient! This action cannot be undone");

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // delete form
                    DataModel dataModel= dataModels.get(info.position);
                    formDatabase.deleteForm(dataModel.getIdForm());
                    // reload MainActivity
                    Intent intent = getIntent();
                    overridePendingTransition(0, 0);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    // Do nothing but close the dialog
                    // dialog.dismiss();
                }
            });

            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                    dialog.dismiss();
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_delete_patient_info,menu);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CONSTANTS.REQUEST_PERMISSION) {
            if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(getApplicationContext(),"This application needs read, write, and camera permissions to run. Application now closing.",Toast.LENGTH_LONG);
                System.exit(0);}}
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
                if(TextUtils.isEmpty(newText)){
                    adapter.filter(newText);
                    listView.clearTextFilter();
                }
                else{
                    adapter.filter(newText);
                }
                return true;
            }
        });
        return true;
    }

    public void showInputMethod(final EditText v) {
        v.requestFocus();
        InputMethodManager imm = (InputMethodManager)getApplicationContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInputFromWindow(v.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void onClick(View v){
        if(v.getId() == R.id.browseimage_floating_action_button){
            ImagePicker.create(MainActivity.this).start();
            animateFAB();
        }
        else if (v.getId() == R.id.info_floating_action_button){
            animateFAB();
        }
        else if (v.getId() == R.id.floating_action_button){
            animateFAB();
        }
        else if (v.getId() == R.id.shadowView){
            animateFAB();
        }
        else if(v.getId() == R.id.usecamera_floating_action_button ){
            animateFAB();
            // Open Camera
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
                // tell camera where to store the resulting picture
                imageUri = getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                // start camera, and wait for it to finish
                startActivityForResult(intent, CONSTANTS.REQUEST_IMAGE);
        }
        
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data) == true){
            ProgressDialog mProgressDialog = ProgressDialog.show(this, "Please wait","Long operation starts...", true);
            new Thread() {
                @Override
                public void run() {
                    Image images = ImagePicker.getFirstImageOrNull(data);
                    if (images != null){
                        imagePath = images.getPath();
                    }
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("ImagePath", imagePath);
                    startActivity(intent);
                    try {

                        // code runs in a thread
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                            }
                        });
                    } catch (final Exception ex) {
                    }
                }
            }.start();

        }
        if ( requestCode == CONSTANTS.REQUEST_IMAGE && resultCode == RESULT_OK){
            ProgressDialog mProgressDialog = ProgressDialog.show(this, "Please wait","Classifying", true);
            new Thread() {
                @Override
                public void run() {
                    imagePath = getRealPathFromURI(imageUri);
                    Log.d(TAG, "selected image from camera ");
                    Log.d(TAG, "onActivityResult: imageUri == " + imageUri);
                    Log.d(TAG, "onActivityResult: real path == "+ getRealPathFromURI(imageUri));
                    Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
                    intent.putExtra("ImagePath", imagePath);
                    startActivity(intent);
                    try {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mProgressDialog.dismiss();
                            }
                        });
                    } catch (final Exception ex) {

                    }
                }
            }.start();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String getRealPathFromURI(Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }
    private static ArrayList<Form> loadSQLiteData(){
        Cursor res = formDatabase.loadData();
        ArrayList<Form> listForm = new ArrayList<>();
        if (res.getCount() != 0){
            while(res.moveToNext()){
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
                showForm.setPathOriginalImage(res.getString(CONSTANTS.COLUMN_PATH_ORIGINAL_IMAGE_INDEX));
                //Log.d(TAG, "path in SQLite ==: " + res.getString(CONSTANTS.COLUMN_PATH_ORIGINAL_IMAGE_INDEX));
                //showForm.setPathContrastEnhaceImage(res.getString(CONSTANTS.COLUMN_PATH_CONTRAST_ENHANCE_IMAGE_INDEX));
                if (res.getString(CONSTANTS.COLUMN_ISDELETE).equals("FALSE")){
                    listForm.add(showForm);
                }
                Log.d(TAG, "loadSQLite data: " + showForm.toString());
            }
        }
        Collections.sort(listForm, Form.CountDate);
        return listForm;
    }

    private void animateFAB(){
        if(isOpen){
            floatingActionButtonNew.startAnimation(rotateBackward);
            browseImageLinearLayout.startAnimation(fabClose);
            useCameraLinearLayout.startAnimation(fabClose);
            infoLinearLayout.startAnimation(fabClose);
            floatingActionButtonBrowse.setClickable(false);
            floatingActionButtonInfo.setClickable(false);
            View v = findViewById( R.id.shadowView);
            v.setVisibility(View.GONE);
            listView.setEnabled(true);
            isOpen = false;
        }
        else
        {
            floatingActionButtonNew.startAnimation(rotateForward);
            browseImageLinearLayout.startAnimation(fabOpen);
            useCameraLinearLayout.startAnimation(fabOpen);
            infoLinearLayout.startAnimation(fabOpen);
            floatingActionButtonBrowse.setClickable(true);
            floatingActionButtonInfo.setClickable(true);
            View v = findViewById( R.id.shadowView);
            v.setVisibility(View.VISIBLE);
            listView.setEnabled(false);
            isOpen = true;
        }
    }

    private Form findFormByID(String idForm){
        for(int i = 0; i < listForm.size(); i++){
            if(listForm.get(i).getID() == idForm){
                return listForm.get(i);
            }
        }
        return null;
    }

    private String dateView (String studyDate){
        // xử lý hiển thị ngày tháng
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat(CONSTANTS.DateFormat, Locale.getDefault());
        String currentDate = sdf.format(new Date());
        try {
            Date today_parsed = sdf.parse(currentDate);
            Date studydate_parsed = sdf.parse(studyDate);
            long deviation = today_parsed.getTime()/86400000 - studydate_parsed.getTime()/86400000;

            if ( deviation == 0){
                studyDate = "Today";
            }
            else if (deviation == 1){
                studyDate = "Yesterday";
            }
            else if (deviation > 1 && deviation < 7){
                studyDate = deviation + " days ago";
            }
            else{
                String[] splitToday = currentDate.split("/");
                String[] splitStudyDate = studyDate.split("/");
                studyDate = handleSplitDMYString(splitToday, splitStudyDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return studyDate;
    }

    private String handleSplitDMYString (String[] today, String[] studyDate){
        String finalString = "N/A";
        if (today[2].equals(studyDate[2])){
            switch (studyDate[1]){
                case "01":
                    finalString = "Jan " + studyDate[0];
                    break;
                case "02":
                    finalString = "Feb " + studyDate[0];
                    break;
                case "03":
                    finalString = "Mar " + studyDate[0];
                    break;
                case "04":
                    finalString = "Apr " + studyDate[0];
                    break;
                case "05":
                    finalString = "May " + studyDate[0];
                    break;
                case "06":
                    finalString = "Jun " + studyDate[0];
                    break;
                case "07":
                    finalString = "Jul " + studyDate[0];
                    break;
                case "08":
                    finalString = "Aug " + studyDate[0];
                    break;
                case "09":
                    finalString = "Sep " + studyDate[0];
                    break;
                case "10":
                    finalString = "Oct " + studyDate[0];
                    break;
                case "11":
                    finalString = "Nov " + studyDate[0];
                    break;
                case "12":
                    finalString = "Dec " + studyDate[0];
                    break;
            }
        }
        else {
            finalString = studyDate[0] + "/" + studyDate[1] + "/" + studyDate[2];
        }
        return finalString;
    }

    private ArrayList<DataModel> GetDataModel (ArrayList<Form> listForm){
        ArrayList<DataModel> dataModels = new ArrayList<>();
        for(int i = 0; i < listForm.size(); i++){
            // Log.d(TAG, i + " loadXMLData: " + listForm.get(i).toString());
            String tempname = listForm.get(i).getName();
            // xử lý chuỗi khi tên quá dài
            if (tempname.length() > CONSTANTS.MAX_LENGTH_FIRST_STRING ){
                tempname = tempname.substring(tempname.length() - CONSTANTS.MAX_LENGTH_FIRST_STRING);
            }
            String tempidForm = listForm.get(i).getID();
            String tempmedhis = listForm.get(i).getMedicalHistory();
            String temppersonalid = listForm.get(i).getPersonalID();
            String tempdob = listForm.get(i).getDateOfBirth();
            String tempresult = listForm.get(i).getClassificationResult();
            String tempStudyDate = listForm.get(i).getToday();
            tempStudyDate = dateView(tempStudyDate);
            if (tempmedhis.length() > CONSTANTS.MAX_LENGTH_THIRD_STRING){
                tempmedhis = tempmedhis.substring(0,CONSTANTS.MAX_LENGTH_THIRD_STRING) + "...";
            }
            dataModels.add(new DataModel(tempname,tempdob + " - " + temppersonalid,tempidForm,tempmedhis,tempresult, tempStudyDate));
        }
        return dataModels;
    }
}
