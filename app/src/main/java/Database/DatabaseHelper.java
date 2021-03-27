package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.util.Log;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper dbInstance;

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
    private static final String KEY_BLEND_ROAST_PROFILE_ID_FK = "roast_profile_id";

    //---------roast_profile table columns------------
    private static final String KEY_ROAST_PROFILE_ID = "id";
    private static final String KEY_ROAST_PROFILE_NAME = "name";
    private static final String KEY_ROAST_PROFILE_ROAST = "roast";
    private static final String KEY_ROAST_PROFILE_BEAN_ID_FK = "bean_id";
    private static final String KEY_ROAST_PROFILE_CHARGE_TEMP = "charge_temp";
    private static final String KEY_ROAST_PROFILE_DROP_TEMP = "drop_temp";
    private static final String KEY_ROAST_PROFILE_FLAVOUR = "flavour";

    //---------checkpoints table columns------------
    private static final String KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK = "id";
    private static final String KEY_CHECKPOINTS_CHECKPOINT_FK = "checkpoint";

    //---------roast_checkpoint table columns------------
    private static final String KEY_ROAST_CHECKPOINT_ID = "id";
    private static final String KEY_ROAST_CHECKPOINT_NAME = "name";
    private static final String KEY_ROAST_CHECKPOINT_TRIGGER = "check_trigger";
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
    private static final String KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK = "roast_profile_id";
    private static final String KEY_ROAST_RECORD_FILENAME = "filename";
    private static final String KEY_ROAST_RECORD_START_WEIGHT = "start_weight";
    private static final String KEY_ROAST_RECORD_END_WEIGHT = "end_weight";
    private static final String KEY_ROAST_RECORD_ROASTER_ID = "roaster_id";

    //---------roaster table columns------------
    private static final String KEY_ROASTER_ID = "id";
    private static final String KEY_ROASTER_NAME = "name";
    private static final String KEY_ROASTER_BRAND = "brand";
    private static final String KEY_ROASTER_CAPACITY_POUNDS = "capacity_pounds";
    private static final String KEY_ROASTER_HEATING_TYPE = "heating_type";
    private static final String KEY_ROASTER_DRUM_SPEED = "drum_speed";

    //---------flavours table columns------------
    private static final String KEY_FLAVOURS_BEAN_ID = "bean_id";
    private static final String KEY_FLAVOURS_FLAVOUR_ID_FK = "flavour_ID";

    //---------flavour table columns------------
    private static final String KEY_FLAVOUR_ID = "id";
    private static final String KEY_FLAVOUR_DESCRIPTION = "description";

    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Function for accessing the database. handles creation of new DatabaseHelper so that there is only one copy.
     * Makes DatabaseHelper a singleton.
     * @param context
     * @return copy of the one DatabaseHelper object.
     */
    public static synchronized DatabaseHelper getInstance(Context context){
        if(dbInstance==null){
            dbInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return dbInstance;
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
        Log.i("Database", "Creating database...");
        String CREATE_BLEND_TABLE = "CREATE TABLE "+TABLE_BLEND+
                "("+
                    KEY_BLEND_NAME + " TEXT," +
                    KEY_BLEND_ROAST_PROFILE_ID_FK + " INTEGER REFERENCES " + TABLE_ROAST_PROFILE +
                ")";

        String CREATE_ROAST_PROFILE_TABLE = "CREATE TABLE "+TABLE_ROAST_PROFILE+
                "("+
                    KEY_ROAST_PROFILE_ID + " INTEGER PRIMARY KEY," +
                    KEY_ROAST_PROFILE_NAME + " TEXT," +
                    KEY_ROAST_PROFILE_ROAST + " TEXT," +
                    KEY_ROAST_PROFILE_BEAN_ID_FK + " INTEGER REFERENCES "+TABLE_BEAN+","+
                    KEY_ROAST_PROFILE_CHARGE_TEMP + " INTEGER," +
                    KEY_ROAST_PROFILE_DROP_TEMP + " INTEGER,"+
                    KEY_ROAST_PROFILE_FLAVOUR+" FLAVOUR"+
                ")";

        String CREATE_CHECKPOINTS_TABLE = "CREATE TABLE "+TABLE_CHECKPOINTS+
                "("+
                    KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK + " INTEGER REFERENCES "+TABLE_ROAST_PROFILE+","+
                    KEY_CHECKPOINTS_CHECKPOINT_FK + " INTEGER REFERENCES "+TABLE_ROAST_CHECKPOINT+
                ")";

        String  CREATE_ROAST_CHECKPOINT_TABLE = "CREATE TABLE "+TABLE_ROAST_CHECKPOINT+
                "("+
                    KEY_ROAST_CHECKPOINT_ID+" INTEGER PRIMARY KEY,"+
                    KEY_ROAST_CHECKPOINT_NAME+" TEXT,"+
                    KEY_ROAST_CHECKPOINT_TRIGGER+" TEXT,"+
                    KEY_ROAST_CHECKPOINT_TIME+" INTEGER,"+
                    KEY_ROAST_CHECKPOINT_TEMPERATURE+" INTEGER"+
                ")";

        String CREATE_BEAN_TABLE = "CREATE TABLE "+TABLE_BEAN+
                "("+
                    KEY_BEAN_ID+" INTEGER PRIMARY KEY,"+
                    KEY_BEAN_NAME+" TEXT,"+
                    KEY_BEAN_ORIGIN+" TEXT,"+
                    KEY_BEAN_FARM+" TEXT,"+
                    KEY_BEAN_ALTITUDE+" INTEGER,"+
                    KEY_BEAN_PROCESS_STYLE+" TEXT,"+
                    KEY_BEAN_FLAVOUR+" TEXT,"+
                    KEY_BEAN_BODY+" TEXT,"+
                    KEY_BEAN_ACIDITY+" TEXT,"+
                    KEY_BEAN_PRICE_PER_POUND+" DECIMAL"+
                ")";

        String CREATE_ROAST_RECORD_TABLE = "CREATE TABLE "+TABLE_ROAST_RECORD+
                "("+
                    KEY_ROAST_RECORD_ID+" INTEGER PRIMARY KEY,"+
                    KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK+" INTEGER REFERENCES "+TABLE_ROAST_PROFILE+","+
                    KEY_ROAST_RECORD_FILENAME+" TEXT,"+
                    KEY_ROAST_RECORD_START_WEIGHT+" DECIMAL,"+
                    KEY_ROAST_RECORD_END_WEIGHT+" DECIMAL,"+
                    KEY_ROAST_RECORD_ROASTER_ID+" INTEGER REFERENCES "+KEY_ROASTER_ID+
                ")";

        String CREATE_ROASTER_TABLE = "CREATE TABLE "+TABLE_ROASTER+
                "("+
                    KEY_ROASTER_ID+" INTEGER PRIMARY KEY,"+
                    KEY_ROASTER_NAME+" TEXT,"+
                    KEY_ROASTER_BRAND+" TEXT,"+
                    KEY_ROASTER_CAPACITY_POUNDS+" DECIMAL,"+
                    KEY_ROASTER_HEATING_TYPE+" TEXT,"+
                    KEY_ROASTER_DRUM_SPEED+" DECIMAL"+
                ")";

        String CREATE_FLAVOURS_TABLE = "CREATE TABLE "+TABLE_FLAVOURS+
                "("+
                    KEY_FLAVOURS_BEAN_ID+" INTEGER,"+
                    KEY_FLAVOURS_FLAVOUR_ID_FK+" INTEGER REFERENCES "+TABLE_FLAVOUR+
                ")";


        String CREATE_FLAVOUR_TABLE = "CREATE TABLE "+TABLE_FLAVOUR+
                "("+
                    KEY_FLAVOUR_ID+" INTEGER PRIMARY KEY,"+
                    KEY_FLAVOUR_DESCRIPTION+" TEXT"+
                ")";

        db.execSQL(CREATE_BLEND_TABLE);
        db.execSQL(CREATE_ROAST_PROFILE_TABLE);
        db.execSQL(CREATE_CHECKPOINTS_TABLE);
        db.execSQL(CREATE_ROAST_CHECKPOINT_TABLE);
        db.execSQL(CREATE_BEAN_TABLE);
        db.execSQL(CREATE_ROAST_RECORD_TABLE);
        db.execSQL(CREATE_ROASTER_TABLE);
        db.execSQL(CREATE_FLAVOURS_TABLE);
        db.execSQL(CREATE_FLAVOUR_TABLE);

        Log.i("Database", "Database created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion){
            Log.i("Database", "Database upgrading...");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_BLEND);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_PROFILE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CHECKPOINTS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_CHECKPOINT);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_BEAN);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_RECORD);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROASTER);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLAVOURS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLAVOUR);

            onCreate(db);
            Log.i("Database", "Database upgrade complete.");
        }
    }

    /**
     * Add a Bean object to the database.
     * @param bean object to be added to the database.
     */
    public void addBean(Bean bean){
        Log.i("Database", "Adding bean entry to database...");
        //Create or open database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_BEAN_NAME, bean.name);
            values.put(KEY_BEAN_ORIGIN, bean.origin);
            values.put(KEY_BEAN_FARM, bean.farm);
            values.put(KEY_BEAN_ALTITUDE, bean.altitude);
            values.put(KEY_BEAN_PROCESS_STYLE, bean.process);
            values.put(KEY_BEAN_FLAVOUR, bean.flavours);
            values.put(KEY_BEAN_BODY, bean.body);
            values.put(KEY_BEAN_ACIDITY, bean.acidity);
            values.put(KEY_BEAN_PRICE_PER_POUND, bean.pricePerPound);

            db.insertOrThrow(TABLE_BEAN, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("Database", "Error adding bean entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }
    }

    /**
     * Get a list of all Bean objects contained in the database.
     * @return List of Bean objects.
     */
    public List<Bean> getAllBeans(){
        List<Bean> beans = new ArrayList<Bean>();

        String BEAN_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_BEAN);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BEAN_SELECT_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                do {
                    Bean bean = new Bean();
                    bean.name = cursor.getString(cursor.getColumnIndex(KEY_BEAN_NAME));

                    beans.add(bean);
                }while(cursor.moveToNext());
            }
        }catch (Exception e) {
            Log.d("Database", "Error getting bean from database. " + e.getMessage());
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }
        return beans;
    }
}
