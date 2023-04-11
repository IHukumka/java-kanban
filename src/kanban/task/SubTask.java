package kanban.task;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = SubTask.Builder.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type",
        defaultImpl = SubTask.class)
      @JsonSubTypes({ 
        @Type(value = SubTask.class, name = "kanban.task.SubTask"), 
        })
public class SubTask extends Task {

    private Long superTask;

    /**
     * @return the superTask
     */
    public Long getSuperTask() {
        return superTask;
    }

    /**
     * @param superTask the superTask to set
     */
    public void setSuperTask(Long superTask) {
        this.superTask = superTask;
    }

    public SubTask(SubTask.Builder builder) {
        super(builder);
        this.superTask = builder.superTask;
    }
    
    @JsonPOJOBuilder(buildMethodName = "build",withPrefix = "set")
    public static class Builder extends Task.Builder<Builder> {

        Long superTask;

        /**
         * @constructor
         */
        public Builder() {
        }

        /**
         * @param superTask the superTask to set
         */
        public Builder setSuperTask(Long superTask) {
            this.superTask = superTask;
            return this;
        }

        @Override
        public SubTask build() {
            return new SubTask(this);
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "SubTask    [id = " + id + 
                ", name=" + name + 
                ", description=" + description +
                ", status=" + status + 
                ", callTime=" + dTF.format(callTime) + 
                ", superTask=" + superTask + 
                ", startTime=" + dTF.format(startTime) +
                ", duration=" + duration.toMinutes() +
                "]";
    }

}
