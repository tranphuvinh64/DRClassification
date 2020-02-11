package uit.vinh.kk;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

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
    private RadioButton rbt_male;
    private RadioButton rbt_female;

    private PhotoView photoViewOriginalImage;
    private PhotoView photoViewContrastEnhnace;
    static {
        if(!OpenCVLoader.initDebug())
            Log.d("...", "OpenCv load fail!");
        else
            Log.d("...", "OpenCv success.");
    }
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

        spinner_result = findViewById(R.id.info_spinner_result);
        rbt_male = findViewById(R.id.rbt_male);
        rbt_female = findViewById(R.id.rbt_female);

        photoViewOriginalImage = findViewById(R.id.info_photoview_OriginalImage);
        photoViewContrastEnhnace = findViewById(R.id.info_photoview_ContrastEnhance);

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


        rbt_female.setEnabled(false);
        rbt_male.setEnabled(false);
        if (prevForm.getSex().equals("Male")){
            rbt_male.setChecked(true);
        }
        else{
            rbt_female.setChecked(true);
        }
        spinner_result.setEnabled(false);
        //spinner_sex.setEnabled(false);

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
        //spinner_sex.setSelection(((ArrayAdapter)spinner_sex.getAdapter()).getPosition(prevForm.getSex()));

        Log.d("debug", "onCreate: image Path  == " + prevForm.getPathOriginalImage());
        Bitmap bitmapOriginalImage = null;
        try {
            bitmapOriginalImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse("file://"+prevForm.getPathOriginalImage()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bitmapOriginalImage!=null){
            if (bitmapOriginalImage.getHeight()>CONSTANTS.MAX_HEIGHT || bitmapOriginalImage.getWidth()>CONSTANTS.MAX_WIDTH){
                float scale = (float) 1.0;
                for (float i = 0; i <= 1.0 ; i = i + (float)0.05){
                    if(bitmapOriginalImage.getWidth()*i<=CONSTANTS.MAX_WIDTH && bitmapOriginalImage.getHeight()*i<=CONSTANTS.MAX_HEIGHT){
                        scale = i;
                    }
                }
                bitmapOriginalImage = Bitmap.createScaledBitmap(bitmapOriginalImage,(int)(bitmapOriginalImage.getWidth()*scale) ,(int)(bitmapOriginalImage.getHeight()*scale) ,true );
            }
            photoViewOriginalImage.setImageBitmap(bitmapOriginalImage);
            photoViewContrastEnhnace.setImageBitmap(contrastEnhance(bitmapOriginalImage));
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
    private Bitmap contrastEnhance(Bitmap bitmapsrc){
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
        Bitmap bitmapdest = Bitmap.createBitmap(matdest.cols(),matdest.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(matdest,bitmapdest);
        return bitmapdest;
    }
}
