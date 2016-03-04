package se.dandel.tools.dbanalyzer;

public class ForeignKey {

    private Table fkTable;
    private String fkName;

    public ForeignKey(Table fkTable, String fkName) {
        this.fkTable = fkTable;
        this.fkName = fkName;
    }

    public Table getFkTable() {
        return fkTable;
    }

    public String getFkName() {
        return fkName;
    }
}