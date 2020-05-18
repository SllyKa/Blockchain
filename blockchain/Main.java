package blockchain;

import java.util.concurrent.*;
import blockchain.chain.Blockchain;
import blockchain.chain.MultiThreadChain;

/* do not forget to delete serialize file if u need it */

public class Main {
    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(12);

        String serialFileName = "chain01.bc";

        Blockchain blockChain = Blockchain.initBlockchain(serialFileName);

        for (int i = 0; i < 6; i++) {
            executor.submit(new MultiThreadChain(i, blockChain));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(10000, TimeUnit.MILLISECONDS);

/*            if (executor.awaitTermination(10000, TimeUnit.MILLISECONDS)) {
                System.out.println("Exec");
            } else {
                System.out.println("Don't exec");
            }*/
        } catch (InterruptedException e) {
            System.out.println(e);
            return;
        }

        //blockChain.generateBlocks(7);

        blockChain.printChain(5);
    }
}
