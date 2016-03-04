package se.dandel.tools.dbanalyzer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public class PlantUMLWriter {
    private static final Comparator<Column> COLUMN_COMPARATOR = new Comparator<Column>() {
        public int compare(Column o1, Column o2) {
            return Integer.compare(o1.getOrdinalPosition(), o2.getOrdinalPosition());
        }
    };

    @Inject
    private Settings settings;

    private ThreadLocal<Context> context = new ThreadLocal<Context>();

    private static class Context {
        PrintWriter pw;

        Context(String outputFilename) throws Exception {
            pw = new PrintWriter(outputFilename);
        }
    }

    public void write(Database database) throws Exception {
        context.set(new Context(settings.getOutputFilename()));
        try {
            writePlantUML(database);
        } finally {
            IOUtils.closeQuietly(context.get().pw);
            context.set(null);
        }
    }

    private void writePlantUML(Database database) {
        pw().println("@startuml");
        writeLegend();
        writeTableAndColumns(database);
        writeRelations(database);
        pw().println("@enduml");
    }

    private void writeLegend() {
        pw().println("legend top");
        pw().println("Separata sektioner för PK, FK, vanliga kolumner och tekniska kolumner");
        pw().println("<i>Kursiv</i> stil för syntetiska nycklar");
        pw().println("<b>Fet</b> stil för NOT NULL");
        pw().println("endlegend");
    }

    private void writeRelations(Database database) {
        for (Table table : database.getTables()) {
            for (ForeignKey key : table.getForeignKeys()) {
                pw().println(table.getName() + " --> " + key.getFkTable().getName() + " : " + key.getFkName());
            }
        }
    }

    private void writeTableAndColumns(Database database) {
        for (Table table : database.getTables()) {
            pw().println("class " + table.getName() + " {");
            for (Column column : getSortedPkColumns(table)) {
                pw().println(getIndent(1) + "<i>" + getColumnString(column) + "</i>");
            }

            Column dtype = table.getColumn(settings.getDiscriminatorColumn());
            if (dtype != null) {
                pw().println(getIndent(1) + "--");
                pw().println(getIndent(1) + getColumnString(dtype));
            }

            List<Column> fkColumns = getSortedFkColumns(table);
            if (!fkColumns.isEmpty()) {
                pw().println(getIndent(1) + "--");
                for (Column column : fkColumns) {
                    pw().println(getIndent(1) + getColumnString(column));
                }
            }

            List<Column> functionalColumns = getSortedFunctionalColumns(table);
            if (!functionalColumns.isEmpty()) {
                pw().println(getIndent(1) + "--");
                for (Column column : functionalColumns) {
                    pw().println(getIndent(1) + getColumnString(column));
                }
            }

            List<Column> technicalColumns = getSortedTechColumns(table);
            if (!technicalColumns.isEmpty()) {
                pw().println(getIndent(1) + "--");
                for (Column column : technicalColumns) {
                    pw().println(getIndent(1) + getColumnString(column));
                }
            }
            pw().println("}");
        }
    }

    private String getColumnString(Column column) {
        String name = column.getName();
        String datatype = column.getDatatype();
        if (!column.isNullable()) {
            name = "<b>" + name + "</b>";
        }
        return name + " : " + datatype;
    }

    private List<Column> getSortedTechColumns(Table table) {
        List<Column> columns = new ArrayList<Column>(table.getOrdinaryColumns());
        List<Column> technicalColumns = getTechnicalColumns(columns);
        Collections.sort(technicalColumns, COLUMN_COMPARATOR);
        return technicalColumns;
    }

    private List<Column> getSortedFunctionalColumns(Table table) {
        List<Column> xcolumns = new ArrayList<Column>(table.getOrdinaryColumns());
        List<Column> functionalColumns = getFunctionalColumns(xcolumns);
        Collections.sort(functionalColumns, COLUMN_COMPARATOR);
        return functionalColumns;
    }

    private List<Column> getSortedFkColumns(Table table) {
        List<Column> fkColumns = new ArrayList<Column>(table.getFkColumns());
        Collections.sort(fkColumns, COLUMN_COMPARATOR);
        return fkColumns;
    }

    private List<Column> getSortedPkColumns(Table table) {
        List<Column> columns = new ArrayList<Column>(table.getPkColumns());
        Collections.sort(columns, COLUMN_COMPARATOR);
        return columns;
    }

    private PrintWriter pw() {
        return context.get().pw;
    }

    private List<Column> getTechnicalColumns(List<Column> columns) {
        List<Column> list = new ArrayList<Column>();
        for (Column column : columns) {
            if (settings.getTechnicalColumns().contains(column.getName())) {
                list.add(column);
            }
        }
        return list;
    }

    private List<Column> getFunctionalColumns(List<Column> columns) {
        List<Column> list = new ArrayList<Column>();
        for (Column column : columns) {
            if (!settings.getTechnicalColumns().contains(column.getName())) {
                list.add(column);
            }
        }
        return list;
    }

    private String getIndent(int indent) {
        return StringUtils.repeat("  ", null, indent);
    }

}
