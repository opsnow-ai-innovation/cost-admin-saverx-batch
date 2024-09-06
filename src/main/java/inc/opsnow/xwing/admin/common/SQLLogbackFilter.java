package inc.opsnow.xwing.admin.common;

import io.quarkus.logging.LoggingFilter;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

@LoggingFilter(name = "sql-filter")
public class SQLLogbackFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
        return record.getMessage().contains("[sql]") || record.getMessage().contains("SQL: ");
    }
}
