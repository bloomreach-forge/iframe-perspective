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

import org.hippoecm.frontend.plugin.config.IPluginConfig;

/**
 * Immutable value object holding the security-related HTTP response headers
 * that the IFrame perspective may emit. Empty/blank strings are normalised to
 * {@code null} so callers can use a simple {@code != null} guard.
 */
public record SecurityHeaders(
        String xFrameOptions,
        String contentSecurityPolicy,
        String xContentSecurityPolicy,
        String xWebkitCSP) {

    /** Key names in the plugin configuration. */
    static final String KEY_X_FRAME_OPTIONS = "x-frame-options";
    static final String KEY_CONTENT_SECURITY_POLICY = "content-security-policy";
    static final String KEY_X_CONTENT_SECURITY_POLICY = "x-content-security-policy";
    static final String KEY_X_WEBKIT_CSP = "x-webkit-csp";

    /**
     * Compact canonical constructor: normalises blank values to {@code null}.
     */
    public SecurityHeaders {
        xFrameOptions = nullIfBlank(xFrameOptions);
        contentSecurityPolicy = nullIfBlank(contentSecurityPolicy);
        xContentSecurityPolicy = nullIfBlank(xContentSecurityPolicy);
        xWebkitCSP = nullIfBlank(xWebkitCSP);
    }

    /**
     * Reads security header values from the supplied plugin configuration.
     * Each value is trimmed; blank/missing values become {@code null}.
     *
     * @param config the plugin configuration; must not be {@code null}
     * @return a new {@code SecurityHeaders} instance
     */
    public static SecurityHeaders from(final IPluginConfig config) {
        return new SecurityHeaders(
                trimmed(config, KEY_X_FRAME_OPTIONS),
                trimmed(config, KEY_CONTENT_SECURITY_POLICY),
                trimmed(config, KEY_X_CONTENT_SECURITY_POLICY),
                trimmed(config, KEY_X_WEBKIT_CSP));
    }

    // -------------------------------------------------------------------------
    // helpers
    // -------------------------------------------------------------------------

    private static String trimmed(final IPluginConfig config, final String key) {
        return StringUtils.trimToNull(config.getString(key, null));
    }

    private static String nullIfBlank(final String value) {
        return StringUtils.isBlank(value) ? null : value;
    }
}
