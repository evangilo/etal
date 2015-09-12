/*
 * Copyright (C) 2015 Evangilo Morais
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.evo.eventus.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private Drawable mDivider;

  public DividerItemDecoration(Context context, AttributeSet attrs) {
    final TypedArray typedArray =
        context.obtainStyledAttributes(attrs, new int[] { android.R.attr.listDivider });
    mDivider = typedArray.getDrawable(0);
    typedArray.recycle();
  }

  @Override
  public void getItemOffsets(Rect rect, View view, RecyclerView parent, RecyclerView.State state) {
    super.getItemOffsets(rect, view, parent, state);

    if (parent.getChildLayoutPosition(view) < 1) return;

    if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
      rect.top = mDivider.getIntrinsicHeight();
    } else {
      rect.left = mDivider.getIntrinsicWidth();
    }
  }

  @Override
  public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    if (getOrientation(parent) == LinearLayoutManager.VERTICAL) {
      drawVertical(canvas, parent);
    } else {
      drawHorizontal(canvas, parent);
    }
  }

  private void drawVertical(Canvas canvas, RecyclerView parent) {
    final int left = parent.getPaddingLeft();
    final int right = parent.getWidth() - parent.getPaddingRight();
    final int childCount = parent.getChildCount();

    for (int i = 1; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int size = mDivider.getIntrinsicHeight();
      final int top = child.getTop() - params.topMargin;
      final int bottom = top + size;
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
  }

  private void drawHorizontal(Canvas canvas, RecyclerView parent) {
    final int top = parent.getPaddingTop();
    final int bottom = parent.getHeight() - parent.getPaddingBottom();
    final int childCount = parent.getChildCount();

    for (int i = 1; i < childCount; i++) {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
      final int size = mDivider.getIntrinsicWidth();
      final int left = child.getLeft() - params.leftMargin;
      final int right = left + size;
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(canvas);
    }
  }

  private int getOrientation(RecyclerView parent) {
    if (parent.getLayoutManager() instanceof LinearLayoutManager) {
      LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
      return layoutManager.getOrientation();
    } else {
      throw new IllegalStateException(
          "DividerItemDecoration can only be used with a LinearLayoutManager.");
    }
  }
}
