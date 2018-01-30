package com.example.myapp.constraint;

import org.apache.commons.beanutils.BeanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {

    private String firstFieldName;
    private String secondFieldName;
    private String message;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean fieldsMatch;
        try {
            final Object firstObject = BeanUtils.getProperty(value, firstFieldName);
            final Object secondObject = BeanUtils.getProperty(value, secondFieldName);

            fieldsMatch = (firstObject == null && secondObject == null) || (firstObject != null && firstObject.equals(secondObject));

            if (!fieldsMatch) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(message).addPropertyNode(secondFieldName).addConstraintViolation();

                return false;
            }

            return true;
        } catch (final Exception ignore) {
            // Ignore
        }

        return true;
    }
}
