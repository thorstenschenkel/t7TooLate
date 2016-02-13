package de.t7soft.android.t7toolate.connections;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.utils.StringUtils;

public class EditConnectionActivity extends Activity {

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

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_connection);

		initActivity();
	}

	protected Connection createConnection() {
		// TODO
		return null;
	}

	private void initActivity() {

		nameEditText = (EditText) findViewById(R.id.editTextConnectionName);
		nameEditText.setText(connection.getName());

		startStationEditText = (EditText) findViewById(R.id.editTextConnectionStartStation);
		startStationEditText.setText(connection.getStartStation());

		final Calendar c = Calendar.getInstance();
		c.setTime(connection.getStartTime());
		endTimeTextView = (TextView) findViewById(R.id.editTextConnectionStartTime);
		endTimeTextView.setText(TIME_FORMAT.format(c.getTime()));

		EditText endStationEditText = (EditText) findViewById(R.id.editTextConnectionEndStation);
		endStationEditText.setText(connection.getEndStation());

		c.setTime(connection.getEndTime());
		startTimeTextView = (TextView) findViewById(R.id.editTextConnectionEndTime);
		startTimeTextView.setText(TIME_FORMAT.format(c.getTime()));

		checkBoxConnectionMoFr = (CheckBox) findViewById(R.id.checkBoxConnectionMoFr);
		checkBoxConnectionMoFr.setSelected(connection.isWeekdays());
		checkBoxConnectionSa = (CheckBox) findViewById(R.id.checkBoxConnectionSa);
		checkBoxConnectionSa.setSelected(connection.isSaturday());
		checkBoxConnectionSu = (CheckBox) findViewById(R.id.checkBoxConnectionSu);
		checkBoxConnectionSu.setSelected(connection.isSunday());

	}

	public void onCancel(final View view) {
		finish();
	}

	public void onSave(final View view) {

		connection.setName(nameEditText.getText().toString());

		connection.setStartStation(startStationEditText.getText().toString());

		String timeString = startTimeTextView.getText().toString();
		try {
			final Date time = TIME_FORMAT.parse(timeString);
			connection.setStartTime(time);
		} catch (final ParseException e) {
		}

		connection.setEndStation(endStationEditText.getText().toString());

		timeString = endTimeTextView.getText().toString();
		try {
			final Date time = TIME_FORMAT.parse(timeString);
			connection.setEndTime(time);
		} catch (final ParseException e) {
		}

		connection.setWeekdays(checkBoxConnectionMoFr.isSelected());
		connection.setSaturday(checkBoxConnectionSa.isSelected());
		connection.setSunday(checkBoxConnectionSu.isSelected());

		if (!validate()) {
			return;
		}

		// TODO

	}

	private boolean validate() {

		boolean ok = true;

		if (StringUtils.isEmpty(connection.getName())) {
			nameEditText.setError(getString(R.string.connection_error_no_name));
			ok = false;
		}

		return ok;

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

		final TimePickerDialog dlg = new TimePickerDialog(this, timeListener, hour, minute, DateFormat.is24HourFormat(this));
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
