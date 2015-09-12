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
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.evo.eventus.entity.SessionType;
import com.evo.eventus.sync.serializer.SessionTypeSerializer;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import retrofit.Endpoint;
import retrofit.Endpoints;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

public class RestAdapterFactory {

  private static final String BASE_URL = "http://eventos.ifrn.edu.br/etal/minicursos/api";
  private static long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB

  private static Endpoint getEndpoint() {
    return Endpoints.newFixedEndpoint(BASE_URL);
  }

  private static Gson getGsonBuilder() {
    return new GsonBuilder()
        .registerTypeAdapter(SessionType.class, new SessionTypeSerializer())
        .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        .create();
  }

  public static OkHttpClient getOkHttpClient(Context context) {
    Cache cache = null;
    try {
      cache = new Cache(new File(context.getCacheDir(), "http"), SIZE_OF_CACHE);
    } catch (IOException e) {
      Log.e(RestAdapterFactory.class.getSimpleName(), "Could not create cache.");
    }
    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setCache(cache);
    okHttpClient.setConnectTimeout(30, TimeUnit.SECONDS);
    okHttpClient.setReadTimeout(30, TimeUnit.SECONDS);
    okHttpClient.networkInterceptors().add(new ClientInterceptor(context));
    return okHttpClient;
  }

  private static RestAdapter getRestAdapter(final Context context) {
    Executor executor = Executors.newCachedThreadPool();

    return new RestAdapter.Builder()
        .setConverter(new GsonConverter(getGsonBuilder()))
        .setClient(new OkClient(getOkHttpClient(context)))
        .setEndpoint(getEndpoint())
        .setExecutors(executor, executor)
        //.setLogLevel(RestAdapter.LogLevel.FULL)
        .build();
  }

  public static RestApi getRestApi(Context context) {
    return getRestAdapter(context).create(RestApi.class);
  }
}
