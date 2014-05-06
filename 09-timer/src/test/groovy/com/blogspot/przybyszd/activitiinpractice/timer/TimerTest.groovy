package com.blogspot.przybyszd.activitiinpractice.timer

import com.jayway.awaitility.groovy.AwaitilitySupport
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

import java.util.concurrent.TimeUnit

@Mixin(AwaitilitySupport)
@ContextConfiguration(locations = "/context.xml")
class TimerTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Unroll
    def "should start event after some seconds and student answer"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("exam-process")
        then:
            processInstance != null
            taskService.createTaskQuery().active().list() == []
        when:
            await().atMost(2, TimeUnit.SECONDS).until({
                taskService.createTaskQuery().active().list() != []
            })
            Task task = taskService.createTaskQuery().taskName("student-gives-solution").singleResult()
        then:
            task != null
        when:
            taskService.complete(task.id)
        then:
            runtimeService.createProcessInstanceQuery().
                    processInstanceId(processInstance.processInstanceId).
                    list() == []
    }

    @Unroll
    def "should start event after some seconds and teacher answers"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("exam-process")
        then:
            processInstance != null
            taskService.createTaskQuery().active().list() == []
        when:
            await().atMost(2, TimeUnit.SECONDS).until({
                taskService.createTaskQuery().active().list() != []
            })
            Task task = taskService.createTaskQuery().taskName("student-gives-solution").singleResult()
        then:
            task != null
        when:
            await().atMost(2,TimeUnit.SECONDS).until {
                taskService.createTaskQuery().taskName("student-gives-solution").singleResult() == null &&
                        taskService.createTaskQuery().taskName("teacher-gives-solution").singleResult() != null
            }
            task = taskService.createTaskQuery().taskName("teacher-gives-solution").singleResult()
        then:
            task != null
        when:
            taskService.complete(task.id)
        then:
            runtimeService.createProcessInstanceQuery().
                    processInstanceId(processInstance.processInstanceId).
                    list() == []
    }

}
