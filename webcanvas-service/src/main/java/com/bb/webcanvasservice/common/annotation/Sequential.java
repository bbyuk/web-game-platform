package com.bb.webcanvasservice.common.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Sequential {

    /**
     * 동시 접근 제어가 필요한 job key
     * @return
     */
    String key() default "";

    /**
     * 작업 대기 최대 시간
     * default 무제한
     */
    long timeoutMillis() default -1L;
}
