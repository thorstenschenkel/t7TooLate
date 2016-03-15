package de.t7soft.android.t7toolate.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;

public class AllListFragment extends ListFragment {

	private View allView;
	private final List<Capture> captures = new ArrayList<Capture>();
	private BaseAdapter listAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		setHasOptionsMenu(true);
		allView = inflater.inflate(R.layout.fragment_all, container, false);

		listAdapter = createListAdapter(captures);
		setListAdapter(listAdapter);

		return allView;
	}

	private AllCapturesListAdapter createListAdapter(final List<Capture> captures) {
		return new AllCapturesListAdapter(this.getActivity(), captures);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.analysis, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_filter:
				// TODO
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void updateListAdapter() {

		captures.clear();
		captures.addAll(getDbAdapter().getAllCaptures());
		Collections.sort(captures, new Comparator<Capture>() {
			@Override
			public int compare(final Capture capture1, final Capture capture2) {
				return capture2.getCaptureDateTime().compareTo(capture1.getCaptureDateTime());
			}
		});
		listAdapter.notifyDataSetChanged();

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

			final Intent intent = new Intent(getActivity(), ShowCaptureActivity.class);
			intent.putExtra(ShowCaptureActivity.CAPTURE_ID, capture.getId());
			startActivity(intent);

		}

	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

}
