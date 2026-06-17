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

import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.bloomreach.forge.iframeperspective.SecurityHeaders.KEY_CONTENT_SECURITY_POLICY;
import static org.bloomreach.forge.iframeperspective.SecurityHeaders.KEY_X_CONTENT_SECURITY_POLICY;
import static org.bloomreach.forge.iframeperspective.SecurityHeaders.KEY_X_FRAME_OPTIONS;
import static org.bloomreach.forge.iframeperspective.SecurityHeaders.KEY_X_WEBKIT_CSP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityHeadersTest {

    @Mock
    private IPluginConfig config;

    // -------------------------------------------------------------------------
    // SecurityHeaders.from(IPluginConfig)
    // -------------------------------------------------------------------------

    @Test
    void from_allValuesPresent_populatesAllFields() {
        when(config.getString(KEY_X_FRAME_OPTIONS, null)).thenReturn("SAMEORIGIN");
        when(config.getString(KEY_CONTENT_SECURITY_POLICY, null)).thenReturn("default-src 'self'");
        when(config.getString(KEY_X_CONTENT_SECURITY_POLICY, null)).thenReturn("default-src 'self'");
        when(config.getString(KEY_X_WEBKIT_CSP, null)).thenReturn("default-src 'self'");

        final SecurityHeaders headers = SecurityHeaders.from(config);

        assertEquals("SAMEORIGIN", headers.xFrameOptions());
        assertEquals("default-src 'self'", headers.contentSecurityPolicy());
        assertEquals("default-src 'self'", headers.xContentSecurityPolicy());
        assertEquals("default-src 'self'", headers.xWebkitCSP());
    }

    @Test
    void from_allValuesAbsent_allFieldsNull() {
        when(config.getString(KEY_X_FRAME_OPTIONS, null)).thenReturn(null);
        when(config.getString(KEY_CONTENT_SECURITY_POLICY, null)).thenReturn(null);
        when(config.getString(KEY_X_CONTENT_SECURITY_POLICY, null)).thenReturn(null);
        when(config.getString(KEY_X_WEBKIT_CSP, null)).thenReturn(null);

        final SecurityHeaders headers = SecurityHeaders.from(config);

        assertNull(headers.xFrameOptions());
        assertNull(headers.contentSecurityPolicy());
        assertNull(headers.xContentSecurityPolicy());
        assertNull(headers.xWebkitCSP());
    }

    @Test
    void from_valueWithLeadingAndTrailingWhitespace_isTrimmed() {
        when(config.getString(KEY_X_FRAME_OPTIONS, null)).thenReturn("  DENY  ");
        when(config.getString(KEY_CONTENT_SECURITY_POLICY, null)).thenReturn(null);
        when(config.getString(KEY_X_CONTENT_SECURITY_POLICY, null)).thenReturn(null);
        when(config.getString(KEY_X_WEBKIT_CSP, null)).thenReturn(null);

        final SecurityHeaders headers = SecurityHeaders.from(config);

        assertEquals("DENY", headers.xFrameOptions());
    }

    @Test
    void from_blankStringValue_normalisedToNull() {
        when(config.getString(KEY_X_FRAME_OPTIONS, null)).thenReturn("   ");
        when(config.getString(KEY_CONTENT_SECURITY_POLICY, null)).thenReturn(null);
        when(config.getString(KEY_X_CONTENT_SECURITY_POLICY, null)).thenReturn(null);
        when(config.getString(KEY_X_WEBKIT_CSP, null)).thenReturn(null);

        final SecurityHeaders headers = SecurityHeaders.from(config);

        assertNull(headers.xFrameOptions(), "Blank value should be normalised to null");
    }

    // -------------------------------------------------------------------------
    // SecurityHeaders canonical constructor (direct construction)
    // -------------------------------------------------------------------------

    @Test
    void constructor_blankValues_normalisedToNull() {
        final SecurityHeaders headers = new SecurityHeaders("", "  ", null, "\t");

        assertNull(headers.xFrameOptions());
        assertNull(headers.contentSecurityPolicy());
        assertNull(headers.xContentSecurityPolicy());
        assertNull(headers.xWebkitCSP());
    }

    @Test
    void constructor_nonBlankValues_preserved() {
        final SecurityHeaders headers = new SecurityHeaders("DENY", "csp-value", "x-csp-value", "webkit-csp-value");

        assertEquals("DENY", headers.xFrameOptions());
        assertEquals("csp-value", headers.contentSecurityPolicy());
        assertEquals("x-csp-value", headers.xContentSecurityPolicy());
        assertEquals("webkit-csp-value", headers.xWebkitCSP());
    }
}
