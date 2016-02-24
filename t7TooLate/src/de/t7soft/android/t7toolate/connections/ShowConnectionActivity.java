package de.t7soft.android.t7toolate.connections;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;

public class ShowConnectionActivity extends Activity {

	public static final String CONNECTION_ID = "connectionId";
	protected static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	protected Connection connection;
	protected TextView nameValueTextView;
	protected TextView startStationValueTextView;
	protected TextView startTimeValueTextView;
	protected TextView endStationValueTextView;
	protected TextView endTimeValueTextView;
	protected CheckBox checkBoxConnectionMoFr;
	protected CheckBox checkBoxConnectionSa;
	protected CheckBox checkBoxConnectionSu;
	protected ToLateDatabaseAdapter dbAdapter;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_connection);

		if (dbAdapter == null) {
			dbAdapter = new ToLateDatabaseAdapter(this);
		}

	}

	@Override
	protected void onResume() {
		dbAdapter.open();
		connection = createConnection();
		initActivity();
		super.onResume();
	}

	@Override
	protected void onPause() {
		dbAdapter.close();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.show_connections, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_edit:
				editConnection();
				return true;
			case R.id.action_delete:
				deleteConnection();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void editConnection() {
		final Intent intent = new Intent(this, EditConnectionActivity.class);
		intent.putExtra(EditConnectionActivity.CONNECTION_ID, connection.getId());
		startActivity(intent);
	}

	private void deleteConnection() {
		String deleteMessage = getString(R.string.connection_delete_msg_text);
		deleteMessage = MessageFormat.format(deleteMessage, connection.getName());
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(deleteMessage) //
				// .setIcon(android.R.drawable.stat_notify_error)
				.setTitle(R.string.connection_delete_msg_title);
		builder.setPositiveButton(getString(R.string.yes), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dbAdapter.deleteConnection(connection);
				dialog.dismiss();
				finish();
			}
		});
		builder.setNegativeButton(getString(R.string.no), new OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, final int which) {
				dialog.dismiss();
			}
		});
		builder.create();
		builder.show();
	}

	protected Connection createConnection() {
		final String connectionId = getIntent().getExtras().getString(CONNECTION_ID);
		return dbAdapter.getConnection(connectionId);
	}

	private void initActivity() {

		nameValueTextView = (TextView) findViewById(R.id.textViewConnectionNameValue);
		nameValueTextView.setText(connection.getName());

		startStationValueTextView = (TextView) findViewById(R.id.textViewConnectionStartStationValue);
		startStationValueTextView.setText(connection.getStartStation());

		startTimeValueTextView = (TextView) findViewById(R.id.textViewConnectionStartTimeValue);
		startTimeValueTextView.setText(TIME_FORMAT.format(connection.getStartTime()));

		endStationValueTextView = (TextView) findViewById(R.id.textViewConnectionEndStationValue);
		endStationValueTextView.setText(connection.getEndStation());

		endTimeValueTextView = (TextView) findViewById(R.id.textViewConnectionEndTimeValue);
		endTimeValueTextView.setText(TIME_FORMAT.format(connection.getEndTime()));

		checkBoxConnectionMoFr = (CheckBox) findViewById(R.id.checkBoxConnectionMoFr);
		checkBoxConnectionMoFr.setChecked(connection.isWeekdays());
		checkBoxConnectionSa = (CheckBox) findViewById(R.id.checkBoxConnectionSa);
		checkBoxConnectionSa.setChecked(connection.isSaturday());
		checkBoxConnectionSu = (CheckBox) findViewById(R.id.checkBoxConnectionSu);
		checkBoxConnectionSu.setChecked(connection.isSunday());

	}

	public void onCancel(final View view) {
		finish();
	}

}
