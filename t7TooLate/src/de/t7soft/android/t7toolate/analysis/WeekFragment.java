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
