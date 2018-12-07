package net.chinaedu.aedu.tools.router;

import net.chinaedu.aedu.tools.annotations.Consts;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author MartinKent
 * @time 2018/1/24
 */
class RouteHelper {
    private static Class<?> routesClass;
    private static Method queryActivityClass;
    private static final Map<String, String> activityClassMap = new HashMap<>();

    static String queryActivityClass(String route) {
        try {
            String className = activityClassMap.get(route);
            if (null == className) {
                if (null == queryActivityClass) {
                    if (null == routesClass) {
                        routesClass = Class.forName(Consts.ROUTES_CLASS_NAME);
                    }
                    queryActivityClass = routesClass.getDeclaredMethod(Consts.ROUTES_CLASS_QUERY_ACTIVITY_CLASS, String.class);
                }
                className = (String) queryActivityClass.invoke(null, route);
                activityClassMap.put(route, className);
            }
            return className;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
