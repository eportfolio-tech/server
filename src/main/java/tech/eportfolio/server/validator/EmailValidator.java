package tech.eportfolio.server.validator;

import tech.eportfolio.server.constraint.EmailConstraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Lynch
 */
public class EmailValidator implements ConstraintValidator<EmailConstraint, String> {
    @Override
    public boolean isValid(String contactField, ConstraintValidatorContext context) {
        return contactField != null &&
                org.apache.commons.validator.routines.EmailValidator.getInstance().isValid(contactField);
    }
}
