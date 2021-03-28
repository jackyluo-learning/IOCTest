package com.ioc.util;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class ConfigurationUtils {
    private String propertiesPath;

    private Properties properties;

    public ConfigurationUtils (String path) {
        this.properties = this.getBeanScanPath(path);
    }

    /**
     * read the configuration file
     * @param pathName: the path to configuration file
     * @return properties
     */
    public Properties getBeanScanPath(String pathName) {
        if (StringUtils.isNotEmpty(pathName)) {
            this.propertiesPath = pathName;
        } else {
            log.info("Default configuration path: application.properties");
            this.propertiesPath = "application.properties";
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(this.propertiesPath);
        log.info("Loading configuration file: application.properties");
        this.properties = new Properties();
        try {
            this.properties.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return this.properties;
    }

    public Object getPropertiesByKey (String propertiesKey) {
        if (this.properties.size() > 0) {
            return this.properties.get(propertiesKey);
        }
        return null;
    }
}
