package com.blogspot.przybyszd.activitiinpractice.exceptions

import org.activiti.engine.delegate.BpmnError
import org.springframework.stereotype.Component

@Component
class CustomerValidator {
    void validate(boolean fetchData) {
        if (!fetchData) {
            throw new BpmnError("UNKNOWN_CUSTOMER")
        }
    }
}
