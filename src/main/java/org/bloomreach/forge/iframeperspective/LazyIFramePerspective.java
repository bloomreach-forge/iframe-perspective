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

import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.cycle.RequestCycle;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class LazyIFramePerspective extends IFramePerspective {

    private static final Logger log = LoggerFactory.getLogger(LazyIFramePerspective.class);

    public static final String CONFIG_RELOAD_ON_ACTIVATE = "reload-on-activate";

    private final boolean reloadOnActivate;
    private boolean iframeLoaded = false;

    public LazyIFramePerspective(final IPluginContext context, final IPluginConfig config) {
        super(context, config);
        reloadOnActivate = config.getAsBoolean(CONFIG_RELOAD_ON_ACTIVATE, false);
    }

    @Override
    protected boolean shouldAddIframeAttribute(final String attrName) {
        return !"src".equals(attrName);
    }

    @Override
    public void onActivated() {
        super.onActivated();

        String src = getIframeSrc();
        if ((reloadOnActivate || !iframeLoaded) && StringUtils.isNotEmpty(src)) {
            iframeLoaded = true;
            final Optional<AjaxRequestTarget> target = RequestCycle.get().find(AjaxRequestTarget.class);
            if (target.isPresent()) {
                target.get().appendJavaScript(
                        "IFramePerspective.loadIFrame('" + iframe.getMarkupId() + "', '" + src + "');");
            } else {
                log.warn("LazyIFramePerspective.onActivated() was called without an AjaxRequestTarget — " +
                         "iframe src '{}' could not be injected lazily.", src);
            }
        }
    }

    protected String getIframeSrc() {
        return iframeSrc;
    }
}
