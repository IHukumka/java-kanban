package kanban.task;

public class Task {

    protected String name;
    protected String description;
    protected Status status;

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
     * @constructor
     */
    public Task(Task.Builder<?> builder) {
        this.name = builder.name;
        this.description = builder.description;
        this.status = builder.status;
    }

    public static class Builder<T extends Builder<T>> {
        private String name;
        private String description;
        private Status status;

        /**
         * @constructor
         */
        public Builder() {
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
