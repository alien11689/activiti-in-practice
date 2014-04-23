package com.blogspot.przybyszd.activitiinpractice.manual

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricProcessInstance
import org.activiti.engine.runtime.ProcessInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = "/context.xml")
class ProcessWithManualTaskTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Unroll
    def "should execute process with manual task"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("process-with-manual-task")
        then:
            HistoricProcessInstance result = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .singleResult()
            result.endTime != null
    }
}
