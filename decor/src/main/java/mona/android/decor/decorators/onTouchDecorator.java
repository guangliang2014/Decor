package mona.android.decor.decorators;

import android.app.Activity;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import hugo.weaving.DebugLog;
import mona.android.decor.AttrsDecorator;
import mona.android.decor.R;

/**
 * Created by cheikhna on 17/02/2015.
 */
public class OnTouchDecorator extends AttrsDecorator<View> {

    private Activity mContainerActivity;

    @DebugLog
    public OnTouchDecorator(Activity activity) {
        mContainerActivity = activity;
    }

    @DebugLog
    @NotNull
    @Override
    protected int[] attrs() {
        return new int[]{R.attr.onTouch};
    }

    @DebugLog
    @NotNull
    @Override
    protected Class<View> clazz() {
        return View.class;
    }

    @DebugLog
    @Override
    protected void apply(final View view, int attr, final TypedValue value) {
        Log.i("TEST", " apply called ");
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.i("TEST", " onTouch called with event "+ event);

                Log.i("TEST", " onTouch called with value.string =" + value.string);
                Method mHandler = null;
                try {
                    if(mContainerActivity != null) {
                        Log.i("TEST", " -> trying to with handler for "+ value.string);
                        mHandler = mContainerActivity.getClass().getMethod(value.string.toString());
                    }
                } catch (NoSuchMethodException e) {
                    Log.i("TEST", " onTouch - NoSuchMethodException");
                    e.printStackTrace();
                }

                if (mHandler == null) return false;
                try {
                    //mHandler.invoke(v.getContext(), view.getClass());//not sure that view.getClass() does correctly replace View.this
                    Log.i("TEST", " -> invoking "+ value.string + " method");
                    mHandler.invoke(mContainerActivity);
                    return true;
                } catch (IllegalAccessException e) {
                    Log.i("TEST", " onTouch - IllegalAccessException");
                    e.printStackTrace();
                    throw new IllegalStateException("Could not execute non "
                            + "public method of the activity", e);
                } catch (InvocationTargetException e) {
                    Log.i("TEST", " onTouch - InvocationTargetException");
                    e.printStackTrace();
                    throw new IllegalStateException("Could not execute "
                            + "method of the activity", e);
                }
            }
        });
    }

}
