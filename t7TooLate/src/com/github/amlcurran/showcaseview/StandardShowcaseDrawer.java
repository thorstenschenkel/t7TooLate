/*
 * Copyright 2014 Alex Curran
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.amlcurran.showcaseview;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import de.t7soft.android.t7toolate.R;

class StandardShowcaseDrawer implements ShowcaseDrawer {

	protected final Paint eraserPaint;
	protected final Drawable showcaseDrawable;
	private final Paint basicPaint;
	private final float showcaseRadius;
	protected int backgroundColour;

	public StandardShowcaseDrawer(final Resources resources, final Resources.Theme theme) {
		final PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY);
		eraserPaint = new Paint();
		eraserPaint.setColor(0xFFFFFF);
		eraserPaint.setAlpha(0);
		eraserPaint.setXfermode(xfermode);
		eraserPaint.setAntiAlias(true);
		basicPaint = new Paint();
		showcaseRadius = resources.getDimension(R.dimen.showcase_radius);
		showcaseDrawable = resources.getDrawable(R.drawable.cling_bleached);
	}

	@Override
	public void setShowcaseColour(final int color) {
		showcaseDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
	}

	@Override
	public void drawShowcase(final Bitmap buffer, final float x, final float y, final float scaleMultiplier) {
		final Canvas bufferCanvas = new Canvas(buffer);
		bufferCanvas.drawCircle(x, y, showcaseRadius, eraserPaint);
		final int halfW = getShowcaseWidth() / 2;
		final int halfH = getShowcaseHeight() / 2;
		final int left = (int) (x - halfW);
		final int top = (int) (y - halfH);
		showcaseDrawable.setBounds(left, top, left + getShowcaseWidth(), top + getShowcaseHeight());
		showcaseDrawable.draw(bufferCanvas);
	}

	@Override
	public int getShowcaseWidth() {
		return showcaseDrawable.getIntrinsicWidth();
	}

	@Override
	public int getShowcaseHeight() {
		return showcaseDrawable.getIntrinsicHeight();
	}

	@Override
	public float getBlockedRadius() {
		return showcaseRadius;
	}

	@Override
	public void setBackgroundColour(final int backgroundColor) {
		this.backgroundColour = backgroundColor;
	}

	@Override
	public void erase(final Bitmap bitmapBuffer) {
		bitmapBuffer.eraseColor(backgroundColour);
	}

	@Override
	public void drawToCanvas(final Canvas canvas, final Bitmap bitmapBuffer) {
		canvas.drawBitmap(bitmapBuffer, 0, 0, basicPaint);
	}

}
