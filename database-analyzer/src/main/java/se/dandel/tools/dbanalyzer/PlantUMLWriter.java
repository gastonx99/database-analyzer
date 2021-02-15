package se.dandel.tools.dbanalyzer;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PlantUMLWriter {
    private static final Comparator<Column> COLUMN_COMPARATOR = Comparator.comparing(Column::getName);

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
        settings.getPrintWriter().println("Separata sektioner för PK, FK, funktionella kolumner och tekniska kolumner");
        settings.getPrintWriter().println("<i>Kursiv stil för NULLABLE</i>");
        settings.getPrintWriter().println("endlegend");
    }

    private void writeRelations(Settings settings, Database database) {
        database.getTables().forEach(t -> writeRelations(settings, t));
    }

    private void writeRelations(Settings settings, Table table) {
        table.getForeignKeys().forEach(fk -> settings.getPrintWriter().println(table.getName() + " --> " + fk.getFkTable().getName() + " : " + fk.getFkName()));
    }

    private void writeTableAndColumns(Settings settings, Database database) {
        LOGGER.debug("Writing tables and columns");
        List<Table> tables = new ArrayList<>(database.getTables());
        Collections.sort(tables, Comparator.comparing(Table::getName));
        tables.forEach(t -> writeTable(settings, t));
    }

    private void writeTable(Settings settings, Table table) {
        LOGGER.debug("Writing table {}", table.getName());
        settings.getPrintWriter().println("class " + table.getName() + " {");
        List<Column> pkColumns = getSortedPkColumns(table);
        if (!pkColumns.isEmpty()) {
            pkColumns.forEach(c -> settings.getPrintWriter().println(getIndent(1) + getColumnString(c)));
        }

        Column dtype = table.getColumn(settings.getDiscriminatorColumn());
        if (dtype != null) {
            settings.getPrintWriter().println(getIndent(1) + "-- Discriminator --");
            settings.getPrintWriter().println(getIndent(1) + getColumnString(dtype));
        }

        List<Column> fkColumns = getSortedFkColumns(table);
        if (!fkColumns.isEmpty()) {
            settings.getPrintWriter().println(getIndent(1) + "-- FK --");
            fkColumns.forEach(c -> settings.getPrintWriter().println(getIndent(1) + getColumnString(c)));
        }

        List<Column> functionalColumns = getSortedFunctionalColumns(settings, table);
        if (!functionalColumns.isEmpty()) {
            settings.getPrintWriter().println(getIndent(1) + "--");
            functionalColumns.forEach(c -> settings.getPrintWriter().println(getIndent(1) + getColumnString(c)));
        }

        List<Column> technicalColumns = getSortedTechColumns(settings, table);
        if (!technicalColumns.isEmpty()) {
            settings.getPrintWriter().println(getIndent(1) + "-- Technical --");
            technicalColumns.forEach(c -> settings.getPrintWriter().println(getIndent(1) + getColumnString(c)));
        }
        settings.getPrintWriter().println("}");
    }

    private String getColumnString(Column column) {
        String name = column.getName();
        String datatype = column.getDatatype();
        if (column.isNullable()) {
            name = "<i>" + name + "</i>";
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
        return columns.stream().filter(c -> settings.getTechnicalColumns().contains(c.getName())).collect(Collectors.toList());
    }

    private List<Column> getFunctionalColumns(Settings settings, List<Column> columns) {
        return columns.stream().filter(c -> !settings.getTechnicalColumns().contains(c.getName())).collect(Collectors.toList());
    }

    private String getIndent(int indent) {
        return StringUtils.repeat("  ", null, indent);
    }

}
