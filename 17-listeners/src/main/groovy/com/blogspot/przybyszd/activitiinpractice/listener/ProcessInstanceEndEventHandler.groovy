package com.blogspot.przybyszd.activitiinpractice.listener

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.ExecutionListener

class ProcessInstanceEndEventHandler implements ExecutionListener {

    @Override
    void notify(DelegateExecution execution) throws Exception {
        List<String> notifications = execution.getVariable("notifications", List)
        notifications << "process finished"
        execution.setVariable("notifications", notifications)
    }
}
