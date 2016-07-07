package de.t7soft.android.t7toolate;

import android.app.Activity;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

public class MainActivityTarget implements Target {

	public enum PosX {
		LEFT, CENTER, RIGHT
	}

	private final Activity activity;
	private final PosX posX;

	public MainActivityTarget(final Activity mainActivity, final PosX posX) {
		this.activity = mainActivity;
		this.posX = posX;
	}

	@Override
	public Point getPoint() {

		final View view = activity.getWindow().getDecorView();
		final DisplayMetrics displayMetrics = view.getResources().getDisplayMetrics();

		final int width = view.getWidth() / 3;
		int x = 0;
		final int y = (int) (100f * displayMetrics.density);
		switch (posX) {
			case LEFT:
				x += width / 2;
				break;
			case CENTER:
				x += (width / 2) + width;
				break;
			case RIGHT:
				x += (width / 2) + (width * 2);
				break;
			default:
				x += width / 2;
				break;
		}

		return new Point(x, y);
	}

}
