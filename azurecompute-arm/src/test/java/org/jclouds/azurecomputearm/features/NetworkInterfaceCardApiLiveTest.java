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

import com.google.common.collect.ImmutableMap;
import org.jclouds.azurecomputearm.domain.IdReference;
import org.jclouds.azurecomputearm.domain.IpConfiguration;
import org.jclouds.azurecomputearm.domain.NetworkInterfaceCard;
import org.jclouds.azurecomputearm.domain.Subnet;
import org.jclouds.azurecomputearm.domain.VirtualNetwork;
import org.jclouds.azurecomputearm.internal.BaseAzureComputeApiLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.assertNotNull;

//mvn -Dtest=NetworkInterfaceCardApiLiveTest -Dtest.azurecompute-arm.identity="f....a" -Dtest.azurecompute-arm.subscriptionid="626f67f6-8fd0-xxxx-yyyy-e3ce95f7dfec" -Dtest.azurecompute-arm.resourcegroup="hardcodedgroup" -Dtest.azurecompute-arm.credential="yorupass" -Dtest.azurecompute-arm.endpoint="https://management.azure.com/" -Dtest.jclouds.oauth.resource="https://management.azure.com/" -Dtest.oauth.endpoint="https://login.microsoftonline.com/youraccount.onmicrosoft.com/oauth2/token" test

@Test(groups = "live", singleThreaded = true)
public class NetworkInterfaceCardApiLiveTest extends BaseAzureComputeApiLiveTest {

    final String subscriptionid =  getSubscriptionId();
    String resourcegroup;
    String subnetID;

    @BeforeClass
    @Override
    public void setup(){
        super.setup();

        resourcegroup = getResourceGroupName();

        //Subnets belong to a virtual network so that needs to be created first
        VirtualNetwork vn = getOrCreateVirtualNetwork(VIRTUAL_NETWORK_NAME);
        assertNotNull(vn);

        //Subnet needs to be up & running before NIC can be created
        Subnet subnet = getOrCreateSubnet(DEFAULT_SUBNET_NAME, VIRTUAL_NETWORK_NAME);
        assertNotNull(subnet);
        assertNotNull(subnet.id());
        subnetID = subnet.id();
    }


    @Test(groups = "live")
    public void deleteNetworkInterfaceCardResourceDoesNotExist() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);
        String statusCode = nicApi.deleteNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);
        assertTrue(statusCode.equals("204"));
    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#createVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "deleteNetworkInterfaceCardResourceDoesNotExist")
    public void createNetworkInterfaceCard() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);


        //Create properties object
        final NetworkInterfaceCard.NetworkInterfaceCardProperties networkInterfaceCardProperties =
                NetworkInterfaceCard.NetworkInterfaceCardProperties.builder()
                        .ipConfigurations(
                                Arrays.asList(
                                        IpConfiguration.builder()
                                                .name("myipconfig")
                                                .properties(
                                                        IpConfiguration.IpConfigurationProperties.builder()
                                                                .subnet(IdReference.create(subnetID))
                                                                .privateIPAllocationMethod("Dynamic")
                                                                .build()
                                                )
                                                .build()
                                ))
                        .build();

        final Map<String, String> tags = ImmutableMap.of("jclouds", "livetest");
        NetworkInterfaceCard nic = nicApi.createOrUpdateNetworkInterfaceCard(NETWORKINTERFACECARD_NAME, LOCATION,  networkInterfaceCardProperties, tags);

        assertEquals(nic.name(), NETWORKINTERFACECARD_NAME);
        assertEquals(nic.location(), LOCATION);
        assertTrue(nic.properties().ipConfigurations().size() > 0 );
        assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetID);
        assertEquals(nic.tags().get("jclouds"), "livetest");

    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#getVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "createNetworkInterfaceCard")
    public void getNetworkInterfaceCard() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);

        NetworkInterfaceCard nic = nicApi.getNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);

        assertEquals(nic.name(), NETWORKINTERFACECARD_NAME);
        assertEquals(nic.location(), LOCATION);
        assertTrue(nic.properties().ipConfigurations().size() > 0 );
        assertEquals(nic.properties().ipConfigurations().get(0).name(), "myipconfig");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().privateIPAllocationMethod(), "Dynamic");
        assertEquals(nic.properties().ipConfigurations().get(0).properties().subnet().id(), subnetID);
    }

    //mvn -Dtest=VirtualNetworkApiLiveTest#listVirtualNetworks test
    @Test(groups = "live", dependsOnMethods = "getNetworkInterfaceCard")
    public void listNetworkInterfaceCards() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);

        List<NetworkInterfaceCard> nicList = nicApi.listNetworkInterfaceCards();

        assertTrue(nicList.size() > 0);
    }


    //mvn -Dtest=VirtualNetworkApiLiveTest#deleteVirtualNetwork test
    @Test(groups = "live", dependsOnMethods = "listNetworkInterfaceCards", alwaysRun = true)
    public void deleteNetworkInterfaceCard() {

        final NetworkInterfaceCardApi nicApi = api.getNetworkInterfaceCardApi(subscriptionid, resourcegroup);

        String statusCode = nicApi.deleteNetworkInterfaceCard(NETWORKINTERFACECARD_NAME);
        assertTrue(statusCode.equals("202"));
    }

}
