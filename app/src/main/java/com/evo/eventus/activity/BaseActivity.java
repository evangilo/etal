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

import android.content.Intent;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.holocron.etal.R;

public class BaseActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener {

  @Nullable @Bind(R.id.toolbar) Toolbar mToolbar;
  @Nullable @Bind(R.id.drawer) DrawerLayout mDrawerLayout;
  @Nullable @Bind(R.id.nav_view) NavigationView mNavigationView;
  public boolean isArrowButtom = false;

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    ButterKnife.bind(this);
    setupToolbar();
    setupNavigation();
  }

  private void setupToolbar() {
    if (mToolbar != null) {
      setSupportActionBar(mToolbar);
    }
  }

  private void setupNavigation() {
    if (mNavigationView != null) {
      mNavigationView.setNavigationItemSelectedListener(this);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        if (isArrowButtom) {
          finish();
        } else {
          mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
    menuItem.setChecked(true);

    switch (menuItem.getItemId()) {
      case R.id.nav_my_schedule:
        startActivity(new Intent(this, ScheduleActivity.class));
      default:
        return false;
    }
  }

  protected void setNavigationViewCurrent(final int currentMenu) {
    if (mNavigationView != null) {
      Menu menu = mNavigationView.getMenu();
      menu.getItem(currentMenu).setChecked(true);
    }
  }

  protected void setDisplayHomeAsUpEnabled(boolean showHomeAsUp) {
    final ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setHomeAsUpIndicator(R.drawable.ic_menu);
      ab.setDisplayHomeAsUpEnabled(showHomeAsUp);
    }
  }

  protected void setDisplayArrowIndicator(boolean showArrow) {
    final ActionBar ab = getSupportActionBar();
    if (ab != null) {
      ab.setDisplayHomeAsUpEnabled(showArrow);
      isArrowButtom = true;
    }
  }
}
