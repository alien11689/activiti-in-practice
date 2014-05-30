package com.blogspot.przybyszd.activitiinpractice.processorder.route

import org.apache.camel.Exchange
import org.apache.camel.Processor
import org.apache.camel.builder.RouteBuilder

class OrderRoute extends RouteBuilder{
    @Override
    void configure() throws Exception {
        from("activiti:process-order:processOrder").to("seda:asyncQueue");
        from("seda:asyncQueue").process(new Processor() {
            @Override
            void process(Exchange exchange) throws Exception {
                Thread.sleep(1000)
                if(exchange.in.getHeader("amount", Integer) < 100){
                    exchange.in.setHeader("orderFulfilled",true)
                }else{
                    exchange.in.setHeader("orderFulfilled", false)
                }
            }
        }).to("activiti:process-order:waitForProcessingEnd");
    }
}
