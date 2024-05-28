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

import walkingkooka.net.AbsoluteUrl;

import java.util.Optional;
import java.util.Set;

public class FakePluginProvider implements PluginProvider {
    @Override
    public <T> Optional<T> plugin(final PluginName name,
                                  final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> Set<T> plugins(final Class<T> type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<PluginInfo> pluginInfos() {
        throw new UnsupportedOperationException();
    }

    @Override
    public PluginProviderName name() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AbsoluteUrl url() {
        throw new UnsupportedOperationException();
    }
}
