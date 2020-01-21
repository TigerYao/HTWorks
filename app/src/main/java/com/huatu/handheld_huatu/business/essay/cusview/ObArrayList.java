package com.huatu.handheld_huatu.business.essay.cusview;

import java.util.ArrayList;
import java.util.Collection;

public class ObArrayList<E> extends ArrayList<E> {

    private ChangeListener changeListener;

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
    }

    @Override
    public boolean add(E e) {
        boolean add = super.add(e);
        onChanged();
        return add;
    }

    @Override
    public void add(int index, E element) {
        super.add(index, element);
        onChanged();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean b = super.addAll(c);
        onChanged();
        return b;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean b = super.addAll(index, c);
        onChanged();
        return b;
    }

    @Override
    public E set(int index, E element) {
        E set = super.set(index, element);
        onChanged();
        return set;
    }

    @Override
    public boolean remove(Object o) {
        boolean remove = super.remove(o);
        onChanged();
        return remove;
    }

    @Override
    public E remove(int index) {
        E remove = super.remove(index);
        onChanged();
        return remove;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean b = super.removeAll(c);
        onChanged();
        return b;
    }

    @Override
    protected void removeRange(int fromIndex, int toIndex) {
        super.removeRange(fromIndex, toIndex);
        onChanged();
    }

    @Override
    public void clear() {
        super.clear();
        onChanged();
    }

    private void onChanged() {
        if (changeListener != null) {
            changeListener.onChanged();
        }
    }

    public interface ChangeListener {
        void onChanged();
    }
}
