package net.chinaedu.aedu.tools.compiler.processors;

/**
 * @author MartinKent
 * @time 2018/1/30
 */
class Utils {

    static String makeConstName(String path) {
        String constName = "";
        String[] arr = null;
        if (path.matches("[a-zA-Z0-9]+(/[a-zA-Z0-9]+)*")) {
            arr = path.split("/");
        } else if (path.matches("[a-zA-Z0-9]+(\\.[a-zA-Z0-9]+)*")) {
            arr = path.split("\\.");
        } else if (path.matches("[a-zA-Z0-9]+(_[a-zA-Z0-9]+)*")) {
            arr = path.split("_");
        } else {
            throw new RuntimeException("Route format error,should be like:[AA.BB.CC or AA/BB/CC or AA_BB_CC");
        }
        if (null == arr || 0 == arr.length) {
            return null;
        }
        for (String anArr : arr) {
            constName += (anArr.toUpperCase() + "_");
        }
        constName = constName.substring(0, constName.lastIndexOf("_"));
        return constName;
    }
}
