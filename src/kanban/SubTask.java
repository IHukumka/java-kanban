package kanban;

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
	
	public static class Builder extends Task.Builder<Builder>{
		
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
		return "SubTask [name=" + name + ", description=" + description + ", status="
				+ status + ", superTask=" + superTask + "]";
	}

}
