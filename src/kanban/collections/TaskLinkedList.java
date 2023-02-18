package kanban.collections;

import java.util.ArrayList;
import java.util.HashMap;

import kanban.task.Task;

@SuppressWarnings({ "rawtypes", "hiding" })
public final class TaskLinkedList<Task> {

    public CustomNode<Task> head;
    public CustomNode<Task> tail;
    private int size = 0;
    private HashMap<Long, CustomNode<Task>> nodes;
    
    /**
     * @constructor
     */
    public TaskLinkedList() {
        this.nodes = new HashMap<>();
    }
    
    /**
     * @add a task to the end of the chain
     */
    public void add(Task task) {
        linkLast(task);
    }
    
    /**
     * @remove task from the chain by id
     */
    @SuppressWarnings("unchecked")
    public void remove(Long id) {
        if (nodes.containsKey(id)) {
            CustomNode<Task> node = this.nodes.get(id);
            removeNode(node);
        }
    }
    
    /**
     * @return the list size
     */
    public int size() {
        return this.size;
    }
    
    /**
     * @return the ArrayList of tasks in the list
     */
    public ArrayList<Task> toArrayList() {
        ArrayList<Task> result = new ArrayList<>();
        for (CustomNode<Task> x = this.head; x != null; x = x.next) {
            result.add(x.data);
        }
        return result;
    }
    
    /**
     * @create tail node
     */
    private void linkLast(Task task) {
        CustomNode<Task> last = tail;
        CustomNode<Task> newNode = new CustomNode<Task>(last, task, null);
        Long id = ((kanban.task.Task) task).getId();
        boolean contains = this.nodes.containsKey(id);
        if (contains) {
            this.remove(id);
        }
        tail = newNode;
        if (last == null) {
            head = newNode;
        } else {
            last.next = newNode;
        }
        this.nodes.put(((kanban.task.Task) task).getId(), newNode);
        size++;
    }
    
    /**
     * @create head node
     */
    public void push(Task task) {
        CustomNode<Task> first = this.head;
        CustomNode<Task> newNode = new CustomNode<>(task);
        Long id = ((kanban.task.Task) task).getId();
        if (this.nodes.containsKey(id)) {
            this.remove(id);
        }
        if (this.head == null) {
            this.head = newNode;
        } else {
            newNode.next = first;
            first.prev = newNode;
            this.head = newNode;
            if (this.tail == null) {
                this.tail = first;
            }
        }
        this.nodes.put(((kanban.task.Task) task).getId(), newNode);
        size++;
    }

    /**
     * @remove the tail node
     */
    public void removeLast() {
        removeNode(this.tail);
    }
    
    /**
     * @remove the head node
     */
    public void removeFirst() {
        removeNode(this.head);
    }
    
    /**
     * @remove given node
     */
    private void removeNode(CustomNode<Task> link) {
        CustomNode<Task> next = link.next;
        CustomNode<Task> prev = link.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
            link.prev = null;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
            link.next = null;
        }

        link.data = null;
        size--;
    }
    
    class CustomNode<Task> {

        public Task data;
        public CustomNode<Task> next;
        public CustomNode<Task> prev;
        
        /**
         * @construct default node
         */
        public CustomNode(Task data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
        
        /**
         * @construct node between the given nodes
         */
        public CustomNode(CustomNode<Task> prev, Task data, CustomNode<Task> next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }
    }
}