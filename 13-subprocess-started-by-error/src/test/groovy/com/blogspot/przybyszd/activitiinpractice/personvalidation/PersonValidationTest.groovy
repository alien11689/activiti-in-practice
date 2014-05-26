package com.blogspot.przybyszd.activitiinpractice.personvalidation

import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "classpath:context.xml")
class PersonValidationTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    def "should pass when person is valid"() {
        when:
            runtimeService.startProcessInstanceByKey("person-validation", [firstName: "John", lastName: "Smith"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }

    def "should pass after first name set to valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("person-validation", [firstName: "john", lastName: "Smith"])
        then:
            Task task = taskService.createTaskQuery().active().executionId(processInstance.processInstanceId).singleResult()
            task.name == "enterPersonData"
        when:
            taskService.complete(task.id, [firstName: "John"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }

    def "should pass after last name set to valid"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("person-validation", [firstName: "John", lastName: "smith"])
        then:
            Task task = taskService.createTaskQuery().active().executionId(processInstance.processInstanceId).singleResult()
            task.name == "enterPersonData"
        when:
            taskService.complete(task.id, [lastName: "Smith"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }
}
