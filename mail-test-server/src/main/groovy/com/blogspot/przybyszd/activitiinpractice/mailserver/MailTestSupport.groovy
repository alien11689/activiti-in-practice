package com.blogspot.przybyszd.activitiinpractice.mailserver

class MailTestSupport {
    static Properties properties

    private static void setUp() {
        if (properties == null) {
            properties = new Properties()
            properties.load(MailTestSupport.class.getResourceAsStream("/smtp.properties"))
        }
    }

    String getMailFolder() {
        setUp()
        properties.getProperty("smtp.folder")
    }

    void deleteMailDir() {
        setUp()
        delete(new File(mailFolder))
    }

    private void delete(File file) {
        println "Dir ${file.canonicalPath}";
        file.eachFile {
            println "Deleting file ${it.canonicalPath}";
            it.delete()
        }
    }

    List<String> getMails() {
        setUp()
        new File(mailFolder).listFiles().sort { File f1, File f2 -> f1.name < f2.name ? -1 : 1 }.collect {
            File f ->
                f.withReader {
                    Reader r ->
                        r.readLines().join('\n')
                }
        }
    }
}
