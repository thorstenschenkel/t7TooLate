package de.t7soft.android.t7toolate.database;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.Connection;

public class ToLateDatabaseAdapter {

	private static final String LOGTAG = ToLateDatabaseAdapter.class.getSimpleName();
	private static final DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");

	private final ToLateDatabaseHelper dbHelper;
	private SQLiteDatabase database;

	public ToLateDatabaseAdapter(final Context context) {
		dbHelper = new ToLateDatabaseHelper(context);
	}

	public void open() throws SQLException {
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

	public long insertCapture(final Capture capture) {
		return insertCapture(database, capture);
	}

	private long insertCapture(final SQLiteDatabase db, final Capture capture) {
		final ContentValues initialValues = createContentValues(capture);
		return db.insert(ToLateDatabaseHelper.CAPTURES_TABLE_NAME, null, initialValues);
	}

	private ContentValues createContentValues(final Capture capture) {
		final ContentValues values = new ContentValues();
		values.put(ToLateDatabaseHelper.CAPTURE_ID_COL_NAME, capture.getId());
		final Connection connection = capture.getConnection();
		values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_NAME_COL_NAME, connection.getName());
		values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_START_STATION_COL_NAME, connection.getStartStation());
		if (connection.getStartTime() != null) {
			values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_START_TIME_COL_NAME,
					TIME_FORMAT.format(connection.getStartTime()));
		} else {
			values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_START_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_END_STATION_COL_NAME, connection.getEndStation());
		if (connection.getEndTime() != null) {
			values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_END_TIME_COL_NAME,
					TIME_FORMAT.format(connection.getEndTime()));
		} else {
			values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_END_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CAPTURE_CONNECTION_TYPE_COL_NAME, connection.getConnectionType());
		if (capture.getCaptureDateTime() != null) {
			values.put(ToLateDatabaseHelper.CAPTURE_DATE_TIME_COL_NAME,
					DATE_TIME_FORMAT.format(capture.getCaptureDateTime()));
		} else {
			values.put(ToLateDatabaseHelper.CAPTURE_DATE_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CAPTURE_COMMENT_COL_NAME, capture.getComment());
		return values;
	}

	public long insertConnection(final Connection connection) {
		return insertConnection(database, connection);
	}

	private long insertConnection(final SQLiteDatabase db, final Connection connection) {
		final ContentValues initialValues = createContentValues(connection);
		return db.insert(ToLateDatabaseHelper.CONNECTIONS_TABLE_NAME, null, initialValues);
	}

	private ContentValues createContentValues(final Connection connection) {
		final ContentValues values = new ContentValues();
		values.put(ToLateDatabaseHelper.CONNECTION_ID_COL_NAME, connection.getId());
		values.put(ToLateDatabaseHelper.CONNECTION_NAME_COL_NAME, connection.getName());
		values.put(ToLateDatabaseHelper.CONNECTION_START_STATION_COL_NAME, connection.getStartStation());
		if (connection.getStartTime() != null) {
			values.put(ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME,
					TIME_FORMAT.format(connection.getStartTime()));
		} else {
			values.put(ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CONNECTION_END_STATION_COL_NAME, connection.getEndStation());
		if (connection.getEndTime() != null) {
			values.put(ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME, TIME_FORMAT.format(connection.getEndTime()));
		} else {
			values.put(ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CONNECTION_WEEKDAYS_COL_NAME, connection.isWeekdays());
		values.put(ToLateDatabaseHelper.CONNECTION_SATURDAY_COL_NAME, connection.isSaturday());
		values.put(ToLateDatabaseHelper.CONNECTION_SUNDAY_COL_NAME, connection.isSunday());
		values.put(ToLateDatabaseHelper.CONNECTION_TYPE_COL_NAME, connection.getConnectionType());
		return values;
	}

	public List<Capture> getAllCaptures() {
		return getAllCapture(database);
	}

	public List<Capture> getAllCapture(final SQLiteDatabase db) {

		final List<Capture> captures = new ArrayList<Capture>();

		final Cursor cursor = db.query(ToLateDatabaseHelper.CAPTURES_TABLE_NAME, null, null, null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					final Capture capture = createCapture(cursor);
					captures.add(capture);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return captures;
	}

	private static Capture createCapture(final Cursor cursor) {
		final String id = getString(cursor, ToLateDatabaseHelper.CAPTURE_ID_COL_NAME);
		final Capture capture = new Capture(id);
		final Connection connection = capture.getConnection();
		connection.setName(getString(cursor, ToLateDatabaseHelper.CAPTURE_CONNECTION_NAME_COL_NAME));
		connection.setStartStation(getString(cursor, ToLateDatabaseHelper.CAPTURE_CONNECTION_START_STATION_COL_NAME));
		connection.setStartTime(getTime(cursor, ToLateDatabaseHelper.CAPTURE_CONNECTION_START_TIME_COL_NAME));
		connection.setEndStation(getString(cursor, ToLateDatabaseHelper.CAPTURE_CONNECTION_END_STATION_COL_NAME));
		connection.setEndTime(getTime(cursor, ToLateDatabaseHelper.CAPTURE_CONNECTION_END_TIME_COL_NAME));
		connection.setConnectionType(getInt(cursor, ToLateDatabaseHelper.CAPTURE_CONNECTION_TYPE_COL_NAME));
		capture.setCaptureDateTime(getDateTime(cursor, ToLateDatabaseHelper.CAPTURE_DATE_TIME_COL_NAME));
		capture.setComment(getString(cursor, ToLateDatabaseHelper.CAPTURE_COMMENT_COL_NAME));
		return capture;
	}

	public List<Connection> getAllConnections() {
		return getAllConnections(database);
	}

	public List<Connection> getAllConnections(final SQLiteDatabase db) {

		final List<Connection> connections = new ArrayList<Connection>();

		final Cursor cursor = db.query(ToLateDatabaseHelper.CONNECTIONS_TABLE_NAME, null, null, null, null, null, null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				while (!cursor.isAfterLast()) {
					final Connection connection = createConnection(cursor);
					connections.add(connection);
					cursor.moveToNext();
				}
			}
			cursor.close();
		}
		return connections;
	}

	private static Connection createConnection(final Cursor cursor) {
		final String id = getString(cursor, ToLateDatabaseHelper.CONNECTION_ID_COL_NAME);
		final Connection connection = new Connection(id);
		connection.setName(getString(cursor, ToLateDatabaseHelper.CONNECTION_NAME_COL_NAME));
		connection.setStartStation(getString(cursor, ToLateDatabaseHelper.CONNECTION_START_STATION_COL_NAME));
		connection.setStartTime(getTime(cursor, ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME));
		connection.setEndStation(getString(cursor, ToLateDatabaseHelper.CONNECTION_END_STATION_COL_NAME));
		connection.setEndTime(getTime(cursor, ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME));
		connection.setWeekdays(getBoolean(cursor, ToLateDatabaseHelper.CONNECTION_WEEKDAYS_COL_NAME));
		connection.setSaturday(getBoolean(cursor, ToLateDatabaseHelper.CONNECTION_SATURDAY_COL_NAME));
		connection.setSunday(getBoolean(cursor, ToLateDatabaseHelper.CONNECTION_SUNDAY_COL_NAME));
		connection.setConnectionType(getInt(cursor, ToLateDatabaseHelper.CONNECTION_TYPE_COL_NAME));
		return connection;
	}

	private static Date getDateTime(final Cursor cursor, final String columnName) {
		final String strg = getString(cursor, cursor.getColumnIndex(columnName));
		try {
			return DATE_TIME_FORMAT.parse(strg);
		} catch (final ParseException e) {
			return null;
		}
	}

	private static Date getTime(final Cursor cursor, final String columnName) {
		final String strg = getString(cursor, cursor.getColumnIndex(columnName));
		try {
			return TIME_FORMAT.parse(strg);
		} catch (final ParseException e) {
			return null;
		}
	}

	private static String getString(final Cursor cursor, final String columnName) {
		return getString(cursor, cursor.getColumnIndex(columnName));
	}

	private static String getString(final Cursor cursor, final int columnIndex) {
		String value = null;
		if (columnIndex < 0) {
			return value;
		}
		try {
			value = cursor.getString(columnIndex);
		} catch (final Exception e) {
			value = null;
		}
		return value;
	}

	private static int getInt(final Cursor cursor, final String columnName) {
		return getInt(cursor, cursor.getColumnIndex(columnName));
	}

	private static int getInt(final Cursor cursor, final int columnIndex) {
		int value = -1;
		if (columnIndex < 0) {
			return -1;
		}
		try {
			value = cursor.getInt(columnIndex);
		} catch (final Exception e) {
			value = -1;
		}
		return value;
	}

	private static boolean getBoolean(final Cursor cursor, final String columnName) {
		return getBoolean(cursor, cursor.getColumnIndex(columnName));
	}

	private static boolean getBoolean(final Cursor cursor, final int columnIndex) {
		boolean value = false;
		if (columnIndex < 0) {
			return false;
		}
		try {
			final int intValue = cursor.getInt(columnIndex);
			value = (intValue != 0);
		} catch (final Exception e) {
			value = false;
		}
		return value;
	}

	public Connection getConnection(final String id) {
		return getConnection(database, id);
	}

	private static Connection getConnection(final SQLiteDatabase db, final String id) {

		Connection connection = null;

		final String selection = createConnectionSelection(id);
		final Cursor cursor = db.query(ToLateDatabaseHelper.CONNECTIONS_TABLE_NAME, null, selection, null, null, null,
				null);

		if (cursor != null) {
			if (cursor.moveToFirst()) {
				connection = createConnection(cursor);
			}
			cursor.close();
		}

		return connection;

	}

	private static String createConnectionSelection(final String id) {
		return ToLateDatabaseHelper.CONNECTION_ID_COL_NAME + "=" + "\"" + id + "\"";
	}

	public long updateConnection(final Connection connection) {
		return updateConnection(database, connection);
	}

	private static long updateConnection(final SQLiteDatabase db, final Connection connection) {
		final String selection = createConnectionSelection(connection.getId());
		final ContentValues values = new ContentValues();
		values.put(ToLateDatabaseHelper.CONNECTION_NAME_COL_NAME, connection.getName());
		values.put(ToLateDatabaseHelper.CONNECTION_START_STATION_COL_NAME, connection.getStartStation());
		if (connection.getStartTime() != null) {
			values.put(ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME,
					TIME_FORMAT.format(connection.getStartTime()));
		} else {
			values.put(ToLateDatabaseHelper.CONNECTION_START_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CONNECTION_END_STATION_COL_NAME, connection.getEndStation());
		if (connection.getEndTime() != null) {
			values.put(ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME, TIME_FORMAT.format(connection.getEndTime()));
		} else {
			values.put(ToLateDatabaseHelper.CONNECTION_END_TIME_COL_NAME, "");
		}
		values.put(ToLateDatabaseHelper.CONNECTION_WEEKDAYS_COL_NAME, connection.isWeekdays());
		values.put(ToLateDatabaseHelper.CONNECTION_SATURDAY_COL_NAME, connection.isSaturday());
		values.put(ToLateDatabaseHelper.CONNECTION_SUNDAY_COL_NAME, connection.isSunday());
		values.put(ToLateDatabaseHelper.CONNECTION_TYPE_COL_NAME, connection.getConnectionType());
		return db.update(ToLateDatabaseHelper.CONNECTIONS_TABLE_NAME, values, selection, null);
	}

	public int deleteConnection(final Connection connection) {
		return deleteConnection(database, connection);
	}

	public static int deleteConnection(final SQLiteDatabase db, final Connection connection) {
		final String selection = createConnectionSelection(connection.getId());
		return db.delete(ToLateDatabaseHelper.CONNECTIONS_TABLE_NAME, selection, null);
	}

}
