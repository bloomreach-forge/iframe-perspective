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

import org.apache.wicket.ResourceReference;
import org.apache.wicket.behavior.AbstractBehavior;
import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.CSSPackageResource;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.JavascriptPackageResource;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.hippoecm.frontend.dialog.IDialogService;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.plugins.standards.perspective.Perspective;
import org.hippoecm.frontend.service.IconSize;

public class GenericIFramePerspective extends Perspective {
    
    private static final String DEFAULT_ICON_PREFIX = "generic-iframe-perspective-";
    private static final String DEFAULT_ICON_SUFFIX = ".png";

    private static final long serialVersionUID = 1L;
    
    private static final String IFRAME_ATTRIBUTE_PREFIX = "iframe.";
    
    private String iconPrefix;
    private String iconSuffix;

    public GenericIFramePerspective(IPluginContext context, IPluginConfig config) {
        super(context, config);
        setOutputMarkupId(true);

        add(CSSPackageResource.getHeaderContribution(getClass(), "generic-iframe-perspective.css"));
        add(JavascriptPackageResource.getHeaderContribution(getClass(), "generic-iframe-perspective.js"));
        
        final WebMarkupContainer iframeContainer = new WebMarkupContainer("generic-perspective-iframe");
        iframeContainer.setOutputMarkupId(true);
        
        iconPrefix = config.getString("icon-prefix", DEFAULT_ICON_PREFIX);
        iconSuffix = config.getString("icon-suffix", DEFAULT_ICON_SUFFIX);
        
        for (String key : config.keySet()) {
            if (key.startsWith(IFRAME_ATTRIBUTE_PREFIX)) {
                String attrName = key.substring(IFRAME_ATTRIBUTE_PREFIX.length());
                String attrValue = config.getString(key, null);
                
                if (attrValue != null) {
                    iframeContainer.add(new SimpleAttributeModifier(attrName, attrValue));
                }
            }
        }
        
        add(iframeContainer);
        
        add(new AbstractBehavior() {
            private static final long serialVersionUID = 1L;

            @Override
            public void renderHead(IHeaderResponse response) {
                String js = "GenericIFramePerspective.showIFrame(\"" + iframeContainer.getMarkupId() + "\");";
                response.renderOnDomReadyJavascript(js);
                super.renderHead(response); 
            } 
        });
    }

    @Override
    public ResourceReference getIcon(IconSize type) {
        return new ResourceReference(GenericIFramePerspective.class, iconPrefix + type.getSize() + iconSuffix);
    }

    public void showDialog(IDialogService.Dialog dialog) {
        getPluginContext().getService(IDialogService.class.getName(), IDialogService.class).show(dialog);
    }

}
