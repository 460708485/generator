package generator.tblloader;

import generator.util.PropertyFileReader;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnection {

    private static Properties property = null;

    static {
        if (null == property) {
            property = PropertyFileReader.getProperties("config.jdbc");
        }
        try {
            DriverManager.registerDriver((Driver)Class.forName(property
                .getProperty("jdbc.driver")).newInstance());
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        
    }

    private DBConnection() {

    }

    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(property
            .getProperty("jdbc.url"), property.getProperty("jdbc.username"),
            property.getProperty("jdbc.password"));
        
        return conn;
    }
}
