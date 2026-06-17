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

import java.util.LinkedHashMap;
import java.util.Map;

import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * Pure-Java utility that extracts {@code <iframe>} HTML attribute overrides
 * from a plugin configuration.
 *
 * <p>Any configuration key that starts with the prefix {@value #IFRAME_ATTRIBUTE_PREFIX}
 * is treated as an iframe attribute override. The prefix is stripped to produce
 * the target HTML attribute name, e.g. {@code iframe.src} → {@code src}.
 * Entries whose value is {@code null} are silently skipped.
 */
final class IFrameAttributeExtractor {

    /** Config key prefix that marks an entry as an iframe attribute override. */
    static final String IFRAME_ATTRIBUTE_PREFIX = "iframe.";

    private IFrameAttributeExtractor() {
        // utility class
    }

    /**
     * Scans the supplied configuration and returns a map of
     * {@code attributeName → attributeValue} for every key that starts with
     * {@value #IFRAME_ATTRIBUTE_PREFIX} and has a non-null value.
     *
     * @param config the plugin configuration; must not be {@code null}
     * @return a mutable, ordered map; never {@code null}
     */
    static Map<String, String> extractAttributes(final IPluginConfig config) {
        final Map<String, String> attributes = new LinkedHashMap<>();
        for (final String key : config.keySet()) {
            if (key.startsWith(IFRAME_ATTRIBUTE_PREFIX)) {
                final String attrName = key.substring(IFRAME_ATTRIBUTE_PREFIX.length());
                final String attrValue = config.getString(key, null);
                if (attrValue != null) {
                    attributes.put(attrName, attrValue);
                }
            }
        }
        return attributes;
    }
}
