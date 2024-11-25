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

import walkingkooka.Cast;
import walkingkooka.naming.Name;
import walkingkooka.predicate.character.CharPredicate;
import walkingkooka.predicate.character.CharPredicates;
import walkingkooka.text.CharacterConstant;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

/**
 * The {@link Name} of a component. Note component names are case-sensitive.
 */
final public class PluginName implements PluginNameLike<PluginName> {

    /**
     * Separator character that is itself an illegal character within a {@link PluginName} and may be used to form a range.
     */
    public final static CharacterConstant SEPARATOR = CharacterConstant.with(':');

    public static boolean isChar(final int pos,
                                 final char c) {
        return PluginNameLike.isChar(pos, c);
    }

    final static CharPredicate INITIAL = CharPredicates.range('A', 'Z')
            .or(CharPredicates.range('a', 'z'));

    final static CharPredicate PART = INITIAL.or(CharPredicates.range('0', '9'))
            .or(CharPredicates.is('-'));

    public final static int MIN_LENGTH = 1;

    /**
     * The maximum valid length
     */
    public final static int MAX_LENGTH = 255;

    /**
     * Factory that creates a {@link PluginName}
     */
    public static PluginName with(final String name) {
        CharPredicates.failIfNullOrEmptyOrInitialAndPartFalse(
                name,
                PluginName.class.getSimpleName(),
                INITIAL,
                PART
        );

        return new PluginName(name);
    }

    /**
     * Private constructor
     */
    private PluginName(final String name) {
        super();
        this.name = name;
    }

    @Override
    public String value() {
        return this.name;
    }

    private final String name;

    /**
     * Note MIN/MAX length is not tested in the ctor, wrappers should invoke this method after calling new with their label.
     */
    public PluginName checkLength(final String label) {
        Name.checkLength(
                label,
                this.value(),
                MIN_LENGTH,
                MAX_LENGTH
        );

        return this;
    }

    // Object..................................................................................................

    @Override
    public int hashCode() {
        return CASE_SENSITIVITY.hash(this.name);
    }

    @Override
    public boolean equals(final Object other) {
        return this == other ||
                other instanceof PluginName &&
                        this.equals0(Cast.to(other));
    }

    private boolean equals0(final PluginName other) {
        return this.compareTo(other) == 0;
    }

    @Override
    public String toString() {
        return this.name;
    }

    // Json.............................................................................................................

    static PluginName unmarshall(final JsonNode node,
                                 final JsonNodeUnmarshallContext context) {
        return with(node.stringOrFail());
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.toString());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(PluginName.class),
                PluginName::unmarshall,
                PluginName::marshall,
                PluginName.class
        );
    }
}
