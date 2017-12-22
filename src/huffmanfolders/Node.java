
package huffmanfolders;

public class Node {
   private int value;
   private char character;
   private Node left=null;
   private Node right=null;

    public Node(int freq, char character, Node left, Node right) {
        this.value = freq;
        this.character = character;
        this.left = left;
        this.right = right;
    }
    public Node(){
        
    }
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

   
    public char getCharacter() {
        return character;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void setRight(Node right) {
        this.right = right;
    }
    public Node getLeft() {
        return left;
    }

    public Node getRight() {
        return right;
    }
   
   
}

