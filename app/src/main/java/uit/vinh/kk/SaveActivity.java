package uit.vinh.kk;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SaveActivity extends AppCompatActivity {
    Runnable saveImageRunnable;
    DatabaseHelper formDatabase ;


    private TextInputLayout textInputLayout_Today;
    private TextInputLayout textInputLayout_PatientName;
    private TextInputLayout textInputLayout_DateOfBirth;
    private TextInputLayout textInputLayout_PersonalID;
    private TextInputLayout textInputLayout_Systolic;
    private TextInputLayout textInputLayout_Diastolic;
    private TextInputLayout textInputLayout_BloodSugar;
    private TextInputLayout textInputLayout_Hba1c;
    private TextInputLayout textInputLayout_HDL;
    private TextInputLayout textInputLayout_LDL;
    private TextInputLayout textInputLayout_MedicalHistory;
    private TextInputLayout textInputLayout_Note;

    private TextInputEditText textInputEditText_Today;
    private TextInputEditText textInputEditText_PatientName;
    private TextInputEditText textInputEditText_DateOfBirth;
    private TextInputEditText textInputEditText_PersonalID;
    private TextInputEditText textInputEditText_Systolic;
    private TextInputEditText textInputEditText_Diastolic;
    private TextInputEditText textInputEditText_BloodSugar;
    private TextInputEditText textInputEditText_Hba1c;
    private TextInputEditText textInputEditText_HDL;
    private TextInputEditText textInputEditText_LDL;
    private TextInputEditText textInputEditText_MedicalHistory;
    private TextInputEditText textInputEditText_Note;

    private Button button_calendarToday;
    private Button button_calendarDOB;

    private Spinner spinner_sex;
    private Spinner spinner_result;

    private PhotoView photoViewOriginalImage;
    private PhotoView photoViewContrastEnhnace;
    private ImageView imageViewOriginalImage;
    private File directory;
    private Thread saveImageThread;
    public Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // create components
        textInputEditText_Today = findViewById(R.id.info_textinputedittext_Today);
        textInputEditText_PatientName = findViewById(R.id.info_textinputedittext_PatientName);
        textInputEditText_DateOfBirth = findViewById(R.id.info_textinputedittext_DateOfBirth);
        textInputEditText_PersonalID = findViewById(R.id.info_textinputedittext_PersonalID);
        textInputEditText_Systolic = findViewById(R.id.info_textinputedittext_Systolic);
        textInputEditText_Diastolic = findViewById(R.id.info_textinputedittext_Diastolic);
        textInputEditText_BloodSugar = findViewById(R.id.info_textinputedittext_BloodSugar);
        textInputEditText_Hba1c = findViewById(R.id.info_textinputedittext_Hba1c);
        textInputEditText_HDL = findViewById(R.id.info_textinputedittext_HDL);
        textInputEditText_LDL = findViewById(R.id.info_textinputedittext_LDL);
        textInputEditText_MedicalHistory = findViewById(R.id.info_textinputedittext_MedicalHistory);
        textInputEditText_Note = findViewById(R.id.info_textinputedittext_Note);


        textInputLayout_Today = findViewById(R.id.info_textinputlayout_Today);
        textInputLayout_PatientName = findViewById(R.id.info_textinputlayout_PatientName);
        textInputLayout_DateOfBirth = findViewById(R.id.info_textinputlayout_DateOfBirth);
        textInputLayout_PersonalID = findViewById(R.id.info_textinputlayout_PersonalID);
        textInputLayout_Systolic = findViewById(R.id.info_textinputlayout_Systolic);
        textInputLayout_Diastolic = findViewById(R.id.info_textinputlayout_Diastolic);
        textInputLayout_BloodSugar = findViewById(R.id.info_textinputlayout_BloodSugar);
        textInputLayout_Hba1c = findViewById(R.id.info_textinputlayout_Hba1c);
        textInputLayout_HDL = findViewById( R.id.info_textinputlayout_HDL);
        textInputLayout_LDL = findViewById(R.id.info_textinputlayout_LDL);
        textInputLayout_MedicalHistory = findViewById(R.id.info_textinputlayout_MedicalHistory);
        textInputLayout_Note = findViewById(R.id.info_textinputlayout_MedicalHistory);


        textInputLayout_Today.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar myCalendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    //updateLabel();
                    String myFormat = "yyyy/MM/dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    textInputEditText_Today.setText(sdf.format(myCalendar.getTime()));
                };
                new DatePickerDialog(SaveActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        textInputLayout_DateOfBirth.setEndIconOnClickListener(v -> {
            Log.d("debug", "onClick: clicked on date picker");
            Calendar myCalendar = Calendar.getInstance();
            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //updateLabel();
                String myFormat = "yyyy/MM/dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                textInputEditText_DateOfBirth.setText(sdf.format(myCalendar.getTime()));
            };
            new DatePickerDialog(SaveActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });
        spinner_sex = findViewById(R.id.info_spinner_sex);
        spinner_result = findViewById(R.id.info_spinner_result);


        photoViewOriginalImage = findViewById(R.id.info_photoview_OriginalImage);
        photoViewContrastEnhnace = findViewById(R.id.info_photoview_ContrastEnhance);
        //imageViewOriginalImage = findViewById(R.id.info_imageview_OriginalImage);

        formDatabase = new DatabaseHelper(this);

        String SaveAs = (String) getIntent().getSerializableExtra("Save As");
        if (SaveAs.equals(CONSTANTS.SAVE_AS_MODE_EDIT)){
            photoViewContrastEnhnace.setVisibility(View.GONE);
            photoViewOriginalImage.setVisibility(View.GONE);
            Form oldForm = (Form)getIntent().getSerializableExtra("oldform");
            textInputEditText_Today.setText(oldForm.getToday());
            textInputEditText_PatientName.setText(oldForm.getName());
            textInputEditText_DateOfBirth.setText(oldForm.getDateOfBirth());
            textInputEditText_PersonalID.setText(oldForm.getPersonalID());
            textInputEditText_Systolic.setText(oldForm.getBloodPressure_Systolic());
            textInputEditText_Diastolic.setText(oldForm.getBloodPressure_Diastolic());
            textInputEditText_BloodSugar.setText(oldForm.getBloodSugar());
            textInputEditText_Hba1c.setText(oldForm.getHba1c());
            textInputEditText_HDL.setText(oldForm.getCholesterolHDL());
            textInputEditText_LDL.setText(oldForm.getCholesterolLDL());
            textInputEditText_MedicalHistory.setText(oldForm.getMedicalHistory());
            textInputEditText_Note.setText(oldForm.getNote());

            spinner_result.setSelection(((ArrayAdapter)spinner_result.getAdapter()).getPosition(oldForm.getClassificationResult()));
            spinner_sex.setSelection(((ArrayAdapter)spinner_sex.getAdapter()).getPosition(oldForm.getSex()));

        }
        else if (SaveAs.equals(CONSTANTS.SAVE_AS_MODE_NEW)){
            SimpleDateFormat sdfToday = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
            textInputEditText_Today.setText(sdfToday.format(new Date()));
            Log.d("debug", "onCreate: Save as Mode new");
            //Uri URIOriginalImage = (Uri) getIntent().getParcelableExtra("URIOriginalImage");
            String originalImagePath = (String)getIntent().getSerializableExtra("ImagePath");
            float scale = (float) getIntent().getSerializableExtra("scalevalue");
            Bitmap bitmapOriginalImage = null;
            try {
                bitmapOriginalImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse("file://"+ originalImagePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(bitmapOriginalImage!=null ){
                bitmapOriginalImage = Bitmap.createScaledBitmap(bitmapOriginalImage,(int)(bitmapOriginalImage.getWidth()*scale) ,(int)(bitmapOriginalImage.getHeight()*scale) ,true );
                photoViewOriginalImage.setImageBitmap(bitmapOriginalImage);
            }
        }
        // bắt sự kiện bấm button hiện calendar

        // Thread
        saveImageThread = new Thread(new Runnable(){
            public void run() {
                String SaveAs = (String) getIntent().getSerializableExtra("Save As");
                Form prevForm = (Form)getIntent().getSerializableExtra("oldform");
                Form saveForm = new Form();
                saveForm.setToday(textInputEditText_Today.getText().toString());
                saveForm.setName(textInputEditText_PatientName.getText().toString());
                saveForm.setDateOfBirth(textInputEditText_DateOfBirth.getText().toString());
                saveForm.setPersonalID(textInputEditText_PersonalID.getText().toString());
                saveForm.setBloodPressure_Systolic(textInputEditText_Systolic.getText().toString());
                saveForm.setBloodPressure_Diastolic(textInputEditText_Diastolic.getText().toString());
                saveForm.setBloodSugar(textInputEditText_BloodSugar.getText().toString());
                saveForm.setHba1c(textInputEditText_Hba1c.getText().toString());
                saveForm.setCholesterolLDL(textInputEditText_LDL.getText().toString());
                saveForm.setCholesterolHDL(textInputEditText_HDL.getText().toString());
                saveForm.setMedicalHistory(textInputEditText_MedicalHistory.getText().toString());
                saveForm.setNote(textInputEditText_Note.getText().toString());
                saveForm.setSex(spinner_sex.getSelectedItem().toString());
                saveForm.setClassificationResult(spinner_result.getSelectedItem().toString());

                // nếu không có sdcard -> không lưu ảnh
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    // set giá trị null cho path
                }
                else{ // nếu có sdcard -> kiểm tra có thư mục hay không -> tạo thư mục -> nếu là thông tin mới -> lưu ảnh
                    directory = new File(CONSTANTS.FOLDER_PATH_STORE_IMG);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }
                }

                // nếu là edit bệnh nhân
                if (SaveAs.equals("OLD")){
                    boolean isUpdated = formDatabase.updateData(prevForm.getID(), saveForm);
                }
                // nếu là lưu mới thông tin bệnh nhân
                else if (SaveAs.equals("NEW")){
                    Bitmap bitmap = ((BitmapDrawable)photoViewOriginalImage.getDrawable()).getBitmap();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String saveImageName = sdf.format(new Date()) + ".jpg";
                    File file = new File(directory ,saveImageName);
                    try {

                        OutputStream output = new FileOutputStream(file);
                        long startTime = System.nanoTime();
                        // Compress into png, jpg format image from 0% - 100%
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                        long endTime = System.nanoTime();
                        Log.d("debug", "onOptionsItemSelected: inside thread Time (milisecond) to compress image is " + (endTime - startTime)/1000);

                        output.flush();
                        output.close();
                    }
                    catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    // lưu vào sqlite
                    saveForm.setPathOriginalImage(CONSTANTS.FOLDER_PATH_STORE_IMG + File.separator + saveImageName);
                    Log.d("debug", "onOptionsItemSelected: saveForm.getPathOriginalImage() == " + saveForm.getPathOriginalImage());
                    Log.d("debug", "onOptionsItemSelected: saveForm.toString() == "+ saveForm.toString());
                    long startTime = System.nanoTime();
                    boolean isInserted = formDatabase.insertNewForm(saveForm);
                    if (isInserted == true){
                        // gửi tín hiệu cho MainActivity


                    }
                    long endTime = System.nanoTime();
                    Log.d("debug", "onOptionsItemSelected: inside thread Time cost (milisecond) to inserted patient is " + (endTime - startTime)/1000);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                SaveActivity.super.onBackPressed();
                return true;
            case R.id.menu_save:
                // save data with multithread
                //saveImageThread.start();
                ProgressDialog mProgressDialog = ProgressDialog.show(this, "Please wait","Saving data", true);
                new Thread() {
                    @Override
                    public void run() {
                        WithoutMultiThread();
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Infomation has been saved successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // xóa các màn hình trước
                                    startActivity(intent);
                                }
                            });
                        } catch (final Exception ex) {

                        }
                    }
                }.start();
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void WithoutMultiThread(){
        String SaveAs = (String) getIntent().getSerializableExtra("Save As");
        Form prevForm = (Form)getIntent().getSerializableExtra("oldform");
        Form saveForm = new Form();
        saveForm.setToday(textInputEditText_Today.getText().toString());
        saveForm.setName(textInputEditText_PatientName.getText().toString());
        saveForm.setDateOfBirth(textInputEditText_DateOfBirth.getText().toString());
        saveForm.setPersonalID(textInputEditText_PersonalID.getText().toString());
        saveForm.setBloodPressure_Systolic(textInputEditText_Systolic.getText().toString());
        saveForm.setBloodPressure_Diastolic(textInputEditText_Diastolic.getText().toString());
        saveForm.setBloodSugar(textInputEditText_BloodSugar.getText().toString());
        saveForm.setHba1c(textInputEditText_Hba1c.getText().toString());
        saveForm.setCholesterolLDL(textInputEditText_LDL.getText().toString());
        saveForm.setCholesterolHDL(textInputEditText_HDL.getText().toString());
        saveForm.setMedicalHistory(textInputEditText_MedicalHistory.getText().toString());
        saveForm.setNote(textInputEditText_Note.getText().toString());
        saveForm.setSex(spinner_sex.getSelectedItem().toString());
        saveForm.setClassificationResult(spinner_result.getSelectedItem().toString());

        // nếu không có sdcard -> không lưu ảnh
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            // set giá trị null cho path
        }
        else{ // nếu có sdcard -> kiểm tra có thư mục hay không -> tạo thư mục -> nếu là thông tin mới -> lưu ảnh
            directory = new File(CONSTANTS.FOLDER_PATH_STORE_IMG);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        // nếu là edit bệnh nhân
        if (SaveAs.equals("OLD")){
            boolean isUpdated = formDatabase.updateData(prevForm.getID(), saveForm);
        }
        // nếu là lưu mới thông tin bệnh nhân
        else if (SaveAs.equals("NEW")){
            Bitmap bitmap = ((BitmapDrawable)photoViewOriginalImage.getDrawable()).getBitmap();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
            String saveImageName = sdf.format(new Date()) + ".jpg";
            File file = new File(directory ,saveImageName);
            try {

                OutputStream output = new FileOutputStream(file);
                long startTime = System.nanoTime();
                // Compress into png, jpg format image from 0% - 100%
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
                long endTime = System.nanoTime();
                output.flush();
                output.close();
            }
            catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // lưu vào sqlite
            saveForm.setPathOriginalImage(CONSTANTS.FOLDER_PATH_STORE_IMG + File.separator + saveImageName);
            long startTime = System.nanoTime();
            boolean isInserted = formDatabase.insertNewForm(saveForm);
            if (isInserted == true){
                // gửi tín hiệu cho MainActivity

            }
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_save, menu);
        return true;
    }

}
