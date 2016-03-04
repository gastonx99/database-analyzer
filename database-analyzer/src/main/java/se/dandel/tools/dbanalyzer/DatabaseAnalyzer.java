package se.dandel.tools.dbanalyzer;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;

import liquibase.Liquibase;
import liquibase.database.DatabaseConnection;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

public class DatabaseAnalyzer {

    @Inject
    private PlantUMLWriter writer;

    @Inject
    private Settings settings;

    private DatabaseAnalyzer() {
        // Create instance using static methods
    }

    public void analyze() throws Exception {
        Database database = analyzeDatabase();
        writer.write(database);
    }

    private Database analyzeDatabase() throws ClassNotFoundException, SQLException, LiquibaseException {
        Connection connection = getConnection();
        if (StringUtils.isNotEmpty(settings.getLiquibaseChangelog())) {
            runLiquibase(connection);
        }

        DatabaseMetaData metaData = connection.getMetaData();
        Database database = new Database();
        analyzeTables(metaData, database);
        analyzeColumns(metaData, database);
        analyzePrimaryKeys(metaData, database);
        analyzeForeignKeys(metaData, database);
        return database;
    }

    private void analyzeForeignKeys(DatabaseMetaData metaData, Database database) throws SQLException {
        for (Table table : database.getTables()) {
            ResultSet resultSet = metaData.getImportedKeys(null, null, table.getName());
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

    private void analyzePrimaryKeys(DatabaseMetaData metaData, Database database) throws SQLException {
        for (Table table : database.getTables()) {
            ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, table.getName());
            while (primaryKeys.next()) {
                table.getColumn(primaryKeys.getString("COLUMN_NAME")).markPk();
            }
        }
    }

    private void analyzeColumns(DatabaseMetaData metaData, Database database) throws SQLException {
        for (Table table : database.getTables()) {
            ResultSet columns = metaData.getColumns(null, null, table.getName(), null);
            while (columns.next()) {
                Column column = table.addColumn(columns.getString("COLUMN_NAME"));
                column.setDatatype(columns.getString("TYPE_NAME"));
                column.setNullable(columns.getInt("NULLABLE") == DatabaseMetaData.columnNullable);
                column.setOrdinalPosition(columns.getInt("ORDINAL_POSITION"));
            }
        }
    }

    private void analyzeTables(DatabaseMetaData metaData, Database database) throws SQLException {
        ResultSet rsTables = metaData.getTables(null, null, settings.getTablenamePattern(), null);
        while (rsTables.next()) {
            String tablename = rsTables.getString("TABLE_NAME");
            database.addTable(tablename);
        }
    }

    private void runLiquibase(Connection c) throws LiquibaseException {
        ResourceAccessor resourceAccessor = new ClassLoaderResourceAccessor();
        DatabaseConnection dbConn = new JdbcConnection(c);
        Liquibase liquibase = new Liquibase(settings.getLiquibaseChangelog(), resourceAccessor, dbConn);
        liquibase.update((String) null);
    }

    private Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(settings.getJdbcDriver());
        return DriverManager.getConnection(settings.getJdbcUrl());
    }

    public static DatabaseAnalyzer newInjectedInstance(final Settings settings) {
        DatabaseAnalyzer analyzer = new DatabaseAnalyzer();

        AbstractModule module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(Settings.class).toInstance(settings);
            }
        };
        Guice.createInjector(module).injectMembers(analyzer);
        return analyzer;
    }

}
