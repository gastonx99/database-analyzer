package se.dandel.tools.dbanalyzer;

import java.util.ArrayList;
import java.util.List;

public class Index {
    private final Table table;
    private final String name;
    private boolean unique;

    private List<Column> columns = new ArrayList<Column>();

    public Index(Table table, String name) {
        this.table = table;
        this.name = name;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public boolean isUnique() {
        return unique;
    }

    public void addColumn(Column c) {
        this.columns.add(c);
    }

    public List<Column> getColumns() {
        return columns;
    }

}