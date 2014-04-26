package com.blogspot.przybyszd.activitiinpractice.variablesandconditions

import org.activiti.engine.FormService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.form.StartFormData
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = "/context.xml")
class VariablesAndConditionsTest extends Specification {

    private static final String PROCESS_DEFINITION = "variables-and-conditions"

    @Autowired
    FormService formService

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Unroll
    def "should get form properties for process"() {
        when:
            StartFormData startFormData = formService.getStartFormData(PROCESS_DEFINITION)
        then:
            startFormData.formProperties.size() == 1
            startFormData.formProperties.get(0).id == "hardwareProblem"
            startFormData.formProperties.get(0).type.name == "boolean"
            startFormData.formProperties.get(0).required
    }

    @Unroll
    def "should only fix hardware problem"() {
        when:
            String processInstanceId = formService.submitStartFormData(PROCESS_DEFINITION, ["hardwareProblem": "true"])
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .list()
            task.name == "fixHardwareProblem"
        when:
            taskService.complete(task.id)
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }

    @Unroll
    def "should only fix software problem"() {
        when:
            String processInstanceId = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION, ["hardwareProblem": false])
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .list()
            task.name == "fixSoftwareProblem"
        when:
            taskService.complete(task.id, ["problemSolved": true])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }

    @Unroll
    def "should fix hardware problem when no software problem"() {
        when:
            String processInstanceId = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION, ["hardwareProblem": false])
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .list()
            task.name == "fixSoftwareProblem"
        when:
            formService.submitTaskFormData(task.id, ["problemSolved": false])
        then:
            Task task2 = taskService.createTaskQuery()
                    .processInstanceId(processInstanceId)
                    .active()
                    .list()
            task2.name == "fixHardwareProblem"
        when:
            taskService.complete(task2.id)
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }
}
