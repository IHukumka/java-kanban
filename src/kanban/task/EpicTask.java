package kanban.task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

@JsonDeserialize(builder = EpicTask.Builder.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type",
        defaultImpl = EpicTask.class)
      @JsonSubTypes({ 
        @Type(value = EpicTask.class, name = "kanban.task.EpicTask"), 
        })
public class EpicTask extends Task {

    private ArrayList<Long> subTasks;

    /**
     * @return the subTasks
     */
    public ArrayList<Long> getSubTasks() {
        return subTasks;
    }

    /**
     * @param subTasks the subTasks to set
     */
    public void setSubTasks(ArrayList<Long> subTasks) {
        this.subTasks = subTasks;
    }

    /**
     * @constructor
     */
    public EpicTask(EpicTask.Builder builder) {
        super(builder);
        this.subTasks = new ArrayList<>();
    }
    
    @JsonPOJOBuilder(buildMethodName = "build",withPrefix = "set")
    public static class Builder extends Task.Builder<Builder> {

        ArrayList<Long> subTasks;

        /**
         * @constructor
         */
        public Builder() {
        }
        
        /**
         * @param subTasks the subTasks to set
         */
        public void setSubTasks(ArrayList<Long> subTasks) {
            this.subTasks = subTasks;
        }

        /**
         * @build EpicTask
         */
        @Override
        public EpicTask build() {
            return new EpicTask(this);
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "EpicTask   [id = " + id + 
                ", name=" + name + 
                ", description=" + description + 
                ", status=" + status + 
                ", callTime=" + dTF.format(callTime) + 
                ", subTasks=" + subTasks + 
                ", startTime=" + dTF.format(startTime) +
                ", duration=" + duration.toMinutes() +
                "]";
    }

}
