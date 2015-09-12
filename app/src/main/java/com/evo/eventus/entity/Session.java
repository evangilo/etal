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

package com.evo.eventus.entity;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import org.parceler.Parcel;
import org.parceler.Transient;

@Parcel(Parcel.Serialization.BEAN)
public class Session {

  @SerializedName("id")
  private long id;

  @SerializedName("sala")
  private Room room;

  @SerializedName("palestrante")
  private Speaker speaker;

  @SerializedName("titulo")
  private String title;

  @SerializedName("descricao")
  private String description;

  @SerializedName("pre_requisitos")
  private String preconditions;

  @SerializedName("tipo")
  @Transient
  private SessionType type;

  @SerializedName("data_e_hora")
  private List<SessionDate> dateList;

  public Session() {

  }

  public Session(long id, Room room, Speaker speaker, String title, String description,
      String preconditions, SessionType type,
      List<SessionDate> dateList) {
    this.id = id;
    this.room = room;
    this.speaker = speaker;
    this.title = title;
    this.description = description;
    this.preconditions = preconditions;
    this.type = type;
    this.dateList = dateList;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public Speaker getSpeaker() {
    return speaker;
  }

  public void setSpeaker(Speaker speaker) {
    this.speaker = speaker;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getPreconditions() {
    return preconditions;
  }

  public void setPreconditions(String preconditions) {
    this.preconditions = preconditions;
  }

  public SessionType getType() {
    return type;
  }

  public void setType(SessionType type) {
    this.type = type;
  }

  public List<SessionDate> getDateList() {
    return dateList;
  }

  public void setDateList(List<SessionDate> dateList) {
    this.dateList = dateList;
  }
}
