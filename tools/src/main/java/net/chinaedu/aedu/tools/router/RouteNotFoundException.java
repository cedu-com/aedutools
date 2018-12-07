package net.chinaedu.aedu.tools.router;

import java.util.Locale;

/**
 * @author MartinKent
 * @time 2018/1/24
 */
final class RouteNotFoundException extends RuntimeException {
    RouteNotFoundException(String route) {
        super(String.format(Locale.getDefault(), "No activity found for route[%s]", route));
    }
}