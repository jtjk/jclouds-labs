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
package org.jclouds.azurecomputearm.oauth.v2.config;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;
import static com.google.common.base.Preconditions.checkNotNull;

/** Defines the contents of the credential field in {@link org.jclouds.ContextBuilder#credentials(String, String)}. */
public enum AzureCredentialType {
    // TODO - this is only present to support BEARER_TOKEN_CREDENTIALS in azurecompute-arm mock tests.
    // The expectation is that the OAuth additions in this provider should augment the OAuth v2 work
    // in jclouds-core, but doing so here keeps the changes into a single repo/provider.
    BEARER_TOKEN_CREDENTIALS,

    /** Secret is a password */
    CLIENT_CREDENTIALS_SECRET;

    @Override public String toString() {
        return UPPER_UNDERSCORE.to(LOWER_CAMEL, name());
    }

    public static AzureCredentialType fromValue(String credentialType) {
        return valueOf(LOWER_CAMEL.to(UPPER_UNDERSCORE, checkNotNull(credentialType, "credentialType")));
    }
}
