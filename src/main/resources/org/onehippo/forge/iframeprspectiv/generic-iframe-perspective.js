/*
 * Copyright 2008 Hippo
 *
 * Licensed under the Apache License, Version 2.0 (the  "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
(function() {

	if (typeof GenericIFramePerspective == 'undefined') {
		GenericIFramePerspective = new Object();
		GenericIFramePerspective.iframes = [];
		GenericIFramePerspective.timerIDs = [];
	}
	
	GenericIFramePerspective.getWindowSize = function() {
        var width = 0;
        var height = 0;
        if (typeof(window.innerWidth) == 'number') {
        	width = window.innerWidth;
        	height = window.innerHeight;
        } else if (document.documentElement && (document.documentElement.clientWidth)) {
            width = document.documentElement.clientWidth;
            height = document.documentElement.clientHeight;
        } else if (document.body && (document.body.clientWidth || document.body.clientHeight)) {
            width = document.body.clientWidth;
            height = document.body.clientHeight;
        }
        return [ width, height ];
	};
	
	GenericIFramePerspective.checkIFrameInfoElement = function(id, ms) {
		var iframeInfoDiv = Wicket.$(id);
		if (!iframeInfoDiv || iframeInfoDiv.className != "generic-iframe") {
			var item = GenericIFramePerspective.iframes[id];
			if (item) {
				item.className = "generic-iframe-perspective-hidden";
			}
			if (GenericIFramePerspective.timerIDs[id]) {
				window.clearTimeout(GenericIFramePerspective.timerIDs[id]);
			}
		} else {
			if (GenericIFramePerspective.timerIDs[id]) {
				window.clearTimeout(GenericIFramePerspective.timerIDs[id]);
			}
			GenericIFramePerspective.timerIDs[id] = window.setTimeout("GenericIFramePerspective.checkIFrameInfoElement('" + id + "', " + ms + ");", ms);
		}
	};
	
	GenericIFramePerspective.showIFrame = function(id) {
		var iframeInfoDiv = Wicket.$(id);
		var iframe = GenericIFramePerspective.iframes[id];
		if (!iframe) {
			iframe = document.createElement("iframe");
			iframe.className = "generic-iframe-perspective-hidden";
			var src = null;
			if (iframeInfoDiv.attributes && iframeInfoDiv.attributes.length > 0) {
				for (var i = 0; i < iframeInfoDiv.attributes.length; i++) {
					if (iframeInfoDiv.attributes[i].name.toUpperCase() != "SRC") {
						iframe.setAttribute(iframeInfoDiv.attributes[i].name, iframeInfoDiv.attributes[i].value);
					} else {
						src = iframeInfoDiv.attributes[i].value;
					}
				}
			}
			document.body.appendChild(iframe);
			GenericIFramePerspective.iframes[id] = iframe;
			if (src) {
				window.setTimeout("GenericIFramePerspective.iframes['" + id + "'].src = '" + src + "';", 100);
			}
		}
		for (var key in GenericIFramePerspective.iframes) {
			var item = GenericIFramePerspective.iframes[key];
			item.className = "generic-iframe-perspective-hidden";
		}
		var windowSize = GenericIFramePerspective.getWindowSize();
		iframe.width = windowSize[0] - 48;
		iframe.height = windowSize[1] - 76;
		iframe.className = "generic-iframe-perspective-visible";
		GenericIFramePerspective.checkIFrameInfoElement(id, 100);
	};
	
})();
