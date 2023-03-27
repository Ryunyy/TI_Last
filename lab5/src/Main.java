import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//d - кодовое расстояние = 2t + 1, t - кол-во исправляемых ошибок.

public class Main {
    public static void main(String[] args) throws IOException {
        ArrayList<int[]> GMatrix;
        int[] basis = {0, 1, 2};
        ArrayList<String> codes;
        int wordsNum, n, d;
        String path = "D:\\Study\\4Rehc\\TI\\lab5\\TI_Last\\lab5\\code.txt";

        GMatrix = fileReader(path);
        //matrixRevork(GMatrix, basis);

        n = GMatrix.size();
        wordsNum = (int)Math.pow(2, n);

        System.out.println("Code dimension: " + n);
        System.out.println("Maximal possible code words: " + wordsNum);
        codes = codeWordGenerator(GMatrix, n);
        d = minimalDistance(codes);
        System.out.println("Minimal code distance: " + d);
        System.out.println("\nGenerative matrix:");
        matrixOutput(GMatrix);
    }

    public static int minimalDistance(ArrayList<String> codes){
        int toReturn = Integer.MAX_VALUE, dist;
        for(int i = 0; i < codes.size()-1; i++)
            for(int j = i+1; j < codes.size(); j++){
                dist = distance(codes.get(i), codes.get(j));
                //System.out.println(codes.get(i) + " | " + codes.get(j) + " -> " + dist);
                toReturn = dist < toReturn? dist: toReturn;
            }
        return toReturn;
    }

    private static int distance(String first, String second){
        int toReturn = 0;
        for(int i = 0; i < first.length(); i++){
            if(first.charAt(i) != second.charAt(i))
                toReturn++;
        }
        return toReturn;
    }

    public static ArrayList<String> codeWordGenerator(ArrayList<int[]>matrix, int n){
        ArrayList<String> codeWords = new ArrayList<>(), codes;
        codes = codeGenerator(n);
        int wordNum = (int)Math.pow(2, n);

        for(int i = 0; i < wordNum; i++) {
            codeWords.add(codeMatrixMultiplier(codes.get(i), matrix));
            System.out.println("Code: " + codes.get(i) + " | code word:" + codeWords.get(i));
        }

        return codeWords;
    }

    private static ArrayList<String> codeGenerator(int n){
        int wordsCount = (int)Math.pow(2, n);
        ArrayList<String> codeWords = new ArrayList<>();
        String code;
        for(int i = 0; i < wordsCount; i++){
            if(i == 0)
                code = setZero(n);
            else
                code = xorPlusOne(codeWords.get(i-1));
            codeWords.add(code);
        }
        return codeWords;
    }

    private static String setZero(int n){
        String toReturn = "";
        for(int i = 0; i < n; i++)
            toReturn += "0";
        return toReturn;
    }

    private static String xorPlusOne(String previous){
        StringBuilder code = new StringBuilder(previous);
        int memory = 0;

        if(code.charAt(previous.length()-1) == '1') {
            code.setCharAt(previous.length()-1, '0');
            memory = 1;
        }
        else{
            code.setCharAt(previous.length()-1, '1');
        }

        for(int pos = previous.length()-2; pos >= 0; pos--){
            if(memory == 1){
                if(code.charAt(pos) == '1')
                    code.setCharAt(pos, '0');
                else{
                    code.setCharAt(pos, '1');
                    memory = 0;
                }
            }
        }
        return code.toString();
    }

    private static String codeMatrixMultiplier(String codeWord, ArrayList<int[]>matrix){
        String toReturn = "";
        int sum;

        for(int i = 0; i < matrix.get(0).length; i++){
            sum = 0;
            for(int j = 0; j < codeWord.length(); j++){
                if(codeWord.charAt(j) == '1' && matrix.get(j)[i] == 1)
                    sum++;
            }
            if(sum % 2 != 0)
                toReturn += "1";
            else
                toReturn += "0";
        }
        return toReturn;
    }

    public static ArrayList<int[]> fileReader(String path) throws IOException {
        BufferedReader file = new BufferedReader(new FileReader(path));
        String params = file.readLine();
        List<String> parameter = List.of(params.split(" "));
        int height = Integer.valueOf(parameter.get(0)), width = Integer.valueOf(parameter.get(1));

        String line;
        ArrayList<int[]> toReturn = new ArrayList<>();
        for(int i = 0; i < height; i++){
            line = file.readLine();
            int[] row = new int[width];
            for(int j = 0; j < width; j++){
                row[j] = Integer.valueOf(Character.toString(line.charAt(j)));
            }
            toReturn.add(row);
        }

        return toReturn;
    }

    public static void matrixOutput(ArrayList<int[]> matrix){
        for (int[] row: matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
