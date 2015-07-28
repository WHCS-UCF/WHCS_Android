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
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "controlModulesManager";

    // Control Modules table name
    private static final String TABLE_CONTROL_MODULES = "control_modules";
    // Control Module Groupings table name
    private static final String TABLE_GROUPINGS = "control_module_groupings";

    // Control Modules Table Columns names
    private static final String KEY_IDENTITY_NUMBER = "identity_number";
    private static final String KEY_NAME = "name";
    private static final String KEY_ROLE = "role";

    // Control Module Groupings Table Column names
    private static final String FOREIGN_KEY_IDENTITY_NUMBER = "identity_number";
    private static final String KEY_GROUP_NUMBER = "group_number";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTROL_MODULES + "("
                + KEY_IDENTITY_NUMBER + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_ROLE + " TEXT )";
        db.execSQL(CREATE_CONTACTS_TABLE);

        String CREATE_GROUPINGS_TABLE = "CREATE TABLE " + TABLE_GROUPINGS + "("
                + FOREIGN_KEY_IDENTITY_NUMBER + " INTEGER NOT NULL, " + KEY_GROUP_NUMBER + " INTEGER NOT NULL, "
                + " PRIMARY KEY (" + FOREIGN_KEY_IDENTITY_NUMBER +", " + KEY_GROUP_NUMBER + "), "
                + " FOREIGN KEY ( " + FOREIGN_KEY_IDENTITY_NUMBER +" ) REFERENCES "+ TABLE_CONTROL_MODULES + "("
                + KEY_IDENTITY_NUMBER +") )";
        db.execSQL(CREATE_GROUPINGS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTROL_MODULES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPINGS);

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
                new String[]{Byte.toString(controlModule.getIdentityNumber())});
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

    public boolean controlModuleExists(ControlModule cm) {
        ControlModule checkIfExistsCM;
        checkIfExistsCM = this.getControlModule(cm.getIdentityNumber());
        if(checkIfExistsCM == null) {
            return false;
        }
        else {
            return true;
        }
    }

    public void addControlModuleGrouping(int controlModuleId, int groupNumber) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FOREIGN_KEY_IDENTITY_NUMBER, controlModuleId); // Unique ID for the Control Module
        values.put(KEY_GROUP_NUMBER, groupNumber); // Group number

        // Inserting Row
        db.insert(TABLE_GROUPINGS, null, values);
        db.close(); // Closing database connection
    }

    public void addControlModuleGrouping(ControlModule cm, int groupNumber) {
        addControlModuleGrouping(cm.getIdentityNumber(), groupNumber);
    }

    public ArrayList<ControlModuleGrouping> getControlModuleGroup(int groupNumber) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GROUPINGS, new String[] { FOREIGN_KEY_IDENTITY_NUMBER,
                        KEY_GROUP_NUMBER}, KEY_GROUP_NUMBER + "=?",
                new String[] { String.valueOf(groupNumber) }, null, null, null, null);
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }
        ArrayList<ControlModuleGrouping> cmGroup = new ArrayList<ControlModuleGrouping>();
        while (cursor.moveToNext()) {
            cmGroup.add(new ControlModuleGrouping(Byte.parseByte(cursor.getString(0)), Integer.parseInt(cursor.getString(1))));
        }

        //Return Control Module Group
        return cmGroup;
    }

    public List<ControlModuleGrouping> getAllControlModuleGroupings() {
        List<ControlModuleGrouping> controlModuleGroupingList = new ArrayList<ControlModuleGrouping>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_GROUPINGS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                // Adding Control Module Grouping to list
                controlModuleGroupingList.add(new ControlModuleGrouping(Byte.parseByte(cursor.getString(0)), Integer.parseInt(cursor.getString(1))));
            } while (cursor.moveToNext());
        }

        // return Control Module Grouping list
        return controlModuleGroupingList;
    }

    public List<ControlModuleGrouping> getSpecificControlModulesGroupings(ControlModule cm) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_GROUPINGS, new String[] { FOREIGN_KEY_IDENTITY_NUMBER,
                        KEY_GROUP_NUMBER}, FOREIGN_KEY_IDENTITY_NUMBER+ "=?",
                new String[] { String.valueOf(cm.getIdentityNumber()) }, null, null, null, null);
        if(cursor == null || cursor.getCount() == 0) {
            return null;
        }
        ArrayList<ControlModuleGrouping> cmGroupings = new ArrayList<ControlModuleGrouping>();
        while (cursor.moveToNext()) {
            cmGroupings.add(new ControlModuleGrouping(Byte.parseByte(cursor.getString(0)), Integer.parseInt(cursor.getString(1))));
        }

        //Return Control Module Group
        return cmGroupings;
    }

    public void deleteControlModuleGrouping(int controlModuleId, int groupNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPINGS, FOREIGN_KEY_IDENTITY_NUMBER+ " = ?, " + KEY_GROUP_NUMBER + " =?",
                new String[]{Integer.toString(controlModuleId), Integer.toString(groupNumber)});
        db.close();
    }

    public void deleteControlModuleGrouping(ControlModuleGrouping grouping) {
        deleteControlModuleGrouping(grouping.getControlModuleId(), grouping.getGroupNumber());
    }

    public void deleteControlModuleGroup(int groupNumber) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPINGS, FOREIGN_KEY_IDENTITY_NUMBER+ " = ?",
                new String[]{Integer.toString(groupNumber)});
        db.close();
    }

    public void deleteSpecificControlModulesGroupings(int controlModuleId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPINGS, FOREIGN_KEY_IDENTITY_NUMBER+ " = ?",
                new String[]{Integer.toString(controlModuleId)});
        db.close();
    }

    public void deleteSpecificControlModulesGroupings(ControlModule cm) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GROUPINGS, FOREIGN_KEY_IDENTITY_NUMBER+ " = ?",
                new String[]{Integer.toString(cm.getIdentityNumber())});
        db.close();
    }

    public int getControlModuleGroupingsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_GROUPINGS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        // return count
        return cursor.getCount();
    }

    public void updateControlModulesGroupings(ControlModule cm, List<Integer> groupNumbers) {
        deleteSpecificControlModulesGroupings(cm);
        for( int groupNumber : groupNumbers ) {
            addControlModuleGrouping(cm, groupNumber);
        }
    }

}