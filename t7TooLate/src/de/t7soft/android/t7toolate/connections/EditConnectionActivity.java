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
import android.widget.TextView;
import android.widget.TimePicker;
import de.t7soft.android.t7toolate.R;

public class EditConnectionActivity extends Activity {

	protected static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_connection);

	}

	public void onCancel(final View view) {
		finish();
	}

	public void onSave(final View view) {
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
