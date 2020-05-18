package blockchain.block;

import java.io.Serializable;

public interface Block extends Serializable {

    long getId();
    long getTimeStamp();
    String getPrevHash();
    String getCurHash();
    long getMagicNumber();
    long getTimeCreated();
    String toString();
}
