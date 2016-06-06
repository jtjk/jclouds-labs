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
package org.jclouds.azurecompute.arm.compute.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_POLL_INITIAL_PERIOD;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_POLL_MAX_PERIOD;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.OPERATION_TIMEOUT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TCP_RULE_FORMAT;
import static org.jclouds.azurecompute.arm.config.AzureComputeProperties.TCP_RULE_REGEXP;
import static org.jclouds.compute.config.ComputeServiceProperties.TIMEOUT_IMAGE_AVAILABLE;
import static org.jclouds.util.Predicates2.retry;

import javax.inject.Named;
import javax.inject.Singleton;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.inject.Provides;
import org.jclouds.azurecompute.arm.AzureComputeApi;
import org.jclouds.azurecompute.arm.compute.AzureComputeServiceAdapter;
import org.jclouds.azurecompute.arm.compute.extensions.AzureComputeImageExtension;
import org.jclouds.azurecompute.arm.compute.functions.ImageReferenceToImage;
import org.jclouds.azurecompute.arm.compute.functions.DeploymentToNodeMetadata;
import org.jclouds.azurecompute.arm.compute.functions.VMSizeToHardware;
import org.jclouds.azurecompute.arm.compute.functions.LocationToLocation;
import org.jclouds.azurecompute.arm.compute.options.AzureComputeArmTemplateOptions;
import org.jclouds.azurecompute.arm.domain.StorageService;
import org.jclouds.azurecompute.arm.domain.VMDeployment;
import org.jclouds.azurecompute.arm.domain.VMSize;
import org.jclouds.azurecompute.arm.domain.ImageReference;
import org.jclouds.azurecompute.arm.domain.Location;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;

import com.google.common.base.Function;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import org.jclouds.compute.extensions.ImageExtension;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants;

public class AzureComputeServiceContextModule
        extends ComputeServiceAdapterContextModule<VMDeployment, VMSize, ImageReference, Location> {

   @Override
   protected void configure() {
      super.configure();

      bind(new TypeLiteral<ComputeServiceAdapter<VMDeployment, VMSize, ImageReference, Location>>() {
      }).to(AzureComputeServiceAdapter.class);
      bind(new TypeLiteral<Function<ImageReference, org.jclouds.compute.domain.Image>>() {
      }).to(ImageReferenceToImage.class);
      bind(new TypeLiteral<Function<VMSize, Hardware>>() {
      }).to(VMSizeToHardware.class);
      bind(new TypeLiteral<Function<VMDeployment, NodeMetadata>>() {
      }).to(DeploymentToNodeMetadata.class);

      //bind(PrioritizeCredentialsFromTemplate.class).to(UseNodeCredentialsButOverrideFromTemplate.class);
      bind(new TypeLiteral<Function<Location, org.jclouds.domain.Location>>() {
      }).to(LocationToLocation.class);

      bind(TemplateOptions.class).to(AzureComputeArmTemplateOptions.class);

      //bind(CreateNodesInGroupThenAddToSet.class).to(GetOrCreateStorageServiceAndVirtualNetworkThenCreateNodes.class);

      // to have the compute service adapter override default locations
      install(new LocationsFromComputeServiceAdapterModule<VMDeployment, VMSize, ImageReference, Location>() {
      });

      bind(new TypeLiteral<ImageExtension>() {
      }).to(AzureComputeImageExtension.class);
   }

   @Singleton
   public static class AzureComputeConstants {

      @Named(OPERATION_TIMEOUT)
      @Inject
      private String operationTimeoutProperty;

      @Named(OPERATION_POLL_INITIAL_PERIOD)
      @Inject
      private String operationPollInitialPeriodProperty;

      @Named(OPERATION_POLL_MAX_PERIOD)
      @Inject
      private String operationPollMaxPeriodProperty;

      @Named(TCP_RULE_FORMAT)
      @Inject
      private String tcpRuleFormatProperty;

      @Named(TCP_RULE_REGEXP)
      @Inject
      private String tcpRuleRegexpProperty;

      public Long operationTimeout() {
         return Long.parseLong(operationTimeoutProperty);
      }

      public Integer operationPollInitialPeriod() {
         return Integer.parseInt(operationPollInitialPeriodProperty);
      }

      public Integer operationPollMaxPeriod() {
         return Integer.parseInt(operationPollMaxPeriodProperty);
      }

      public String tcpRuleFormat() {
         return tcpRuleFormatProperty;
      }

      public String tcpRuleRegexp() {
         return tcpRuleRegexpProperty;
      }
   }

   @Provides
   @Named("azureGroupId")
   public static String getGroupId() {
      String group =  System.getProperty("test.azurecompute-arm.groupname");
      if (group == null)
         group = "jCloudsGroup22";
      return group;
   }

   @Provides
   @Named(TIMEOUT_IMAGE_AVAILABLE)
   protected Predicate<String> provideImageAvailablePredicate(final AzureComputeApi api, ComputeServiceConstants.Timeouts timeouts,
                                                               ComputeServiceConstants.PollPeriod pollPeriod) {
      return retry(new ImageDonePredicate(api), timeouts.imageAvailable, pollPeriod.pollInitialPeriod,
            pollPeriod.pollMaxPeriod);
   }

   @VisibleForTesting
   static class ImageDonePredicate implements Predicate<String> {

      private final AzureComputeApi api;

      public ImageDonePredicate(AzureComputeApi api) {
         this.api = checkNotNull(api, "api must not be null");
      }

      @Override
      public boolean apply(String input) {
         checkNotNull(input, "action id cannot be null");
         StorageService service = api.getStorageAccountApi(AzureComputeServiceContextModule.getGroupId()).get(input);
         return service == null ? false : true;
      }

   }

}
