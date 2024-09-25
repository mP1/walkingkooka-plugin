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

import walkingkooka.naming.Names;
import walkingkooka.naming.StringName;
import walkingkooka.net.AbsoluteUrl;
import walkingkooka.net.Url;
import walkingkooka.tree.json.JsonNode;
import walkingkooka.tree.json.marshall.JsonNodeContext;
import walkingkooka.tree.json.marshall.JsonNodeUnmarshallContext;

import java.util.Objects;

public class TestPluginInfo implements PluginInfoLike<TestPluginInfo, StringName> {

    public static TestPluginInfo parse(final String text) {
        return PluginInfoLike.parse(
                text,
                Names::string,
                TestPluginInfo::new
        );
    }

    TestPluginInfo(final AbsoluteUrl url,
                   final StringName name) {
        this.url = url;
        this.name = name;
    }

    TestPluginInfo(final String url,
                   final String name) {
        this(
                Url.parseAbsolute(url),
                Names.string(name)
        );
    }

    @Override
    public StringName name() {
        return this.name;
    }

    @Override
    public TestPluginInfo setName(final StringName name) {
        Objects.requireNonNull(name, "name");

        return this.name.equals(name) ?
                this :
                new TestPluginInfo(
                        this.url,
                        name
                );
    }

    private final StringName name;

    @Override
    public AbsoluteUrl url() {
        return this.url;
    }

    private final AbsoluteUrl url;

    // object.......................................................................................................

    @Override
    public int hashCode() {
        return Objects.hash(
                this.url,
                this.name
        );
    }

    public boolean equals(final Object other) {
        return this == other || other instanceof TestPluginInfo && this.equals0((TestPluginInfo) other);
    }

    private boolean equals0(final TestPluginInfo other) {
        return this.url.equals(other.url) &&
                this.name.equals(other.name);
    }

    @Override
    public String toString() {
        return PluginInfoLike.toString(this);
    }

    // json.........................................................................................................

    // @VisibleForTesting
    static TestPluginInfo unmarshall(final JsonNode node,
                                     final JsonNodeUnmarshallContext context) {
        return PluginInfoLike.unmarshall(
                node,
                context,
                Names::string,
                TestPluginInfo::new
        );
    }

    static {
        JsonNodeContext.register(
                JsonNodeContext.computeTypeName(TestPluginInfo.class),
                TestPluginInfo::unmarshall,
                TestPluginInfo::marshall,
                TestPluginInfo.class
        );
    }
}
