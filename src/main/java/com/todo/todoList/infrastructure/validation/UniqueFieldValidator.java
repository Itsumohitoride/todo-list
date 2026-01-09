package com.todo.todoList.infrastructure.validation;

import com.todo.todoList.domain.enums.UniqueType;
import com.todo.todoList.domain.port.out.IUserRepository;
import com.todo.todoList.infrastructure.validation.annotation.UniqueField;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class UniqueFieldValidator implements ConstraintValidator<UniqueField, String> {

    private final IUserRepository userRepository;
    private UniqueType type;

    @Override
    public void initialize(UniqueField annotation) {
        this.type = annotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        return switch (type) {
            case EMAIL -> userRepository.findByEmail(value).isEmpty();
            case NICKNAME -> userRepository.findByNickname(value).isEmpty();
        };
    }
}
