package com.todo.todoList.validator.annotation;

import com.todo.todoList.model.enums.UniqueType;
import com.todo.todoList.validator.UniqueFieldValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueFieldValidator.class)
public @interface UniqueField {

    String message() default "Value already exists";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    UniqueType type();
}
