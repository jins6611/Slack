package jl.slacktest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper {

    public static final String KEY_SENDER = "sender";
    public static final String KEY_MESSAGE_TYPE = "messagetype";
    public static final String KEY_ROWID = "_id";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_CHANNEL_NAME = "channelname";



    private static final String TAG = "dbdata";
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
            "create table messagetable (_id integer primary key autoincrement, "
                    + "sender varchar, channelname varchar, message varchar,messagetype varchar);";


    private static final String DATABASE_NAME = "data.db";
    private static final String DATABASE_TABLE = "messagetable";

    private static final int DATABASE_VERSION = 2;

    private final Context mCtx;


    private static class DatabaseHelper extends SQLiteOpenHelper {


        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS devicedetails");
            onCreate(db);
        }
    }


    public DBHelper(Context ctx) {
        this.mCtx = ctx;
    }


    public DBHelper open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDbHelper.close();
    }


    public long insertMsgDetail(String sender, String channel, String message, String messagetype) {

        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SENDER, sender);
        initialValues.put(KEY_CHANNEL_NAME, channel);
        initialValues.put(KEY_MESSAGE, message);
        initialValues.put(KEY_MESSAGE_TYPE, messagetype);
        return mDb.insert(DATABASE_TABLE, null, initialValues);

    }

    public Cursor fetchAllMessage() {

        return mDb.query(DATABASE_TABLE, new String[]{KEY_ROWID, KEY_SENDER,
                KEY_CHANNEL_NAME, KEY_MESSAGE, KEY_MESSAGE_TYPE}, null, null, null, null, null,null);
    }

}
