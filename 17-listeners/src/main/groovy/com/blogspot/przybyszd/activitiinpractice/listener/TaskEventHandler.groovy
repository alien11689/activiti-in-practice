package com.blogspot.przybyszd.activitiinpractice.listener

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.DelegateTask
import org.springframework.stereotype.Component

@Component
class TaskEventHandler {
    void handleCreation(DelegateExecution execution){
        execution.setVariable("notifications", ["task created"])
    }

    void handleAssign(DelegateExecution execution, DelegateTask task){
        List<String> notifications = execution.getVariable("notifications", List)
        notifications << "task assigned to $task.assignee"
        execution.setVariable("notifications", notifications)
    }

    void handleCompletion(DelegateExecution execution){
        List<String> notifications = execution.getVariable("notifications", List)
        notifications << "task completed"
        execution.setVariable("notifications", notifications)
    }
}
