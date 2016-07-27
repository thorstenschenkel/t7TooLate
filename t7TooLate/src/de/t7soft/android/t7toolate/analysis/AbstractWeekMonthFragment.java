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
package de.t7soft.android.t7toolate.analysis;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.database.ToLateDatabaseAdapter;
import de.t7soft.android.t7toolate.model.Capture;
import de.t7soft.android.t7toolate.utils.CaptureUtils;

public abstract class AbstractWeekMonthFragment extends AbstractAnalysisFragment {

	private View view;
	private WeekMonthCapturesCursorAdapter capturesAdapter;
	private TextView textViewTotalDelay;
	private TextView textViewCanceled;

	protected abstract Cursor getCursor();

	protected abstract int getViewResource();

	protected abstract void registerPickerListener(View weekMonthView);

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {

		view = inflater.inflate(getViewResource(), container, false);
		textViewTotalDelay = (TextView) view.findViewById(R.id.textViewTotalDelay);
		textViewCanceled = (TextView) view.findViewById(R.id.textViewCanceled);
		textViewCanceled.setVisibility(View.GONE);
		textViewCanceled.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(final View v) {
				if (v == textViewCanceled) {
					String msg = textViewCanceled.getText().toString();
					msg += view.getResources().getString(R.string.analysis_delay_canceled);
					Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
				}
			}
		});

		registerPickerListener(view);

		capturesAdapter = new WeekMonthCapturesCursorAdapter(getActivity(), getCursor());
		setListAdapter(capturesAdapter);

		return view;
	}

	@Override
	public void onResume() {
		updateListAdapter();
		updateTotalDelay();
		super.onResume();
	}

	protected void updateTotalDelay() {

		final Cursor cursor = getCursor();
		if ((cursor == null) || (cursor.getCount() <= 0)) {
			textViewTotalDelay.setText(R.string.analysis_no_captures);
			return;
		}

		int delay = 0;
		int canceled = 0;
		try {
			while (cursor.moveToNext()) {
				final Capture capture = ToLateDatabaseAdapter.createCapture(cursor);
				if (!capture.isCanceled()) {
					delay += CaptureUtils.getDelayMinutes(capture);
				} else {
					canceled++;
				}
			}
		} finally {
			cursor.close();
		}

		if (canceled > 0) {
			final String canceledText = canceled + "x ";
			textViewCanceled.setText(canceledText);
			textViewCanceled.setVisibility(View.VISIBLE);
		} else {
			textViewCanceled.setVisibility(View.GONE);
		}

		String delayText = "";
		if (delay > 0) {
			delayText += " +";
			delayText += delay;
		} else if (delay < 0) {
			delayText += delay;
		} else {
			delayText += delay;
		}
		delayText += " min";
		textViewTotalDelay.setText(delayText);

	}

	protected void updateListAdapter() {
		capturesAdapter.changeCursor(getCursor());
	}

}
