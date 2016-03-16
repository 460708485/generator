package generator.util;

import generator.Globles;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

public class CommonUtil implements Globles {

    private CommonUtil() {

    }

    private static Properties property = null;

    static {
        if (null == property) {
            property = PropertyFileReader.getProperties("config.base");
        }
    }
    /**
     * 将表明首字母大写
     * @param tblName
     * @param firstUpcase
     * @return
     */
    public static String getOutputColumnName(String tblName, boolean firstUpcase) {
        StringTokenizer stoken = new StringTokenizer(tblName, "_");
        String outStr = "";

        while (stoken.hasMoreTokens()) {
            String token = stoken.nextToken().toLowerCase();
            if ("".equals(outStr) && !firstUpcase) {
                outStr += token;
            } else {
                outStr += String.valueOf(token.charAt(0)).toUpperCase()
                    + token.substring(1);
            }
        }

        if (!firstUpcase) {
            if (outStr.length() > 2) {
                String first2Letter = outStr.substring( 0, 2 ).toLowerCase();
                outStr = first2Letter + outStr.substring( 2 );
            } else {
                outStr = outStr.toLowerCase();
            }
        } else {
            if (outStr.length() > 2) {
                String firstLetter = outStr.substring( 0, 1 );
                String secondLetter = outStr.substring( 1, 2 ).toLowerCase();
                outStr = firstLetter + secondLetter + outStr.substring( 2 );
            } else {
                String firstLetter = outStr.substring( 0, 1 );
                String secondLetter = outStr.substring( 1 ).toLowerCase();
                outStr = firstLetter + secondLetter;
            }
        }

        return outStr;
    }

    /**
     * 获取dao的输出路径
     * @param tableName
     * @return
     */
    public static String getDaoOutPutPath(String tableName) {
        
    	String rootPath = property.getProperty("root.path");
        String sqlMapRootPath = getDaoPackage(tableName);
        sqlMapRootPath = sqlMapRootPath.replace('.', '/');
        return rootPath + "/" + sqlMapRootPath;
    }

    public static String getServiceOutPutPath(String tableName) {
        String rootPath = property.getProperty("root.path");

        String sqlMapRootPath = getServicePackage(tableName);
        sqlMapRootPath = sqlMapRootPath.replace('.', '/');

        return rootPath + "/" + sqlMapRootPath;
    }

    public static String getSqlMapOutPutPath(String tableName) {

        String rootPath = property.getProperty("root.path");

        String sqlMapRootPath = getSqlMapPackage(tableName);
        sqlMapRootPath = sqlMapRootPath.replace('.', '/');

        return rootPath + "/" + sqlMapRootPath;
    }

    public static String getDomainOutPutPath(String tableName) {

        String rootPath = property.getProperty("root.path");
        String domainRootPath = getDomainPackage(tableName);
        domainRootPath = domainRootPath.replace('.', '/');

        return rootPath + "/" + domainRootPath;
    }
    
    public static String getControllerOutPutPath(String tableName) {

        String rootPath = property.getProperty("root.path");
        String domainRootPath = getDomainPackage(tableName);
        domainRootPath = domainRootPath.replace('.', '/');

        return rootPath + "/" + domainRootPath;
    }
    
    public static String getVoOutPutPath(String tableName) {

        String rootPath = property.getProperty("root.path");

        //String domainRootPath = getDomainPackage(tableName);
        //domainRootPath = domainRootPath.replace('.', '/');

        return rootPath + "/vo" ;
    }

    public static String getRootPath() {
        return property.getProperty("root.path");
    }

    public static String getXmlRootPath() {
        return property.getProperty("xml.root.path");
    }
    
    /**
     * 获取domain包名
     * @param tableName
     * @return
     */
    public static String getDomainPackage(String tableName) {

        String packageOut = property.getProperty("domain.package.root");
        String packageAppend = getPackageAppend(tableName);

        if (null == packageAppend || "".equals(packageAppend)) {
            return packageOut;
        }

        return packageOut + "." + packageAppend;
    }
    
    /**
     * 获取Controller包名
     * @param tableName
     * @return
     */
    public static String getControllerPackage(String tableName) {

        String packageOut = property.getProperty("controller.package");
        String packageAppend = getPackageAppend(tableName);

        if (null == packageAppend || "".equals(packageAppend)) {
            return packageOut;
        }

        return packageOut + "." + packageAppend;
    }
    
    public static String getDomainColumnSkip() {
        return property.getProperty("domain.column.skip");
    }
    /**
     * 获取domain的父类
     * @return
     */
    public static String getDomainParent() {
        return property.getProperty("domain.parent");
    }
    /**
     * 获取domain的实现类数组
     * @return
     */
    public static String[] getImplementsArray() {
        return property.getProperty("domain.implements").split(",");
    }
    
    /**
     * 获取domain实现类
     * @return
     */
    public static String getDomainImplements() {
        return property.getProperty("domain.implements");
    }
    /**
     * 获取dao层包名
     * @param tableName
     * @return
     */
    public static String getDaoPackage(String tableName) {

        String packageOut = property.getProperty("dao.package");
        String packageAppend = getPackageAppend(tableName);

        if (null == packageAppend || "".equals(packageAppend)) {
            return packageOut;
        }

        return packageOut + "." + packageAppend;
    }
    
    public static String getSqlMapPackage(String tableName) {

       return getDaoPackage(tableName) + ".ibatis";
    }

    /**
     * 获取dto包名
     * @return
     */
    public static String getDtoPackage() {
        return property.getProperty("dto.package");
    }
    /**
     * 获取dtoCondition包名
     * @return
     */
    public static String getConditionPackage() {
        return property.getProperty("dtoCondition.package");
    }
    /**
     * 获取controller包名
     * @return
     */
    public static String getControllerPackage() {
        return property.getProperty("controller.package");
    }
    /**
     * 获取dao包名
     * @return
     */
    public static String getDaoPackage() {
        return property.getProperty("dao.package");
    }
    /**
     * 获取daoImpl包名
     * @return
     */
    public static String getDaoImplPackage() {
        return property.getProperty("daoImpl.package");
    }
    /**
     * 获取service包名
     * @return
     */
    public static String getServicePackage() {
        return property.getProperty("service.package");
    }
    /**
     * 获取serviceImpl包名
     * @return
     */
    public static String getServiceImplPackage() {
        return property.getProperty("serviceImpl.package");
    }
    /**
     * 获取dto输出路径
     * @return
     */
    public static String getDtoOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getDtoPackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }
    /**
     * 获取dtoCondition输出路径
     * @return
     */
    public static String getConditionOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getConditionPackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }
    /**
     * 获取Controller输出路径
     * @return
     */
    public static String getControllerOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getControllerPackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }
    /**
     * 获取Dao输出路径
     * @return
     */
    public static String getDaoOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getDaoPackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }
    /**
     * 获取DaoImpl输出路径
     * @return
     */
    public static String getDaoImplOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getDaoImplPackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }
    /**
     * 获取Service输出路径
     * @return
     */
    public static String getServiceOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getServicePackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }
    /**
     * 获取ServiceImpl输出路径
     * @return
     */
    public static String getServiceImplOutPutPath() {
        String rootPath = property.getProperty("root.path");
        String entityRootPath = getServiceImplPackage();
        entityRootPath = entityRootPath.replace('.', '/');
        return rootPath + "/" + entityRootPath;
    }

    /**
     * 获取dao层继承类
     * @return
     */
    public static String getDaoParent() {
        return property.getProperty("dao.parent");
    }

    public static String getServicePackage(String tableName) {
        String packageOut = property.getProperty("service.package.root");
        String packageAppend = getPackageAppend(tableName);

        if (null == packageAppend || "".equals(packageAppend)) {
            return packageOut;
        }

        return packageOut + "." + packageAppend;
    }

    public static String getServiceParent() {
        return property.getProperty("service.parent");
    }

    public static String getConditionDomainParent() {
        return property.getProperty("condition.domain.parent");
    }
    public static String getConditionDomainImplements() {
        return property.getProperty("condition.domain.implements");
    }
    public static String getImportSkip() {
        return property.getProperty("import.skip");
    }

    public static String getJavaMappingType(String jdbcType) {

        String outPut = "";
        outPut = property.getProperty(jdbcType.toUpperCase());
        if (null == outPut || "".equals(outPut)) {
            System.out.println("Java mapping is not existence");
        }

        return outPut;
    }

    private static String getPackageAppend(String tableName) {
        String disPatch = "";
        disPatch = property.getProperty("package.dispach");

        if (null == disPatch || "".equals(disPatch)) {
            return "";
        }

        String[] patchs = disPatch.split(",");

        for (int i = 0; i < patchs.length; i++ ) {
            List<String> include = Arrays.asList(property.getProperty(patchs[i] + DISPATCH_INCLUDE).split(","));

            if (null == include || "".equals(include)) {
                System.out.println(patchs[i] + DISPATCH_INCLUDE + " is not Definition");
            }

            if (include.contains(tableName)) {
                return property.getProperty(patchs[i] + DISPATCH_APPEND);
            }
        }

        return "";
    }

    public static String getSourceEncode() {
        return property.getProperty("encode");
    }

    public static boolean isGenarateAll() {
        return "0".equals(getGenerateType());
    }

    public static boolean isGenarateSourceOnly() {
        return "1".equals(getGenerateType());
    }

    public static boolean isGenarateXmlOnly() {
        return "2".equals(getGenerateType());
    }

    /**
     * 返回extends+继承类
     * @param parentDefine
     * @return
     */
    public static String getExtends(String parentDefine) {

        String parent = parentDefine;
        final int pos = parent.lastIndexOf(".");

        if (pos > -1) {
            parent = parent.substring(pos + 1);
            parent = " extends " + parent;
        }

        return parent;
    }

    public static String getImplements(String _implements) {

        String[] _interfaces = _implements.split(",");
        StringBuffer impBuff = new StringBuffer();
        for (int i = 0; i < _interfaces.length; i++) {
            if (i == 0)
                impBuff.append(" implements ");

            String _if = _interfaces[i];
            final int pos = _if.lastIndexOf(".");

            if (pos > -1) {
                impBuff.append(_if.substring(pos + 1));
            }

            if (i < _interfaces.length - 1)
                impBuff.append(" , ");
        }

        return impBuff.toString();
    }

    public static String getType(String importValue) {

        String type = importValue;
        final int pos = importValue.lastIndexOf(".");

        if (pos > -1) {
            type = importValue.substring(pos + 1);
        }

        return type;
    }

    private static String getGenerateType() {
        return property.getProperty("gen.type");
    }
    public static String getRootName() {
		return property.getProperty("root.name");
	}
    
	public static String getControllerParent() {
		return null;
	}

	public static String getControllerImplements() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
