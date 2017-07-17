package de.t7soft.android.t7toolate.database;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import de.t7soft.android.t7toolate.R;

public class DatabaseActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.database);

	}

	public void onClose(final View view) {
		finish();
	}

}
