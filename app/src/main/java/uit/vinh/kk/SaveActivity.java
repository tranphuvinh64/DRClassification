package uit.vinh.kk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SaveActivity extends AppCompatActivity {
    DatabaseHelper formDatabase ;
    private EditText edittext_idForm;
    private EditText edittext_today;
    private EditText edittext_patientName;
    private EditText edittext_dateOfBirth;
    private EditText edittext_personalID;
    private EditText edittext_systolic;
    private EditText edittext_diastolic;
    private EditText edittext_bloodSugar;
    private EditText edittext_hba1c;
    private EditText edittext_hdl;
    private EditText edittext_ldl;
    private EditText edittext_medicalHistory;
    private EditText edittext_note;

    private Button button_calendarToday;
    private Button button_calendarDOB;

    private Spinner spinner_sex;
    private Spinner spinner_result;

    private ImageView imageViewOriginalImage;
    private File directory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // create components
        //edittext_idForm = findViewById(R.id.info_edittext_idform);
        edittext_today = findViewById(R.id.info_edittext_today);
        edittext_patientName = findViewById(R.id.info_edittext_fullname);
        edittext_dateOfBirth = findViewById(R.id.info_edittext_dob);
        edittext_personalID = findViewById(R.id.info_edittext_personalID);
        edittext_systolic = findViewById(R.id.info_edittext_bloodpressure_systolic);
        edittext_diastolic = findViewById(R.id.info_edittext_bloodpressure_diastolic);
        edittext_bloodSugar = findViewById(R.id.info_edittext_bloodsugar);
        edittext_hba1c = findViewById(R.id.info_edittext_hba1c);
        edittext_hdl = findViewById(R.id.info_edittext_cholesterolHDL);
        edittext_ldl = findViewById(R.id.info_edittext_cholesterolLDL);
        edittext_medicalHistory = findViewById(R.id.info_edittext_medical_history);
        edittext_note = findViewById(R.id.info_edittext_note);

        spinner_sex = findViewById(R.id.info_spinner_sex);
        spinner_result = findViewById(R.id.info_spinner_result);

        button_calendarDOB = findViewById(R.id.info_button_calendar_dob);
        button_calendarToday = findViewById(R.id.info_button_calendar_today);

        imageViewOriginalImage = findViewById(R.id.info_imageview_OriginalImage);

        formDatabase = new DatabaseHelper(this);

        String SaveAs = (String) getIntent().getSerializableExtra("Save As");
        if (SaveAs.equals(CONSTANTS.SAVE_AS_MODE_EDIT)){
            Form oldForm = (Form)getIntent().getSerializableExtra("oldform");
            edittext_today.setText(oldForm.getToday());
            edittext_patientName.setText(oldForm.getName());
            edittext_dateOfBirth.setText(oldForm.getDateOfBirth());
            edittext_personalID.setText(oldForm.getPersonalID());
            edittext_systolic.setText(oldForm.getBloodPressure_Systolic());
            edittext_diastolic.setText(oldForm.getBloodPressure_Diastolic());
            edittext_bloodSugar.setText(oldForm.getBloodSugar());
            edittext_hba1c.setText(oldForm.getHba1c());
            edittext_hdl.setText(oldForm.getCholesterolHDL());
            edittext_ldl.setText(oldForm.getCholesterolLDL());
            edittext_medicalHistory.setText(oldForm.getMedicalHistory());
            edittext_note.setText(oldForm.getNote());

            spinner_result.setSelection(((ArrayAdapter)spinner_result.getAdapter()).getPosition(oldForm.getClassificationResult()));
            spinner_sex.setSelection(((ArrayAdapter)spinner_sex.getAdapter()).getPosition(oldForm.getSex()));

        }
        else if (SaveAs.equals(CONSTANTS.SAVE_AS_MODE_NEW)){
            Log.d("debug", "onCreate: Save as Mode new");
            // auto get ngày hôm nay set vào edittext
            Uri URIOriginalImage = (Uri) getIntent().getParcelableExtra("URIOriginalImage");
            imageViewOriginalImage.setImageURI(URIOriginalImage);
            //Log.d("save activity", "onCreate: imageview.getdrawable value ==" + imageViewOriginalImage.getDrawable());
        }
        // bắt sự kiện bấm button hiện calendar
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                SaveActivity.super.onBackPressed();
                return true;
            case R.id.menu_save:

                String SaveAs = (String) getIntent().getSerializableExtra("Save As");
                Form prevForm = (Form)getIntent().getSerializableExtra("oldform");
                Form saveForm = new Form();
                saveForm.setToday(edittext_today.getText().toString());
                saveForm.setName(edittext_patientName.getText().toString());
                saveForm.setDateOfBirth(edittext_dateOfBirth.getText().toString());
                saveForm.setPersonalID(edittext_personalID.getText().toString());
                saveForm.setBloodPressure_Systolic(edittext_systolic.getText().toString());
                saveForm.setBloodPressure_Diastolic(edittext_diastolic.getText().toString());
                saveForm.setBloodSugar(edittext_bloodSugar.getText().toString());
                saveForm.setHba1c(edittext_hba1c.getText().toString());
                saveForm.setCholesterolLDL(edittext_ldl.getText().toString());
                saveForm.setCholesterolHDL(edittext_hdl.getText().toString());
                saveForm.setMedicalHistory(edittext_medicalHistory.getText().toString());
                saveForm.setNote(edittext_note.getText().toString());
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

                if (imageViewOriginalImage.getDrawable() != null){
                    // convert bitmap to byte array
                    byte[] bytearrayOriginalImage = convertBitmaptoByteArray(((BitmapDrawable)imageViewOriginalImage.getDrawable()).getBitmap());
                    //saveForm.setBytearrOriginalImage(bytearrayOriginalImage);
                }
                // nếu là edit bệnh nhân
                if (SaveAs.equals("OLD")){
                    boolean isUpdated = formDatabase.updateData(prevForm.getID(), saveForm);
                    if(isUpdated == true){
                        Log.d("debug", "Update successfully");
                        // hiển thị Toast thông báo đã lưu
                        Toast.makeText(getApplicationContext(), "Infomation has been updated successfully", Toast.LENGTH_SHORT).show();
                        // chuyển về màn hình chính
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // xóa các màn hình trước
                        startActivity(intent);
                    }
                    else{
                        // hiển thị Toast thông báo đã lưu
                        Toast.makeText(getApplicationContext(), "Sorry! Unexpected error!", Toast.LENGTH_SHORT).show();
                    }
                }
                // nếu là lưu mới thông tin bệnh nhân
                else if (SaveAs.equals("NEW")){
                    Bitmap bitmap = ((BitmapDrawable)imageViewOriginalImage.getDrawable()).getBitmap();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
                    String saveImageName = sdf.format(new Date()) + ".png";
                    File file = new File(directory ,saveImageName);
                    try {
                        OutputStream output = new FileOutputStream(file);
                        // Compress into png format image from 0% - 100%
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
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
                    boolean isInserted = formDatabase.insertNewForm(saveForm);
                    if(isInserted == true){
                        // hiển thị Toast thông báo đã lưu
                        Toast.makeText(getApplicationContext(), "Infomation has been saved successfully", Toast.LENGTH_SHORT).show();
                        // chuyển về màn hình chính
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // xóa các màn hình trước
                        startActivity(intent);
                    }
                    else{
                        // hiển thị Toast thông báo không lưu
                        Toast.makeText(getApplicationContext(), "Sorry! Unexpected error!", Toast.LENGTH_SHORT).show();
                    }
                }
                // mở xml và lưu
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_save, menu);
        return true;
    }

    private byte[] convertBitmaptoByteArray(Bitmap bmp){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }
}
