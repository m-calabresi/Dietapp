package com.example.dietapp.viewmodel.livedata;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MutableListHolder<T> {
    private final List<T> list;

    private Integer positionStart;
    private Integer itemCount;
    private UpdateType updateType;

    public MutableListHolder() {
        this.list = new ArrayList<>();
        this.positionStart = null;
        this.itemCount = null;
        this.updateType = null;
    }

    public MutableListHolder(List<T> items) {
        this.list = items;
        this.positionStart = 0;
        this.itemCount = items.size();
        this.updateType = UpdateType.INSERT_RANGE;
    }

    public List<T> asList() {
        return this.list;
    }

    protected void addItem(T item) {
        this.list.add(item);
        this.positionStart = this.list.size() - 1;
        this.itemCount = null;
        this.updateType = UpdateType.INSERT;
    }

    protected void addItem(T item, int position) {
        this.list.add(position, item);
        this.positionStart = position;
        this.itemCount = null;
        this.updateType = UpdateType.INSERT;
    }

    protected void addItemRange(List<T> items) {
        addItemRange(items, this.list.size());
    }

    protected void addItemRange(List<T> items, int positionStart) {
        this.list.addAll(positionStart, items);
        this.positionStart = positionStart;
        this.itemCount = items.size();
        this.updateType = UpdateType.INSERT_RANGE;
    }

    protected void removeItem(T item) {
        if ((this.positionStart = this.list.indexOf(item)) == -1)
            return;
        this.list.remove(item);
        this.itemCount = null;
        this.updateType = UpdateType.REMOVE;
    }

    protected void removeItem(int position) {
        this.list.remove(position);
        this.positionStart = position;
        this.itemCount = null;
        this.updateType = UpdateType.REMOVE;
    }

    protected void removeItemRange(List<T> items) {
        this.positionStart = Collections.indexOfSubList(this.list, items);

        if (this.positionStart == -1)
            return;

        this.list.subList(this.positionStart, items.size()).clear();
        this.itemCount = this.positionStart - this.list.size();
        this.updateType = UpdateType.REMOVE_RANGE;
    }

    protected void removeItemRange(List<T> items, int positionStart) {
        this.positionStart = Collections.indexOfSubList(this.list, items);

        if (this.positionStart == -1 || this.positionStart != positionStart)
            return;

        this.list.subList(this.positionStart, items.size()).clear();
        this.itemCount = this.positionStart - this.list.size();
        this.updateType = UpdateType.REMOVE_RANGE;
    }

    protected void setItem(T item, int position) {
        this.list.set(position, item);
        this.positionStart = position;
        this.itemCount = null;
        this.updateType = UpdateType.CHANGE;
    }

    protected void setItemRange(List<T> items, int positionStart) {
        final List<T> range = this.list.subList(positionStart, items.size());

        for (int i = 0; i < range.size(); i++)
            range.set(i, items.get(i));

        this.positionStart = positionStart;
        this.itemCount = items.size();
        this.updateType = UpdateType.CHANGE_RANGE;
    }

    protected T get(int position) {
        return this.list.get(position);
    }

    protected int size() {
        return this.list.size();
    }

    public void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.updateType.notifyChange(adapter, positionStart, itemCount);
    }

    private enum UpdateType {
        INSERT {
            @Override
            protected void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int position, Integer itemCount) {
                if (itemCount != null)
                    throw new UnsupportedOperationException("Range insertion requires calling UpdateType#INSERT_RANGE");
                adapter.notifyItemInserted(position);
            }
        },
        INSERT_RANGE {
            @Override
            protected void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int positionStart, Integer itemCount) {
                adapter.notifyItemRangeInserted(positionStart, itemCount);
            }
        },
        REMOVE {
            @Override
            protected void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int position, Integer itemCount) {
                if (itemCount != null)
                    throw new UnsupportedOperationException("Range removal requires calling UpdateType#REMOVE_RANGE");
                adapter.notifyItemRemoved(position);
            }
        },
        REMOVE_RANGE {
            @Override
            protected void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int positionStart, Integer itemCount) {
                adapter.notifyItemRangeRemoved(positionStart, itemCount);
            }
        },
        CHANGE {
            @Override
            protected void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int position, Integer itemCount) {
                if (itemCount != null)
                    throw new UnsupportedOperationException("Range change requires calling UpdateType#CHANGE_RANGE");
                adapter.notifyItemChanged(position);
            }
        },
        CHANGE_RANGE {
            @Override
            protected void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int positionStart, Integer itemCount) {
                adapter.notifyItemRangeChanged(positionStart, itemCount);
            }
        };

        protected abstract void notifyChange(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter, int positionStart, Integer itemCount);
    }
}
