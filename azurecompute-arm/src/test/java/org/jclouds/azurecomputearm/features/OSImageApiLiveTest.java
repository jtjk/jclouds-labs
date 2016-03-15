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
package org.jclouds.azurecomputearm.features;

import org.jclouds.azurecomputearm.domain.Location;
import org.jclouds.azurecomputearm.domain.Offer;
import org.jclouds.azurecomputearm.domain.Publisher;
import org.jclouds.azurecomputearm.domain.SKU;
import org.jclouds.azurecomputearm.internal.AbstractAzureComputeApiLiveTest;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

@Test(groups = "live", testName = "OSImageApiLiveTest")
public class OSImageApiLiveTest extends AbstractAzureComputeApiLiveTest {

   @Test
   public void testList() {
      Iterable<Publisher> list = api().listPublishers();
      for (Publisher publisher : list) {
         Iterable<Offer> offerList = api().listOffers(publisher.name());
         for (Offer offer : offerList) {
            Iterable<SKU> skuList = api().listSKUs(publisher.name(), offer.name());
            for (SKU sku : skuList) {
               System.out.println(sku.id());
               System.out.println(sku.name());
               System.out.println(sku.location());
            }
         }

      }
   }

   @Test
   public void testList2() {
      Iterable<Offer> offerList = api().listOffers("MicrosoftWindowsServer");
      for (Offer offer : offerList) {
         Iterable<SKU> skuList = api().listSKUs("MicrosoftWindowsServer", offer.name());
         for (SKU sku : skuList) {
            Iterable<SKU> versions = api().listVersions("MicrosoftWindowsServer", offer.name(), sku.name());
            for (SKU version : versions) {
               System.out.println(version.id());
               System.out.println(version.name());
               System.out.println(version.location());
            }
         }
      }
   }

   @Test
   public void testListCanonicalUbuntu() {
      Iterable<Offer> offerList = api().listOffers("canonical");
      for (Offer offer : offerList) {
         Iterable<SKU> skuList = api().listSKUs("canonical", offer.name());
         for (SKU sku : skuList) {
            Iterable<SKU> versions = api().listVersions("canonical", offer.name(), sku.name());
            for (SKU version : versions) {
               System.out.println(version.id());
               System.out.println(version.name());
               System.out.println(version.location());
            }
         }
      }
   }

   protected String getSubscriptionId() {
      String subscriptionId = null;
      if (System.getProperties().containsKey("test.azurecompute-arm.subscriptionid"))
         subscriptionId = System.getProperty("test.azurecompute-arm.subscriptionid");
      assertNotNull(subscriptionId);
      return subscriptionId;
   }

   private OSImageApi api() {
      return api.getOSImageApi(getSubscriptionId(), "eastus");
   }
}
