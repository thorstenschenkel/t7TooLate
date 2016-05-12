package de.t7soft.android.t7toolate.analysis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;

public class WeekPicker extends LinearLayout {

	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE, dd.MM.yyyy");

	private final TextView textViewWeek;
	private final Button buttonWeekPrev;
	private final Button buttonWeekNext;
	private final Calendar currentMonday;

	public WeekPicker(final Context context, final AttributeSet attrs) {

		super(context, attrs);

		currentMonday = getMonday();

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.week_picker, this, true);
		textViewWeek = (TextView) findViewById(R.id.textViewWeek);
		buttonWeekPrev = (Button) findViewById(R.id.buttonWeekPrev);
		buttonWeekPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				prevWeek();
			}
		});
		buttonWeekNext = (Button) findViewById(R.id.buttonWeekNext);
		buttonWeekNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				nextWeek();
				// TODO disable button
			}
		});

		updateWeek();

	}

	private void prevWeek() {
		currentMonday.add(Calendar.DATE, -7);
		updateWeek();
	}

	private void nextWeek() {
		currentMonday.add(Calendar.DATE, 7);
		updateWeek();
	}

	private void updateWeek() {
		final String mondayStrg = DATE_TIME_FORMAT.format(getMonday().getTime());
		final String sundayStrg = DATE_TIME_FORMAT.format(getSunday(currentMonday).getTime());
		final String weekString = mondayStrg + "-" + sundayStrg;
		textViewWeek.setText(weekString);
	}

	private Calendar getMonday() {
		final Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return c;
	}

	private Calendar getSunday(final Calendar monday) {
		final Calendar c = Calendar.getInstance();
		c.setTime(monday.getTime());
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return c;
	}

}
