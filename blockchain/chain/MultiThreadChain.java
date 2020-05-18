package blockchain.chain;

import blockchain.block.Block;
import blockchain.block.SimpleBlock;

public class MultiThreadChain implements Runnable {

    private Blockchain blockchain;
    private long minerId;

    public MultiThreadChain(long minerId, Blockchain chain) {
        this.minerId = minerId;
        this.blockchain = chain;
    }
    @Override
    public void run() {
        boolean added = false;
        while (!added) {
            Block block = SimpleBlock.makeBlock(blockchain);
            added = blockchain.addBlock(block, minerId);
        }
    }
}
