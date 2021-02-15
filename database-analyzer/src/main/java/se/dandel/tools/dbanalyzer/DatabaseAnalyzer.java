package se.dandel.tools.dbanalyzer;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.sql.*;

public class DatabaseAnalyzer {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Inject
    private PlantUMLWriter writer;

    public void analyze(Settings settings) throws Exception {
        LOGGER.debug("Analyzing using settings {}", settings);
        Database database = analyzeDatabase(settings);
        writer.write(settings, database);
    }

    private Database analyzeDatabase(Settings settings) throws ClassNotFoundException, SQLException, LiquibaseException {
        Connection connection = getConnection(settings);
        if (StringUtils.isNotEmpty(settings.getLiquibaseChangelog())) {
            runLiquibase(settings, connection);
        }

        DatabaseMetaData metaData = connection.getMetaData();
        Database database = new Database();
        analyzeTables(settings, metaData, database);
        analyzeColumns(settings, metaData, database);
        analyzePrimaryKeys(settings, metaData, database);
        analyzeForeignKeys(settings, metaData, database);
        return database;
    }

    private void analyzeForeignKeys(Settings settings, DatabaseMetaData metaData, Database database) throws SQLException {
        for (Table table : database.getTables()) {
            try (ResultSet resultSet = metaData.getImportedKeys(settings.getCatalogueName(), settings.getSchemaName(), table.getName())) {
                while (resultSet.next()) {
                    String pkTableName = resultSet.getString("PKTABLE_NAME");
                    // String pkColumnName = resultSet.getString("PKCOLUMN_NAME");
                    String fkTableName = resultSet.getString("FKTABLE_NAME");
                    String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
                    String fkName = resultSet.getString("FK_NAME");
                    table.addImportedKey(database.getTable(pkTableName), fkName);
                    database.getTable(fkTableName).getColumn(fkColumnName).markFk();
                }
            }
        }
    }

    private void analyzePrimaryKeys(Settings settings, DatabaseMetaData metaData, Database database) throws SQLException {
        for (Table table : database.getTables()) {
            try (ResultSet primaryKeys = metaData.getPrimaryKeys(settings.getCatalogueName(), settings.getSchemaName(), table.getName())) {
                while (primaryKeys.next()) {
                    table.getColumn(primaryKeys.getString("COLUMN_NAME")).markPk();
                }
            }
        }
    }

    private void analyzeColumns(Settings settings, DatabaseMetaData metaData, Database database) throws SQLException {
        for (Table table : database.getTables()) {
            try (ResultSet columns = metaData.getColumns(settings.getCatalogueName(), settings.getSchemaName(), table.getName(), null)) {
                while (columns.next()) {
                    Column column = table.addColumn(columns.getString("COLUMN_NAME"));
                    column.setDatatype(columns.getString("TYPE_NAME"));
                    column.setNullable(columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                    column.setOrdinalPosition(columns.getInt("ORDINAL_POSITION"));
                }
            }
        }
    }

    private void analyzeTables(Settings settings, DatabaseMetaData metaData, Database database) throws SQLException {
        LOGGER.debug("Analyzing tables using catalog {}, schemapattern {}, tablepattern {} and types {}", null, null, settings.getTablenamePattern(), null);
        try (ResultSet rsTables = metaData.getTables(settings.getCatalogueName(), settings.getSchemaName(), settings.getTablenamePattern(), null)) {
            while (rsTables.next()) {
                String tablename = rsTables.getString("TABLE_NAME");
                LOGGER.debug("Analyzing table {}", tablename);
                database.addTable(tablename);
            }
        }
        LOGGER.debug("Done analyzing tables");
    }

    private void runLiquibase(Settings settings, Connection c) throws LiquibaseException {
        ResourceAccessor resourceAccessor = new FileSystemResourceAccessor();
        DatabaseConnection dbConn = new JdbcConnection(c);
        Liquibase liquibase = new Liquibase(settings.getLiquibaseChangelog(), resourceAccessor, dbConn);
        liquibase.update((String) null);
    }

    private Connection getConnection(Settings settings) throws ClassNotFoundException, SQLException {
        Class.forName(settings.getDriver());
        return DriverManager.getConnection(settings.getUrl(), settings.getUser(), settings.getPassword());
    }

}
