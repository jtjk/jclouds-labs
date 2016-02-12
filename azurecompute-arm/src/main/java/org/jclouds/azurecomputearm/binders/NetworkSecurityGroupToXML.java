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

import org.jclouds.azurecomputearm.domain.NetworkSecurityGroup;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.Binder;

import com.jamesmurty.utils.XMLBuilder;

public class NetworkSecurityGroupToXML implements Binder {

   @Override
   @SuppressWarnings("unchecked")
   public <R extends HttpRequest> R bindToRequest(R request, Object input) {
      NetworkSecurityGroup networkSecurityGroup = NetworkSecurityGroup.class.cast(input);
      try {
         XMLBuilder builder = XMLBuilder.create("NetworkSecurityGroup", "http://schemas.microsoft.com/windowsazure")
                 .e("Name").t(networkSecurityGroup.name()).up();
         if (networkSecurityGroup.label() != null) {
            builder.e("Label").t(networkSecurityGroup.label()).up();
         }
         if (networkSecurityGroup.location() != null) {
            builder.e("Location").t(networkSecurityGroup.location()).up();
         }
         return (R) request.toBuilder().payload(builder.asString()).build();
      } catch (Exception e) {
         throw propagate(e);
      }
   }
}
