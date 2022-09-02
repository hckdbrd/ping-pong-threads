import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Main {
    public static void main(String[] args) {

        int count = 5;
        List<Thread> processors = createProcessors(count);
        processors.forEach(Thread::start);
    }

    /**
     * "createProcessors" describes the work of group of threads
     * where every thread id is an index [0;5).
     **/
    private static List<Thread> createProcessors(int count) {

        //Common queue of threads.
        List<BlockingQueue<String>> queues = new ArrayList<>();
        for (int index = 0; index < count; index++) {
            queues.add(new LinkedBlockingQueue<>());
        }
        //-----

        List<Thread> result = new ArrayList<>();
        //Processing into "sendTo" and "readFrom" message queues and return of thread call.
        for (int index = 0; index < count; index++) {
            BlockingQueue<String> to = queues.get(index);
            BlockingQueue<String> from = index - 1 < 0 ? queues.get(queues.size() - 1) : queues.get(index - 1);

            result.add(new EventProcessor(String.valueOf(index+1), to, from));
        }
        //-----

        //Start trigger.
        queues.get(queues.size() - 1).add("START");
        //-----
        return result;
    }

    /**
     * "EventProcessor" describes the entity of thread
     * which can receive and send some message from some of other threads.
     * Messages store in queues "sendTo" and "readFrom".
    **/
    @RequiredArgsConstructor
    static class EventProcessor extends Thread {
        private final String message;
        private final BlockingQueue<String> sendTo;
        private final BlockingQueue<String> readFrom;

        @SneakyThrows
        @SuppressWarnings("All")
        @Override
        public void run() {
            while (true) {
                String value = readFrom.take();
                Thread.sleep(1000);

                System.out.println(value);

                sendTo.add(message);
                Thread.sleep(1000);
            }
        }
    }
}