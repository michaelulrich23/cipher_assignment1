package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        long startTime = System.nanoTime();

        final int minLength = 5;

        String dictionary = "dictionary_5000.txt";
        String input = "input.txt";

        String realInput = readDictionaryFile(input, 1, "").toString();
        realInput = realInput.substring(1, realInput.length() - 1); //-[]
        Set<String> words = readDictionaryFile(dictionary, minLength, realInput);

        Set<Set<String>> result = bruteForce(realInput, words);

        result = filterResults(result, realInput);

        System.out.println(result.size());
        for(Set<String> res : result){
            TreeSet<String> tmp = new TreeSet<>(res);
            System.out.println(tmp);
        }

        long endTime = System.nanoTime();
        System.out.println((endTime - startTime)/1000000000.0 + "s");
    }

    private static Set<Set<String>> filterResults(Set<Set<String>> result, String input) {
        Set<Set<String>> newResult = new HashSet<>();

        for (Set<String> innerList : result) {
            if(innerList.size() < 2) continue;

            int combinedLength = 0;
            StringBuilder containsAllLettersCheckTwo = new StringBuilder();

            for (String str : innerList) {
                combinedLength += str.length();
                containsAllLettersCheckTwo.append(str);
            }

            if(!containsAllLetters(input, containsAllLettersCheckTwo.toString())){
                continue;
            }

            if (combinedLength == input.length()) {
                newResult.add(innerList);
            }
        }

        return newResult;
    }

    private static Set<Set<String>> bruteForce(String input, Set<String> words) {
        if(input.isEmpty())
            return new HashSet<>();

        Set<Set<String>> result = new HashSet<>();

        for(String word : words){
            if(containsAllLetters(input, word)){

                String alteredInput = removeWordLetters(input, word);
                Set<Set<String>> partialResult = bruteForce(alteredInput, words);

                if(partialResult.isEmpty()) {
                    partialResult.add(new HashSet<>(List.of(word)));
                }
                else {
                    for(Set<String> combination : partialResult){
                        combination.add(word);
                    }
                }

                result.addAll(partialResult);
            }
        }

        return result;
    }

    private static String removeWordLetters(String input, String word) {
        StringBuilder result = new StringBuilder();
        result.append(input);
        ArrayList<Character> wordLetters = new ArrayList<>();

        for (char c : word.toCharArray()) {
            wordLetters.add(c);
        }

        for (int i = 0; i < word.length(); i++) {
            char c = wordLetters.get(i);
            result.deleteCharAt(result.indexOf(String.valueOf(c)));
        }

        return result.toString();
    }

    private static Set<String> readDictionaryFile(String dictionary, int minLength, String input) throws IOException {
        Set<String> words = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(dictionary))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if(line.length() >= minLength && containsAllLetters(input, line)) {
                    words.add(line);
                }
            }
        }


        return words;
    }

    private static boolean containsAllLetters(String input, String word) {
        if(input.isEmpty()) return true;
        if (word.length() > input.length()) {
            return false;
        }

        Map<Character, Integer> inputFreqMap = new HashMap<>();

        for (char c : input.toCharArray()) {
            inputFreqMap.put(c, inputFreqMap.getOrDefault(c, 0) + 1);
        }

        for (char c : word.toCharArray()) {
            if (!inputFreqMap.containsKey(c) || inputFreqMap.get(c) == 0) {
                return false;
            }
            inputFreqMap.put(c, inputFreqMap.get(c) - 1);
        }

        return true;
    }

}