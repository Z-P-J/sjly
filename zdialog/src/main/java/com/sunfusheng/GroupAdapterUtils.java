package com.sunfusheng;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author sunfusheng on 2018/2/4.
 */
public class GroupAdapterUtils {

    private static final String TAG = "GroupAdapterUtils";

    public static boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    public static <T> boolean isEmpty(T[] array) {
        return null == array || array.length == 0;
    }

    public static <T> boolean checkGroupData(T[] group, int minCountPerGroup) {
        if (!isEmpty(group)) {
            return minCountPerGroup <= group.length;
        }
        return false;
    }

    public static <T> boolean checkGroupData(List<T> group, int minCountPerGroup) {
        if (!isEmpty(group)) {
            return minCountPerGroup <= group.size();
        }
        return false;
    }

    public static <T> boolean checkGroupsData(T[][] groups, int minCountPerGroup) {
        if (!isEmpty(groups)) {
            return checkGroupsData(convertGroupsData(groups, minCountPerGroup), minCountPerGroup);
        }
        return false;
    }

    public static <T> boolean checkGroupsData(List<List<T>> groups, int minCountPerGroup) {
        if (isEmpty(groups)) {
            return false;
        }

        Iterator<List<T>> iterator = groups.iterator();
        while (iterator.hasNext()) {
            List<T> group = iterator.next();
            if (isEmpty(group) || group.size() < minCountPerGroup) {
                iterator.remove();
                Log.w(TAG, "Data illegal, already removed group = " + group);
            }
        }
        return !isEmpty(groups);
    }

    public static <T> List<List<T>> convertGroupsData(T[][] groups, int minCountPerGroup) {
        List<List<T>> lists = new ArrayList<>();
        if (!isEmpty(groups)) {
            for (T[] group : groups) {
                if (!isEmpty(group) && group.length >= minCountPerGroup) {
                    List<T> list = new ArrayList<>();
                    list.addAll(Arrays.asList(group));
                    lists.add(list);
                }
            }
        }
        return lists;
    }

    /**
     * 返回所有组的个数
     *
     * @param groups
     * @param <T>
     * @return
     */
    public static <T> int countGroups(List<List<T>> groups) {
        return isEmpty(groups) ? 0 : groups.size();
    }

    /**
     * 返回指定组的组项的个数，包括header、child、footer
     *
     * @param groups
     * @param groupPosition 组下标
     * @param <T>
     * @return
     */
    public static <T> int countGroupItems(List<List<T>> groups, int groupPosition) {
        if (!isEmpty(groups)) {
            return isEmpty(groups.get(groupPosition)) ? 0 : groups.get(groupPosition).size();
        }
        return 0;
    }

    /**
     * 返回指定组所有child项的个数，只含有child，不包括header、footer
     *
     * @param groupPosition 组下标
     * @return
     */
    public static <T> int countGroupChildren(List<List<T>> groups, int groupPosition, int minCountPerGroup) {
        int groupItemsCount = countGroupItems(groups, groupPosition);
        if (0 == groupItemsCount) {
            return 0;
        }

        int childCount = groupItemsCount - minCountPerGroup;
        if (0 > childCount) {
            return 0;
        }
        return childCount;
    }

    /**
     * 返回多个组的组项的个数
     *
     * @param groups
     * @param start
     * @param count
     * @param <T>
     * @return
     */
    public static <T> int countGroupsItemsRange(List<List<T>> groups, int start, int count) {
        int itemCount = 0;
        for (int i = start, groupsCount = countGroups(groups); i < start + count && i < groupsCount; i++) {
            itemCount += countGroupItems(groups, i);
        }
        return itemCount;
    }

}
