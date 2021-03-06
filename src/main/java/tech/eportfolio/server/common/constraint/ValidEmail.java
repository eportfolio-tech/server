package tech.eportfolio.server.common.constraint;


import tech.eportfolio.server.common.validator.EmailConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "Email is invalid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}