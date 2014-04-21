package com.blogspot.przybyszd.activitiinpractice.mailserver

import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by alien on 21.04.14.
 */
class MailTestSupportTest extends Specification {

    @Shared
    MailTestSupport sut = new MailTestSupport()

    @Unroll
    def "delete files in mail dir"() {
        given:
            def f1 = new File(sut.mailFolder + File.separatorChar + "test1")
            f1 << "test 1"
            def f2 = new File(sut.mailFolder + File.separatorChar + "test2")
            f2 << "test 2"
        when:
            sut.deleteMailDir()
        then:
            new File(sut.mailFolder).list().length == 0
    }

    @Unroll
    def "get files when empty"() {
        expect:
            sut.mails == []
    }

    @Unroll
    def "get files in mail dir when files exists"() {
        given:
            def f1 = new File(sut.mailFolder + File.separatorChar + "test1")
            f1 << """test 1
trololo


..."""
            def f2 = new File(sut.mailFolder + File.separatorChar + "test2")
            f2 << "test 2"
        when:
            List<String> result = sut.mails
        then:
            result == ["""test 1
trololo


...""", "test 2"]
    }

    def setupSpec() {
        new File(sut.mailFolder).mkdir()
    }

    def cleanupSpec() {
        new File(sut.mailFolder).delete()
    }


    def setup() {
        sut.deleteMailDir()

    }

    def cleanup() {
        sut.deleteMailDir()
    }


}
