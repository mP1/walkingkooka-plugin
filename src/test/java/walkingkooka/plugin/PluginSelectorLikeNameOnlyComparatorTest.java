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
import walkingkooka.Cast;
import walkingkooka.collect.list.Lists;
import walkingkooka.compare.ComparatorTesting2;
import walkingkooka.naming.StringName;
import walkingkooka.reflect.ClassTesting2;
import walkingkooka.reflect.JavaVisibility;

import java.util.ArrayList;
import java.util.TreeSet;

public final class PluginSelectorLikeNameOnlyComparatorTest implements ComparatorTesting2<PluginSelectorLikeNameOnlyComparator<TestPluginSelector, StringName>, TestPluginSelector>,
    ClassTesting2<PluginSelectorLikeNameOnlyComparator<TestPluginSelector, StringName>> {

    @Test
    public void testTreeSet() {
        final TestPluginSelector apple = new TestPluginSelector("apple");
        final TestPluginSelector banana = new TestPluginSelector("banana");
        final TestPluginSelector carrot = new TestPluginSelector("carrot");
        final TestPluginSelector dog = new TestPluginSelector("dog");

        final TreeSet<TestPluginSelector> treeSet = new TreeSet<>(PluginSelectorLikeNameOnlyComparator.instance());

        treeSet.add(apple);
        treeSet.add(dog);
        treeSet.add(carrot);
        treeSet.add(banana);

        this.checkEquals(
            Lists.of(
                apple,
                banana,
                carrot,
                dog
            ),
            new ArrayList<>(treeSet)
        );
    }

    @Override
    public PluginSelectorLikeNameOnlyComparator<TestPluginSelector, StringName> createComparator() {
        return PluginSelectorLikeNameOnlyComparator.instance();
    }

    // class............................................................................................................

    @Override
    public Class<PluginSelectorLikeNameOnlyComparator<TestPluginSelector, StringName>> type() {
        return Cast.to(PluginSelectorLikeNameOnlyComparator.class);
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
