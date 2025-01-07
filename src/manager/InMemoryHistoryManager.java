package manager;

import classes.tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    private Node head;
    private Node tail;
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
        historyManager.put(element.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> listOfTasks = new ArrayList<>();
        Node currentHead = head;
        listOfTasks.add(head.data);
        while (currentHead.data != null && currentHead.data != tail.data) {
            currentHead = currentHead.next;
            listOfTasks.add(currentHead.data);
        }
        return listOfTasks;
    }

    private void removeNode(Node node) {
        if (node.data == head.data && node.data == tail.data) {
            head = null;
            tail = null;
        } else {
            if (node.equals(head)) {
                node.next.prev = null;
                head = node.next;
                historyManager.put(head.data.getId(), head);
            } else {
                if (node.equals(tail)) {
                    node.prev.next = null;
                    tail = node.prev;
                    historyManager.put(tail.data.getId(), tail);
                } else {
                    node.prev.next = node.next;
                    node.next.prev = node.prev;
                    historyManager.put(node.prev.data.getId(), node.prev);
                    historyManager.put(node.next.data.getId(), node.next);
                }
            }
        }
    }


    @Override
    public void add(Task task) {
        Node nodeInHistory = historyManager.get(task.getId());
        if (historyManager.containsKey(task.getId())) {
            removeNode(nodeInHistory);
        }
        linkLast(task);
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

    private static class Node {
        private final Task data;
        private Node next;
        private Node prev;

        private Node(Node prev, Task data, Node next) {
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

