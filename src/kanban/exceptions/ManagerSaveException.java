package kanban.exceptions;

import java.io.IOException;

@SuppressWarnings("serial")
public final class ManagerSaveException extends IOException {
    public ManagerSaveException() {
        super();
    }
    
    public ManagerSaveException(final String message) {
        super(message);
    }
}
