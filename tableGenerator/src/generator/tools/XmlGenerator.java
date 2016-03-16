package generator.tools;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import generator.Globles;
import generator.bean.KeyValueBean;
import generator.util.CommonUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlGenerator implements Globles {

    private static List<String> getSqlMapDefineList(List<String> tableNames) {

        List<String> xmlNameList = new ArrayList<String>();

        for (String tblName : tableNames) {

            String pck = CommonUtil.getSqlMapPackage(tblName);
            pck = pck.replace('.', '/');
            xmlNameList.add(pck + "/" + SqlMapGenerator.getSqlMapName(tblName));
        }

        return xmlNameList;
    }

    private static void genaratorSqlMapXml(Configuration cfg,
        List<String> tableNames) throws IOException, TemplateException {
        BufferedWriter writer = null;

        try {

            
            String xmlPath = CommonUtil.getXmlRootPath() + "/";
            File dir = new File(xmlPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            xmlPath +=  "sqlmap-config.xml";

            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(xmlPath), CommonUtil.getSourceEncode()));

            Map<String, Object> rootMap = new HashMap<String, Object>();

            rootMap.put("xmlPathList", getSqlMapDefineList(tableNames));

            Template tpl = cfg.getTemplate("SqlMapConfig.tpl");
            tpl.setEncoding("UTF-8");

            tpl.process(rootMap, writer);
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }
    }

    private static List<KeyValueBean> getDaoXmlList(List<String> tableNames) {

        List<KeyValueBean> beanDefList = new ArrayList<KeyValueBean>();

        KeyValueBean bean = null;
        for (String tblName : tableNames) {
            bean = new KeyValueBean();
            bean.setKey(CommonUtil.getOutputColumnName(tblName, true));
            String pck = CommonUtil.getDaoPackage(tblName);
            bean.setValue(pck + "." + DaoImplGenerator.getDaoImplName(tblName));

            beanDefList.add(bean);
        }

        return beanDefList;
    }

    private static List<KeyValueBean> getServiceXmlList(List<String> tableNames) {

        List<KeyValueBean> beanDefList = new ArrayList<KeyValueBean>();

        KeyValueBean bean = null;
        for (String tblName : tableNames) {
            bean = new KeyValueBean();
            bean.setKey(CommonUtil.getOutputColumnName(tblName, true));
            String pck = CommonUtil.getServicePackage(tblName);
            bean.setValue(pck + "." + ServiceImplGenerator.getServiceImplName(tblName));

            beanDefList.add(bean);
        }

        return beanDefList;
    }

    private static void genaratorSpringXml(Configuration cfg,
        List<String> tableNames) throws IOException, TemplateException {
        BufferedWriter writer = null;

        try {

            String xmlPath = CommonUtil.getXmlRootPath() + "/"
                + "app-services-config.xml";

            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(xmlPath), CommonUtil.getSourceEncode()));

            Map<String, Object> rootMap = new HashMap<String, Object>();

            rootMap.put("daoBeanList", getDaoXmlList(tableNames));
            rootMap.put("serviceBeanList", getServiceXmlList(tableNames));
            rootMap.put("daoPackage", CommonUtil.getDaoPackage(""));
            rootMap.put("servicePackage", CommonUtil.getServicePackage(""));


            Template tpl = cfg.getTemplate("applicationContext.tpl");
            tpl.setEncoding("UTF-8");

            tpl.process(rootMap, writer);
        } finally {
            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }
    }

    public static void genaratorXmls(Configuration cfg, List<String> tableNames)
        throws IOException, TemplateException {
        genaratorSqlMapXml(cfg, tableNames);
        genaratorSpringXml(cfg, tableNames);
    }
}
