package Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    //Database info
    private static final String DATABASE_NAME = "coffeeDatabase";
    private static final int DATABASE_VERSION = 1;

    //---------Table names------------
    private static final String TABLE_BLEND = "blend";
    private static final String TABLE_ROAST_PROFILE = "roast_profile";
    private static final String TABLE_CHECKPOINTS = "checkpoints";
    private static final String TABLE_ROAST_CHECKPOINT = "roast_checkpoint";
    private static final String TABLE_BEAN = "bean";
    private static final String TABLE_ROAST_RECORD = "roast_record";
    private static final String TABLE_ROASTER = "roaster";
    private static final String TABLE_FLAVOURS = "flavours";
    private static final String TABLE_FLAVOUR = "flavour";

    //---------Blend table columns------------
    private static final String KEY_BLEND_NAME = "name";
    private static final String KEY_BLEND_ROAST_PROFILE_ID = "roast_profile_id";

    //---------roast_profile table columns------------
    private static final String KEY_ROAST_PROFILE_ID = "id";
    private static final String KEY_ROAST_PROFILE_NAME = "name";
    private static final String KEY_ROAST_PROFILE_ROAST = "roast";
    private static final String KEY_ROAST_PROFILE_BEAN_ID = "bean_id";
    private static final String KEY_ROAST_PROFILE_CHARGE_TEMP = "charge_temp";
    private static final String KEY_ROAST_PROFILE_DROP_TEMP = "drop_temp";
    private static final String KEY_ROAST_PROFILE_FLAVOUR = "flavour";

    //---------checkpoints table columns------------
    private static final String KEY_CHECKPOINTS_ROAST_PROFILE_ID = "id";
    private static final String KEY_CHECKPOINTS_CHECKPOINT = "checkpoint";

    //---------roast_checkpoint table columns------------
    private static final String KEY_ROAST_CHECKPOINT_ID = "id";
    private static final String KEY_ROAST_CHECKPOINT_NAME = "name";
    private static final String KEY_ROAST_CHECKPOINT_TRIGGER = "trigger";
    private static final String KEY_ROAST_CHECKPOINT_TIME = "time";
    private static final String KEY_ROAST_CHECKPOINT_TEMPERATURE = "temperature";

    //---------bean table columns------------
    private static final String KEY_BEAN_ID = "id";
    private static final String KEY_BEAN_NAME = "name";
    private static final String KEY_BEAN_ORIGIN = "origin";
    private static final String KEY_BEAN_FARM = "farm";
    private static final String KEY_BEAN_ALTITUDE = "altitude";
    private static final String KEY_BEAN_PROCESS_STYLE = "process_style";
    private static final String KEY_BEAN_FLAVOUR = "flavour";
    private static final String KEY_BEAN_BODY = "body";
    private static final String KEY_BEAN_ACIDITY = "acidity";
    private static final String KEY_BEAN_PRICE_PER_POUND = "price_per_pound";

    //---------roast_record table columns------------
    private static final String KEY_ROAST_RECORD_ID = "id";
    private static final String KEY_ROAST_RECORD_ROAST_PROFILE_ID = "roast_profile_id";
    private static final String KEY_ROAST_RECORD_FILENAME = "filename";
    private static final String KEY_ROAST_RECORD_START_WEIGHT = "start_weight";
    private static final String KEY_ROAST_RECORD_END_WEIGHT = "end_weight";
    private static final String KEY_ROAST_RECORD_ROASTER = "roaster";

    //---------roaster table columns------------
    private static final String KEY_ROASTER_NAME = "name";
    private static final String KEY_ROASTER_BRAND = "brand";
    private static final String KEY_ROASTER_CAPACITY_POUNDS = "capacity_pounds";
    private static final String KEY_ROASTER_HEATING_TYPE = "heating_type";
    private static final String KEY_ROASTER_DRUM_SPEED = "drum_speed";

    //---------flavours table columns------------
    private static final String KEY_FLAVOURS_ID = "id";
    private static final String KEY_FLAVOURS_flavour = "flavour";

    //---------flavour table columns------------
    private static final String KEY_FLAVOUR_DESCRIPTION = "description";

    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * called when the db connection is being configured.
     * config settings like foreign key support, write-ahead logging etc
     * @param db
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_BLEND_TABLE = "CREATE TABLE "+TABLE_BLEND+
                "("+
                    KEY_BLEND_NAME + " TEXT," +
                    KEY_BLEND_ROAST_PROFILE_ID + " TEXT" +
                ")";

        String CREATE_ROAST_PROFILE_TABLE = "CREATE TABLE "+TABLE_ROAST_PROFILE+
                "("+
                    KEY_ROAST_PROFILE_ID + " INTEGER PRIMARY KEY," +
                    KEY_ROAST_PROFILE_NAME + " TEXT," +
                    KEY_ROAST_PROFILE_ROAST + " TEXT," +
                    KEY_ROAST_PROFILE_BEAN_ID + " INTEGER REFERENCES "+TABLE_BEAN+","+
                    KEY_ROAST_PROFILE_CHARGE_TEMP + " INTEGER," +
                    KEY_ROAST_PROFILE_DROP_TEMP + " INTEGER,"+
                    KEY_ROAST_PROFILE_FLAVOUR+" FLAVOUR"+
                ")";

        String CREATE_CHECKPOINTS_TABLE = "CREATE TABLE "+TABLE_CHECKPOINTS+
                "("+
                    KEY_ROAST_PROFILE_ID + "INTEGER REFERENCES "+TABLE_ROAST_PROFILE+
                    KEY_ROAST_CHECKPOINT_ID + "INTEGER REFERENCES "+TABLE_ROAST_CHECKPOINT+
                ")";

        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
