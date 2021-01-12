package com.zpj.shouji.market.installer;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class InstallAccessibilityService extends AccessibilityService {

    private static final String TAG = InstallAccessibilityService.class.getSimpleName();

    private final String[] key = new String[]{"卸载", "安装", "继续", "继续安装", "替换", "下一步", "仅允许一次", "完成", "确定"};

    /**
     * 当指定事件发出服务时
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.d(TAG, "窗口事件的包名" + event.getPackageName());

        if (event == null || event.getPackageName() == null
                || !event.getPackageName().toString().contains("packageinstaller"))
            return;

        if (event.getSource() != null) {
            for (String s : key) {
                AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                if (nodeInfo != null) {
                    List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(s);
                    System.out.println("节点的个数" + nodes.size());
                    if (nodes != null && !nodes.isEmpty()) {
                        AccessibilityNodeInfo node;
                        for (int j = 0; j < nodes.size(); j++) {
                            node = nodes.get(j);
                            System.out.println("节点的类名" + node.getClassName().toString());
                            if ((node.getClassName().equals("android.widget.Button") || node.getClassName().equals("android.widget.TextView")) && node.isEnabled()) {
                                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onServiceConnected() {
        Log.e(TAG, "无障碍服务已开启");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "无障碍服务已关闭");
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {

    }
}
