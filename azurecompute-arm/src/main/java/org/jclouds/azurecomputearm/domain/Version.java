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
package org.jclouds.azurecomputearm.domain;

import com.google.auto.value.AutoValue;
import org.jclouds.json.SerializedNames;

/**
 * Version
 */
@AutoValue
public abstract class Version {

   Version() {
   } // For AutoValue only!

   /**
    * The location of the Version
    */
   public abstract String location();

   /**
    * The name of the Version
    */
   public abstract String name();

   /**
    * The id of the Version
    */
   public abstract String id();

   @SerializedNames({"location", "name", "id"})
   public static Version create(final String location, final String name, final String id) {

      return new AutoValue_Version(location, name, id);
   }
}

