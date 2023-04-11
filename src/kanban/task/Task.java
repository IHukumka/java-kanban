package kanban.task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = Task.Builder.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type")
      @JsonSubTypes({ 
        @Type(value = CommonTask.class, name = "kanban.task.CommonTask"), 
        @Type(value = EpicTask.class, name = "kanban.task.EpicTask"),
        @Type(value = SubTask.class, name = "kanban.task.SubTask")
      })
public class Task implements Comparable<Task> {
    
    @JsonProperty
    protected Class<? extends Task> type;
    protected Long id;
    protected String name;
    protected String description;
    protected Status status;
    protected LocalDateTime callTime;
    protected LocalDateTime startTime;
    protected Duration duration;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * @return the callTime
     */
    public LocalDateTime getCallTime() {
        return callTime;
    }

    /**
     * @param callTime the callTime to set
     */
    public void setCallTime(LocalDateTime callTime) {
        this.callTime = callTime;
    }

   	/**
     * @return the comparison based on callTime
     */
    @Override
    public int compareTo(Task task) {
        return this.getCallTime().compareTo(task.getCallTime());
    }

    /**
	 * @return the startTime
	 */
	public LocalDateTime getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	/**
	 * @return the duration
	 */
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @param duration the duration to set
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
     * @constructor
     */
    public Task(Task.Builder<?> builder) {
        this.type = this.getClass();
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.callTime = LocalDateTime.now();
        this.id = builder.id;
        this.startTime = builder.startTime;
        this.duration = builder.duration;
    }
    
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder<T extends Builder<T>> {
        protected Long id;
        protected String name;
        protected String description;
        protected Status status;
        protected LocalDateTime callTime;
        protected LocalDateTime startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneId.systemDefault());
        protected Duration duration = Duration.ZERO;
        

        /**
         * @constructor
         */
        public Builder() {
        }
        
        /**
         * @return the id
         */
        public Long getId() {
            return id;
        }

        /**
         * @param id the id to set
         */
        @SuppressWarnings("unchecked")
        public T setId(Long id) {
            this.id = id;
            return (T) this;
        }

        /**
         * @return the callTime
         */
        public LocalDateTime getCallTime() {
            return callTime;
        }

        /**
         * @param callTime the callTime to set
         */
        @SuppressWarnings("unchecked")
        public T setCallTime(LocalDateTime callTime) {
            this.callTime = callTime;
            return (T) this;
        }


        /**
         * @param name the name to set
         */
        @SuppressWarnings("unchecked")
        public T setName(String name) {
            this.name = name;
            return (T) this;
        }

        /**
         * @param description the description to set
         */
        @SuppressWarnings("unchecked")
        public T setDescription(String description) {
            this.description = description;
            return (T) this;
        }

        /**
         * @param status the status to set
         */
        @SuppressWarnings("unchecked")
        public T setStatus(Status status) {
            this.status = status;
            return (T) this;
        }

        @SuppressWarnings("unchecked")
		public T setStartTime(LocalDateTime startTime) {
			this.startTime = startTime;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public T setDuration(Duration duration) {
			this.duration = duration;
			return (T) this;
		}

		/**
         * @build CommonTask
         */
        public Task build() {
            return new Task(this);
        }
    }
}
