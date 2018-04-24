package net.bgx.bgxnetwork.audit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bgx.bgxnetwork.audit.strategy.AuditCode;
import net.bgx.bgxnetwork.audit.strategy.AuditLogs;

@Target({ElementType.METHOD, ElementType.FIELD})

@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {
    AuditCode code();
    AuditLogs strategy() default AuditLogs.STANDART_LOG;
}
