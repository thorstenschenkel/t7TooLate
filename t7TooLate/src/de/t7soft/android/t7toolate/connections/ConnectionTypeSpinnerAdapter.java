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
