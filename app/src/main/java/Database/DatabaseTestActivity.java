package Database;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.roastingassistant.R;

public class DatabaseTestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        Log.d("Database", "Clearing database for testing...");
        deleteDatabase("coffeeDatabase");

        Button button1 = findViewById(R.id.DBT01button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbT01();
            }
        });
        Button button2 = findViewById(R.id.DBT02button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbT02();
            }
        });
        Button button3 = findViewById(R.id.DBT03button);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbT03();
            }
        });
        Button button4 = findViewById(R.id.DBT04button);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbT04();
            }
        });
    }

    public DatabaseHelper createDB(){
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        return db;
    }

    public void dbT01(){
        Log.d("Database", "Running test DBT01...");
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());
        db.triggerDbBuild();
    }
    public void dbT02(){
        Log.d("Database", "Running test DBT02...");
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        Bean bean = new Bean();
        bean.name = "TestBean";
        bean.flavours = "Chocolate";
        db.addBean(bean);
    }
    public void dbT03(){
        Log.d("Database", "Running test DBT03...");
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        Checkpoint check = new Checkpoint();
        check.name = "TestCheckpoint";
        check.temperature=425;
        check.id = (int)db.addCheckpoint(check);

        Bean bean = new Bean();
        bean.name = "TestBean";
        bean.flavours = "Chocolate";
        bean.id = db.addBean(bean);

        Roast roast = new Roast();
        roast.name = "TestRoast";
        roast.dropTemp = 425;
        roast.checkpoints.add(check);
        roast.bean = bean;
        roast.roastLevel = "Dark";
        db.addRoast(roast);
    }
    public void dbT04(){
        Log.d("Database", "Running test DBT04...");
        DatabaseHelper db = DatabaseHelper.getInstance(this.getApplicationContext());

        Checkpoint check = new Checkpoint();
        check.name = "TestCheckpoint";
        check.temperature=425;
        check.id = (int)db.addCheckpoint(check);

        Bean bean = new Bean();
        bean.name = "TestBean";
        bean.flavours = "Chocolate";
        bean.id = db.addBean(bean);

        Roast roast = new Roast();
        roast.name = "Colombian";
        roast.dropTemp = 425;
        roast.checkpoints.add(check);
        roast.bean = bean;
        roast.roastLevel = "Dark";
        roast.id = db.addRoast(roast);

        Blend blend = new Blend();
        blend.roasts.add(roast);
        blend.name = "TestBlend";

        db.addBlend(blend);
    }
}