/*
 * Copyright 2020 Miroslav Pokorny (github.com/mP1)
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
import walkingkooka.collect.list.Lists;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.HasTextTesting;
import walkingkooka.text.printer.TreePrintableTesting;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeMarshallingTesting;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public final class LabelledTextAndAlternativesTest implements HashCodeEqualsDefinedTesting2<LabelledTextAndAlternatives>,
        HasTextTesting,
        ClassTesting2<LabelledTextAndAlternatives>,
        JsonNodeMarshallingTesting<LabelledTextAndAlternatives>,
        TreePrintableTesting {

    private final static String LABEL = "Label123";

    private final static String TEXT = "Text123";

    private final static List<LabelledText> ALTERNATIVES = Lists.of(
            LabelledText.with(
                    "alternative-label-1",
                    "alternative-text-1"
            ),
            LabelledText.with(
                    "alternative-label-2",
                    "alternative-text-2"
            )
    );

    @Test
    public void testWithNullLabelFails() {
        assertThrows(
                NullPointerException.class,
                () -> LabelledTextAndAlternatives.with(
                        null,
                        TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testWithNullLTextFails() {
        assertThrows(
                NullPointerException.class,
                () -> LabelledTextAndAlternatives.with(
                        LABEL,
                        null,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testWith() {
        final LabelledTextAndAlternatives labelledTextAndAlternatives = LabelledTextAndAlternatives.with(
                LABEL,
                TEXT,
                ALTERNATIVES
        );
        this.checkEquals(
                LABEL,
                labelledTextAndAlternatives.label(),
                "label"
        );
        this.textAndCheck(
                labelledTextAndAlternatives,
                TEXT
        );
        this.checkEquals(
                ALTERNATIVES,
                labelledTextAndAlternatives.alternatives(),
                "label"
        );
    }

    @Test
    public void testWithEmptyLabelEmptyTextAndEmptyAlternatives() {
        final String label = "";
        final String text = "";
        final List<LabelledText> alternatives = Lists.empty();

        final LabelledTextAndAlternatives labelledTextAndAlternatives = LabelledTextAndAlternatives.with(
                label,
                text,
                alternatives
        );
        this.checkEquals(
                label,
                labelledTextAndAlternatives.label(),
                "label"
        );
        this.textAndCheck(
                labelledTextAndAlternatives,
                text
        );
    }

    // hashCode/equals..................................................................................................

    @Test
    public void testEqualsDifferentLabel() {
        this.checkNotEquals(
                LabelledTextAndAlternatives.with(
                        "different " + LABEL,
                        TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testEqualsDifferentText() {
        this.checkNotEquals(
                LabelledTextAndAlternatives.with(
                        LABEL,
                        "different " + TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Test
    public void testEqualsDifferentAlternatives() {
        this.checkNotEquals(
                LabelledTextAndAlternatives.with(
                        LABEL,
                        TEXT,
                        Lists.empty()
                )
        );
    }

    @Override
    public LabelledTextAndAlternatives createObject() {
        return LabelledTextAndAlternatives.with(
                LABEL,
                TEXT,
                ALTERNATIVES
        );
    }

    // TreePrintable.....................................................................................................

    @Test
    public void testTreePrint() {
        this.treePrintAndCheck(
                LabelledTextAndAlternatives.with(
                        LABEL,
                        TEXT,
                        ALTERNATIVES
                ),
                "Label123\n" +
                        "Text123\n" +
                        "  alternative-label-1\n" +
                        "  alternative-text-1\n" +
                        "  alternative-label-2\n" +
                        "  alternative-text-2\n"
        );
    }

    // json.............................................................................................................

    @Test
    public void testJsonMarshall() {
        this.marshallAndCheck(
                LabelledTextAndAlternatives.with(
                        LABEL,
                        TEXT,
                        ALTERNATIVES
                ),
                "{\n" +
                        "  \"label\": \"Label123\",\n" +
                        "  \"text\": \"Text123\",\n" +
                        "  \"alternatives\": [\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-1\",\n" +
                        "      \"text\": \"alternative-text-1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-2\",\n" +
                        "      \"text\": \"alternative-text-2\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}"
        );
    }

    @Test
    public void testJsonUnmarshall() {
        this.unmarshallAndCheck(
                "{\n" +
                        "  \"label\": \"Label123\",\n" +
                        "  \"text\": \"Text123\",\n" +
                        "  \"alternatives\": [\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-1\",\n" +
                        "      \"text\": \"alternative-text-1\"\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"label\": \"alternative-label-2\",\n" +
                        "      \"text\": \"alternative-text-2\"\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}",
                LabelledTextAndAlternatives.with(
                        LABEL,
                        TEXT,
                        ALTERNATIVES
                )
        );
    }

    @Override
    public LabelledTextAndAlternatives unmarshall(final JsonNode json,
                                                  final JsonNodeUnmarshallContext context) {
        return LabelledTextAndAlternatives.unmarshall(
                json,
                context
        );
    }

    @Override
    public LabelledTextAndAlternatives createJsonNodeMarshallingValue() {
        return LabelledTextAndAlternatives.with(
                LABEL,
                TEXT,
                ALTERNATIVES
        );
    }

    // Class............................................................................................................

    @Override
    public Class<LabelledTextAndAlternatives> type() {
        return LabelledTextAndAlternatives.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PUBLIC;
    }
}
