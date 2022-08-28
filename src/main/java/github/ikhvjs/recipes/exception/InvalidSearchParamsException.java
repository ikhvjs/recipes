package github.ikhvjs.recipes.exception;

import java.util.List;

public class InvalidSearchParamsException extends RuntimeException{

    List<String> messages;

    public InvalidSearchParamsException(String message) {
        super(message);
    }

    public InvalidSearchParamsException(List<String> messages){
        super(String.join(", ", messages));
        this.messages = messages;
    }

    public List<String> getMessages() {
        return messages;
    }
}
