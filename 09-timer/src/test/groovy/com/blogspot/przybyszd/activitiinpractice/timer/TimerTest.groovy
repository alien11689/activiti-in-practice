package com.blogspot.przybyszd.activitiinpractice.timer

import com.jayway.awaitility.groovy.AwaitilitySupport
import org.activiti.engine.ProcessEngineConfiguration
import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.joda.time.DateTime
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = "/context.xml")
class TimerTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Autowired
    ProcessEngineConfiguration processEngineConfiguration

    @Unroll
    def "should start event after some seconds and student answer"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("exam-process")
        then:
            processInstance != null
            taskService.createTaskQuery().active().list() == []
        when:
            processEngineConfiguration.clock.currentTime = DateTime.now().plusHours(1).toDate()
        then:
            Task task = taskService.createTaskQuery().taskName("studentGivesSolution").singleResult()
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
            taskService.createTaskQuery().list() == []
        when:
            processEngineConfiguration.clock.currentTime = DateTime.now().plusHours(1).toDate()
        then:
            Task studentTask = taskService.createTaskQuery().taskName("studentGivesSolution").singleResult()
            studentTask != null
        when:
            processEngineConfiguration.clock.currentTime = DateTime.now().plusHours(1).toDate()
        then:
            Task teacherTask = taskService.createTaskQuery().taskName("teacherGivesSolution").singleResult()
            teacherTask != null
        when:
            taskService.complete(teacherTask.id)
        then:
            runtimeService.createProcessInstanceQuery().
                    processInstanceId(processInstance.processInstanceId).
                    list() == []
    }

}
