package com.blogspot.przybyszd.activitiinpractice.async

import com.jayway.awaitility.groovy.AwaitilitySupport
import org.activiti.engine.HistoryService
import org.activiti.engine.ManagementService
import org.activiti.engine.RuntimeService
import org.activiti.engine.history.HistoricVariableInstance
import org.activiti.engine.runtime.Job
import org.activiti.engine.runtime.ProcessInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import java.util.concurrent.TimeUnit

@Mixin(AwaitilitySupport)
@ContextConfiguration(locations = "/context.xml")
class AsyncAndRestartTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    HistoryService historyService

    @Autowired
    ManagementService managementService

    @Unroll
    def "should transcode movie without problems"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("transcode-process", ["file": "file.avi"])
        then:
            runtimeService.createProcessInstanceQuery().list() != []
        when:
            await().pollDelay(500, TimeUnit.MILLISECONDS).atMost(7, TimeUnit.SECONDS).until {
                runtimeService.createProcessInstanceQuery().list() == []
            }
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            managementService.createJobQuery().
                    processInstanceId(processInstance.processInstanceId).
                    withException().
                    list() == []
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().
                    processInstanceId(processInstance.processInstanceId).
                    orderByVariableName().
                    asc().
                    list()
            variables.get(0).variableName == "file"
            variables.get(1).variableName == "transcoded"
            variables.get(1).value == true
    }

    @Unroll
    def "should transcode movie with problems"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("transcode-process", ["file": "file.mp4"])
        then:
            runtimeService.createProcessInstanceQuery().list() != []
        when:
            await().pollDelay(500, TimeUnit.MILLISECONDS).atMost(20, TimeUnit.SECONDS).until {
                runtimeService.createProcessInstanceQuery().list() == []
            }
        then:
            List<Job> jobs = managementService.createJobQuery().
                    processInstanceId(processInstance.processInstanceId).
                    withException().
                    list()
            jobs != null
        when:
            Transcoder.acceptOthers(true)
            managementService.executeJob(jobs.get(0).id)
        then:
            runtimeService.createProcessInstanceQuery().list() != []
        when:
            await().pollDelay(500, TimeUnit.MILLISECONDS).atMost(7, TimeUnit.SECONDS).until {
                runtimeService.createProcessInstanceQuery().list() == []
            }
        then:
            List<HistoricVariableInstance> variables = historyService.createHistoricVariableInstanceQuery().
                    processInstanceId(processInstance.processInstanceId).
                    orderByVariableName().
                    asc().
                    list()
            variables.get(0).variableName == "file"
            variables.get(1).variableName == "transcoded"
            variables.get(1).value == true
    }

    def setup() {
        Transcoder.acceptOthers(false)
    }
}
