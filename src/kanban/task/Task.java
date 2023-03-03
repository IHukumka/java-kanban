package kanban.task;

import java.time.LocalDateTime;

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
     * @constructor
     */
    public Task(Task.Builder<?> builder) {
        this.type = this.getClass();
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
        this.callTime = LocalDateTime.now();
        this.id = builder.id;
    }
    
    @JsonPOJOBuilder(withPrefix = "set")
    public static class Builder<T extends Builder<T>> {
        protected Long id;
        protected String name;
        protected String description;
        protected Status status;
        protected LocalDateTime callTime;
        

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

        /**
         * @build CommonTask
         */
        public Task build() {
            return new Task(this);
        }
    }
}
