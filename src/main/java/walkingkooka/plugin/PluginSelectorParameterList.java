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

import walkingkooka.collect.list.ImmutableList;
import walkingkooka.collect.list.ImmutableListDefaults;
import walkingkooka.naming.Name;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * An {@link ImmutableList} that contains parameters parsed from a {@link PluginSelector}.
 * The {@link #get(int)} throws a {@link ArrayIndexOutOfBoundsException} with better messages mentioning the plugin and message.
 */
final class PluginSelectorParameterList extends AbstractList<Object>
    implements ImmutableListDefaults<PluginSelectorParameterList, Object> {

    static PluginSelectorParameterList with(final List<Object> values,
                                            final Name name) {
        return new PluginSelectorParameterList(
            values,
            name
        );
    }

    private PluginSelectorParameterList(final List<Object> values,
                                        final Name name) {
        this.values = values;
        this.name = name;
    }

    @Override
    public Object get(final int index) {
        final List<Object> values = this.values;

        final int count = values.size();
        if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(this.name + ": Invalid parameter " + index);
        }
        if (index >= count) {
            throw new ArrayIndexOutOfBoundsException(this.name + ": Missing parameter " + index);
        }
        return values.get(index);
    }

    private final Name name;

    @Override
    public int size() {
        return this.values.size();
    }

    private final List<Object> values;

    @Override
    public PluginSelectorParameterList setElements(final Collection<Object> values) {
        Objects.requireNonNull(values, "values");
        throw new UnsupportedOperationException();
    }

    @Override
    public void elementCheck(final Object value) {
        // nulls are allowed
    }
}
