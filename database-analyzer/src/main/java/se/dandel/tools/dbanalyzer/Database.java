package se.dandel.tools.dbanalyzer;

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
}
