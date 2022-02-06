package Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.provider.ContactsContract;
import android.speech.RecognizerResultsIntent;
import android.util.Log;

import com.example.roastingassistant.user_interface.BlendActivity;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Currency;
import java.util.List;

import kotlinx.coroutines.DispatchedKt;

/**
 * Singleton class handling all database interactions.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private static DatabaseHelper dbInstance;

    //Database info
    private static final String DATABASE_NAME = "coffeeDatabase";
    private static final int DATABASE_VERSION = 3;

    //---------Table names------------
    private static final String TABLE_BLEND = "blend";
    private static final String TABLE_ROAST_PROFILE = "roast_profile";
    private static final String TABLE_ROAST_BLEND = "roast_blend";
    private static final String TABLE_CHECKPOINTS = "checkpoints";
    private static final String TABLE_ROAST_CHECKPOINT = "roast_checkpoint";
    private static final String TABLE_BEAN = "bean";
    private static final String TABLE_ROAST_RECORD = "roast_record";
    private static final String TABLE_ROASTER = "roaster";
    private static final String TABLE_FLAVOURS = "flavours";
    private static final String TABLE_FLAVOUR = "flavour";

    //---------Blend table columns------------
    private static final String KEY_BLEND_NAME = "name";
    private static final String KEY_BLEND_DESCRIPTION = "description";
    private static final String KEY_BLEND_ID = "blend_id";

    //---------Roast_blend columns-------------
    private static final String KEY_ROAST_BLEND_ROASTID_FK = "roast_profile_id";
    private static final String KEY_ROAST_BLEND_BLENDID_FK = "blend_id";

    //---------roast_profile table columns------------
    private static final String KEY_ROAST_PROFILE_ID = "roast_profile_id";
    private static final String KEY_ROAST_PROFILE_NAME = "roast_profile_name";
    private static final String KEY_ROAST_PROFILE_ROAST = "roast";
    private static final String KEY_ROAST_PROFILE_BEAN_ID_FK = "bean_id";
    private static final String KEY_ROAST_PROFILE_CHARGE_TEMP = "charge_temp";
    private static final String KEY_ROAST_PROFILE_DROP_TEMP = "drop_temp";
    private static final String KEY_ROAST_PROFILE_FLAVOUR = "flavour";

    //---------checkpoints table columns------------
    private static final String KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK = "id";
    private static final String KEY_CHECKPOINTS_CHECKPOINT_FK = "checkpoint";

    //---------roast_checkpoint table columns------------
    private static final String KEY_ROAST_CHECKPOINT_ID = "checkpoint_id";
    private static final String KEY_ROAST_CHECKPOINT_NAME = "checkpoint_name";
    private static final String KEY_ROAST_CHECKPOINT_TRIGGER = "check_trigger";
    private static final String KEY_ROAST_CHECKPOINT_TIME = "time";
    private static final String KEY_ROAST_CHECKPOINT_TEMPERATURE = "temperature";

    //---------bean table columns------------
    private static final String KEY_BEAN_ID = "bean_id";
    private static final String KEY_BEAN_NAME = "bean_name";
    private static final String KEY_BEAN_ORIGIN = "origin";
    private static final String KEY_BEAN_FARM = "farm";
    private static final String KEY_BEAN_ALTITUDE = "altitude";
    private static final String KEY_BEAN_PROCESS_STYLE = "process_style";
    private static final String KEY_BEAN_FLAVOUR = "flavour";
    private static final String KEY_BEAN_BODY = "body";
    private static final String KEY_BEAN_ACIDITY = "acidity";
    private static final String KEY_BEAN_PRICE_PER_POUND = "price_per_pound";
    private static final String KEY_BEAN_DRYING_METHOD = "drying_method";

    //---------roast_record table columns------------
    private static final String KEY_ROAST_RECORD_ID = "roast_record_id";
    private static final String KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK = "roast_profile_id";
    private static final String KEY_ROAST_RECORD_FILENAME = "filename";
    private static final String KEY_ROAST_RECORD_START_WEIGHT = "start_weight";
    private static final String KEY_ROAST_RECORD_END_WEIGHT = "end_weight";
    private static final String KEY_ROAST_RECORD_DATETIME = "datetime";
    private static final String KEY_ROAST_RECORD_FILESIZE = "filesize";
    //private static final String KEY_ROAST_RECORD_ROASTER_ID = "roaster_id";

    //---------roaster table columns------------
    private static final String KEY_ROASTER_ID = "roaster_id";
    private static final String KEY_ROASTER_NAME = "roaster_name";
    private static final String KEY_ROASTER_DESCRIPTION = "roaster_description";
    private static final String KEY_ROASTER_BRAND = "brand";
    private static final String KEY_ROASTER_CAPACITY_POUNDS = "capacity_pounds";
    private static final String KEY_ROASTER_HEATING_TYPE = "heating_type";
    private static final String KEY_ROASTER_DRUM_SPEED = "drum_speed";

    //---------flavours table columns------------
    private static final String KEY_FLAVOURS_BEAN_ID = "bean_id";
    private static final String KEY_FLAVOURS_FLAVOUR_ID_FK = "flavour_id";

    //---------flavour table columns------------
    private static final String KEY_FLAVOUR_ID = "id";
    private static final String KEY_FLAVOUR_DESCRIPTION = "description";

    /**
     * Singleton so private constructor. Only instantiated by self.
     * @param context Activity context
     */
    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Function for accessing the database. handles creation of new DatabaseHelper so that there is only one copy.
     * Makes DatabaseHelper a singleton.
     * @param context Activity context
     * @return copy of the one DatabaseHelper object.
     */
    public static synchronized DatabaseHelper getInstance(Context context){
        if(dbInstance==null){
            dbInstance = new DatabaseHelper(context);
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
                    KEY_BLEND_ID + " INTEGER PRIMARY KEY,"+
                    KEY_BLEND_DESCRIPTION + " TEXT,"+
                    KEY_BLEND_NAME + " TEXT"+
                ")";

        String CREATE_ROAST_BLEND_TABLE = "CREATE TABLE "+TABLE_ROAST_BLEND+
                "("+
                    KEY_ROAST_BLEND_BLENDID_FK+" INTEGER REFERENCES "+TABLE_BLEND+", "+
                    KEY_ROAST_BLEND_ROASTID_FK+" INTEGER REFERENCES "+TABLE_ROAST_PROFILE+
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
                    KEY_BEAN_DRYING_METHOD+" TEXT,"+
                    KEY_BEAN_PRICE_PER_POUND+" DECIMAL"+
                ")";

        String CREATE_ROAST_RECORD_TABLE = "CREATE TABLE "+TABLE_ROAST_RECORD+
                "("+
                    KEY_ROAST_RECORD_ID+" INTEGER PRIMARY KEY,"+
                    KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK+" INTEGER REFERENCES "+TABLE_ROAST_PROFILE+","+
                    KEY_ROAST_RECORD_FILENAME+" TEXT,"+
                    KEY_ROAST_RECORD_START_WEIGHT+" DECIMAL,"+
                    KEY_ROAST_RECORD_END_WEIGHT+" DECIMAL,"+
                    KEY_ROAST_RECORD_DATETIME+" TEXT,"+
                    KEY_ROAST_RECORD_FILESIZE+" INTEGER"+
                    //KEY_ROAST_RECORD_ROASTER_ID+" INTEGER REFERENCES "+KEY_ROASTER_ID+
                ")";

        String CREATE_ROASTER_TABLE = "CREATE TABLE "+TABLE_ROASTER+
                "("+
                    KEY_ROASTER_ID+" INTEGER PRIMARY KEY,"+
                    KEY_ROASTER_NAME+" TEXT,"+
                    KEY_ROASTER_DESCRIPTION+" TEXT,"+
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
        db.execSQL(CREATE_ROAST_BLEND_TABLE);
        db.execSQL(CREATE_CHECKPOINTS_TABLE);
        db.execSQL(CREATE_ROAST_CHECKPOINT_TABLE);
        db.execSQL(CREATE_BEAN_TABLE);
        db.execSQL(CREATE_ROAST_RECORD_TABLE);
        db.execSQL(CREATE_ROASTER_TABLE);
        db.execSQL(CREATE_FLAVOURS_TABLE);
        db.execSQL(CREATE_FLAVOUR_TABLE);

        Log.i("Database", "Database created.");
    }

    /**
     * Called when database structure is updated.
     * Drops tables so that it can be recreated.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion!=newVersion){
            Log.i("Database", "Database upgrading...");
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_BLEND);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_CHECKPOINTS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_CHECKPOINT);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_PROFILE);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_BEAN);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_RECORD);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROASTER);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLAVOURS);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLAVOUR);
            db.execSQL("DROP TABLE IF EXISTS "+TABLE_ROAST_BLEND);


            onCreate(db);
            Log.i("Database", "Database upgrade complete.");
        }
    }

    /**
     * Add a Bean object to the database.
     * @param bean bean object to be added to the database.
     * @return id of bean insertion. -1 if not successful.
     */
    public int addBean(Bean bean){
        Log.i("Database", "Adding bean entry to database...");
        //Create or open database for writing
        SQLiteDatabase db = getWritableDatabase();
        int beanId=-1;

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
            values.put(KEY_BEAN_DRYING_METHOD, bean.dryingMethod);
            values.put(KEY_BEAN_PRICE_PER_POUND, bean.pricePerPound);

            beanId = (int)db.insertOrThrow(TABLE_BEAN, null, values);
            db.setTransactionSuccessful();
            Log.i("Database", "Bean entry successfully added.");
        }catch (Exception e){
            Log.d("Database", "Error adding bean entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }

        return beanId;
    }

    /**
     * Add a roaster to database
     * @param roaster roaster to add.
     * @return id of inserted roaster. -1 if not successful.
     */
    public int addRoaster(Roaster roaster){
        Log.i("Database", "Adding roaster entry to database...");
        //Create or open database for writing
        SQLiteDatabase db = getWritableDatabase();
        int roasterId=-1;

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ROASTER_NAME, roaster.name);
            values.put(KEY_ROASTER_DESCRIPTION, roaster.description);
            values.put(KEY_ROASTER_BRAND, roaster.brand);
            values.put(KEY_ROASTER_CAPACITY_POUNDS, roaster.capacityPounds);
            values.put(KEY_ROASTER_DRUM_SPEED, roaster.drumSpeed);
            values.put(KEY_ROASTER_HEATING_TYPE, roaster.heatingType);

            roasterId = (int)db.insertOrThrow(TABLE_ROASTER, null, values);
            db.setTransactionSuccessful();
            Log.i("Database", "Roaster entry successfully added.");
        }catch (Exception e){
            Log.d("Database", "Error adding bean entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }

        return roasterId;
    }

    /**
     * Updates the bean weights of a roast in the database.
     * @param record RoastRecord object containing the updated weights
     */
    public void updateRoastRecordWeights(RoastRecord record){
        Log.i("Database", "Updating roast_record weights in database...");

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        ContentValues args = new ContentValues();
        args.put(KEY_ROAST_RECORD_START_WEIGHT, ""+record.startWeightPounds);
        args.put(KEY_ROAST_RECORD_END_WEIGHT, ""+record.endWeightPounds);

        db.update(TABLE_ROAST_RECORD, args, KEY_ROAST_RECORD_ID+"="+record.id, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Update roaster table with passed roaster object.
     * @param roaster object representing roaster table entry
     */
    public void updateRoaster(Roaster roaster){
        Log.i("Database", "Updating roaster entry in database...");
        //Create or open database for writing
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ROASTER_NAME, roaster.name);
            values.put(KEY_ROASTER_DESCRIPTION, roaster.description);
            values.put(KEY_ROASTER_BRAND, roaster.brand);
            values.put(KEY_ROASTER_CAPACITY_POUNDS, roaster.capacityPounds);
            values.put(KEY_ROASTER_DRUM_SPEED, roaster.drumSpeed);
            values.put(KEY_ROASTER_HEATING_TYPE, roaster.heatingType);

            db.update(TABLE_ROASTER, values, KEY_ROASTER_ID+"="+roaster.id, null );
            db.setTransactionSuccessful();
            Log.i("Database", "Roaster entry successfully updated.");
        }catch (Exception e){
            Log.d("Database", "Error updating roaster entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
            db.close();
        }
    }

    /**
     * Add roast record to database.
     * @param record record to be added
     * @return id of added record. -1 if not successful.
     */
    public int addRoastRecord(RoastRecord record){
        Log.i("Database", "Adding roast_record entry to database...");

        long recordId = -1;

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK, record.roastProfile.id);
            values.put(KEY_ROAST_RECORD_FILENAME, record.filename);
            values.put(KEY_ROAST_RECORD_START_WEIGHT, record.startWeightPounds);
            values.put(KEY_ROAST_RECORD_END_WEIGHT, record.endWeightPounds);
            values.put(KEY_ROAST_RECORD_DATETIME, record.dateTime);
            values.put(KEY_ROAST_RECORD_FILESIZE, record.filesizeBytes);

            recordId = db.insertOrThrow(TABLE_ROAST_RECORD, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("Database", "Error adding roast record to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }

        return (int)recordId;
    }

    /**
     * Add roast checkpoint to database.
     * @param checkpoint to be added
     * @return id of added checkpoint. -1 if not successful.
     */
    public int addCheckpoint(Checkpoint checkpoint){
        Log.i("Database", "Adding checkpoint entry to database...");
        //Create or open database for writing

        int id = getCheckpoint(checkpoint);
        if(id!=-1) {
            return id;
        }

        long checkpointId=-1;
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ROAST_CHECKPOINT_NAME, checkpoint.name);
            values.put(KEY_ROAST_CHECKPOINT_TRIGGER, checkpoint.trigger.toString());
            int checkTime = checkpoint.minutes*60+checkpoint.seconds;
            values.put(KEY_ROAST_CHECKPOINT_TIME, checkTime);
            values.put(KEY_ROAST_CHECKPOINT_TEMPERATURE, checkpoint.temperature);

            checkpointId = db.insertOrThrow(TABLE_ROAST_CHECKPOINT, null, values);
            db.setTransactionSuccessful();
        }catch (Exception e){
            Log.d("Database", "Error adding checkpoint entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }

        return (int)checkpointId;
    }

    /**
     * Add coffee roast to database
     * @param roast to be added
     * @return id of roast in db. -1 if not successful.
     */
    public int addRoast(Roast roast){
        Log.i("Database", "Adding roast entry to database...");
        //Create or open database for writing
        SQLiteDatabase db = getWritableDatabase();

        int roastId=-1;

        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_ROAST_PROFILE_NAME, roast.name);
            values.put(KEY_ROAST_PROFILE_ROAST, roast.roastLevel);
            values.put(KEY_ROAST_PROFILE_BEAN_ID_FK, roast.bean.id);
            values.put(KEY_ROAST_PROFILE_CHARGE_TEMP, roast.chargeTemp);
            values.put(KEY_ROAST_PROFILE_DROP_TEMP, roast.dropTemp);
            values.put(KEY_ROAST_PROFILE_FLAVOUR, roast.flavour);

            roastId = (int)db.insertOrThrow(TABLE_ROAST_PROFILE, null, values);
            if(roastId==-1){
                Log.d("Database", "Roast insertion failed.");
                return -1;
            }
            int checkpointCount = roast.checkpoints.size();
            if(checkpointCount>0) {
                long[] checkpointIds = new long[checkpointCount];
                int pos = 1;

                ContentValues cValues = new ContentValues();
                for (Checkpoint checkpoint : roast.checkpoints) {
                    Log.d("Database", "Adding Checkpoints table to database...");
                    cValues.put(KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK, roastId);
                    cValues.put(KEY_CHECKPOINTS_CHECKPOINT_FK, checkpoint.id);
                    long checkTableId = db.insertOrThrow(TABLE_CHECKPOINTS, null, cValues);
                    if (checkTableId == -1) {
                        Log.d("Database", "Checkpoints table insertion failed.");
                        return -1;
                    }
                    Log.d("Database", "Checkpoints table successfully added to database.");
                }
            }
            db.setTransactionSuccessful();
            Log.d("Database","Roast entry successfully added to database.");
        }catch (Exception e){
            Log.d("Database", "Error adding roast entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }

        return roastId;
    }

    /**
     * Add blend to database
     * @param blend to be added
     * @return id of blend in db. -1 if not successful.
     */
    public int addBlend(Blend blend){
        Log.i("Database", "Adding blend entry to database...");
        int blendId = -1;

        int roastCount = blend.roasts.size();
        if(roastCount<=0){
            Log.d("Database", "No roasts in blend.");
            return -1;
        }

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try{
            ContentValues values = new ContentValues();
            values.put(KEY_BLEND_NAME, blend.name);
            values.put(KEY_BLEND_DESCRIPTION, blend.description);

            blendId = (int)db.insertOrThrow(TABLE_BLEND, null, values);
            if(blendId==-1){
                Log.d("Database", "Blend insertion failed.");
                return -1;
            }

            Log.d("Database", "Adding "+TABLE_ROAST_BLEND+" association table to database...");
            ContentValues aValues = new ContentValues();
            for(Roast roast: blend.roasts){
                aValues.put(KEY_ROAST_BLEND_ROASTID_FK, roast.id);
                aValues.put(KEY_ROAST_BLEND_BLENDID_FK, blendId);
                long roastBlendId = db.insertOrThrow(TABLE_ROAST_BLEND, null, aValues);
                if(roastBlendId==-1){
                    Log.d("Database", "Error inserting roast-blend association table to database.");
                    return -1;
                }
                Log.d("Database", roast.name+" roast successfully associated with blend.");
            }
            db.setTransactionSuccessful();
            Log.d("Database", "Blend successfully added to database.");

        }catch (Exception e){
            Log.d("Database", "Error adding blend entry to database. "+e.getMessage());
        }finally {
            db.endTransaction();
        }

        return blendId;
    }

    /**
     * Get all coffee blend information stored in db
     * @return all the blends in db. null if not found
     */
    public ArrayList<Blend> getAllBlends(){
        ArrayList<Blend> blends = new ArrayList<Blend>();

        int blendCount = 0;

        String COUNT_STRING = "amount";
        String BLEND_COUNT_QUERY =
                "SELECT COUNT(*) "+COUNT_STRING+" FROM "+TABLE_BLEND;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BLEND_COUNT_QUERY, null);
        try{
            if(cursor.moveToFirst()) {
                String blendCountString = cursor.getString(cursor.getColumnIndex(COUNT_STRING));
                blendCount = Integer.parseInt(blendCountString);
            }else{
                Log.d("Database", "No blends found in database.");
                return null;
            }
        }catch (Exception e){
            Log.d("Database", "Error getting all blends from database. "+e.getMessage());
            return null;
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }

        if(blendCount<=0){
            Log.d("Database", "No blends found in database.");
            return null;
        }

        for(int i=1; i<=blendCount; i++){
            Blend blend = getBlend(i);
            if(blend!=null)
                blends.add(blend);
        }

        return blends;
    }

    /**
     * get specific coffee blend in database
     * @param id id of blend in db
     * @return object representing the table entry. null if not found
     */
    public Blend getBlend(int id){
        Blend blend = new Blend();

        String BLEND_SELECT_QUERY =
                "SELECT * FROM "+TABLE_BLEND+" WHERE "+KEY_BLEND_ID+"="+id;
        String ROAST_BLEND_SELECT_QUERY =
                "SELECT * FROM "+TABLE_ROAST_BLEND+" WHERE "+KEY_ROAST_BLEND_ROASTID_FK+"="+id;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BLEND_SELECT_QUERY, null);
        Cursor aCursor = db.rawQuery(ROAST_BLEND_SELECT_QUERY, null);

        try{
            if(cursor.moveToFirst()){
                blend.id = id;
                blend.name = cursor.getString(cursor.getColumnIndex(KEY_BLEND_NAME));
                blend.description = cursor.getString(cursor.getColumnIndex(KEY_BLEND_DESCRIPTION));

            }else{
                Log.d("Database", "No blend found with id "+id);
                return null;
            }
            if(aCursor.moveToFirst()){
                do{
                    String roastIdString = aCursor.getString(aCursor.getColumnIndex(KEY_ROAST_BLEND_ROASTID_FK));
                    int roastId = Integer.parseInt(roastIdString);
                    Roast roast = getRoast(roastId);
                    blend.roasts.add(roast);
                }while(aCursor.moveToNext());
            }else{
                Log.d("Database", "No roasts found associated with blend id "+id);
                return null;
            }
        }catch (Exception e){
            Log.d("Database", "Error getting blend from database. "+e.getMessage());
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
            if(aCursor!=null&&aCursor.isClosed())
                aCursor.close();
        }

        return blend;
    }

    /**
     * Get array of all roast records in the db
     * @return all the roast records in db. null if not found
     */
    public ArrayList<RoastRecord> getAllRoastRecords(){
        ArrayList<RoastRecord> records = new ArrayList<>();

        String ROAST_RECORD_IDS_QUERY =
                String.format("SELECT %s FROM %s", KEY_ROAST_RECORD_ID, TABLE_ROAST_RECORD);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ROAST_RECORD_IDS_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                do {
                    int curId = cursor.getInt(cursor.getColumnIndex(KEY_ROAST_RECORD_ID));
                    records.add(getRoastRecord(curId));
                }while(cursor.moveToNext());
            }else{
                Log.e("Database", "No roast record entries found.");
                return null;
            }
        }catch (Exception e){
            Log.d("Database", "Failed to access roast record from database. "+e.getMessage());
        }finally{
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }

        return records;
    }

    /**
     * get individual roast record
     * @param id id of roast record desired
     * @return object representing the roast record. null if not found.
     */
    public RoastRecord getRoastRecord(int id){
        RoastRecord record = new RoastRecord();


        String ROAST_RECORD_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s=%s", TABLE_ROAST_RECORD, KEY_ROAST_RECORD_ID, id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ROAST_RECORD_SELECT_QUERY, null);

        /**
         *                     KEY_ROAST_RECORD_ID+" INTEGER PRIMARY KEY,"+
         *                     KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK+" INTEGER REFERENCES "+TABLE_ROAST_PROFILE+","+
         *                     KEY_ROAST_RECORD_FILENAME+" TEXT,"+
         *                     KEY_ROAST_RECORD_START_WEIGHT+" DECIMAL,"+
         *                     KEY_ROAST_RECORD_END_WEIGHT+" DECIMAL,"+
         *                     KEY_ROAST_RECORD_DATETIME+" TEXT,"+
         *                     KEY_ROAST_RECORD_ROASTER_ID+" INTEGER REFERENCES "+KEY_ROASTER_ID+
         */

        try{
            if(cursor.moveToFirst()){
                int roastId = cursor.getInt(cursor.getColumnIndex(KEY_ROAST_RECORD_ROAST_PROFILE_ID_FK));
                Roast roast = getRoast(roastId);
                if(roast!=null)
                    record.roastProfile = roast;
                //Roaster roaster = new Roaster();//TODO get roaster?

                record.id = id;
                record.filename = cursor.getString(cursor.getColumnIndex(KEY_ROAST_RECORD_FILENAME));
                record.startWeightPounds = (float)cursor.getDouble(cursor.getColumnIndex(KEY_ROAST_RECORD_START_WEIGHT));
                record.endWeightPounds = (float)cursor.getDouble(cursor.getColumnIndex(KEY_ROAST_RECORD_END_WEIGHT));
                record.dateTime = cursor.getString(cursor.getColumnIndex(KEY_ROAST_RECORD_DATETIME));
                record.name = record.roastProfile.name+" "+record.dateTime;
                record.filesizeBytes = cursor.getInt(cursor.getColumnIndex(KEY_ROAST_RECORD_FILESIZE));

            }else{
                Log.d("Database", "No roast record found with id "+id);
                return null;//No entries found matching
            }

        }catch (Exception e){
            Log.d("Database", "Failed to access Bean entry from database. "+e.getMessage());
        }finally{
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }

        return record;
    }

    /**
     * Deletes a roast entry with the passed id.
     * @param id id of entry to be deleted.
     * @throws android.database.sqlite.SQLiteConstraintException
     */
    public void deleteRoast(int id) throws android.database.sqlite.SQLiteConstraintException{
        deleteCheckpoint(id);
        SQLiteDatabase db = getWritableDatabase();

//        if(crCursor.moveToFirst()){
//            do {
//                String checkIdString = crCursor.getString(crCursor.getColumnIndex(KEY_CHECKPOINTS_CHECKPOINT_FK));
//                int checkId = Integer.parseInt(checkIdString);
//                roast.checkpoints.add(getCheckpoint(checkId));
//            }while(crCursor.moveToNext());
//        }else{
//            Log.d("Database", "No checkpoints found.");
//        }

        db.beginTransaction();
        String WHERE_CLAUSE = KEY_ROAST_PROFILE_ID+"="+id;
        int rows = db.delete(TABLE_ROAST_PROFILE, WHERE_CLAUSE, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Deletes a roast blend junction table with the passed id.
     * @param id id of entry to be deleted.
     */
    public void deleteRoastBlend(int id){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        String WHERE_CLAUSE = KEY_ROAST_BLEND_BLENDID_FK+"="+id;
        int rows = db.delete(TABLE_ROAST_BLEND, WHERE_CLAUSE, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Deletes a blend table with the passed id.
     * @param id id of entry to be deleted.
     */
    public void deleteBlend(int id){
        deleteRoastBlend(id);
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        String WHERE_CLAUSE = KEY_BLEND_ID+"="+id;
        int rows = db.delete(TABLE_BLEND, WHERE_CLAUSE, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Deletes a bean entry at the passed id.
     * @param id id of entry to be deleted.
     * @throws android.database.sqlite.SQLiteConstraintException
     */
    public void deleteBean(int id) throws android.database.sqlite.SQLiteConstraintException{
        //deleteCheckpoints(id);

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        String WHERE_CLAUSE = KEY_BEAN_ID+"="+id;
        int rows = db.delete(TABLE_BEAN, WHERE_CLAUSE, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Deletes a checkpoint entry at the passed id.
     * @param id id of entry to be deleted.
     */
    public void deleteCheckpoint(int id){
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        String WHERE_CLAUSE = KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK+"="+id;
        int rows = db.delete(TABLE_CHECKPOINTS, WHERE_CLAUSE, null);
        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();
    }

    /**
     * Get a roast entry at the passed id
     * @param id id of roast entry
     * @return Roast object representing the table entry. null if not found.
     */
    public Roast getRoast(int id){
        Roast roast = new Roast();
        int checkpointCount;
        int[] checkpointIds;

        final String KEY_CHECK_COUNT = "count";

        String ROAST_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s=%s", TABLE_ROAST_PROFILE, KEY_ROAST_PROFILE_ID, id);
        String ROAST_CHECKPOINT_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s=%s", TABLE_CHECKPOINTS, KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK, id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ROAST_SELECT_QUERY, null);
        Cursor crCursor = db.rawQuery(ROAST_CHECKPOINT_SELECT_QUERY, null);

        try{
            if(cursor.moveToFirst()){
                roast.id = id;
                roast.name = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_NAME));
                roast.roastLevel = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_ROAST));
                String beanIdString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_BEAN_ID_FK));
                roast.bean = getBean(Integer.parseInt(beanIdString));
                String chargeTempString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_CHARGE_TEMP));
                roast.chargeTemp = Integer.parseInt(chargeTempString);
                String dropTempString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_DROP_TEMP));
                roast.dropTemp = Integer.parseInt(dropTempString);
                roast.flavour = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_FLAVOUR));
            }else{
                Log.d("Database", "No roast found with id "+id);
                return null;//No entries found matching
            }
            if(crCursor.moveToFirst()){
                do {
                    String checkIdString = crCursor.getString(crCursor.getColumnIndex(KEY_CHECKPOINTS_CHECKPOINT_FK));
                    int checkId = Integer.parseInt(checkIdString);
                    roast.checkpoints.add(getCheckpoint(checkId));
                }while(crCursor.moveToNext());
            }else{
                Log.d("Database", "No checkpoints found.");
            }

        }catch (Exception e){
            Log.d("Database", "Failed to access Bean entry from database. "+e.getMessage());
        }finally{
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
            if(crCursor!=null&&crCursor.isClosed())
                crCursor.close();
        }

        return roast;
    }

    /**
     * Checks if a checkpoint already exists in database and returns the id where it is found.
     * @param checkpoint checkpoint to be searched for
     * @return id of found checkpoint. -1 if not found.
     */
    public int getCheckpoint(Checkpoint checkpoint){
        String CHECKPOINT_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE (%s='%s' AND %s='%s' AND %s=%s AND %s=%s)", TABLE_ROAST_CHECKPOINT, KEY_ROAST_CHECKPOINT_NAME, checkpoint.name, KEY_ROAST_CHECKPOINT_TRIGGER, checkpoint.trigger.toString(), KEY_ROAST_CHECKPOINT_TEMPERATURE, ""+checkpoint.temperature, KEY_ROAST_CHECKPOINT_TIME, ""+checkpoint.timeTotalInSeconds());

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CHECKPOINT_SELECT_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_ID));
                cursor.close();
                return id;
            }else{
                Log.d("Database", "No entries returned.");
                cursor.close();
                return -1;
            }
        }catch (Exception e){
            Log.d("Database", "Error getting Checkpoint from database.");
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }
        return -1;
    }

    /**
     * Get checkpoint entry at passed id.
     * @param id id of desired entry.
     * @return Checkpoint object representing entry
     */
    public Checkpoint getCheckpoint(int id){
        Checkpoint checkpoint = new Checkpoint();

        String CHECKPOINT_SELECT_QUERY =
                String.format("SELECT * FROM %s WHERE %s=%s", TABLE_ROAST_CHECKPOINT, KEY_ROAST_CHECKPOINT_ID, id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CHECKPOINT_SELECT_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                checkpoint.name = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_NAME));
                String checkIDString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_ID));
                checkpoint.id = Integer.parseInt(checkIDString);
                String checkTempString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_TEMPERATURE));
                checkpoint.temperature = Integer.parseInt(checkTempString);
                String checkTimeString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_TIME));
                int totalTime = Integer.parseInt(checkTimeString);
                int checkMins = totalTime/60;
                int checkSecs = totalTime-checkMins*60;
                checkpoint.minutes = checkMins;
                checkpoint.seconds = checkSecs;
                checkpoint.trigger = Checkpoint.trig.valueOf(cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_TRIGGER)));
            }else{
                Log.d("Database", "No entries returned.");
                return null;
            }
        }catch (Exception e){
            Log.d("Database", "Error getting Checkpoint from database.");
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }

        return checkpoint;
    }

    /**
     * Get ArrayList of Roasts with only information relevant to UI buttons.
     * Contains id, name, roastlevel and droptemp filled in for UI buttons. ID will be used to get full roast information when button is pressed.
     * @return ArrayList of partially filled in Roasts for UI buttons.
     */
    public ArrayList<Roast> getAllRoastsForButtons(){
        Log.d("Database", "Getting all roast entries in database...");
        ArrayList<Roast> roasts = new ArrayList<Roast>();
        List<RoastCheckpointAssociation> associations = new ArrayList<RoastCheckpointAssociation>();

        String KEY_ROAST_COUNT = "roast_entries";

        String ROAST_WITHOUT_BEAN_AND_CHECK_QUERY =
                String.format("SELECT * FROM %s", TABLE_ROAST_PROFILE);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ROAST_WITHOUT_BEAN_AND_CHECK_QUERY, null);

        if(cursor.moveToFirst()){
            do{
                Roast roast = new Roast();

                String roastIdString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_ID));
                int roastId = Integer.parseInt(roastIdString);

                roast.id = roastId;
                roast.name = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_NAME));
                roast.roastLevel = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_ROAST));
                roast.dropTemp = cursor.getInt(cursor.getColumnIndex(KEY_ROAST_PROFILE_DROP_TEMP));

                roasts.add(roast);
            }while(cursor.moveToNext());
        }

        return roasts;
    }

    /**
     * Get ArrayList with all roast entries.
     * @return ArrayList of Roast objects representing roast table data.
     */
    public ArrayList<Roast> getAllRoasts(){
        Log.d("Database", "Getting all roast entries in database...");
        ArrayList<Roast> roasts = new ArrayList<Roast>();
        List<RoastCheckpointAssociation> associations = new ArrayList<RoastCheckpointAssociation>();
        Roast[] roastArray = new Roast[0];

        String KEY_ROAST_COUNT = "roast_entries";

        String ROAST_COUNT_QUERY =
                String.format("SELECT COUNT(*) AS %s FROM %s", KEY_ROAST_COUNT, TABLE_ROAST_PROFILE);//Counts how many roast profiles are contained in the db;
        String ROAST_AND_CHECKPOINTS_SELECT_QUERY2 =
                String.format("SELECT r.*, rc.* FROM %s r LEFT JOIN %s c ON r.%s=c.%s LEFT JOIN %s rc ON c.%s=rc.%s", TABLE_ROAST_PROFILE, TABLE_CHECKPOINTS, KEY_ROAST_PROFILE_ID, KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK, TABLE_ROAST_CHECKPOINT, KEY_CHECKPOINTS_CHECKPOINT_FK, KEY_ROAST_CHECKPOINT_ID);

        String ROAST_AND_CHECKPOINTS_SELECT_QUERY =
                "SELECT r.*, rc.* FROM "+TABLE_ROAST_PROFILE+" r LEFT JOIN "+TABLE_CHECKPOINTS+" c ON r."+KEY_ROAST_PROFILE_ID+"=c."+KEY_CHECKPOINTS_ROAST_PROFILE_ID_FK+" LEFT JOIN "+TABLE_ROAST_CHECKPOINT+" rc ON c."+KEY_CHECKPOINTS_CHECKPOINT_FK+"=rc."+KEY_ROAST_CHECKPOINT_ID;

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ROAST_COUNT_QUERY, null);
        try {
            if(cursor.moveToFirst()) {
                Log.d("Database", "Getting count of roast entries in database...");
                String roastCountString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_COUNT));
                int roastCount = Integer.parseInt(roastCountString);
                roastArray = new Roast[roastCount];//Create an array of size equal to the amount of roasts in the database.
                for(int i=0; i<roastCount; i++)
                    roastArray[i]=new Roast();
            }else{
                return null;//nothing was returned from database.
            }

            //TODO: fix CHECKPOINTS_SELECT_QUERY to join with roast data as one query.

            cursor = db.rawQuery(ROAST_AND_CHECKPOINTS_SELECT_QUERY, null);
            if(cursor.moveToFirst()){
                do{
                    Log.d("Database", "Getting Roast entry from database by Checkpoints table key...");
                    String roastIdString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_ID));
                    int roastId = Integer.parseInt(roastIdString);
                    if(roastArray[roastId-1].id==0){//if entry unfilled, fill it
                        roastArray[roastId-1].id = roastId;
                        roastArray[roastId-1].name = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_NAME));
                        roastArray[roastId-1].roastLevel = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_ROAST));
                        String beanIdString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_BEAN_ID_FK));
                        roastArray[roastId-1].bean = getBean(Integer.parseInt(beanIdString));
                        String chargeTempString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_CHARGE_TEMP));
                        roastArray[roastId-1].chargeTemp = Integer.parseInt(chargeTempString);
                        String dropTempString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_DROP_TEMP));
                        roastArray[roastId-1].dropTemp = Integer.parseInt(dropTempString);
                        roastArray[roastId-1].flavour = cursor.getString(cursor.getColumnIndex(KEY_ROAST_PROFILE_FLAVOUR));
                    }
                    Log.d("Database", "Getting checkpoint for Roast id:"+roastIdString+"...");
                    Checkpoint checkpoint = new Checkpoint();
                    String checkpointIdString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_ID));
                    if(checkpointIdString==null)
                        continue;
                    checkpoint.id = Integer.parseInt(checkpointIdString);
                    checkpoint.name = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_NAME));
                    checkpoint.trigger = Checkpoint.trig.valueOf(cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_TRIGGER)));
                    String checkpointTimeString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_TIME));
                    int checkpointTime = Integer.parseInt(checkpointTimeString);
                    checkpoint.minutes = checkpointTime/60;
                    checkpoint.seconds = checkpointTime-checkpoint.minutes*60;
                    String checkpointTemperatureString = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_TEMPERATURE));
                    checkpoint.temperature = Integer.parseInt(checkpointTemperatureString);

                    roastArray[roastId-1].checkpoints.add(checkpoint);


                }while(cursor.moveToNext());
            }
        }catch (Exception e){
            Log.d("Database","Error getting roast entries. "+e.getMessage());
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }

        Collections.addAll(roasts, roastArray);
        for(int i=0; i<roasts.size(); i++){
            if(roasts.get(i).id==0) {
                roasts.remove(i);
                i-=1;
            }
        }

        return roasts;
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
                    bean.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_BEAN_ID)));
                    bean.name = cursor.getString(cursor.getColumnIndex(KEY_BEAN_NAME));

                    beans.add(bean);
                }while(cursor.moveToNext());
            }
        }catch (Exception e) {
            Log.d("Database", "Error getting bean list from database. " + e.getMessage());
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }
        return beans;
    }

    /**
     * Get ArrayList of all checkpoint entries in db
     * @return all checkpoint entries
     */
    public List<Checkpoint> getAllCheckpoints(){
        List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

        String CHECKPOINT_SELECT_QUERY =
                String.format("SELECT * FROM %s", TABLE_ROAST_CHECKPOINT);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CHECKPOINT_SELECT_QUERY, null);
        try{
            if(cursor.moveToFirst()){
                do {
                    Checkpoint checkpoint = new Checkpoint();
                    checkpoint.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_ID)));
                    checkpoint.name = cursor.getString(cursor.getColumnIndex(KEY_ROAST_CHECKPOINT_NAME));

                    checkpoints.add(checkpoint);
                }while(cursor.moveToNext());
            }
        }catch (Exception e) {
            Log.d("Database", "Error getting checkpoint list from database. " + e.getMessage());
        }finally {
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
        }
        return checkpoints;
    }

    /**
     * Get specific bean entry at id
     * @param id id of entry
     * @return Bean object representing db entry
     */
    public Bean getBean(int id){
        Bean bean = new Bean();

        String BEAN_SELECT_QUERY=
                String.format("SELECT * FROM %s WHERE %s=%d", TABLE_BEAN, KEY_BEAN_ID, id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(BEAN_SELECT_QUERY, null);
        try{
             if(cursor.moveToFirst()){
                 bean.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_BEAN_ID)));
                 bean.name = cursor.getString(cursor.getColumnIndex(KEY_BEAN_NAME));
                 bean.origin = cursor.getString(cursor.getColumnIndex(KEY_BEAN_ORIGIN));
                 bean.farm = cursor.getString(cursor.getColumnIndex(KEY_BEAN_FARM));
                 bean.dryingMethod = cursor.getString(cursor.getColumnIndex(KEY_BEAN_DRYING_METHOD));
                 bean.process = cursor.getString(cursor.getColumnIndex(KEY_BEAN_PROCESS_STYLE));
                 bean.flavours = cursor.getString(cursor.getColumnIndex(KEY_BEAN_FLAVOUR));
                 bean.altitude = cursor.getString(cursor.getColumnIndex(KEY_BEAN_ALTITUDE));
                 bean.body = cursor.getString(cursor.getColumnIndex(KEY_BEAN_BODY));
                 bean.acidity = cursor.getString(cursor.getColumnIndex(KEY_BEAN_ACIDITY));
                 bean.pricePerPound = Float.parseFloat(cursor.getString(cursor.getColumnIndex(KEY_BEAN_PRICE_PER_POUND)));
             }
        }catch (Exception e){
            Log.d("Database", "Error retrieving bean from database. "+e.getMessage());
        }finally {
            cursor.close();
        }

        return bean;
    }

    /**
     * Get roaster entry at id
     * @param id id of entry
     * @return Roaster object representing db entry
     */
    public Roaster getRoaster(int id){
        Roaster roaster = new Roaster();

        String ROASTER_SELECT_QUERY=
                String.format("SELECT * FROM %s WHERE %s=%d", TABLE_ROASTER, KEY_ROASTER_ID, id);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(ROASTER_SELECT_QUERY , null);
        try{
            if(cursor.moveToFirst()){
                roaster.id = id;
                roaster.name = cursor.getString(cursor.getColumnIndex(KEY_ROASTER_NAME));
                roaster.brand = cursor.getString(cursor.getColumnIndex(KEY_ROASTER_BRAND));
                roaster.capacityPounds = cursor.getFloat(cursor.getColumnIndex(KEY_ROASTER_CAPACITY_POUNDS));
                roaster.heatingType= cursor.getString(cursor.getColumnIndex(KEY_ROASTER_HEATING_TYPE));
                roaster.drumSpeed = cursor.getFloat(cursor.getColumnIndex(KEY_ROASTER_DRUM_SPEED));
                roaster.description = cursor.getString(cursor.getColumnIndex(KEY_ROASTER_DESCRIPTION));
            }
        }catch (Exception e){
            Log.d("Database", "Error retrieving roaster from database. "+e.getMessage());
        }finally {
            cursor.close();
        }

        return roaster;
    }

}
