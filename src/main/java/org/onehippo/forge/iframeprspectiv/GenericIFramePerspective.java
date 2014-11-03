/*
 *  Copyright 2008 Hippo.
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
package org.onehippo.forge.iframeprspectiv;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.JavaScriptResourceReference;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;
import org.hippoecm.frontend.service.IconSize;

public class GenericIFramePerspective extends Perspective {

    private static final String DEFAULT_ICON_PREFIX = "generic-iframe-perspective-";
    private static final String DEFAULT_ICON_SUFFIX = ".png";

    private static final ResourceReference GENERIC_IFRAME_CSS = new CssResourceReference(GenericIFramePerspective.class, "generic-iframe-perspective.css");
    private static final ResourceReference GENERIC_IFRAME_JS = new JavaScriptResourceReference(GenericIFramePerspective.class, "generic-iframe-perspective.js");

    private static final long serialVersionUID = 1L;

    private static final String IFRAME_ATTRIBUTE_PREFIX = "iframe.";

    private String iconPrefix;
    private String iconSuffix;

    private final WebMarkupContainer iframe;

    public GenericIFramePerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setOutputMarkupId(true);

        iframe = new WebMarkupContainer("generic-perspective-iframe");
        iframe.setOutputMarkupId(true);

        iconPrefix = config.getString("icon-prefix", DEFAULT_ICON_PREFIX);
        iconSuffix = config.getString("icon-suffix", DEFAULT_ICON_SUFFIX);

        for (String key : config.keySet()) {
            if (key.startsWith(IFRAME_ATTRIBUTE_PREFIX)) {
                String attrName = key.substring(IFRAME_ATTRIBUTE_PREFIX.length());
                String attrValue = config.getString(key, null);

                if (attrValue != null) {
                    iframe.add(new AttributeModifier(attrName, attrValue));
                }
            }
        }

        add(iframe);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.render(CssHeaderItem.forReference(GENERIC_IFRAME_CSS));
        response.render(JavaScriptHeaderItem.forReference(GENERIC_IFRAME_JS));
        response.render(OnDomReadyHeaderItem.forScript("GenericIFramePerspective.showIFrame(\"" + iframe.getMarkupId() + "\");"));
    }

    @Override
    public ResourceReference getIcon(IconSize type) {
        return new PackageResourceReference(GenericIFramePerspective.class, iconPrefix + type.getSize() + iconSuffix);
    }

    public void showDialog(IDialogService.Dialog dialog) {
        getPluginContext().getService(IDialogService.class.getName(), IDialogService.class).show(dialog);
    }
}
