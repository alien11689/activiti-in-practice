package com.blogspot.przybyszd.activitiinpractice.exceptions

import org.activiti.engine.impl.pvm.PvmTransition
import org.activiti.engine.impl.pvm.delegate.ActivityBehavior
import org.activiti.engine.impl.pvm.delegate.ActivityExecution

class CustomerFetcher implements ActivityBehavior{

    @Override
    void execute(ActivityExecution execution) throws Exception {
        String customer = execution.getVariable("customer")
        PvmTransition transition
        if(customer == "Smith"){
            execution.setVariable("fetchData", true)
            transition = execution.getActivity().findOutgoingTransition("autoFetch")
        }else{
            execution.setVariable("fetchData", false)
            transition = execution.getActivity().findOutgoingTransition("manualFetch")
        }
        execution.take(transition)
    }
}
