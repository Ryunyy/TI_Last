import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//d - кодовое расстояние = 2t + 1, t - кол-во исправляемых ошибок.

public class Main {
    public static void main(String[] args) throws IOException {
        //порождающая матрица
        ArrayList<int[]> GMatrix;
        //массив кодов. уже прям кодов кодов, т.е. с контрольными битами
        ArrayList<String> codes;
        //переменные всякие, за себя говорящие
        int wordsNum, n, d;
        //поменяй путь к файлику
        String path = "D:\\Study\\4Rehc\\TI\\lab5\\TI_Last\\lab5\\code.txt";
        //считываем матрикс в матрикс
        GMatrix = fileReader(path);
        //размерность кода
        n = GMatrix.size();
        //максимум кол-во слов для кодирования
        wordsNum = (int)Math.pow(2, n);

        //полином для вычисления разрешенных комбинаций
        int permitted = 2*n;
        System.out.println("Number of permitted combinations: " + permitted);

        System.out.println("Code dimension: " + n);
        System.out.println("Maximal possible code words: " + wordsNum);
        //генерация кодов поменбше и кодов побольбше
        codes = codeWordGenerator(GMatrix, n);
        //вычисление мин расстояния между кодами
        d = minimalDistance(codes);
        System.out.println("Minimal code distance: " + d);
        System.out.println("\nGenerative matrix:");
        matrixOutput(GMatrix);
    }

    public static int minimalDistance(ArrayList<String> codes){
        int toReturn = Integer.MAX_VALUE, dist;
        for(int i = 1; i < codes.size()-1; i++)
            for(int j = i+1; j < codes.size(); j++){
                //функция сравнения двух кодов
                dist = distance(codes.get(i), codes.get(j));
                //System.out.println(codes.get(i) + " | " + codes.get(j) + " -> " + dist);
                toReturn = dist < toReturn? dist: toReturn;
            }
        return toReturn;
    }

    private static int distance(String first, String second){
        //сложение происходит только если разряды отличаются, это и есть отличающаяся часть
        int toReturn = 0;
        for(int i = 0; i < first.length(); i++){
            if(first.charAt(i) != second.charAt(i))
                toReturn++;
        }
        return toReturn;
    }

    public static ArrayList<String> codeWordGenerator(ArrayList<int[]>matrix, int n){
        //первый - для кодов с контрольными битами, второй - для обычных
        ArrayList<String> codeWords = new ArrayList<>(), codes;
        //список разрешенных комбинаций
        ArrayList<String> permittedCodeWords = new ArrayList<>();
        //тут сами коды, ну т.е. 000 001 и т.д. к примеру, если n = 3
        codes = codeGenerator(n);
        int wordNum = (int)Math.pow(2, n);

        for(int i = 0; i < wordNum; i++) {
            //умножаем код на матрицу - получаем код с контрольными битами
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
                //для первого - 000 к прим. одтельно задается
                code = setZero(n);
            else
                //к предыдущему ксором плюсуется единичка
                code = xorPlusOne(codeWords.get(i-1));
            codeWords.add(code);
        }
        return codeWords;
    }

    private static String setZero(int n){
        //ну тут все просто
        String toReturn = "";
        for(int i = 0; i < n; i++)
            toReturn += "0";
        return toReturn;
    }

    private static String xorPlusOne(String previous){
        //мемои - перегруз, т.е. при сложении 1+1 ответ 0, но 1 в уме. вот этот "в уме" и есть мемори
        StringBuilder code = new StringBuilder(previous);
        int memory = 0;

        //для последней цифры считается отдельно, т.к. она порождает мемори
        if(code.charAt(previous.length()-1) == '1') {
            code.setCharAt(previous.length()-1, '0');
            memory = 1;
        }
        else{
            code.setCharAt(previous.length()-1, '1');
        }

        //ну и от конца к началу мы этот "в уме" раскидывем по числу, куда можем
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
        //суть проста - складываются только те элементы, где и в коде 1, и где в матрице 1. остальное 0. такая магия
        for(int i = 0; i < matrix.get(0).length; i++){
            sum = 0;
            for(int j = 0; j < codeWord.length(); j++){
                if(codeWord.charAt(j) == '1' && matrix.get(j)[i] == 1)
                    sum++;
            }
            //а тут по сумме получаем, что писать
            if(sum % 2 != 0)
                toReturn += "1";
            else
                toReturn += "0";
        }
        return toReturn;
    }

    public static ArrayList<int[]> fileReader(String path) throws IOException {
        //буффер, строка для чтения линии, список параметров - ну типо эти 3 5 к примеру в начале файла. сплитятся по пробелу
        BufferedReader file = new BufferedReader(new FileReader(path));
        String params = file.readLine();
        List<String> parameter = List.of(params.split(" "));
        int height = Integer.valueOf(parameter.get(0)), width = Integer.valueOf(parameter.get(1));

        String line;
        //массив массивов. ну типо матрица
        ArrayList<int[]> toReturn = new ArrayList<>();
        for(int i = 0; i < height; i++){
            //считали строку
            line = file.readLine();
            int[] row = new int[width];
            for(int j = 0; j < width; j++){
                //расхерачили строку на букавы и запихали ету сю пакостб в ряды. кста, числа, не символы
                row[j] = Integer.valueOf(Character.toString(line.charAt(j)));
            }
            //грузим контейнеры с букавами в матрицу
            toReturn.add(row);
        }

        return toReturn;
    }

    public static void matrixOutput(ArrayList<int[]> matrix){
        //пакостим в консольку букавами из контейнеров
        for (int[] row: matrix) {
            System.out.println(Arrays.toString(row));
        }
    }
}
