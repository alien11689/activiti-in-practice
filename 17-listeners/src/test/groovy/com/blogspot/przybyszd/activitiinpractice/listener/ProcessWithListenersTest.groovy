package com.blogspot.przybyszd.activitiinpractice.listener

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.Execution
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
            runtimeService.startProcessInstanceByKey("process-with-listeners", "bk")
        then:
            Task task = taskService.createTaskQuery()
                    .processInstanceBusinessKey("bk")
                    .taskName("enterGroupData")
                    .singleResult()
            task != null
        when:
            taskService.claim(task.id, "test")
            taskService.complete(task.id, ["groupName": "admin"])
        then:
            HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceBusinessKey("bk")
                    .singleResult()
            result.endTime != null
            HistoricVariableInstance variable = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(result.id)
                .variableName("notifications")
                .singleResult()
            variable != null
            variable.value == ["task created",
                               "task assigned to test",
                               "task completed",
                               "group admin will be added",
                               "group added",
                               "process finished"]
    }

}
