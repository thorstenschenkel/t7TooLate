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
		final long rowId = dbAdapter.insertConnection(connection);
		if (rowId == -1) {
			final String errorMsg = getString(R.string.connection_error_insert);
			showErrorMsg(errorMsg);
			return false;
		}
		return true;
	}

}
