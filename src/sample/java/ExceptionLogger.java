package sample.java;

class ExceptionLogger {

    static void log(Exception e) {
        String source = Thread.currentThread().getStackTrace()[2].getClassName();
        System.err.println(e.toString());
        for(StackTraceElement stackTraceElement : e.getStackTrace()) {
            if(stackTraceElement.getClassName().equals(source)) {
                System.err.println("\tat " + stackTraceElement);
                break;
            }
        }
    }
}
