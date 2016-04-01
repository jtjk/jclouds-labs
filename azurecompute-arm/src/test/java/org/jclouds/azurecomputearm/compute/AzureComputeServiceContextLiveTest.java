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
package org.jclouds.azurecomputearm.compute;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.jclouds.compute.predicates.NodePredicates.runningInGroup;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import org.jclouds.azurecomputearm.AzureComputeProviderMetadata;
import org.jclouds.azurecomputearm.internal.AzureLiveTestUtils;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.RunScriptOnNodesException;
import org.jclouds.compute.config.ComputeServiceProperties;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.inject.Module;

@Test(groups = "live", testName = "AzureComputeServiceContextLiveTest")
public class AzureComputeServiceContextLiveTest extends BaseComputeServiceContextLiveTest {

   public  final String OPERATION_TIMEOUT = "jclouds.azurecompute.operation.timeout";

   public  final String OPERATION_POLL_INITIAL_PERIOD = "jclouds.azurecompute..operation.poll.initial.period";

   public  final String OPERATION_POLL_MAX_PERIOD = "jclouds.azurecompute.operation.poll.max.period";

   public  final String TCP_RULE_FORMAT = "jclouds.azurecompute.tcp.rule.format";

   public  final String TCP_RULE_REGEXP = "jclouds.azurecompute.tcp.rule.regexp";
   private static final int COUNT = 2;

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override protected Properties setupProperties() {
      Properties properties = super.setupProperties();

      properties.put(ComputeServiceProperties.POLL_INITIAL_PERIOD, 1000);
      properties.put(ComputeServiceProperties.POLL_MAX_PERIOD, 10000);
      properties.setProperty(OPERATION_TIMEOUT, "60000");
      properties.setProperty(OPERATION_POLL_INITIAL_PERIOD, "5");
      properties.setProperty(OPERATION_POLL_MAX_PERIOD, "15");
      properties.setProperty(TCP_RULE_FORMAT, "tcp_%s-%s");
      properties.setProperty(TCP_RULE_REGEXP, "tcp_\\d{1,5}-\\d{1,5}");

      // for oauth
      AzureLiveTestUtils.defaultProperties(properties);
      checkNotNull(setIfTestSystemPropertyPresent(properties, "jclouds.oauth.resource"), "test.jclouds.oauth.resource");
      checkNotNull(setIfTestSystemPropertyPresent(properties, "azurecompute-arm.identity"), "c79e3a19-6553-xxxx-8e8e-ab245fd7e7f8");
      checkNotNull(setIfTestSystemPropertyPresent(properties, "azurecompute-arm.credential"), "salasana");
      checkNotNull(setIfTestSystemPropertyPresent(properties, "azurecompute-arm.subscriptionid"), "dc5f2e9c-7a0b-xxxx-97f8-94042641cf42");
      checkNotNull(setIfTestSystemPropertyPresent(properties, "oauth.endpoint"), "test.oauth.endpoint");
      return properties;

   }

   public AzureComputeServiceContextLiveTest() {
      provider = "azurecompute-arm";
   }


   @Test
   public void testLinuxNode() throws RunNodesException {
      final String groupName = String.format("testi%s", System.getProperty("user.name"));

      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      //templateBuilder.imageId("3a50f22b388a4ff7ab41029918570fa6__Windows-Server-2012-Essentials-20141204-enus");
      templateBuilder.imageId("UbuntuServer12.04.5-LTS12.04.201508190");
      templateBuilder.hardwareId("Standard_A0");
      templateBuilder.locationId("/subscriptions/dc5f2e9c-7a0b-49cf-97f8-94042641cf42/locations/eastasia");
      final Template template = templateBuilder.build();

      // test passing custom options
//      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
//      options.inboundPorts(5985);

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 25, template);
         assertThat(nodes).hasSize(25);
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Test
   public void testLaunchNodes() throws RunNodesException {
      final int rand = new Random().nextInt(999);
      final String groupName = String.format("%s%dgroupacsclt", System.getProperty("user.name"), rand);

      final TemplateBuilder templateBuilder = view.getComputeService().templateBuilder();
      templateBuilder.imageId("b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_1-LTS-amd64-server-20150123-en-us-30GB");
      templateBuilder.hardwareId("BASIC_A0");
      templateBuilder.locationId("/subscriptions/dc5f2e9c-7a0b-49cf-97f8-94042641cf42/locations/eastasia");
      final Template template = templateBuilder.build();

      // test passing custom options
//      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
//      options.inboundPorts(22);

      try {
         Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, COUNT, template);
         assertThat(nodes).hasSize(COUNT);

         Map<? extends NodeMetadata, ExecResponse> responses = view.getComputeService().runScriptOnNodesMatching(runningInGroup(groupName), "echo hello");
         assertThat(responses).hasSize(COUNT);

         for (ExecResponse execResponse : responses.values()) {
            assertThat(execResponse.getOutput().trim()).isEqualTo("hello");
         }
      } catch (RunScriptOnNodesException e) {
         Assert.fail();
      } finally {
         view.getComputeService().destroyNodesMatching(inGroup(groupName));
      }
   }

   @Test(expectedExceptions = { IllegalStateException.class })
   public void testNotExistingStorageAccount() throws RunNodesException {
      final int rand = new Random().nextInt(999);
      final String groupName = String.format("%s%d-group-acsclt", System.getProperty("user.name"), rand);

      final String storageServiceName = "not3x1st1ng";

      final Template template = view.getComputeService().templateBuilder().build();

      // test passing custom options
//      final AzureComputeTemplateOptions options = template.getOptions().as(AzureComputeTemplateOptions.class);
//      options.storageAccountName(storageServiceName);

      Set<? extends NodeMetadata> nodes = view.getComputeService().createNodesInGroup(groupName, 1, template);
   }

   @Override
   protected ProviderMetadata createProviderMetadata() {
      AzureComputeProviderMetadata pm = AzureComputeProviderMetadata.builder().build();
      return pm;
   }

}
