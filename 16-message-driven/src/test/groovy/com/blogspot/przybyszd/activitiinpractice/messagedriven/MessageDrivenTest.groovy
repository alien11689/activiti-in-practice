package com.blogspot.przybyszd.activitiinpractice.messagedriven

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricActivityInstance
import org.activiti.engine.runtime.Execution
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "/context.xml")
class MessageDrivenTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Autowired
    HistoryService historyService

    def "should start process with data and data is not valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("availableData", [data: "data"])
        then:
            taskService.createTaskQuery().active().list() == []
            Execution execution = runtimeService.createExecutionQuery().activityId("waitWhenDataAvailable").singleResult()
            execution != null
        when:
            runtimeService.messageEventReceived("invalidData", execution.id)
        then:
            HistoricActivityInstance historicActivity = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId).activityType("endEvent").singleResult()
            historicActivity.activityId == "failure"
    }

    def "should start process without data and data is valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByMessage("notAvailableData")
        then:
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.processInstanceId).singleResult()
            task != null
        when:
            taskService.complete(task.id, [data: "data"])
        then:
            Execution execution = runtimeService.createExecutionQuery().activityId("waitWhenDataAvailable").singleResult()
            execution != null
        when:
            runtimeService.messageEventReceived("validData", execution.id)
        then:
            HistoricActivityInstance historicActivity = historyService.createHistoricActivityInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId).activityType("endEvent").singleResult()
            historicActivity.activityId == "success"
    }

}
