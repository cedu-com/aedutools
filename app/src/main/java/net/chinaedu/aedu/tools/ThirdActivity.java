package net.chinaedu.aedu.tools;

import android.os.Bundle;

import net.chinaedu.aedu.tools.annotations.IntentData;

public class ThirdActivity extends BaseActivity {

    @IntentData
    String aaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
