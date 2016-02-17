package de.t7soft.android.t7toolate.connections;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.Connection;

public class AddConnectionActivity extends EditConnectionActivity {

	@Override
	protected boolean isEdit() {
		return false;
	}

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		final TextView subTitleTextView = (TextView) findViewById(R.id.textViewEditConnectionSubTitle);
		subTitleTextView.setText(R.string.add_connection_sub_tilte);

		final ImageView subTitleImageView = (ImageView) findViewById(R.id.imageViewEditConnectionSubTitle);
		subTitleImageView.setImageResource(R.drawable.ic_add);

		final Button addButton = (Button) findViewById(R.id.buttonConnectionSave);
		addButton.setText(R.string.add);

	}

	@Override
	protected Connection createConnection() {
		return new Connection();
	}

	@Override
	protected boolean save() {
		long rowId = dbAdapter.insertConnection(connection);
		if (rowId == -1) {
			String errorMsg = getString(R.string.connection_error_insert);
			showErrorMsg(errorMsg);
			return false;
		}
		return true;
	}

}
