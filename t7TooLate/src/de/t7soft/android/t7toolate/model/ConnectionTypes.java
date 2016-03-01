package de.t7soft.android.t7toolate.model;

import de.t7soft.android.t7toolate.R;

public final class ConnectionTypes {

	public static final int UNKOWN = 0;
	public static final int TRAIN = 1;
	public static final int BUS = 2;
	public static final int TRAM = 3;
	public static final int PLANE = 4;

	public static int[] ICON_IDS = {
			R.drawable.ct_train, R.drawable.ct_train, R.drawable.ct_bus, R.drawable.ct_tram, R.drawable.ct_plane
	};

	private ConnectionTypes() {
	}

}
