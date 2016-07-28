/**
 * Copyright (c) 2016 Thorsten Schenkel (t7soft.de)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 * @author Thorsten Schenkel (t7soft.de)
 * 
 */
package de.t7soft.android.t7toolate.connections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import de.t7soft.android.t7toolate.IDialogResultTarget;
import de.t7soft.android.t7toolate.ITabFragment;
import de.t7soft.android.t7toolate.MainActivity;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;

public class ConnectionsFragment extends ListFragment implements ITabFragment, IDialogResultTarget {

	private static final String LOG_TAG = ConnectionsFragment.class.getSimpleName();

	private final List<Connection> connections = new ArrayList<Connection>();
	private BaseAdapter listAdapter;

	private DeleteConnectionDialog deleteDialog;

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		final View connectionsView = inflater.inflate(R.layout.connections, container, false);

		final Button buttonAddConnection = (Button) connectionsView.findViewById(R.id.buttonAddConnection);
		buttonAddConnection.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				onAdd(v);
			}
		});

		listAdapter = createListAdapter(connections);
		setListAdapter(listAdapter);

		return connectionsView;

	}

	@Override
	public void onActivityCreated(final Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		registerForContextMenu(getListView());
		deleteDialog = new DeleteConnectionDialog(getActivity(), this);
	}

	private ConnectionsListAdapter createListAdapter(final List<Connection> connections) {
		return new ConnectionsListAdapter(this.getActivity(), connections);
	}

	private void updateListAdapter() {

		connections.clear();
		connections.addAll(getDbAdapter().getAllConnections());
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

	public void onAdd(final View view) {
		showAddAction();
	}

	@Override
	public void onResume() {
		updateListAdapter();
		super.onResume();
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
			openConnection(connection);
		}

	}

	private void editConnection(final Connection connection) {
		if (connection != null) {
			final Intent intent = new Intent(getActivity(), EditConnectionActivity.class);
			intent.putExtra(EditConnectionActivity.CONNECTION_ID, connection.getId());
			startActivity(intent);
		}
	}

	private void openConnection(final Connection connection) {
		if (connection != null) {
			final Intent intent = new Intent(getActivity(), ShowConnectionActivity.class);
			intent.putExtra(ShowConnectionActivity.CONNECTION_ID, connection.getId());
			startActivity(intent);
		}
	}

	public ToLateDatabaseAdapter getDbAdapter() {
		return ((MainActivity) getActivity()).getDbAdapter();
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v == getListView()) {
			final MenuInflater inflater = getActivity().getMenuInflater();
			inflater.inflate(R.menu.connections_context_menu, menu);
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final Connection connection = (Connection) getListAdapter().getItem(info.position);
		if (connection == null) {
			Log.w(LOG_TAG, "No connection at position " + info.position);
			return false;
		}
		switch (item.getItemId()) {
			case R.id.open_connection_item:
				openConnection(connection);
				return true;
			case R.id.edit_connection_item:
				editConnection(connection);
				return true;
			case R.id.delete_connection_item:
				deleteDialog.show(connection);
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	@Override
	public void onDialogResult(final int resultCode) {
		if (resultCode == IDialogResultTarget.DELETED) {
			updateListAdapter();
			((MainActivity) getActivity()).setPickerInvalidate(true);
		}
	}

}
