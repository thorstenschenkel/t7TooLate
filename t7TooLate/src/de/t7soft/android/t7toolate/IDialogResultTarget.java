package de.t7soft.android.t7toolate;


public interface IDialogResultTarget {

	static final int CANCELED = -1;
	static final int DELETED = 1;

	void onDialogResult(int resultCode);

}
