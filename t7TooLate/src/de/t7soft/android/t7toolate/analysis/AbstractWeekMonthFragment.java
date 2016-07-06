package de.t7soft.android.t7toolate.analysis;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.utils.CaptureUtils;

public abstract class AbstractWeekMonthFragment extends ListFragment {

	private View view;
	private WeekMonthCapturesCursorAdapter capturesAdapter;
	private TextView textViewTotalDelay;
	private TextView textViewCanceled;

	protected abstract Cursor getCursor();

	protected abstract int getViewResource();

	protected abstract void registerPickerListener(View weekMonthView);

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		view = inflater.inflate(getViewResource(), container, false);
		textViewTotalDelay = (TextView) view.findViewById(R.id.textViewTotalDelay);
		textViewCanceled = (TextView) view.findViewById(R.id.textViewCanceled);
		textViewCanceled.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (v == textViewCanceled) {
					String msg = textViewCanceled.getText().toString();
					msg += view.getResources().getString(R.string.analysis_delay_canceled);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
			}
		});

		registerPickerListener(view);

		capturesAdapter = new WeekMonthCapturesCursorAdapter(getActivity(), getCursor());
		setListAdapter(capturesAdapter);

		return view;
	}

	@Override
	public void onResume() {
		updateListAdapter();
		updateTotalDelay();
		super.onResume();
	}

	protected void updateTotalDelay() {

		final Cursor cursor = getCursor();
		if ((cursor == null) || (cursor.getCount() <= 0)) {
			textViewTotalDelay.setText(R.string.analysis_no_captures);
			return;
		}

		int delay = 0;
		int canceled = 0;
		try {
			while (cursor.moveToNext()) {
				final Capture capture = ToLateDatabaseAdapter.createCapture(cursor);
				if (!capture.isCanceled()) {
					delay += CaptureUtils.getDelayMinutes(capture);
				} else {
					canceled++;
				}
			}
		} finally {
			cursor.close();
		}

		if (canceled > 0) {
			final String canceledText = canceled + "x ";
			textViewCanceled.setText(canceledText);
			textViewCanceled.setVisibility(View.VISIBLE);
		} else {
			textViewCanceled.setVisibility(View.GONE);
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

	protected void updateListAdapter() {
		capturesAdapter.changeCursor(getCursor());
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

}
