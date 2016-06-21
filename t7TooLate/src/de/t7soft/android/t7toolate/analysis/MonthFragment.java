package de.t7soft.android.t7toolate.analysis;

import java.util.Calendar;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.model.PeriodFilter;
import de.t7soft.android.t7toolate.utils.CaptureUtils;

public class MonthFragment extends ListFragment {

	private View monthView;
	private TextView textViewTotalDelay;
	private MonthPicker picker;
	private WeekCapturesCursorAdapter capturesAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		monthView = inflater.inflate(R.layout.fragment_month, container, false);

		picker = (MonthPicker) monthView.findViewById(R.id.monthPicker);
		picker.setOnMonthListener(new OnMonthListener() {
			@Override
			public boolean onMonthChanged(final MonthPicker v) {
				updateListAdapter();
				updateTotalDelay();
				return true;
			}
		});

		capturesAdapter = new WeekCapturesCursorAdapter(getActivity(), getCursor());
		setListAdapter(capturesAdapter);

		return monthView;
	}

	private Cursor getCursor() {
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

	private void updateListAdapter() {
		capturesAdapter.changeCursor(getCursor());
	}

	@Override
	public void onResume() {
		updateListAdapter();
		updateTotalDelay();
		super.onResume();
	}

	private void updateTotalDelay() {

		textViewTotalDelay = (TextView) monthView.findViewById(R.id.textViewTotalDelay);

		final Cursor cursor = getCursor();
		if ((cursor == null) || (cursor.getCount() <= 0)) {
			textViewTotalDelay.setText(R.string.analysis_no_captures);
			return;
		}

		int delay = 0;
		try {
			while (cursor.moveToNext()) {
				final Capture capture = ToLateDatabaseAdapter.createCapture(cursor);
				if (!capture.isCanceled()) {
					delay += CaptureUtils.getDelayMinutes(capture);
				} else {
					// TODO
				}
			}
		} finally {
			cursor.close();
		}
		String delayText = "";
		if (delay > 0) {
			delayText += " +";
			delayText += delay;
		} else if (delay < 0) {
			delayText += delay;
		} else {
			delayText += delay;
		}
		delayText += " min";
		textViewTotalDelay.setText(delayText);

	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {

		final int itemPosition = position - getListView().getHeaderViewsCount();
		if ((itemPosition >= 0) && (itemPosition < getListAdapter().getCount())) {

			final Capture capture = (Capture) getListAdapter().getItem(itemPosition);
			if (capture != null) {
				final Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
				intent.putExtra(ShowCaptureActivity.CAPTURE_ID, capture.getId());
				startActivity(intent);
			}

		}

	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

}
