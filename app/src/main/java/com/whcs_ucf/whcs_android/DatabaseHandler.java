package com.whcs_ucf.whcs_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jimmy on 7/7/2015.
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "controlModulesManager";

    // Control Modules table name
    private static final String TABLE_CONTROL_MODULES = "control_modules";

    // Control Modules Table Columns names
    private static final String KEY_IDENTITY_NUMBER = "identity_number";
    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTROL_MODULES + "("
                + KEY_IDENTITY_NUMBER + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_ROLE + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTROL_MODULES);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new Control Module
    void addControlModule(ControlModule controlModule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_IDENTITY_NUMBER, controlModule.getIdentityNumber());
        values.put(KEY_NAME, controlModule.getName()); // Control Module Name
        values.put(KEY_ROLE, controlModule.getRole().name()); // Control  Module Role

        // Inserting Row
        db.insert(TABLE_CONTROL_MODULES, null, values);
        db.close(); // Closing database connection
    }

    // Getting single Control Module
    ControlModule getControlModule(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_CONTROL_MODULES, new String[] { KEY_IDENTITY_NUMBER,
                        KEY_NAME, KEY_ROLE}, KEY_IDENTITY_NUMBER + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if(cursor == null || cursor.getCount() == 0)
            return null;
        if (cursor != null)
            cursor.moveToFirst();

        //Return Control Module
        return constructControlModuleFromQuery(new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2)});
    }

    // Getting All Contacts
    public List<ControlModule> getAllControlModules() {
        List<ControlModule> controlModuleList = new ArrayList<ControlModule>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTROL_MODULES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding Control Module to list
                controlModuleList.add(constructControlModuleFromQuery(new String[] {cursor.getString(0), cursor.getString(1), cursor.getString(2)}));
            } while (cursor.moveToNext());
        }

        // return Control Module list
        return controlModuleList;
    }

    // Updating single Control Module
    public int updateControlModule(ControlModule controlModule) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, controlModule.getName());
        values.put(KEY_ROLE, controlModule.getRole().name());

        // updating row
        return db.update(TABLE_CONTROL_MODULES, values, KEY_IDENTITY_NUMBER + " = ?",
                new String[] { Byte.toString(controlModule.getIdentityNumber()) });
    }

    // Deleting single ControlModule
    public void deleteControlModule(ControlModule controlModule) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTROL_MODULES, KEY_IDENTITY_NUMBER + " = ?",
                new String[] { Byte.toString(controlModule.getIdentityNumber()) });
        db.close();
    }


    // Getting ControlModules Count
    public int getControlModulesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_CONTROL_MODULES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    private ControlModule constructControlModuleFromQuery(String[] results) {
        ControlModule controlModule = null;
        if(results[2] == null) {
            controlModule = new ControlModule(ControlModuleRole.GetRandomControlModuleRole());
        } else {
            ControlModuleRole role = ControlModuleRole.valueOf(results[2]);
            if (role == ControlModuleRole.SENSOR_COLLECTOR) {
                controlModule = new DataCollectionControlModule(ControlModuleRole.SENSOR_COLLECTOR);
            } else{
                controlModule = new ToggleableControlModule(role);
            }
        }

        controlModule.setIdentityNumber(Byte.parseByte(results[0]));
        controlModule.setName(results[1]);

        //Return Control Module
        return controlModule;
    }

}