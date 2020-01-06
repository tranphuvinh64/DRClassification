package uit.vinh.kk;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class DisplayPatientInfoActivity extends AppCompatActivity{

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

    private ImageView imageViewOriginalImage;
    @Override
    protected void onCreate(Bundle savedInstanceState){
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

        spinner_sex = findViewById(R.id.info_spinner_sex);
        spinner_result = findViewById(R.id.info_spinner_result);

        imageViewOriginalImage = findViewById(R.id.info_imageview_OriginalImage);

//        textView.setTag(textView.getKeyListener());
//        textView.setKeyListener(null);
        //Name.setTag(Name.getKeyListener());

        // lấy dữ liệu từ activity trước
        Form prevForm = (Form)getIntent().getSerializableExtra("patientinfo");

        // không cho sửa khi đang view

        //edittext_idForm.setKeyListener(null);
        textInputEditText_Today.setKeyListener(null);
        textInputEditText_PatientName.setKeyListener(null);
        textInputEditText_DateOfBirth.setKeyListener(null);
        textInputEditText_PersonalID.setKeyListener(null);
        textInputEditText_Systolic.setKeyListener(null);
        textInputEditText_Diastolic.setKeyListener(null);
        textInputEditText_BloodSugar.setKeyListener(null);
        textInputEditText_Hba1c.setKeyListener(null);
        textInputEditText_HDL.setKeyListener(null);
        textInputEditText_LDL.setKeyListener(null);
        textInputEditText_MedicalHistory.setKeyListener(null);
        textInputEditText_Note.setKeyListener(null);



        spinner_result.setEnabled(false);
        spinner_sex.setEnabled(false);

        //edittext_idForm.setText(prevForm.getID());
        textInputEditText_PatientName.setText(prevForm.getName());
        textInputEditText_Today.setText(prevForm.getToday());
        textInputEditText_DateOfBirth.setText(prevForm.getDateOfBirth());
        textInputEditText_PersonalID.setText(prevForm.getPersonalID());
        textInputEditText_Systolic.setText(prevForm.getBloodPressure_Systolic());
        textInputEditText_Diastolic.setText(prevForm.getBloodPressure_Diastolic());
        textInputEditText_BloodSugar.setText(prevForm.getBloodSugar());
        textInputEditText_Hba1c.setText(prevForm.getHba1c());
        textInputEditText_HDL.setText(prevForm.getCholesterolHDL());
        textInputEditText_LDL.setText(prevForm.getCholesterolLDL());
        textInputEditText_MedicalHistory.setText(prevForm.getMedicalHistory());
        textInputEditText_Note.setText(prevForm.getNote());


        spinner_result.setSelection(((ArrayAdapter)spinner_result.getAdapter()).getPosition(prevForm.getClassificationResult()));
        spinner_sex.setSelection(((ArrayAdapter)spinner_sex.getAdapter()).getPosition(prevForm.getSex()));

        //byte[] bitmapdata = prevForm.getBytearrOriginalImage();
        //Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        //imageViewOriginalImage.setImageBitmap(bitmap);

        Log.d("debug", "onCreate: uri == " + prevForm.getPathOriginalImage());
        if (prevForm.getPathOriginalImage().equals("null") == false){
            Uri uri = Uri.parse(prevForm.getPathOriginalImage());
            Log.d("debug", "onCreate: uri == " + uri);
            if (uri != null ){
                imageViewOriginalImage.setImageURI(uri);
            }

        }
    }


        @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                DisplayPatientInfoActivity.super.onBackPressed();
                //backtoHomeScreen();
                return true;
            case R.id.menu_edit:
                // gửi tín hiệu để biết là lưu thông tin edit
                Form prevForm = (Form)getIntent().getSerializableExtra("patientinfo");
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                intent.putExtra("Save As", "OLD");
                intent.putExtra("oldform", prevForm);
                getIntent().getSerializableExtra("Save As");
                // chuyển màn hình SaveActivity
                startActivity(intent);
                break;
            default:break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_edit, menu);
        return true;
    }
}
