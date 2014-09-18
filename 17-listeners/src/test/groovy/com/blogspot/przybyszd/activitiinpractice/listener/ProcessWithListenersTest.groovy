package com.blogspot.przybyszd.activitiinpractice.listener

import org.activiti.engine.EngineServices
import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.delegate.event.ActivitiEvent
import org.activiti.engine.delegate.event.ActivitiEventType
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(locations = "/context.xml")
class ProcessWithListenersTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Autowired
    TaskService taskService

    def "should finished simplest process"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-with-listeners", "bk")
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceBusinessKey("bk")
                    .taskName("enterGroupData")
                    .singleResult()
            task != null
        when:
            taskService.claim(task.id, "test")
            taskService.complete(task.id)
        then:
            HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
            result.endTime != null
            HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstance.processInstanceId)
                .variableName("notifications")
                .singleResult()
            variable.value == ["task assign to test", "task completed", "group added", "process finished"]
    }

}
