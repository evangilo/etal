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

package com.evo.eventus.adapter;

import android.content.Context;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.holocron.etal.R;
import com.evo.eventus.entity.filters.SessionItemFilter;
import java.util.List;

public class SessionFilterAdapter extends RecyclerView.Adapter<SessionFilterAdapter.SessionFilterViewHolder> {

  private List<SessionItemFilter> mFilters;
  private final TypedValue mTypedValue = new TypedValue();
  private int mBackground;
  private OnSessionFilterListener mOnSessionFilterListener;

  public interface OnSessionFilterListener {
    public void onChecked(SessionItemFilter filter);
  }

  public SessionFilterAdapter(Context context, List<SessionItemFilter> filters, OnSessionFilterListener listener) {
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
    mBackground = mTypedValue.resourceId;
    mFilters = filters;
    mOnSessionFilterListener = listener;
  }

  @Override public SessionFilterViewHolder onCreateViewHolder(ViewGroup parent, int i) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_filter_session,
        parent, false);
    view.setBackgroundResource(mBackground);
    return new SessionFilterViewHolder(view);
  }

  @Override public void onBindViewHolder(final SessionFilterViewHolder holder, int position) {
    final SessionItemFilter item = mFilters.get(position);
    holder.filter.setText(item.getDisplayName());
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        boolean checked = !holder.filter.isChecked();
        item.setChecked(checked);
        holder.filter.setChecked(checked);
        mOnSessionFilterListener.onChecked(item);
      }
    });
  }

  @Override public int getItemCount() {
    return mFilters.size();
  }

  static class SessionFilterViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.cvFilter) AppCompatCheckedTextView filter;

    SessionFilterViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

}
