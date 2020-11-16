package com.wwsl.mdsj.utils.tiktok;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;


/**
 * @author :
 * @date : 2020/6/17 17:10
 * @description : RxBus管理类
 */
public class RxBus {

    private static volatile RxBus instance;
    private final Subject<Object> bus;

    private RxBus() {
        bus = PublishSubject.create().toSerialized();
    }

    public static RxBus getInstance() {
        if (instance == null) {
            synchronized (RxBus.class) {
                if (instance == null) {
                    instance = new RxBus();
                }
            }
        }
        return instance;
    }


    public void post(Object o) {
        bus.onNext(o);
    }

    public <T> Observable<T> toObservable(Class<T> eventType) {
        return bus.ofType(eventType);
    }
}