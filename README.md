# Tools for:
```
1、Inject Views，same as ButterKnife，avoid repeated works for findViewById or something.
2、Save state when onSaveInstanceState，or Restore state when onRestoreInstanceState，onCreate.
3、Activity router and Intent data binder.
```

# Integration:
```
Root project's build.gradle:
dependencies {
    classpath 'com.deparse.dpframe.tools:DPTools-gradle-plugin:3.0.0'
}

Module's build.gradle:
apply plugin: 'com.deparse.gradle.plugin.tools' //this is not need if you don't use R2 support like butterknife

dependencies {
    compile "com.deparse.dpframe.tools:DPTools:3.0.0"
    annotationProcessor "com.deparse.dpframe.tools:DPTools-compiler:3.0.0"
}
```

# Instructions:
1. ViewBinder:
```
//You can use plugins for ButterKnife to generate fields
@BindView(R.id.test)
//@BindView(R2.id.test) //if R2 supported
TextView textView;

DPViewBinder.bind(Activity activity);
DPViewBinder.bind(Dialog dialog);
DPViewBinder.bind(android.support.v4.app.Fragment fragment);
DPViewBinder.bind(android.app.Fragment fragment);
DPViewBinder.bind(Object host, View sourse);
DPViewBinder.bind(Object host, Object sourse, ViewFinder finder);
```
2. StateSaver:
```
@SaveState("test_key_3")
boolean[] v3;
@SaveState
byte v4;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    DPStateSaver.restore(this, savedInstanceState);
}
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(DPStateSaver.save(this, outState));
}

@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(DPStateSaver.restore(this, savedInstanceState));
}
```
3. Router:
```
@Route("SecondActivity")
public class SecondActivity extends BaseActivity {
    @IntentData
    String value;
    @IntentData("test_key")
    int index;
    @IntentData
    int[] intentData5;
    ....
    
//        DPRouter.builder()
//                .putExtra("value", "testValue")
//                .putExtra("test_key", -1)
//                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                .start(this, DPRoutes.SECONDACTIVITY);
//        DPRouter.start(this, DPRoutes.SECONDACTIVITY);
        SecondActivityRouteHelper //with @IntentData and @Route
                .builder()
                .withIndex(-1)
                .withValue("testValue")
                .start(this);
//        ThirdActivityRouteHelper //with @IntentData but without @Route
//                .builder()
//                .start(this);
       
The file SecondActivityRouteHelper & DPRoutes is auto generated when the project is built;
DPRoutes.SECONDACTIVITY is generated for @Route("SecondActivity"), so that you don't need to type route strings repeatedly
```

# Recommended Plugin:
https://plugins.jetbrains.com/plugin/10258-dpframeplugin
<br/>You can also install the plugin in android studio or intellij idea.
<br/>File=>settings=>plugins=>browse repositories,and type 'dpframe' in searching box, then you can install it.

# Enjoy yourself!
