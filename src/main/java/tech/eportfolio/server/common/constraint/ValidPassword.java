package tech.eportfolio.server.common.constraint;

import tech.eportfolio.server.common.validator.PasswordConstraintValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RUNTIME)
public @interface ValidPassword {

    String message() default "Password is too weak";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}