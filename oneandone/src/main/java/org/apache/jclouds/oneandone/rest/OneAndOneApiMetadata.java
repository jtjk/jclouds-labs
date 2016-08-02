/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.jclouds.oneandone.rest;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import java.net.URI;
import java.util.Properties;
import org.apache.jclouds.oneandone.rest.config.OneAndOneHttpApiModule;
import org.jclouds.http.okhttp.config.OkHttpCommandExecutorServiceModule;
import org.jclouds.rest.internal.BaseHttpApiMetadata;

public class OneAndOneApiMetadata extends BaseHttpApiMetadata<OneAndOneApi> {

   @Override
   public Builder toBuilder() {
      return new Builder().fromApiMetadata(this);
   }

   public OneAndOneApiMetadata() {
      this(new Builder());
   }

   protected OneAndOneApiMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = BaseHttpApiMetadata.defaultProperties();
      return properties;
   }

   public static class Builder extends BaseHttpApiMetadata.Builder<OneAndOneApi, Builder> {

      protected Builder() {
         id("oneandone")
                 .name("OneAndOne REST API")
                 .identityName("API Username")
                 .documentation(URI.create("https://cloudpanel-api.1and1.com/documentation/1and1/v1/en/documentation.html"))
                 .defaultEndpoint("https://cloudpanel-api.1and1.com/v1")
                 .defaultProperties(OneAndOneApiMetadata.defaultProperties())
                 .defaultModules(ImmutableSet.<Class<? extends Module>>builder()
                         .add(OkHttpCommandExecutorServiceModule.class)
                         .add(OneAndOneHttpApiModule.class)
                         .build());
      }

      @Override
      public OneAndOneApiMetadata build() {
         return new OneAndOneApiMetadata(this);
      }

      @Override
      protected Builder self() {
         return this;
      }
   }
}
