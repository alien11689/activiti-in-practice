package com.blogspot.przybyszd.activitiinpractice.personvalidation

import org.activiti.engine.delegate.BpmnError
import org.activiti.engine.delegate.DelegateExecution

class LastNameValidator {

    void validate(DelegateExecution execution){
        String lastName = execution.getVariable("lastName")
        if(lastName == null || lastName == "" || !Character.isUpperCase(lastName.charAt(0))){
            throw new BpmnError("InvalidPerson")
        }
    }
}
