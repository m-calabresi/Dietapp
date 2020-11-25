package com.android.dietapp.viewmodel.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.Objects;

public class MutableListLiveData<T> extends MutableLiveData<MutableListHolder<T>> {
    /* public MutableListLiveData() {
        super();
        setValue(new MutableListHolder<>());
    } */

    public MutableListLiveData(@NonNull final List<T> items) {
        super();
        setValue(new MutableListHolder<>(items));
    }

    /* public void addItem(T item) {
        getValueSafe().addItem(item);
        forceUpdate();
    } */

    public void addItem(@NonNull final T item, final int position) {
        getValueSafe().addItem(item, position);
        forceUpdate();
    }

    /* public void addItemRange(@NonNull final List<T> items) {
        getValueSafe().addItemRange(items);
        forceUpdate();
    } */

    /* public void addItemRange(@NonNull final List<T> items, final int startPosition) {
        getValueSafe().addItemRange(items, startPosition);
        forceUpdate();
    } */

    /* public void removeItem(@NonNull final T item) {
        getValueSafe().removeItem(item);
        forceUpdate();
    } */

    /* public void removeItem(final int position) {
        getValueSafe().removeItem(position);
        forceUpdate();
    } */

    /* public void removeItemRange(@NonNull final List<T> items) {
        getValueSafe().removeItemRange(items);
        forceUpdate();
    } */

    /* public void removeItemRange(@NonNull final List<T> items, final int positionStart) {
        getValueSafe().removeItemRange(items, positionStart);
        forceUpdate();
    } */

    public void setItem(@NonNull final T item, final int position) {
        getValueSafe().setItem(item, position);
        forceUpdate();
    }

    /* public void setItemRange(@NonNull final List<T> items, final int positionStart) {
        getValueSafe().setItemRange(items, positionStart);
        forceUpdate();
    } */

    public int size() {
        return getValueSafe().size();
    }

    @NonNull
    public T get(final int position) {
        return getValueSafe().get(position);
    }

    @NonNull
    private MutableListHolder<T> getValueSafe() {
        return Objects.requireNonNull(this.getValue());
    }

    private void forceUpdate() {
        this.setValue(this.getValue());
    }

    @NonNull
    public List<T> asList() {
        return getValueSafe().asList();
    }
}
