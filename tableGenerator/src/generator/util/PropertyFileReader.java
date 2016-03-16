package generator.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertyFileReader {

    private PropertyFileReader() {
    }

    public static Properties getProperties(String propertyFileName) {

        Properties prop = new Properties();

        if (propertyFileName == null) {
            return null;
        }

        ResourceBundle resourceBundle = ResourceBundle.getBundle(propertyFileName);
        Enumeration<?> enumeration = resourceBundle.getKeys();
        while (enumeration.hasMoreElements()) {
            Object key = enumeration.nextElement();
            prop.put(key,charString(resourceBundle.getObject((String)key)+""));
        }
        return prop;
    }
    
    public static String charString(String o){
    	String key =null;
    	try {
    		key= new String(o.getBytes("ISO8859-1"),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return key;
    }
}
