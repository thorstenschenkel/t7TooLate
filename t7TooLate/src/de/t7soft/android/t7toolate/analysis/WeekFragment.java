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

import java.util.Calendar;

import android.database.Cursor;
import android.view.View;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.PeriodFilter;

public class WeekFragment extends AbstractWeekMonthFragment {

	private WeekPicker picker;

	@Override
	protected Cursor getCursor() {
		PeriodFilter filter = null;
		if (picker != null) {
			final Calendar monday = picker.getCurrentMonday();
			final Calendar sunday = picker.getSunday(monday);
			filter = new PeriodFilter();
			filter.setFrom(monday.getTime());
			filter.setTo(sunday.getTime());
			filter.setActive(true);
		}
		return getDbAdapter().getPeriodCapturesCursor(filter);
	}

	@Override
	protected int getViewResource() {
		return R.layout.fragment_week;
	}

	@Override
	protected void registerPickerListener(final View weekMonthView) {
		picker = (WeekPicker) weekMonthView.findViewById(R.id.weekPicker);
		picker.setOnWeekListener(new OnWeekListener() {
			@Override
			public boolean onWeekChanged(final WeekPicker v) {
				updateListAdapter();
				updateTotalDelay();
				return true;
			}
		});
	}

}
