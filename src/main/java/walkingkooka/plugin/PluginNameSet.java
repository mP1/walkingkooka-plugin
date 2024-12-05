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

import walkingkooka.collect.set.ImmutableSortedSetDefaults;
import walkingkooka.collect.set.SortedSets;
import walkingkooka.text.CharacterConstant;
import walkingkooka.text.HasText;
import walkingkooka.text.printer.IndentingPrinter;
import walkingkooka.text.printer.TreePrintable;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeMarshallContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.SortedSet;

/**
 * An immutable {@link SortedSet} of {@link PluginName}.
 */
public final class PluginNameSet extends AbstractSet<PluginName>
        implements ImmutableSortedSetDefaults<PluginNameSet, PluginName>,
        HasText,
        TreePrintable {

    public static PluginNameSet parse(final String text) {
        Objects.requireNonNull(text, "text");

        final SortedSet<PluginName> names = SortedSets.tree();

        final PluginNameSetParser parser = PluginNameSetParser.with(
                text
        );

        parser.spaces();

        if (parser.isNotEmpty()) {
            for (; ; ) {
                parser.spaces();

                names.add(
                        PluginName.with(
                                parser.name()
                        )
                );

                parser.spaces();

                if (SEPARATOR.string().equals(parser.comma())) {
                    continue;
                }

                if (parser.isEmpty()) {
                    break;
                }

                parser.invalidCharacterException();
            }
        }

        return withCopy(names);
    }

    /**
     * Empty constant
     */
    public final static PluginNameSet EMPTY = new PluginNameSet(SortedSets.empty());

    /**
     * The character that separates multiple {@link  PluginName}.
     */
    private final static CharacterConstant SEPARATOR = CharacterConstant.COMMA;

    /**
     * Factory that creates a {@link PluginNameSet} after taking a copy.
     */
    public static PluginNameSet with(final SortedSet<PluginName> names) {
        Objects.requireNonNull(names, "names");

        return withCopy(
                SortedSets.immutable(names)
        );
    }

    private static PluginNameSet withCopy(final SortedSet<PluginName> names) {
        return names.isEmpty() ?
                EMPTY :
                new PluginNameSet(names);
    }

    // @VisibleForTesting
    PluginNameSet(final SortedSet<PluginName> names) {
        this.names = names;
    }

    // ImmutableSortedSet...............................................................................................

    @Override
    public Iterator<PluginName> iterator() {
        return this.names.iterator();
    }

    @Override
    public int size() {
        return this.names.size();
    }

    @Override
    public Comparator<PluginName> comparator() {
        return (Comparator<PluginName>)
                this.names.comparator();
    }

    @Override
    public PluginNameSet subSet(final PluginName from,
                                final PluginName to) {
        return withCopy(
                this.names.subSet(
                        from,
                        to
                )
        );
    }

    @Override
    public PluginNameSet headSet(final PluginName alias) {
        return withCopy(
                this.names.headSet(alias)
        );
    }

    @Override
    public PluginNameSet tailSet(final PluginName alias) {
        return withCopy(
                this.names.tailSet(alias)
        );
    }

    @Override
    public PluginName first() {
        return this.names.first();
    }

    @Override
    public PluginName last() {
        return this.names.last();
    }

    @Override
    public SortedSet<PluginName> toSet() {
        final SortedSet<PluginName> names = SortedSets.tree();
        names.addAll(this.names);
        return names;
    }

    @Override
    public PluginNameSet setElements(final SortedSet<PluginName> names) {
        final PluginNameSet copy = with(names);

        return this.equals(copy) ?
                this :
                copy;
    }

    private final SortedSet<PluginName> names;

    // HasText..........................................................................................................

    @Override
    public String text() {
        return SEPARATOR.toSeparatedString(
                this,
                Object::toString
        );
    }

    // TreePrintable....................................................................................................

    @Override
    public void printTree(final IndentingPrinter printer) {
        for (final PluginName name : this) {
            TreePrintable.printTreeOrToString(
                    name,
                    printer
            );
            printer.lineStart();
        }
    }


    // json.............................................................................................................

    static PluginNameSet unmarshall(final JsonNode node,
                                    final JsonNodeUnmarshallContext context) {
        return parse(
                node.stringOrFail()
        );
    }

    private JsonNode marshall(final JsonNodeMarshallContext context) {
        return JsonNode.string(this.text());
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(PluginNameSet.class),
                PluginNameSet::unmarshall,
                PluginNameSet::marshall,
                PluginNameSet.class
        );
    }
}
