package com.blogspot.przybyszd.activitiinpractice.timer

import org.activiti.engine.RuntimeService
import org.activiti.engine.runtime.ProcessInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

@ContextConfiguration(locations = "/context.xml")
class TimerTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Unroll
    def "should start event after some seconds"() {
        when:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("exam-process")
        then:
            processInstance != null
            runtimeService.getActiveActivityIds("student-gives-solution") == null
        
    }
}
