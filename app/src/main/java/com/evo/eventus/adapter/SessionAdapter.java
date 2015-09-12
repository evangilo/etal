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
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.bumptech.glide.Glide;
import com.holocron.etal.R;
import com.evo.eventus.entity.Session;
import com.evo.eventus.entity.SessionType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SessionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private final TypedValue mTypedValue = new TypedValue();
  private int mBackground;

  private List<SectionItem<Session>> mContentList;
  private Set<SessionType> mHeaderList;

  private OnClickSessionListener mOnClickSessionListener;

  public interface OnClickSessionListener {
    void onItemSessionClick(Session session);
  }

  public SessionAdapter(Context context, OnClickSessionListener onClickSessionListener) {
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
    mBackground = mTypedValue.resourceId;
    mOnClickSessionListener = onClickSessionListener;
    mContentList = new ArrayList<>();
    mHeaderList = new HashSet<>();
  }

  public void add(Session session) {
    if (!mHeaderList.contains(session.getType())) {
      mHeaderList.add(session.getType());
      mContentList.add(new SectionItem<>(SectionItem.TYPE_HEADER, session));
    }
    mContentList.add(new SectionItem<>(SectionItem.TYPE_ITEM, session));
    notifyDataSetChanged();
  }

  public boolean isEmpty() {
    return mContentList.isEmpty();
  }

  public void clear() {
    mHeaderList.clear();
    mContentList.clear();
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    return mContentList.size();
  }


  @Override public int getItemViewType(int position) {
    return mContentList.get(position).getType();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == SectionItem.TYPE_ITEM) {
      return onCreateSessionItemViewHolder(parent, viewType);
    } else if (viewType == SectionItem.TYPE_HEADER) {
      return onCreateSessionHeaderViewHolder(parent, viewType);
    } else {
      throw new IllegalStateException("Illegal view type.");
    }
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Session session = mContentList.get(position).getItem();

    if (holder instanceof SessionItemViewHolder) {
      setSessionItemViewHolder((SessionItemViewHolder) holder, session);
    } else if (holder instanceof SessionHeaderViewHolder) {
      setSessionHeaderViewHolder((SessionHeaderViewHolder) holder, session);
    }
  }

  private void setSessionHeaderViewHolder(SessionHeaderViewHolder holder, final Session session) {
    SessionType sessionType = session.getType();
      if (sessionType == SessionType.TALK) {
        holder.title.setText(R.string.session_item_header_talks);
      } else if (sessionType == SessionType.ROUNDTABLE) {
        holder.title.setText(R.string.session_item_header_roundtables);
      } else {
        holder.title.setText(R.string.session_item_header_workshops);
      }
  }

  private void setSessionItemViewHolder(SessionItemViewHolder holder, final Session session) {
    holder.title.setText(session.getTitle());
    holder.description.setText(session.getDescription());

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mOnClickSessionListener.onItemSessionClick(session);
      }
    });

    setBitmap(holder, session.getSpeaker().getThumbnailUrl());
  }

  private int validPosition(int position) {
    position -= mHeaderList.size();

    if (position < 0) {
      return 0;
    }
    return position;
  }

  private SessionItemViewHolder onCreateSessionItemViewHolder(ViewGroup parent, int position) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_list_sessions, parent, false);
    view.setBackgroundResource(mBackground);
    return new SessionItemViewHolder(view);
  }

  private SessionHeaderViewHolder onCreateSessionHeaderViewHolder(ViewGroup parent, int position) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.item_header_session, parent, false);
    return new SessionHeaderViewHolder(view);
  }

  private void setBitmap(SessionItemViewHolder holder, String url) {
    Glide.with(holder.itemView.getContext())
        .load(url)
        .centerCrop()
        .crossFade()
        .error(R.drawable.user)
        .into(holder.avatar);
  }

  static class SessionItemViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvTitle) TextView title;
    @Bind(R.id.tvDescription) TextView description;
    @Bind(R.id.ivSessionAvatar) ImageView avatar;

    SessionItemViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }

  static class SessionHeaderViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvHeaderItemSession) TextView title;

    private SessionHeaderViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}