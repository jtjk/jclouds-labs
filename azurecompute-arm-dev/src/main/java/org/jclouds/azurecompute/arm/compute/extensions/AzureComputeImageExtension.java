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
package org.jclouds.azurecompute.arm.compute.extensions;

import com.google.common.base.Predicate;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jclouds.Constants;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.functions.ImageReferenceToImage;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.ResourceDefinition;
import org.jclouds.compute.domain.CloneImageTemplate;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.ImageTemplate;
import org.jclouds.compute.domain.ImageTemplateBuilder;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.util.Predicates2;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;

import static com.google.inject.internal.util.$Preconditions.checkState;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;

public class AzureComputeImageExtension implements ImageExtension {
   private final AzureComputeApi api;
   private final ListeningExecutorService userExecutor;
   private final Predicate<String> imageAvailablePredicate;
   private final String group;
   private final ImageReferenceToImage imageReferenceToImage;
   public static final String CONTAINER_NAME = "vhdsnew";

   @Inject
   AzureComputeImageExtension(AzureComputeApi api,
                              @Named(TIMEOUT_IMAGE_AVAILABLE) Predicate<String> imageAvailablePredicate,
                              @Named(Constants.PROPERTY_USER_THREADS) ListeningExecutorService userExecutor,
                              @Named("azureGroupId") String group,
                              ImageReferenceToImage imageReferenceToImage) {
      this.userExecutor = userExecutor;
      this.imageAvailablePredicate = imageAvailablePredicate;
      this.group = group;
      this.imageReferenceToImage = imageReferenceToImage;
      this.api = api;
   }

   @Override
   public ImageTemplate buildImageTemplateFromNode(String name, String id) {
      name = name.toLowerCase();
      return new ImageTemplateBuilder.CloneImageTemplateBuilder().nodeId(id).name(name).build();
   }

   @Override
   public ListenableFuture<Image> createImage(ImageTemplate template) {
      checkState(template instanceof CloneImageTemplate, "AzureCompute arm only supports creating images through cloning.");
      final CloneImageTemplate cloneTemplate = (CloneImageTemplate) template;
      String id = cloneTemplate.getSourceNodeId();
      final String storageAccountName = id.replaceAll("[^A-Za-z0-9 ]", "") + "storage";

      boolean generalized = false;
      while (!generalized) {
         try {
            api.getVirtualMachineApi(group).generalize(id);
         } catch (Exception e) {
            System.out.println("error" + e.getMessage());
         } finally {
            generalized = true;
         }
      }

      final String[] disks = new String[2];
      URI uri = api.getVirtualMachineApi(group).capture(id, cloneTemplate.getName(), CONTAINER_NAME);
      if (uri != null){
         boolean jobDone = Predicates2.retry(new Predicate<URI>() {
            @Override public boolean apply(URI uri) {
               try {
                  List<ResourceDefinition> definitions = api.getJobApi().captureStatus(uri);
                  if (definitions != null) {
                     for (ResourceDefinition definition : definitions) {
                        LinkedTreeMap<String, String> properties = (LinkedTreeMap<String, String>) definition.properties();
                        Object storageObject = properties.get("storageProfile");
                        LinkedTreeMap<String, String> properties2 = (LinkedTreeMap<String, String>) storageObject;
                        Object osDiskObject = properties2.get("osDisk");
                        LinkedTreeMap<String, String> osProperties = (LinkedTreeMap<String, String>) osDiskObject;
                        Object dataDisksObject = properties2.get("dataDisks");
                        ArrayList<Object> dataProperties = (ArrayList<Object>) dataDisksObject;
                        LinkedTreeMap<String, String> datadiskObject = (LinkedTreeMap<String, String>) dataProperties.get(0);

                        disks[0] = osProperties.get("name");
                        disks[1] = datadiskObject.get("name");

                     }
                     System.out.println("return true");
                     return true;
                  }
                  System.out.println("return false");
                  return false;
               }
               catch (Exception e) {
                  System.out.println("errorown"+e.getMessage());
                  return false;
               }
            }
         }, 15 * 1000 /* 15 second timeout */).apply(uri);

      }

      final ImageReference ref = ImageReference.create("custom" + group, "custom" + storageAccountName, disks[0], disks[1]);

      return userExecutor.submit(new Callable<Image>() {
         @Override
         public Image call() throws Exception {
               return imageReferenceToImage.apply(ref);
         }
      });

   }

   @Override
   public boolean deleteImage(String id) {
      return false;
   }
}
