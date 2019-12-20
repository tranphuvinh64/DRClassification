package uit.vinh.kk;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

public class PatientInfoActivity extends AppCompatActivity implements View.OnClickListener {
    Button saveButton;
    EditText EdText_ID, EdText_Name;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_savepatientinfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        saveButton = findViewById(R.id.id_savebutton);
        EdText_ID = findViewById(R.id.id_ID);
        EdText_Name = findViewById(R.id.id_Name);
        saveButton.setOnClickListener(this);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back_forward, menu);
        return true;
    }
    @Override
    public void onClick(View v){
        if(v.getId() == R.id.id_savebutton){
            Log.d("click", "onClick: clicked button save");
            // check XML file exist
            // create XML
            // get data from component
            String Name = EdText_Name.getText().toString();

            Log.d("Name", Name);
            Log.d("path", Environment.getExternalStorageDirectory().getAbsolutePath());
            // write XML



            // test write file
            final String xmlFile = "patientData";
            try {
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + xmlFile);
                Log.d("debug", Environment.getExternalStorageDirectory().getAbsolutePath()+ "/" + xmlFile);
                if (file.exists() == false){
                    file.createNewFile();
                }

                FileOutputStream fileos = new FileOutputStream(file);

                XmlSerializer xmlSerializer = Xml.newSerializer();
                StringWriter writer = new StringWriter();
                xmlSerializer.setOutput(writer);
                xmlSerializer.startDocument("UTF-8", true);
                xmlSerializer.startTag(null, "patient");
                xmlSerializer.startTag(null, "ID");
                xmlSerializer.text(EdText_ID.getText().toString());
                xmlSerializer.endTag(null, "ID");
                xmlSerializer.startTag(null,"Name");
                xmlSerializer.text(EdText_Name.getText().toString());
                xmlSerializer.endTag(null, "Name");
                xmlSerializer.endTag(null, "patient");
                xmlSerializer.endDocument();
                xmlSerializer.flush();
                String dataWrite = writer.toString();
                fileos.write(dataWrite.getBytes());
                fileos.close();
            }
            catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
