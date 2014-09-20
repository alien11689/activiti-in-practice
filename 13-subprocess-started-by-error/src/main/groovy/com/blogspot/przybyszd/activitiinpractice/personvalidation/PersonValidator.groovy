package com.blogspot.przybyszd.activitiinpractice.personvalidation

import org.springframework.stereotype.Component

@Component
class PersonValidator {

    boolean validateWithPossibleNull(String value) {
        value == null || (value != "" && Character.isUpperCase(value.charAt(0)))
    }

    boolean validateWithoutNull(String value) {
        value != "" && Character.isUpperCase(value.charAt(0))
    }
}
