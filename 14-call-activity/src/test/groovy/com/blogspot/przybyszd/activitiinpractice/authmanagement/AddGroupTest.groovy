package com.blogspot.przybyszd.activitiinpractice.authmanagement

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "/context.xml")
class AddGroupTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Autowired
    TaskService taskService

    def "should add group"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("add-group")
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
            task != null
            task.name == "enter-group-data"
        when:
            taskService.complete(task.id, [groupName: "tester"])
        then:
            runtimeService.createExecutionQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.size() == 2
            variables.get(0).variableName == "groupAdded"
            variables.get(0).value == true
            variables.get(1).variableName == "groupName"
            variables.get(1).value == "tester"
    }
}
