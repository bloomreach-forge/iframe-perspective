/*
 * Copyright 2008-2020 Bloomreach
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

    if (typeof IFramePerspective == 'undefined') {
        IFramePerspective = new Object();
        IFramePerspective.resizeEventRegisteredMap = [];
    }

    IFramePerspective.getWindowSize = function() {
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

    IFramePerspective.addEvent = function(elem, type, eventHandle) {
        if (elem == null || elem == undefined) return;
        if (elem.addEventListener) {
            elem.addEventListener(type, eventHandle, false);
        } else if (elem.attachEvent) {
            elem.attachEvent("on" + type, eventHandle);
        }
    };

    IFramePerspective.resizeIFrame = function(id) {
        var iframe = Wicket.$(id);
        if (iframe) {
            var windowSize = IFramePerspective.getWindowSize();
            iframe.height = windowSize[1];
        }
    };

    IFramePerspective.showIFrame = function(id) {
        IFramePerspective.resizeIFrame(id);
        if (!IFramePerspective.resizeEventRegisteredMap[id]) {
            IFramePerspective.addEvent(window, "resize", function() { IFramePerspective.resizeIFrame(id); } );
            IFramePerspective.resizeEventRegisteredMap[id] = true;
        }
    };

})();
