package com.sunfusheng;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author sunfusheng on 2018/2/1.
 */
abstract public class GroupRecyclerViewAdapter<T> extends RecyclerView.Adapter<GroupViewHolder> {

    private static final String TAG = "GroupAdapter";

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_CHILD = 1;
    public static final int TYPE_FOOTER = 2;

    protected Context context;
    protected LayoutInflater inflater;
    protected List<List<T>> groups;
    protected int itemPosition;

    public GroupRecyclerViewAdapter(Context context) {
        this(context, new ArrayList<>());
    }

    public GroupRecyclerViewAdapter(Context context, T[][] groups) {
        init(context, GroupAdapterUtils.convertGroupsData(groups, minCountPerGroup()));
    }

    public GroupRecyclerViewAdapter(Context context, List<List<T>> groups) {
        init(context, groups);
    }

    private void init(Context context, List<List<T>> groups) {
        GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup());
        this.context = context;
        this.groups = groups;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setGroups(T[][] groups) {
        GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup());
        this.groups = GroupAdapterUtils.convertGroupsData(groups, minCountPerGroup());
        notifyDataSetChanged();
    }

    public void setGroups(List<List<T>> groups) {
        GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup());
        this.groups = groups;
        notifyDataSetChanged();
    }

    public List<List<T>> getGroups() {
        return this.groups;
    }

    public List<T> getGroupItems(int groupPosition) {
        return getGroups().get(groupPosition);
    }

    public T getItem(int groupPosition, int childPosition) {
        return getGroupItems(groupPosition).get(childPosition);
    }

    public List<T> getGroupItemsWithoutHeader(int groupPosition) {
        if (checkGroupPosition(groupPosition)) {
            List<T> items = new ArrayList<>(getGroups().get(groupPosition));
            if (showHeader()) {
                items.remove(0);
            }
            return items;
        }
        return null;
    }

    public List<T> getGroupItemsWithoutHeaderFooter(int groupPosition) {
        if (checkGroupPosition(groupPosition)) {
            List<T> items = new ArrayList<>(getGroups().get(groupPosition));
            if (showFooter()) {
                items.remove(items.size() - 1);
            }
            if (showHeader()) {
                items.remove(0);
            }
            return items;
        }
        return null;
    }

    @Override
    public GroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GroupViewHolder(inflater.inflate(getLayoutId(viewType), parent, false));
    }

    @Override
    public void onBindViewHolder(GroupViewHolder holder, int position) {
        int viewType = confirmItemViewType(position);
        int groupPosition = getGroupPosition(position);
        int childPosition = getGroupChildPosition(groupPosition, position);
        T item = getItem(groupPosition, childPosition);

        if (TYPE_HEADER == viewType) {
            onBindHeaderViewHolder(holder, item, groupPosition);
        } else if (TYPE_CHILD == viewType) {
            onBindChildViewHolder(holder, item, groupPosition, childPosition);
        } else if (TYPE_FOOTER == viewType) {
            onBindFooterViewHolder(holder, item, groupPosition);
        }

        if (null != onItemClickListener) {
            holder.itemView.setOnClickListener(view -> {
                if (null != onItemClickListener) {
                    onItemClickListener.onItemClick(this, item, groupPosition, childPosition);
                }
            });
        }

        if (null != onItemLongClickListener) {
            holder.itemView.setOnLongClickListener(view -> {
                if (null != onItemLongClickListener) {
                    onItemLongClickListener.onItemLongClick(this, item, groupPosition, childPosition);
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        this.itemPosition = position;
        int viewType = confirmItemViewType(position);
        int groupPosition = getGroupPosition(position);

        if (TYPE_HEADER == viewType) {
            return getHeaderItemViewType(groupPosition);
        } else if (TYPE_CHILD == viewType) {
            int childPosition = getGroupChildPosition(groupPosition, position);
            return getChildItemViewType(groupPosition, childPosition);
        } else if (TYPE_FOOTER == viewType) {
            return getFooterItemViewType(groupPosition);
        }
        return super.getItemViewType(position);
    }

    public int confirmItemViewType(int itemPosition) {
        int itemCount = 0;
        for (int i = 0, groupsCount = groupsCount(); i < groupsCount; i++) {
            if (showHeader()) {
                itemCount += 1;
                if (itemPosition < itemCount) {
                    return TYPE_HEADER;
                }
            }

            itemCount += countGroupChildren(i);
            if (itemPosition < itemCount) {
                return TYPE_CHILD;
            }

            if (showFooter()) {
                itemCount += 1;
                if (itemPosition < itemCount) {
                    return TYPE_FOOTER;
                }
            }
        }
        throw new IndexOutOfBoundsException("Confirm item type failed, " + "itemPosition = " + itemPosition + ", itemCount = " + itemCount);
    }

    public int getLayoutId(int viewType) {
        int itemViewType = confirmItemViewType(getItemPosition());
        if (TYPE_HEADER == itemViewType) {
            return getHeaderLayoutId(viewType);
        } else if (TYPE_CHILD == itemViewType) {
            return getChildLayoutId(viewType);
        } else if (TYPE_FOOTER == itemViewType) {
            return getFooterLayoutId(viewType);
        }
        return 0;
    }

    /**
     * @return 返回列表下标
     */
    public int getItemPosition() {
        return itemPosition;
    }

    /**
     * @param groupPosition 组下标
     * @param childPosition 组项下标
     * @return 返回列表的position
     */
    public int getPosition(int groupPosition, int childPosition) {
        int position = 0;
        for (int i = 0; i < groupPosition; i++) {
            position += GroupAdapterUtils.countGroupItems(getGroups(), groupPosition);
        }
        position += childPosition;
        return position;
    }

    /**
     * @param itemPosition 列表下标
     * @return 返回所在组
     */
    public int getGroupPosition(int itemPosition) {
        int itemsCount = 0;
        for (int i = 0, groupsCount = groupsCount(); i < groupsCount; i++) {
            itemsCount += countGroupItems(i);
            if (itemPosition < itemsCount) {
                return i;
            }
        }
        return -1;
    }

    /**
     * @param groupPosition 组下标
     * @return 返回指定组header的列表下标，如果没有header返回-1
     */
    public int getGroupHeaderPosition(int groupPosition) {
        if (showHeader() && checkGroupPosition(groupPosition)) {
            return countGroupsItemsRange(0, groupPosition);
        }
        return -1;
    }

    /**
     * @param groupPosition 组下标
     * @param itemPosition  列表下标
     * @return 返回所在组的下标
     */
    public int getGroupChildPosition(int groupPosition, int itemPosition) {
        if (checkGroupPosition(groupPosition)) {
            int position = itemPosition - countGroupsItemsRange(0, groupPosition);
            if (0 <= position) {
                return position;
            }
        }
        return -1;
    }

    /**
     * @param groupPosition 组下标
     * @return 返回指定组footer的列表下标，如果没有footer返回-1
     */
    public int getGroupFooterPosition(int groupPosition) {
        if (showFooter() && checkGroupPosition(groupPosition)) {
            return countGroupsItemsRange(0, groupPosition + 1) - 1;
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return countGroupsItemsRange(0, groupsCount());
    }

    protected boolean checkGroupPosition(int groupPosition) {
        return groupPosition >= 0 && groupPosition < groupsCount();
    }

    protected boolean checkGroupPositionForInsert(int groupPosition) {
        return groupPosition >= 0 && groupPosition <= groupsCount();
    }

    protected boolean checkChildPosition(int childPosition, int groupItemsCount) {
        return childPosition >= 0 && childPosition < groupItemsCount;
    }

    protected boolean checkChildPositionForInsert(int childPosition, int groupItemsCount) {
        return childPosition >= 0 && childPosition <= groupItemsCount;
    }

    public int groupsCount() {
        return GroupAdapterUtils.countGroups(groups);
    }

    public int countGroupItems(int groupPosition) {
        return GroupAdapterUtils.countGroupItems(groups, groupPosition);
    }

    public int countGroupChildren(int groupPosition) {
        return GroupAdapterUtils.countGroupChildren(groups, groupPosition, minCountPerGroup());
    }

    public int countGroupsItemsRange(int start, int count) {
        return GroupAdapterUtils.countGroupsItemsRange(groups, start, count);
    }

    /****************************************************************
     * insert operation
     ***************************************************************/

    public boolean insertGroup(int groupPosition, T[] group) {
        return insertGroup(groupPosition, group, true);
    }

    public boolean insertGroup(int groupPosition, T[] group, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && GroupAdapterUtils.checkGroupData(group, minCountPerGroup())) {
            List<T> list = Arrays.asList(group);
            return insertGroup(groupPosition, new ArrayList<>(list), withAnim);
        }
        return false;
    }

    public boolean insertGroup(int groupPosition, List<T> group) {
        return insertGroup(groupPosition, group, true);
    }

    public boolean insertGroup(int groupPosition, List<T> group, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && GroupAdapterUtils.checkGroupData(group, minCountPerGroup())) {
            this.groups.add(groupPosition, group);
            if (withAnim) {
                int positionStart = countGroupsItemsRange(0, groupPosition);
                notifyItemRangeInserted(positionStart, group.size());
                notifyItemRangeChanged(positionStart + group.size(), getItemCount() - positionStart - group.size());
            } else {
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public boolean insertGroups(int groupPosition, T[][] groups) {
        return insertGroups(groupPosition, groups, true);
    }

    public boolean insertGroups(int groupPosition, T[][] groups, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup())) {
            List<List<T>> lists = GroupAdapterUtils.convertGroupsData(groups, minCountPerGroup());
            return insertGroups(groupPosition, lists, withAnim);
        }
        return false;
    }

    public boolean insertGroups(int groupPosition, List<List<T>> groups) {
        return insertGroups(groupPosition, groups, true);
    }

    public boolean insertGroups(int groupPosition, List<List<T>> groups, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup())) {
            this.groups.addAll(groupPosition, groups);
            if (withAnim) {
                int groupItemCount = GroupAdapterUtils.countGroupsItemsRange(groups, 0, groups.size());
                int positionStart = countGroupsItemsRange(0, groupPosition);
                notifyItemRangeInserted(positionStart, groupItemCount);
                notifyItemRangeChanged(positionStart + groupItemCount, getItemCount() - positionStart - groupItemCount);
            } else {
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public boolean insertItem(int groupPosition, int childPosition, T item) {
        return insertItem(groupPosition, childPosition, item, true);
    }

    public boolean insertItem(int groupPosition, int childPosition, T item, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && null != item) {
            int groupItemsCount = countGroupItems(groupPosition);
            if (!checkChildPositionForInsert(childPosition, groupItemsCount)) {
                return false;
            }

            getGroupItems(groupPosition).add(childPosition, item);
            if (withAnim) {
                int positionStart = countGroupsItemsRange(0, groupPosition) + childPosition;
                notifyItemInserted(positionStart);
                notifyItemRangeChanged(positionStart + 1, getItemCount() - positionStart - 1);
            } else {
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public boolean insertItems(int groupPosition, int childPosition, T[] items) {
        return insertItems(groupPosition, childPosition, items, true);
    }

    public boolean insertItems(int groupPosition, int childPosition, T[] items, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && !GroupAdapterUtils.isEmpty(items)) {
            List<T> list = Arrays.asList(items);
            return insertItems(groupPosition, childPosition, new ArrayList<>(list), withAnim);
        }
        return false;
    }

    public boolean insertItems(int groupPosition, int childPosition, List<T> items) {
        return insertItems(groupPosition, childPosition, items, true);
    }

    public boolean insertItems(int groupPosition, int childPosition, List<T> items, boolean withAnim) {
        if (checkGroupPositionForInsert(groupPosition) && !GroupAdapterUtils.isEmpty(items)) {
            int groupItemsCount = countGroupItems(groupPosition);
            if (!checkChildPositionForInsert(childPosition, groupItemsCount)) {
                return false;
            }

            getGroupItems(groupPosition).addAll(childPosition, items);
            if (withAnim) {
                int positionStart = countGroupsItemsRange(0, groupPosition) + childPosition;
                notifyItemRangeInserted(positionStart, items.size());
                notifyItemRangeChanged(positionStart + items.size(), getItemCount() - positionStart - items.size());
            } else {
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    /****************************************************************
     * remove operation
     ***************************************************************/

    public boolean removeGroup(int groupPosition) {
        return removeGroup(groupPosition, true);
    }

    public boolean removeGroup(int groupPosition, boolean withAnim) {
        if (checkGroupPosition(groupPosition)) {
            int positionStart = countGroupsItemsRange(0, groupPosition);
            int itemCount = countGroupItems(groupPosition);
            this.groups.remove(groupPosition);
            if (withAnim) {
                notifyItemRangeRemoved(positionStart, itemCount);
                notifyItemRangeChanged(positionStart, getItemCount() - positionStart);
            } else {
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public boolean removeGroups(int groupPosition, int count) {
        return removeGroups(groupPosition, count, true);
    }

    public boolean removeGroups(int groupPosition, int count, boolean withAnim) {
        if (checkGroupPosition(groupPosition) && count > 0) {
            int groupsCount = count;
            if (groupPosition + count > groupsCount()) {
                groupsCount = groupsCount() - groupPosition;
            }

            int positionStart = countGroupsItemsRange(0, groupPosition);
            int itemCount = countGroupsItemsRange(groupPosition, groupsCount);

            for (int i = 0; i < groupsCount; i++) {
                this.groups.remove(groupPosition);
            }

            if (withAnim) {
                notifyItemRangeRemoved(positionStart, itemCount);
                notifyItemRangeChanged(positionStart, getItemCount() - positionStart);
            } else {
                notifyDataSetChanged();
            }
            return true;
        }
        return false;
    }

    public boolean removeItem(int groupPosition, int childPosition) {
        return removeItem(groupPosition, childPosition, true);
    }

    public boolean removeItem(int groupPosition, int childPosition, boolean withAnim) {
        return removeItems(groupPosition, childPosition, 1, withAnim);
    }

    public boolean removeItems(int groupPosition, int childPosition, int count) {
        return removeItems(groupPosition, childPosition, count, true);
    }

    public boolean removeItems(int groupPosition, int childPosition, int count, boolean withAnim) {
        if (checkGroupPosition(groupPosition) && count > 0) {
            int positionStart = countGroupsItemsRange(0, groupPosition);
            int groupItemsCount = countGroupItems(groupPosition);
            if (!checkChildPosition(childPosition, groupItemsCount)) {
                return false;
            }

            int childCount = count;
            if (childPosition + count > groupItemsCount) {
                childCount = groupItemsCount - childPosition;
            }

            if (groupItemsCount < minCountPerGroup() + childCount) {
                removeGroup(groupPosition);
            } else {
                for (int i = 0; i < childCount; i++) {
                    getGroupItems(groupPosition).remove(childPosition);
                }

                if (withAnim) {
                    notifyItemRangeRemoved(positionStart + childPosition, childCount);
                    notifyItemRangeChanged(positionStart, getItemCount() - positionStart);
                } else {
                    notifyDataSetChanged();
                }
            }
            return true;
        }
        return false;
    }

    /****************************************************************
     * update operation
     ***************************************************************/

    public boolean updateGroup(int groupPosition, T[] group) {
        if (checkGroupPosition(groupPosition) && GroupAdapterUtils.checkGroupData(group, minCountPerGroup())) {
            List<T> list = Arrays.asList(group);
            return updateGroup(groupPosition, new ArrayList<>(list));
        }
        return false;
    }

    public boolean updateGroup(int groupPosition, List<T> group) {
        if (checkGroupPosition(groupPosition) && GroupAdapterUtils.checkGroupData(group, minCountPerGroup())) {
            int positionStart = countGroupsItemsRange(0, groupPosition);
            this.groups.remove(groupPosition);
            this.groups.add(groupPosition, group);
            notifyItemRangeChanged(positionStart, getItemCount() - positionStart);
            return true;
        }
        return false;
    }

    public boolean updateGroups(int groupPosition, T[][] groups) {
        if (!GroupAdapterUtils.isEmpty(groups)) {
            return updateGroups(groupPosition, GroupAdapterUtils.convertGroupsData(groups, minCountPerGroup()));
        }
        return false;
    }

    public boolean updateGroups(int groupPosition, List<List<T>> groups) {
        if (checkGroupPosition(groupPosition) && GroupAdapterUtils.checkGroupsData(groups, minCountPerGroup())) {
            int positionStart = countGroupsItemsRange(0, groupPosition);
            int size = groups.size();
            if (groupPosition + size > groupsCount()) {
                size = groupsCount() - groupPosition;
            }

            for (int i = 0; i < size; i++) {
                this.groups.remove(groupPosition);
            }
            this.groups.addAll(groupPosition, groups.subList(0, size));
            notifyItemRangeChanged(positionStart, getItemCount() - positionStart);
            return true;
        }
        return false;
    }

    public boolean updateItem(int groupPosition, int childPosition, T item) {
        if (null != item && checkGroupPosition(groupPosition)) {
            int positionStart = countGroupsItemsRange(0, groupPosition);
            int groupItemsCount = countGroupItems(groupPosition);
            if (!checkChildPosition(childPosition, groupItemsCount)) {
                return false;
            }

            getGroupItems(groupPosition).remove(childPosition);
            getGroupItems(groupPosition).add(childPosition, item);
            notifyItemChanged(positionStart + childPosition);
            return true;
        }
        return false;
    }

    public boolean updateItems(int groupPosition, int childPosition, T[] items) {
        if (!GroupAdapterUtils.isEmpty(items)) {
            List<T> list = Arrays.asList(items);
            return updateItems(groupPosition, childPosition, new ArrayList<>(list));
        }
        return false;
    }

    public boolean updateItems(int groupPosition, int childPosition, List<T> items) {
        if (checkGroupPosition(groupPosition)) {
            int positionStart = countGroupsItemsRange(0, groupPosition);
            int groupItemsCount = countGroupItems(groupPosition);
            if (!checkChildPosition(childPosition, groupItemsCount)) {
                return false;
            }

            int childCount = items.size();
            if (childPosition + childCount > groupItemsCount) {
                childCount = groupItemsCount - childPosition;
            }

            for (int i = 0; i < childCount; i++) {
                getGroupItems(groupPosition).remove(childPosition);
            }
            getGroupItems(groupPosition).addAll(childPosition, items.subList(0, childCount));
            notifyItemRangeChanged(positionStart + childPosition, childCount);
            return true;
        }
        return false;
    }

    public int getHeaderItemViewType(int groupPosition) {
        return TYPE_HEADER;
    }

    public int getChildItemViewType(int groupPosition, int childPosition) {
        return TYPE_CHILD;
    }

    public int getFooterItemViewType(int groupPosition) {
        return TYPE_FOOTER;
    }

    public int minCountPerGroup() {
        return (showHeader() ? 1 : 0) + (showFooter() ? 1 : 0);
    }

    public boolean isHeader(int groupPosition, int childPosition) {
        if (checkGroupPosition(groupPosition)) {
            if (showHeader() && 0 < countGroupItems(groupPosition) && 0 == childPosition) {
                return true;
            }
        }
        return false;
    }

    public boolean isGroupLastItem(int groupPosition, int childPosition) {
        if (checkGroupPosition(groupPosition)) {
            int groupItemsCount = countGroupItems(groupPosition);
            if (0 < groupItemsCount && childPosition == groupItemsCount - 1) {
                return true;
            }
        }
        return false;
    }

    public boolean isFooter(int groupPosition, int childPosition) {
        return showFooter() && isGroupLastItem(groupPosition, childPosition);
    }

    public boolean showHeader() {
        return true;
    }

    public boolean showFooter() {
        return false;
    }

    /****************************************************************
     * abstract method
     ***************************************************************/

    abstract public int getHeaderLayoutId(int viewType);

    abstract public int getChildLayoutId(int viewType);

    abstract public int getFooterLayoutId(int viewType);

    abstract public void onBindHeaderViewHolder(GroupViewHolder holder, T item, int groupPosition);

    abstract public void onBindChildViewHolder(GroupViewHolder holder, T item, int groupPosition, int childPosition);

    abstract public void onBindFooterViewHolder(GroupViewHolder holder, T item, int groupPosition);

    /****************************************************************
     * item click listener
     ***************************************************************/

    protected OnItemClickListener<T> onItemClickListener;
    protected OnItemLongClickListener<T> onItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener<T> {
        void onItemClick(GroupRecyclerViewAdapter adapter, T data, int groupPosition, int childPosition);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(GroupRecyclerViewAdapter adapter, T data, int groupPosition, int childPosition);
    }

}
