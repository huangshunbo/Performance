package com.tdw.prism;

import com.tencent.wstt.gt.GTConfig;
import com.tencent.wstt.gt.client.GTRClient;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import java.lang.ref.WeakReference;

@Aspect
public class LeckAspect {
    @Around("execution(* com.squareup.leakcanary.RefWatcher.gone(..))")
    public Object gone(ProceedingJoinPoint joinPoint) throws Throwable {
        Object o = joinPoint.proceed();
        boolean isGone = (Boolean) o;

        if (isGone) {
            return true;
        }

        uploadData(joinPoint);

        if (PrismInternal.isOpenDumpHeap) {
            return o;
        } else {
            return true;
        }
    }

    private void uploadData(ProceedingJoinPoint joinPoint) {
        if (joinPoint.getArgs()[0] instanceof WeakReference
                && ((WeakReference)joinPoint.getArgs()[0]).get() != null ) {
            WeakReference weakReference = (WeakReference) joinPoint.getArgs()[0];

            String content = "leakCollect" +
                    GTConfig.separator + System.currentTimeMillis() +
                    GTConfig.separator + weakReference.get().getClass().toString();

            GTRClient.pushData(content);
        }
    }
}
