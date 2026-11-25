/*
 * Copyright 2019 Miroslav Pokorny (github.com/mP1)
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

import org.junit.jupiter.api.Test;
import walkingkooka.EmptyTextException;
import walkingkooka.InvalidCharacterException;
import walkingkooka.reflect.ThrowableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import static org.junit.jupiter.api.Assertions.assertThrows;

final public class PluginNameTest implements PluginNameTesting<PluginName>,
    ThrowableTesting {
    
    private final static String LABEL = "PluginName123";

    @Test
    public void testWithNullLabelFails() {
        this.getMessageAndCheck(
            assertThrows(
                NullPointerException.class,
                () -> PluginName.with(
                    null,
                    PluginName.SEPARATOR + "hello"
                )
            ),
            "label"
        );
    }

    @Test
    public void testWithEmptyLabelFails() {
        this.getMessageAndCheck(
            assertThrows(
                EmptyTextException.class,
                () -> PluginName.with(
                    "",
                    PluginName.SEPARATOR + "hello"
                )
            ),
            "Empty \"label\""
        );
    }

    @Test
    public void testWithInitialSeparatorFails() {
        this.getMessageAndCheck(
            assertThrows(
                InvalidCharacterException.class,
                () -> PluginName.with(
                    LABEL,
                    PluginName.SEPARATOR + "hello"
                )
            ),
            "Invalid \"PluginName123\" character ':' at 0"
        );
    }

    @Test
    public void testWithIncludesSeparatorFails() {
        this.getMessageAndCheck(
            assertThrows(
                InvalidCharacterException.class,
                () -> PluginName.with(
                    LABEL,
                    "hello" + PluginName.SEPARATOR + "123"
                )
            ),
            "Invalid \"PluginName123\" character ':' at 5"
        );
    }

    // Name.............................................................................................................

    @Override
    public PluginName createName(final String name) {
        return PluginName.with(
            LABEL,
            name
        );
    }

    // Class............................................................................................................

    @Override
    public Class<PluginName> type() {
        return PluginName.class;
    }

    // Json.............................................................................................................

    @Override
    public PluginName unmarshall(final JsonNode from,
                                 final JsonNodeUnmarshallContext context) {
        return PluginName.unmarshall(from, context);
    }
}
