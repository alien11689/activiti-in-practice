package com.blogspot.przybyszd.activitiinpractice.variablesandconditions

import org.activiti.engine.FormService
import org.activiti.engine.RepositoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.form.StartFormData
import org.activiti.engine.repository.ProcessDefinition
import org.activiti.engine.runtime.ProcessInstance
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

    @Autowired
    RepositoryService repositoryService

    @Unroll
    def "should get form properties for process"() {
        given:
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(PROCESS_DEFINITION)
                    .singleResult()
        when:
            StartFormData startFormData = formService.getStartFormData(processDefinition.id)
        then:
            startFormData.formProperties.size() == 1
            startFormData.formProperties.get(0).id == "hardwareProblem"
            startFormData.formProperties.get(0).type.name == "boolean"
            startFormData.formProperties.get(0).required
    }

    @Unroll
    def "should only fix hardware problem"() {
        given:
            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(PROCESS_DEFINITION)
                    .singleResult()
        when:
            ProcessInstance processInstance = formService.submitStartFormData(processDefinition.id, ["hardwareProblem": "true"])
        then:
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .active()
                    .list()
            tasks.get(0).name == "fixHardwareProblem"
        when:
            taskService.complete(tasks.get(0).id)
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }

    @Unroll
    def "should only fix software problem"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION, ["hardwareProblem": false])
        then:
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .active()
                    .list()
            tasks.get(0).name == "fixSoftwareProblem"
        when:
            taskService.complete(tasks.get(0).id, ["problemSolved": true])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }

    @Unroll
    def "should fix hardware problem when no software problem"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINITION, ["hardwareProblem": false])
        then:
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .active()
                    .list()
            tasks.get(0).name == "fixSoftwareProblem"
        when:
            formService.submitTaskFormData(tasks.get(0).id, ["problemSolved": "false"])
        then:
            List<Task> tasks2 = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .active()
                    .list()
            tasks2.get(0).name == "fixHardwareProblem"
        when:
            taskService.complete(tasks2.get(0).id)
        then:
            runtimeService.createProcessInstanceQuery().list() == []
    }
}
