package de.t7soft.android.t7toolate.connections;

import java.util.Calendar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;

public class AddConnectionActivity extends EditConnectionActivity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		final TextView subTitleTextView = (TextView) findViewById(R.id.textViewEditConnectionSubTitle);
		subTitleTextView.setText(R.string.add_connection_sub_tilte);

		final ImageView subTitleImageView = (ImageView) findViewById(R.id.imageViewEditConnectionSubTitle);
		subTitleImageView.setImageResource(R.drawable.ic_add);

		final Button addButton = (Button) findViewById(R.id.buttonConnectionSave);
		addButton.setText(R.string.add);

		initActivity();

	}

	private void initActivity() {

		final Calendar c = Calendar.getInstance();
		TextView timeTextView = (TextView) findViewById(R.id.editTextConnectionStartTime);
		timeTextView.setText(TIME_FORMAT.format(c.getTime()));
		timeTextView = (TextView) findViewById(R.id.editTextConnectionEndTime);
		timeTextView.setText(TIME_FORMAT.format(c.getTime()));

	}

}
