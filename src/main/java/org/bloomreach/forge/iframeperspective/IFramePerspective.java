/*
 *  Copyright 2026 Bloomreach, Inc. (https://www.bloomreach.com).
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.bloomreach.forge.iframeperspective;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IFramePerspective extends Perspective {

    private static Logger log = LoggerFactory.getLogger(IFramePerspective.class);

    private static final ResourceReference IFRAME_CSS = new CssResourceReference(IFramePerspective.class, "iframe-perspective.css");
    private static final ResourceReference IFRAME_JS = new JavaScriptResourceReference(IFramePerspective.class, "iframe-perspective.js");

    private final SecurityHeaders securityHeaders;
    private final WebMarkupContainer iframe;

    public IFramePerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setOutputMarkupId(true);

        iframe = new WebMarkupContainer("perspective-iframe");
        iframe.setOutputMarkupId(true);

        securityHeaders = SecurityHeaders.from(config);

        IFrameAttributeExtractor.extractAttributes(config)
                .forEach((name, value) -> iframe.add(new AttributeModifier(name, value)));

        add(iframe);
    }

    @Override
    protected void onRender() {
        super.onRender();

        final Response response = RequestCycle.get().getResponse();

        if (response instanceof WebResponse webResponse) {
            applySecurityHeaders(webResponse);
        } else {
            log.error("Failed to write response headers because response is not WebResponse: {}", response);
        }
    }

    private void applySecurityHeaders(final WebResponse response) {
        if (securityHeaders.xFrameOptions() != null) {
            response.setHeader("X-Frame-Options", securityHeaders.xFrameOptions());
        }
        if (securityHeaders.contentSecurityPolicy() != null) {
            response.setHeader("Content-Security-Policy", securityHeaders.contentSecurityPolicy());
        }
        if (securityHeaders.xContentSecurityPolicy() != null) {
            response.setHeader("X-Content-Security-Policy", securityHeaders.xContentSecurityPolicy());
        }
        if (securityHeaders.xWebkitCSP() != null) {
            response.setHeader("X-Webkit-CSP", securityHeaders.xWebkitCSP());
        }
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(IFRAME_CSS));
        response.render(JavaScriptHeaderItem.forReference(IFRAME_JS));
        response.render(OnDomReadyHeaderItem.forScript("IFramePerspective.showIFrame(\"" + iframe.getMarkupId() + "\");"));
    }
}
