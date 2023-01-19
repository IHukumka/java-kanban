package kanban;

public class CommonTask extends Task{
    
    
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
		return "CommonTask [name=" + name + ", description=" + description + ", status=" + status + "]";
	}
}