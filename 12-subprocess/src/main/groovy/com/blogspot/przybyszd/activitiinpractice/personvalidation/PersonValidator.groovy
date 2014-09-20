package com.blogspot.przybyszd.activitiinpractice.personvalidation

import org.activiti.engine.delegate.BpmnError
import org.springframework.stereotype.Component

@Component
class PersonValidator {

    void validateWithPossibleNull(String value) {
        if (value != null && (value == "" || !Character.isUpperCase(value.charAt(0)))) {
            throw new BpmnError("InvalidPerson")
        }
    }

    void validateWithoutNull(String value) {
        if (value == "" || !Character.isUpperCase(value.charAt(0))) {
            throw new BpmnError("InvalidPerson")
        }
    }
}
