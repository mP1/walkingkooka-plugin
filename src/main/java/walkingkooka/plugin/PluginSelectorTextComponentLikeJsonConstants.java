/*
 * Copyright 2024 Miroslav Pokorny (github.com/mP1)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package walkingkooka.plugin;

import walkingkooka.tree.json.JsonPropertyName;

/**
 * Holds non-public marshall constants
 */
final class PluginSelectorTextComponentLikeJsonConstants {

    final static String LABEL_PROPERTY_STRING = "label";

    final static String TEXT_PROPERTY_STRING = "text";

    final static String ALTERNATIVES_PROPERTY_STRING = "alternatives";

    final static JsonPropertyName LABEL_PROPERTY = JsonPropertyName.with(LABEL_PROPERTY_STRING);

    final static JsonPropertyName TEXT_PROPERTY = JsonPropertyName.with(TEXT_PROPERTY_STRING);

    final static JsonPropertyName ALTERNATIVES_PROPERTY = JsonPropertyName.with(ALTERNATIVES_PROPERTY_STRING);

    private PluginSelectorTextComponentLikeJsonConstants() {
        throw new UnsupportedOperationException();
    }
}
