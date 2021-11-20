package com.revature.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notifies that the Field is a Column (NOT for if it's a PKey).
 *  - isUnique: Is the column Unique? Default false.
 *  - isNotNull: Is the column Not Null? Default true.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column{
    boolean isUnique() default false;
    boolean isNotNull() default true;
}
