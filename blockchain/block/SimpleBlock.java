package blockchain.block;

import blockchain.StringUtil;
import blockchain.chain.Blockchain;

import java.io.Serializable;
import java.util.Date;

public class SimpleBlock implements Block {
    private final long id;
    private final long timeStamp;
    private final String prevHash;
    private long magicNumber = 0;
    private final String curHash;

    private final int zerosNum;

    private long timeCreated = 0;

    private SimpleBlock(Builder builder) {
        this.id = builder.id;
        this.timeStamp = builder.timeStamp;
        this.prevHash = builder.prevHash;
        this.zerosNum = builder.zerosNum;

        this.curHash = getHash();
    }

    public static class Builder {
        private long id;
        private long timeStamp;
        private String prevHash;

        private int zerosNum = 0;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder prevHash(String prevHash) {
            this.prevHash = prevHash;
            return this;
        }

        public Builder zerosNum(int zNum) {
            if (zNum <= 0) {
                return this;
            }
            this.zerosNum = zNum;
            return this;
        }

        public Block build() {
            timeStamp = getTimeStamp();
            return new SimpleBlock(this);
        }

        private long getTimeStamp() {
            return new Date().getTime();
        }
    }

    private String getHash() {
        StringBuilder sum = new StringBuilder();
        sum.append(id).append(timeStamp).append(prevHash);
        return proofWork(sum.toString());
    }

    private String proofWork(String sum) {
        String hash = null;
        long start = System.nanoTime();
        do {
            magicNumber = getRandom(0, Long.MAX_VALUE);
            String tryMagic = sum + magicNumber;
            hash = StringUtil.applySha256(tryMagic);
        } while (!checkZeros(hash));
        timeCreated = System.nanoTime() - start;
        return hash;
    }

    private long getRandom(long a, long b) {
        return (long) (a + Math.random() * b);
    }

    private boolean checkZeros(String hash) {
        for (int i = 0; i < hash.length() && i < zerosNum; i++) {
            if (hash.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    @Override
    public long getId() {
        return this.id;
    }

    @Override
    public long getTimeStamp() {
        return this.timeStamp;
    }

    @Override
    public String getPrevHash() {
        return this.prevHash;
    }

    @Override
    public String getCurHash() {
        return this.curHash;
    }

    @Override
    public long getMagicNumber() {
        return this.magicNumber;
    }

    @Override
    public long getTimeCreated() {
        return this.timeCreated;
    }

    public static synchronized Block makeBlock(Blockchain blockchain) {
        Block block = new Builder()
                .id(blockchain.getId())
                .prevHash(blockchain.getLastHash())
                .zerosNum(blockchain.getZerosNum())
                .build();
        return block;
    }

    @Override
    public String toString() {
        return "Id: " + getId() + "\n"
                + "Timestamp: " + getTimeStamp() + "\n"
                + "Magic number: " + getMagicNumber() + "\n"
                + "Hash of the previous block:" + "\n" + getPrevHash() + "\n"
                + "Hash of the block:" + "\n" + getCurHash() + "\n"
                + "Block was generating for " + (timeCreated / 1000000000) + " seconds";
    }
}
