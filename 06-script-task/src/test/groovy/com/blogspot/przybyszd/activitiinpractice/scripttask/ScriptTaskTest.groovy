package com.blogspot.przybyszd.activitiinpractice.scripttask

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.ProcessInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by alien on 4/26/14.
 */
@ContextConfiguration(locations = "/context.xml")
class ScriptTaskTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Unroll
    def "should split name into two variables"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("script-task", ["username": "Piter Parker"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            historicVariableInstances.size() == 3
            historicVariableInstances.get(0).variableName == "firstname"
            historicVariableInstances.get(0).value == "Piter"
            historicVariableInstances.get(1).variableName == "lastname"
            historicVariableInstances.get(1).value == "Parker"
            historicVariableInstances.get(2).variableName == "username"
            historicVariableInstances.get(2).value == "Piter Parker"
    }
}
