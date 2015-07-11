package org.cryse.lkong.event;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class RxEventBus {
    private final Subject<AbstractEvent, AbstractEvent> mInstance = new SerializedSubject<>(PublishSubject.create());

    public void sendEvent(AbstractEvent event) {
        try {
            mInstance.onNext(event);
        } catch (Exception ex) {
            mInstance.onError(ex);
        }
    }

    public Observable<AbstractEvent> toObservable() {
        return mInstance;
    }
}