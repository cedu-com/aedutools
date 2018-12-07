package net.chinaedu.aedu.tools;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import net.chinaedu.aedu.tools.annotations.BindView;
import net.chinaedu.aedu.tools.annotations.OnClick;
import net.chinaedu.aedu.tools.annotations.SaveState;
import net.chinaedu.aedu.tools.router.SecondActivityRouteHelper;
import net.chinaedu.aedu.tools.statesaver.StateSaverIml;
import net.chinaedu.aedu.tools.viewbinder.ViewBinderIml;

import java.io.Serializable;
import java.util.ArrayList;

//import net.chinaedu.aedu.tools.router.DPRoutes;

public class MainActivity extends BaseActivity {

    @SaveState("test_key_1")
    Bundle v1;
    @SaveState("test_key_2")
    boolean v2;
    @SaveState("test_key_3")
    boolean[] v3;
    @SaveState
    byte v4;
    @SaveState
    byte[] v5;
    @SaveState
    char v6;
    @SaveState
    char[] v7;
    @SaveState
    CharSequence v8;
    @SaveState
    CharSequence[] v9;
    @SaveState
    ArrayList<CharSequence> v10;
    @SaveState
    double v11;
    @SaveState
    double[] v12;
    @SaveState
    float v13;
    @SaveState
    float[] v14;
    @SaveState
    int v15;
    @SaveState
    int[] v16;
    @SaveState
    ArrayList<Integer> v17;
    @SaveState
    long v18;
    @SaveState
    long[] v19;
    @SaveState
    Parcelable v20;
    @SaveState
    Parcelable[] v21;
    @SaveState
    ArrayList<Parcelable> v22;
    @SaveState
    Serializable v23;
    @SaveState
    short v24;
    @SaveState
    short[] v25;
    @SaveState
    SparseArray<Parcelable> v26;
    @SaveState
    String v27;
    @SaveState
    String[] v28;
    @SaveState
    ArrayList<String> v29;

    @BindView(R.id.test)
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewBinderIml.bind(this);
        StateSaverIml.restore(this, savedInstanceState);
        textView.setBackgroundColor(Color.RED);
        textView.setText("BindView with int id");
    }

    @OnClick(R.id.test)
    public void onClick(View view) {
        Toast.makeText(MainActivity.this, "This is a test!", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.goto_second_activity)
    public void gotoSecondActivity() {
//        Router.builder()
//                .putExtra("value", "testValue")
//                .putExtra("test_key", -1)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .start(this, DPRoutes.SECONDACTIVITY);
//        Router.start(this, DPRoutes.SECONDACTIVITY);
        SecondActivityRouteHelper //with @IntentData and @Route
                .builder()
                .withIndex(-1)
                .withValue("testValue")
                .withIntentData9(new String[]{"1", "2", "3", "4", "5", "6"})
                .start(this);
//        ThirdActivityRouteHelper //with @IntentData but without @Route
//                .builder()
//                .start(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(StateSaverIml.save(this, outState));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(StateSaverIml.restore(this, savedInstanceState));
        //TODO refresh views
    }
}
