import util.HMM;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Scanner sc = new Scanner(System.in);
        HMM hmm = new HMM();
        String input;
        System.out.println("\n\n\n\n\n");
        do {
            System.out.print("\nEnter Command (h - list of commands): ");
            input = sc.nextLine();
            switch (input) {
                case "ti":
                    computeSentenceProbability_Input(sc, hmm);
                    break;
                case "tf":
                    assignPosTagsToSentence_File(sc, hmm);
                    break;
                case "q":
                    System.exit(0);
                    break;
                case "h":
                    printHelp();
                    break;
            }
        } while(true);
    }

    private static void assignPosTagsToSentence_File(Scanner sc, HMM HMM) throws InterruptedException {
        boolean repeat;
        do {
            repeat = false;
            System.out.print("\nEnter File Name (default ./sentence.txt): ");
            String fileName = sc.nextLine();
            if (fileName.isEmpty()) {
                fileName = "./sentence.txt";
            }

            try (BufferedReader brTest = new BufferedReader(new FileReader(fileName))) {
                String sentence = brTest.readLine();
                System.out.println("\nSentence Got:\n" + sentence);

                printAssignPosTags(sentence, HMM);
            } catch (FileNotFoundException e) {
                System.err.println("\nFile Not Found");
                Thread.sleep(500);
                repeat = true;
            } catch (IOException e) {
                System.err.println("\nIOException (aborting back to main menu)");
                Thread.sleep(500);
            }
        } while(repeat);
    }

    private static void computeSentenceProbability_Input(Scanner sc, HMM HMM) throws InterruptedException {
        String defaultSentence = "back the bill Janet will";
        System.out.print("\nEnter Test Sentence (default `" + defaultSentence + "`): ");
        String sentence = sc.nextLine();
        if (sentence.isEmpty()) {
            sentence = defaultSentence;
        }
        System.out.println("\nSentence Got:\n" + sentence);

        printAssignPosTags(sentence, HMM);
    }

    private static void printAssignPosTags(String sentence, HMM HMM) throws InterruptedException {
        try {
            String[] tags = HMM.getPosTags(sentence);
            System.out.println("\nAssigned Tags: " + Arrays.toString(tags));
        } catch (Exception e) {
            System.err.println(e.toString());
            Thread.sleep(500);
        }
    }

    private static void printHelp() {
        System.out.println(
                "  ti - input sentence to decode (<s> added automatically)\n" +
                "  tf - input file to decode sentence of first line of file\n" +
                "  h - help menu\n" +
                "  q - quit");
    }
}
