/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.sling.starter.webapp.integrationtest.servlets.post;

import org.apache.sling.junit.rules.TeleporterRule;
import org.apache.sling.servlets.post.JakartaPostOperation;
import org.apache.sling.servlets.post.PostOperation;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class PostOperationsRegistrationTest {

    @Rule
    public final TeleporterRule teleporter = TeleporterRule.forClass(getClass(), "Launchpad");

    @Test
    public void jakartaPostOperationIsRegistered() {
        JakartaPostOperation modifyOperation =
                teleporter.getService(JakartaPostOperation.class, "(sling.post.operation=modify)");
        assertNotNull("JakartaPostOperation for modify", modifyOperation);
    }

    @Test
    @SuppressWarnings("deprecation")
    @Ignore("SLING-13086")
    public void javaxSlingPostOperationIsRegistered() {
        PostOperation modifyOperation = teleporter.getService(PostOperation.class, "(sling.post.operation=modify)");
        assertNotNull("PostOperation for modify", modifyOperation);
    }
}
