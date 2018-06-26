package se.dandel.tools.dbanalyzer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlantUMLWriter {
    private static final Comparator<Column> COLUMN_COMPARATOR = Comparator.comparingInt(Column::getOrdinalPosition);

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public void write(Settings settings, Database database) throws Exception {
        LOGGER.debug("Writing output to {}", settings.getOutputFilename());
        writePlantUML(settings, database);
        settings.getPrintWriter().flush();
    }

    private void writePlantUML(Settings settings, Database database) {
        LOGGER.debug("Write plantuml to {}", database);
        settings.getPrintWriter().println("@startuml");
        writeLegend(settings);
        writeTableAndColumns(settings, database);
        writeRelations(settings, database);
        settings.getPrintWriter().println("@enduml");
    }

    private void writeLegend(Settings settings) {
        settings.getPrintWriter().println("legend top");
        settings.getPrintWriter().println("Separata sektioner för PK, FK, vanliga kolumner och tekniska kolumner");
        settings.getPrintWriter().println("<i>Kursiv</i> stil för syntetiska nycklar");
        settings.getPrintWriter().println("<b>Fet</b> stil för NOT NULL");
        settings.getPrintWriter().println("endlegend");
    }

    private void writeRelations(Settings settings, Database database) {
        for (Table table : database.getTables()) {
            for (ForeignKey key : table.getForeignKeys()) {
                settings.getPrintWriter().println(table.getName() + " --> " + key.getFkTable().getName() + " : " + key.getFkName());
            }
        }
    }

    private void writeTableAndColumns(Settings settings, Database database) {
        LOGGER.debug("Writing tables and columns");
        for (Table table : database.getTables()) {
            LOGGER.debug("Writing table {}", table.getName());
            settings.getPrintWriter().println("class " + table.getName() + " {");
            for (Column column : getSortedPkColumns(table)) {
                settings.getPrintWriter().println(getIndent(1) + "<i>" + getColumnString(column) + "</i>");
            }

            Column dtype = table.getColumn(settings.getDiscriminatorColumn());
            if (dtype != null) {
                settings.getPrintWriter().println(getIndent(1) + "--");
                settings.getPrintWriter().println(getIndent(1) + getColumnString(dtype));
            }

            List<Column> fkColumns = getSortedFkColumns(table);
            if (!fkColumns.isEmpty()) {
                settings.getPrintWriter().println(getIndent(1) + "--");
                for (Column column : fkColumns) {
                    settings.getPrintWriter().println(getIndent(1) + getColumnString(column));
                }
            }

            List<Column> functionalColumns = getSortedFunctionalColumns(settings, table);
            if (!functionalColumns.isEmpty()) {
                settings.getPrintWriter().println(getIndent(1) + "--");
                for (Column column : functionalColumns) {
                    settings.getPrintWriter().println(getIndent(1) + getColumnString(column));
                }
            }

            List<Column> technicalColumns = getSortedTechColumns(settings, table);
            if (!technicalColumns.isEmpty()) {
                settings.getPrintWriter().println(getIndent(1) + "--");
                for (Column column : technicalColumns) {
                    settings.getPrintWriter().println(getIndent(1) + getColumnString(column));
                }
            }
            settings.getPrintWriter().println("}");
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

    private List<Column> getSortedTechColumns(Settings settings, Table table) {
        List<Column> columns = new ArrayList<>(table.getOrdinaryColumns());
        List<Column> technicalColumns = getTechnicalColumns(settings, columns);
        technicalColumns.sort(COLUMN_COMPARATOR);
        return technicalColumns;
    }

    private List<Column> getSortedFunctionalColumns(Settings settings, Table table) {
        List<Column> columns = new ArrayList<>(table.getOrdinaryColumns());
        List<Column> functionalColumns = getFunctionalColumns(settings, columns);
        functionalColumns.sort(COLUMN_COMPARATOR);
        return functionalColumns;
    }

    private List<Column> getSortedFkColumns(Table table) {
        List<Column> fkColumns = new ArrayList<>(table.getFkColumns());
        fkColumns.sort(COLUMN_COMPARATOR);
        return fkColumns;
    }

    private List<Column> getSortedPkColumns(Table table) {
        List<Column> columns = new ArrayList<>(table.getPkColumns());
        columns.sort(COLUMN_COMPARATOR);
        return columns;
    }

    private List<Column> getTechnicalColumns(Settings settings, List<Column> columns) {
        List<Column> list = new ArrayList<>();
        if (settings.getTechnicalColumns() != null) {
            for (Column column : columns) {
                if (settings.getTechnicalColumns().contains(column.getName())) {
                    list.add(column);
                }
            }
        }
        return list;
    }

    private List<Column> getFunctionalColumns(Settings settings, List<Column> columns) {
        List<Column> list = new ArrayList<>();
        if (settings.getTechnicalColumns() != null) {
            for (Column column : columns) {
                if (!settings.getTechnicalColumns().contains(column.getName())) {
                    list.add(column);
                }
            }
        }
        return list;
    }

    private String getIndent(int indent) {
        return StringUtils.repeat("  ", null, indent);
    }

}
