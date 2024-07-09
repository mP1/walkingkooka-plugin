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

import org.junit.jupiter.api.Test;
import walkingkooka.HashCodeEqualsDefinedTesting2;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class PluginSelectorTextComponentAlternativeTest implements HashCodeEqualsDefinedTesting2<PluginSelectorTextComponentAlternative>,
        HasTextTesting,
        ClassTesting2<PluginSelectorTextComponentAlternative> {

    private final static String LABEL = "Label123";

    private final static String TEXT = "Text123";

    @Test
    public void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginSelectorTextComponentAlternative.with(
                        null,
                        TEXT
                )
        );
    }

    @Test
    public void testWithNullLTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> PluginSelectorTextComponentAlternative.with(
                        LABEL,
                        null
                )
        );
    }

    @Test
    public void testWith() {
        final PluginSelectorTextComponentAlternative alternative = PluginSelectorTextComponentAlternative.with(
                LABEL,
                TEXT
        );
        this.checkEquals(
                LABEL,
                alternative.label(),
                "label"
        );
        this.textAndCheck(
                alternative,
                TEXT
        );
    }

    @Test
    public void testWithEmptyLabelAndEmptyText() {
        final String label = "";
        final String text = "";

        final PluginSelectorTextComponentAlternative alternative = PluginSelectorTextComponentAlternative.with(
                label,
                text
        );
        this.checkEquals(
                label,
                alternative.label(),
                "label"
        );
        this.textAndCheck(
                alternative,
                text
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(
                PluginSelectorTextComponentAlternative.with(
                        "different " + LABEL,
                        TEXT
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                PluginSelectorTextComponentAlternative.with(
                        LABEL,
                        "different " + TEXT
                )
        );
    }

    @Override
    public PluginSelectorTextComponentAlternative createObject() {
        return PluginSelectorTextComponentAlternative.with(
                LABEL,
                TEXT
        );
    }

    // Class............................................................................................................

    @Override
    public Class<PluginSelectorTextComponentAlternative> type() {
        return PluginSelectorTextComponentAlternative.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
