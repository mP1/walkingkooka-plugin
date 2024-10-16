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
import walkingkooka.collect.set.Sets;
import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.reflect.JavaVisibility;
import walkingkooka.text.cursor.TextCursors;
import walkingkooka.text.cursor.parser.ParserContexts;

public final class PluginHelperTestingTest implements PluginHelperTesting<TestPluginHelper, StringName, TestPluginInfo, TestPluginInfoSet, TestPluginSelector, TestPluginAlias, TestPluginAliasSet> {

    @Override
    public void testTestNaming() {
        throw new UnsupportedOperationException();
    }

    @Test
    public void testName() {
        final String text = "A1";

        this.nameAndCheck(
                text,
                Names.string(text)
        );
    }

    @Test
    public void testParseNameFails() {
        final String text = "A1";

        this.parseNameAndCheck(
                TextCursors.charSequence(text),
                ParserContexts.fake()
        );
    }

    @Test
    public void testParseName() {
        final String text = "a1";

        this.parseNameAndCheck(
                TextCursors.charSequence(text),
                ParserContexts.fake(),
                Names.string(text)
        );
    }

    @Test
    public void testParseInfo() {
        final AbsoluteUrl url = Url.parseAbsolute("https://example.com/");
        final StringName name = Names.string("A1");

        this.parseInfoAndCheck(
                url + " " + name,
                new TestPluginInfo(
                        url,
                        name
                )
        );
    }

    @Test
    public void testInfoSet() {
        final TestPluginInfo info1 = TestPluginInfo.parse("https://example.com/1 name1");
        final TestPluginInfo info2 = TestPluginInfo.parse("https://example.com/2 name2");

        this.infoSetAndCheck(
                Sets.of(
                        info1,
                        info2
                ),
                new TestPluginInfoSet(
                        Sets.of(
                                info1,
                                info2
                        )
                )
        );
    }

    @Test
    public void testParseSelector() {
        final String text = "a1(\"Hello\")";

        this.parseSelectorAndCheck(
                text,
                TestPluginSelector.parse(text)
        );
    }

    @Override
    public TestPluginHelper createPluginHelper() {
        return TestPluginHelper.INSTANCE;
    }

    @Override
    public StringName createName() {
        return Names.string("Hello");
    }

    // class............................................................................................................

    @Override
    public Class<TestPluginHelper> type() {
        return TestPluginHelper.class;
    }

    @Override
    public JavaVisibility typeVisibility() {
        return JavaVisibility.PACKAGE_PRIVATE;
    }
}
