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
class AddUserTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Autowired
    TaskService taskService

    def "should add user without creating group"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("add-user")
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
            task != null
            task.name == "enter-user-data"
        when:
            taskService.complete(task.id, [userName: "dpr", createGroup: false])
        then:
            runtimeService.createExecutionQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.size() == 3
            variables.get(0).variableName == "createGroup"
            variables.get(0).value == false
            variables.get(1).variableName == "userAdded"
            variables.get(1).value == true
            variables.get(2).variableName == "userName"
            variables.get(2).value == "dpr"
    }

    def "should add user with creating group"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("add-user")
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
            task != null
            task.name == "enter-user-data"
        when:
            taskService.complete(task.id, [userName: "dpr", createGroup: true])
        then:
            Task task2 = taskService.createTaskQuery()
                    .singleResult()
            task2 != null
            task2.name == "enter-group-data"
        when:
            taskService.complete(task2.id, [groupName: "tester"])
        then:
            runtimeService.createExecutionQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.size() == 5
            variables.get(0).variableName == "createGroup"
            variables.get(0).value == true
            variables.get(1).variableName == "groupCreated"
            variables.get(1).value == true
            variables.get(2).variableName == "groupName"
            variables.get(2).value == "tester"
            variables.get(3).variableName == "userAdded"
            variables.get(3).value == true
            variables.get(4).variableName == "userName"
            variables.get(4).value == "dpr"
    }
}
