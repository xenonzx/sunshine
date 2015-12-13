package com.luxtech_eg.sunshine;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.luxtech_eg.sunshine.data.WeatherContract;
import com.luxtech_eg.sunshine.data.WeatherDbHelper;

/**
 * Created by ahmed on 02/12/15.
 */
public class TestDB extends AndroidTestCase {
    //mContext in context in AndroidTestCase
    //1 get a reference  to db
    static final String TEST_LOCATION = "99705";
    static final long TEST_DATE = 1419033600L;

    public void testLocationTable(){
        WeatherDbHelper dbHelper= new WeatherDbHelper(mContext);
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        ContentValues testValues =TestUtilities.createNorthPoleLocationValues();
        long locationRowId;
        locationRowId = db.insert(WeatherContract.LocationEntry.TABLE_NAME, null, testValues);
         // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Cursor cursor = db.query(
                WeatherContract.LocationEntry.TABLE_NAME,null,null,null,null,null,null);
        assertTrue( "Error: No Records returned from location query", cursor.moveToFirst() );
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Failed", cursor, testValues);
        assertFalse("Error: More than one record returned from location query", cursor.moveToNext());
        cursor.close();
        db.close();
    }


}
