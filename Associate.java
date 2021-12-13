import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

/* Author: Rongxin Yang
* The limitation of this project is:
* It can only calculate 2nd order and 3rd order that can be decided by users with combination of all possible values of
* specific columns that are entered by users (NOT permute for all possibles, only for the columns that user enter)
* It cannot deal with general problems with 4 or more orders
* In the input file, first line is threshold, second line is row and column number, third line is # of states for each variable
* The function below end by 2 is work for Order 2, and end by 3 is work for Order 3
* */

public class Associate {

    public static double threshold;
    public static int COL;
    public static int ROW;
    int[][] Table;
    public static final double o = 3.0;

    public static int[] VariableEachCol;


    // constructor: used to initialize objects: load data from file
    public Associate(String fileName){
        try {
            File file = new File(fileName);             // read from file
            Scanner s = new Scanner(file);

            String line = s.nextLine();
            threshold = Double.parseDouble(line);       // get threshold: convert first line to double

            line = s.nextLine();                        // get ROW and COL
            String[] tempArr = line.split(" ");   // convert String to Array that separated by blank space
            ROW = Integer.parseInt(tempArr[0]);
            COL = Integer.parseInt(tempArr[1]);
            Table = new int[ROW][COL];

            line = s.nextLine();                        // get each columns' values
            tempArr = line.split(" ");
            VariableEachCol = new int[tempArr.length];
            for (int i = 0; i < tempArr.length; i++){
                VariableEachCol[i] = Integer.parseInt(tempArr[i]);
            }
            System.out.println("Each column " + AToString(VariableEachCol));
            System.out.println("---------------------------------------");

            int row = 0;                                // get Table
            while (s.hasNext()){
                line = s.nextLine();
                tempArr = line.split(" ");
                for (int col = 0; col < tempArr.length; col++){
                    Table[row][col] = Integer.parseInt(tempArr[col]);
                }
                row++;
            }
            tableToString(Table);       // output table
        } catch (FileNotFoundException e) {
            System.out.println(e);
        }
    }


    public static void main(String[]args) {
        Associate a = new Associate("input");
        System.out.println("==========================================");

        // let users enter the information they want ot check
        Scanner user = new Scanner(System.in);
        System.out.print("You want to know 2 order or 3 order association pattern: ");
        int order = user.nextInt();

        if (order == 2){
            System.out.println("Which columns you want to check?   (columns from 1 to " + (COL-1) + ")");
            System.out.print("1st column you want: ");
            int First2 = user.nextInt();
            System.out.print("2nd column you want: ");
            int Second2 = user.nextInt();
            System.out.println("==========================================");
            a.Associate2(First2-1, Second2-1);       // for multi patterns of column 0 and 1  e.g. P(0:1, 1:1)
        } else if (order == 3){
            System.out.println("Which columns you want to check?   (columns from 1 to " + (COL-1) + ")");
            System.out.print("1st column you want: ");
            int First3 = user.nextInt();
            System.out.print("2nd column you want: ");
            int Second3 = user.nextInt();
            System.out.print("3rd column you want: ");
            int Third3 = user.nextInt();
            System.out.println("==========================================");
            a.Associate3(First3-1, Second3-1, Third3-1);     // for multi patterns of column 0 and 1 and 2 e.g. P(0:1, 1:1, 2:4)
        } else {
            System.out.println("Sorry, not available of this order.");
        }

    }

    // 1st. Support Measure
    public void SupportMeasure3(int[][] p){
        // to calculate Intersection Probability of variables such as P(0:1, 1:0, 2:2)
        double Probability = IntersectProb3(p);

        boolean temp;
        if (Probability > threshold){
            System.out.println("P" + patternToString(p) + " = " + Probability + " > " + threshold + " => True");
            temp = true;
        } else if (Probability == threshold) {
            System.out.println("P" + patternToString(p) + " = " + Probability + " = " + threshold + " => False");
            System.out.println("P" + patternToString(p) + " is NOT statistically significant association pattern. ");
            temp = false;
        } else {
            System.out.println("P" + patternToString(p) + " = " + Probability + " < " + threshold + " => False");
            System.out.println("P" + patternToString(p) + " is NOT statistically significant association pattern. ");
            temp = false;
        }

        // if First true, then do the second test
        if (temp == true){
            LevelDependency3(p);
        }
    }
    public void SupportMeasure2(int[][] p){
        // 1st. Support Measure
        double Probability = IntersectProb2(p);
        boolean temp;

        if (Probability > threshold){
            System.out.println("P" + patternToString(p) + " = " + Probability + " > " + threshold + " => True");
            temp = true;
        } else if (Probability == threshold) {
            System.out.println("P" + patternToString(p) + " = " + Probability + " = " + threshold + " => False");
            System.out.println("P" + patternToString(p) + " is NOT statistically significant association pattern. ");
            temp = false;
        } else {
            System.out.println("P" + patternToString(p) + " = " + Probability + " < " + threshold + " => False");
            System.out.println("P" + patternToString(p) + " is NOT statistically significant association pattern. ");
            temp = false;
        }

        // if First true, then do the second test
        if (temp == true){
            LevelDependency2(p);
        }
    }


    // 2nd. Level of dependency in terms of mutual information measure
    public void LevelDependency3(int[][] p){
        // LHS
        double baseProb = Prob(p[0][0], p[0][1]) * Prob(p[1][0], p[1][1]) * Prob(p[2][0], p[2][1]);
        double MI = log2(IntersectProb3(p) / baseProb);
        System.out.println("LHS: MI" + patternToString(p) + " = " + MI);

        // RHS
        double R1 = 1/IntersectProb3(p);
        System.out.println("1/P" + patternToString(p) + " = " + R1);
        double R2 = X3(p)/(2*N());
        System.out.println("X^2/2N = " + R2);
        double R3 = Eup3()/ Edown3();
        System.out.println("E^/E' = " + R3);
        double R4 = o/2;
        System.out.println("o/2 = " + R4);
        double RHS = R1 * Math.pow(R2, Math.pow(Math.sqrt(R3), o));
        System.out.println("RHS: (1/P" + patternToString(p) + ") * (X^2/2N)^(E^/E')^(o/2) = " + RHS);

        // Conclusion
        if (MI > RHS) {
            System.out.println("P" + patternToString(p) + " is statistically significant association pattern. ");
        } else {
            System.out.println("P" + patternToString(p) + " is NOT statistically significant association pattern. ");
        }
    }
    public void LevelDependency2(int[][] p){
        // LHS
        double baseProb = Prob(p[0][0], p[0][1]) * Prob(p[1][0], p[1][1]);
        double MI = log2(IntersectProb2(p) / baseProb);
        System.out.println("LHS: MI" + patternToString(p) + " = " + MI);

        // RHS
        double RHS = X2(p)/(2*N());
        System.out.println("RHS: X^2/2N = " + RHS);

        // Conclusion
        if (MI > RHS) {
            System.out.println("P" + patternToString(p) + " is statistically significant association pattern. ");
        } else {
            System.out.println("P" + patternToString(p) + " is NOT statistically significant association pattern. ");
        }
    }


    // for multi patterns
    public void Associate3(int col1, int col2, int col3){
        int max1 = VariableEachCol[col1];
        int max2 = VariableEachCol[col2];
        int max3 = VariableEachCol[col3];
        for (int i = 0; i < max1; i++){
            for (int j = 0; j < max2; j++){
                for (int n = 0; n < max3; n++) {
                    SupportMeasure3(setPattern3(col1, i, col2, j, col3, n));
                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
                }
            }
        }
    }
    public void Associate2(int col1, int col2){
        int max1 = VariableEachCol[col1];
        int max2 = VariableEachCol[col2];
        for (int i = 0; i < max1; i++){
            for (int j = 0; j < max2; j++){
                SupportMeasure2(setPattern2(col1, i, col2, j));
                System.out.println("-------------------------------------------------------------");
            }
        }
    }


    // set patterns
    public int[][] setPattern3(int col1, int val1, int col2, int val2, int col3, int val3){     // for 3 order
        int[][] tempPattern = new int[3][3];
        tempPattern[0][0] = col1;
        tempPattern[0][1] = val1;
        tempPattern[1][0] = col2;
        tempPattern[1][1] = val2;
        tempPattern[2][0] = col3;
        tempPattern[2][1] = val3;
        return tempPattern;
    }
    public int[][] setPattern2(int col1, int val1, int col2, int val2){     // for 2 order
        int[][] tempPattern = new int[2][2];
        tempPattern[0][0] = col1;
        tempPattern[0][1] = val1;
        tempPattern[1][0] = col2;
        tempPattern[1][1] = val2;
        return tempPattern;
    }


    //P(v1, v2, v3) Intersection Probability
    public double IntersectProb3(int[][] p){
        return Count3(p)/N();
    }
    public double Count3(int[][] p){
        double ip = 0.0;
        Vector<Integer> temp = RowMatch3(p);
        for (int i = 0; i < temp.get(temp.size()-1); i++){
            ip += Table[temp.get(i)][COL-1];
        }
        return ip;
    }
    //P(v1, v2) Intersection Probability
    public double IntersectProb2(int[][] p){
        return Count2(p)/N();
    }
    public double Count2(int[][] p){
        double ip = 0.0;
        Vector<Integer> temp = RowMatch2(p);
        for (int i = 0; i < temp.get(temp.size()-1); i++){
            ip += Table[temp.get(i)][COL-1];
        }
        return ip;
    }


    // total Count
    public double N(){
        double n = 0.0;
        for (int r = 0; r < ROW; r++){
            n += Table[r][COL-1];
        }
        return n;
    }

    // X^2  chi-square
    public double X3(int[][] p){
        double x = 0.0;
        double Oi = Count3(p);
        System.out.println("o_i = " + Oi);
        double Probs = Prob(p[0][0], p[0][1]) * Prob(p[1][0], p[1][1]) * Prob(p[2][0], p[2][1]);
        double Ei = N() * Probs;
        System.out.println("e_i = " + Ei);
        x = Math.pow(Oi-Ei, 2) / Ei;
        System.out.println("X^2 = " + x);
        System.out.println("2N = " + 2*N());
        return x;
    }
    public double X2(int[][] p){
        double x = 0.0;
        double Oi = Count2(p);
        System.out.println("O_i = " + Oi);
        double Probs = Prob(p[0][0], p[0][1]) * Prob(p[1][0], p[1][1]);
        double Ei = N() * Probs;
        System.out.println("e_i = " + Ei);
        x = Math.pow(Oi-Ei, 2) / Ei;
        return x;
    }

    // E^  Expected entropy measure of estimated probability model
    public double Eup3(){
        double E = 0.0;
        for (int r = 0; r < ROW; r++){
            E += Table[r][COL-1]/N() * log2(N()/Table[r][COL-1]);
        }
        System.out.println("E^ = " + E);
        return E;
    }
    // E'  Maximum possible entropy of estimated probability model
    public double Edown3(){
        double E = 1.0;
        // times all columns' states
        int column = 0;
        while (column < COL-1){
            Vector<Integer> tempArr = new Vector<>();
            E *= VariableEachCol[column];
            column++;
        }
        E = log2(E);
        System.out.println("E' = " + E);
        return E;
    }

    // Log_2    Logarithm formula
    public static double log2(double x) {
        return Math.log(x) / Math.log(2);
    }

    //P(v)
    public double Prob(int col, int val){
        double p = 0.0;
        Vector<Integer> temp = RowMatch1(col, val);
        for (int i = 0; i < temp.get(temp.size()-1); i++){
            p += Table[temp.get(i)][COL-1];
        }
        return p/N();
    }



    //find the pattern and return how many rows match       [rowNum1, rowNum2, ..., numRows]
    public Vector<Integer> RowMatch3(int[][] p){
        Vector<Integer> out = new Vector<>();
        int n = 0;
        for (int row = 0; row < ROW; row++){
            if (Table[row][p[0][0]] == p[0][1] && Table[row][p[1][0]] == p[1][1] && Table[row][p[2][0]] == p[2][1]){
                out.add(row);
                n++;
            }
        }
        out.add(n);
        return out;
    }

    //find the pattern and return how many rows match       [rowNum1, rowNum2, ..., numRows]
    public Vector<Integer> RowMatch2(int[][] p){
        Vector<Integer> out = new Vector<>();
        int n = 0;
        for (int row = 0; row < ROW; row++){
            if (Table[row][p[0][0]] == p[0][1] && Table[row][p[1][0]] == p[1][1]){
                out.add(row);
                n++;
            }
        }
        out.add(n);
        return out;
    }

    // find the value that match in a column    [rowNum1, rowNum2, ..., numRows]
    public Vector<Integer> RowMatch1(int col, int val){
        Vector<Integer> out = new Vector<>();
        int n = 0;
        for (int row = 0; row < ROW; row++){
            if (Table[row][col] == val){
                out.add(row);
                n++;
            }
        }
        out.add(n);
        return out;
    }


    // output Table
    public void tableToString(int[][] t){    // output Table
        for (int r = 0; r < ROW; r++){
            for (int c = 0; c < COL; c++){
                System.out.print(t[r][c] + "\t");
            }
            System.out.println();
        }
    }

    public String patternToString(int[][] p){
        String str = "(";
        for (int i = 0; i < p.length; i++){
            int j = 0;
            str += p[i][j]+1 + ":" + p[i][j+1];
            if (i < p.length-1){
                str += ", ";
            }
        }
        return str + ")";
    }

    public String AToString(int[] t){    // output Table
        String str = "(";
        for (int i = 0; i < t.length; i++){
            str += t[i] ;
            if (i < t.length-1){
                str += ", ";
            }
        }
        return str + ")";
    }
}