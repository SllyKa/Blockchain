package blockchain.chain;

import blockchain.block.Block;
import blockchain.block.BlockDecorator;
import blockchain.block.SimpleBlock;
import blockchain.chain.exceptions.OutOfDoubleLinkedList;
import blockchain.chain.serial.SerialUnit;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String serialFileName;

    private DoubleLinkedList chain;
    private long id;

    private int zerosNum;
    private long zerosIncreaseTime = 10;
    private long zerosDecreaseTime = 60;

    private Blockchain(String serialFileName) {
        this.chain = new DoubleLinkedList();
        this.id = 1;
        zerosNum = 0;
        this.serialFileName = serialFileName;
    }

    private Blockchain(int zerosNum, String serialFileName) {
        this(serialFileName);
        this.zerosNum = zerosNum;
    }

    public void generateBlock() {
         SimpleBlock.Builder builder = new SimpleBlock.Builder().id(id);

         builder.prevHash(getLastHash());
         builder.zerosNum(zerosNum);

         Block block = builder.build();
         addCheckedBlock(block);
    }

    public synchronized boolean addBlock(Block block, long minerId) {
        if (block == null) {
            return false;
        }
        int oldZerosNum = zerosNum;

        if (block.getPrevHash().equals(getLastHash())
        && checkZeros(block.getCurHash())) {
            long timeCreated = block.getTimeCreated() / 1000000000;
            if (timeCreated < zerosIncreaseTime) {
                incZerosNum();
            } else if (timeCreated > zerosDecreaseTime) {
                decZerosNum();
            }
            BlockDecorator bd = new BlockDecorator(block, minerId, oldZerosNum, zerosNum);
            addCheckedBlock(bd);
            return true;
        }
        return false;
    }

    private void addCheckedBlock(Block block) {
        chain.add(block);
        blockIncrement();

        try {
            SerialUnit.serialize(this, serialFileName);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public long getId() {
        return this.id;
    }

    public int getZerosNum() {
        return this.zerosNum;
    }

    public void generateBlocks(int n) {
        for (int i = 0; i < n; i++) {
            this.generateBlock();
        }
    }

    public String getLastHash() {
        if (id == 1) {
            return "0";
        } else {
            try {
                return chain.getTail().block.getCurHash();
            } catch (OutOfDoubleLinkedList e) {
                System.out.println(e);
                System.exit(1);
            }
        }
        return null;
    }

    public boolean checkChain() {
        try {
            DoubleLinked it = chain.getHead().next;

            while(it.block != null) {
                if (!it.block.getPrevHash().equals(it.prev.block.getCurHash())) {
                    return false;
                }
                it = it.next;
            }
        } catch (OutOfDoubleLinkedList e) {
            System.out.println(e);
            System.exit(1);
        }
        return true;
    }

    public long size() {
        return this.id;
    }

    public void printChain(int to) {
        int i = 0;
        try {
            DoubleLinked it = chain.getHead();
            while(it.block != null && i < to) {
                System.out.println(it.block.toString());
                System.out.println();
                it = it.next;
                i++;
            }
        } catch (OutOfDoubleLinkedList e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public void printLastBlock() {
        try {
            DoubleLinked it = chain.getTail();
            System.out.println(it.block.toString());
            System.out.println();
        } catch (OutOfDoubleLinkedList e) {
            System.out.println(e);
            System.exit(1);
        }
    }

    public static Blockchain initBlockchain(String serialFileName) {
        File serialFile = new File(serialFileName);
        boolean isNewFile = false;

        Blockchain blockChain;

        try {
            isNewFile = serialFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
            return null;
        }

        if (!isNewFile) {
            try {
                blockChain = (Blockchain) SerialUnit.deserialize(serialFileName);
                if (!blockChain.checkChain()) {
                    System.out.println("Chain is invalid.");
                    return null;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e);
                return null;
            }
        } else {
            blockChain = new Blockchain(serialFileName);
        }
        return blockChain;
    }

    private void incZerosNum() {
        this.zerosNum++;
    }

    private void decZerosNum() {
        this.zerosNum--;
    }

    private boolean checkZeros(String hash) {
        for (int i = 0; i < hash.length() && i < zerosNum; i++) {
            if (hash.charAt(i) != '0') {
                return false;
            }
        }
        return true;
    }

    private void blockIncrement() {
        id++;
    }

    private class DoubleLinked implements Serializable {
        protected Block block;
        protected DoubleLinked prev;
        protected DoubleLinked next;

        protected DoubleLinked(Block block) {
            this.block = block;
        }
    }

    private class DoubleLinkedList implements Serializable {
        private DoubleLinked head;
        private DoubleLinked tail;

        DoubleLinkedList() {
            head = new DoubleLinked(null);
            tail = new DoubleLinked(null);

            tail.next = null;

            head.prev = null;

            head.next = tail;
            tail.prev = head;
        }

        public void add(Block block) {
            DoubleLinked newBlock = new DoubleLinked(block);
            DoubleLinked prev = tail.prev;

            newBlock.next = tail;
            newBlock.prev = prev;

            tail.prev = newBlock;
            prev.next = newBlock;
        }

        public DoubleLinked getHead() throws OutOfDoubleLinkedList {
            if (head.next == tail) {
                throw new OutOfDoubleLinkedList("List is empty(head).");
            }
            return head.next;
        }

        public DoubleLinked getTail() throws OutOfDoubleLinkedList {
            if (tail.prev == head) {
                throw new OutOfDoubleLinkedList("List is empty(tail).");
            }
            return tail.prev;
        }
    }
}
