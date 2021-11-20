package com.revature.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Notifies that the Field is a Primary Key column.
 *  - isSerial: Is the column Serializable? Default true.
 *  - isUnique: Is the column Unique? Default true.
 *  - isNotNull: Is the column Not Null? Default true.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PKey{
    boolean isSerial() default true;
    boolean isUnique() default true;
    boolean isNotNull() default true;
}
