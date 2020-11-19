package com.example.dietapp.viewmodel.livedata;

import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;

public class MutableListLiveData<T> extends MutableLiveData<MutableListHolder<T>> {
    public MutableListLiveData() {
        super();
        setValue(new MutableListHolder<>());
    }

    public MutableListLiveData(List<T> items) {
        super();
        setValue(new MutableListHolder<>(items));
    }

    public void addItem(T item) {
        getValueSafe().addItem(item);
        forceUpdate();
    }

    public void addItem(T item, int position) {
        getValueSafe().addItem(item, position);
        forceUpdate();
    }

    public void addItemRange(List<T> items) {
        getValueSafe().addItemRange(items);
        forceUpdate();
    }

    public void addItemRange(List<T> items, int startPosition) {
        getValueSafe().addItemRange(items, startPosition);
        forceUpdate();
    }

    public void removeItem(T item) {
        getValueSafe().removeItem(item);
        forceUpdate();
    }

    public void removeItem(int position) {
        getValueSafe().removeItem(position);
        forceUpdate();
    }

    public void removeItemRange(List<T> items) {
        getValueSafe().removeItemRange(items);
        forceUpdate();
    }

    public void removeItemRange(List<T> items, int positionStart) {
        getValueSafe().removeItemRange(items, positionStart);
        forceUpdate();
    }

    public void setItem(T item, int position) {
        getValueSafe().setItem(item, position);
        forceUpdate();
    }

    public void setItemRange(List<T> items, int positionStart) {
        getValueSafe().setItemRange(items, positionStart);
        forceUpdate();
    }

    public int size() {
        return getValueSafe().size();
    }

    public T get(int position) {
        return getValueSafe().get(position);
    }

    private MutableListHolder<T> getValueSafe() {
        return Objects.requireNonNull(this.getValue());
    }

    private void forceUpdate() {
        this.setValue(this.getValue());
    }

    public List<T> asList() {
        return getValueSafe().asList();
    }
}
