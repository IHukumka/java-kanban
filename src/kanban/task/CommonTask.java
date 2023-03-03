package kanban.task;

import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(builder = CommonTask.Builder.class)
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME, 
        include = JsonTypeInfo.As.PROPERTY, 
        property = "type",
        defaultImpl = CommonTask.class)
      @JsonSubTypes({ 
        @Type(value = CommonTask.class, name = "kanban.task.CommonTask"), 
        })
public class CommonTask extends Task {

    public CommonTask(CommonTask.Builder builder) {
        super(builder);
    }

    
    public static class Builder extends Task.Builder<Builder> {

        /**
         * @constructor
         */
        public Builder() {
        }

        /**
         * @build CommonTask
         */
        @Override
        public CommonTask build() {
            return new CommonTask(this);
        }
    }

    @Override
    public String toString() {
        DateTimeFormatter dTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "CommonTask [id = " + id + 
                ", name=" + name + 
                ", description=" + description + 
                ", status=" + status + 
                ", callTime=" + dTF.format(callTime) + 
                "]";
    }
}