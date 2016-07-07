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
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.ConnectionTypes;

public class ConnectionTypeSpinnerAdapter extends ArrayAdapter<CharSequence> {

	private final List<Drawable> icons = new ArrayList<Drawable>();
	private final LayoutInflater inflater;
	private final int rowResource;
	private final int imageViewResource;

	public ConnectionTypeSpinnerAdapter(final Context context) {
		super(context, R.layout.type_row);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowResource = R.layout.type_row;
		imageViewResource = R.layout.type_image_view;
		for (final int iconId : ConnectionTypes.ICON_IDS) {
			icons.add(context.getResources().getDrawable(iconId));
		}
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, imageViewResource);
	}

	@Override
	public View getDropDownView(final int position, final View convertView, final ViewGroup parent) {
		return createViewFromResource(position, convertView, parent, rowResource);
	}

	private View createViewFromResource(final int position, final View convertView, final ViewGroup parent,
			final int resource) {
		View view;

		if (convertView == null) {
			view = inflater.inflate(resource, parent, false);
		} else {
			view = convertView;
		}

		final ImageView imageView = (ImageView) view.findViewById(R.id.imageViewRowConnectionType);
		imageView.setImageDrawable(icons.get(position));

		final TextView textView = (TextView) view.findViewById(R.id.textViewRowConnectionType);
		if (textView != null) {
			textView.setText(getItem(position));
		}

		return view;
	}

}
