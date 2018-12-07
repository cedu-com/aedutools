package net.chinaedu.aedu.tools;

import android.os.Bundle;
import android.widget.TextView;

import net.chinaedu.aedu.tools.annotations.IntentData;
import net.chinaedu.aedu.tools.annotations.Route;
import net.chinaedu.aedu.tools.intentbinder.IntentBinderIml;

import java.util.Arrays;

@Route("SecondActivity")
public class SecondActivity extends BaseActivity {
    @IntentData
    String value;
    @IntentData("test_key")
    int index;

    @IntentData("user_name")
    byte[] intentData1;
    @IntentData("user_pass")
    char[] intentData2;
    @IntentData("user_age")
    double[] intentData3;
    @IntentData
    float[] intentData4;
    @IntentData
    int[] intentData5;
    @IntentData
    long[] intentData6;
    @IntentData
    android.os.Parcelable[] intentData7;
    @IntentData
    java.lang.CharSequence[] intentData8;
    @IntentData
    java.lang.String[] intentData9;
    @IntentData
    short[] intentData10;
    @IntentData
    boolean[] intentData11;
    @IntentData
    android.os.Parcelable intentData12;
    @IntentData
    boolean intentData13;
    @IntentData
    byte intentData14;
    @IntentData
    char intentData15;
    @IntentData
    double intentData16;
    @IntentData
    float intentData17;
    @IntentData
    int intentData18;
    @IntentData
    java.io.Serializable intentData19;
    @IntentData
    java.lang.CharSequence intentData20;
    @IntentData
    java.lang.String intentData21;
    @IntentData
    long intentData22;
    @IntentData
    short intentData23;
    @IntentData
    android.os.Bundle intentData24;
    @IntentData
    java.util.ArrayList<java.lang.Integer> intentData25;
    @IntentData
    java.util.ArrayList<? extends android.os.Parcelable> intentData26;
    @IntentData
    java.util.ArrayList<java.lang.String> intentData27;
    @IntentData
    java.util.ArrayList<java.lang.CharSequence> intentData28;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        IntentBinderIml.bind(this);
        ((TextView) findViewById(R.id.text)).setText("value=" + value + ",index=" + index + ", intentData9=" + Arrays.toString(intentData9));
    }
}
