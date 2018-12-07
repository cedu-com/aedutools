package net.chinaedu.aedu.tools.statesaver;

import android.os.Bundle;
import android.os.Parcelable;

import net.chinaedu.aedu.tools.annotations.Consts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MartinKent
 * @time 2018/1/22
 */
@SuppressWarnings("unchecked")
public class StateSaverIml {
    private static final Map<String, StateSaver> saverMap = new LinkedHashMap<>();
    private static final String superDataKey = StateSaverIml.class.getName() + ".super_data";

    /**
     * @param target objects with Annotations @State
     * @param data   super data
     * @param <T>    must be android.os.Parcelable or android.os.Bundle
     * @return new data
     */
    public static <T extends Parcelable> T save(Object target, T data) {
        if (null == data) {
            data = (T) new Bundle();
        }
        Bundle thisData = new Bundle();
        thisData.putParcelable(superDataKey, data);
        String targetClassName = target.getClass().getName();
        StateSaver saver = saverMap.get(targetClassName);
        if (null == saver) {
            try {
                String saverClass = targetClassName + Consts.STATE_SAVER_SUFFIX;
                Class<?> cls = Class.forName(saverClass);
                saver = (StateSaver) cls.newInstance();
                saverMap.put(targetClassName, saver);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
        if (null != saver) {
            return (T) saver.save(target, thisData);
        }
        return data;
    }

    /**
     * @param target objects with Annotations @State
     * @param data   super data
     * @param <T>    must be android.os.Parcelable or android.os.Bundle
     * @return new data
     */
    public static <T extends Parcelable> T restore(Object target, T data) {
        if (null == data) {
            data = (T) new Bundle();
        }
        if (!(data instanceof Bundle)) {
            return data;
        }
        Bundle thisData = (Bundle) data;
        if (!thisData.containsKey(superDataKey)) {
            return data;
        }
        Class<?> targetClass = target.getClass();
        StateSaver saver = saverMap.get(targetClass.getName());
        if (null == saver) {
            try {
                String saverClass = targetClass.getName() + Consts.STATE_SAVER_SUFFIX;
                Class<?> cls = Class.forName(saverClass);
                saver = (StateSaver) cls.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != saver) {
            saver.restore(target, thisData);
        }
        return thisData.getParcelable(superDataKey);
    }
}
