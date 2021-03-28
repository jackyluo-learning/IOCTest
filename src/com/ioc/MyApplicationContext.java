package com.ioc;

import com.ioc.anotation.MyAutowired;
import com.ioc.anotation.MyComponent;
import com.ioc.anotation.MyController;
import com.ioc.anotation.MyMapping;
import com.ioc.anotation.MyService;
import com.ioc.anotation.Value;
import com.ioc.util.ConfigurationUtils;
import com.ioc.util.Utils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MyApplicationContext {
    //store those beans after initiation
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();

    //store the full path of those objects that need to be initiated.
    private Set<String> classNameSet = new HashSet<>();

    //Configuration object creation
    private ConfigurationUtils configurationUtils = new ConfigurationUtils(null);

    public MyApplicationContext() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void init() throws Exception {
        this.classLoader();
    }

    /**
     * load classes when initiation
     *
     * @throws Exception
     */
    private void classLoader() throws Exception {
        String beanClassPath = (String) configurationUtils.getPropertiesByKey("ioc.bean.path");
        if (StringUtils.isNotEmpty(beanClassPath)) {
            beanClassPath = beanClassPath.replace(".", "/");
        } else {
            throw new RuntimeException("Please configure bean scan path by key : ioc.bean.path");
        }
        log.info("Reading bean path: " + beanClassPath);
        loadBeanClass(beanClassPath);
        for (String className : classNameSet) {
            registerBean(Class.forName(className));
        }

        Set<String> beanNameSet = beanMap.keySet();
        for (String beanName : beanNameSet) {
            doInjection(beanMap.get(beanName));
        }
    }

    private void doInjection(Object o) throws Exception {
        Field[] fields = o.getClass().getDeclaredFields();
        if (ArrayUtils.isNotEmpty(fields)) {
            for (Field field : fields) {
                if (field.isAnnotationPresent(MyAutowired.class)) {
                    MyAutowired myAutowired = field.getAnnotation(MyAutowired.class);
                    field.setAccessible(true);
                    Class fieldClass = field.getType();
                    if (fieldClass.isInterface()) {
                        if (StringUtils.isNotEmpty(myAutowired.value())) {
                            field.set(o, beanMap.get(Utils.toLowerCaseIndex(myAutowired.value())));
                            log.info("Dependency inject: " + field.getName());
                        } else {
                            List<Object> list = findImplByInterface(fieldClass);
                            if (list.size() > 0) {
                                if (list.size() > 1) {
                                    //Spring中如果一个interface有多个实现类，不能用autowired，因为不知道要注入哪一个实现类，需要用@Resource指定实现类的名字
                                    throw new RuntimeException(o.getClass() + " interface fail to inject " + fieldClass
                                            + ", please specify the implementation by name");
                                } else {
                                    field.set(o, list.get(0));
                                    log.info("Dependency inject: " + field.getName());
                                    doInjection(field.getType());
                                }
                            } else {
                                throw new RuntimeException(field.getName() + " is not implemented by any implementation");
                            }
                        }
                    } else {
                        String beanName = StringUtils.isNotEmpty(myAutowired.value())
                                ? myAutowired.value()
                                : Utils.toLowerCaseIndex(field.getType().getSimpleName());
                        if (!beanMap.containsKey(beanName)) {
                            Class clazz = field.getType();
                            beanMap.put(beanName, clazz.newInstance());
                        }
                        field.set(o, beanMap.get(beanName));
                        log.info("Dependency inject: " + field.getName());
                        doInjection(beanMap.get(beanName));
                    }
                } else if (field.isAnnotationPresent(Value.class)) {
                    field.setAccessible(true);
                    Value value = field.getAnnotation(Value.class);
                    field.set(o, StringUtils.isNotEmpty(value.value())
                            ? configurationUtils.getPropertiesByKey(value.value())
                            : null);
                    log.info("Inject " + field.getName() + " using configuration key " + value.value());
                }
            }
        }
    }

    private List<Object> findImplByInterface(Class clazz) {
        Set<String> beanNameSet = beanMap.keySet();
        ArrayList<Object> implList = new ArrayList<>();
        for (String beanName : beanNameSet) {
            Object impl = beanMap.get(beanName);
            Class[] interfaces = impl.getClass().getInterfaces();
            if (Utils.objectExist(interfaces, clazz)) {
                implList.add(impl);
            }
        }
        return implList;
    }

    private void registerBean(Class clazz) throws Exception {
        if (clazz != null) {
            String beanName = null;
            if (clazz.isAnnotationPresent(MyComponent.class)) {
                MyComponent myComponent = (MyComponent) clazz.getAnnotation(MyComponent.class);
                beanName = StringUtils.isNotEmpty(myComponent.value())
                        ? myComponent.value()
                        : Utils.toLowerCaseIndex(clazz.getSimpleName());
                beanMap.put(beanName, clazz.newInstance());
            } else if (clazz.isAnnotationPresent(MyController.class)) {
                beanName = Utils.toLowerCaseIndex(clazz.getSimpleName());
                beanMap.put(beanName, clazz.newInstance());
            } else if (clazz.isAnnotationPresent(MyService.class)) {
                MyService myService = (MyService) clazz.getAnnotation(MyService.class);
                beanName = StringUtils.isNotEmpty(myService.value())
                        ? myService.value()
                        : Utils.toLowerCaseIndex(clazz.getSimpleName());
                beanMap.put(beanName, clazz.newInstance());
            } else if (clazz.isAnnotationPresent(MyMapping.class)) {
                MyMapping myMapping = (MyMapping) clazz.getAnnotation(MyMapping.class);
                beanName = StringUtils.isNotEmpty(myMapping.value())
                        ? myMapping.value()
                        : Utils.toLowerCaseIndex(clazz.getSimpleName());
                beanMap.put(beanName, clazz.newInstance());
            }

            if ((beanName != null)) {
                log.info("Registered " + beanName);
            } else {
                log.warn("Ignoring bean: " + clazz.getSimpleName());
            }
        }
    }

    private void loadBeanClass(String classScanPath) {
        if (classScanPath.contains(";")) {
            String[] classScanPathList = classScanPath.split(";");
            for (String path : classScanPathList) {
                URL url = this.getClass().getClassLoader().getResource(path);
                assert url != null;
                File file = new File(url.getFile());
                if (file.exists() && file.isDirectory()) {
                    for (File f : file.listFiles()) {
                        if (f.isDirectory()) {
                            loadBeanClass(path + "/" + file.getName());
                        } else {
                            if (f.getName().endsWith(".class")) {
                                classNameSet.add(
                                        path.replace("/", ".") + "." + f.getName().replace(".class", ""));
                                log.info("Loading: " + path.replace("/", ".") + "." + f.getName());
                            }
                        }
                    }
                } else {
                    throw new RuntimeException("Can not find any directories.");
                }
            }
        } else {
            throw new RuntimeException("Please input paths end with ;");
        }
    }

    public Object getBean(String name) {
        return beanMap != null ? beanMap.get(name) : null;
    }

    public void getBeanMap() {
        System.out.println(this.beanMap);
    }

    public void getClassName() {
        System.out.println(this.classNameSet);
    }
}
