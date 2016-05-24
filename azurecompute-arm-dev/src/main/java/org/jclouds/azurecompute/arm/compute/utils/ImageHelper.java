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
package org.jclouds.azurecompute.arm.compute.utils;

import com.google.common.collect.Lists;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.Offer;
import org.jclouds.azurecompute.arm.domain.SKU;
import org.jclouds.azurecompute.arm.features.OSImageApi;

import java.util.List;

public class ImageHelper {

   private final AzureComputeApi api;
   private final String location;

   public ImageHelper(AzureComputeApi api, String location) {
      this.api = api;
      this.location = location;
   }

   private void getImagesFromPublisher(String publisherName, List<ImageReference> osImagesRef) {
      OSImageApi osImageApi = api.getOSImageApi(location);
      Iterable<Offer> offerList = osImageApi.listOffers(publisherName);
      for (Offer offer : offerList) {
         Iterable<SKU> skuList = osImageApi.listSKUs(publisherName, offer.name());
         for (SKU sku : skuList) {
            osImagesRef.add(ImageReference.create(publisherName, offer.name(), sku.name(), null));
         }
      }
   }

   public Iterable<ImageReference> listImages() {
      final List<ImageReference> osImages = Lists.newArrayList();
      getImagesFromPublisher("Microsoft.WindowsAzure.Compute", osImages);
      getImagesFromPublisher("MicrosoftWindowsServer", osImages);
      getImagesFromPublisher("Canonical", osImages);
      return osImages;
   }

   public ImageReference getImage(final String id) {
      Iterable<ImageReference> images = listImages();
      for (ImageReference image : images) {
         if (id.contains(image.offer()) && id.contains(image.sku())) {
            return image;
         }
      }
      return null;
   }

}
