package com.blogspot.przybyszd.activitiinpractice.servicetask

import org.activiti.engine.delegate.DelegateExecution
import org.activiti.engine.delegate.JavaDelegate

/**
 * Created by alien on 4/26/14.
 */
class PersonCreator implements JavaDelegate {

    @Override
    void execute(DelegateExecution execution) throws Exception {
        String username = execution.getVariable("username")
        String[] splitted = username.split(" ")
        Person person = new Person(splitted[0], splitted[1])
        execution.setVariable("person", person)
    }
}
