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

import java.util.Map;
import java.util.Set;

import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IFrameAttributeExtractorTest {

    @Mock
    private IPluginConfig config;

    // -------------------------------------------------------------------------
    // extractAttributes — happy path
    // -------------------------------------------------------------------------

    @Test
    void extractAttributes_singleIframeKey_returnsStrippedName() {
        when(config.keySet()).thenReturn(Set.of("iframe.src"));
        when(config.getString("iframe.src", null)).thenReturn("https://example.com");

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertEquals(1, result.size());
        assertEquals("https://example.com", result.get("src"));
    }

    @Test
    void extractAttributes_multipleIframeKeys_allReturned() {
        when(config.keySet()).thenReturn(Set.of("iframe.src", "iframe.width", "iframe.height"));
        when(config.getString("iframe.src", null)).thenReturn("https://example.com");
        when(config.getString("iframe.width", null)).thenReturn("100%");
        when(config.getString("iframe.height", null)).thenReturn("600");

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertEquals(3, result.size());
        assertEquals("https://example.com", result.get("src"));
        assertEquals("100%", result.get("width"));
        assertEquals("600", result.get("height"));
    }

    // -------------------------------------------------------------------------
    // extractAttributes — prefix filtering
    // -------------------------------------------------------------------------

    @Test
    void extractAttributes_nonIframeKeysIgnored() {
        when(config.keySet()).thenReturn(Set.of("x-frame-options", "iframe.src", "service.class"));
        when(config.getString("iframe.src", null)).thenReturn("https://example.com");

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("src"), "Only 'src' should be extracted");
        assertFalse(result.containsKey("x-frame-options"));
        assertFalse(result.containsKey("service.class"));
    }

    @Test
    void extractAttributes_emptyConfig_returnsEmptyMap() {
        when(config.keySet()).thenReturn(Set.of());

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertTrue(result.isEmpty());
    }

    @Test
    void extractAttributes_noIframeKeys_returnsEmptyMap() {
        when(config.keySet()).thenReturn(Set.of("service.class", "wicket.id"));

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // extractAttributes — null value handling
    // -------------------------------------------------------------------------

    @Test
    void extractAttributes_nullAttributeValue_skipped() {
        when(config.keySet()).thenReturn(Set.of("iframe.src", "iframe.allow"));
        when(config.getString("iframe.src", null)).thenReturn("https://example.com");
        when(config.getString("iframe.allow", null)).thenReturn(null);

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("src"));
        assertFalse(result.containsKey("allow"), "Key with null value must not appear in result");
    }

    @Test
    void extractAttributes_allNullValues_returnsEmptyMap() {
        when(config.keySet()).thenReturn(Set.of("iframe.src", "iframe.width"));
        when(config.getString("iframe.src", null)).thenReturn(null);
        when(config.getString("iframe.width", null)).thenReturn(null);

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertTrue(result.isEmpty());
    }

    // -------------------------------------------------------------------------
    // extractAttributes — prefix edge cases
    // -------------------------------------------------------------------------

    @Test
    void extractAttributes_keyIsExactlyPrefix_producesEmptyAttributeName() {
        // "iframe." with nothing after the dot → attribute name is ""
        when(config.keySet()).thenReturn(Set.of("iframe."));
        when(config.getString("iframe.", null)).thenReturn("value");

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        // The extractor should include it (empty-string key); callers decide whether to use it
        assertTrue(result.containsKey(""));
    }

    @Test
    void extractAttributes_keyStartsWithIframePrefixAsSubstring_notMatched() {
        // "not-iframe.src" must NOT be treated as an iframe key
        when(config.keySet()).thenReturn(Set.of("not-iframe.src"));

        final Map<String, String> result = IFrameAttributeExtractor.extractAttributes(config);

        assertTrue(result.isEmpty());
    }
}
