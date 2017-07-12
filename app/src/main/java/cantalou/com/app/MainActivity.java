package cantalou.com.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.cantalou.android.util.Log;
import com.cantalou.android.util.ReflectUtil;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.explicitType).setOnClickListener(this);
        findViewById(R.id.fuzzyType).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.explicitType: {
                Outer out = new Outer();
                long start = System.currentTimeMillis();
                Class[] paramType = new Class[]{CharSequence.class};
                for (int i = 0; i < 100000; i++) {
                    ReflectUtil.findMethod(Outer.class, "staticOuterMethod", paramType);
                    //ReflectUtil.invoke(out, "staticOuterMethod","staticOuterMethod", new Class[]{CharSequence.class} , "4");
                }
                Log.d("staticOuterMethod explicit CharSequence type time {}", System.currentTimeMillis() - start);
                break;
            }
            case R.id.fuzzyType: {
                Outer out = new Outer();
                long start = System.currentTimeMillis();
                Class[] paramType = new Class[]{String.class};
                for (int i = 0; i < 100000; i++) {
                    ReflectUtil.findMethod(Outer.class, "staticOuterMethod", paramType);
                    //ReflectUtil.invoke(out, "staticOuterMethod", paramType, "4");
                }
                Log.d("staticOuterMethod explicit String type time {}", System.currentTimeMillis() - start);
                break;
            }
        }
    }

    static class Inner {
        String str;
        static String staticStr;

        String innerMethod() {
            return "innerMethod";
        }

        static String staticInnerMethod() {
            return "staticInnerMethod";
        }
    }

    static class Outer {
        String str;
        static String staticStr;

        Inner inner = new Inner();
        static Inner staticInner = new Inner();

        String outerMethod() {
            return "outerMethod";
        }

        String outerMethod(String s) {
            return "outerMethodString" + s;
        }

        String outerMethod(CharSequence s) {
            return "outerMethodString" + s;
        }

        static String staticOuterMethod() {
            return "staticOuterMethod" + 0;
        }

        static String staticOuterMethod(String s) {
            return "staticOuterMethod" + s;
        }

        static String staticOuterMethod(CharSequence s) {
            return "staticOuterMethod" + s;
        }

        static String staticOuterMethod(int s) {
            return "staticOuterMethod" + s;
        }

        static String staticOuterMethod(Integer s) {
            return "staticOuterMethod" + s;
        }

        Inner getStaticInnerMethod() {
            return staticInner;
        }
    }
}
