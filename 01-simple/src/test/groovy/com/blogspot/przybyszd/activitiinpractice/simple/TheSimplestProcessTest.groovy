package com.blogspot.przybyszd.activitiinpractice.simple

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "/test-context.xml")
class TheSimplestProcessTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Autowired
    HistoryService historyService

    def "should finished simplest process"() {
        given:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("simplest-process")
            Task task = taskService.createTaskQuery().singleResult()
        when:
            taskService.complete(task.getId())
        then:
            HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.processInstanceId).singleResult()
            result.endTime != null
    }

}
