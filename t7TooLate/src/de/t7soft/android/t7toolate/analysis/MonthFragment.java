package de.t7soft.android.t7toolate.analysis;

import java.util.Calendar;

import android.database.Cursor;
import android.view.View;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.PeriodFilter;

public class MonthFragment extends AbstractWeekMonthFragment {

	private MonthPicker picker;

	@Override
	protected Cursor getCursor() {
		PeriodFilter filter = null;
		if (picker != null) {
			final Calendar firstDay = picker.getCurrentMonth();
			final Calendar lastDay = Calendar.getInstance();
			lastDay.setTime(firstDay.getTime());
			lastDay.set(Calendar.DATE, lastDay.getActualMaximum(Calendar.DAY_OF_MONTH));
			filter = new PeriodFilter();
			filter.setFrom(firstDay.getTime());
			filter.setTo(lastDay.getTime());
			filter.setActive(true);
		}
		return getDbAdapter().getPeriodCapturesCursor(filter);
	}

	@Override
	protected int getViewResource() {
		return R.layout.fragment_month;
	}

	@Override
	protected void registerPickerListener(final View weekMonthView) {
		picker = (MonthPicker) weekMonthView.findViewById(R.id.monthPicker);
		picker.setOnMonthListener(new OnMonthListener() {
			@Override
			public boolean onMonthChanged(final MonthPicker v) {
				updateListAdapter();
				updateTotalDelay();
				return true;
			}
		});
	}

}
