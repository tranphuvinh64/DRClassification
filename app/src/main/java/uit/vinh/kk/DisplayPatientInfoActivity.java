package uit.vinh.kk;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayPatientInfoActivity extends AppCompatActivity {
    EditText Name;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patientinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Name = findViewById(R.id.info_edittext_fullname);
//        textView.setTag(textView.getKeyListener());
//        textView.setKeyListener(null);
        //Name.setTag(Name.getKeyListener());

        // không cho sửa khi đang view
        Name.setKeyListener(null);

    }
}
