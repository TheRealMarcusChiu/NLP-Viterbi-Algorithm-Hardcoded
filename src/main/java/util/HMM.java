package util;

import java.util.ArrayList;
import java.util.Arrays;

public class HMM {

    public static final String SOS_MARKER = "<s>";

    ArrayList<String> tokenWordSet = new ArrayList<>(Arrays.asList(SOS_MARKER, "Janet", "will", "back", "the", "bill"));
    ArrayList<String> tokenPosSet = new ArrayList<>(Arrays.asList(SOS_MARKER, "NNP", "MD", "VB", "JJ", "NN", "RB", "DT")); // new ArrayList<>(Arrays.asList("CC", "CD", "DT", "EX", "FW", "IN", "JJ", "JJR", "JJS", "LS", "MD", "NN", "NNS", "NNP", "NNPS", "PDT", "POS", "PRP", "PRP$", "RB", "RBR", "RBS", "RP", "SYM", "TO", "UH", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ", "WDT", "WP", "WP$", "WRB", "$", "#", "“", "”", "(", ")", ",", ".", ":"));

    public double[][] transitionProbabilities = {
            {0.0, 0.2767, 0.0006, 0.0031, 0.0453, 0.0449, 0.0510, 0.2026},
            {0.0, 0.3777, 0.0110, 0.0009, 0.0084, 0.0584, 0.0090, 0.0025},
            {0.0, 0.0008, 0.0002, 0.7968, 0.0005, 0.0008, 0.1698, 0.0041},
            {0.0, 0.0322, 0.0005, 0.0050, 0.0837, 0.0615, 0.0514, 0.2231},
            {0.0, 0.0366, 0.0004, 0.0001, 0.0733, 0.4509, 0.0036, 0.0036},
            {0.0, 0.0096, 0.0176, 0.0014, 0.0086, 0.1216, 0.0177, 0.0068},
            {0.0, 0.0068, 0.0102, 0.1011, 0.1012, 0.0120, 0.0728, 0.0479},
            {0.0, 0.1147, 0.0021, 0.0002, 0.2157, 0.4744, 0.0102, 0.0017}
    }; // Pos * PoS
    public double[][] emissionProbabilities = {
            {1.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000},
            {0.000000, 0.000032, 0.000000, 0.000000, 0.000048, 0.000000},
            {0.000000, 0.000000, 0.308431, 0.000000, 0.000000, 0.000000},
            {0.000000, 0.000000, 0.000028, 0.000672, 0.000000, 0.000028},
            {0.000000, 0.000000, 0.000000, 0.000340, 0.000000, 0.000000},
            {0.000000, 0.000000, 0.000200, 0.000223, 0.000000, 0.002337},
            {0.000000, 0.000000, 0.000000, 0.010446, 0.000000, 0.000000},
            {0.000000, 0.000000, 0.000000, 0.000000, 0.506099, 0.000000}
    };   // Pos * Word

    public HMM() {
    }

    // HMM's Viterbi Algorithm
    public String[] getPosTags(String sentence) throws Exception {
        sentence = SOS_MARKER + " " +  sentence.trim();
        String[] words = sentence.split("\\s+");

        // COMPUTE TRELLIS MATRIX & BACKTRACE
        double[][] trellisMatrixProbabilities = new double[words.length][tokenPosSet.size()];
        int[][] trellisMatrixBackTrace = new int[words.length][tokenPosSet.size()];
        for (int i = 0; i < words.length; i++) {
            trellisMatrixProbabilities[i] = new double[tokenPosSet.size()];
            trellisMatrixBackTrace[i] = new int[tokenPosSet.size()];
            // INITIALIZE BACKTRACE
            for (int j = 0; j < tokenPosSet.size(); j++) {
                trellisMatrixBackTrace[i][j] = -1;
            }
        }
        // INITIALIZE TRELLIS MATRIX
        trellisMatrixProbabilities[0][0] = 1.0; // initial pos probability P(<s>) = 1

        for (int i = 1; i < words.length; i++) {
            int wordIndex = tokenWordSet.indexOf(words[i]);
            for (int j = 0; j < tokenPosSet.size(); j++) {
                double maxProb = 0.0;
                int previousPosIndex = -1;
                for (int k = 0; k < tokenPosSet.size(); k++) {
                    double pp = trellisMatrixProbabilities[i-1][k];
                    double tp = transitionProbabilities[k][j];
                    double ep = emissionProbabilities[j][wordIndex];
                    double prob = tp * ep * pp;
                    if (prob > maxProb) {
                        maxProb = prob;
                        previousPosIndex = k;
                    }
                }
                trellisMatrixProbabilities[i][j] = maxProb;
                trellisMatrixBackTrace[i][j] = previousPosIndex;
            }
        }


        // DECODE TRELLIS MATRIX
        int[] tagIndices = new int[words.length]; // remove the <s> and </s>

        double maxProb = 0.0;
        int posIndex = -1;
        for (int i = 0; i < tokenPosSet.size(); i++) {
            if (trellisMatrixProbabilities[words.length - 1][i] > maxProb) {
                maxProb = trellisMatrixProbabilities[words.length - 1][i];
                posIndex = i;
            }
        }
        if (posIndex == -1) {
            throw new Exception("zero probability");
        }

        tagIndices[words.length - 1] = trellisMatrixBackTrace[words.length - 1][posIndex];

        for (int i = words.length - 1; i >= 1; i--) {
            if (tagIndices[i] == -1) {
                throw new Exception("zero probability");
            }
            tagIndices[i-1] = trellisMatrixBackTrace[i-1][tagIndices[i]];
        }

        String[] tags = new String[words.length - 2]; // remove the <s> and </s>
        for (int i = 2; i < words.length; i++) {
            tags[i-2] = tokenPosSet.get(tagIndices[i]);
        }

        return tags;
    }
}
