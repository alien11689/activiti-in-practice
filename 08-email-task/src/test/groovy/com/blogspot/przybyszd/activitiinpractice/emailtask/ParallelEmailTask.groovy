package com.blogspot.przybyszd.activitiinpractice.emailtask

import org.activiti.engine.RuntimeService
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Specification
import spock.lang.Unroll

class ParallelEmailTask extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Unroll
    def "should "() {
        when:
            runtimeService.startProcessInstanceByKey("notify-users", ["users": ["trolo@example.net", "john@doe.com"]])
        then:
            runtimeService.createProcessInstanceQuery().list() == []

        where:

    }
}
