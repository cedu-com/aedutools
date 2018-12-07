package net.chinaedu.aedu.tools.viewbinder;

/**
 * @author MartinKent
 * @time 2018/1/5
 */
public interface ViewBinder<T> {
    void bindView(T host, Object sourse, ViewFinder finder);

    void unBindView(T host);
}
