package de.t7soft.android.t7toolate.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.t7soft.android.t7toolate.AbstractTabFragment;
import de.t7soft.android.t7toolate.R;

public class AnalysisFragment extends AbstractTabFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View analysisView = inflater.inflate(R.layout.analysis, container, false);
		return analysisView;
	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.analysis_tab_tilte);
	}

}
