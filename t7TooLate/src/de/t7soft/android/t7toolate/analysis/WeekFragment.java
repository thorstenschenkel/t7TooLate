package de.t7soft.android.t7toolate.analysis;

import java.util.Calendar;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.PeriodFilter;

public class WeekFragment extends ListFragment {

	private View weekView;
	private WeekPicker picker;
	private WeekCapturesCursorAdapter capturesAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		weekView = inflater.inflate(R.layout.fragment_week, container, false);

		picker = (WeekPicker) weekView.findViewById(R.id.weekPicker);
		picker.setOnWeekListener(new OnWeekListener() {
			@Override
			public boolean onWeekChanged(final WeekPicker v) {
				capturesAdapter.changeCursor(getCursor());
				return true;
			}
		});

		capturesAdapter = new WeekCapturesCursorAdapter(getActivity(), getCursor());
		setListAdapter(capturesAdapter);

		return weekView;
	}

	private Cursor getCursor() {
		PeriodFilter filter = null;
		if (picker != null) {
			final Calendar monday = picker.getCurrentMonday();
			final Calendar sunday = picker.getSunday(monday);
			filter = new PeriodFilter();
			filter.setFrom(monday.getTime());
			filter.setTo(sunday.getTime());
			filter.setActive(true);
		}
		return getDbAdapter().getWeekCapturesCursor(filter);
	}

	private void updateListAdapter() {
		capturesAdapter.changeCursor(getCursor());
	}

	@Override
	public void onResume() {
		updateListAdapter();
		super.onResume();
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

}
