package com.blogspot.przybyszd.activitiinpractice.personvalidation

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.ProcessInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:context.xml")
class PersonValidationTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Autowired
    HistoryService historyService

    def "should pass when person is valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("person-validation", [firstName: "John", lastName: "Smith"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.get(0).variableName == "firstName"
            variables.get(0).value == "John"
            variables.get(1).variableName == "lastName"
            variables.get(1).value == "Smith"

    }

    def "should pass after first name set to valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("person-validation", [firstName: "john", lastName: "Smith"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.get(0).variableName == "firstName"
            variables.get(0).value == "Jan"
            variables.get(1).variableName == "lastName"
            variables.get(1).value == "Testowski"
    }

    def "should pass after last name set to valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("person-validation", [firstName: "John", lastName: "smith"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.get(0).variableName == "firstName"
            variables.get(0).value == "Jan"
            variables.get(1).variableName == "lastName"
            variables.get(1).value == "Testowski"
    }
}
