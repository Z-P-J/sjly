package io.haydar.filescanner;

import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Haydar
 * @Package io.haydar.filescanner
 * @DATE 2017-04-24
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class FileScannerTest {
    public static final String TAG = "FileScannerTest";
    private long mStartTime, mEndTime;

    @Before
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Test
    public void startTest() {
       // FileScanner.getInstance(InstrumentationRegistry.getTargetContext()).start();
    }

    @After
    public void end() {
        mEndTime = System.currentTimeMillis();
        Log.d(TAG, "运行时间=" + (mEndTime - mStartTime) + "ms");
    }

}
