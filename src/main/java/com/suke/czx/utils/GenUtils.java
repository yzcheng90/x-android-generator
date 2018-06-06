package com.suke.czx.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 代码生成器   工具类
 *
 * @author czx
 * @email object_czx@163.com
 * @date 2016年12月19日 下午11:40:24
 */
public class GenUtils {

    public static List<String> getTemplates(){
        List<String> templates = new ArrayList<String>();
        templates.add("template/Activity.java.vm");
        templates.add("template/activity.xml.vm");
        templates.add("template/ActivityEdit.java.vm");
        templates.add("template/editActivity.xml.vm");
        templates.add("template/Adapter.java.vm");
        templates.add("template/adapter.xml.vm");
        templates.add("template/Present.java.vm");
        templates.add("template/editPresent.java.vm");
        return templates;
    }

    /**
     * 生成代码
     */
    public static void generatorCode(Map<String, Object> params, ZipOutputStream zip) {
        //配置信息
        Configuration config = getConfig();
        //JSON对象
        String className = params.get("moduleName").toString();
        String classNameCS = params.get("moduleNameCS").toString();
        HashMap<String,String> moduleData = new Gson().fromJson(params.get("moduleData").toString(),new TypeToken<HashMap<String, Object>>(){}.getType());

        //设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader" );
        Velocity.init(prop);
        String mainPath = config.getString("mainPath" );
        mainPath = StringUtils.isBlank(mainPath) ? "com.suke.czx" : mainPath;

        Map<String, Object> map = new HashMap<>();
        //封装模板数据
        map.put("mainPath", mainPath);
        map.put("package", config.getString("package" ));
        map.put("moduleName", className);
        map.put("moduleNameLower", className.toLowerCase());
        map.put("className", className);
        map.put("GenData", moduleData);
        map.put("comments", classNameCS); //类描述
        map.put("author", config.getString("author" ));
        map.put("email", config.getString("email" ));
        map.put("datetime", DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
        VelocityContext context = new VelocityContext(map);

        //获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            //渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8" );
            tpl.merge(context, sw);

            try {
                //添加到zip
                zip.putNextEntry(new ZipEntry(getFileName(template, className, config.getString("package" ), className)));
                IOUtils.write(sw.toString(), zip, "UTF-8" );
                IOUtils.closeQuietly(sw);
                zip.closeEntry();
            } catch (IOException e) {
                throw new RRException("渲染模板失败：" + template, e);
            }
        }
    }


    /**
     * 获取配置信息
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties" );
        } catch (ConfigurationException e) {
            throw new RRException("获取配置文件失败，", e);
        }
    }

    /**
     * 获取文件名
     */
    public static String getFileName(String template, String className, String packageName, String moduleName) {
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            packagePath += packageName.replace(".", File.separator) + File.separator + moduleName + File.separator;
        }

        if (template.equals("template/Activity.java.vm" )) {
            return packagePath +File.separator + className + "Activity.java";
        }

        if (template.equals("template/activity.xml.vm" )) {
            return packagePath + File.separator + className.toLowerCase() + "_activity.xml";
        }

        if (template.equals("template/ActivityEdit.java.vm" )) {
            return packagePath +File.separator + className + "ActivityEdit.java";
        }

        if (template.equals("template/editActivity.xml.vm" )) {
            return packagePath + File.separator + className.toLowerCase() + "_edit_activity.xml";
        }

        if (template.equals("template/Adapter.java.vm" )) {
            return packagePath + File.separator + className + "Adapter.java";
        }

        if (template.equals("template/adapter.xml.vm" )) {
            return packagePath + File.separator + className.toLowerCase() + "_adapter.xml";
        }

        if (template.equals("template/Present.java.vm" )) {
            return packagePath + File.separator + className + "Present.java";
        }

        if (template.equals("template/editPresent.java.vm" )) {
            return packagePath + File.separator + className + "EditPresent.java";
        }
        return null;
    }
}
