package generator.tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import generator.bean.KeyValueBean;
import generator.bean.TableInfoBean;
import generator.util.CommonUtil;
import generator.util.FileUtil;
import generator.util.PropertyFileReader;

public class SqlMapGenerator {

    private SqlMapGenerator() {

    }


    public static void genaratorSqlMap(Configuration cfg, Entry <String, List <TableInfoBean>> tableInfo) throws IOException, TemplateException {

        BufferedWriter writer = null;

        try {
            String path = CommonUtil.getSqlMapOutPutPath(tableInfo.getKey()) + "/" + getSqlMapName(tableInfo.getKey());

            FileUtil.mkdir(path);

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path), "UTF-8"));

            Map <String, Object> rootMap = new HashMap <String, Object>();

            setRootMapForSQLMap(rootMap, tableInfo);

            Template tpl = cfg.getTemplate("SqlMap.tpl");
            tpl.setEncoding("UTF-8");

            tpl.process(rootMap, writer);
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }

    }


    private static void setRootMapForSQLMap(Map <String, Object> rootMap, Entry <String, List <TableInfoBean>> tableInfo) {

        Properties props = PropertyFileReader.getProperties("config.base");
        List <String> commonColumns = Arrays.asList(props.getProperty("update.columns.nouse").split(","));

        rootMap.put("tblName", CommonUtil.getOutputColumnName(tableInfo.getKey(), false));
        rootMap.put("tblNameSql", tableInfo.getKey());

        //        rootMap.put("package", CommonUtil.getDomainPackage(tableInfo.getKey()));
        rootMap.put("package", CommonUtil.getDaoPackage());

        rootMap.put("daoPackage", CommonUtil.getDaoPackage(tableInfo.getKey()));

        List <KeyValueBean> whereKeyValueList = new ArrayList <KeyValueBean>();
        rootMap.put("whereConditionList", whereKeyValueList);

        List <KeyValueBean> updateKeyValueList = new ArrayList <KeyValueBean>();
        rootMap.put("updateColumnList", updateKeyValueList);

        List <KeyValueBean> rsltSetKeyValueList = new ArrayList <KeyValueBean>();
        rootMap.put("rsltSetColumList", rsltSetKeyValueList);

        KeyValueBean keyValueBean = null;

        String primaryKey = "";
        String defaultSortKeys = "";

        // add start for where key DEL_FLG='0'
        List <String> allColums = new ArrayList <String>();
        boolean isDelFlgPK = false;
        // add end for where key DEL_FLG='0'

        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {

            // add start for where key DEL_FLG='0'
            allColums.add(tableInfoBean.getColumnName());
            // add end for where key DEL_FLG='0'

            keyValueBean = new KeyValueBean();
            keyValueBean.setKey(tableInfoBean.getColumnName());
            keyValueBean.setValue(CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false));
            whereKeyValueList.add(keyValueBean);
            if (!commonColumns.contains(keyValueBean.getKey())) {
                updateKeyValueList.add(keyValueBean);
            }
            rsltSetKeyValueList.add(keyValueBean);

            if (!rootMap.containsKey("countColumn")) {
                rootMap.put("countColumn", keyValueBean.getKey());
            }

            if (tableInfoBean.isPrimaryKey()) {
                // add start for where key DEL_FLG='0'
                if ("DEL_FLG".equals(keyValueBean.getKey())) {
                    isDelFlgPK = true;
                }
                // add end for where key DEL_FLG='0'

                if ("".equals(primaryKey)) {
                    primaryKey += keyValueBean.getKey() + "=#" + keyValueBean.getValue() + "#";
                    defaultSortKeys += keyValueBean.getKey() + " asc";
                } else {
                    primaryKey += " and " + keyValueBean.getKey() + "=#" + keyValueBean.getValue() + "#";
                    defaultSortKeys += ", " + keyValueBean.getKey() + " asc";
                }
            }
        }

        // add start for where key DEL_FLG='0'
        if (!isDelFlgPK && allColums.contains("DEL_FLG")) {
            primaryKey += " and DEL_FLG='0'";
        }
        // add end for where key DEL_FLG='0'

        rootMap.put("primaryKeys", primaryKey);
        rootMap.put("selectSql", getSelectSql(tableInfo));
        rootMap.put("insertKeySql", getInsertKeySql(tableInfo));
        rootMap.put("insertValueSql", getInsertValueSql(tableInfo));
        rootMap.put("updateSql", getUpdateSql(tableInfo));
        rootMap.put("conditionSql", getConditionSql(tableInfo));

        // ソート順の設定
        String sortKeys = props.getProperty(tableInfo.getKey() + ".sortKeys");
        if (null == sortKeys || "".equals(sortKeys)) {
            rootMap.put("sortKeys", defaultSortKeys);
        } else {
            rootMap.put("sortKeys", sortKeys);
        }
    }

    private static String getSelectSql(Entry <String, List <TableInfoBean>> tableInfo) {

        String selectSql = "select\r\n";

        selectSql += "\t\t";
        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {
            selectSql += tableInfoBean.getColumnName() + ",";
        }

        selectSql = selectSql.substring(0, selectSql.length() - 1);
        selectSql += "\r\n";
        selectSql += "\t\t";
        selectSql += "from " + tableInfo.getKey();

        return selectSql;
    }

    private static String getInsertKeySql(Entry <String, List <TableInfoBean>> tableInfo) {

        String insertSql = "\t\tinsert into\r\n";

        insertSql += "\t\t";
        String keys = "";
        //        String values = "";
        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {
            keys += tableInfoBean.getColumnName() + ",";
            //            values += "#"
            //                + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false)
            //                + "#,";
        }

        keys = keys.substring(0, keys.length() - 1);
        //        values = values.substring(0, values.length() - 1);

        insertSql += tableInfo.getKey() + "(";
        insertSql += keys + ")";
        //        insertSql += "\t\t";
        //        insertSql += "values(" + values + ")";

        return insertSql;
    }


    private static String getInsertValueSql(Entry <String, List <TableInfoBean>> tableInfo) {

        ArrayList <TableInfoBean> tableInfoList = new ArrayList <TableInfoBean>();

        int columnCount = 0;

        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {
            tableInfoList.add(columnCount, tableInfoBean);
            columnCount++;
        }

        String updateSql = "\t\t<dynamic>\r\n\t\t(\r\n";

        for (int i = 0; i < tableInfoList.size(); i++) {

            TableInfoBean tableInfoBean = tableInfoList.get(i);
            if ("java.math.BigDecimal".equals(CommonUtil.getJavaMappingType(tableInfoBean.getTypeName()))) {

                updateSql += "\t\t<isEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if (i < tableInfoList.size() - 1) {
                    if (tableInfoBean.isNullAble()) {
                        updateSql += "\t\t\tnull,\r\n";
                    } else {
                        updateSql += "\t\t\t0,\r\n";
                    }
                } else {
                    if (tableInfoBean.isNullAble()) {
                        updateSql += "\t\t\tnull\r\n";
                    } else {
                        updateSql += "\t\t\t0\r\n";
                    }
                }
                updateSql += "\t\t</isEmpty>\r\n";

                updateSql += "\t\t<isNotEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if (i < tableInfoList.size() - 1) {
                    updateSql += "\t\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#,\r\n";
                } else {
                    updateSql += "\t\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#\r\n";
                }
                updateSql += "\t\t</isNotEmpty>\r\n";

            } else if (!"java.lang.String".equals(CommonUtil.getJavaMappingType(tableInfoBean.getTypeName()))) {

                updateSql += "\t\t<isEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if (i < tableInfoList.size() - 1) {
                    updateSql += "\t\t\tnull,\r\n";
                } else {
                    updateSql += "\t\t\tnull\r\n";
                }
                updateSql += "\t\t</isEmpty>\r\n";

                updateSql += "\t\t<isNotEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if (i < tableInfoList.size() - 1) {
                    updateSql += "\t\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#,\r\n";
                } else {
                    updateSql += "\t\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#\r\n";
                }
                updateSql += "\t\t</isNotEmpty>\r\n";

            } else {
                //                if (i < tableInfoList.size() - 1) {
                //                    updateSql += "\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + ":" + tableInfoBean.getTypeName()
                //                            + ":NO_ENTRY#,\r\n";
                //                } else {
                //                    updateSql += "\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + ":" + tableInfoBean.getTypeName()
                //                            + ":NO_ENTRY#\r\n";
                //                }
                updateSql += "\t\t<isEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if (i < tableInfoList.size() - 1) {
                    if (tableInfoBean.isNullAble()) {
                        updateSql += "\t\t\tnull,\r\n";
                    } else {
                        updateSql += "\t\t\t'',\r\n";
                    }
                } else {
                    if (tableInfoBean.isNullAble()) {
                        updateSql += "\t\t\tnull\r\n";
                    } else {
                        updateSql += "\t\t\t''\r\n";
                    }
                }
                updateSql += "\t\t</isEmpty>\r\n";

                updateSql += "\t\t<isNotEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if (i < tableInfoList.size() - 1) {
                    updateSql += "\t\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#,\r\n";
                } else {
                    updateSql += "\t\t\t#" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#\r\n";
                }
                updateSql += "\t\t</isNotEmpty>\r\n";
            }
        }

        updateSql = updateSql.substring(0, updateSql.length() - 2);
        return updateSql + "\r\n\t\t)\r\n\t\t</dynamic>";
    }


    private static String getUpdateSql(Entry <String, List <TableInfoBean>> tableInfo) {

        String updateSql = "";

        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {
            if (!"CRT_TIMESTAMP".equals(tableInfoBean.getColumnName()) && !"CRT_USER_ID".equals(tableInfoBean.getColumnName())) {
                updateSql += "\t\t\t<isNotNull prepend=\",\" property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false)
                        + "\">\r\n";

                updateSql += "\t\t\t\t<isEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                if ("java.math.BigDecimal".equals(CommonUtil.getJavaMappingType(tableInfoBean.getTypeName())) && !tableInfoBean.isNullAble()) {
                    updateSql += "\t\t\t\t\t" + tableInfoBean.getColumnName() + " = 0\r\n";
                } else if ("java.lang.String".equals(CommonUtil.getJavaMappingType(tableInfoBean.getTypeName())) && !tableInfoBean.isNullAble()) {
                    updateSql += "\t\t\t\t\t" + tableInfoBean.getColumnName() + " = ''\r\n";
                } else {
                    updateSql += "\t\t\t\t\t" + tableInfoBean.getColumnName() + " = null\r\n";
                }
                updateSql += "\t\t\t\t</isEmpty>\r\n";

                updateSql += "\t\t\t\t<isNotEmpty property=\"" + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "\">\r\n";
                updateSql += "\t\t\t\t\t" + tableInfoBean.getColumnName() + " = #"
                        + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), false) + "#\r\n";
                updateSql += "\t\t\t\t</isNotEmpty>\r\n";

                updateSql += "\t\t\t</isNotNull>\r\n";
            }
        }

        return updateSql;
    }


    private static String getConditionSql(Entry <String, List <TableInfoBean>> tableInfo) {

        String whereSql = "";

        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {
            whereSql += "\t\t\t<isNotEmpty prepend=\"and\" property=\"condition"
                    + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), true) + "\">\r\n";
            whereSql += "\t\t\t\t" + tableInfoBean.getColumnName() + " = #condition"
                    + CommonUtil.getOutputColumnName(tableInfoBean.getColumnName(), true) + "#\r\n";
            whereSql += "\t\t\t</isNotEmpty>\r\n";
        }

        return whereSql;
    }

    public static String getSqlMapName(String tableName) {

        String sqlMapTableName = CommonUtil.getOutputColumnName(tableName, true);
        return "SqlMap-" + sqlMapTableName + ".xml";
    }
}
