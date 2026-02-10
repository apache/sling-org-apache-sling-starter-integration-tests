<%--
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
--%>
<%@page session="false"%>
<%@taglib prefix="sling" uri="http://sling.apache.org/taglibs/sling/1.0"%>
<sling:defineObjects/>
<%
  // Output the class names of the resource resolver from defineObjects and from the bindings
  org.apache.sling.api.scripting.SlingBindings b =
      (org.apache.sling.api.scripting.SlingBindings)
          request.getAttribute(org.apache.sling.api.scripting.SlingBindings.class.getName());
  Object bindingsResolver = b.get("resolver");
%>
defineObjectsResolverClass=<%= resourceResolver.getClass().getName() %>
bindingsResolverClass=<%= bindingsResolver != null ? bindingsResolver.getClass().getName() : "null" %>
resourceResolverFromResource=<%= resource.getResourceResolver().getClass().getName() %>
resolversMatch=<%= resourceResolver == bindingsResolver %>
