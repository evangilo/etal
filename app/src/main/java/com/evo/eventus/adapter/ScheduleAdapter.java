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
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.holocron.etal.R;
import com.evo.eventus.entity.Session;
import com.evo.eventus.entity.SessionDate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleAdapter
    extends RecyclerView.Adapter<ScheduleAdapter.MyScheduleViewHolder> {

  private final TypedValue mTypedValue = new TypedValue();
  private int mBackground;

  private List<Session> mSessions;
  private Date mDay;
  private OnClickStarredListener mOnClickStarredListener;

  public interface OnClickStarredListener {
    void onItemStarredClcik(Session session);
  }

  public ScheduleAdapter(Context context, OnClickStarredListener onClickStarredListener, Date day) {
    this(context, new ArrayList<Session>(), onClickStarredListener, day);
  }

  public ScheduleAdapter(Context context, List<Session> sessions,
      OnClickStarredListener onClickStarredListener, Date day) {
    context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
    mBackground = mTypedValue.resourceId;
    mOnClickStarredListener = onClickStarredListener;
    mSessions = sessions;
    mDay = day;
  }

  public void addAll(List<Session> sessions) {
    mSessions.clear();
    mSessions.addAll(sessions);
    notifyDataSetChanged();
  }

  public void add(Session session) {
    mSessions.add(session);
    notifyDataSetChanged();
  }

  public void clear() {
    mSessions.clear();
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    return mSessions.size();
  }

  public boolean isEmpty() {
    return mSessions.isEmpty();
  }

  @Override public MyScheduleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.list_item_schedule, parent, false);
    view.setBackgroundResource(mBackground);
    return new MyScheduleViewHolder(view);
  }

  @Override public void onBindViewHolder(MyScheduleViewHolder holder, int position) {
    final Session session = mSessions.get(position);

    holder.title.setText(session.getTitle());
    holder.subtitle.setText(formatFirstDateAndLocal(session));

    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        mOnClickStarredListener.onItemStarredClcik(session);
      }
    });
  }

  private String formatFirstDateAndLocal(Session session) {
    SessionDate sessionDate = getSessionDateStart(session.getDateList());

    if (sessionDate == null) return "";

    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
    String startTime = dateFormat.format(sessionDate.getStart());
    String endTime = dateFormat.format(sessionDate.getEnd());
    return String.format("%s - %s (%s)", startTime, endTime, session.getRoom().getName());
  }

  private SessionDate getSessionDateStart(List<SessionDate> sessionDates) {
    for (SessionDate sessionDate: sessionDates) {
      if (sessionDate.getStart().getDay() == mDay.getDay()) {
        return sessionDate;
      }
    }
    return null;
  }

  static class MyScheduleViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tvTitle) TextView title;
    @Bind(R.id.tvSubtitle) TextView subtitle;

    MyScheduleViewHolder(View view) {
      super(view);
      ButterKnife.bind(this, view);
    }
  }
}
