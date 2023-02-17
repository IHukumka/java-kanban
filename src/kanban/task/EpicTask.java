package kanban.task;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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

    public static class Builder extends Task.Builder<Builder> {

        ArrayList<Long> subTasks;

        /**
         * @constructor
         */
        public Builder() {
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
                "]";
    }

}
