package kanban.exceptions;

import java.time.LocalDateTime;

@SuppressWarnings("serial")
public class TimeInputException extends Exception {
	private final LocalDateTime startTime;
	private final LocalDateTime endTime;
		
    public TimeInputException(final String message, final LocalDateTime startTime, final LocalDateTime endTime) {
        super(message);
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public String getDetailedMessage() {
    	return getMessage() + startTime.toString() + " - " + endTime.toString();
    }
} 