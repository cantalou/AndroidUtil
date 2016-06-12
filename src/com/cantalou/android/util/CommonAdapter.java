package com.cantalou.android.util;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * 
 * @author cantalou
 * @date 2016年2月29日 上午10:55:43
 */
public abstract class CommonAdapter<T> extends BaseAdapter {

    private int[] resId;

    private LayoutInflater inflater;

    private List<T> data;

    protected View currentView;

    protected int currentPosition;

    public CommonAdapter(Context cxt, List<T> data, int... resId) {
        super();
        this.resId = resId;
        this.data = data;
        inflater = LayoutInflater.from(cxt);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(resId[getItemViewType(position)], parent, false);
        }
        currentView = convertView;
        currentPosition = position;
        handle(data.get(position));
        return convertView;
    }

    @SuppressWarnings("unchecked")
    public final <E> E findViewById(int id) {
        View result;
        SparseArray<View>[] viewHolders = (SparseArray<View>[]) currentView.getTag();
        if (viewHolders == null) {
            viewHolders = new SparseArray[7];
            currentView.setTag(viewHolders);
        }
        SparseArray<View> viewHolder = viewHolders[id & 0x00000007];
        result = viewHolder.get(id);
        if (result == null) {
            result = currentView.findViewById(id);
            viewHolder.put(id, result);
        }
        return (E) result;
    }

    public abstract void handle(T data);

    /**
     * fix bug observer may be null
     *
     * @param observer
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}
