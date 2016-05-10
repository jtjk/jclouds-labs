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
package org.jclouds.azurecomputearm.xml;

import java.util.List;

import org.jclouds.azurecomputearm.domain.StorageService;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;

public class ListStorageServicesHandler extends ParseSax.HandlerForGeneratedRequestWithResult<List<StorageService>> {

   private boolean inStorageService;

   private final StorageServiceHandler storageServiceHandler;

   private final ImmutableList.Builder<StorageService> storageAccounts = ImmutableList.builder();

   @Inject
   ListStorageServicesHandler(final StorageServiceHandler storageServiceHandler) {
      this.storageServiceHandler = storageServiceHandler;
   }

   @Override
   public List<StorageService> getResult() {
      return storageAccounts.build();
   }

   @Override
   public void startElement(final String url, final String name, final String qName, final Attributes attributes) {
      if ("StorageService".equals(qName)) {
         inStorageService = true;
      }
      if (inStorageService) {
         storageServiceHandler.startElement(url, name, qName, attributes);
      }
   }

   @Override
   public void endElement(final String uri, final String name, final String qName) {
      if ("StorageService".equals(qName)) {
         inStorageService = false;
         storageAccounts.add(storageServiceHandler.getResult());
      } else if (inStorageService) {
         storageServiceHandler.endElement(uri, name, qName);
      }
   }

   @Override
   public void characters(final char ch[], final int start, final int length) {
      if (inStorageService) {
         storageServiceHandler.characters(ch, start, length);
      }
   }
}
