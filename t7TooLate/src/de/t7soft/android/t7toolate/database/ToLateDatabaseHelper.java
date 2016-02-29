package de.t7soft.android.t7toolate.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ToLateDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tolate.db";
	private static final int DATABASE_VERSION = 1;

	// connection cols
	public static final String CONNECTIONS_TABLE_NAME = "locations";
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

	public ToLateDatabaseHelper(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		createConnectionsTable(db);
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

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
		if (oldVersion < DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + CONNECTIONS_TABLE_NAME);
		}
		onCreate(db);
	}

}
