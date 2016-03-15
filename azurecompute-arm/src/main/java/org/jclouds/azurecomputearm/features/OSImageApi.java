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

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import java.util.List;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jclouds.Fallbacks.EmptyListOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.azurecomputearm.binders.OSImageParamsToXML;
import org.jclouds.azurecomputearm.domain.OSImageParams;
import org.jclouds.azurecomputearm.domain.Offer;
import org.jclouds.azurecomputearm.domain.Publisher;
import org.jclouds.azurecomputearm.domain.SKU;
import org.jclouds.azurecomputearm.functions.OSImageParamsName;
import org.jclouds.azurecomputearm.functions.ParseRequestIdHeader;
import org.jclouds.azurecomputearm.oauth.v2.filters.OAuthFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.SelectJson;

/**
 * The Azure Resource Management API includes operations for managing the OS images in your subscription.
 */
@Path("/subscriptions/{subscriptionId}/providers/Microsoft.Compute/locations/{location}")
@RequestFilters(OAuthFilter.class)
@QueryParams(keys = "api-version", values = "2015-06-15")
@Consumes(APPLICATION_JSON)
public interface OSImageApi {

   /**
    * List Publishers in location
    */
   @Named("ListPublishers")
   @GET
   @Produces(APPLICATION_JSON)
   @Path("/publishers")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Publisher> listPublishers();

   /*
Version- https://management.azure.com/subscriptions/<sub-id>/providers/Microsoft.Compute/locations/eastus/publishers/<publisher>/artifacttypes/vmimage/offers/<offer>/skus/<sku>/versions?api-version=2015-06-15
    */
   /**
    * List Offers in publisher
    */
   @Named("ListOffers")
   @GET
   @Produces(APPLICATION_JSON)
   @Path("/publishers/{publisher}/artifacttypes/vmimage/offers")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<Offer> listOffers(@PathParam("publisher") String publisher);

   /**
    * List SKUs in offer
    */
   @Named("ListSKUs")
   @GET
   @Produces(APPLICATION_JSON)
   @Path("/publishers/{publisher}/artifacttypes/vmimage/offers/{offer}/skus")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<SKU> listSKUs(@PathParam("publisher") String publisher, @PathParam("offer") String offer);

   /**
    * List SKUs in offer
    */
   @Named("ListVersions")
   @GET
   @Produces(APPLICATION_JSON)
   @Path("/publishers/{publisher}/artifacttypes/vmimage/offers/{offer}/skus/{sku}/versions")
   @Fallback(EmptyListOnNotFoundOr404.class)
   List<SKU> listVersions(@PathParam("publisher") String publisher, @PathParam("offer") String offer,
                          @PathParam("sku") String sku);

   /**
    * The Add OS Image operation adds an OS image that is currently stored in a storage account in your subscription to
    * the image repository.
    */
   @Named("AddImage")
   @POST
   @ResponseParser(ParseRequestIdHeader.class)
   String add(@BinderParam(OSImageParamsToXML.class) OSImageParams params);

   /**
    * The Update OS Image operation updates an OS image that in your image repository.
    */
   @Named("UpdateImage")
   @PUT
   @Path("/{imageName}")
   @ResponseParser(ParseRequestIdHeader.class)
   String update(@PathParam("imageName") @ParamParser(OSImageParamsName.class)
           @BinderParam(OSImageParamsToXML.class) OSImageParams params);

   /**
    * The Delete Cloud Service operation deletes the specified cloud service from Windows Azure.
    *
    * @param imageName the unique DNS Prefix value in the Windows Azure Management Portal
    */
   @Named("DeleteImage")
   @DELETE
   @Path("/{imageName}")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(ParseRequestIdHeader.class)
   String delete(@PathParam("imageName") String imageName);
}
