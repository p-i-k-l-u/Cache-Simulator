//
//
//import javax.swing.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.io.FileNotFoundException;
//
//public class CacheSimulatorGUI {
//    private JFrame frame;
//    private JTextField cacheSizeField, associativityField, replacementPolicyField, writeBackPolicyField, inputFileField;
//    private JTextArea outputArea;
//
//    public CacheSimulatorGUI() {
//        // Initialize the frame
//        frame = new JFrame("Cache Simulator");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
//
//        // Create input fields
//        cacheSizeField = new JTextField(20);
//        associativityField = new JTextField(20);
//        replacementPolicyField = new JTextField(20);
//        writeBackPolicyField = new JTextField(20);
//        inputFileField = new JTextField(20);
//
//        // Add components to frame
//        frame.add(new JLabel("Cache Size:"));
//        frame.add(cacheSizeField);
//        frame.add(new JLabel("Associativity:"));
//        frame.add(associativityField);
//        frame.add(new JLabel("Replacement Policy:"));
//        frame.add(replacementPolicyField);
//        frame.add(new JLabel("Write Back Policy:"));
//        frame.add(writeBackPolicyField);
//        frame.add(new JLabel("Input File:"));
//        frame.add(inputFileField);
//
//        // Create output area
//        outputArea = new JTextArea(10, 30);
//        outputArea.setEditable(false);
//
//        // Add submit button
//        JButton submitButton = new JButton("Run Simulation");
//        frame.add(submitButton);
//        submitButton.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                runSimulation();
//            }
//        });
//
//        // Add output area
//        frame.add(new JScrollPane(outputArea));
//
//        // Set frame size and make it visible
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    private void runSimulation() {
//        // Get values from text fields
//        int cacheSize = Integer.parseInt(cacheSizeField.getText());
//        int associativity = Integer.parseInt(associativityField.getText());
//        int replacementPolicy = Integer.parseInt(replacementPolicyField.getText());
//        int writeBackPolicy = Integer.parseInt(writeBackPolicyField.getText());
//        String inputFile = inputFileField.getText();
//
//        // Run the simulation and catch any exceptions
//        try {
//            // Create a cache simulator object
//            CacheSimulator.Cache cache = new CacheSimulator.Cache(associativity, cacheSize);
//            // Call a method to process the input file and run the simulation
//            String results = CacheSimulator.runSimulation(replacementPolicy, writeBackPolicy, inputFile);
//            // Display output in outputArea
//            outputArea.setText(results);
//        } catch (NumberFormatException ex) {
//            outputArea.setText("Number format exception: " + ex.getMessage());
//        } catch (FileNotFoundException ex) {
//            outputArea.setText("File not found: " + ex.getMessage());
//        } catch (Exception ex) {
//            outputArea.setText("Error: " + ex.getMessage());
//        }
//    }
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                new CacheSimulatorGUI();
//            }
//        });
//    }
//}
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.math.*;
import java.util.*;

public class CacheSimulatorGUI {

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Cache Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);

        // Layout
        BoxLayout boxLayout = new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS);
        frame.setLayout(boxLayout);

        // Inputs
        JTextField cacheSizeField = new JTextField(10);
        JTextField associativityField = new JTextField(10);
        JTextField replacementPolicyField = new JTextField(10);
        JTextField writeBackPolicyField = new JTextField(10);
        final JFileChooser fileChooser = new JFileChooser();

        // Button
        JButton fileButton = new JButton("Choose File");
        JButton submitButton = new JButton("Run Simulation");

        // Output area
        JTextArea outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);

        // Add components to frame
        frame.add(new JLabel("Cache Size:"));
        frame.add(cacheSizeField);
        frame.add(new JLabel("Associativity:"));
        frame.add(associativityField);
        frame.add(new JLabel("Replacement Policy:"));
        frame.add(replacementPolicyField);
        frame.add(new JLabel("Write Back Policy:"));
        frame.add(writeBackPolicyField);
        frame.add(fileButton);
        frame.add(submitButton);
        frame.add(new JScrollPane(outputArea));

        // File chooser action
        fileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileChooser.showOpenDialog(frame);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    // Set file path to a field or variable
                }
            }
        });

        // Submit button action
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    int cacheSize = Integer.parseInt(cacheSizeField.getText());
                    int associativity = Integer.parseInt(associativityField.getText());
                    int replacementPolicy = Integer.parseInt(replacementPolicyField.getText());
                    int writeBackPolicy = Integer.parseInt(writeBackPolicyField.getText());
                    File selectedFile = fileChooser.getSelectedFile();

                    if (selectedFile != null) {
                        String filePath = selectedFile.getAbsolutePath();
                        CacheSimulator simulator = new CacheSimulator();
                        String result = simulator.runSimulation(cacheSize, associativity, replacementPolicy, writeBackPolicy, filePath);
                        outputArea.setText(result);
                    } else {
                        outputArea.setText("No file selected");
                    }
                } catch (NumberFormatException ex) {
                    outputArea.setText("Error: Please enter valid numbers");
                } catch (Exception ex) {
                    outputArea.setText("Error: " + ex.getMessage());
                }
            }
        });

        // Display the frame
        frame.setVisible(true);
    }

    // CacheSimulator class (embedded within the GUI class)
   static class CacheSimulator {
        public static final int BLOCK_SIZE = 64;
        public static final int LRU = 0;
        public static final int WRITE_THROUGH = 0;
        public static final int WRITE_BACK = 1;

        private static int replacementPolicy;
        private static int writeBackPolicy;
        private static double numMisses = 0;
        private static double numReads = 0;
        private static double numWrites = 0;
        private static double totalRequests = 0;

        // Inner classes Block and Cache remain unchanged

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
                this.numSets = cacheSize / (BLOCK_SIZE * associativity);
                if(numSets<=0){
                    throw new IllegalArgumentException("Number of sets must be positive");
                }
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
                    if (blocks[setNumber][i].tag != null && blocks[setNumber][i].tag.equals(tag))
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

        // Constructor (if needed)
        public CacheSimulator() {
            // Initialization code (if any)
        }

        public String runSimulation(int cacheSize, int associativity, int replacementPolicy, int writeBackPolicy, String filePath) throws FileNotFoundException {
            this.replacementPolicy = replacementPolicy;
            this.writeBackPolicy = writeBackPolicy;
            Cache cache = new Cache(associativity, cacheSize);
            processInputFile(cache, filePath);

            String result = "Miss rate: " + String.format("%.6f\n", numMisses / totalRequests);
            result += "Total writes: " + String.format("%.6f\n", numWrites);
            result += "Total reads: " + String.format("%.6f\n", numReads);
            return result;
        }

        // Method processInputFile remains unchanged
        static void processInputFile(Cache cache, String file) throws FileNotFoundException {
            Scanner scanner = new Scanner(new File(file));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                char operation = line.charAt(0);
                BigInteger address = new BigInteger(line.substring(4), 16);
                BigInteger tag = address.divide(BigInteger.valueOf(BLOCK_SIZE));
                int setNumber = tag.mod(BigInteger.valueOf(cache.numSets)).intValue();
                totalRequests++;

                if (setNumber < 0 || setNumber >= cache.numSets) {
                    System.out.println("Error: Calculated setNumber " + setNumber + " is out of bounds.");
                    continue;
                }

                switch (operation) {
                    case 'R':
                        cache.read(tag, setNumber);
                        break;
                    case 'W':
                        cache.write(tag, setNumber);
                        break;
                    default:
                        // Handle invalid operations
                        System.out.println("Error: Invalid operation '" + operation + "' in input file.");
                        break;
                }
            }
            scanner.close();
    }
}}
