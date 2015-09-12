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

package com.evo.eventus.entity.filters;

public class SessionItemFilter {

  private String mDisplayName;
  private boolean mChecked;
  private SessionFilter mSessionFilter;

  public SessionItemFilter(String displayName, SessionFilter sessionFilter) {
    mDisplayName = displayName;
    mSessionFilter = sessionFilter;
    mChecked = false;
  }

  public String getDisplayName() {
    return mDisplayName;
  }

  public SessionFilter getSessionFilter() {
    return mSessionFilter;
  }

  public boolean isChecked() {
    return mChecked;
  }

  public void setChecked(boolean mChecked) {
    this.mChecked = mChecked;
  }
}
