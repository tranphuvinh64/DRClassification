package uit.vinh.kk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayPatientInfoActivity extends AppCompatActivity{
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
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // create components
        edittext_idForm = findViewById(R.id.info_edittext_idform);
        edittext_today = findViewById(R.id.info_edittext_today);
        edittext_patientName = findViewById(R.id.info_edittext_fullname);
        edittext_dateOfBirth = findViewById(R.id.info_edittext_dob);
        //spinner sex later
        edittext_personalID = findViewById(R.id.info_edittext_personalID);
        // spinner result
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
//        textView.setTag(textView.getKeyListener());
//        textView.setKeyListener(null);
        //Name.setTag(Name.getKeyListener());

        // lấy dữ liệu từ activity trước
        Form prevForm = (Form)getIntent().getSerializableExtra("patientinfo");
        Log.d("debug", "previous info: " + prevForm.getName());
        // không cho sửa khi đang view

        edittext_idForm.setKeyListener(null);
        edittext_today.setKeyListener(null);
        edittext_patientName.setKeyListener(null);
        edittext_dateOfBirth.setKeyListener(null);
        edittext_personalID.setKeyListener(null);
        edittext_systolic.setKeyListener(null);
        edittext_diastolic.setKeyListener(null);
        edittext_bloodSugar.setKeyListener(null);
        edittext_hba1c.setKeyListener(null);
        edittext_hdl.setKeyListener(null);
        edittext_ldl.setKeyListener(null);
        edittext_medicalHistory.setKeyListener(null);
        edittext_note.setKeyListener(null);

        button_calendarDOB.setVisibility(View.INVISIBLE);
        button_calendarToday.setVisibility(View.INVISIBLE);

        spinner_result.setEnabled(false);
        spinner_sex.setEnabled(false);


        edittext_patientName.setText(prevForm.getName());

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
                // gửi thông tin để biết là lưu thông tin edit

                // chuyển màn hình SaveActivity
                Intent intent = new Intent(getApplicationContext(), SaveActivity.class);
                startActivity(intent);
                break;
            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
//    private void backtoHomeScreen(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar_edit, menu);
        return true;
    }
}