package generator;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import generator.bean.TableInfoBean;
import generator.tblloader.TableInfoFetch;
import generator.tools.ControllerGenerator;
import generator.tools.DaoGenerator;
import generator.tools.DaoImplGenerator;
import generator.tools.DtoConditionGenerator;
import generator.tools.DtoGenerator;
import generator.tools.ServiceGenerator;
import generator.tools.ServiceImplGenerator;
import generator.tools.SqlMapGenerator;
import generator.tools.XmlGenerator;
import generator.util.CommonUtil;

public class Generator {

    private static Configuration cfg = null;

    static {
        if (null == cfg) {
            cfg = new Configuration();
            try {
                cfg.setDirectoryForTemplateLoading(new File(Generator.class.getResource("/").getPath()+ "template"));
                cfg.setDefaultEncoding("UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void main(String args[]) {
        try {
            Map<String, List<TableInfoBean>> infoMap = TableInfoFetch.fetchTableInfo();

            if (null != infoMap) {
                Entry<String, List<TableInfoBean>> tblInfo = null;

                List<String> tableNames = new ArrayList<String>();

                for (Iterator<Entry<String, List<TableInfoBean>>> i = infoMap
                    .entrySet().iterator(); i.hasNext();) {             
                    tblInfo = i.next();

                    if (CommonUtil.isGenarateAll()|| CommonUtil.isGenarateSourceOnly()) {
                    	
                        SqlMapGenerator.genaratorSqlMap(cfg, tblInfo);
                        
                        
                        DtoGenerator.genaratorDto(cfg, tblInfo);
                        DtoConditionGenerator.genaratorDtoCondition(cfg, tblInfo);
                        DaoGenerator.genaratorDao( cfg, tblInfo );
                        DaoImplGenerator.genaratorDaoImpl(cfg, tblInfo);
                        ServiceGenerator.genaratorService(cfg, tblInfo);
                        ServiceImplGenerator.genaratorServiceImpl(cfg, tblInfo);
                        ControllerGenerator.genaratorController(cfg, tblInfo);
                       
                    }
                    tableNames.add(tblInfo.getKey());
                }

                if (CommonUtil.isGenarateAll()
                    || CommonUtil.isGenarateXmlOnly()) {
                    XmlGenerator.genaratorXmls(cfg, tableNames);
                }

            } else {
                System.out.println("table is not defined, please check the config file");
            }
        } catch (IOException e) {

            e.printStackTrace();
        } catch (TemplateException e) {

            e.printStackTrace();
        } catch (SQLException e) {

            e.printStackTrace();
        }
    }
}
