import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;


public class Cache_Sim {
    public static final int BLOCK_SIZE = 64;
    public static final int LRU = 0;
    public static final int WRITE_THROUGH = 0;
    public static final int WRITE_BACK = 1;




    static int replacementPolicy;
    static int writeBackPolicy;

    static double numMisses = 0;
    static double numReads = 0;
    static double numWrites = 0;
    static double totalRequests = 0;

    static class Block {
        BigInteger tag;
        boolean isDirty;
        boolean isEmpty;

        Block() {
            tag = BigInteger.valueOf(0);
            isDirty = false;
            isEmpty = true;
        }
    }

    static class Cache {
        int associativity;
        int numSets;
        int size;
        Block[][] blocks;
        ArrayList<LinkedList<Integer>> metadata;

        Cache(int associativity, int cacheSize) {
            this.associativity = associativity;
            size = cacheSize;
            numSets = cacheSize / ( BLOCK_SIZE * associativity );
            blocks = new Block[numSets][associativity];
            metadata = new ArrayList<LinkedList<Integer>>();

            // Initialize blocks.
            for (int i = 0; i < blocks.length; i++)
                for (int j = 0; j < blocks[i].length; j++)
                    blocks[i][j] = new Block();

            // Initialize metadata.
            for (int i = 0; i < numSets; i++)
                metadata.add(new LinkedList<Integer>());
        }

        // Finds a free cache block to be used.
        int getFreeBlock(int setNumber) {
            LinkedList<Integer> set = metadata.get(setNumber);

            // Check if there is a free block.
            for (int i = 0; i < associativity; i++)
                if (blocks[setNumber][i].isEmpty)
                    return i;

            return set.remove();
        }

        // Returns the index for the given tag. Returns -1 if the tag is not in the cache.
        int indexOf(BigInteger tag, int setNumber) {
            for (int i = 0; i < associativity; i++)
                if (blocks[setNumber][i].tag != null && blocks[setNumber][i].tag.compareTo(tag) == 0)
                    return i;

            return -1;
        }

        // Reads a tag from the cache in the specified set.
        void read(BigInteger tag, int setNumber) {
            int index = indexOf(tag, setNumber);

            // Check for a hit.
            if (index != -1) {
                updateMetadata(setNumber, index);
            }

            // Check for a miss.
            else {
                numMisses++;
                index = getFreeBlock(setNumber);
                Block block = blocks[setNumber][index];

                block.tag = tag;
                block.isEmpty = false;
                numReads++;

                if (writeBackPolicy == WRITE_BACK) {
                    if (block.isDirty)
                        numWrites++;

                    block.isDirty = false;
                }

                updateMetadata(setNumber, index);
            }
        }

        // Updates the cache metadata according to the specified replacement policy.
        void updateMetadata(int setNumber, int index) {
            LinkedList<Integer> set = metadata.get(setNumber);

            // Check if the queue is empty.
            if (set.size() == 0) {
                set.add(index);
            } else {
                if (replacementPolicy == LRU) {
                    int targetIndex = set.indexOf(index);

                    if (targetIndex != -1)
                        set.remove(targetIndex);
                }

                set.add(index);
            }
        }

        // Writes a tag to the cache in the specified set.
        void write(BigInteger tag, int setNumber) {
            Block block;
            int index = indexOf(tag, setNumber);

            // Check for a hit.
            if (index != -1) {
                block = blocks[setNumber][index];

                block.tag = tag;
                block.isEmpty = false;

                // Check the replacement policy.
                switch (writeBackPolicy) {
                    case WRITE_THROUGH:
                        numWrites++;
                        break;

                    case WRITE_BACK:
                        block.isDirty = true;
                        break;
                }

                updateMetadata(setNumber, index);
            }

            // Check for a miss.
            else {
                numMisses++;
                index = getFreeBlock(setNumber);
                block = blocks[setNumber][index];
                block.tag = tag;
                block.isEmpty = false;
                numReads++;

                // Check the replacement policy.
                switch (writeBackPolicy) {
                    case WRITE_THROUGH:
                        numWrites++;
                        break;

                    case WRITE_BACK:
                        if (block.isDirty)
                            numWrites++;
                        blocks[setNumber][index].isDirty = true;
                        break;
                }

                updateMetadata(setNumber, index);
            }
        }
    }

    public static void main(String [] args) throws FileNotFoundException
    {
        // Parse command line arguments.
        int cacheSize = Integer.parseInt(args[0]);
        int associativity = Integer.parseInt(args[1]);
        replacementPolicy = Integer.parseInt(args[2]);
        writeBackPolicy = Integer.parseInt(args[3]);
        String file = args[4];

        Cache cache = new Cache(associativity, cacheSize);
        Scanner scanner = new Scanner(new File(file));

        BigInteger address;
        BigInteger tag;
        char operation;
        int setNumber;
        String line;

        while (scanner.hasNextLine())
        {
            // Process each line.
            line = scanner.nextLine();
            operation = line.charAt(0);
            address = new BigInteger(line.substring(4), 16);


            tag = address.divide(BigInteger.valueOf(BLOCK_SIZE));
          //setNumber = (tag.mod(BigInteger.valueOf(cache.numSets))).intValue(); // setNumber = tag % numSet
           setNumber = (tag.mod(BigInteger.valueOf(cache.numSets+1))).intValue();
            totalRequests++;

            // Check the operation.
            switch (operation)
            {
                case 'R':
                    cache.read(tag, setNumber);
                    break;

                case 'W':
                    cache.write(tag, setNumber);
                    break;

                default:
                    System.out.println("Error! Invalid operation.");
            }
        }

        // Print the results.
        System.out.printf("%f\n", numMisses / totalRequests);
        System.out.printf("%f\n", numWrites);
        System.out.printf("%f\n", numReads);
    }
}

