package net.chinaedu.aedu.tools;

import android.os.Bundle;

import net.chinaedu.aedu.tools.annotations.Route;

@Route
public class FourthActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}
