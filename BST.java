/**
 * Created by sondreberdal on 02.09.16.
 * Oblig1, INF2220
 */
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class BST {
    private static class Node implements Comparable<Node> {
        private String word; // Word
        Node left; // Left child
        Node right; // Right child

        Node(String s){
            this.word = s;
            left = null;
            right = null;
        }

        public int compareTo(Node n){
            return this.getString().compareTo(n.getString());
        }
        public String getString(){
            return this.word;
        }
    } // Node

    public static Node root;

    public BST() {
            this.root = null;
    }
    private void makeEmpty() {
        root = null;
    }
    private boolean isEmpty() {
        return root == null;
    }

    private boolean contains(String s, Node n) {
        if (n == null) {
            return false;
        }

        int compareRes = s.compareTo(n.getString());

        if (compareRes < 0) {
            return contains(s, n.left);
        } else if (compareRes > 0) {
            return contains(s, n.right);
        } else {
            return true; // Match
        }
    }

    private Node findMin(Node n){
        if (n == null){
            return null;
        } else if (n.left == null){
            return n;
        }
        return findMin(n.left);
    }

    private Node findMax(Node n){
        if( n == null){
            return null;
        } else if(n.right == null){
            return n;
        }
        return findMax(n.right);
    }

    private Node insert(String s, Node n) {
        if (n == null) {
            //    System.out.println(s);
            return new Node(s);
        }

        int compareRes = s.compareTo(n.getString());

        if (compareRes < 0) {
            n.left = insert(s, n.left);
            //    System.out.println(s);
        } else if (compareRes > 0) {
            n.right = insert(s, n.right);
         //   System.out.println(s);
        } else {
            return n; //duplicate do nothing
        }
        return n;
    } // Insert

    private Node remove(String s, Node n) {
        if (n == null) {
            return null; //Item not found
        }
        int compareRes = s.compareTo(n.getString());
        if (compareRes < 0){
            n.left = remove(s, n.left);
        } else if(compareRes > 0){
            n.right = remove(s, n.right);
        } else if(n.left != null && n.right != null){ // 2x children
            n.word = findMin(n.right).getString();
            n.right = remove(n.getString(), n.right);
        } else {
            n = (n.left != null ) ? n.left : n.right;
        }return n;
    } // Remove

    private void readFile(String file){
        String location = BST.class.getProtectionDomain().getCodeSource().getLocation().toString();
        location = location.replace("file:", "");
        String line;
        try {
            BufferedReader bf = new BufferedReader(new FileReader(location + file));
            while((line = bf.readLine()) != null) {
                if(root == null) {
                    root = insert(line, root);
                } else {
                    insert(line, root);
                }

            }
        }catch(IOException e) {
            e.printStackTrace();
            System.out.println("Could not find the input file. Try something else");
            quit();
        }
    }

    private static void display(Node n){ //Prints the tree sorted
        if(n != null){
            display(n.left);
            System.out.println(n.getString());
            display(n.right);
        }
    }

    private static int countNodes(Node n) {
        if (n == null) {
            return 0;
        }
        return 1 + countNodes(n.left) + countNodes(n.right);
    }

    public ArrayList<String> similarWord(String s){

        char[] wordArray = s.toCharArray();
        char[] wordArray3 = new char[s.length()+1]; // wordArray for similar words check3
        wordArray3[0] = 'a';
        System.arraycopy(s.toCharArray(),0,wordArray3,1,s.length());


        char[] tmp1; // first similar words check ... Swap
        char[] tmp2; // second similar words check ... Switch one character
        char[] tmp3; // ... add one character

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        String[] words1 = new String[wordArray.length]; // Same sequence as tmp ...
        String[] words2 = new String[wordArray.length*alphabet.length];
        String[] words3 = new String[(wordArray3.length)*alphabet.length];
        String[] words4 = new String[(s.length())]; // remove char


        ArrayList<String> similarWords = new ArrayList<String>(); // result container

        int total = 0;
        int count = 0;
        for(int i = 0; i < wordArray.length; i++) {
            if(i != wordArray.length-1) {
                tmp1 = wordArray.clone();
                words1[i] = swap(i, i + 1, tmp1);
                if(contains(words1[i], root)){
                    similarWords.add(count, words1[i]);
                    count++;
                }
            }
            if(i != 0){
                wordArray3 = swap(i-1, i, wordArray3).toCharArray();
            }

            String tmp4; // remove one character
            words4[i] = tmp4 = s.substring(0,i) + s.substring(i+1);

            if(contains(words4[i], root)){
                similarWords.add(count, words4[i]);
                count++;
            }
            for (int j = 0; j < alphabet.length; j++){
                tmp2 = wordArray.clone();
                tmp3 = wordArray3.clone();

                tmp2[i] = alphabet[j];
                tmp3[i] = alphabet[j];

                String tmp_str2 = new String(tmp2);
                words2[total] = tmp_str2;
                String tmp_str3 = new String(tmp3);
                words3[total] = tmp_str3;

                if(contains(words2[total], root)){
                    similarWords.add(count, words2[total]);
                    count++;
                }
                if(contains(words3[total], root)){
                    similarWords.add(count, words3[total]);
                    count++;
                }
                total++;
            }
        }

        /**
         * Need to loop once more for the last character in add one char check
         */

        wordArray3 = swap(wordArray.length-1, wordArray.length, wordArray3).toCharArray();
        for (int j = 0; j < alphabet.length; j++){
            tmp3 = wordArray3.clone();
            tmp3[wordArray.length] = alphabet[j];
            String tmp_str = new String(tmp3);
            words3[total] = tmp_str;
            if(contains(words3[total], root)){
                similarWords.add(count, words3[total]);
                count++;
            }
            total++;
        }
        Collections.sort(similarWords);
        return similarWords;
    }

    public String swap(int a, int b, char[] word){
        char tmp = word[a];
        word[a] = word[b];
        word[b] = tmp;
        return new String(word);
    }

    public int findDepth(Node n){
        if(n == null){
            return 0;
        }

        int l = findDepth(n.left);
        int r = findDepth(n.right);

        int res = l > r ? l+1 : r+1; // if l is bigger l+1, else r+1 - return to parent
        return res;
    }



    public void printStats(){

        System.out.println("*** TREE STATISTICS ***");
        // print depth

        System.out.println("The depth of the tree was: "+ findDepth(root));

        // print how many nodes for c depth of the tree
        System.out.println("There were "+countNodes(root)/findDepth(root)+" nodes for each depth of tree" );


        // print the average depth of all the nodes
        System.out.println("The average depth for all the nodes were: "+sumDepthOfAllChildren(root, 0)/countNodes(root));

        // last and first alphabetically:

        System.out.println("The alphabetically first and last words of the dictionary were: "
                + findMin(root).word + " and " + findMax(root).word);

    }

    public int sumDepthOfAllChildren(Node node, int depth)
    {
        if ( node == null )
            return 0;  // starting to see a pattern?
        return depth + sumDepthOfAllChildren(node.left, depth + 1) +
                sumDepthOfAllChildren(node.right, depth + 1);
    }

    public static void main(String[] args){

        System.out.println("*** Booting up simple dictionary ***");
        BST bst = new BST();

        if(args.length < 1) {
            System.out.println("No input file provided");
            System.out.println("*** do you want to continue with the default dictionary.txt file? ***");
            System.out.println("Enter Y/N:");

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            try {
                boolean run = true;
                while(run){
                    String input = br.readLine();
                    switch(input) {

                        case "Y":
                        case "y":
                        case "yes":
                        case "Yes":
                        case "ok":
                        case "OK":
                            run = false;
                            break;
                        case "N":
                        case "n":
                        case "No":
                        case "no":
                            quit();
                        default:
                            System.out.println("*** Invalid input, please enter a valid input: Y/N ***");
                            System.out.print("Enter Y/N: ");
                            break;
                    }
                }
            }catch(IOException e){ e.printStackTrace();}
            bst.readFile("dictionary.txt");
            bst.remove("busybody",root);
            bst.insert("busybody",root);
        } else {
            bst.readFile(args[0]);

        } // read file

        printMenu();
        try {
            boolean run = true;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            while(run) {
                System.out.print("Input: ");
                String input = br.readLine();
                switch(input){
                    case("q"): run = false; break;
                    case("help"):case("h"): printMenu(); break;
                    case("insert"):
                        System.out.println("Enter word you want to insert:");
                        input = br.readLine().toLowerCase();
                        bst.insert(input, root);
                        break;
                    case("search"):case("s"):
                        System.out.println("Enter search word:");
                        input = br.readLine().toLowerCase();
                        if(!bst.contains(input, root)){
                            System.out.println("Could not find word in the dictionary ...");
                            System.out.println("Looking for similar suggestions ...");
                            ArrayList<String> resultList = new ArrayList<String>();
                            long startTime = System.currentTimeMillis();
                            resultList = bst.similarWord(input);
                            long timeElapsed = (System.currentTimeMillis() - startTime);
                            if(resultList.size() > 0) {
                                System.out.println("Found suggestion(s):");
                                for (int i = 0; i < resultList.size(); i++) {
                                    System.out.println(resultList.get(i));
                                }
                                System.out.println("Lookups that gave a positive answer: "+resultList.size());
                                System.out.println("Time used to generate and look for similar words: "+
                                        timeElapsed+" milliseconds");
                            } else{
                                System.out.println("Couldn't find any suggestions in the dictionary :( ...");
                                System.out.println("Time used to generate and look for similar words: "+
                                        timeElapsed+" milliseconds");
                            }
                        }
                        break;
                    case("delete"):
                        System.out.println("Enter word you want to delete:");
                        input = br.readLine().toLowerCase();

                        bst.remove(input, root);
                        break;
                    default:
                        System.out.println("*** Invalid input, note that the program is case sensitive ***");
                        break;
                }
            }
        } catch(IOException e){e.printStackTrace();}

        bst.printStats();
        quit();
    } // Main

    static void quit(){
        System.out.println("*** Exiting simple dictionary, good bye! :) ***");
        System.exit(0);
    }

    static void printMenu(){
        System.out.println("| ----------MENU---------- |");
        System.out.println("|*** Available commands ***|");
        System.out.println("| help/h: This menu        |");
        System.out.println("| insert: Insert to tree   |");
        System.out.println("| search: Search in tree   |");
        System.out.println("| delete: Delete from tree |");
        System.out.println("| quit/q: Exit dictionary  |");
        System.out.println("| ------------------------ |");


    }
} //BST

