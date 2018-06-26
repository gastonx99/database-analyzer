package se.dandel.tools.dbanalyzer;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}