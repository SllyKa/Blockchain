package blockchain.block;

import blockchain.block.Block;

public class BlockDecorator implements Block {

    private final Block block;
    private final long minerId;
    private final int zerosNum;
    private final int zerosNumChange;

    public BlockDecorator(Block block, long minerId, int zerosNum, int zerosNumChange) {
        this.block = block;
        this.minerId = minerId;
        this.zerosNum = zerosNum;
        this.zerosNumChange = zerosNumChange;
    }

    @Override
    public long getId() {
        return block.getId();
    }

    @Override
    public long getTimeStamp() {
        return block.getTimeStamp();
    }

    @Override
    public String getPrevHash() {
        return block.getPrevHash();
    }

    @Override
    public String getCurHash() {
        return block.getCurHash();
    }

    @Override
    public long getMagicNumber() {
        return block.getMagicNumber();
    }

    @Override
    public long getTimeCreated() {
        return block.getTimeCreated();
    }

    @Override
    public String toString() {
        String zerosNumberMessage;

        if (zerosNum > zerosNumChange) {
            zerosNumberMessage = "N was decreased by " + Math.abs(zerosNum - zerosNumChange);
        } else if (zerosNum < zerosNumChange) {
            zerosNumberMessage = "N was increased to " + Math.abs(zerosNum - zerosNumChange);
        } else {
            zerosNumberMessage = "N stays the same";
        }

        return "Block: " + "\n"
                + "Created by miner # " + minerId + "\n"
                + block.toString() + "\n"
                + zerosNumberMessage;
    }
}
