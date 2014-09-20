package com.blogspot.przybyszd.activitiinpractice.emailtask

import org.activiti.engine.RuntimeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.subethamail.wiser.Wiser
import spock.lang.Ignore
import spock.lang.Specification
import spock.lang.Unroll

import javax.mail.Message
import javax.mail.internet.MimeMessage

@ContextConfiguration(locations = "/test-context.xml")
class ParallelEmailTaskTest extends Specification {

    @Autowired
    RuntimeService runtimeService

    @Autowired
    Wiser wiser

    def setup() {
        wiser.start()
    }

    def cleanup() {
        wiser.stop()
    }

    @Unroll
    def "should send mail to recipients"() {
        when:
            runtimeService.startProcessInstanceByKey("notify-users", ["users": ["trolo@example.net", "john@doe.com"]])
        then:
            runtimeService.createProcessInstanceQuery().list() == []
            List<MimeMessage> messages = getMessagesSortedByRecipientTo()
            messages.size() == 2

            messages.get(0).getFrom()[0].toString().contains("activiti@example.com")
            messages.get(0).subject == "Event occurred"
            messages.get(0).getRecipients(Message.RecipientType.TO)[0].toString().contains("john@doe.com")

            messages.get(1).getFrom()[0].toString().contains("activiti@example.com")
            messages.get(1).subject == "Event occurred"
            messages.get(1).getRecipients(Message.RecipientType.TO)[0].toString().contains("trolo@example.net")
    }

    @Ignore
    private List<MimeMessage> getMessagesSortedByRecipientTo() {
        wiser.getMessages()
                .collect({ it.mimeMessage })
                .sort({ MimeMessage m1, MimeMessage m2 -> m1.getRecipients(Message.RecipientType.TO)[0].toString().compareTo(m2.getRecipients(Message.RecipientType.TO)[0].toString()) })
    }
}
