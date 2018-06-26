package se.dandel.tools.dbanalyzer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Database {

    private Map<String, Table> tables = new HashMap<String, Table>();

    public Table addTable(String name) {
        Table table = new Table(name);
        tables.put(name, table);
        return table;
    }

    public Collection<Table> getTables() {
        return tables.values();
    }

    public Table getTable(String name) {
        return tables.get(name);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
