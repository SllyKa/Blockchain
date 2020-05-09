package blockchain.chain;

import blockchain.chain.exceptions.OutOfDoubleLinkedList;
import blockchain.chain.serial.SerialUnit;

import java.io.IOException;
import java.io.Serializable;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String serialFileName;

    private DoubleLinkedList chain;
    private long id;
    private int zerosNum;

    public Blockchain(String serialFileName) {
        this.chain = new DoubleLinkedList();
        this.id = 1;
        zerosNum = 0;
        this.serialFileName = serialFileName;
    }

    public Blockchain(int zerosNum, String serialFileName) {
        this(serialFileName);
        this.zerosNum = zerosNum;
    }

    public void generateBlock() {
         Block.Builder builder = new Block.Builder().id(id);

         builder.prevHash(getPrevHash());
         builder.zerosNum(zerosNum);

         chain.add(builder.build());
         blockIncrement();

         try {
             SerialUnit.serialize(this, serialFileName);
         } catch (IOException e) {
             System.out.println(e);
             System.exit(1);
         }

    }

    public void generateBlocks(int n) {
        for (int i = 0; i < n; i++) {
            this.generateBlock();
        }
    }

    private void blockIncrement() {
        id++;
    }

    private String getPrevHash() {
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
