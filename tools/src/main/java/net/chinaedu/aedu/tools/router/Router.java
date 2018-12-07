package net.chinaedu.aedu.tools.router;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author MartinKent
 * @time 2018/1/24
 */
public final class Router {
    private static final List<Intercepter> intercepters = new ArrayList<>();

    public static Builder builder() {
        return new Builder();
    }

    public static void start(@NonNull Context context, @NonNull String route) {
        start(new Intent(), context, route);
    }

    public static void start(@NonNull Activity context, @NonNull String route, int requestCode) {
        start(new Intent(), context, route, requestCode, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void start(@NonNull Activity context, @NonNull String route, int requestCode, Bundle options) {
        start(new Intent(), context, route, requestCode, options);
    }

    private static void start(@NonNull Intent intent, @NonNull Context context, @NonNull String route) {
        boolean intercepted = false;
        for (Intercepter intercepter : intercepters) {
            intercepted |= intercepter.intercept(context, intent);
        }
        if (!intercepted) {
            String activityClassName = RouteHelper.queryActivityClass(route);
            if (null == activityClassName) {
                throw new RouteNotFoundException(route);
            }
            intent.setClassName(context, activityClassName);
            context.startActivity(intent);
        }
    }

    @SuppressLint("NewApi")
    private static void start(@NonNull Intent intent, @NonNull Activity context, @NonNull String route, int requestCode, Bundle options) {
        boolean intercepted = false;
        for (Intercepter intercepter : intercepters) {
            intercepted |= intercepter.intercept(context, intent);
        }
        if (!intercepted) {
            String activityClassName = RouteHelper.queryActivityClass(route);
            if (null == activityClassName) {
                throw new RouteNotFoundException(route);
            }
            intent.setClassName(context, activityClassName);
            if (null == options) {
                context.startActivityForResult(intent, requestCode);
            } else {
                context.startActivityForResult(intent, requestCode, options);
            }
        }
    }

    public static void addIntercepter(Intercepter intercepter) {
        if (null == intercepter) {
            return;
        }
        intercepters.add(intercepter);
    }

    public static void removeIntercepter(Intercepter intercepter) {
        if (null == intercepter) {
            return;
        }
        if (intercepters.contains(intercepter)) {
            intercepters.remove(intercepter);
        }
    }

    public interface Intercepter {
        boolean intercept(Context context, Intent intent);
    }

    public static class Builder {
        private Intent intent = new Intent();

        private Builder() {

        }

        public Builder putExtra(String name, boolean value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, byte value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, char value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, short value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, int value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, long value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, float value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, double value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, String value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, CharSequence value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, Parcelable value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, Parcelable[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
            intent.putParcelableArrayListExtra(name, value);
            return this;
        }

        public Builder putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
            intent.putIntegerArrayListExtra(name, value);
            return this;
        }

        public Builder putStringArrayListExtra(String name, ArrayList<String> value) {
            intent.putStringArrayListExtra(name, value);
            return this;
        }

        public Builder putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
            intent.putCharSequenceArrayListExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, Serializable value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, boolean[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, byte[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, short[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, char[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, int[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, long[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, float[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, double[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, String[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, CharSequence[] value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtra(String name, Bundle value) {
            intent.putExtra(name, value);
            return this;
        }

        public Builder putExtras(Intent src) {
            intent.putExtras(src);
            return this;
        }

        public Builder putExtras(Bundle extras) {
            intent.putExtras(extras);
            return this;
        }

        public Builder addFlags(int flags) {
            intent.addFlags(flags);
            return this;
        }

        public Builder setFlags(int flags) {
            intent.setFlags(flags);
            return this;
        }

        public Builder setData(Uri uri) {
            intent.setData(uri);
            return this;
        }

        public Builder setType(String type) {
            intent.setType(type);
            return this;
        }

        public Builder setDataAndType(Uri uri, String type) {
            intent.setDataAndType(uri, type);
            return this;
        }

        public Intent getIntent() {
            return intent;
        }

        public void start(@NonNull Context context, @NonNull String route) {
            Router.start(intent, context, route);
        }

        public void start(@NonNull Activity context, @NonNull String route, int requestCode) {
            Router.start(intent, context, route, requestCode, null);
        }

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        public void start(@NonNull Activity context, @NonNull String route, int requestCode, Bundle options) {
            Router.start(intent, context, route, requestCode, options);
        }
    }
}
