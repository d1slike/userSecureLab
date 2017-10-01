package ru.disdev.entity;

import javafx.scene.layout.Region;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
    String name();

    Type type();

    String description() default "";

    String csvColumnName() default "";

    double width() default Region.USE_COMPUTED_SIZE;
}
