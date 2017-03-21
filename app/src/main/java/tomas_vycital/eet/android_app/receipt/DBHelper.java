package tomas_vycital.eet.android_app.receipt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tom on 20.3.17.
 */

class DBHelper extends SQLiteOpenHelper {
    DBHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE receipts_history " +
                        "(" +
                        "id INTEGER PRIMARY KEY," +
                        "year INTEGER," +
                        "month INTEGER," +
                        "day INTEGER," +
                        "json TEXT" +
                        ")" +
                        ";"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    void addDay(int year, int month, int day, String json) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("year", year);
        values.put("month", month);
        values.put("day", day);
        values.put("json", json);

        db.insert("receipts_history", null, values);
        db.close();
    }

    List<String> getDay(int year, int month, int day) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                "receipts_history",
                new String[]{"json"},
                "year = " + year + " AND month = " + month + " AND day = " + day,
                null,
                null,
                null,
                "id ASC",
                null
        );
        List<String> jsons = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                jsons.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return jsons;
    }
}
