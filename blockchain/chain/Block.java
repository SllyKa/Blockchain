package blockchain.chain;

import blockchain.StringUtil;

import java.io.Serializable;
import java.util.Date;

class Block implements Serializable {
    private final long id;
    private final long timeStamp;
    private final String prevHash;
    private final String curHash;

    private final int zerosNum;
    private long magicNumber = 0;

    private long timeCreated = 0;

    private Block(Builder builder) {
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
            return new Block(this);
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

    public long getId() {
        return this.id;
    }

    public long getTimeStamp() {
        return this.timeStamp;
    }

    public String getPrevHash() {
        return this.prevHash;
    }

    public String getCurHash() {
        return this.curHash;
    }

    public long getMagicNumber() {
        return this.magicNumber;
    }

    @Override
    public String toString() {
        return "Block:" + "\n"
                + "Id: " + getId() + "\n"
                + "Timestamp: " + getTimeStamp() + "\n"
                + "Magic number: " + getMagicNumber() + "\n"
                + "Hash of the previous block:" + "\n" + getPrevHash() + "\n"
                + "Hash of the block:" + "\n" + getCurHash() + "\n"
                + "Block was generating for " + (timeCreated / 1000000000) + " seconds";
    }
}
