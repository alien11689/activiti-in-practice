package com.blogspot.przybyszd.activitiinpractice.parallelgateway

import org.activiti.engine.RuntimeService
import org.activiti.engine.TaskService
import org.activiti.engine.runtime.ProcessInstance
import org.activiti.engine.task.Task
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by alien on 4/26/14.
 */
@ContextConfiguration(locations = "/context.xml")
class ParallelGatewayTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    TaskService taskService

    @Unroll
    def "should first start #first task"() {
        given:
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("parallel-gateway")
        when:
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .active()
                    .list()
        then:
            tasks.size() == 2
            tasks.get(0).name == "sendTweet"
            tasks.get(1).name == "addToRss"
        when:
            taskService.complete(tasks.get(firstIndex).id)
        then:
            taskService.createTaskQuery()
                    .processInstanceId(processInstance.processInstanceId)
                    .active()
                    .list().size() == 1
        when:
            taskService.complete(tasks.get(secondIndex).id)
        then:
            runtimeService.createProcessInstanceQuery().list() == []
        where:
            first   | firstIndex | secondIndex
            "tweet" | 0          | 1
            "rss"   | 1          | 0
    }
}
