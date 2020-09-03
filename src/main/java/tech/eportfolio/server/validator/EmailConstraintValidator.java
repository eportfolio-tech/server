package tech.eportfolio.server.validator;

import tech.eportfolio.server.constraint.ValidEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Lynch
 */
public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {
    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext context) {
        return contactField != null &&
                org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(contactField);
    }
}
