package net.chinaedu.aedu.tools.statesaver;

import android.os.Bundle;

/**
 * @author MartinKent
 * @time 2018/1/23
 */
public interface StateSaver<T> {

    Bundle save(T target, Bundle data);

    void restore(T target, Bundle data);
}
