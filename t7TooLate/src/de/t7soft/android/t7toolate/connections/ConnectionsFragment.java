package de.t7soft.android.t7toolate.connections;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import de.t7soft.android.t7toolate.AbstractTabFragment;
import de.t7soft.android.t7toolate.R;

public class ConnectionsFragment extends AbstractTabFragment {

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		final View connectionsView = inflater.inflate(R.layout.connections, container, false);
		return connectionsView;
	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.connections_tab_tilte);
	}

}
