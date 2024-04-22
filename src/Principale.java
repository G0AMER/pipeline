import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

class Prime extends Thread {
    private final int index;
    private final SynchronousQueue<Integer> I;
    private final SynchronousQueue<Integer> O;

    public Prime(int index, SynchronousQueue<Integer> inputQueue, SynchronousQueue<Integer> outputQueue) {
        this.index = index;
        this.I = inputQueue;
        this.O = outputQueue;
    }

    @Override
    public void run() {
        try {
            int pr;
            int n = 25;
            if (index == 1)//traiter le premier thread
            {
                pr = 2;
                int b = 0;
                System.out.println(pr + "est premier");
                for (int i = 3; i <= n; i++) {
                    if (i % pr != 0) {
                        if (b == 0) {
                            SynchronousQueue<Integer> In = O;
                            SynchronousQueue<Integer> Out = new SynchronousQueue<Integer>();
                            Prime p2 = new Prime(index + 1, In, Out);
                            Principale.pipeline.add(index + 1, p2);
                            Principale.pipeline.get(index + 1).start();
                            b = 1;
                        }
                        O.put(i);
                    }
                }
                if (b == 1) O.put(0);//msg de special de fin de communication

            } else {
                int b = 0;
                pr = I.take();
                System.out.println(pr + "est premier");
                int next;
                do {
                    next = I.take();
                    if (next % pr != 0) {

                        if (b == 0) {
                            SynchronousQueue<Integer> In = O;
                            SynchronousQueue<Integer> Out = new SynchronousQueue<Integer>();
                            Prime p2 = new Prime(index + 1, In, Out);
                            Principale.pipeline.add(index + 1, p2);
                            Principale.pipeline.get(index + 1).start();
                            b = 1;
                        }
                        O.put(next);

                    }
                }

                while (next != 0);
                if (b == 1) O.put(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}


public class Principale {


    static ArrayList<Prime> pipeline = new ArrayList<>();

    public static void main(String[] args) {


        SynchronousQueue<Integer> I = new SynchronousQueue<>();
        SynchronousQueue<Integer> O = new SynchronousQueue<>();
        Prime p = new Prime(1, I, O);
        pipeline.add(0, null);
        pipeline.add(1, p);
        pipeline.get(1).start();

    }

}