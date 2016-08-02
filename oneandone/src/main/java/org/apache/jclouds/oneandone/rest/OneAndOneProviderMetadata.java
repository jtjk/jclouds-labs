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

import com.google.auto.service.AutoService;
import java.net.URI;
import java.util.Properties;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_MAX_PERIOD;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_PERIOD;
import static org.apache.jclouds.oneandone.rest.config.OneAndOneProperties.POLL_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_CONNECTION_TIMEOUT;
import static org.jclouds.Constants.PROPERTY_SO_TIMEOUT;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.internal.BaseProviderMetadata;

@AutoService(ProviderMetadata.class)
public class OneAndOneProviderMetadata extends BaseProviderMetadata {

   public static Builder builder() {
      return new Builder();
   }

   @Override
   public Builder toBuilder() {
      return builder().fromProviderMetadata(this);
   }

   public OneAndOneProviderMetadata() {
      super(builder());
   }

   public OneAndOneProviderMetadata(Builder builder) {
      super(builder);
   }

   public static Properties defaultProperties() {
      Properties properties = OneAndOneApiMetadata.defaultProperties();
      long defaultTimeout = 60l * 60l; // 1 hour
      properties.put(POLL_TIMEOUT, defaultTimeout);
      properties.put(POLL_PERIOD, 1l);
      properties.put(POLL_MAX_PERIOD, 1l * 9l);
      properties.put(PROPERTY_SO_TIMEOUT, 60000 * 5);
      properties.put(PROPERTY_CONNECTION_TIMEOUT, 60000 * 5);

      return properties;
   }

   public static class Builder extends BaseProviderMetadata.Builder {

      protected Builder() {
         id("oneandone")
                 .name("OneAndOne REST Compute")
                 .apiMetadata(new OneAndOneApiMetadata())
                 .homepage(URI.create("https://cloudpanel-api.1and1.com"))
                 .console(URI.create("https://account.1and1.com"))
                 .endpoint("https://cloudpanel-api.1and1.com/v1")
                 .defaultProperties(OneAndOneProviderMetadata.defaultProperties());
      }

      @Override
      public OneAndOneProviderMetadata build() {
         return new OneAndOneProviderMetadata(this);
      }

      @Override
      public Builder fromProviderMetadata(ProviderMetadata in) {
         super.fromProviderMetadata(in);
         return this;
      }
   }
}
