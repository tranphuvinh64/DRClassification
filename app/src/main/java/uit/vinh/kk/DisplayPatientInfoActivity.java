package uit.vinh.kk;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class DisplayPatientInfoActivity extends AppCompatActivity{
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

        // lấy dữ liệu từ activity trước
        Form prevForm = (Form)getIntent().getSerializableExtra("patientinfo");
        Log.d("debug", "previous info: " + prevForm.getName());
        // không cho sửa khi đang view
        Name.setKeyListener(null);
        Name.setText(prevForm.getName());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                backtoHomeScreen();
                return true;

            default:break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void backtoHomeScreen(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
