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
import android.widget.ImageView;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Connection;
import de.t7soft.android.t7toolate.model.ConnectionTypes;

public class ShowConnectionActivity extends Activity {

	public static final String CONNECTION_ID = "connectionId";
	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
	private Connection connection;
	private TextView nameValueTextView;
	private ImageView connectionTypeImageView;
	private TextView startStationValueTextView;
	private TextView startTimeValueTextView;
	private TextView endStationValueTextView;
	private TextView endTimeValueTextView;
	private CheckBox checkBoxConnectionMoFr;
	private CheckBox checkBoxConnectionSa;
	private CheckBox checkBoxConnectionSu;
	private ToLateDatabaseAdapter dbAdapter;

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

		connectionTypeImageView = (ImageView) findViewById(R.id.imageViewConnectionType);
		connectionTypeImageView.setImageResource(ConnectionTypes.ICON_IDS[connection.getConnectionType()]);

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
