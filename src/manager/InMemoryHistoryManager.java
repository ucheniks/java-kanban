package manager;

import classes.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
    private int size;
    private final Map<Integer, Node> historyManager;

    public InMemoryHistoryManager() {
        historyManager = new HashMap<>();
    }

    private void linkLast(Task element) {
        Node oldTail = tail;
        Node newNode = new Node(oldTail, element, null);
        tail = newNode;
        if (oldTail == null) {
            head = tail;
        } else {
            oldTail.next = newNode;
        }
        size++;
        historyManager.put(element.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        Node currentHead = head;
        int size = 0;
        if (head.data == tail.data) {
            listOfTasks.add(tail.data);
            return listOfTasks;
        }
        while (size < this.size) {
            listOfTasks.add(currentHead.data);
            currentHead = currentHead.next;
            size++;
        }
        return listOfTasks;
    }

    private void removeNode(Node node) {
        if (node.equals(head) && size == 2) {
            tail.prev = null;
            head = tail;
        } else if (node.equals(tail) && size == 2) {
            head.next = null;
            tail = head;
        } else if (node.equals(tail)) {
            Node oldTail = tail;
            Node newNode = new Node(oldTail.prev.prev, oldTail.prev.data, null);
            tail = newNode;
            oldTail.prev.prev.next = newNode;
        } else {
            node.data = node.next.data;
            if (node.data == tail.data) {
                tail = node;
                tail.next = null;
                tail.prev.next = tail;
            } else {
                node.next = node.next.next;
                node.next.prev = node;
            }
        }

        size--;
        historyManager.put(node.data.getId(), node);
    }


    @Override
    public void add(Task task) {
        Node nodeInHistory = historyManager.get(task.getId());
        if (historyManager.containsKey(task.getId()) && !nodeInHistory.equals(tail) && size != 1) {
            removeNode(nodeInHistory);
            linkLast(task);
        } else if (!historyManager.containsKey(task.getId())) {
            linkLast(task);
        }
    }

    @Override
    public void remove(int id) {
        if (historyManager.containsKey(id)) {
            removeNode(historyManager.get(id));
            historyManager.remove(id);
        }
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    class Node {
        public Task data;
        public Node next;
        public Node prev;

        public Node(Node prev, Task data, Node next) {
            this.data = data;
            this.next = next;
            this.prev = prev;
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;
            Node node = (Node) object;
            return Objects.equals(data, node.data) && Objects.equals(next, node.next) && Objects.equals(prev, node.prev);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data, next, prev);
        }
    }
}

