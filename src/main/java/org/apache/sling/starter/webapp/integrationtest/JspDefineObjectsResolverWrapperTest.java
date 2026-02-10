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
package org.apache.sling.starter.webapp.integrationtest;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.sling.commons.testing.integration.HttpTest;
import org.apache.sling.commons.testing.junit.Retry;
import org.apache.sling.commons.testing.junit.RetryRule;
import org.apache.sling.servlets.post.SlingPostConstants;
import org.apache.sling.starter.webapp.integrationtest.util.UniqueResourceType;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test that a javax.servlet.Filter wrapping the SlingHttpServletRequest
 * with a custom ResourceResolver has that ResourceResolver properly
 * propagated to JSP scripts via &lt;sling:defineObjects /&gt;.
 *
 * <p>This test verifies backward compatibility with the Jakarta migration:
 * when a javax Filter wraps the request with a custom
 * SlingHttpServletRequestWrapper that overrides getResourceResolver(),
 * the resourceResolver variable set by DefineObjectsTag in JSPs should
 * reflect the custom wrapper, not the original request's resolver.</p>
 */
public class JspDefineObjectsResolverWrapperTest {

    private String nodeUrl;
    private String scriptPath;
    private Set<String> toDelete = new HashSet<>();

    /** HTTP tests helper */
    private final HttpTest H = new HttpTest();

    @Rule
    public RetryRule retryRule = new RetryRule();

    @Before
    public void setUp() throws Exception {
        H.setUp();

        // Create a test node with a unique resource type
        UniqueResourceType urt = new UniqueResourceType();
        final String url = HttpTest.HTTP_BASE_URL + "/" + getClass().getSimpleName() + "/" + System.currentTimeMillis()
                + SlingPostConstants.DEFAULT_CREATE_SUFFIX;
        final Map<String, String> props = new HashMap<>();
        props.put("sling:resourceType", urt.getResourceType());
        props.put("text", "test-content");
        nodeUrl = H.getTestClient().createNode(url, props);

        // Upload the test JSP that outputs resourceResolver class names
        scriptPath = urt.getScriptPath();
        H.getTestClient().mkdirs(HttpTest.WEBDAV_BASE_URL, scriptPath);
        toDelete.add(H.uploadTestScript(scriptPath, "resolver-wrapper-test.jsp", "html.jsp"));
    }

    @After
    public void tearDown() throws Exception {
        H.tearDown();
        for (String script : toDelete) {
            H.getTestClient().delete(script);
        }
    }

    /**
     * Test that without the wrapping filter, the resourceResolver from
     * defineObjects is the normal resolver (baseline test).
     */
    @Test
    @Retry
    public void testWithoutWrapper() throws IOException {
        final String content = H.getContent(nodeUrl + ".html", HttpTest.CONTENT_TYPE_HTML);
        // Without the filter, resolversMatch should be true (defineObjects and bindings should agree)
        assertTrue(
                "resolversMatch should be true without wrapper: " + content, content.contains("resolversMatch=true"));
    }

    /**
     * Test that when a javax Filter wraps the request with a custom ResourceResolver,
     * the script bindings resolver reflects the custom wrapper. This verifies the fix
     * in DefaultSlingScript.verifySlingBindings().
     */
    @Test
    @Retry
    @Ignore("SLING-13103")
    public void testBindingsResolverIsCustomWrapper() throws IOException {
        // Trigger the ResourceResolverWrappingFilter by adding the wrapResourceResolver param
        final String content = H.getContent(nodeUrl + ".html?wrapResourceResolver=true", HttpTest.CONTENT_TYPE_HTML);

        // The bindings resolver should be the custom wrapper
        assertTrue(
                "Bindings resolver should be custom wrapper: " + content,
                content.contains(
                        "bindingsResolverClass=org.apache.sling.starter.testservices.filters.ResourceResolverWrappingFilter$CustomResourceResolverWrapper"));
    }

    /**
     * Test that when a javax Filter wraps the request with a custom ResourceResolver,
     * the resourceResolver variable set by DefineObjectsTag also reflects the custom
     * wrapper and matches the bindings resolver. This verifies the fix in
     * DefineObjectsTag.doEndTag() (org.apache.sling.scripting.jsp.taglib).
     */
    @Test
    @Retry
    @Ignore("SLING-13103 or follow-up")
    public void testDefineObjectsResolverMatchesBindings() throws IOException {
        // Trigger the ResourceResolverWrappingFilter by adding the wrapResourceResolver param
        final String content = H.getContent(nodeUrl + ".html?wrapResourceResolver=true", HttpTest.CONTENT_TYPE_HTML);

        // The defineObjects resolver SHOULD also be the custom wrapper
        assertTrue(
                "DefineObjects resolver should be custom wrapper: " + content,
                content.contains(
                        "defineObjectsResolverClass=org.apache.sling.starter.testservices.filters.ResourceResolverWrappingFilter$CustomResourceResolverWrapper"));

        // They should match
        assertTrue(
                "DefineObjects resolver should match bindings resolver: " + content,
                content.contains("resolversMatch=true"));
    }
}
