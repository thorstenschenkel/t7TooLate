package de.t7soft.android.t7toolate.connections;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.Connection;

public class ConnectionsListAdapter extends BaseAdapter {

	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	private final Context context;
	private final List<Connection> listItems;
	private final LayoutInflater inflater;

	public ConnectionsListAdapter(final Context context, final List<Connection> listItems) {
		this.context = context;
		this.listItems = listItems;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return listItems.size();
	}

	@Override
	public Connection getItem(final int position) {
		return listItems.get(position);
	}

	@Override
	public long getItemId(final int position) {
		return position;
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {

		View rowView = convertView;
		if (rowView == null) {
			rowView = inflater.inflate(R.layout.connection_row, null);
		}
		final Connection connection = getItem(position);
		updateRow(rowView, connection);

		return rowView;
	}

	private void updateRow(final View rowView, final Connection connection) {

		final TextView textViewName = (TextView) rowView.findViewById(R.id.textViewRowConnectionName);
		textViewName.setText(connection.getName());

		final TextView textViewStartStation = (TextView) rowView.findViewById(R.id.textViewRowConnectionStartStation);
		textViewStartStation.setText(connection.getStartStation());

		final TextView textViewStartTime = (TextView) rowView.findViewById(R.id.textViewRowConnectionStartTime);
		String timeStrg = TIME_FORMAT.format(connection.getStartTime());
		textViewStartTime.setText(timeStrg);

		final TextView textViewEndStation = (TextView) rowView.findViewById(R.id.textViewRowConnectionEndStation);
		textViewEndStation.setText(connection.getEndStation());

		final TextView textViewEndTime = (TextView) rowView.findViewById(R.id.textViewRowConnectionEndTime);
		timeStrg = TIME_FORMAT.format(connection.getEndTime());
		textViewEndTime.setText(timeStrg);

	}

}
