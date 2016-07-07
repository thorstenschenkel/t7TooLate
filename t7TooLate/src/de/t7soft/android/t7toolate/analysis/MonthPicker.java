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

public class MonthPicker extends LinearLayout {

	private static final DateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM yyyy");

	private final TextView textViewMonth;
	private final ImageButton buttonMonthThis;
	private final ImageButton buttonMonthPrev;
	private final ImageButton buttonMonthNext;
	private Calendar currentMonth;
	private ListenerInfo mListenerInfo;

	public MonthPicker(final Context context, final AttributeSet attrs) {

		super(context, attrs);

		currentMonth = getMonth();

		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.month_picker, this, true);
		textViewMonth = (TextView) findViewById(R.id.textViewMonth);
		buttonMonthPrev = (ImageButton) findViewById(R.id.buttonMonthPrev);
		buttonMonthPrev.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				prevMonth();
				fireMonthChanged();
			}
		});
		buttonMonthNext = (ImageButton) findViewById(R.id.buttonMonthNext);
		buttonMonthNext.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				nextMonth();
				fireMonthChanged();
			}
		});
		buttonMonthThis = (ImageButton) findViewById(R.id.buttonMonthThis);
		buttonMonthThis.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				currentMonth = getMonth();
				updatePicker();
				fireMonthChanged();
			}
		});

		updatePicker();

	}

	private void prevMonth() {
		currentMonth.add(Calendar.MONTH, -1);
		updatePicker();
	}

	private void nextMonth() {
		currentMonth.add(Calendar.MONTH, 1);
		updatePicker();
	}

	private void updatePicker() {

		final String monthString = DATE_TIME_FORMAT.format(currentMonth.getTime());
		textViewMonth.setText(monthString);

		final boolean after = !currentMonth.after(getMonth());
		final boolean same = currentMonth.equals(getMonth());
		buttonMonthNext.setEnabled(after && !same);
		buttonMonthThis.setEnabled(!same);
	}

	private Calendar getMonth() {
		final Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		removeTime(c);
		c.set(Calendar.DAY_OF_MONTH, 1);
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

	private boolean fireMonthChanged() {
		final ListenerInfo li = mListenerInfo;
		if ((li != null) && (li.mOnMonthListener != null)) {
			playSoundEffect(SoundEffectConstants.CLICK);
			return li.mOnMonthListener.onMonthChanged(this);
		}
		return false;
	}

	public Calendar getCurrentMonth() {
		return currentMonth;
	}

	public void setOnMonthListener(final OnMonthListener l) {
		getListenerInfo().mOnMonthListener = l;
	}

	@Override
	public boolean hasOnClickListeners() {
		final ListenerInfo li = mListenerInfo;
		return ((li != null) && (li.mOnMonthListener != null));
	}

	private ListenerInfo getListenerInfo() {
		if (mListenerInfo != null) {
			return mListenerInfo;
		}
		mListenerInfo = new ListenerInfo();
		return mListenerInfo;
	}

	private final static class ListenerInfo {

		public OnMonthListener mOnMonthListener;

	}

}
