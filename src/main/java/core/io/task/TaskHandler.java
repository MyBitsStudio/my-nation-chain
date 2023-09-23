package core.io.task;

import core.utils.exception.TaskException;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class TaskHandler {

    private final static Queue<Task> pendingTasks = new LinkedList<>();

    private final static List<Task> activeTasks = new LinkedList<>();

    private TaskHandler() {
        throw new UnsupportedOperationException(
                "This class cannot be instantiated!");
    }

    public static void sequence(){
        long start = System.currentTimeMillis();
        try {
            Task t;
            while ((t = pendingTasks.poll()) != null) {
                if (t.getRunning().get()) {
                    activeTasks.add(t);
                }
            }

            Task[] tasks = activeTasks.stream().filter(t2 -> t2.getBoss() == null).toArray(Task[]::new);
            for (Task task : tasks) {
                if (task.getRunning().get()) {
                    task.onTick();
                }
                if (!task.sequence()) {
                    activeTasks.remove(task);
                }
            }
        } catch (Exception e) {
            throw new TaskException("TaskHandler::sequence", e);
        }
    }

    public static void submit(@NotNull Task task) {
        if (!task.getRunning().get())
            return;
        task.onStart();
        if (task.isImmediate()) {
            task.execute();
        }
        pendingTasks.add(task);
    }

    public static void cancelTasks(Object key) {
        try {
            pendingTasks.stream().filter(t -> t != null && t.getBoss() == key).forEach(Task::stop);
            activeTasks.stream().filter(t -> t != null && t.getBoss() == key).forEach(Task::stop);
        } catch(Exception e) {
            throw new TaskException("TaskHandler::cancelTasks::"+key.toString(), e);
        }
    }

    public static void shutdown(){
        try {
            pendingTasks.forEach(Task::stop);
            activeTasks.forEach(Task::stop);
            pendingTasks.clear();
            activeTasks.clear();
        } catch(Exception e) {
            throw new TaskException("TaskHandler::shutdown", e);
        }
    }

}
