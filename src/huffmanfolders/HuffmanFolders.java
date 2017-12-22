
package huffmanfolders;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class HuffmanFolders {

    ArrayList<String> filesNames = new ArrayList<>();
    ArrayList<Integer> codeSizes = new ArrayList<>();
    ArrayList<String> filesNames2 = new ArrayList<>();
    ArrayList<Integer> codeSizes2 = new ArrayList<>();
    public HashMap<Character, Integer> map = new HashMap<>();
    public HashMap<Character, String> codesMap = new HashMap<>();
    public HashMap<String, Character> codesMap2 = new HashMap<>();
    public PriorityQueue<Node> queue = new PriorityQueue<>(new Comparator<Node>() {
        public int compare(Node node1, Node node2) {
            if (node1.getValue() < node2.getValue()) {
                return -1;
            }
            if (node1.getValue() > node2.getValue()) {
                return 1;
            }
            return 0;
        }
    });

    public void getFilesNames() {
        File folder = new File("salma");
        File[] listOfFiles = folder.listFiles();

        for (File file : listOfFiles) {
            if (file.isFile()) {
                filesNames.add(file.getName());
            }
        }
    }

    public void read(String name) {

        String fileName = name;
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            int ch;
            char c;
            int freq = 1;
            while ((ch = bufferedReader.read()) != -1) {
                c = (char) ch;
                if (map.containsKey(c)) {
                    freq = map.get(c) + 1;
                    map.put(c, freq);
                } else {
                    map.put(c, 1);
                }
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void readFolder() {
        for (String s : filesNames) {
            read("salma/" + s);
        }
    }

    public void getCodeSizes() {
        for (String s : filesNames) {
            int size = 0;
            try {
                FileReader fileReader = new FileReader("salma/" + s);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                int ch;
                char c;
                while ((ch = bufferedReader.read()) != -1) {
                    c = (char) ch;
                    size += codesMap.get(c).length();
                }
                codeSizes.add(size);
            } catch (FileNotFoundException ex) {
                System.out.println("Unable to open file '" + "salma/" + s + "'");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void insertToHeap() {
        for (char key : map.keySet()) {
            Node node = new Node();
            node.setValue(map.get(key));
            node.setCharacter(key);
            queue.add(node);
        }

    }

    public Node buildHuffmanTree() {
        while (queue.size() != 1) {
            Node left = queue.poll();
            Node right = queue.poll();
            Node n = new Node(left.getValue() + right.getValue(), '$', left, right);
            queue.add(n);
        }
        return queue.poll();
    }

    public void getHuffmanCodes(Node root, String code) {
        if (root == null) {
            return;
        }

        if (root.getLeft() == null && root.getRight() == null) {
            codesMap.put(root.getCharacter(), code);
        }

        getHuffmanCodes(root.getLeft(), code + "0");
        getHuffmanCodes(root.getRight(), code + "1");
    }

    public void compress() {
        String outputFile = "compressed";
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(outputFile);
            //header
            byte[] noOfFiles = ByteBuffer.allocate(4).putInt(filesNames.size()).array();
            stream.write(noOfFiles);
            for (int i = 0; i < filesNames.size(); i++) {
                byte[] sizeOfName = ByteBuffer.allocate(4).putInt(filesNames.get(i).length()).array();
                stream.write(sizeOfName);
                byte[] fileName = filesNames.get(i).getBytes();
                stream.write(fileName);
                byte[] sizeOfCode = ByteBuffer.allocate(4).putInt(codeSizes.get(i)).array();
                stream.write(sizeOfCode);
            }
            byte[] mapSizeBytes = ByteBuffer.allocate(4).putInt(codesMap.size()).array();
            stream.write(mapSizeBytes);
            for (char key : codesMap.keySet()) {
                String character = new String();
                character += key;
                byte[] charBytes = character.getBytes();
                stream.write(charBytes);
                byte[] sizeBytes = ByteBuffer.allocate(4).putInt(codesMap.get(key).length()).array();
                stream.write(sizeBytes);
                byte[] codeBytes = codesMap.get(key).getBytes();
                stream.write(codeBytes);
            }

            for (String s : filesNames) {

                try {
                    FileReader fileReader = new FileReader("salma/" + s);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    int ch;
                    char c;
                    String code = new String();
                    while ((ch = bufferedReader.read()) != -1) {
                        c = (char) ch;
                        code += codesMap.get(c);
                        if (code.length() % 8 == 0 && code.length() != 0) {
                            int length = code.length();
                            byte[] bytes = new byte[(length + Byte.SIZE - 1) / Byte.SIZE];
                            char character;
                            for (int i = 0; i < length; i++) {
                                if ((character = code.charAt(i)) == '1') {
                                    bytes[i / Byte.SIZE] = (byte) (bytes[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
                                }
                            }
                            stream.write(bytes);
                            code = "";
                        }
                    }
                    if (code.length() != 0) {
                        int length = code.length();
                        byte[] bytes = new byte[(length + Byte.SIZE - 1) / Byte.SIZE];
                        char character;
                        for (int i = 0; i < length; i++) {
                            if ((character = code.charAt(i)) == '1') {
                                bytes[i / Byte.SIZE] = (byte) (bytes[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
                            }
                        }
                        stream.write(bytes);

                    }

                } catch (FileNotFoundException ex) {
                    System.out.println("Unable to open file '" + "salma/" + s + "'");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            stream.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + outputFile + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void decompress() {
        String inputFile = "compressed";
        File file = new File(inputFile);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(inputFile);
            byte fileContent[] = new byte[(int) file.length()];
            stream.read(fileContent);
            stream.close();
            int i, index, numberOfFiles, sizeOfMap;
            char c;
            String s = new String();
            for (i = 0; i < 4; i++) {
                s += String.format("%02x", fileContent[i]);
            }
            numberOfFiles = Integer.parseInt(s, 16);
            s = "";
            index = i;
            for (int j = 0; j < numberOfFiles; j++) {
                String name = new String();
                int sizeOfName = 0, sizeOfCode;
                for (i = index; i < index + 4; i++) {
                    s += String.format("%02x", fileContent[i]);
                }
                sizeOfName = Integer.parseInt(s, 16);
                s = "";
                index = i;
                for (i = index; i < index + sizeOfName; i++) {
                    name += (char) fileContent[i];
                }
                filesNames2.add(name);
                s = "";
                index = i;
                for (i = index; i < index + 4; i++) {
                    s += String.format("%02x", fileContent[i]);
                }
                sizeOfCode = Integer.parseInt(s, 16);
                codeSizes2.add(sizeOfCode);
                s = "";
                index = i;
            }
            for (i = index; i < index + 4; i++) {
                s += String.format("%02x", fileContent[i]);
            }
            sizeOfMap = Integer.parseInt(s, 16);
            s = "";
            index = i;
            for (int k = 0; k < sizeOfMap; k++) {
                int size = 0;
                c = (char) fileContent[index];
                index++;
                for (i = index; i < index + 4; i++) {
                    s += String.format("%02x", fileContent[i]);
                }
                size = Integer.parseInt(s, 16);
                s = "";
                index = i;
                String code = new String();
                for (i = index; i < index + size; i++) {
                    code += (char) fileContent[i];
                }
                codesMap2.put(code, c);
                index = i;
            }
            File folder = new File("decompressed");
            try {
                folder.mkdir();
            } catch (SecurityException se) {
            }
            for (int m = 0; m < filesNames2.size(); m++) {
                BufferedWriter bw = null;
                FileWriter fw = null;
                try {
                    fw = new FileWriter(folder.getName() + "/" + filesNames2.get(m));
                    bw = new BufferedWriter(fw);
                    String decode = new String();
                    int place = 0, taken = 0;
                    int length = (codeSizes2.get(m) / 8) + 1;
                    for (i = index; i < index + length; i++) {
                        s += String.format("%8s", Integer.toBinaryString(fileContent[i] & 0xFF)).replace(' ', '0');
                        for (int z = 0; z < s.length(); z++) {
                            decode += s.charAt(z);
                            if (codesMap2.containsKey(decode)) {
                                taken += decode.length();
                                if (taken <= codeSizes2.get(m)) {
                                    bw.write(codesMap2.get(decode));
                                    decode = "";
                                    place = z;
                                }
                            }
                        }
                        if (place != 0) {
                            s = s.substring(place + 1, s.length());
                            place = 0;
                        }
                        decode = "";
                    }
                    index = i;
                    bw.close();
                } catch (FileNotFoundException ex) {
                    System.out.println("Unable to open file '" + inputFile + "'");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                s = "";
            }
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + inputFile + "'");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void execute() {
        getFilesNames();
        readFolder();
        insertToHeap();
        Node root = buildHuffmanTree();
        getHuffmanCodes(root, "");
        getCodeSizes();
        compress();
        decompress();
    }

}
