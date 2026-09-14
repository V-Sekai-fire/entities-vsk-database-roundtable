// A one-shot JDBC Driver wrapper that loads the FoundationDB-VFS SQLite
// extension the first time YCSB's jdbc binding asks for a connection.
// The extension registers a VFS named "fdb_vfs"; the URL YCSB opens
// after that names the VFS through the standard ?vfs= URI parameter.
//
// Registered under jdbc:weftsqlite:... so the sqlite-jdbc org.sqlite.JDBC
// driver auto-registration and this one do not race for the same URL
// prefix; the roundtable workflow points db.url at the weftsqlite prefix.

package weftspun;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class WeftSqliteDriver implements java.sql.Driver {

	static final String PREFIX = "jdbc:weftsqlite:";
	static final String EXTENSION_PATH_PROPERTY = "weftspun.sqlite.extension";
	static final String EXTENSION_ENTRY = "sqlite3_weftfdbvfs_init";
	static volatile boolean loaded = false;

	static {
		try {
			Class.forName("org.sqlite.JDBC");
			DriverManager.registerDriver(new WeftSqliteDriver());
		} catch (ClassNotFoundException | SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Connection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) {
			return null;
		}
		String sqliteUrl = "jdbc:sqlite:" + url.substring(PREFIX.length());
		Properties merged = new Properties(info);
		merged.setProperty("enable_load_extension", "true");
		Connection conn = DriverManager.getConnection(sqliteUrl, merged);
		if (!loaded) {
			synchronized (WeftSqliteDriver.class) {
				if (!loaded) {
					String path = System.getProperty(
							EXTENSION_PATH_PROPERTY,
							"/usr/local/lib/libweft_fdb_vfs_ext");
					try (Statement st = conn.createStatement()) {
						st.execute("SELECT load_extension('" + path + "', '" + EXTENSION_ENTRY + "')");
					}
					loaded = true;
				}
			}
		}
		return conn;
	}

	public boolean acceptsURL(String url) {
		return url != null && url.startsWith(PREFIX);
	}

	public java.sql.DriverPropertyInfo[] getPropertyInfo(String url, Properties info) {
		return new java.sql.DriverPropertyInfo[0];
	}

	public int getMajorVersion() { return 1; }
	public int getMinorVersion() { return 0; }
	public boolean jdbcCompliant() { return false; }

	public java.util.logging.Logger getParentLogger() {
		return java.util.logging.Logger.getLogger("weftspun");
	}
}
