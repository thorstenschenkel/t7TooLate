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
package de.t7soft.android.t7toolate.capture;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;
import de.t7soft.android.t7toolate.R;
import de.t7soft.android.t7toolate.model.Connection;

/**
 * https://android-arsenal.com/details/1/3309
 */
public class ConnectionPicker extends NumberPicker {

	// NumberPicker.SELECTOR_WHEEL_ITEM_COUNT
	private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
	// NumberPicker.SELECTOR_MIDDLE_ITEM_INDEX
	private static final int SELECTOR_MIDDLE_ITEM_INDEX = SELECTOR_WHEEL_ITEM_COUNT / 2;
	/**
	 * The default unscaled height of the selection divider.
	 */
	private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;

	/**
	 * The default unscaled distance between the selection dividers.
	 */
	private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
	private static final java.text.DateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");

	/**
	 * The text for showing the current value.
	 */
	private EditText pickerInputText;
	private Paint selectorWheelPaint;
	private Paint selectorWheelPaint2;
	private int selectorElementHeight;
	private int mTopSelectionDividerTop;
	private int mBottomSelectionDividerBottom;
	private int mSelectionDividerHeight;
	private Drawable selectionDivider;
	private List<Connection> connections;
	private Rect orgiBmpRect;
	private Rect destBmpRect;
	private Rect nameBounds;

	public ConnectionPicker(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ConnectionPicker(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ConnectionPicker(final Context context) {
		super(context);
		init();
	}

	private void init() {

		pickerInputText = getInputText();

		if (pickerInputText != null) {

			final Paint paint = new Paint();
			paint.setAntiAlias(true);
			paint.setTextAlign(Align.CENTER);
			paint.setTextSize((int) (pickerInputText.getTextSize() * 1.10f));
			paint.setTypeface(pickerInputText.getTypeface());
			final ColorStateList colors = pickerInputText.getTextColors();
			final int color = colors.getColorForState(ENABLED_STATE_SET, Color.WHITE);
			paint.setColor(color);
			selectorWheelPaint = paint;

			selectorWheelPaint2 = new Paint(selectorWheelPaint);
			selectorWheelPaint2.setTextSize((int) (pickerInputText.getTextSize() * 0.75f));
			final int color2 = getResources().getColor(R.color.btn_background);
			selectorWheelPaint2.setColor(color2);
			pickerInputText.setVisibility(INVISIBLE);
		}

		selectionDivider = getMSelectionDivider();

		nameBounds = new Rect();
		orgiBmpRect = new Rect();
		destBmpRect = new Rect();

	}

	public void setValueInternal(final int current, final boolean notifyChange) {
		try {
			final Method m = getClass().getSuperclass().getDeclaredMethod("setValueInternal", int.class, boolean.class);
			m.setAccessible(true);
			m.invoke(this, current, notifyChange);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onLayout(final boolean changed, final int left, final int top, final int right, final int bottom) {

		super.onLayout(changed, left, top, right, bottom);

		if (changed && (pickerInputText != null)) {

			final int[] selectorIndices = getMSelectorIndices();
			if (selectorIndices.length == 0) {
				selectorElementHeight = Integer.MIN_VALUE;
				return;
			}
			final int textSize = (int) pickerInputText.getTextSize();
			final int totalTextHeight = selectorIndices.length * textSize;
			final float totalTextGapHeight = getHeight() - totalTextHeight;
			final float textGapCount = selectorIndices.length;
			final int selectorTextGapHeight = (int) ((totalTextGapHeight / textGapCount) + 0.5f);
			selectorElementHeight = textSize + selectorTextGapHeight;

		}

		if (changed) {
			final int defSelectionDividerDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE, getResources().getDisplayMetrics());
			final int defSelectionDividerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT, getResources().getDisplayMetrics());
			mSelectionDividerHeight = defSelectionDividerHeight;
			mTopSelectionDividerTop = ((getHeight() - defSelectionDividerDistance) / 2) - mSelectionDividerHeight;
			mBottomSelectionDividerBottom = mTopSelectionDividerTop + (2 * mSelectionDividerHeight)
					+ defSelectionDividerDistance;
		}
	}

	@Override
	protected void onDraw(final Canvas canvas) {

		boolean callSuper = false;

		if ((pickerInputText == null) || (selectorWheelPaint == null) || (selectorElementHeight == Integer.MIN_VALUE)) {
			callSuper = true;
		}

		final float x = getWidth() / 2;
		int y = getMCurrentScrollOffset();
		if (y == Integer.MIN_VALUE) {
			callSuper = true;
		}

		final int[] selectorIndices = getMSelectorIndices();
		if (selectorIndices.length == 0) {
			callSuper = true;
		}
		final SparseArray<String> selectorIndexToStringCache = getMSelectorIndexToStringCache();
		if (selectorIndexToStringCache == null) {
			callSuper = true;
		}

		if (callSuper) {
			super.onDraw(canvas);
			return;
		}

		for (int i = 0; i < selectorIndices.length; i++) {
			final int selectorIndex = selectorIndices[i];
			final String scrollSelectorValue = selectorIndexToStringCache.get(selectorIndex);
			if ((i != SELECTOR_MIDDLE_ITEM_INDEX) || (pickerInputText.getVisibility() != VISIBLE)) {
				final Connection connection = getConnection(scrollSelectorValue);
				if (connection != null) {
					selectorWheelPaint.getTextBounds(scrollSelectorValue, 0, scrollSelectorValue.length(), nameBounds);
					final int yOffset = (int) ((selectorWheelPaint.getTextSize() + selectorWheelPaint2.getTextSize()) / 4.0f);
					canvas.drawText(scrollSelectorValue, x, y - yOffset, selectorWheelPaint);
					// drawBitmap(canvas, connection, x, y - yOffset);
					canvas.drawText(getLineTwo(connection), x, y + yOffset, selectorWheelPaint2);
				}
			}
			y += selectorElementHeight;
		}

		if (selectionDivider != null) {
			// draw the top divider
			final int topOfTopDivider = mTopSelectionDividerTop;
			final int bottomOfTopDivider = topOfTopDivider + mSelectionDividerHeight;
			selectionDivider.setBounds(0, topOfTopDivider, getWidth(), bottomOfTopDivider);
			selectionDivider.draw(canvas);

			// draw the bottom divider
			final int bottomOfBottomDivider = mBottomSelectionDividerBottom;
			final int topOfBottomDivider = bottomOfBottomDivider - mSelectionDividerHeight;
			selectionDivider.setBounds(0, topOfBottomDivider, getWidth(), bottomOfBottomDivider);
			selectionDivider.draw(canvas);
		}

	}

	// private void drawBitmap(final Canvas canvas, final Connection connection, final float x, final float y) {
	//
	// final int drawableResId = ConnectionTypes.ICON_IDS[connection.getConnectionType()];
	// final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawableResId);
	// orgiBmpRect.right = bitmap.getWidth();
	// orgiBmpRect.bottom = bitmap.getHeight();
	//
	// final int newHeight = (int) (Math.abs(nameBounds.height()) * 1.3);
	// final int newWidth = newHeight;
	// destBmpRect.left = (int) (x - (nameBounds.width() / 2) - newWidth - (newWidth / 2));
	// destBmpRect.right = destBmpRect.left + newWidth;
	// destBmpRect.top = (int) (y - newHeight);
	// destBmpRect.bottom = destBmpRect.top + newHeight;
	// canvas.drawBitmap(bitmap, orgiBmpRect, destBmpRect, null);
	//
	// }

	private String getLineTwo(final Connection connection) {

		if (connection == null) {
			return "";
		}

		final StringBuilder sb = new StringBuilder(TIME_FORMAT.format(connection.getStartTime()));
		sb.append(' ');
		sb.append(connection.getStartStation());
		sb.append(" \u2794 ");
		sb.append(TIME_FORMAT.format(connection.getEndTime()));
		sb.append(' ');
		sb.append(connection.getEndStation());

		return sb.toString();

	}

	private EditText getInputText() {
		for (int i = 0; i < getChildCount(); i++) {
			final View child = getChildAt(i);
			if (child instanceof EditText) {
				return ((EditText) child);
			}
		}
		return null;
	}

	private Drawable getMSelectionDivider() {
		final Object value = getFieldValue("mSelectionDivider");
		return (Drawable) value;
	}

	private SparseArray<String> getMSelectorIndexToStringCache() {
		final Object value = getFieldValue("mSelectorIndexToStringCache");
		return (SparseArray<String>) value;
	}

	private int[] getMSelectorIndices() {
		final Object value = getFieldValue("mSelectorIndices");
		if (value != null) {
			return (int[]) value;
		}
		return new int[0];
	}

	private int getMCurrentScrollOffset() {
		final Object value = getFieldValue("mCurrentScrollOffset");
		if (value != null) {
			return (Integer) value;
		}
		return Integer.MIN_VALUE;
	}

	private Runnable getMBeginSoftInputOnLongPressCommand() {
		final Object value = getFieldValue("mBeginSoftInputOnLongPressCommand");
		if (value != null) {
			return (Runnable) value;
		}
		return null;
	}

	private Object getFieldValue(final String fieldName) {
		try {
			final Field f = NumberPicker.class.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(this);
		} catch (final NoSuchFieldException e) {
			e.printStackTrace();
		} catch (final IllegalAccessException e) {
			e.printStackTrace();
		} catch (final IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Connection> getConnections() {
		return connections;
	}

	public void setConnections(final List<Connection> connections) {
		this.connections = connections;
	}

	private Connection getConnection(final String name) {
		if (connections != null) {
			for (final Connection connection : connections) {
				if (connection.getName().equals(name)) {
					return connection;
				}
			}
		}
		return null;
	}

	private void removeBeginSoftInputCommand() {
		final Runnable mBeginSoftInputOnLongPressCommand = getMBeginSoftInputOnLongPressCommand();
		if (mBeginSoftInputOnLongPressCommand != null) {
			removeCallbacks(mBeginSoftInputOnLongPressCommand);
		}
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent event) {
		final boolean ret = super.onInterceptTouchEvent(event);
		if (ret) {
			removeBeginSoftInputCommand();
		}
		return ret;
	}

}
