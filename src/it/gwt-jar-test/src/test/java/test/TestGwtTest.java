package test;

import com.google.gwt.junit.client.GWTTestCase;

import java.util.Comparator;
import java.util.List;

import walkingkooka.collect.list.Lists;
import walkingkooka.j2cl.locale.LocaleAware;
import walkingkooka.plugin.PluginName;

@LocaleAware
public class TestGwtTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "test.Test";
    }

    public void testAssertEquals() {
        assertEquals(
            1,
            1
        );
    }

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

        assertEquals(
            Lists.of(a, b, d, c),
            list
        );
    }
}
