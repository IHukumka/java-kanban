package kanban.exceptions;

@SuppressWarnings("serial")
public final class ManagerSaveException extends RuntimeException {
    public ManagerSaveException() {
        super();
    }
    
    public ManagerSaveException(final String message) {
        super(message);
    }
}
