package generator.tblloader;

import generator.bean.TableInfoBean;
import generator.util.PropertyFileReader;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

public class TableInfoFetch {

    private static Properties property = null;

    static {
        if (null == property) {
            property = PropertyFileReader.getProperties("config.tbldef");
        }
    }


    public static String[] getTableNames() {
        String tblNames = property.getProperty("tableNames");
        String[] tblNamesArr = new String[0];
        if (StringUtils.isEmpty(tblNames)) {
            String tableNamesDefFile = property.getProperty("tableNamesDefFile");
            if (!StringUtils.isEmpty(tableNamesDefFile)) {
                try {
                    List <?> lines = FileUtils.readLines(new File(tableNamesDefFile), "utf-8");
                    int size = lines.size();
                    tblNamesArr = new String[size];
                    for (int i = 0; i < size; i++) {
                        String line = lines.get(i).toString();
                        int start = line.indexOf("\"") + 1;
                        int end = line.lastIndexOf("\"");
                        tblNamesArr[i] = line.substring(start, end);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            tblNamesArr = tblNames.split(",");
        }

        return tblNamesArr;
    }


    public static Map <String, List <TableInfoBean>> fetchTableInfo() throws SQLException {

        Connection conn = null;
        DatabaseMetaData dbmd = null;
        ResultSet rs = null;

        Map <String, List <TableInfoBean>> infoMap = new LinkedHashMap <String, List <TableInfoBean>>();

        try {
            conn = DBConnection.getConnection();
            dbmd = conn.getMetaData();

            String schema = property.getProperty("schema");
            // String tblNames = property.getProperty("tableNames");
            // String[] tables = tblNames.split(",");
            String[] tables = getTableNames();
            for (int i = 0; i < tables.length; i++) {
                try {
                    rs = dbmd.getTables(null, schema, tables[i], null);
                    while (rs.next()) {
                        String tableName = rs.getString("TABLE_NAME");
                        System.out.println("「" + schema + "/" + tableName + "」");

                        List <String> pkColumns = getPrimaryKeys(schema, tableName, dbmd);

                        getTableInfo(schema, tableName, dbmd, pkColumns, infoMap);
                    }
                } finally {
                    if (null != rs) {
                        rs.close();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (null != conn) {
                conn.close();
            }
        }

        return infoMap;
    }


    private static List <String> getPrimaryKeys(String schema, String tableName, DatabaseMetaData dbmd) throws SQLException {

        ResultSet rs = null;
        List <String> pkColumns = new ArrayList <String>();
        try {
            rs = dbmd.getPrimaryKeys(null, schema, tableName);
            while (rs.next()) {
                pkColumns.add(rs.getString("COLUMN_NAME"));
            }

        } finally {
            if (null != rs) {
                rs.close();
            }
        }

        return pkColumns;
    }


    private static void getTableInfo(String schema, String tableName, DatabaseMetaData dbmd, List <String> pkColumns,
            Map <String, List <TableInfoBean>> infoMap) throws SQLException {

        List <TableInfoBean> tblInfoList = null;
        ResultSet rs = null;
        try {
            tblInfoList = new ArrayList <TableInfoBean>();
            rs = dbmd.getColumns(null, schema, tableName, "%");

            TableInfoBean bean = null;
            while (rs.next()) {
                bean = new TableInfoBean();

                bean.setColumnName(rs.getString("COLUMN_NAME"));
                bean.setTypeName(rs.getString("TYPE_NAME"));
                bean.setSize(rs.getInt("COLUMN_SIZE"));
                bean.setNullAble(0 == rs.getInt("NULLABLE") ? false : true);

                if (pkColumns.contains(bean.getColumnName())) {
                    bean.setPrimaryKey(true);
                }

                tblInfoList.add(bean);
            }

            infoMap.put(tableName, tblInfoList);

        } finally {
            if (null != rs) {
                rs.close();
            }
        }
    }


    public static void main(String[] args) {
        try {
        	Map <String, List <TableInfoBean>> result=fetchTableInfo();
        	List <TableInfoBean> emp = result.get("emp");
        	for (TableInfoBean b : emp) {
				System.out.println(b.getColumnName()+":"+b.getTypeName());
			}
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
