package de.t7soft.android.t7toolate.connections;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.utils.StringUtils;

public class EditConnectionActivity extends Activity {

	public static final String CONNECTION_ID = "connectionId";
	protected static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	protected Connection connection;
	protected EditText nameEditText;
	protected EditText startStationEditText;
	protected TextView startTimeTextView;
	protected EditText endStationEditText;
	protected TextView endTimeTextView;
	protected CheckBox checkBoxConnectionMoFr;
	protected CheckBox checkBoxConnectionSa;
	protected CheckBox checkBoxConnectionSu;
	protected ToLateDatabaseAdapter dbAdapter;
	private boolean save_called;

	protected boolean isEdit() {
		return true;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_connection);

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

	}

	@Override
	protected void onResume() {
		dbAdapter.open();
		connection = createConnection();
		initActivity();
		save_called = false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	protected Connection createConnection() {
		final String connectionId = getIntent().getExtras().getString(CONNECTION_ID);
		return dbAdapter.getConnection(connectionId);
	}

	private void initActivity() {

		nameEditText = (EditText) findViewById(R.id.editTextConnectionName);
		nameEditText.setText(connection.getName());
		nameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				validateName();
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
			}
		});

		startStationEditText = (EditText) findViewById(R.id.editTextConnectionStartStation);
		startStationEditText.setText(connection.getStartStation());
		startStationEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				validateStartStation();
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
			}
		});

		final Calendar c = Calendar.getInstance();
		c.setTime(connection.getStartTime());
		startTimeTextView = (TextView) findViewById(R.id.editTextConnectionStartTime);
		startTimeTextView.setText(TIME_FORMAT.format(c.getTime()));

		endStationEditText = (EditText) findViewById(R.id.editTextConnectionEndStation);
		endStationEditText.setText(connection.getEndStation());
		endStationEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
				validateEndStation();
			}

			@Override
			public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
			}
		});

		c.setTime(connection.getEndTime());
		endTimeTextView = (TextView) findViewById(R.id.editTextConnectionEndTime);
		endTimeTextView.setText(TIME_FORMAT.format(c.getTime()));

		checkBoxConnectionMoFr = (CheckBox) findViewById(R.id.checkBoxConnectionMoFr);
		checkBoxConnectionMoFr.setChecked(connection.isWeekdays());
		checkBoxConnectionSa = (CheckBox) findViewById(R.id.checkBoxConnectionSa);
		checkBoxConnectionSa.setChecked(connection.isSaturday());
		checkBoxConnectionSu = (CheckBox) findViewById(R.id.checkBoxConnectionSu);
		checkBoxConnectionSu.setChecked(connection.isSunday());

	}

	public void onCancel(final View view) {
		finish();
	}

	public void onSave(final View view) {

		connection.setWeekdays(checkBoxConnectionMoFr.isSelected());
		connection.setSaturday(checkBoxConnectionSa.isSelected());
		connection.setSunday(checkBoxConnectionSu.isSelected());

		save_called = true;

		if (!validate()) {
			return;
		}

		if (save()) {
			finish();
		}

	}

	protected boolean save() {
		final long rowId = dbAdapter.updateConnection(connection);
		if (rowId == -1) {
			final String errorMsg = getString(R.string.connection_error_update);
			showErrorMsg(errorMsg);
			return false;
		}
		return true;
	}

	private boolean validate() {

		boolean ok = true;

		if (!validateName()) {
			ok = false;
		}
		if (!validateStartStation()) {
			ok = false;
		}
		if (!validateEndStation()) {
			ok = false;
		}
		if (ok) {
			if (!validateTimes()) {
				return false;
			}
			if (!validateWeekdays()) {
				return false;
			}
		}

		return ok;

	}

	private boolean validateName() {

		if (!save_called) {
			return false;
		}

		connection.setName(nameEditText.getText().toString());

		if (StringUtils.isEmpty(connection.getName())) {
			nameEditText.setError(getString(R.string.connection_error_no_name));
			return false;
		} else {
			nameEditText.setError(null);
			return true;
		}

	}

	private boolean validateStartStation() {

		if (!save_called) {
			return false;
		}

		connection.setStartStation(startStationEditText.getText().toString());

		if (StringUtils.isEmpty(connection.getStartStation())) {
			startStationEditText.setError(getString(R.string.connection_error_no_start_station));
			return false;
		} else {
			startStationEditText.setError(null);
			return true;
		}

	}

	private boolean validateEndStation() {

		if (!save_called) {
			return false;
		}

		connection.setEndStation(endStationEditText.getText().toString());

		if (StringUtils.isEmpty(connection.getEndStation())) {
			endStationEditText.setError(getString(R.string.connection_error_no_end_station));
			return false;
		} else {
			endStationEditText.setError(null);
			return true;
		}

	}

	private boolean validateTimes() {

		boolean ok = true;
		startTimeTextView.setError(null);
		endTimeTextView.setError(null);

		String errorMessage = "";

		String timeString = startTimeTextView.getText().toString();
		try {
			final Date time = TIME_FORMAT.parse(timeString);
			connection.setStartTime(time);
		} catch (final ParseException e) {
			// startTimeTextView.setError(getString(R.string.connection_error_no_start_time));
			if (errorMessage.length() > 0) {
				errorMessage += "\n";
			}
			errorMessage += getString(R.string.connection_error_no_start_time);
			ok = false;
		}

		timeString = endTimeTextView.getText().toString();
		try {
			final Date time = TIME_FORMAT.parse(timeString);
			connection.setEndTime(time);
		} catch (final ParseException e) {
			// endTimeTextView.setError(getString(R.string.connection_error_no_time));
			if (errorMessage.length() > 0) {
				errorMessage += "\n";
			}
			errorMessage += getString(R.string.connection_error_no_end_time);
			ok = false;
		}

		if (ok) {
			if (!connection.getEndTime().after(connection.getStartTime())) {
				// endTimeTextView.setError(getString(R.string.connection_error_end_time));
				if (errorMessage.length() > 0) {
					errorMessage += "\n";
				}
				errorMessage += getString(R.string.connection_error_end_time);
				ok = false;
			}
		}

		if (!ok && !StringUtils.isEmpty(errorMessage)) {
			showErrorMsg(errorMessage);
		}

		return ok;

	}

	private boolean validateWeekdays() {

		connection.setWeekdays(checkBoxConnectionMoFr.isChecked());
		connection.setSaturday(checkBoxConnectionSa.isChecked());
		connection.setSunday(checkBoxConnectionSu.isChecked());

		if (connection.isWeekdays() || connection.isSaturday() || connection.isSunday()) {
			return true;
		}

		final String errorMessage = getString(R.string.connection_error_weekdays);
		showErrorMsg(errorMessage);

		return false;

	}

	protected void showErrorMsg(final String errorMessage) {
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(errorMessage) //
				// .setIcon(android.R.drawable.stat_notify_error)
				.setTitle(R.string.connection_error_msg_title);
		builder.setPositiveButton(getString(R.string.ok), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
			}
		});
		builder.create();
		builder.show();
	}

	public void onStartTime(final View view) {
		onTime(R.id.editTextConnectionStartTime);
	}

	public void onEndTime(final View view) {
		onTime(R.id.editTextConnectionEndTime);
	}

	private void onTime(final int timeFieldId) {

		final TextView timeTextView = (TextView) EditConnectionActivity.this.findViewById(timeFieldId);
		final TimeListener timeListener = new TimeListener(timeTextView);

		final CharSequence timeString = timeTextView.getText();
		final Calendar c = Calendar.getInstance();
		try {
			final Date time = TIME_FORMAT.parse(timeString.toString());
			c.setTime(time);
		} catch (final ParseException e) {
		}
		final int hour = c.get(Calendar.HOUR_OF_DAY);
		final int minute = c.get(Calendar.MINUTE);

		final TimePickerDialog dlg = new TimePickerDialog(this, timeListener, hour, minute,
				DateFormat.is24HourFormat(this));
		dlg.show();

	}

	private class TimeListener implements OnTimeSetListener {

		final TextView timeTextView;

		public TimeListener(final TextView timeField) {
			this.timeTextView = timeField;
		}

		@Override
		public void onTimeSet(final TimePicker view, final int hourOfDay, final int minute) {
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.HOUR_OF_DAY, hourOfDay);
			c.set(Calendar.MINUTE, minute);
			timeTextView.setText(TIME_FORMAT.format(c.getTime()));
		}

	}

}
