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

package com.evo.eventus.sync;

import android.content.Context;
import com.evo.eventus.utils.NetworkStatus;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;

public class ClientInterceptor implements Interceptor {

  private Context mContext;

  public ClientInterceptor(Context context) {
    mContext = context;
  }

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    // Add Cache Control only for GET methods
    if (request.method().equals("GET")) {
      if (NetworkStatus.isAvailable(mContext)) {
        request.newBuilder().header("Cache-Control", "only-if-cached").build(); // 1 day
      } else {
        request.newBuilder().header("Cache-Control", "public, max-stale=2419200").build();// 4 weeks stale
      }
    }
    Response response = chain.proceed(request);
    // Re-write response CC header to force use of cache
    return response.newBuilder()
        .header("Cache-Control", "public, max-age=1800") // 10 minutes
        .build();
  }
}
