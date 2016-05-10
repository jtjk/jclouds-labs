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

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecomputearm.binders.NetworkConfigurationToXML;
import org.jclouds.azurecomputearm.domain.NetworkConfiguration;
import org.jclouds.azurecomputearm.domain.NetworkConfiguration.VirtualNetworkSite;
import org.jclouds.azurecomputearm.functions.ParseRequestIdHeader;
import org.jclouds.azurecomputearm.xml.ListVirtualNetworkSitesHandler;
import org.jclouds.azurecomputearm.xml.NetworkConfigurationHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.Headers;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;

@Path("/services/networking")
@Headers(keys = "x-ms-version", values = "{jclouds.api-version}")
@Consumes(MediaType.APPLICATION_XML)
public interface VirtualNetworkApi {

   /**
    * The Get Network Configuration operation retrieves the network configuration file.
    *
    * @return The response body is a netcfg.cfg file.
    *
    */
   @Named("GetVirtualNetworkConfiguration")
   @Path("/media")
   @GET
   @XMLResponseParser(NetworkConfigurationHandler.class)
   @Fallback(NullOnNotFoundOr404.class)
   NetworkConfiguration getNetworkConfiguration();

   @Named("ListVirtualNetworkSites")
   @Path("/virtualnetwork")
   @GET
   @XMLResponseParser(ListVirtualNetworkSitesHandler.class)
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<VirtualNetworkSite> list();

   @Named("SetVirtualNetworkConfiguration")
   @Path("/media")
   @PUT
   @Produces(MediaType.TEXT_PLAIN)
   @ResponseParser(ParseRequestIdHeader.class)
   String set(@BinderParam(NetworkConfigurationToXML.class) NetworkConfiguration networkConfiguration);

}
