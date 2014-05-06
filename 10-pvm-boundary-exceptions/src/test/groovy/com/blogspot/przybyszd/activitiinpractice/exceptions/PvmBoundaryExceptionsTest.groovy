package com.blogspot.przybyszd.activitiinpractice.exceptions

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = "/context.xml")
class PvmBoundaryExceptionsTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Autowired
    HistoryService historyService

    @Unroll
    def "should fetch customer data without problems and accept customer"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("accept-customer-process", ["customer": "Smith"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.get(0).variableName == "customer"
            variables.get(0).value == "Smith"
            variables.get(1).variableName == "customerAccepted"
            variables.get(1).value == true
            variables.get(2).variableName == "fetchData"
            variables.get(2).value == true
    }

    @Unroll
    def "should fetch customer data with problems and accept customer"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("accept-customer-process", ["customer": "Kowalski"])
        then:
            runtimeService.createProcessInstanceQuery().list() != []
        when:
            Task task = taskService.createTaskQuery().
                    processInstanceId(processInstance.processInstanceId).
                    taskName("manualFetchData").singleResult()
        then:
            task != null
        when:
            taskService.complete(task.id, ["fetchData": true])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.get(0).variableName == "customer"
            variables.get(0).value == "Kowalski"
            variables.get(1).variableName == "customerAccepted"
            variables.get(1).value == true
            variables.get(2).variableName == "fetchData"
            variables.get(2).value == true
    }

    @Unroll
    def "should not fetch customer data and reject customer"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("accept-customer-process", ["customer": "Kowalski"])
        then:
            runtimeService.createProcessInstanceQuery().list() != []
        when:
            Task task = taskService.createTaskQuery().
                    processInstanceId(processInstance.processInstanceId).
                    taskName("manualFetchData").singleResult()
        then:
            task != null
        when:
            taskService.complete(task.id, ["fetchData": false])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.get(0).variableName == "customer"
            variables.get(0).value == "Kowalski"
            variables.get(1).variableName == "customerAccepted"
            variables.get(1).value == false
            variables.get(2).variableName == "fetchData"
            variables.get(2).value == false
    }
}
