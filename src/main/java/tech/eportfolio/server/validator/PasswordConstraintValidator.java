package tech.eportfolio.server.validator;

import com.google.common.base.Joiner;
import org.passay.*;
import tech.eportfolio.server.constraint.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(ValidPassword arg0) {
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
                // length requirement 8 to 30
                new LengthRule(8, 30),
                // password must contains a lowercase character
                new CharacterRule(EnglishCharacterData.LowerCase),
                // password must contains a uppercase character
                new CharacterRule(EnglishCharacterData.UpperCase),
                // password must contains a digit
                new CharacterRule(EnglishCharacterData.Digit),
                // new CharacterRule(EnglishCharacterData.Special),
                // password must not contains whitespace
                new WhitespaceRule(),
                // password must not contain 3 repeating character
                new RepeatCharacterRegexRule(3)));

        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                Joiner.on(",").join(validator.getMessages(result)))
                .addConstraintViolation();
        return false;
    }
}