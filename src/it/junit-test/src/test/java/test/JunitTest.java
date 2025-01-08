/*
 * Copyright © 2024 Miroslav Pokorny
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
 */
package test;


import com.google.j2cl.junit.apt.J2clTestInput;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;

import walkingkooka.collect.list.Lists;
import walkingkooka.plugin.PluginName;

@J2clTestInput(JunitTest.class)
public class JunitTest {
    @Test
    public void testMetadataNonLocaleDefaults() {
        Assert.assertEquals(1, 3 - 2);
    }

    @Test
    public void testPluginNameSort() {
        final PluginName a = PluginName.with("STRING");
        final PluginName b = PluginName.with("date-of-month");
        final PluginName c = PluginName.with("text-case-insensitive");
        final PluginName d = PluginName.with("month-of-year");

        final List<PluginName> list = Lists.array();
        list.add(a);
        list.add(b);
        list.add(c);
        list.add(d);
        list.sort(Comparator.naturalOrder());

        Assert.assertEquals(
            Lists.of(a, b, d, c),
            list
        );
    }
}

