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

import static org.assertj.core.api.Assertions.assertThat;

import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiMockTest;
import org.jclouds.azurecomputearm.xml.ListLocationsHandlerTest;
import org.testng.annotations.Test;

import com.squareup.okhttp.mockwebserver.MockWebServer;

@Test(groups = "unit", testName = "LocationApiMockTest")
public class LocationApiMockTest extends BaseAzureComputeApiMockTest {

   public void testList() throws Exception {
      MockWebServer server = mockAzureManagementServer();
      server.enqueue(xmlResponse("/locations.xml"));

      try {
         LocationApi api = api(server.getUrl("/")).getLocationApi();

         assertThat(api.list()).containsExactlyElementsOf(ListLocationsHandlerTest.expected());

         assertSent(server, "GET", "/locations");
      } finally {
         server.shutdown();
      }
   }

}
