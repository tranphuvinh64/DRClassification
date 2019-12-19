package uit.vinh.kk;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classificationresult);
//        Toolbar toolbar = findViewById(R.id.displaypatientinfo);
//
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Drawable drawable= getResources().getDrawable(android.R.drawable.ic_menu_search);
//        getSupportActionBar().setHomeAsUpIndicator(drawable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_back_menuitem, menu);
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
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
