package com.github.yanbing.model.an;


import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author dingyunwei
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface McpService {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}
