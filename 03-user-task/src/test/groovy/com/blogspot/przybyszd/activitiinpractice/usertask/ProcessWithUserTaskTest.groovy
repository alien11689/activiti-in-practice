package com.blogspot.przybyszd.activitiinpractice.usertask

import org.activiti.engine.HistoryService
import org.activiti.engine.IdentityService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.identity.Group
import org.activiti.engine.identity.User
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "/context.xml")
class ProcessWithUserTaskTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Autowired
    HistoryService historyService

    @Autowired
    IdentityService identityService

    def setup() {
        User user = identityService.newUser("salesman")
        identityService.saveUser(user)
        Group group = identityService.newGroup("worker")
        identityService.saveGroup(group)
        identityService.createMembership(user.id, group.id)
    }

    def "should finished process with user task"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-with-user-task")
        then:
            HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
            result.endTime == null
        when:
            Task task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
        then:
            task != null
            task.taskDefinitionKey == "sendOrder"
        when:
            taskService.complete(task.id)
        then:
            null == taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .taskDefinitionKey(task.taskDefinitionKey)
                    .singleResult()
        when:
            task = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .taskCandidateGroup("worker")
                    .singleResult()
        then:
            task != null
            task.taskDefinitionKey == "completeOrder"
        when:
            taskService.claim(task.id, "salesman")
        then:
            task.id == taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .taskAssignee("salesman")
                    .singleResult().id
        when:
            taskService.complete(task.id)
        then:
            null == runtimeService.createProcessInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
    }

    def cleanup() {
        identityService.deleteMembership("salesman", "worker")
        identityService.deleteGroup("worker")
        identityService.deleteUser("salesman")
    }
}
