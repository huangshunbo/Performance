package com.tdw.prism.leak;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class KeyedWeakReference extends WeakReference<Object> {
    public final String key;
    public final String name;

    KeyedWeakReference(Object referent, String key, String name,
                       ReferenceQueue<Object> referenceQueue) {
        super(referent, referenceQueue);
        this.key = key;
        this.name = name;
    }
}
