package com.blogspot.przybyszd.activitiinpractice.servicetask

import org.springframework.stereotype.Component

@Component("personDao")
class PersonDao {

    long persist(Person person) {
        return 15l
    }
}
