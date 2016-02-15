package de.t7soft.android.t7toolate.database;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.t7soft.android.t7toolate.model.Connection;

public class ToLateDatabaseAdapter {

	private static final String LOGTAG = ToLateDatabaseAdapter.class.getSimpleName();
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private final Context context;
	private ToLateDatabaseHelper dbHelper;
	private SQLiteDatabase database;

	public ToLateDatabaseAdapter(final Context context) {
		this.context = context;
	}

	public void open() throws SQLException {
		dbHelper = new ToLateDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public boolean canWrite() {
		if (database != null) {
			return database.isOpen() && !database.isReadOnly() && !database.isDbLockedByCurrentThread();
		}
		return false;
	}

	public long insertConnection(final Connection connection) {
		return insertConnection(database, connection);
	}

	private long insertConnection(SQLiteDatabase db, Connection connection) {
		final ContentValues initialValues = createContentValues(connection);
		return db.insert(ToLateDatabaseHelper.CONNECTIONS_TABLE_NAME, null, initialValues);
	}

	private ContentValues createContentValues(Connection connection) {
		final ContentValues values = new ContentValues();
		values.put(ToLateDatabaseHelper.CONNECTION_ID_COL_NAME, connection.getId());
		values.put(ToLateDatabaseHelper.CONNECTION_NAME_COL_NAME, connection.getId());
		values.put(ToLateDatabaseHelper.CONNECTION_START_STATION_COL_NAME, connection.getId());
		if (connection.getStartTime() != null) {
			values.put(ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME, TIME_FORMAT.format(connection.getStartTime()));
		} else {
			values.put(ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CONNECTION_END_STATION_COL_NAME, connection.getId());
		if (connection.getEndTime() != null) {
			values.put(ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME, TIME_FORMAT.format(connection.getEndTime()));
		} else {
			values.put(ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CONNECTION_WEEKDAYS_COL_NAME, connection.isWeekdays());
		values.put(ToLateDatabaseHelper.CONNECTION_SATURDAY_COL_NAME, connection.isSaturday());
		values.put(ToLateDatabaseHelper.CONNECTION_SUNDAY_COL_NAME, connection.isSunday());
		return values;
	}

}
