package core.utils.exception;

public class TaskException extends RuntimeException {

    public TaskException(String message, Exception e){
        super(message, e);
    }

    public TaskException(String message){
        super("[TASK EXCEPTION] "+message);
    }
}
