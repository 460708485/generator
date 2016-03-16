package other.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectStreamClass;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import generator.Generator;
import generator.util.CommonUtil;
import generator.util.FileUtil;

public class SerialVersionUIDGen {

    private static Configuration cfg = null;

    static {
        if (null == cfg) {
            cfg = new Configuration();
            try {
                cfg.setDirectoryForTemplateLoading(new File(Generator.class
                    .getResource("/").getPath()
                    + "template"));
                cfg.setDefaultEncoding("UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void addSerialVersionUID() throws IOException,
        TemplateException {

        List<File> javaFiles = getFileNames();
        for (File javaFile : javaFiles) {

            if (isFileSkip(javaFile)) {
                continue;
            }

            String className = getPackage(javaFile) + "."
                + javaFile.getName().replaceAll(".java", "");

            ObjectStreamClass osc = null;
            try {
                Class<?> classObj = Class.forName(className);
                osc = ObjectStreamClass.lookup(classObj);

            } catch (Exception e) {
                System.out.println("Class「" + className + "」 is not definition");
            }

            File newFile = fileOutput(javaFile, getSerialText(osc
                .getSerialVersionUID()));

            javaFile.delete();
            newFile.renameTo(javaFile);
        }
    }

    private static File fileOutput(File javaFile, String serialText)
        throws IOException {

        BufferedReader reader = null;
        BufferedWriter writer = null;
        File file = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(javaFile), CommonUtil.getSourceEncode()));

            file = new File(javaFile.getParent() + "/" + javaFile.getName()
                + ".new");
            writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), CommonUtil.getSourceEncode()));

            String line = null;
            while (null != (line = reader.readLine())) {
                writer.write(line);
                writer.newLine();
                if (line.contains("public class")) {
                    writer.newLine();
                    writer.write(serialText);
                    writer.newLine();
                }
            }
        } finally {
            if (null != reader) {
                reader.close();
            }

            if (null != writer) {
                writer.flush();
                writer.close();
            }
        }

        return file;
    }

    private static String getSerialText(long serialVersionUID)
        throws TemplateException, IOException {

        Map<String, Object> rootMap = new HashMap<String, Object>();

        rootMap.put("serial", String.valueOf(serialVersionUID));

        Template tpl = cfg.getTemplate("serial.tpl");
        tpl.setEncoding("UTF-8");

        StringWriter sw = new StringWriter();
        tpl.process(rootMap, sw);

        return sw.toString();
    }

    private static boolean isFileSkip(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), CommonUtil.getSourceEncode()));

            String line = null;
            while (null != (line = reader.readLine())) {
                if (line.contains("serialVersionUID")) {
                    return true;
                }
            }
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return false;
    }

    private static String getPackage(File file) throws IOException {

        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), CommonUtil.getSourceEncode()));

            String line = null;
            while (null != (line = reader.readLine())) {
                if (line.contains("package")) {
                    StringTokenizer stoken = new StringTokenizer(line);
                    stoken.nextToken();

                    String packageName = stoken.nextToken();
                    packageName = packageName.substring(0,
                        packageName.length() - 1);

                    return packageName;
                }
            }
        } finally {
            if (null != reader) {
                reader.close();
            }
        }

        return "";
    }

    private static List<File> getFileNames() {

        String packageRoot = CommonUtil.getDomainPackage("____");
        packageRoot = packageRoot.replace('.', '/');
        String rootPath = CommonUtil.getRootPath();

        String fileRoot = rootPath + "/" + packageRoot;

        File file = new File(fileRoot);
        List<File> fileList = new ArrayList<File>();
        FileUtil.findAllFile(file, ".java", fileList);

        return fileList;

    }

    public static void main(String[] args) {

        try {
            addSerialVersionUID();
        } catch (IOException e) {

            e.printStackTrace();
        } catch (TemplateException e) {

            e.printStackTrace();
        }

    }
}
