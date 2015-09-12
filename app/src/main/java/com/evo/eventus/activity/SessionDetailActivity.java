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

package com.evo.eventus.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import com.bumptech.glide.Glide;
import com.holocron.etal.R;
import com.evo.eventus.service.SessionAlarmService;
import com.evo.eventus.entity.Session;
import com.evo.eventus.entity.SessionDate;
import com.evo.eventus.repository.ScheduleRepository;
import com.evo.eventus.repository.ScheduleRepositoryImpl;
import de.hdodenhof.circleimageview.CircleImageView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.parceler.Parcels;

public class SessionDetailActivity extends BaseActivity implements View.OnClickListener {

  public static final String SESSION_ARG = "session_arg";

  @Bind(R.id.fab) FloatingActionButton mFab;
  @Bind(R.id.tvSessionTitle) TextView mTitle;
  @Bind(R.id.tvDates) TextView mDates;
  @Bind(R.id.tvDescription) TextView mDescription;
  @Bind(R.id.tvPreconditions) TextView mPreconditions;
  @Bind(R.id.tvSpeakerName) TextView mSpeakerName;
  @Bind(R.id.tvSpeakerBio) TextView mSpeakerBio;
  @Bind(R.id.ivSpeakerThumbnail) CircleImageView mSpeakerThumbnail;

  private Session mSession;
  private ScheduleRepository mScheduleRepository;
  private SessionAlarmService mSessionAlarmService;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_detail);
    setDisplayArrowIndicator(true);
    mFab.setOnClickListener(this);
    mScheduleRepository = new ScheduleRepositoryImpl(this);
    mSessionAlarmService = new SessionAlarmService(this);
  }

  @Override protected void onStart() {
    super.onStart();
    setupSession();
    setImageStarredButton();
  }

  public void onClick(View view) {
    if (mScheduleRepository.isStarred(mSession)) {
      mScheduleRepository.remove(mSession);
      mSessionAlarmService.removeAlarm(mSession);
    } else {
      mScheduleRepository.add(mSession);
      mSessionAlarmService.addAlarm(mSession);
    }
    setImageStarredButton();
  }

  private void setImageStarredButton() {
    if (mScheduleRepository.isStarred(mSession)) {
      mFab.setImageResource(R.drawable.ic_done_white);
    } else {
      mFab.setImageResource(R.drawable.ic_event_white);
    }
  }

  private void setupSession() {
    mSession = Parcels.unwrap(getIntent().getParcelableExtra(SESSION_ARG));
    if (mSession != null) {
      mTitle.setText(mSession.getTitle());
      mDates.setText(getDates(mSession.getDateList()));
      mDescription.setText(mSession.getDescription());
      mPreconditions.setText(mSession.getPreconditions());
      mSpeakerName.setText(mSession.getSpeaker().getName());
      mSpeakerBio.setText(mSession.getSpeaker().getBio());

      Glide.with(this)
          .load(mSession.getSpeaker().getThumbnailUrl())
          .centerCrop()
          .crossFade()
          .error(R.drawable.user)
          .into(mSpeakerThumbnail);
    }
  }

  private String getDates(List<SessionDate> sessionDateList) {
    StringBuilder dateBuilder = new StringBuilder();
    String datePlusLocalFormat = "%s das %s às %s (%s)\n";

    for (SessionDate sessionDate : sessionDateList) {
      String date = dateFormated(sessionDate.getStart(), getString(R.string.session_date_format));
      String start = dateFormated(sessionDate.getStart(), getString(R.string.session_hour_format));
      String end = dateFormated(sessionDate.getEnd(), getString(R.string.session_hour_format));
      String local = mSession.getRoom().getName();

      dateBuilder.append(String.format(datePlusLocalFormat, date, start, end, local));
    }
    return dateBuilder.toString();
  }

  private String dateFormated(Date date, String format) {
    final SimpleDateFormat dateFormat = new SimpleDateFormat(format);
    return dateFormat.format(date);
  }
}
