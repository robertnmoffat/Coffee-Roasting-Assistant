package Database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;

import com.example.roastingassistant.R;

public class DatabaseTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);
    }

    public DatabaseHelper createDB(){
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        return db;
    }
}