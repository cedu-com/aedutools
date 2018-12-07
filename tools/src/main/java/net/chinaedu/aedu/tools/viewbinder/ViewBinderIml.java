package net.chinaedu.aedu.tools.viewbinder;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;

import net.chinaedu.aedu.tools.annotations.Consts;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author MartinKent
 * @time 2018/1/5
 */
@SuppressWarnings("unchecked")
public class ViewBinderIml {
    private static final ViewFinder mDefaultViewFinder = new DefaultViewFinder();//默认声明一个Activity View查找器

    private static class DefaultViewFinder implements ViewFinder {

        @Override
        public View findView(Object sourse, int id) {
            if (sourse instanceof View) {
                return ((View) sourse).findViewById(id);
            }
            View sourseView = null;
            if (sourse instanceof Activity) {
                sourseView = ((Activity) sourse).getWindow().getDecorView();
            } else if (sourse instanceof Dialog) {
                sourseView = ((Dialog) sourse).getWindow().getDecorView();
            } else if (sourse instanceof android.app.Fragment) {
                sourseView = ((android.app.Fragment) sourse).getView();
            } else if (sourse instanceof android.support.v4.app.Fragment) {
                sourseView = ((android.support.v4.app.Fragment) sourse).getView();
            }
            return null == sourseView ? null : sourseView.findViewById(id);
        }
    }

    private static final Map<String, ViewBinder> binderMap = new LinkedHashMap<>();//管理保持管理者Map集合

    /**
     * Activity注解绑定
     *
     * @param activity
     */
    public static void bind(Activity activity) {
        bind(activity, activity.getWindow().getDecorView());
    }

    /**
     * Dialog
     *
     * @param dialog
     */
    public static void bind(Dialog dialog) {
        bind(dialog, dialog.getWindow().getDecorView());
    }

    /**
     * Fragment注解绑定
     *
     * @param fragment
     */
    public static void bind(android.support.v4.app.Fragment fragment) {
        bind(fragment, fragment.getView());
    }

    /**
     * Fragment注解绑定
     *
     * @param fragment
     */
    public static void bind(android.app.Fragment fragment) {
        bind(fragment, fragment.getView());
    }

    /**
     * 通用注解绑定
     *
     * @param host   目标
     * @param sourse sourseView
     */
    public static void bind(Object host, View sourse) {
        bind(host, sourse, mDefaultViewFinder);
    }

    /**
     * '注解绑定
     *
     * @param host   表示注解 View 变量所在的类，也就是注解类
     * @param sourse 表示查找 View 的地方，Activity & View 自身就可以查找，Fragment 需要在自己的 itemView 中查找
     * @param finder ui绑定提供者接口
     */
    public static void bind(Object host, Object sourse, ViewFinder finder) {
        String className = host.getClass().getName();
        try {
            ViewBinder binder = binderMap.get(className);
            if (binder == null) {
                Class<?> aClass = Class.forName(className + Consts.VIEW_BINDER_SUFFIX);
                binder = (ViewBinder) aClass.newInstance();
                binderMap.put(className, binder);
            }
            binder.bindView(host, sourse, finder);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解除注解绑定
     *
     * @param host
     */
    public static void unBind(Object host) {
        String className = host.getClass().getName();
        ViewBinder binder = binderMap.get(className);
        if (binder != null) {
            binder.unBindView(host);
        }
        binderMap.remove(className);
    }
}
