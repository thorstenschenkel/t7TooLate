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
package de.t7soft.android.t7toolate.analysis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.DelayFilter;
import de.t7soft.android.t7toolate.model.PeriodFilter;
import de.t7soft.android.t7toolate.utils.FilterUtils;
import de.t7soft.android.t7toolate.utils.StringUtils;

public class FilterActivity extends Activity {

	private static final String LOG_TAG = FilterActivity.class.getSimpleName();

	private static final java.text.DateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	private Switch switchPeriod;
	private EditText editTextFilterFrom;
	private EditText editTextFilterTo;
	private Switch switchDelay;
	private CheckBox checkBoxOnTime;
	private CheckBox checkBoxSlight;
	private CheckBox checkBoxLate;
	private CheckBox checkBoxExterm;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.filter);

		checkBoxOnTime = (CheckBox) findViewById(R.id.checkBoxOnTime);
		checkBoxSlight = (CheckBox) findViewById(R.id.checkBoxSlight);
		checkBoxLate = (CheckBox) findViewById(R.id.checkBoxLate);
		checkBoxExterm = (CheckBox) findViewById(R.id.checkBoxExterm);

		switchPeriod = (Switch) findViewById(R.id.switchPeriod);
		switchPeriod.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				enablePeriodPanel(isChecked);
			}
		});

		switchDelay = (Switch) findViewById(R.id.switchDelay);
		switchDelay.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
				enableDelayPanel(isChecked);
			}
		});

		editTextFilterFrom = (EditText) findViewById(R.id.editTextFilterFrom);
		editTextFilterTo = (EditText) findViewById(R.id.editTextFilterTo);

	}

	private void enablePeriodPanel(final boolean isChecked) {
		final ViewGroup layoutPeriodFrom = (ViewGroup) findViewById(R.id.layoutPeriodFrom);
		final ViewGroup layoutPeriodTo = (ViewGroup) findViewById(R.id.layoutPeriodTo);
		disableEnableControls(isChecked, layoutPeriodFrom);
		disableEnableControls(isChecked, layoutPeriodTo);
	}

	private void enableDelayPanel(final boolean isChecked) {
		checkBoxOnTime.setEnabled(isChecked);
		checkBoxSlight.setEnabled(isChecked);
		checkBoxLate.setEnabled(isChecked);
		checkBoxExterm.setEnabled(isChecked);
	}

	@Override
	protected void onResume() {
		super.onResume();
		final PeriodFilter periodFilter = FilterUtils.createPeriodFilter(this);
		switchPeriod.setChecked(periodFilter.isActive());
		enablePeriodPanel(switchPeriod.isChecked());
		if (periodFilter.getFrom() == null) {
			editTextFilterFrom.setText("");
		} else {
			editTextFilterFrom.setText(DATE_FORMAT.format(periodFilter.getFrom()));
		}
		if (periodFilter.getTo() == null) {
			editTextFilterTo.setText("");
		} else {
			editTextFilterTo.setText(DATE_FORMAT.format(periodFilter.getTo()));
		}
		final DelayFilter delayFilter = FilterUtils.createDelayFilter(this);
		switchDelay.setChecked(delayFilter.isActive());
		checkBoxOnTime.setChecked(delayFilter.isOnTime());
		checkBoxSlight.setChecked(delayFilter.isSlight());
		checkBoxLate.setChecked(delayFilter.isLate());
		checkBoxExterm.setChecked(delayFilter.isExtreme());

		enableDelayPanel(switchDelay.isChecked());
	}

	public void onOk(final View view) {

		// period
		final PeriodFilter periodFilter = new PeriodFilter();
		periodFilter.setActive(switchPeriod.isChecked());
		String dateString = editTextFilterFrom.getText().toString();
		if (StringUtils.isEmpty(dateString)) {
			periodFilter.setFrom(null);
		} else {
			try {
				final Date date = DATE_FORMAT.parse(dateString);
				periodFilter.setFrom(date);
			} catch (final ParseException e) {
				Log.e(LOG_TAG, "string of from date has not the correct format", e);
				return;
			}
		}
		dateString = editTextFilterTo.getText().toString();
		if (StringUtils.isEmpty(dateString)) {
			periodFilter.setTo(null);
		} else {
			try {
				final Date date = DATE_FORMAT.parse(dateString);
				periodFilter.setTo(date);
			} catch (final ParseException e) {
				Log.e(LOG_TAG, "string of to date has not the correct format", e);
				return;
			}
		}
		FilterUtils.storePeriodFilter(this, periodFilter);

		// period
		final DelayFilter delayFilter = new DelayFilter(this);
		delayFilter.setActive(switchDelay.isChecked());
		delayFilter.setOnTime(checkBoxOnTime.isChecked());
		delayFilter.setSlight(checkBoxSlight.isChecked());
		delayFilter.setLate(checkBoxLate.isChecked());
		delayFilter.setExterme(checkBoxExterm.isChecked());
		FilterUtils.storeDelayFilter(this, delayFilter);

		finish();

	}

	public void onStartDate(final View view) {
		onDate(R.id.editTextFilterFrom);
	}

	public void onEndDate(final View view) {
		onDate(R.id.editTextFilterTo);
	}

	private void onDate(final int dateFieldId) {

		final TextView dateTextView = (TextView) findViewById(dateFieldId);
		final DateListener dateListener = new DateListener(dateTextView);

		final CharSequence dateString = dateTextView.getText();
		final Calendar c = Calendar.getInstance();
		try {
			final Date date = DATE_FORMAT.parse(dateString.toString());
			c.setTime(date);
		} catch (final ParseException e) {
		}
		final int day = c.get(Calendar.DAY_OF_MONTH);
		final int month = c.get(Calendar.MONTH);
		final int year = c.get(Calendar.YEAR);

		final DatePickerDialog dlg = new DatePickerDialog(this, dateListener, year, month, day);
		dlg.show();

	}

	private class DateListener implements OnDateSetListener {

		final TextView dateTextView;

		public DateListener(final TextView dateField) {
			this.dateTextView = dateField;
		}

		@Override
		public void onDateSet(final DatePicker view, final int year, final int month, final int day) {
			final Calendar c = Calendar.getInstance();
			c.set(Calendar.DAY_OF_MONTH, day);
			c.set(Calendar.MONTH, month);
			c.set(Calendar.YEAR, year);
			dateTextView.setText(DATE_FORMAT.format(c.getTime()));
		}

	}

	private void disableEnableControls(final boolean enable, final ViewGroup vg) {
		for (int i = 0; i < vg.getChildCount(); i++) {
			final View child = vg.getChildAt(i);
			child.setEnabled(enable);
			if (child instanceof ViewGroup) {
				disableEnableControls(enable, (ViewGroup) child);
			}
		}
	}

}
