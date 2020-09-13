package tech.eportfolio.server.common.validator;

import tech.eportfolio.server.common.constraint.ValidEmail;

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
