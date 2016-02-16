package de.t7soft.android.t7toolate.capture;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.t7soft.android.t7toolate.ITabFragment;
import de.t7soft.android.t7toolate.R;

public class CaptueFragment extends Fragment implements ITabFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View captureView = inflater.inflate(R.layout.capture, container, false);
		return captureView;
	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.capture_tab_tilte);
	}

}
