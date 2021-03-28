package antonp.uni.mmn14_algo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.BitSet;
import java.util.Scanner;
import static antonp.uni.mmn14_algo.MurmurHash.hash32;

/**
 *
 * @author Anton Pluzharov
 * This class contains solution for exercise 14 in course 20407.
 * 
 * Program will add words from input file provided as argument to custom hashtable.
 * Program can check if words from input file were added to hashtable.
 * The method of hashtable was implemented according to assignment requests.
 * 
 * @date 15.1.2020
 */
public class NewMain {
//Constants given in the assignment
    final static int MAX_NUMBER_OF_BITS=(int) (32*Math.pow(10, 6));
    final static int MAX_NUMBER_OF_HASH_FUNCTIONS=13;
    
    //Variables to use across the program
    static int m=-1; //number of bits in hashTable  
    static int k=-1;//number of hash functions
    static int choise=-1; // choise of user in last question
    static int argnum=-1; //choise of argument index to check or add values from
    static BitSet hashTable ;//hashTable
    
    /**
     * Function will map 32 bits output from Murmurhash function to fit in given BitSet
     * Some values were returned as negative, that's why "and" with 0xFF (method of conversion to unsigned)
     * @param word String to generate hash from
     * @return Index of bit to turn on in BitSet
     */
    public static int generateHash(String word, int iteration) {
        final byte[] bytes = word.getBytes();
        return (hash32(bytes,bytes.length,iteration)&0xFF)%m;
    }
    
    /**
     * Function will enable bits according to generated hash for provided word.
     * @param word String to generate hash from
     */
    public static void addToHashTable(String word) {
        System.out.println("Adding \""+word+"\", enabling bits: ");
        for(int i=0; i<k;i++) {//Run hash with k different seeds to generate k different hash functions
            int temp = generateHash(word, i);
            String print = (i==k-1)? temp+"." : temp+", ";
            System.out.print(print);
            hashTable.set(temp);
        }
        System.out.println("\n");
    }
    
    /**
     * Function determinates if word was "inserted into" hash table.
     * @param word String to check if was inserted into hashTable.
     * @return True if string was inserted into hash table and false otherwise.
     */
    public static boolean isValueInTable(String word) {
        boolean flag = true;
        //System.out.println("Checking persistance of word \'"+word+"\'.");
        for(int i=0; i<k;i++) {//Run hash with k different seeds to generate k different hash functions and check if the bits enabled
            int temp = generateHash(word, i);
            //String print = hashTable.get(temp) ? temp+" on" : temp+" off";
           // print += (i<k) ? ", ":".";
            if (!hashTable.get(temp)) flag = false;
           // System.out.println(print);
        }
        return flag;
    }
    
    /**
     * Function will get K and M input from user
     * @param read scanner to use
     */
    public static void getBasicParameters(Scanner read) {
        System.out.println("Please enter desired number of hash functions (K): ");
        read = new Scanner(System.in);
        k = read.nextInt();
        while (k<1 || k>MAX_NUMBER_OF_HASH_FUNCTIONS) {
            System.err.println("Please enter number between "+MAX_NUMBER_OF_HASH_FUNCTIONS+" and 1: ");
            k = read.nextInt();
        }
        System.out.println("Please enter desired number of members in hash table: ");
        m = read.nextInt();
        while (m>MAX_NUMBER_OF_BITS || m<1) {
            System.err.println("Please enter value between "+MAX_NUMBER_OF_BITS+" and 1:");
            m = read.nextInt();
        }
    }
    
    /**
     * Function will assign value to chosen argument number from input.
     * 
     * @param read Scanner to read from.
     * @param args arguments of current program execution.
     * @return 1 in case of error, 0 otherwise.
     */
    public static int getArgNum(Scanner read, String[] args) {
        if (args.length == 0) {
            System.out.println("Please run the program with parameter containing full path to the text file input.");
            return 1;
        }
        for(int i=0; i<args.length;i++) {
            System.out.println("Argument "+i+": \'"+args[i]+"\'");
        }
        System.out.println("Please enter number of argument with full path to the file: ");
        argnum = read.nextInt();
        while (argnum < 0 || argnum>=args.length) {
            System.err.println("Invalid index "+argnum+", should be between 0 and "+args.length+".");
            System.out.println("Please enter number of argument with full path to the file: ");
            argnum = read.nextInt();
        }
        return 0;
    }
    
    public static void main(String[] args) {
        //initialize objects
        Scanner textFileScanner = null, read = new Scanner(System.in);
        File textFile = null;
        //get K and M
        getBasicParameters(read);
        hashTable = new BitSet(m);
        while(true) { //Main loop
            //Get choise of next operation
            System.out.println("Add values (0), check their belonging to the structure(1) or exit (2)? ");
            choise = read.nextInt();
                if(choise >=0 && choise <2) {
                    if (getArgNum(read,args) == 1) return; // Get argument number 
                                                           //from user and return in case of failure
                    textFile = new File(args[argnum]);
                    try {
                        textFileScanner = new Scanner(textFile);
                        textFileScanner.useDelimiter(",");
                    } catch (FileNotFoundException e) {
                        System.err.println(e.getMessage());
                        return;
                    }
                    if( choise == 0) {// case need to add values to hash table
                        while (textFileScanner.hasNext()) {
                            addToHashTable(textFileScanner.next()); //add all words from file to data structure
                        }
                        textFileScanner.close();
                    } else if (choise == 1) {//case need to check values
                        while (textFileScanner.hasNext()) {
                            String nextWord = textFileScanner.next();
                            String printText = "Word \'"+nextWord+"\' ";
                            printText += (isValueInTable(nextWord))? "was":"wasn't";
                            System.out.println(printText+" found in table.");
                        }
                        textFileScanner.close();
                    }
                } else if (choise == 2) { //exit
                    return;
                } else {
                    System.err.println("Wrong choise, please enter number between 0 and 2 terminating with \\n."); 
                }
        }
    }
    
}
