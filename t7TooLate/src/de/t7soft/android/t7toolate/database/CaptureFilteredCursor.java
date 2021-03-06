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
package de.t7soft.android.t7toolate.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import de.t7soft.android.t7toolate.model.AbstractFilter;

/*protected*/class CaptureFilteredCursor extends CursorWrapper {

	private final int[] filterMap;
	private int mPos = -1;
	private int mCount = 0;

	public CaptureFilteredCursor(final Cursor cursor, final AbstractFilter filter) {

		super(cursor);

		final int count = super.getCount();
		filterMap = new int[count];
		int filteredCount = 0;
		if ((filter != null) && filter.isActive()) {
			for (int i = 0; i < count; i++) {
				super.moveToPosition(i);
				if (filter.filter(getWrappedCursor())) {
					filterMap[filteredCount] = i;
					filteredCount++;
				}
			}
			moveToFirst();
		} else {
			for (int i = 0; i < count; i++) {
				filterMap[i] = i;
			}
			filteredCount = count;
		}
		mCount = filteredCount;

	}

	@Override
	public int getCount() {
		return this.mCount;
	}

	@Override
	public boolean moveToPosition(final int position) {

		final int count = getCount();
		if (position >= count) {
			mPos = count;
			return false;
		}

		if (position < 0) {
			mPos = -1;
			return false;
		}

		final int realPosition = filterMap[position];
		final boolean moved = realPosition == -1 ? true : super.moveToPosition(realPosition);
		if (moved) {
			mPos = position;
		} else {
			mPos = -1;
		}

		return moved;

	}

	@Override
	public final boolean move(final int offset) {
		return moveToPosition(mPos + offset);
	}

	@Override
	public final boolean moveToFirst() {
		return moveToPosition(0);
	}

	@Override
	public final boolean moveToLast() {
		return moveToPosition(getCount() - 1);
	}

	@Override
	public final boolean moveToNext() {
		return moveToPosition(mPos + 1);
	}

	@Override
	public final boolean moveToPrevious() {
		return moveToPosition(mPos - 1);
	}

	@Override
	public final boolean isFirst() {
		return (mPos == 0) && (getCount() != 0);
	}

	@Override
	public final boolean isLast() {
		final int cnt = getCount();
		return (mPos == (cnt - 1)) && (cnt != 0);
	}

	@Override
	public final boolean isBeforeFirst() {
		if (getCount() == 0) {
			return true;
		}
		return mPos == -1;
	}

	@Override
	public final boolean isAfterLast() {
		if (getCount() == 0) {
			return true;
		}
		return mPos == getCount();
	}

	@Override
	public int getPosition() {
		return mPos;
	}
}
