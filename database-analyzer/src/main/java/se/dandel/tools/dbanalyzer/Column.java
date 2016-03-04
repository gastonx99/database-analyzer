package se.dandel.tools.dbanalyzer;

public class Column {
    private final Table table;
    private final String name;
    private boolean pk;
    private boolean fk;
    private boolean nullable;
    private String datatype;
    private int ordinalPosition;

    public Column(Table table, String name) {
        this.table = table;
        this.name = name;
    }

    public void setOrdinalPosition(int ordinalPosition) {
        this.ordinalPosition = ordinalPosition;
    }

    public int getOrdinalPosition() {
        return ordinalPosition;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public void markPk() {
        pk = true;
    }

    public void markFk() {
        fk = true;
    }

    public Table getTable() {
        return table;
    }

    public String getName() {
        return name;
    }

    public boolean isPk() {
        return pk;
    }

    public boolean isFk() {
        return fk;
    }
}