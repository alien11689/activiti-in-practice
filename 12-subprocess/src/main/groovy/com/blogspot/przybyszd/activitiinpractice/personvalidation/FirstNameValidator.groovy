package com.blogspot.przybyszd.activitiinpractice.personvalidation

import org.activiti.engine.delegate.BpmnError
import org.activiti.engine.delegate.DelegateExecution

class FirstNameValidator {

    void validate(DelegateExecution execution) {
        String firstName = execution.getVariable("firstName")
        if (firstName != null && (firstName == "" || !Character.isUpperCase(firstName.charAt(0)))) {
            throw new BpmnError("InvalidPerson")
        }
    }
}
