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
