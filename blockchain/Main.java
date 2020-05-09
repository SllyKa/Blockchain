package blockchain;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import blockchain.chain.Blockchain;
import blockchain.chain.serial.SerialUnit;

public class Main {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);

        Blockchain blockChain;

        String serialFileName = "chain01.bc";
        File serialFile = new File(serialFileName);
        boolean isNewFile = false;

        try {
            isNewFile = serialFile.createNewFile();
        } catch (IOException e) {
            System.out.println(e);
            return;
        }

        if (!isNewFile) {
            try {
                blockChain = (Blockchain) SerialUnit.deserialize(serialFileName);
                if (!blockChain.checkChain()) {
                    System.out.println("Chain is invalid.");
                    return;
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e);
                return;
            }
        } else {
            System.out.println("Enter how many zeros the hash must starts with: ");
            int zerosNum = Integer.parseInt(scan.nextLine());

            blockChain = new Blockchain(zerosNum, serialFileName);
        }

        blockChain.generateBlocks(7);
        blockChain.printChain(5);
    }
}
