package org.cryse.lkong.modules.base;

public interface BasePresenter<T> {
    void bindView(T view);
    void unbindView();
    void destroy();
}
