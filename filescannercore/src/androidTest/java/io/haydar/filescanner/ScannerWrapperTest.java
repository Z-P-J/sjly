package io.haydar.filescanner;

import android.os.Environment;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

/**
 * @author Haydar
 * @Package io.haydar.filescanner
 * @DATE 2017-04-24
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScannerWrapperTest {
    public static final String TAG = "ScannerWrapperTest";
    private long mStartTime, mEndTime;

    @Before
    public void start() {
        mStartTime = System.currentTimeMillis();
    }

    @Test
    public void scanDirsTest() {
        ArrayList<FileInfo> fileInfoArrayList=ScannerWrapper.scanDirs(Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.i(TAG, "scanDirsTest: "+fileInfoArrayList.size());
    }

    @After
    public void end() {
        mEndTime = System.currentTimeMillis();
        Log.i(TAG, "运行时间=" + (mEndTime - mStartTime) + "ms");
    }
}
