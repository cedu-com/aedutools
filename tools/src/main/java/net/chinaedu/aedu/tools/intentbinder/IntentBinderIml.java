package net.chinaedu.aedu.tools.intentbinder;

import android.app.Activity;
import android.content.Intent;

import net.chinaedu.aedu.tools.annotations.Consts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MartinKent
 * @time 2018/1/25
 */
@SuppressWarnings("unchecked")
public class IntentBinderIml {
    private static final Map<String, IntentBinder> binderMap = new LinkedHashMap<>();

    public static void bind(Activity activity) {
        bind(activity.getIntent(), activity);
    }

    public static void bind(android.app.Fragment fragment) {
        bind(fragment.getActivity());
    }

    public static void bind(android.support.v4.app.Fragment fragment) {
        bind(fragment.getActivity());
    }

    public static void bind(Intent intent, Object target) {
        String targetClassName = target.getClass().getCanonicalName();
        IntentBinder binder = binderMap.get(targetClassName);
        if (null == binder) {
            try {
                Class<?> binderClass = Class.forName(target.getClass().getName() + Consts.INTENT_BINDER_SUFFIX);
                binder = (IntentBinder) binderClass.newInstance();
                binderMap.put(targetClassName, binder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != binder) {
            binder.bind(intent, target);
        }
    }
}
