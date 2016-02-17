package de.t7soft.android.t7toolate.connections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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

		final View connectionsView = inflater.inflate(R.layout.connections, container, false);

		listAdapter = createListAdapter(connections);
		setListAdapter(listAdapter);

		return connectionsView;

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

	public void onAdd(final View view) {
		showAddAction();
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

	private void showAddAction() {
		final Intent intent = new Intent(this.getActivity(), AddConnectionActivity.class);
		startActivity(intent);
	}

	@Override
	public void onListItemClick(final ListView l, final View v, final int position, final long id) {

		final int itemPosition = position - getListView().getHeaderViewsCount();
		if ((itemPosition >= 0) && (itemPosition < getListAdapter().getCount())) {

			final Connection connection = (Connection) getListAdapter().getItem(itemPosition);

			final Intent intent = new Intent(getActivity(), EditConnectionActivity.class);
			intent.putExtra(EditConnectionActivity.CONNECTION_ID, connection.getId());
			startActivity(intent);

		}

	}

}
