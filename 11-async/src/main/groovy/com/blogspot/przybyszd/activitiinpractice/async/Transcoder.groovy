package com.blogspot.przybyszd.activitiinpractice.async

import org.springframework.stereotype.Component

@Component
class Transcoder {
    private static acceptOthers

    static void acceptOthers(boolean accept) {
        acceptOthers = accept
    }

    boolean transcode(String file) {
        if (file.endsWith(".avi")) {
            Thread.sleep(2000)
            return true
        }
        if (acceptOthers) {
            Thread.sleep(2000)
            return true
        }
        Thread.sleep(1000)
        throw new UnsupportedOperationException()

    }
}
