package de.t7soft.android.t7toolate.analysis;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;

public class AllListFragment extends ListFragment {

	private View allView;
	private AllCapturesCursorAdapter capturesAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		allView = inflater.inflate(R.layout.fragment_all, container, false);

		final Cursor cursor = getDbAdapter().getAllCapturesCursor();
		capturesAdapter = new AllCapturesCursorAdapter(getActivity(), cursor);
		setListAdapter(capturesAdapter);

		return allView;
	}

	private void updateListAdapter() {
		final Cursor cursor = getDbAdapter().getAllCapturesCursor();
		capturesAdapter.changeCursor(cursor);
	}

	@Override
	public void onResume() {
		updateListAdapter();
		super.onResume();
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
