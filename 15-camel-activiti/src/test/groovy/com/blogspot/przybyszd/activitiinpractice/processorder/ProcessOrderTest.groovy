package com.blogspot.przybyszd.activitiinpractice.processorder

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

import java.util.concurrent.TimeUnit

@Mixin(AwaitilitySupport)
@ContextConfiguration(locations = "classpath:context.xml")
class ProcessOrderTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Autowired
    TaskService taskService


    def "should fulfill order"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-order")
        then:
            Task task = taskService.createTaskQuery().singleResult()
            task != null
        when:
            taskService.complete(task.id, [amount: 45, name: "book"])
        then:
            runtimeService.getActiveActivityIds(processInstance.processInstanceId) == ["waitForProcessingEnd"]
            await().atMost(3, TimeUnit.SECONDS).until {
                runtimeService.createExecutionQuery().list() == []
            }
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.size() == 5
            variables.get(0).variableName == "amount"
            variables.get(0).value == 45
            variables.get(1).variableName == "breadcrumbId"
            variables.get(2).variableName == "camelBody"
            variables.get(2).value == null
            variables.get(3).variableName == "name"
            variables.get(3).value == "book"
            variables.get(4).variableName == "orderFulfilled"
            variables.get(4).value == true
    }

    def "should not fulfill order"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-order")
        then:
            Task task = taskService.createTaskQuery().singleResult()
            task != null
        when:
            taskService.complete(task.id, [amount: 10000, name: "book"])
        then:
            runtimeService.getActiveActivityIds(processInstance.processInstanceId) == ["waitForProcessingEnd"]
            await().atMost(3, TimeUnit.SECONDS).until {
                runtimeService.createExecutionQuery().list() == []
            }
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            variables.size() == 5
            variables.get(0).variableName == "amount"
            variables.get(0).value == 10000
            variables.get(1).variableName == "breadcrumbId"
            variables.get(2).variableName == "camelBody"
            variables.get(2).value == null
            variables.get(3).variableName == "name"
            variables.get(3).value == "book"
            variables.get(4).variableName == "orderFulfilled"
            variables.get(4).value == false
    }
}
