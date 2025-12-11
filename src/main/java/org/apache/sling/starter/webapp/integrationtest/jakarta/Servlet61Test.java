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
package org.apache.sling.starter.webapp.integrationtest.jakarta;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.http.HttpServletResponse;
import org.apache.sling.commons.testing.integration.HttpTestBase;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Verifies that Jakarta Servlet 6.1 API usage on the server is working
 */
public class Servlet61Test extends HttpTestBase {

    public void testSetCharsetOverload() throws IOException {

        String content = getContent(HTTP_BASE_URL + "/bin/jakarta-servlet61.txt", "text/plain;charset=utf-8");

        assertThat("Text output", content, equalTo("Hello Servlet API 6.1"));
    }

    public void testNewRedirectOverload() throws IOException {

        String content = getContent(
                HTTP_BASE_URL + "/bin/jakarta-servlet61-redirect.txt",
                "text/plain;charset=utf-8",
                List.of(),
                HttpServletResponse.SC_PERMANENT_REDIRECT);

        assertThat("Text otput", content, equalTo("Custom body before redirect"));
    }
}
