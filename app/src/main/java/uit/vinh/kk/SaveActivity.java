package uit.vinh.kk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SaveActivity extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);
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
        //tạo giá trị cho spinner


        // auto get ngày hôm nay set vào edittext

        // bắt sự kiện bấm button hiện calendar
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                SaveActivity.super.onBackPressed();
                //backtoHomeScreen();
                return true;
            case R.id.menu_save:

                // get dữ liệu từ các EditText
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

                Log.d("debug", "thông tin bệnh nhân" + saveForm.toString());

                // mở xml và lưu

                // chuyển về màn hình chính

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // xóa các màn hình trước
                startActivity(intent);

                // hiển thị Toast thông báo đã lưu
                Toast.makeText(getApplicationContext(), "Infomation has been saved successfully", Toast.LENGTH_SHORT).show();

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
}
