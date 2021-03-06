/**
 * Copyright (c) 2016 Thorsten Schenkel (t7soft.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author Thorsten Schenkel (t7soft.de)
 * 
 */
package de.t7soft.android.t7toolate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*protected*/public class ToLateDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tolate.db";
	private static final int DATABASE_VERSION = 3;

	// connection cols
	public static final String CONNECTIONS_TABLE_NAME = "connections";
	public static final String CONNECTION_ID_COL_NAME = "connectionId";
	public static final String CONNECTION_NAME_COL_NAME = "connectionName";
	public static final String CONNECTION_START_STATION_COL_NAME = "startStation";
	public static final String CONNECTION_START_TIME_COL_NAME = "startTime";
	public static final String CONNECTION_END_STATION_COL_NAME = "endStation";
	public static final String CONNECTION_END_TIME_COL_NAME = "endTime";
	public static final String CONNECTION_WEEKDAYS_COL_NAME = "weekdays";
	public static final String CONNECTION_SATURDAY_COL_NAME = "saturday";
	public static final String CONNECTION_SUNDAY_COL_NAME = "sunday";
	public static final String CONNECTION_TYPE_COL_NAME = "type";

	// capture cols
	public static final String CAPTURES_TABLE_NAME = "captures";
	public static final String CAPTURE_ID_COL_NAME = "captureId";
	public static final String CAPTURE_CONNECTION_NAME_COL_NAME = "connectionName";
	public static final String CAPTURE_CONNECTION_START_STATION_COL_NAME = "startStation";
	public static final String CAPTURE_CONNECTION_START_TIME_COL_NAME = "startTime";
	public static final String CAPTURE_CONNECTION_END_STATION_COL_NAME = "endStation";
	public static final String CAPTURE_CONNECTION_END_TIME_COL_NAME = "endTime";
	public static final String CAPTURE_CONNECTION_TYPE_COL_NAME = "type";
	public static final String CAPTURE_DATE_TIME_COL_NAME = "dateTime";
	public static final String CAPTURE_COMMENT_COL_NAME = "comment";
	public static final String CAPTURE_DELAY_COL_NAME = "delay";

	public ToLateDatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		createConnectionsTable(db);
		createCaptursTable(db);
	}

	private void createConnectionsTable(final SQLiteDatabase db) {
		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(CONNECTIONS_TABLE_NAME);
		sqlBuffer.append("(");
		sqlBuffer.append("_id INTEGER PRIMARY KEY AUTOINCREMENT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_NAME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_START_STATION_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_START_TIME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_END_STATION_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_END_TIME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_WEEKDAYS_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_SATURDAY_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_SUNDAY_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(", ");
		sqlBuffer.append(CONNECTION_TYPE_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(");");
		db.execSQL(sqlBuffer.toString());
	}

	private void createCaptursTable(final SQLiteDatabase db) {
		final StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("CREATE TABLE IF NOT EXISTS ");
		sqlBuffer.append(CAPTURES_TABLE_NAME);
		sqlBuffer.append("(");
		sqlBuffer.append("_id INTEGER PRIMARY KEY AUTOINCREMENT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_ID_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_CONNECTION_NAME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_CONNECTION_START_STATION_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_CONNECTION_START_TIME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_CONNECTION_END_STATION_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_CONNECTION_END_TIME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_CONNECTION_TYPE_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_DATE_TIME_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_COMMENT_COL_NAME);
		sqlBuffer.append(" TEXT");
		sqlBuffer.append(", ");
		sqlBuffer.append(CAPTURE_DELAY_COL_NAME);
		sqlBuffer.append(" INTEGER");
		sqlBuffer.append(");");
		db.execSQL(sqlBuffer.toString());
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		if (oldVersion < DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + CONNECTIONS_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + CAPTURES_TABLE_NAME);
			onCreate(db);
		}

	}
}
