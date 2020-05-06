package blockchain;

import java.util.Date;

public class Blockchain {
    private DoubleLinkedList chain;
    private long id;

    public Blockchain() {
        this.chain = new DoubleLinkedList();
        this.id = 1;
    }

    public void generateBlock() {
         Block.Builder builder = new Block.Builder().id(id);

         if (id == 1) {
             builder.prevHash("0");
         } else {
             try {
                 builder.prevHash(chain.getTail().block.getCurHash());
             } catch (OutOfDoubleLinkedList e) {
                 System.out.println(e);
                 System.exit(1);
             }
         }
         id++;
         chain.add(builder.build());
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

    private class DoubleLinked {
        protected Block block;
        protected DoubleLinked prev;
        protected DoubleLinked next;

        protected DoubleLinked(Block block) {
            this.block = block;
        }
    }

    private class DoubleLinkedList {
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

class OutOfDoubleLinkedList extends Exception {
    public OutOfDoubleLinkedList(String message) {
        super(message);
    }
}

class Block {
    private final long id;
    private final long timeStamp;
    private final String prevHash;
    private final String curHash;

    private Block(long id, long timeStamp, String prevHash, String curHash) {
        this.id = id;
        this.timeStamp = timeStamp;
        this.prevHash = prevHash;
        this.curHash = curHash;
    }

    public static class Builder {
        private long id;
        private long timeStamp;
        private String prevHash;
        private String curHash;

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder prevHash(String prevHash) {
            this.prevHash = prevHash;
            return this;
        }

        public Block build() {
            timeStamp = getTimeStamp();
            curHash = getHash();
            return new Block(id, timeStamp, prevHash, curHash);
        }

        private long getTimeStamp() {
            return new Date().getTime();
        }

        private String getHash() {
            StringBuilder hash = new StringBuilder();
            hash.append(id).append(timeStamp).append(prevHash);
            return StringUtil.applySha256(hash.toString());
        }
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

    @Override
    public String toString() {
        return "Block:" + "\n"
                + "Id: " + getId() + "\n"
                + "Timestamp: " + getTimeStamp() + "\n"
                + "Hash of the previous block:" + "\n" + getPrevHash() + "\n"
                + "Hash of the block:" + "\n" + getCurHash();
    }
}
