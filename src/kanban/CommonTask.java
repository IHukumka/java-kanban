package kanban;

public class CommonTask {

	protected String name;
	protected String description;
	protected String status;

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
	public void setStatus(String status) {
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
	public String getStatus() {
		return status;
	}

	public CommonTask(CommonTask.Builder<?> builder) {
		this.name = builder.name;
		this.description = builder.description;
		this.status = builder.status;
	}

	public static class Builder<T extends Builder<T>> {
		private String name;
		private String description;
		private String status;

		/**
		 * @constructor
		 */
		public Builder() {
		}

		/**
		 * @param name the name to set
		 */
		public T setName(String name) {
			this.name = name;
			return (T) this;
		}

		/**
		 * @param description the description to set
		 */
		public T setDescription(String description) {
			this.description = description;
			return (T) this;
		}

		/**
		 * @param status the status to set
		 */
		public T setStatus(String status) {
			this.status = status;
			return (T) this;
		}

		/**
		 * @build CommonTask
		 */
		public CommonTask build() {
			return new CommonTask(this);
		}
	}

	@Override
	public String toString() {
		return "CommonTask [name=" + name + ", description=" + description + ", status=" + status + "]";
	}
}