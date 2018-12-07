package net.chinaedu.aedu.tools.intentbinder;

import android.content.Intent;

/**
 * @author MartinKent
 * @time 2018/1/25
 */
public interface IntentBinder<T> {
    void bind(Intent intent, T target);
}
