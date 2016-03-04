package se.dandel.tools.dbanalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Table {
    private final String name;
    private Map<String, Column> columns = new HashMap<String, Column>();
    private Collection<ForeignKey> foreignKeys = new ArrayList<ForeignKey>();

    public Table(String name) {
        this.name = name;
    }

    public Collection<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void addImportedKey(Table fkTable, String fkName) {
        foreignKeys.add(new ForeignKey(fkTable, fkName));

    }

    public Column getColumn(String columnName) {
        return columns.get(columnName);
    }

    public String getName() {
        return name;
    }

    public Column addColumn(String name) {
        Column c = new Column(this, name);
        columns.put(name, c);
        return c;
    }

    public Collection<Column> getPkColumns() {
        List<Column> list = new ArrayList<Column>();
        for (Column column : columns.values()) {
            if (column.isPk()) {
                list.add(column);
            }
        }
        return list;
    }

    public Collection<Column> getFkColumns() {
        List<Column> list = new ArrayList<Column>();
        for (Column column : columns.values()) {
            if (column.isFk()) {
                list.add(column);
            }
        }
        return list;
    }

    public Collection<Column> getOrdinaryColumns() {
        List<Column> list = new ArrayList<Column>();
        for (Column column : columns.values()) {
            if (!column.isPk() && !column.isFk() && !"DTYPE".equals(column.getName())) {
                list.add(column);
            }
        }
        return list;
    }

}