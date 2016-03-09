package org.jclouds.azurecomputearm.functions;

import com.google.common.base.Function;
import org.jclouds.http.HttpResponse;

import javax.inject.Singleton;

import static org.jclouds.http.HttpUtils.releasePayload;

/**
 * Parses an http response code from http responser
 */
@Singleton
public class StatusCodeParser implements Function<HttpResponse, String> {

   public String apply(final HttpResponse from) {
      releasePayload(from);
      final String statusCode = Integer.toString(from.getStatusCode());
      if (statusCode != null) {
         return statusCode;
      }
      throw new IllegalStateException("did not receive RequestId in: " + from);
   }

}
