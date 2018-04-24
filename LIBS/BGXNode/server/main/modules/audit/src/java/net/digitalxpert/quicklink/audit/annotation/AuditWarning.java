package net.bgx.bgxnetwork.audit.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bgx.bgxnetwork.audit.strategy.AuditCode;

@Target( { ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditWarning{
    AuditCode value();
}
