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
package org.jclouds.azurecomputearm.binders;

import static com.google.common.base.Throwables.propagate;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;
import org.jclouds.azurecomputearm.domain.CreateProfileParams;
import org.jclouds.azurecomputearm.domain.UpdateProfileParams;

public final class ProfileParamsToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(final R request, final Object input) {

      try {
         final XMLBuilder bld = XMLBuilder.create("Profile", "http://schemas.microsoft.com/windowsazure");
         if (input instanceof CreateProfileParams) {
            final CreateProfileParams params = CreateProfileParams.class.cast(input);
            bld.e("DomainName").t(params.domain()).up();
            bld.e("Name").t(params.name()).up().up();
            return (R) request.toBuilder().payload(bld.up().asString()).build();
         } else {
            final UpdateProfileParams params = UpdateProfileParams.class.cast(input);
            bld.e("Status").t(params.status().getValue()).up();
            bld.e("StatusDetails").e("EnabledVersion").t("1").up().up().up();
            return (R) request.toBuilder().payload(bld.up().asString()).build();
         }
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
