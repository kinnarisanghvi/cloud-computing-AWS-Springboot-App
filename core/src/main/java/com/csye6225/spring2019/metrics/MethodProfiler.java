package com.csye6225.spring2019.metrics;

import com.timgroup.statsd.StatsDClient;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

import java.util.concurrent.TimeUnit;

public class MethodProfiler {
    private final StatsDClient statsd;

    public MethodProfiler(StatsDClient statsd) {
        this.statsd = statsd;
    }

    @Pointcut("execution(* com.csye6225.spring2019.controller.NoteController*(..))")
    public void restServiceMethods() {
    }

    @Around("restServiceMethods()")
    public Object profile(ProceedingJoinPoint pjp) throws Throwable {

        Object output = pjp.proceed();

        // send the recorded time to statsd
        String key = String.format("%s.%s", pjp.getSignature().getDeclaringTypeName(), pjp.getSignature().getName());
//        statsd.recordExecutionTime(key, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        statsd.incrementCounter("note.controller");
        // return the recorded result
        return output;

    }
}
