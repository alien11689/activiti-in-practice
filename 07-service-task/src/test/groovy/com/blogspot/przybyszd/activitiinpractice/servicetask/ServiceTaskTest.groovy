package com.blogspot.przybyszd.activitiinpractice.servicetask

import org.activiti.engine.HistoryService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.ProcessInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = "/context.xml")
class ServiceTaskTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Unroll
    def "should persist person"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("add-person", ["username": "Piter Parker"])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricDetailQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .orderByVariableName()
                    .asc()
                    .list()
            historicVariableInstances.size() == 3
            historicVariableInstances.get(0).variableName == "id"
            historicVariableInstances.get(0).value == 15l
            historicVariableInstances.get(0).variableName == "person"
            historicVariableInstances.get(0).value == new Person("Piter","Parker")
            historicVariableInstances.get(0).variableName == "username"
            historicVariableInstances.get(0).value == "Piter Parker"

    }
}
