package kanban;
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

	public EpicTask (EpicTask.Builder builder) {
		super(builder);
		this.subTasks = new ArrayList<>();
	}
	
	public static class Builder extends Task.Builder<Builder>{
		
		ArrayList<Long> subTasks;
		
		/**
		 * @constructor
		 */
		public Builder() {
		}

		@Override
		public EpicTask build() {
			return new EpicTask(this);
		}
	}

	@Override
	public String toString() {
		return "EpicTask [name=" + name + ", description=" + description + ", status=" + status + ", subTasks="
				+ subTasks + "]";
	}

}
