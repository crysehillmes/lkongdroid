package org.cryse.lkong.presenter;

public interface BasePresenter<T> {
    void bindView(T view);
    void unbindView();
    void destroy();
}
