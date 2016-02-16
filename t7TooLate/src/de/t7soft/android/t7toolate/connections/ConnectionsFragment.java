package de.t7soft.android.t7toolate.connections;

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
import de.t7soft.android.t7toolate.ITabFragment;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;

public class ConnectionsFragment extends ListFragment implements ITabFragment {

	private ToLateDatabaseAdapter dbAdapter;
	private final List<Connection> connections = new ArrayList<Connection>();
	private BaseAdapter listAdapter;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		// final ListView listView = getListView();

		listAdapter = createListAdapter(connections);
		setListAdapter(listAdapter);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	private ConnectionsListAdapter createListAdapter(final List<Connection> connections) {
		return new ConnectionsListAdapter(this.getActivity(), connections);
	}

	private void updateListAdapter() {

		connections.clear();
		connections.addAll(dbAdapter.getAllConnections());
		Collections.sort(connections, new Comparator<Connection>() {
			@Override
			public int compare(final Connection connection1, final Connection connection2) {
				return connection1.getName().compareTo(connection2.getName());
			}
		});
		listAdapter.notifyDataSetChanged();

	}

	@Override
	public CharSequence getTabTitle() {
		return getResources().getString(R.string.connections_tab_tilte);
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(getActivity());
		}
		setHasOptionsMenu(true);
	}

	@Override
	public void onResume() {
		dbAdapter.open();
		updateListAdapter();
		super.onResume();
	}

	@Override
	public void onPause() {
		dbAdapter.close();
		super.onPause();
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
