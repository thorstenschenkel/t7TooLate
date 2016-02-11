package de.t7soft.android.t7toolate.connections;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
		inflater.inflate(R.menu.connections, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_add:
				showAddAction();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showAddAction() {
		final Intent intent = new Intent(this.getActivity(), AddConnectionActivity.class);
		startActivity(intent);
	}

}
