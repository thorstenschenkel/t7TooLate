package de.t7soft.android.t7toolate.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

		allView = inflater.inflate(R.layout.fragment_all, container, false);

		listAdapter = createListAdapter(captures);
		setListAdapter(listAdapter);

		return allView;
	}

	private AllCapturesListAdapter createListAdapter(final List<Capture> captures) {
		return new AllCapturesListAdapter(this.getActivity(), captures);
	}

	private void updateListAdapter() {

		captures.clear();
		captures.addAll(getDbAdapter().getAllCaptures());
		Collections.sort(captures, new Comparator<Capture>() {
			@Override
			public int compare(final Capture capture1, final Capture capture2) {
				return capture1.getCaptureDateTime().compareTo(capture2.getCaptureDateTime());
			}
		});
		listAdapter.notifyDataSetChanged();

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
