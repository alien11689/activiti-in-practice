package com.blogspot.przybyszd.activitiinpractice.listener

import org.activiti.engine.delegate.DelegateExecution
import org.springframework.stereotype.Component

@Component
class GroupAdder {
    void add(String groupName){}

    void beforeAdd(DelegateExecution execution, String groupName){
        List<String> notifications = execution.getVariable("notifications", List)
        notifications << "group $groupName will be added"
        execution.setVariable("notifications", notifications)
    }

    void afterAdd(DelegateExecution execution){
        List<String> notifications = execution.getVariable("notifications", List)
        notifications << "group added"
        execution.setVariable("notifications", notifications)
    }
}
