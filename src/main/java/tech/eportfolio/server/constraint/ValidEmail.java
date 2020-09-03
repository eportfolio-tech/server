package tech.eportfolio.server.constraint;


import tech.eportfolio.server.validator.EmailConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EmailConstraintValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEmail {
    String message() default "is not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}