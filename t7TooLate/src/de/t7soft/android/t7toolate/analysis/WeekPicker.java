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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;

public class WeekPicker extends LinearLayout {

	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("EEE, dd.MM.yyyy");

	private final TextView textViewWeek;
	private final ImageButton buttonWeekThis;
	private final ImageButton buttonWeekPrev;
	private final ImageButton buttonWeekNext;
	private Calendar currentMonday;
	private ListenerInfo mListenerInfo;

	public WeekPicker(final Context context, final AttributeSet attrs) {

		super(context, attrs);

		currentMonday = getMonday();

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.week_picker, this, true);
		textViewWeek = (TextView) findViewById(R.id.textViewWeek);
		buttonWeekPrev = (ImageButton) findViewById(R.id.buttonWeekPrev);
		buttonWeekPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				prevWeek();
				fireWeekChanged();
			}
		});
		buttonWeekNext = (ImageButton) findViewById(R.id.buttonWeekNext);
		buttonWeekNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				nextWeek();
				fireWeekChanged();
			}
		});
		buttonWeekThis = (ImageButton) findViewById(R.id.buttonWeekThis);
		buttonWeekThis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				currentMonday = getMonday();
				updatePicker();
				fireWeekChanged();
			}
		});

		updatePicker();

	}

	private void prevWeek() {
		currentMonday.add(Calendar.DATE, -7);
		updatePicker();
	}

	private void nextWeek() {
		currentMonday.add(Calendar.DATE, 7);
		updatePicker();
	}

	private void updatePicker() {

		final String mondayStrg = DATE_TIME_FORMAT.format(getMonday(currentMonday).getTime());
		final String sundayStrg = DATE_TIME_FORMAT.format(getSunday(currentMonday).getTime());
		final String weekString = mondayStrg + " - " + sundayStrg;
		textViewWeek.setText(weekString);

		final boolean after = !currentMonday.after(getMonday());
		final boolean same = currentMonday.equals(getMonday());
		buttonWeekNext.setEnabled(after && !same);
		buttonWeekThis.setEnabled(!same);
	}

	private Calendar getMonday() {
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		removeTime(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return c;
	}

	private Calendar getMonday(final Calendar monday) {
		final Calendar c = Calendar.getInstance();
		c.setTime(monday.getTime());
		removeTime(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return c;
	}

	public Calendar getSunday(final Calendar monday) {
		final Calendar c = Calendar.getInstance();
		c.setTime(monday.getTime());
		removeTime(c);
		c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		return c;
	}

	private void removeTime(final Calendar calendar) {
		calendar.clear(Calendar.HOUR_OF_DAY);
		calendar.clear(Calendar.AM_PM);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		calendar.clear(Calendar.MILLISECOND);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
	}

	private boolean fireWeekChanged() {
		final ListenerInfo li = mListenerInfo;
		if ((li != null) && (li.mOnWeekListener != null)) {
			playSoundEffect(SoundEffectConstants.CLICK);
			return li.mOnWeekListener.onWeekChanged(this);
		}
		return false;
	}

	public Calendar getCurrentMonday() {
		return currentMonday;
	}

	public void setOnWeekListener(final OnWeekListener l) {
		getListenerInfo().mOnWeekListener = l;
	}

	@Override
	public boolean hasOnClickListeners() {
		final ListenerInfo li = mListenerInfo;
		return ((li != null) && (li.mOnWeekListener != null));
	}

	private ListenerInfo getListenerInfo() {
		if (mListenerInfo != null) {
			return mListenerInfo;
		}
		mListenerInfo = new ListenerInfo();
		return mListenerInfo;
	}

	private final static class ListenerInfo {

		public OnWeekListener mOnWeekListener;

	}

}
