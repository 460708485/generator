package generator.tools;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import generator.Globles;
import generator.bean.DomainDefineBean;
import generator.bean.TableInfoBean;
import generator.util.CommentUtil;
import generator.util.CommonUtil;
import generator.util.DateUtil;
import generator.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

public class DaoGenerator implements Globles {

    public static void genaratorDao(Configuration cfg,
        Entry<String, List<TableInfoBean>> tableInfo) throws IOException,
        TemplateException {
        BufferedWriter writer = null;

        try {
            String _package=CommonUtil.getDaoOutPutPath();
            String path =_package  + "/"+ getDaoName(tableInfo.getKey()) + ".java";
            FileUtil.mkdir(path);

            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(path), CommonUtil.getSourceEncode()));

            Map<String, Object> rootMap = new HashMap<String, Object>();

            setRootMapForDao(rootMap, tableInfo);

            Template tpl = cfg.getTemplate("Dao.tpl");
            tpl.setEncoding("UTF-8");

            tpl.process(rootMap, writer);
            FileUtils.writeStringToFile(new File(_package + "/package.html"), "entity", "UTF-8");
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }

    }

    private static void setRootMapForDao(Map<String, Object> rootMap,
        Entry<String, List<TableInfoBean>> tableInfo) {

        rootMap.put("date", DateUtil.getStringToday());
        rootMap.put("rootName", CommonUtil.getRootName());
        rootMap.put("package", CommonUtil.getDaoPackage());
        rootMap.put("parentImport", CommonUtil.getDaoParent());
        rootMap.put("classdef", getDaoName(tableInfo.getKey()));
        rootMap.put("extends", CommonUtil.getExtends(CommonUtil.getDaoParent()));
        rootMap.put("tableComment", CommentUtil.getTableComment(tableInfo.getKey()));
        rootMap.put("TableName", CommonUtil.getOutputColumnName(tableInfo.getKey(), true));
        rootMap.put("tableName", CommonUtil.getOutputColumnName(tableInfo.getKey(), false));

        List<DomainDefineBean> fieldList = new ArrayList<DomainDefineBean>();
        List<DomainDefineBean> columnList = new ArrayList<DomainDefineBean>();
        List<String> importList = new ArrayList<String>();

        DomainDefineBean ddb = null;
        DomainDefineBean column = null;
        List<String> domainColumnSkip = Arrays.asList(CommonUtil.getDomainColumnSkip().split(","));
        String importSkip = CommonUtil.getImportSkip();
        for (TableInfoBean tableInfoBean : tableInfo.getValue()) {

            if (domainColumnSkip.contains(tableInfoBean.getColumnName())) {
                continue;
            }

            ddb = new DomainDefineBean();

            String importValue = CommonUtil.getJavaMappingType(tableInfoBean.getTypeName());

//            if (!importList.contains(importValue)
//                && !importSkip.contains(importValue)) {
//                importList.add(importValue);
//            }

//            ddb.setType(CommonUtil.getType(importValue));
            ddb.setType("String");
            ddb.setName(CommonUtil.getOutputColumnName(tableInfoBean
                .getColumnName(), false));
            ddb.setMethodAppend(CommonUtil.getOutputColumnName(tableInfoBean
                .getColumnName(), true));
            ddb.setComment(CommentUtil.getColumnComent(tableInfo.getKey(), tableInfoBean
                .getColumnName()));

            fieldList.add(ddb);
            
            column=new DomainDefineBean();
            column.setType("String");
            column.setName(tableInfoBean.getColumnName());
            column.setComment(CommentUtil.getColumnComent(tableInfo.getKey(), tableInfoBean
                    .getColumnName()));
            columnList.add(column);
        }

        rootMap.put("fieldList", fieldList);
        rootMap.put("importList", importList);
        rootMap.put("columnList", columnList);
    }

    private static String getDaoName(String tableName) {
    	String domainName = CommonUtil.getOutputColumnName(tableName, true);
        return domainName + DAO;
    }
}
