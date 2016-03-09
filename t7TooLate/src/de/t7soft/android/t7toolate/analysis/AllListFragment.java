package de.t7soft.android.t7toolate.analysis;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.t7soft.android.t7toolate.R;

public class AllListFragment extends ListFragment {

	private View allView;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		allView = inflater.inflate(R.layout.fragment_all, container, false);
		return allView;
	}

}
