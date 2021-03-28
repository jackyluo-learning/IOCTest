package com.ioc.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class Utils {
    /**
     * change the first character to lower case
     * @param name
     * @return
     */
    public static String toLowerCaseIndex(String name) {
        if (StringUtils.isNotEmpty(name)) {
            StringBuilder sb = new StringBuilder();
            sb.append(name.substring(0, 1).toLowerCase(Locale.ROOT));
            sb.append(name.substring(1));
            return sb.toString();
        }
        return null;
    }

    public static Boolean objectExist(Class[] arr, Class o) {
        return ArrayUtils.contains(arr, o);
    }
}
