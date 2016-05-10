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
package org.jclouds.azurecomputearm.xml;

import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static org.jclouds.util.SaxUtils.currentOrNull;

import org.jclouds.azurecomputearm.domain.Error;
import org.jclouds.azurecomputearm.domain.Operation;
import org.jclouds.azurecomputearm.domain.Operation.Status;
import org.jclouds.http.functions.ParseSax;
import org.xml.sax.Attributes;

/**
 * @see <a href="http://msdn.microsoft.com/en-us/library/ee460783" >api</a>
 */
public final class OperationHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Operation> {

   private String id;

   private Status status;

   private Integer httpStatusCode;

   private Error error;

   private boolean inError;

   private final ErrorHandler errorHandler = new ErrorHandler();

   private final StringBuilder currentText = new StringBuilder();

   @Override
   public Operation getResult() {
      return Operation.create(id, status, httpStatusCode, error);
   }

   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) {
      if (qName.equals("Error")) {
         inError = true;
      }
   }

   @Override
   public void endElement(String uri, String name, String qName) {
      if (qName.equals("Error")) {
         error = errorHandler.getResult();
         inError = false;
      } else if (inError) {
         errorHandler.endElement(uri, name, qName);
      } else if (qName.equals("ID")) {
         id = currentOrNull(currentText);
      } else if (qName.equals("Status")) {
         String statusText = currentOrNull(currentText);
         status = Status.fromString(UPPER_CAMEL.to(UPPER_UNDERSCORE, statusText));
      } else if (qName.equals("HttpStatusCode")) {
         httpStatusCode = Integer.parseInt(currentOrNull(currentText));
      }
      currentText.setLength(0);
   }

   @Override
   public void characters(char ch[], int start, int length) {
      if (inError) {
         errorHandler.characters(ch, start, length);
      } else {
         currentText.append(ch, start, length);
      }
   }
}
