package blockchain;

public class Main {
    public static void main(String[] args) {
        Blockchain blockChain = new Blockchain();

        for (int i = 0; i < 10; i++) {
            blockChain.generateBlock();
        }
        blockChain.printChain(5);
    }
}
