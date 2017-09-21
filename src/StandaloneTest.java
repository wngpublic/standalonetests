import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.atomic.*;
import java.util.zip.CRC32;
import java.lang.instrument.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;

/*
ASCII table
ASCII   DECIMAL
0-9     48-57
A-Z     65-90
a-z     97-122
*/
public class StandaloneTest {
    public static boolean debug_ = false;
    public static void p(String f, Object ...o) {
        System.out.printf(f, o);
    }
    public static void pl(Object o) {
        System.out.println(o);
    }
    List<Method> methods = new ArrayList<>();
    public static void main(String[] args) {
        StandaloneTest test = new StandaloneTest();
        List<String> listString = new ArrayList<>();
        boolean useScanner = false;
        long timebegin = System.nanoTime();
        try {
            if(useScanner) {
                useScanner(listString);
                //test.t03(args, listString);
            } 
            else if(args.length > 0) {
            	test.test(args);
            }
            else {
            	test.printMethods();
                //test.t111(); 
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        long timeend = System.nanoTime();
        long timediff = timeend - timebegin;
        long timediffmilli = timediff / 1000000;
        p("\n");
        p("time elapse millis:%d\n", timediffmilli);
    }
    Utils util = new Utils();
    Random rand = new Random();
    Utils u = null;
    Random r = null;
    public StandaloneTest() {
        u = util;
        r = rand;
        getMethods();
    }
    private void test(String [] args) 
        throws  IllegalAccessException, 
                IllegalArgumentException, 
                InvocationTargetException 
    {
    	Integer i = Integer.parseInt(args[0]);
    	if(i >= methods.size()) {
    		printMethods();
    	} else {
    		Method m = methods.get(i);
    		m.invoke(this);
    	}
    }
    private void getMethods() {
        Method [] tmpM = this.getClass().getMethods();
    	for(int i = 0; i < tmpM.length; i++) {
    		if(tmpM[i].getName().matches("^t\\d+")) {
        		if(tmpM[i].getModifiers() == Modifier.PUBLIC) {
        			methods.add(tmpM[i]);
        		}
    		}
    	}
    }
    private void printMethods() {
    	for(int i = 0; i < methods.size(); i++) {
    		p("%3d: %s\n", i, methods.get(i).getName());
    	}
    }
    private static void print(String format, Object ... args) {
        System.out.printf(format, args);
    }
    private static void useScanner(List<String> listString) {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            String s = scanner.nextLine();
            if(s.length() == 0) {
                break;
            }
            listString.add(s);
        }
        if(listString.size() == 0) {
            scanner.close();
            return;
        }
        scanner.close();
    }
    public void t00(String[] args, List<String> list) {
        /*
         * Remove all adjacent matching letters. 
         * ie aaabccddd = abd
         */
        if(list.size() == 0) {
            // generate data
        }
        String s0 = list.get(0);
        char [] a = s0.toCharArray();
        char [] acopy = new char[a.length];
        char p = 0, c = 0;
        int i = 0, j = 0;
        while(true) {
            if(c == p) {
                if(i >= a.length) {
                    break;
                }
                p = a[i++];
                if(i >= a.length) {
                    if(j != 0 && acopy[j-1] == p) {
                        j--;
                    } else {
                        acopy[j++] = p;
                    }
                    break;
                }
                c = a[i++];                
            } else {
                if(j != 0 && acopy[j-1] == p) {
                    j--;
                } else {
                    acopy[j++] = p;
                }
                p = c;
                if(i >= a.length) {
                    if(j != 0 && acopy[j-1] == c) {
                        j--;
                    } else {
                        acopy[j++] = c;
                    }
                    break;
                }
                c = a[i++];                
            }
        }
        String result = new String(acopy, 0, j);
        if(j == 0) {
            System.out.printf("Empty String\n");
        } else {
            System.out.printf("%s\n", result);
        }
    }

    public void t01(String [] args, List<String> listString) {
        // count camel case and print out words
        if(listString.size() == 0) {
            String s = "saveTestChanges";
            listString.add(s);
        }
        String sOriginal = listString.get(0);
        char [] a = sOriginal.toCharArray();
        int j = 0;
        List<String> listWords = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        while(true) {
            if(j >= a.length) {
                String s = sb.toString();
                if(s.length() != 0) {
                    listWords.add(s);
                }
                break;
            }
            if(j == 0) {
                sb.append(a[j++]);
            } else if(Character.isUpperCase(a[j])) {                
                String s = sb.toString();
                if(s.length() != 0) {
                    listWords.add(s);
                }
                sb = new StringBuilder();
                sb.append(a[j++]);
            } else {
                sb.append(a[j++]);
            }
        }
        //for(int i = 0; i < listWords.size(); i++) {
        //    System.out.printf("%s\n", listWords.get(i));
        //}
        System.out.printf("%d\n", listWords.size());
    }
    public void t02(String [] args, List<String> listString) {
        /*
         * String always consists of two distinct alternating characters. 
         * For example, if string 's two distinct characters are x and y, 
         * then t could be xyxyx or yxyxy but not xxyy or xyyx.
         * You can convert some string to string by deleting characters 
         * from . When you delete a character from , you must delete all 
         * occurrences of it in . For example, if abaacdabd and you delete 
         * the character a, then the string becomes bcdbd.
         * Given , convert it to the longest possible string . Then 
         * print the length of string on a new line; if no string can 
         * be formed from , print instead.
         * 
         * If resulting string does not have all alternating chars,
         * then print 0. 
         * 
         * The first line contains a single integer denoting the length of .
         * The second line contains string .
         * 
         * S only contains lowercase English alphabetic letters 
         * (i.e., a to z).
         */
        char [] amap = new char[26];
        for(int i = 0; i < amap.length; i++) {
            amap[i] = 0;
        }
        if(listString.size() != 2) {
            return;
        }
        String sref = listString.get(1);
        char [] a = sref.toCharArray();
        char [] acopy = new char[a.length];
        // populate map
        for(int i = 0; i < a.length; i++) {
            char c = a[i];
            if(c < 97 || c > 122) {
                return;
            }
            int intval = c - 97;
            amap[intval] = 1;
        }
        // go through all the possible combinations of 2 chars
        // and get the max length
        int max = 0;
        char c0 = 0, c1 = 0;
        for(int i = 0; i < 26; i++) {
            if(amap[i] == 0) {
                continue;
            }
            c0 = (char)(i + 97);
            for(int j = i+1; j < 26; j++) {
                if(amap[j] == 0) {
                    continue;
                }
                c1 = (char)(j + 97);
                int z = 0;
                boolean stop = false;
                for(int k = 0; k < a.length && !stop; k++) {
                    if(a[k] == c0 || a[k] == c1) {
                        // if previous character == current char then invalid
                        if(z != 0 && acopy[z-1] == a[k]) {
                            stop = true;
                        } else {
                            acopy[z++] = a[k];
                        }
                    }
                }
                if(!stop) {
                    if(z > max) {
                        max = z;
                    }
                }
            }
        }
        System.out.printf("%d\n", max);
    }
    public void t03(String [] args, List<String> listString) {
        /*
         * Sandy likes palindromes. A palindrome is a word, phrase, 
         * number, or other sequence of characters which reads the 
         * same backward as it does forward. For example, madam is 
         * a palindrome.
         * On her birthday, Sandy's uncle, Richie Rich, offered her 
         * an -digit check which she refused because the number was 
         * not a palindrome. Richie then challenged Sandy to make 
         * the number palindromic by changing no more than digits. 
         * Sandy can only change digit at a time, and cannot add 
         * digits to (or remove digits from) the number.
         * Given and an -digit number, help Sandy determine the 
         * largest possible number she can make by changing digits.
         * Note: Treat the integers as numeric strings. Leading 
         * zeros are permitted and can't be ignored (So 0011 is 
         * not a palindrome, 0110 is a valid palindrome). A digit 
         * can be modified more than once.
         * Input Format
         * The first line contains two space-separated integers, 
         * (the number of digits in the number) and (the maximum 
         * number of digits that can be altered), respectively.
         * The second line contains an -digit string of numbers 
         * that Sandy must attempt to make palindromic.
         * Constraints
         *     Each character in the number is an integer where .
         * Output Format
         * Print a single line with the largest number that can 
         * be made by changing no more than digits; if this is 
         * not possible, print -1.
         * Sample Input 0
         * 4 1
         * 3943
         * Sample Output 0
         * 3993
         * Sample Input 1
         * 6 3
         * 092282
         * Sample Output 1
         * 992299
         * Sample Input 2
         * 4 1
         * 0011
         * Sample Output 2
         * -1
         * https://www.hackerrank.com/challenges/richie-rich
         */
        System.out.printf("\n%s %s\n", listString.get(0), listString.get(1));
        String s0 = listString.get(0);
        String [] ary0 = s0.split("\\s+");
        int szString = Integer.parseInt(ary0[0]);
        int maxChanges = Integer.parseInt(ary0[1]);
        String sNum = listString.get(1);;
        char [] ac = sNum.toCharArray();
        if(szString != ac.length) {
            System.out.printf("-1\n");
            return;
        }
        int numMismatches = 0;
        int [] ai = null;
        final int maxHalfIdx;
        {
            // populate ai, the list of mirror mismatches
            if(szString % 2 == 0) {
                maxHalfIdx = szString / 2 - 1;  // 4/2-1 = 1
                ai = new int[maxHalfIdx + 1];
                for(int i = 0; i < ai.length; i++) {
                    ai[i] = -1;
                }
                for(int i = 0; i <= maxHalfIdx; i++) {
                    int idxMirror = szString - i - 1;    // 4-0-1 = 3
                    if(ac[i] != ac[idxMirror]) {
                        ai[i] = idxMirror;
                        numMismatches++;
                    }
                }
            } else {
                maxHalfIdx = (szString - 1) / 2 - 1; // (5-1)/2-1 = 1
                ai = new int[maxHalfIdx + 1];
                for(int i = 0; i < ai.length; i++) {
                    ai[i] = -1;
                }
                for(int i = 0; i <= maxHalfIdx; i++) {
                    int idxMirror = szString - i - 1;    // 5-0-1 = 4
                    if(ac[i] != ac[idxMirror]) {
                        ai[i] = idxMirror;
                        numMismatches++;
                    }
                }
            }
        }
        if(maxChanges < numMismatches) {
            System.out.printf("-1\n");
            return;
        }
        {
            int numChangesRemaining = maxChanges;
            int mismatchesRemaining = numMismatches;
            // go through the array of mismatches and address those
            for(int i = 0; i < ai.length; i++) {
                int idxMirror = szString - i - 1;
                char lc = ac[i];
                char rc = ac[idxMirror];
                int l = Character.getNumericValue(lc);
                int r = Character.getNumericValue(rc);

                if(ai[i] == -1) {
                    if(numChangesRemaining > (mismatchesRemaining + 1)) {
                        if(ac[i] != '9') {
                            ac[i] = '9';
                            numChangesRemaining--;
                        }
                        if(ac[idxMirror] != '9') {
                            ac[idxMirror] = '9';
                            numChangesRemaining--;
                        }
                    }
                    continue;
                }
                
                if(numChangesRemaining > mismatchesRemaining) {
                    if(ac[i] != '9') {
                        ac[i] = '9';
                        numChangesRemaining--;
                    }
                    if(ac[idxMirror] != '9') {
                        ac[idxMirror] = '9';
                        numChangesRemaining--;
                    }
                } else {
                    if(l > r) {
                        ac[idxMirror] = ac[i];
                    } else {
                        ac[i] = ac[idxMirror];
                    }
                    numChangesRemaining--;
                }
                if(mismatchesRemaining > 0) {
                    mismatchesRemaining--;
                }
            }
            
            // it should be a palindrome here now
            if(numChangesRemaining > 0) {
                // use up remaining credits to change digits
                if(numChangesRemaining >= 2) {
                    for(int i = 0; 
                        i <= maxHalfIdx && numChangesRemaining >= 2; 
                        i++) 
                    {
                        int idxR = szString - i - 1;
                        char lc = ac[i];
                        int li = Character.getNumericValue(lc);
                        if(li != 9) {
                            ac[i] = '9';
                            ac[idxR] = '9';
                            numChangesRemaining -= 2;
                        }
                    }
                }
                if(numChangesRemaining >= 1) {
                    if(szString % 2 == 1) {
                        int idxHalf = (szString-1)/2;
                        ac[idxHalf] = '9';
                        numChangesRemaining--;
                    }
                }
            }
            String sfinal = new String(ac, 0, szString);
            System.out.printf("%s\n", sfinal);
        }
    }
    public void t03Shell(String [] args, List<String> listString) {
        t03(args, Arrays.asList("4 1", "3943"));
        t03(args, Arrays.asList("4 1", "0011"));        
        t03(args, Arrays.asList("5 0", "12345"));
        t03(args, Arrays.asList("5 1", "12345"));
        t03(args, Arrays.asList("5 2", "12345"));
        t03(args, Arrays.asList("5 3", "12345"));
        t03(args, Arrays.asList("5 4", "12345"));
        t03(args, Arrays.asList("5 5", "12345"));
        t03(args, Arrays.asList("5 0", "12321"));
        t03(args, Arrays.asList("5 1", "12321"));
        t03(args, Arrays.asList("5 2", "12321"));
        t03(args, Arrays.asList("5 3", "12321"));
        t03(args, Arrays.asList("5 4", "12321"));
        t03(args, Arrays.asList("5 5", "12321"));
        t03(args, Arrays.asList("5 0", "00000"));
        t03(args, Arrays.asList("5 1", "00000"));
        t03(args, Arrays.asList("5 2", "00000"));
        t03(args, Arrays.asList("5 3", "00000"));
        t03(args, Arrays.asList("5 4", "00000"));
        t03(args, Arrays.asList("5 5", "00000"));
        t03(args, Arrays.asList("5 0", "12341"));
        t03(args, Arrays.asList("5 1", "12341"));
        t03(args, Arrays.asList("5 2", "12341"));
        t03(args, Arrays.asList("5 3", "12341"));
        t03(args, Arrays.asList("5 4", "12341"));
        t03(args, Arrays.asList("5 5", "12341"));
        t03(args, Arrays.asList("5 6", "12341"));
        t03(args, Arrays.asList("6 3", "092282"));
        t03(args, Arrays.asList("6 3", "092282"));
        t03(args, Arrays.asList("6 4", "092282"));
        t03(args, Arrays.asList("6 5", "092282"));
        t03(args, Arrays.asList("6 6", "092282"));
        t03(args, Arrays.asList("6 7", "092282"));
    }
    public void t04(String [] args, List<String> listString) {
        /*
         * listString has 1-4 lines. Each line has 2 args each, first arg is 
         * denomination, second line has amount of 1, 5, 10, 25. 
         * eg:
         * 4        4 subsequent lines total
         * 3 1        3 x 1 cent
         * 4 5        4 x 5 cent
         * 5 10        5 x 10 cent
         * 6 25        6 x 25 cent
         * 
         */
        //Map<Integer, Integer> map = new HashMap<>();
        String s = listString.get(0);
        int numLines = Integer.parseInt(s);
        for(int i = 1; i < numLines; i++) {
            
        }
    }
    public void t05(int [] aryCoinValues, int max) {
        int [] totalWays = new int[max + 1];
        for(int i = 0; i < totalWays.length; i++) {
            totalWays[i] = 0;
        }
        for(int i = 0; i < aryCoinValues.length; i++) {
            for(int curCoinValue = aryCoinValues[i]; 
                    curCoinValue <= max; 
                    curCoinValue++) {
                int idxPrev = curCoinValue - aryCoinValues[i];
                if(idxPrev == 0) {
                    totalWays[curCoinValue] += 1;
                } else {
                    totalWays[curCoinValue] += totalWays[idxPrev];
                }
            }
            for(int j = 0; j <= max; j++) {
                System.out.printf("%2d ", totalWays[j]);
            }
            System.out.printf("\n");
        }
    }
    public void t06(List<String> list) {
        /*
         * knapsack. line 1 = maxWeight, line 2-x = weight,value tuple
            
            knapsack(int []av, int []aw, int max)
                int n = av.length
                int [][] a = new int[n][max+1]
                for(int i = 0; i < n; i++)
                    for(int j = 0; j <= max; j++)
                        int wCur = aw[i]
                        int vCur = av[i]
                        if(wCur > j)
                            a[i][j] = a[i-1][j]
                        else
                            int maxPrv = m[i-1][j]
                            int maxCur = m[i-1][j-wCur] + vCur
                            a[i][j] = (maxPrv > maxCur) ? maxPrv : maxCur
                return a[n-1][max]
         */
    }
    public void t07(List<String> list) {
        /*
         * longest subsequence. line 1 = string1, line2 = string2. 
         */
        if(list.size() != 2) {
            return;
        }
        String s0 = list.get(0);
        String s1 = list.get(1);
        char [] a0 = s0.toCharArray();
        char [] a1 = s1.toCharArray();
        int sz0 = a0.length;
        int sz1 = a1.length;
        int [][] a = new int[sz0][sz1];
        for(int i = 0; i < sz0; i++) {
            for(int j = 0; j < sz1; j++) {
                a[i][j] = 0;
            }
        }
        for(int i = 0; i < sz0; i++) {
            for(int j = 0; j < sz1; j++) {
                int idxI = (i == 0) ? 0 : (i - 1);
                int idxJ = (j == 0) ? 0 : (j - 1);
                int valN = a[idxI][j];
                int valW = a[i][idxJ];
                int valNW = a[idxI][idxJ];
                int valMax = (valN > valW) ? valN : valW;
                valMax = (valMax > valNW) ? valMax : valNW;
                if(a0[i] == a1[j]) {
                    valMax += 1;
                } else {
                    
                }
                a[i][j] = valMax;
            }
        }
        {
            print("   ");
            for(int i = 0; i < sz0; i++) {
                print("%2s ", a0[i]);
            }
            for(int i = 0; i < sz1; i++) {
                print("%2s ", a1[i]);
                for(int j = 0; j < sz0; j++) {
                    print("%2d ", a[j][i]);
                }
                print("\n");
                
            }
        }
    }
    public void t08() {
        for(long i = 0; i < 0xfffffffffL; i++) {
            List<String> list = Arrays.asList(Long.toString(i));
            t08(list);
        }
        print("done\n");
    }
    private void t08(List<String> listString) {
        long n = Long.parseLong(listString.get(0));
        long idxLargestBit = 0;
        boolean stop = false;
        for(long i = 62; i >= 0 && !stop; i--) {
            if((n >> i) == 1) {
                idxLargestBit = i;
                stop = true;
            }
        }
        long ctr1 = 0;
        // do a mini table calculation
        boolean doLoop = true;
        if(doLoop) {
            for(long i = 0; i <= n; i++) {
                long sum = n + i;
                long xor = n ^ i;
                if(sum == xor) {
                    ctr1++;
                }
            }
        }
        int ctr3 = 1;
        if(n != 0L) {
            for(long i = 0; i <= idxLargestBit; i++) {
                long bitVal = (n >> i) & 1L;
                if(bitVal == 0) {
                    ctr3 *= 2L;
                }
            }
        }
        if(ctr1 != ctr3) {
            System.out.printf("n:%d %d %d\n", n, ctr1, ctr3);
        }
    }
    public void t09(List<String> listString) {
        
        //for(String s: listString) {
            //char [] a = s.toCharArray();
        //}
    }
    public void t10(List<String> list) {
        /*
        longest common increasing subsequence LCS-LIS = LCIS
        
        LCIS(int []a0, int []a1) 
            int []a = {0}
            int n = a0.length
            int m = a1.length
            for(int i = 0; i < n; i++) 
                int c = 0
                for(int j = 0; j < m; j++)
                    if(a0[i] == a1[j])
                        if((c+1) > a[j])
                            a[j] = c + 1
                    if(a0[i] > a1[j])
                        if(a[j] > c)
                            c = a[j]
            int result = 0
            for(int i = 0; i < m; i++)
                if(a[i] > result)
                    result = a[i]
            return result
        */
    }
    public void t11(List<String> list) {
        /*
            longest increasing subsequence
        
            lis(int [] ain)
                int n = ain.length
                int [] ap = new int[n]
                int [] am = new int[n+1]
                int l = 0
                for(int i = 0; i < n; i++)
                    int lo = 1
                    int hi = l
                    while(lo <= hi)
                        int mid = ceil((lo+hi)/2)
                        if(
        */
    }
    public void t12(List<String> list) {
        /*
            queue<Integer> pre
            queue<Integer> post
            stack<Integer> reversePost

            void dfs(Graph g, int v)
                pre.add(v)
                onStack[v] = true
                marked[v] = true
                for(int w: g.adj(v))
                    if(hasCycle())
                        return
                    else if(!marked[w])
                        edgeTo[w] = v
                        dfs(g, w)
                    else if(onStack[w])
                        cycle = new Stack<Integer>()
                        for(int i = v; i != w; i = edgeTo[i])
                            cycle.push(i)
                        cycle.push(w)
                        cycle.push(v)
                onStack[v] = false
                post.add(v)
                reversePost.push(v) 
                // reverse post order in DAG is topologic sort V + E
            boolean hasCycle()
                return cycle != null

        */
    }
    public void t13() {
        AsynchronousTests test = new AsynchronousTests();
        test.test();
    }

    public void t14() {
        List<String> list = new ArrayList<>();
        list.add("I");
        list.add("II");
        list.add("III");
        list.add("IV");
        list.add("V");
        list.add("VI");
        list.add("VII");
        list.add("VIII");
        list.add("IX");
        list.add("X");
        list.add("XI");
        list.add("XII");
        list.add("XIII");
        list.add("XIV");
        list.add("XV");
        list.add("XVI");
        list.add("XVII");
        list.add("XVIII");
        list.add("XIX");
        list.add("XX");
        list.add("XXI");
        list.add("XXII");
        list.add("XXIII");
        list.add("XXIV");
        list.add("XXV");
        list.add("XXVI");
        list.add("XXVII");
        list.add("XXVIII");
        list.add("XXIX");
        list.add("XXX");
        list.add("XXXI");
        list.add("XXXII");
        list.add("XXXIII");
        list.add("XXXIV");
        list.add("XXXV");
        list.add("XXXVI");
        list.add("XXXVII");
        list.add("XXXVIII");
        list.add("XXXIX");
        list.add("XL");
        list.add("XLI");
        list.add("XLII");
        list.add("XLIII");
        list.add("XLIV");
        list.add("XLV");
        list.add("XLVI");
        list.add("XLVII");
        list.add("XLVIII");
        list.add("XLIX");
        list.add("L");
        
        Misc misc = new Misc();

        for(String s: list) {
            int sz = s.length();
            int v = 0;
            String subString = null;
            if(s.startsWith("I")) {
                v = 1;
                if(sz > 1) {
                    subString = s;
                    v = misc.convertRomanLessThan10(subString);
                }
            } else if(s.startsWith("V")) {
                v = 5;
                if(sz > 1) {
                    subString = s;
                    v = misc.convertRomanLessThan10(subString);
                }
            } else if(s.startsWith("L")) {
                v = 50;
                if(sz > 1) {
                    subString = s.substring(1);
                    v = misc.convertRomanLessThan10(subString);
                }
            } else if(s.startsWith("XL")) {
                v = 40;
                if(sz > 2) {
                    subString = s.substring(2);
                    v += misc.convertRomanLessThan10(subString);
                }
            } else if(s.startsWith("XXX")) {
                v = 30;
                if(sz > 3) {
                    subString = s.substring(3);
                    v += misc.convertRomanLessThan10(subString);
                }
            } else if(s.startsWith("XX")) {
                v = 20;
                if(sz > 2) {
                    subString = s.substring(2);
                    v += misc.convertRomanLessThan10(subString);
                }
            } else if(s.startsWith("X")) {
                v = 10;
                if(sz > 1) {
                    subString = s.substring(1);
                    v += misc.convertRomanLessThan10(subString);
                }
            }
            print("ROMAN:%10s V:%d\n", s, v);
        }
    }
    public void t15() {
        long max        = 100000000L; // Long.MAX_VALUE; 
        long sizeBlock  = 10000000L;
        //List<Long> list = new ArrayList<>();
        // sieve of erasthmus
        Set<Long> set = new LinkedHashSet<>();
        Set<Long> setPrime = new LinkedHashSet<>();
        long cur = 0;
        StringBuilder sb = new StringBuilder();
        String s;
        for(long i = 0; i < max; i += sizeBlock) {
            long timeBeg = System.currentTimeMillis();
            long offsetMax = cur + sizeBlock;
            // add cur:sizeBlock to set and evaluate that slice
            for(long j = cur; j < offsetMax; j++) {
                if(j != 2 && j != 3 && (j % 2 == 0 || j % 3 == 0)) {
                    continue;
                }
                //s = String.format("populate range: %10d to %10d add:%10d\n", 
                //    cur, offsetMax, j);
                //sb.append(s);
                set.add(j);
            }
            long half = offsetMax/2+1;
            // for each number starting from 3, mark all multiples of this
            // in range cur:offsetMax
            boolean rangeStartMinIsExecuted = false;
            for(long j = 3; j < half; j += 2) {
                if(!set.contains(j)) {
                    continue;
                }
                long multiple = cur / j;
                long rangeStart = (cur / j);
                rangeStart = (rangeStart < 2) ? 2 : rangeStart;
                if(rangeStart == 2) {
                    if(!rangeStartMinIsExecuted) {
                        rangeStartMinIsExecuted = true;
                    } else {
                        break;
                    }
                }
                //s = String.format("half:%10d j:%10d mult:%10d range:%10d\n",
                //    half, j, multiple, rangeStart);
                //sb.append(s);
                for(long k = rangeStart; multiple < offsetMax; k++) {
                    multiple = j * k;
                    set.remove(multiple);
                    //s = String.format("remove mult:%10d j:%10d k:%10d\n", 
                    //    multiple, j, k);
                    //sb.append(s);
                }
            }
            long timeEnd = System.currentTimeMillis();
            long timeDif = timeEnd - timeBeg;
            s = String.format("add range:%10d:%10d timeDiff:%d ms\n", 
                cur, offsetMax, timeDif);
            sb.append(s);
            print(s);
            cur += sizeBlock;
        }
        // add the remaining values to set, dont worry about overlap
        for(Long l: set) {
            //print("add to prime: %d\n", l);
            setPrime.add(l);
        }
        int i = 1;
        for(Long l: setPrime) {
            s = String.format("%10d", l);
            sb.append(s);
            //print(s);
            i++;
            if(i % 10 == 0) {
                s = "\n";
                sb.append(s);
                //print(s);
                i = 1;
            }
        }
        s = "\n";
        sb.append(s);
        //print(s);
        s = String.format("size:%d\n", setPrime.size());
        print(s);
        sb.append(s);
        String filename = "primes.txt";
        PrintWriter pw = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename); // (filename, true);
            pw = new PrintWriter(new BufferedWriter(fileWriter));
            pw.print(sb.toString());
        } catch(Exception e) {
        } finally {
            if(pw != null) {
                pw.close();
            }
        }
    }
    public void t16() {
        long max        = 300000000L; // Long.MAX_VALUE; 
        long sizeBlock  = 10000000L;
        //List<Long> list = new ArrayList<>();
        // sieve of erasthmus
        Set<Long> set = new LinkedHashSet<>();
        Set<Long> setPrime = new LinkedHashSet<>();
        long cur = 0;
        StringBuilder sb = new StringBuilder();
        String s;
        for(long i = 0; i < max; i += sizeBlock) {
            long timeBeg = System.currentTimeMillis();
            long offsetMax = cur + sizeBlock;
            // add cur:sizeBlock to set and evaluate that slice
            for(long j = cur; j < offsetMax; j++) {
                if(j != 2 && j != 3 && (j % 2 == 0 || j % 3 == 0)) {
                    continue;
                }
                set.add(j);
            }
            for(long j = 3; j < offsetMax; j += 2) {
                if(!set.contains(j)) {
                    continue;
                }
                long multiple = 1;
                for(long k = 2; multiple < offsetMax; k++) {
                    multiple = j * k;
                    if(multiple < cur) {
                        continue;
                    }
                    set.remove(multiple);
                }
            }
            long timeEnd = System.currentTimeMillis();
            long timeDif = timeEnd - timeBeg;
            s = String.format("add range:%10d:%10d timeDiff:%d ms\n", 
                cur, offsetMax, timeDif);
            sb.append(s);
            print(s);
            cur += sizeBlock;
        }
        // add the remaining values to set, dont worry about overlap
        for(Long l: set) {
            //print("add to prime: %d\n", l);
            setPrime.add(l);
        }
        int i = 1;
        for(Long l: setPrime) {
            s = String.format("%10d", l);
            sb.append(s);
            i++;
            if(i % 10 == 0) {
                s = "\n";
                sb.append(s);
                i = 1;
            }
        }
        s = "\n";
        sb.append(s);
        s = String.format("size:%d\n", setPrime.size());
        print(s);
        sb.append(s);
        String filename = "primes.txt";
        PrintWriter pw = null;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filename); // (filename, true);
            pw = new PrintWriter(new BufferedWriter(fileWriter));
            pw.print(sb.toString());
        } catch(Exception e) {
        } finally {
            if(pw != null) {
                pw.close();
            }
        }
    }
    public void t17() {
        String filename = "file_1billion_numbers.txt";
        File file = new File(filename);
        int max = 200000000;
        int width = 80;
        int sizeBuf = 10000000;
        if(file.exists() && file.isFile()) {
            return;
        }
        PrintWriter printWriter = null;
        try {
            FileWriter fileWriter = new FileWriter(file);
            printWriter = new PrintWriter(fileWriter);
            StringBuilder sb = new StringBuilder();
            String s;
            String sline;
            int sizeLine = 0;
            for(int i = 0; i <= max; i++) {
                if(i != max) {
                    s = String.format("%d,",i);
                } else {
                    s = String.format("%d\n",i);
                }
                if(sizeLine > width) {
                    sb.append("\n");
                    sizeLine = s.length();
                } else {
                    sizeLine += s.length();
                }
                sb.append(s);
                if(sb.length() > sizeBuf) {
                    sline = sb.toString();
                    printWriter.print(sline);
                    sb.delete(0, sb.length());
                    print("writing out. count:%d\n", i);
                }
            }
            if(sb.length() != 0) {
                sline = sb.toString();
                printWriter.print(sline);
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(printWriter != null) {
                printWriter.close();
            }
        }
    }
    public void t18() {
        // kmp algos
        int casenum = 0xffff;
        List<String> lpat = new ArrayList<>();
        List<String> lstr = new ArrayList<>();
        lpat.add("ababaababa");
        lstr.add("abaabababaababa");
        if(((casenum >> 0) & 1) == 1) {
            lpat.add("ababaabaababaa");
            lstr.add("abaabababbababaaababaababababababaabaababaaababb");
        }
        if(((casenum >> 1) & 1) == 1) {
            lpat.add("ababaa");
            lstr.add("abaabababbababaaa");
        }
        if(((casenum >> 2) & 1) == 1) {
            lpat.add("abababaa");
            lstr.add("abababababbababababaabab");
        }
        if(((casenum >> 3) & 1) == 1) {
            lpat.add("aabaababaa");
            lstr.add("abaabaaabaabaaabaabaabaabaababaabab");
        }
        for(int j = 0; j < lpat.size(); j++) {
            print("-------------------------- CASE %d\n", j);
            String pat = lpat.get(j);
            String str = lstr.get(j);
            Strings.KMP kmp = new Strings.KMP(pat);
            int i = kmp.match(str);
            assert (i != -1) : String.format("i=%d\n", i); 
            print("i=%d\n", i);
        }
    }
    public void t19() {
        // coin change problem
        Misc misc = new Misc();
        int en = 0x2;
        if(((en >> 0) & 1) == 1) 
        {
            int [] coins = {1,5,10};
            int amount = 10;
            int numComb = misc.change(amount, coins);
            print("t19: numComb = %d\n", numComb);
        }
        if(((en >> 1) & 1) == 1) 
        {
            int [] c = {1,2,3};
            int amt = 6;
            int num = misc.change(amt, c);
            print("t19: comb: %d\n", num);
        }
    }

    public void t20() {
        // print out different combinations
        int [] a = {1,2,3,4};
        List<Integer> list = new ArrayList<>();
        Misc misc = new Misc();
        misc.printComb(a, 0, list);
    }

    public void t21() {
        // treemap example
        // Object firstKey()
        // Object lastKey()
        // SortedMap headMap(Object lessThanKey)
        // SortedMap tailMap(Object greaterEqualKey)
        // SortedMap subMap(Object fromKey, Object toKey)
        print("\tTreemap Integer, Character\n");
        { 
            TreeMap<Integer, Character> map = new TreeMap<>();
            map.put(1,'a');
            map.put(9,null);
            map.put(11,'b');
            map.put(19,null);
            map.put(21,'c');
            map.put(29,null);
            map.put(31,'d');

            Map.Entry<Integer, Character> e1;
            Map.Entry<Integer, Character> e2;
            e1 = map.floorEntry(1);
            e2 = map.lowerEntry(1);
            print(" 1 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
            e1 = map.floorEntry(8);
            e2 = map.lowerEntry(8);
            print(" 8 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
            e1 = map.floorEntry(9);
            e2 = map.lowerEntry(9);
            print(" 9 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
            e1 = map.floorEntry(10);
            e2 = map.lowerEntry(10);
            print("10 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
            e1 = map.floorEntry(11);
            e2 = map.lowerEntry(11);
            print("11 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
            e1 = map.floorEntry(31);
            e2 = map.lowerEntry(31);
            print("31 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
            e1 = map.floorEntry(50);
            e2 = map.lowerEntry(50);
            print("50 floor:%s lower:%s\n",e1.getValue(), 
                (e2 == null) ? null : e2.getValue());
        }
        print("\tTreemap Double, Character\n");
        { 
            TreeMap<Double, Character> map = new TreeMap<>();
            map.put(1.0,'a');
            map.put(1.9,null);
            map.put(2.0,'b');
            map.put(3.9,'c');
            map.put(4.0,'d');
            print("1.0=>'a'\n");
            print("1.9=>null\n");
            print("2.0=>'b'\n");
            print("3.9=>'c'\n");
            print("4.0=>'d'\n");

            Map.Entry<Double, Character> e1;
            Map.Entry<Double, Character> e2;
            Map.Entry<Double, Character> e3;
            e1 = map.floorEntry(1.0);
            e2 = map.lowerEntry(1.0);
            e3 = map.ceilingEntry(1.0);
            print("1.0 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(1.8);
            e2 = map.lowerEntry(1.8);
            e3 = map.ceilingEntry(1.8);
            print("1.8 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(1.9);
            e2 = map.lowerEntry(1.9);
            e3 = map.ceilingEntry(1.9);
            print("1.9 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(2.0);
            e2 = map.lowerEntry(2.0);
            e3 = map.ceilingEntry(2.0);
            print("2.0 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(2.9);
            e2 = map.lowerEntry(2.9);
            e3 = map.ceilingEntry(2.9);
            print("2.9 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(3.0);
            e2 = map.lowerEntry(3.0);
            e3 = map.ceilingEntry(3.0);
            print("3.0 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(3.8);
            e2 = map.lowerEntry(3.8);
            e3 = map.ceilingEntry(3.8);
            print("3.8 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(3.9);
            e2 = map.lowerEntry(3.9);
            e3 = map.ceilingEntry(3.9);
            print("3.9 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());

            e1 = map.floorEntry(5.8);
            e2 = map.lowerEntry(5.8);
            e3 = map.ceilingEntry(5.8);
            print("5.8 floor:%s lower:%s ceil:%s\n",
                e1.getValue(), (e2 == null) ? null : e2.getValue(), 
                (e3 == null) ? null : e3.getValue());
        }
 
    }
    public void t22() {
        // navigablemap example
    }
    public void t23() {
        // ACircularQueue 10 producers and 5 consumers
        List<Thread> listConsumer = new ArrayList<>();
        List<Thread> listProducer = new ArrayList<>();
        SConcurrency.Multithreading.ACircularQueue<Integer> queue = new SConcurrency.Multithreading.ACircularQueue<>(8);
        List<Integer> listExpected = new ArrayList<>();
        int szProducers = 10;
        int szConsumers = 5;
        int szToProduce = 1000;
        int szTotalToProduce = szProducers * szToProduce;
        int szPerConsume = 5;
        String nameBaseProducer = "producer";
        String nameBaseConsumer = "consumer";
        int idxStart = 0;
        for(int i = 0; i < szProducers; i++) {
            String name = String.format("%s%02d", nameBaseProducer, i);
            SConcurrency.Multithreading.ACircularQueueProducer p = 
                    new SConcurrency.Multithreading.ACircularQueueProducer(queue, name, idxStart, szToProduce);
            Thread t = new Thread(p);
            idxStart += szToProduce;
            listProducer.add(t);
        }
        for(int i = 0; i < szConsumers; i++) {
            String name = String.format("%s%02d", nameBaseConsumer, i);
            SConcurrency.Multithreading.ACircularQueueConsumer c = 
                    new SConcurrency.Multithreading.ACircularQueueConsumer(queue, name, szPerConsume);
            Thread t = new Thread(c);
            idxStart += szToProduce;
            listConsumer.add(t);
        }
        for(Thread t: listConsumer) {
            t.start();
        }
        for(Thread t: listProducer) {
            t.start();
        }
        try {
            for(Thread t: listConsumer) {
                t.join();
            }
            for(Thread t: listProducer) {
                t.join();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
        for(int i = 0; i < szTotalToProduce; i++) {
            listExpected.add(new Integer(i));
        }
        ConcurrentHashMap<Integer, Integer> cmap = queue.getResultMap();
        if(cmap.size() != szTotalToProduce) {
            print("cmap size:%d != total expected:%d\n", 
                    cmap.size(), szTotalToProduce);
            for(Integer i: listExpected) {
                Integer v = cmap.get(i);
                if(v == null) {
                    print("error: %d is null in map\n", i);
                }
            }
            assert(false) : 
                String.format("cmap size:%d != total expected:%d\n", 
                        cmap.size(), szTotalToProduce);
        } else {
            print("cmap size:%d == total expected:%d\n",
                    cmap.size(), szTotalToProduce);
            boolean errorSeen = false;
            for(Integer i: listExpected) {
                Integer v = cmap.get(i);
                if(v == null) {
                    print("error: %d is null in map\n", i);
                    errorSeen = true;
                }
            }
            if(!errorSeen) {
                print("no error with comparisons\n");
            }
        }
    }
    public void t24() {
        // ACircularQueue 10 consumers and 5 producers
        List<Thread> listConsumer = new ArrayList<>();
        List<Thread> listProducer = new ArrayList<>();
        SConcurrency.Multithreading.ACircularQueue<Integer> queue = new SConcurrency.Multithreading.ACircularQueue<>(8);
        List<Integer> listExpected = new ArrayList<>();
        int szProducers = 5;
        int szConsumers = 10;
        int szToProduce = 2000;
        int szTotalToProduce = szProducers * szToProduce;
        int szPerConsume = 5;
        String nameBaseProducer = "producer";
        String nameBaseConsumer = "consumer";
        int idxStart = 0;
        for(int i = 0; i < szProducers; i++) {
            String name = String.format("%s%02d", nameBaseProducer, i);
            SConcurrency.Multithreading.ACircularQueueProducer p = 
                    new SConcurrency.Multithreading.ACircularQueueProducer(queue, name, idxStart, szToProduce);
            Thread t = new Thread(p);
            idxStart += szToProduce;
            listProducer.add(t);
        }
        for(int i = 0; i < szConsumers; i++) {
            String name = String.format("%s%02d", nameBaseConsumer, i);
            SConcurrency.Multithreading.ACircularQueueConsumer c = 
                    new SConcurrency.Multithreading.ACircularQueueConsumer(queue, name, szPerConsume);
            Thread t = new Thread(c);
            idxStart += szToProduce;
            listConsumer.add(t);
        }
        for(Thread t: listConsumer) {
            t.start();
        }
        for(Thread t: listProducer) {
            t.start();
        }
        try {
            for(Thread t: listConsumer) {
                t.join();
            }
            for(Thread t: listProducer) {
                t.join();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        } finally {

        }
        for(int i = 0; i < szTotalToProduce; i++) {
            listExpected.add(new Integer(i));
        }
        ConcurrentHashMap<Integer, Integer> cmap = queue.getResultMap();
        if(cmap.size() != szTotalToProduce) {
            print("cmap size:%d != total expected:%d\n", 
                    cmap.size(), szTotalToProduce);
            for(Integer i: listExpected) {
                Integer v = cmap.get(i);
                if(v == null) {
                    print("error: %d is null in map\n", i);
                }
            }
            assert(false) : 
                String.format("cmap size:%d != total expected:%d\n", 
                        cmap.size(), szTotalToProduce);
        } else {
            print("cmap size:%d == total expected:%d\n",
                    cmap.size(), szTotalToProduce);
            boolean errorSeen = false;
            for(Integer i: listExpected) {
                Integer v = cmap.get(i);
                if(v == null) {
                    print("error: %d is null in map\n", i);
                    errorSeen = true;
                }
            }
            if(!errorSeen) {
                print("no error with comparisons\n");
            }
        }
    }
    public void t25() {
        List<Thread> list = new ArrayList<>();
        int numThreads = 10;
        int numLoops = 20;
        SConcurrency.Multithreading.ASimpleSemaphore semaphore = new SConcurrency.Multithreading.ASimpleSemaphore();
        for(int i = 0; i < numThreads; i++) {
            String name = String.format("user%02d", i);
            SConcurrency.Multithreading.ASimpleSemaphoreUser user = 
                    new SConcurrency.Multithreading.ASimpleSemaphoreUser(numLoops, name, semaphore);
            Thread t = new Thread(user);
            list.add(t);
        }
        for(int i = 0; i < numThreads; i++) {
            Thread t = list.get(i);
            t.start();
        }
        try {
            for(int i = 0; i < numThreads; i++) {
                list.get(i).join();
            }
        } catch(InterruptedException e) {
            
        } finally {
            print("done with semaphores\n");
        }
    }
    public void t26() {
        {
            Misc.Line l1 = new Misc.Line(10,10,20,20);
            Misc.Line l2 = new Misc.Line(15,10,15,20);
            boolean intersects = l1.intersects(l2);        
            l1.print();
            l2.print();
            print("intersects %s\n", intersects);
        }
        {
            Misc.Line l1 = new Misc.Line(10,10,20,20);
            Misc.Line l2 = new Misc.Line(15,10,25,20);
            boolean intersects = l1.intersects(l2);        
            l1.print();
            l2.print();
            print("intersects %s\n", intersects);
        }
    }
    public void t27() {
        // max sum array subset with pos/neg numbers
        int [] a = {-1,4,-2,5,-2,4,2,-5};
        int max = a[0];
        int maxTmp = a[0];
        for(int i = 0; i < a.length; i++) {
            int v = maxTmp + a[i];
            maxTmp = (a[i] > v) ? a[i] : v;
            max = (max < maxTmp) ? maxTmp : max;
        }
        for(int i = 0; i < a.length; i++) {
            print("%3d ", a[i]);
        }
        print(" max:%d\n", max);
    }
    public void t28() {
        // rewrite remove all adjacent matching letters.  ie aaabccddd = abd
        String s = null;
        s = "aaabccddd";
        {
            if(s == null || s.length() <= 1) {
                return;
            }
            char [] a = s.toCharArray();
            char cur = 0;
            char prv = 0;
            StringBuilder sb = new StringBuilder();
            for(int i = 0; i < a.length; i++) {
                cur = a[i];
                if(cur == prv) {
                    prv = 0;
                } else {
                    if(prv != 0) {
                        sb.append(prv);
                    }
                    if(i == (a.length - 1)) {
                        sb.append(cur);
                    }
                    prv = cur;
                }
            }
            String res = sb.toString();
            print("orig:%s, res:%s\n", s, res);
        }
    }
    public void t29() {
        // sudoko solver
    }
    public void t30() {
        // generate all binary strings without consecutive 1s
        // eg: k = 3 => 000,001,010,100,101
        Trees.Recursion recursion = new Trees.Recursion();
        {
            int max = 5;
            char [] a = new char[max];
            List<String> l = new ArrayList<>();
            recursion.genBinStringsNoConsecutiveOnes(max, 0, a, l);
            for(String s: l) {
                print("%s ", s);
            }
            print("\n");
        }
    }
    public void t31() {
        Trees.TreeRange tree = new Trees.TreeRange();
        int [] amin = {40,15,65,05,25,55,75,00,10,20,30,50,60,70,80,72,78,03,24,33,57,67};
        int [] amax = {50,25,75,15,35,65,85,10,20,30,40,60,70,80,90,82,88,13,34,43,97,77};
        for(int i = 0; i < amin.length; i++) {
            tree.add(amin[i], amax[i]);
        }
        tree.print();
    }
    public void t32() {
        // elevator logic
        //int max = 30;
        boolean isUp = true;
        int hCur = 0;
        int hMax = 0;
        int hMin = 0;
        int numIterations = 10;
        //boolean [] a = new boolean[max];
        for(int i = 0; i < numIterations; i++) {
            //int nextInt = rand.nextInt(max + 1);
            while((isUp && hCur != hMax) || (!isUp && hCur != hMin)) {
            }
        }
    }
    public void t33() {
        // n strings of variable length, k pieces, get max length
        Trees.Recursion recursion = new Trees.Recursion();
        int k = 43;
        double [] ad = {8.02,4.03,7.46,6.32,9.99,30.10};
        int [] ai = new int[ad.length];
        int min = 0;
        int max = 0;
        int maxPieceLen = 0;
        int minPieceLen = 0;
        for(int i = 0; i < ai.length; i++) {
            ai[i] = (int)(ad[i] * 100.0);
            if(i == 0) {
                max = ai[i];
                minPieceLen = min;
                maxPieceLen = max;
            } else {
                if(ai[i] < minPieceLen) {
                    minPieceLen = ai[i];
                }
                if(ai[i] > maxPieceLen) {
                    maxPieceLen = ai[i];
                }
            }
        }
        print("k = %d\n", k);
        for(int i = 0; i < ai.length; i++) {
            print("%3d = %d\n", i, ai[i]);
        }
        min = 0;
        max = maxPieceLen;
        int maxLength = recursion.findMaxLength2(ai, k, 0, max);
        double maxD = (double)maxLength/100.0;
        print("max:%.2f\n", maxD);
    }
    public void t34() {
        // max rope cutting
        // cut rope in different parts of int length that max product 
        // of all length of all parts
        Trees.Recursion recursion = new Trees.Recursion();
        int len = 9;
        int v = recursion.maxCutProductOfParts(9);
        print("len:%d v:%d\n", len, v);
    }
    public void t35() {
        // binary search recursive
        int [] a = {1,2,3,4,5,6,7,8,9,10,11,12};
        Trees.Recursion recursion = new Trees.Recursion();
        int [] b = {3,11,-1,21};
        for(int i = 0; i < b.length; i++) {
            int v = recursion.binarySearchIterative(a, 0, a.length-1, b[i]);
            print("search %2d -> %d\n", b[i], v);
        }
    }
    public void t36() {
        // binary search iterative
        int [] a = {1,2,3,4,5,6,7,8,9,10,11,12};
        Trees.Recursion recursion = new Trees.Recursion();
        int [] b = {3,11,-1,21};

        //for(int i = 0; i < b.length; i++) {
        //    int v = recursion.binarySearchRecursive(a, 0, a.length-1, a.length/2, b[i]);
        //    print("search %2d -> %d\n", b[i], v);
        //}
        for(int i = 0; i < b.length; i++) {
            int v = recursion.binarySearch(a, 0, a.length - 1, b[i]);
            print("binsearch %2d -> %2d\n", b[i], v);
        }
    }
    public void t37() {
        // find all factor combinations, ie 8 = 2*2*2|2*4|1*8
        Trees.Recursion r = new Trees.Recursion();

        // factor table
        Map<Integer, Map<Integer,Integer>> t = new LinkedHashMap<>();
        r.createFactorTable(t, 500);

        List<List<Integer>> lf = new ArrayList<>();
        List<Integer> l = new ArrayList<>();
        int v = 24;
        r.getListFactors(v, l, t, lf);
        for(List<Integer> list: lf) {
            if(list == null) {
                continue;
            }
            for(Integer i: list) {
                print("%d ", i);
            }
            print("\n");
        }
    }
    public void t38() {
        // test factor table
        Trees.Recursion r = new Trees.Recursion();
        int [] a = {500}; 
        for(int i = 0; i < a.length; i++) {
            Map<Integer, Map<Integer,Integer>> m = new LinkedHashMap<>();
            print("\tTABLE FOR %d\n", a[i]);
            r.createFactorTable(m, a[i]);
            r.printFactorTable(m);
        }
    }
    public void t39() {
        // test factor table
        Trees.Recursion r = new Trees.Recursion();
        //int v = 100;
        int [] a = {24,32,36,64};

        for(int i = 0; i < a.length; i++) {
            Map<Integer,Integer> m = new HashMap<>();
            print("\tTABLE FOR %d\n", a[i]);
            r.createFactorTable2(m, a[i]);
            for(Map.Entry<Integer, Integer> kv: m.entrySet()) {
                print("%2d,%2d\n", kv.getKey(), kv.getValue());
            }
        }
    }
    public void t40() {
        // given n*n matrix from 1:n^2, all distinct, find max length
        // of progressive sequence, must be +1 and no diagonal move.
        //int [][] a0 = { {1,2,9},
        //                {5,3,8},
        //                {4,6,7}};   // ans: 4 because 6789
        //int [][] a1 = { { 9, 1,15, 2},
        //                { 3, 7,14,13},
        //                { 4, 5, 6,12},
        //                { 0, 8,10,11}};
        //List<Integer> l = new ArrayList<>();
        //Graph g = new Graph();
        //g.findLongestPathMatrix(a, l);
    }
    public void t41() {
        // find all bridges in a undirected graph. a bridge is an edge where
        // if disconnected, forms disjoint graphs.
    }
    public void t42() {
        // given n lines with time x for all n lines, fit a queue of jobs
        // into each line that best fits time x, where each job is time <= x.
    }
    public void t43() {
        // return all values in sparse table within a given range. 
    }
    public void t44() {
        // given array a and b of equal length, pair each element of a to b,
        // and find min sum of absolute diff best pair.
    }
    public void t45() {
        // boyer moore algorithm
    }
    public void t46() {
        // kmp algorithm
    }
    public void t47() {
        // quicksort algorithm
    }
    public void t48() {
        // biggest valley between 2 peaks in 1d array
    }
    public void t49() {
        // peak in 2d array
    }
    public void t50() {
        // biggest valley between 2 peaks in 2d array
    }
    public void t51() {
        // find all anagram pairs in string
        //String s = "alababala";
        //String s = "abqwrtyba";
        String s = "abba";
        char [] a = s.toCharArray();
        print("%s\n", s);
        Map<Character, List<Integer>> m = new HashMap<>();
        for(int i = 0; i < a.length; i++) {
            List<Integer> l = m.get(a[i]);
            if(l == null) {
                l = new ArrayList<Integer>();
                m.put(a[i], l);
            }
            l.add(i);
        }
        int ctr = 0;
        // for each char with frequency > 1, check anagram
        for(int i = 0; i < a.length; i++) {
            List<Integer> l = m.get(a[i]);
            if(l.size() == 1) {
                continue;
            }
            // get the index from list of cur char idx,
            // which enables looping from that position to end matches
            int idxList = 0;
            for(int j = 0; j < l.size(); j++) {
                if(l.get(j) == i) {
                    idxList = j;
                    break;
                }
            }
            if((idxList + 1) == l.size()) {
                continue;
            }
            // for each matching char in array
            print("%s: idxList:%d ", a[i], idxList);
            int idxBeg = i;
            int idxEnd = l.get(idxList + 1);
            print("beg:%d end:%d\n", idxBeg, idxEnd);
            StringBuilder sb0 = new StringBuilder();
            StringBuilder sb1 = new StringBuilder();
            for(int j = idxList + 1; j < l.size(); j++) {
                Map<Character, Integer> map0 = new HashMap<>();
                Map<Character, Integer> map1 = new HashMap<>();
                for(int k = idxBeg, n = idxEnd; k <= idxEnd; k++, n--) {
                    Integer i0 = map0.get(a[k]);
                    if(i0 == null) {
                        i0 = 1;
                    } else {
                        i0 += 1;
                    }
                    map0.put(a[k], i0);
                    Integer i1 = map1.get(a[n]);
                    if(i1 == null) {
                        i1 = 1;
                    } else {
                        i1 += 1;
                    }
                    map1.put(a[n], i1);
                    //print("\ta[k:%d]=%s a[n:%d]=%s\n", k,a[k],n,a[n]);
                    sb0.append(a[k]);
                    sb1.append(a[n]);
                    boolean isMismatch = false;
                    for(Map.Entry<Character, Integer> kv: map0.entrySet()) {
                        char key = kv.getKey().charValue();
                        int value = kv.getValue().intValue();
                        if(map1.containsKey(key)) {
                            int v2 = map1.get(key).intValue();
                            if(v2 != value) {
                                isMismatch = true;
                                break;
                            }
                        } else {
                            isMismatch = true;
                            break;
                        }
                    }
                    if(!isMismatch) {
                        String s0 = sb0.toString();
                        StringBuilder sbreverse = new StringBuilder(sb1.toString());
                        String s1 = sbreverse.reverse().toString();
                        if(s0.equalsIgnoreCase(s1) && s0.length() != 1) {
                            continue;
                        }
                        ctr++;
                        print("ctr:%2d s0:%15s s1:%15s\n", ctr, s0, s1);
                    }
                }
            }
        }
    }
    public void t52() {
        Matrices.ArrayAlgos array = new Matrices.ArrayAlgos();
        int [] a = {8,6,4,4,5,1,5,7,9,9,7,5,5,3,3,2,3,2,4,3,2,4,5,5,7,5,4};
        array.findBiggestValley(a);
    }
    public void t53() {
        int sz = 20;
        int numloops = 3;
        for(int i = 0; i < sz; i++) {
            print("%2d ", i);
        }
        print("\n");
        for(int j = 0; j < numloops; j++) {
            int [] a = new int[sz];
            for(int i = 0; i < sz; i++) {
                a[i] = i+1;
            }
            util.shuffle(a, 3);
            for(int i = 0; i < sz; i++) {
                print("%2d ", a[i]);
            }
            print("\n");
        }
        util.setCharset("abcd");
        char [] ac = util.getAryChar(sz);
        for(int j = 0; j < numloops; j++) {
            util.shuffle(ac, 3);
            for(int i = 0; i < sz; i++) {
                print("%2s ", ac[i]);
            }
            print("\n");
        }
    }
    public void t54() {
        {
            int sz = 20;
            util.reset();
            char [] a = "abcdefghi".toCharArray();
            util.shuffle(a, 5); // util.getAryChar(sz);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.sort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
    }
    public void t55() {
        // quicksort
        { // no duplicates
            print("CASE 1\n");
            int sz = 10;
            util.reset();
            int [] a = {1,2,3,4,5,6,7,8,9,10};
            util.shuffle(a, 5);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.sort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
        { // duplicates
            print("CASE 2\n");
            int sz = 10;
            util.reset();
            int [] a = {1,2,2,3,3,3,4,5,6,6};
            util.shuffle(a, 5);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.sort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
        { // duplicates
            print("CASE 3\n");
            int sz = 30;
            util.reset();
            int [] a = util.getAryInt(sz, 10,20);
            util.shuffle(a, 5);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.sort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
    }
    public void t56() {
        // test currentTimeMillis
        {
            List<Long> a = new ArrayList<>();
            List<String> as = new ArrayList<>();
            for(int i = 0; i < 20; i++) {
                Long l = System.nanoTime();
                a.add(l);
                String s = l.toString();
                s = s.substring(s.length() - 8, s.length());
                as.add(s);
            }
            for(int i = 0; i < a.size(); i++) {
                print("LONG   time:%d\n", a.get(i));
                print("STRING time:%s\n", as.get(i));
            }
        }
        {
            //List<String> a = new ArrayList<>();
            for(int i = 0; i < 20; i++) {
                Long l0 = System.nanoTime();
                String s0 = l0.toString();
                s0 = s0.substring(s0.length() - 8, s0.length());
                Long l1 = System.nanoTime();
                String s1 = l1.toString();
                s1 = s1.substring(s1.length() - 8, s1.length());
                String s2 = new StringBuilder(s1).reverse().toString();
                int xor = Integer.parseInt(s0) ^ Integer.parseInt(s2);
                if(xor < 0x989680) {
                    xor = xor ^ Integer.parseInt(s1);
                }
                String s3 = String.valueOf(xor);
                if(s3.length() > 8) {
                    s3 = s3.substring(s3.length() - 8, s3.length());
                }
                print("s0:%s s1:%s xor:%s\n", s0, s1, s3);
            }
        }
    }
    public void t57() {
        // merge sort
        { // no duplicates
            print("CASE 1\n");
            int sz = 10;
            util.reset();
            int [] a = {1,2,3,4,5,6,7,8,9,10};
            util.shuffle(a, 3);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.mergesort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
        boolean earlyReturn = true;
        if(earlyReturn) {
            return;
        }
        { // duplicates
            print("CASE 2\n");
            int sz = 10;
            util.reset();
            int [] a = {1,2,2,3,3,3,4,5,6,6};
            util.shuffle(a, 5);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.mergesort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
        { // duplicates
            print("CASE 3\n");
            int sz = 30;
            util.reset();
            int [] a = util.getAryInt(sz, 10,20);
            util.shuffle(a, 5);
            for(int i = 0; i < sz; i++) {
                print("%2d ", i);
            }
            print("\n");
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            util.mergesort(a);
            for(int i = 0; i < a.length; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
        }
    }
    public void t58() {
        Trees.TreeBinary t = new Trees.TreeBinary();
        {
            /*
             *                  20
             *         10                 40
             *       5    15          25      55
             *     6                24  26      56
             *                    23      27      57
             *                  22          28      58
             *                                29      59
             *                                  30
             */
            //int [] a = {3,1,0,2,5,4,6};
            int [] a = {20,10,5,6,15,40,55,25,26,27,28,29,30,24,23,22,56,57,58,59};
            for(int i = 0; i < a.length; i++) {
                t.add(a[i]);
            }
        }
        //t.printTree();
        int maxHeight = t.getMaxHeight();
        int maxWidth = t.getMaxWidth();
        print("maxwidth:%d maxHeight:%d\n", maxWidth, maxHeight);
    }
    public void t59() {
        Strings t = new Strings();
        String s =     "This is a regular expression. This supports *, ?, \\ digits and alphas. " +
                    "Also numbers like 0-9, 1234567890, 0123456789";
        List<String> l = t.regex("\\d+", s);
        for(String sl: l) {
            print("%s\n", sl);
        }
    }
    public void t60() {
        // tree get range test
        Trees.TreeBinary tree = new Trees.TreeBinary();
        tree.generateRandomTree(10);
        tree.printTree();
        print("get range 4,8\n");
        tree.printGetRange(4, 8);
    }
    public void t61() {
        // test print matrix borders
        Matrices.Array arrays = new Matrices.Array();
        arrays.generateRandomArray(10, 10);
        print("\nprint matrix\n");
        arrays.printMatrix();
        print("\nprint border 0,0\n");
        arrays.printBorders(0, 0);
        print("\nprint border 10,10\n");
        arrays.printBorders(10, 10);
        print("\nprint border 9,9\n");
        arrays.printBorders(9, 9);
        print("\nprint border 0,9\n");
        arrays.printBorders(0, 9);
        print("\nprint border 9,0\n");
        arrays.printBorders(9, 0);
        print("\nprint border 5,5\n");
        arrays.printBorders(5, 5);
    }
    public void t62() {
        // test print matrix wraparound
        Matrices.Array arrays = new Matrices.Array();
        arrays.generateRandomArray(10, 10);
        print("\nprint matrix\n");
        arrays.printMatrix();
        print("\nprint border wrapped 0,0\n");
        arrays.printWrapAround(0, 0);
        print("\nprint border wrapped 10,10\n");
        arrays.printWrapAround(10, 10);
        print("\nprint border wrapped 9,9\n");
        arrays.printWrapAround(9, 9);
        print("\nprint border wrapped 0,9\n");
        arrays.printWrapAround(0, 9);
        print("\nprint border wrapped 9,0\n");
        arrays.printWrapAround(9, 0);
        print("\nprint border wrapped 5,5\n");
        arrays.printWrapAround(5, 5);
    }
    public void t63() {
        // test generate random graph DAG
        GraphOld.Graph graph = new GraphOld.Graph();
        print("rand graph 15, isDirected, isDAG, 2 connectivity\n");
        graph.generateRandomGraph(15, true, true, 2);
        graph.printGraph();
        print("\n");
        graph.printTopologicalSort();
        print("\n");
        graph.printAllChains();
    }
    public void t64() {
        // test generate random graph directed not DAG
        GraphOld.Graph graph = new GraphOld.Graph();
        print("rand graph 15, isDirected, !isDAG, 3 connectivity\n");
        graph.generateRandomGraph(15, true, false, 3);
        graph.printGraph();
        print("\n");
        graph.printTopologicalSort();
        print("\n");
        graph.printAllChains();
    }
    public void t65() {
        // test generate random graph undirected
        GraphOld.Graph graph = new GraphOld.Graph();
        print("rand graph 15, !isDirected, !isDAG, 3 connectivity\n");
        graph.generateRandomGraph(15, false, false, 3);
        graph.printGraph();
        print("\n");
        graph.printTopologicalSort();
        print("\n");
        graph.printAllChains();
    }
    public void t66() {
        // test generate random graph DAG
        GraphOld.Graph graph = new GraphOld.Graph();
        print("rand graph 50, isDirected, isDAG, 3 connectivity\n");
        graph.generateRandomGraph(50, true, true, 3);
        graph.printGraph();
        print("\n");
        graph.printTopologicalSort();
        print("\n");
        graph.printAllChains();
    }
    public void t67() {
        // test generate random graph directed
        GraphOld.Graph graph = new GraphOld.Graph();
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<List<Integer>> ll = new ArrayList<>();
        
        ll.add(Arrays.asList(9,5,13));
        ll.add(Arrays.asList(2,12,13));
        ll.add(Arrays.asList(12,14));
        ll.add(Arrays.asList(6,7,13));
        ll.add(Arrays.asList(5,8,12,13));

        ll.add(Arrays.asList(6,14));
        ll.add(Arrays.asList(9,13));
        ll.add(Arrays.asList(10,11,13));
        ll.add(Arrays.asList(11,14));
        ll.add(Arrays.asList(11,14));

        ll.add(Arrays.asList(13,14));
        ll.add(Arrays.asList(12,14));
        ll.add(Arrays.asList());
        ll.add(Arrays.asList());
        ll.add(Arrays.asList());

        for(int i = 0; i < ll.size(); i++) {
            map.put(i, ll.get(i));
        }
        print("rand graph 15, isDirected, !isDAG, 3 connectivity\n");
        graph.loadGraphList(map);
        //graph.generateRandomGraph(15, true, true, 3);
        graph.printGraph();
        print("\n");
        graph.printTopologicalSort();
        print("\n");
        graph.printAllChains();
        print("\n");
        print("minspangraph:\n");
        graph.printMinSpanGraphUnweighted();
    }
    public void t68() {
        // test generate random graph undirected
        GraphOld.Graph graph = new GraphOld.Graph();
        print("rand graph 15, !isDirected, !isDAG, 3 connectivity\n");
        Map<Integer, List<Integer>> map = new HashMap<>();
        List<List<Integer>> ll = new ArrayList<>();
        
        ll.add(Arrays.asList(9,5,13));
        ll.add(Arrays.asList(2,12,13));
        ll.add(Arrays.asList(12,14));
        ll.add(Arrays.asList(6,7,13));
        ll.add(Arrays.asList(5,8,12,13));

        ll.add(Arrays.asList(6,14));
        ll.add(Arrays.asList(9,13));
        ll.add(Arrays.asList(10,11,13));
        ll.add(Arrays.asList(11,14));
        ll.add(Arrays.asList(11,14));

        ll.add(Arrays.asList(13,14));
        ll.add(Arrays.asList(12,14));
        ll.add(Arrays.asList());
        ll.add(Arrays.asList());
        ll.add(Arrays.asList());

        for(int i = 0; i < ll.size(); i++) {
            map.put(i, ll.get(i));
        }
        graph.loadGraphList(map);
        //graph.generateRandomGraph(15, true, true, 3);
        graph.printGraph();
        print("\n");
        graph.printTopologicalSort();
        print("\n");
        graph.printAllChains();
        print("\n");
        print("minspangraph:\n");
        graph.printMinSpanGraphUnweighted();
    }
    public void t69() {
        // how long before a random string of 8 characters gets duplicated?
        Random rand = new Random();
        String stringset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Set<String> set = new HashSet<>();
        int maxTries = 1000000000; // 1 billion tries
        boolean hasDuplicate = false;
        int sizeStr = 8;
        int sizeSet = stringset.length();
        int i = 0;
        String word = null;
        for(i = 0; i < maxTries && !hasDuplicate; i++) {
            {
                StringBuilder sb = new StringBuilder();
                for(int j = 0; j < sizeStr; j++) {
                    int idx = rand.nextInt(sizeSet);
                    sb.append(stringset.charAt(idx));
                }
                word = sb.toString();
            }
            if(set.contains(word)) {
                //{
                //    StringBuilder sb = new StringBuilder();
                //    for(int j = 0; j < sizeStr; j++) {
                //        int idx = rand.nextInt(sizeSet);
                //        sb.append(stringset.charAt(idx));
                //    }
                //    word = sb.toString();
                //}
                if(set.contains(word)) {
                    hasDuplicate = true;
                }
                else {
                    set.add(word);
                }
            }
            else {
                set.add(word);
            }
            if(i % 1000000 == 0) {
                print("i = %d\n", i);
            }
        }
        print("got duplicate string of length 8 at %d iter with word:%s\n", 
            i, word);
    }
    public void t70() {
        // how long before a random string of 8 characters gets duplicated?
        //Random rand = new Random();
        //String stringset = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String stringset = "abcdefghijklmnopqrstuvwxyz0123456789";
        //int maxTries = 1000000000; // 1 billion tries
        int maxTries = 100; // 1 billion tries
        boolean hasDuplicate = false;
        int sizeStr = 8;
        int sizeSet = stringset.length();
        int i = 0;
        String word = null;
        Utils u = new Utils();
        // do a trie to save space
        Trees.Nodes.NodeCharTree tree = new Trees.Nodes.NodeCharTree(sizeSet);
        int numTriesDuplicates = 2;
        for(i = 0; i < maxTries && !hasDuplicate; i++) {
            int k = 0;
            for(k = 0; k < numTriesDuplicates; k++) {
                word = u.getRandString(stringset, sizeStr);
                if(!tree.put(word)) {
                    break;
                }
            }
            if(k == numTriesDuplicates) {
                hasDuplicate = true;
            }
            if(i % 100000 == 0) {
                //System.gc();
                //Runtime.gc();
                long freeMemory = Runtime.getRuntime().freeMemory();
                print("i = %d free mem:%d\n", i, freeMemory);
            }
        }
        print("got duplicate string of length 8 at %d iter with word:%s\n", 
            i, word);
    }
    public void t71() {
        // Transactions
        List<String> names = Arrays.asList("joe","tom","john","alice","jane","mary");
        Map<String, Misc.Ledger.Person> m = new HashMap<>();
        int initialCredit = 100;
        for(String name: names) {
            Misc.Ledger.Person p = new Misc.Ledger.Person(name);
            p.credit(initialCredit);
            m.put(name, p);
        }
        Misc.Ledger ledger = new Misc.Ledger();
        {
            // the list of people owe joe total of 80
            List<Misc.Ledger.Person> l = Arrays.asList(
                m.get("joe"),
                m.get("tom"),
                m.get("mary"),
                m.get("jane")
            );
            m.get("joe").debit(80);
            ledger.addTransaction(l, m.get("joe"), 80);
        }
        {
            // list of ppl owe mary total of 30
            List<Misc.Ledger.Person> l = Arrays.asList(
                m.get("joe"),
                m.get("tom"),
                m.get("jane")
            );
            m.get("mary").debit(30);
            ledger.addTransaction(l, m.get("mary"), 30);
        }
        {
            // list of ppl owe jane total of 20
            List<Misc.Ledger.Person> l = Arrays.asList(
                m.get("joe"),
                m.get("tom")
            );
            m.get("jane").debit(20);
            ledger.addTransaction(l, m.get("jane"), 20);
        }

        print("FULL TRANSACTIONS\n");
        ledger.printTransactions();
        print("CONSOLIDATE TRANSACTIONS\n");
        ledger.consolidatePendingTransactions();
        print("SETTLE\n");
        ledger.settle();
    }
    public void t72() {
        /*
         * do 1M jobs asynchronously.
         */
        (new SConcurrency.TestConcurrency()).test();
    }
    public void t73() {
        // permutation
        Trees.SCombinations t = new Trees.SCombinations();
        String s = "abcdef";
        t.permutations(s);
    }
    public void t74() {
        // combination
        Trees.SCombinations t = new Trees.SCombinations();
        String s = "abcdef";
        t.combinations(s);
    }
    public void t75() {
        // array algos view
        Matrices.ArrayAlgos t = new Matrices.ArrayAlgos();
        int [] a0 = {3,2,1,7,6,4,5};
        t.findL2RViews(a0);
        int [] a1 = {7,5,3,4,2,6,3};
        t.findL2RViews(a1);
    }
    public void t76() {
        // LCA BT
        /*
         *                         90
         *             50                        25
         *         45        80                30        15
         *       75  20  40  55          35  60
         * 
         * pre:    90 50 45 75 20 80 40 55 25 30 35 60 15
         * in:    75 45 20 50 40 80 55 90 35 30 60 25 15
         */
        Trees.TreeBinary bt = new Trees.TreeBinary();
        int [] a = {90,50,25,45,80,30,15,75,20,40,55,35,60};
        Map<Integer, Trees.TreeBinary.Node> m = 
            new HashMap<>();
        for(int i = 0; i < a.length; i++) {
            Trees.TreeBinary.Node n = 
                new Trees.TreeBinary.Node(a[i]);
            m.put(a[i], n);
        }

        LinkedList<Trees.TreeBinary.Node> l =
            new LinkedList<>();
        int i = 1;
        Trees.TreeBinary.Node r = m.get(a[0]);
        l.add(r);
        while(l.size() != 0) {
            Trees.TreeBinary.Node n = l.poll();
            if(i < a.length) {
                n.l = m.get(a[i++]);
                l.add(n.l);
            }
            if(i < a.length) {
                n.r = m.get(a[i++]);
                l.add(n.r);
            }
        }
        bt.setRoot(r);
        bt.printPreOrder();
        bt.printInOrder();
        bt.findLCA(80, 15);
        bt.findLCA(80, 45);
        bt.findLCA(35, 60);
        bt.findLCA(75, 40);
        bt.findLCA(75, 55);
        bt.findLCA(40, 45);
        bt.findLCA(15, 60);
        bt.findLCA(50, 60);
        bt.findLCA(75, 20);
        bt.findLCA(40, 55);
    }
    public void t77() {
        // binary search
        int sz = 9 + rand.nextInt(5);
        int min = 10;
        int max = 99;
        List<Integer> l = util.getListInt(sz, min, max);
        int idxRand = rand.nextInt(sz);
        int v = l.get(idxRand);
        Search t = new Search();
        System.out.println(l);
        Integer result = t.binarySearch(l, v);
        assert result != null : String.format("cannot find binarysearch val %d\n", v);
        System.out.println(l);
        print("binary search found %d\n", v);
    }
    public void t78() {
        // many t77
        for(int i = 0; i < 10; i++) {
            t77();
        }
    }
    public void t79() {
        // range search
    }
    public void t80() {
        // levenshtein edit
        Strings t = new Strings();
        t.levenshteinEdit("abdeh", "abcfh");
        t.levenshteinEdit("abdeh", "bdehi");
        t.levenshteinEdit("abdeh", "bdehlm");
        t.levenshteinEdit("sitting","kitten");
        t.levenshteinEdit("sunday","saturday");
    }
    public void t81() {
        // min test on comparable
        util.printMinInts();
        util.printMinInts(4,7,8);
        util.printMinInts(9,8,3,6);
    }
    public void t82() {
        // test hash string
        int prime = 31;
        Map<Integer, String> map = new HashMap<>();
        int numcases = 20000;
        int szstr = 8;
        List<String> liststring = new ArrayList<>();
        List<Integer> listint = new ArrayList<>();
        for(int i = 0; i < numcases; i++) {
            String s = util.getRandString(szstr);
            int hash = 0;
            for(int j = 0; j < s.length(); j++) {
                hash = hash * prime + s.charAt(j);
            }
            liststring.add(s);
            listint.add(hash);
            if(map.get(hash) != null) {
                for(int j = 0; j < liststring.size(); j++) {
                    print("hash:%16d string:%s\n", listint.get(j), liststring.get(j));
                }
                print("collision for hash %d, thisstring = %s, oldstring = %s\n", hash, s, map.get(hash));
                print("map contains %d items already\n", liststring.size());
                return;
            }
            map.put(hash, s);
        }
        //for(int j = 0; j < liststring.size(); j++) {
        //    print("hash:%16d string:%s\n", listint.get(j), liststring.get(j));
        //}
        print("hash test ok for %d cases\n", numcases);
    }
    public void t83() {
        Misc t = new Misc();
        t.testCRCDistribution(1000000, 8);
    }
    public void t84() {
        // longest common substring
        Strings t = new Strings();
        String s1 = "abcdefghijkl";
        String s2 = "zyxcdefgabc";
        t.longestCommonSubstring(s1, s2, true);
    }
    public void t85() {
        // gaussian distribution test
        Misc t = new Misc();
        t.distributionGaussian();
    }
    public void t86() {
        // uniform distribution test
        Misc t = new Misc();
        t.distributionUniform();
    }
    public void t87() throws IOException {
        // testWriteTestFile
        Misc t = new Misc();
        String filename = "test1.txt";
        filename = t.testWriteTestFile(filename);
        p("filename %s written\n", filename);
        t.testReadTestFile(filename);
        p("filename %s read\n", filename);
    }
    public void t88() {
        Matrices.UncompressedMatrix matrix = 
            new Matrices.UncompressedMatrix();
        p("put 0,0,1\n");
        matrix.put(0, 0, 1);
        p("put 3,4,4\n");
        matrix.put(3, 4, 4);
        matrix.print();
        p("put 2,1,2\n");
        matrix.put(2, 1, 2);
        matrix.print();
        p("put 2,8,8\n");
        matrix.put(2, 8, 8);
        matrix.print();
        int sizeCol = matrix.getSizeCol();
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < sizeCol; i++) {
            list.add(i);
        }
        p("put row 4 same size\n");
        matrix.putRow(4, list);
        matrix.print();
        list.add(10);
        list.add(11);
        p("put row 4 increase size\n");
        matrix.putRow(4, list);
        matrix.print();
        p("put row 7 same size\n");
        matrix.putRow(7, list);
        matrix.print();
        int sizeRow = matrix.getSizeRow();
        List<Integer> list1 = new ArrayList<>();
        for(int i = 0; i < sizeRow; i++) {
            list1.add(i*10 + i);
        }
        p("put col into col 1\n");
        matrix.putColumn(1, list1);
        matrix.print();
        list1.add(200);
        list1.add(300);
        p("put col into outer column with expansion of rows\n");
        sizeCol = matrix.getSizeCol();
        matrix.putColumn(sizeCol+3, list1);
        matrix.print();
    }
    public void t89() {
        // test Row
        p("test row\n");
        Matrices.SimpleCompressedMatrix.Row row = 
            new Matrices.SimpleCompressedMatrix.Row(5);
        assert (row.rowNum() == 5) : "row num not 5\n";
        
        p("add 0,1 and 3,4\n");
        row.add(0, 1);
        row.add(3, 4);
        row.printCompressed();
        row.printFull();
        p("\n");

        p("add 3,5\n");
        row.add(3, 5);
        row.printCompressed();
        row.printFull();
        p("\n");

        p("add 7,8\n");
        row.add(7, 8);
        row.printCompressed();
        row.printFull();
        p("\n");
        
        p("add 5,6\n");
        row.add(5, 6);
        row.printCompressed();
        row.printFull();
        p("\n");

        Integer v = row.get(8);
        assert (v == 8) : "expected 8 lookup has val 8\n";
        v = row.get(3);
        assert (v == 5) : "expected 3 lookup has val 5\n";

        p("add 1,2\n");
        row.add(1, 2);
        row.printCompressed();
        row.printFull();
        p("\n");
        
        p("remove col 7\n");
        row.remove(7);
        row.printCompressed();
        row.printFull();
        p("\n");

        p("remove col 4\n");
        row.remove(4);
        row.printCompressed();
        row.printFull();
        p("\n");
    }
    public void t90() {
        p("graph methods test\n");
        Map<Integer, Set<Integer>> map = new HashMap<>();
        /*
         * create manual graph:
         * 
         * 0:    2
         * 1:    3
         * 2:    4,5,7
         * 3:    2,4
         * 4:    5,7,8
         * 5:    6
         * 6:    -
         * 7:    8
         * 8:    -
         * 
         *        +------------------------+
         *        |                        |
         * 0----->2------+----->5----->6   |
         *        ^      |      ^          |
         *        |      |      |          |
         *        |      V      |          V
         * 1----->3----->4------+--------->7------>8
         *                      |                  ^
         *                      +------------------+
         * 
         * 
         */
        {
            map.put(0, new HashSet<Integer>(Arrays.asList(2)));
            map.put(1, new HashSet<Integer>(Arrays.asList(3)));
            map.put(2, new HashSet<Integer>(Arrays.asList(4,5,7)));
            map.put(3, new HashSet<Integer>(Arrays.asList(2,4)));
            map.put(4, new HashSet<Integer>(Arrays.asList(5,7,8)));
            map.put(5, new HashSet<Integer>(Arrays.asList(6)));
            map.put(6, new HashSet<Integer>(Arrays.asList()));
            map.put(7, new HashSet<Integer>(Arrays.asList(8)));
            map.put(8, new HashSet<Integer>(Arrays.asList()));
        }
        GraphClass.Graph graph = new GraphClass.Graph(map);
        graph.printGraph();

        Collection<GraphClass.Edge> edges = null;
        edges = graph.getEdges(2);
        p("getEdges of node 2\n");
        for(GraphClass.Edge edge: edges) {
            p("edge id:%2d weight:%d\n", edge.dstId(), edge.weight());
        }
        p("\n");

        p("getEdges of node 5\n");
        edges = graph.getEdges(5);
        for(GraphClass.Edge edge: edges) {
            p("edge id:%2d weight:%d\n", edge.dstId(), edge.weight());
        }
        p("\n");

        p("getEdges of node 6\n");
        edges = graph.getEdges(6);
        for(GraphClass.Edge edge: edges) {
            p("edge id:%2d weight:%d\n", edge.dstId(), edge.weight());
        }
        if(edges.isEmpty()) {
            p("is empty\n");
        }
        p("\n");

    }
    public void t91() {
        p("generate random undirected graph test\n");
        GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
        int numNodes = 10;
        boolean isDirected = false;
        boolean isDAG = false;
        boolean isSelfAllowed = true;
        int maxInbound = 3;
        int maxOutbound = 3;
        int numHeads = 2;
        int numTails = 2;
        GraphClass.Graph graph = algos.generateRandomGraph(
                numNodes, 
                isDirected, 
                isDAG, 
                isSelfAllowed,
                maxInbound, 
                maxOutbound, 
                numHeads, 
                numTails);
        graph.printGraph();
    }
    public void t92() {
        p("generate random directed acyclic graph test\n");
        GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
        int numNodes = 12;
        boolean isDirected = true;
        boolean isDAG = true;
        boolean isSelfAllowed = true;
        int maxInbound = 4;
        int maxOutbound = 4;
        int numHeads = 2;
        int numTails = 2;
        GraphClass.Graph graph = algos.generateRandomGraph(
                numNodes, 
                isDirected, 
                isDAG,
                isSelfAllowed,
                maxInbound, 
                maxOutbound, 
                numHeads, 
                numTails);
        graph.printGraph();
    }
    public void t93() {
        p("generate random directed graph test\n");
        GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
        int numNodes = 12;
        boolean isDirected = true;
        boolean isDAG = false;
        boolean isSelfAllowed = true;
        int maxInbound = 4;
        int maxOutbound = 4;
        int numHeads = 2;
        int numTails = 2;
        GraphClass.Graph graph = algos.generateRandomGraph(
                numNodes, 
                isDirected, 
                isDAG, 
                isSelfAllowed,
                maxInbound, 
                maxOutbound, 
                numHeads, 
                numTails);
        graph.printGraph();
    }
    public void t94() {
        p("rolling hash test\n");
        Strings.RollingHash rHash = new Strings.RollingHash(10);
    }
    public void t95() {
        p("Test priority queue insert and removes\n");
        Misc.MiscAPIs t = new Misc.MiscAPIs();
        t.testPriorityQueue();
    }
    public void t96() {
        p("Test priority queue insert and removes\n");
        Misc.MiscAPIs t = new Misc.MiscAPIs();
        t.testPriorityQueueObj1();
        p("\n\nNOW TEST OBJ2\n\n");
        t.testPriorityQueueObj2();
    }
    public void t97() {
        p("Test set copies\n");
        Misc.MiscAPIs t = new Misc.MiscAPIs();
        t.testSetCopies();
    }
    public void t98() {
        p("Min spanning tree case 1\n");
        /*
         * use MST for this undirected case:
         * 
         * 
         * 0---1   4---6
         * |\ /|\ / \ /|
         * | x | x   x |
         * |/ \|/ \ / \|
         * 2   3---5   7
         * 
         * 
         * 0   1   4   6
         * |\  |\   \ /|
         * | \ | \   x |
         * |  \|  \ / \|
         * 2   3   5   7
         * 
         */
        Map<Integer, Set<Integer>> map = new HashMap<>();
        {
            map.put(0, new HashSet<>(Arrays.asList(1,2,3)));
            map.put(1, new HashSet<>(Arrays.asList(0,2,3,5)));
            map.put(2, new HashSet<>(Arrays.asList(0,1)));
            map.put(3, new HashSet<>(Arrays.asList(0,1,4,5)));
            map.put(4, new HashSet<>(Arrays.asList(3,6,7)));
            map.put(5, new HashSet<>(Arrays.asList(1,3,6)));
            map.put(6, new HashSet<>(Arrays.asList(4,5,7)));
            map.put(7, new HashSet<>(Arrays.asList(4,6)));
        }
        GraphClass.Graph graph = new GraphClass.Graph(map);
        p("PRINT ORIGINAL\n");
        graph.printGraph();
        GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
        GraphClass.Graph graphMST = algos.getMST(graph);
        p("PRINT MST\n");
        graphMST.printGraph();
    }
    public void t99() {
        p("Min spanning tree case 2\n");
        /*
         * use MST for this undirected case:
         * 
         * 
         *    ___ ___
         *   /   x   \
         *  /   / \   \
         * 0---1---4---6
         * |\ /|\ /|\ /|
         * | x | x | x |
         * |/ \|/ \|/ \|
         * 2---3---5---7
         *  \      |  /
         *   \_____+_/
         * 
         * 
         * 0   1---4   6
         * |  /   /   /
         * | /   /   /  
         * |/   /   /   
         * 2   3   5---7
         *  \_____/
         * 
         * 
         */
        Map<Integer, Set<Integer>> map = new HashMap<>();
        {
            map.put(0, new HashSet<>(Arrays.asList(1,2,3,4)));
            map.put(1, new HashSet<>(Arrays.asList(0,2,3,4,5,6)));
            map.put(2, new HashSet<>(Arrays.asList(0,1,3,5,7)));
            map.put(3, new HashSet<>(Arrays.asList(0,1,2,4,5)));
            map.put(4, new HashSet<>(Arrays.asList(0,1,3,5,6,7)));
            map.put(5, new HashSet<>(Arrays.asList(1,2,3,5,6,7)));
            map.put(6, new HashSet<>(Arrays.asList(1,4,5,7)));
            map.put(7, new HashSet<>(Arrays.asList(2,4,5,6)));
        }
        GraphClass.Graph graph = new GraphClass.Graph(map);
        p("PRINT ORIGINAL\n");
        graph.printGraph();
        GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
        GraphClass.Graph graphMST = algos.getMST(graph);
        p("PRINT MST\n");
        graphMST.printGraph();
    }
    public void t100() {
        p("Fibonacci 21\n");
        int sz = 21;
        Misc.FibonacciHeaps t = new Misc.FibonacciHeaps();
        List<Integer> list = t.getFibonacci(sz);
        for(int i = 0; i < sz; i++) {
            p("%3d: %d\n", i, list.get(i));
        }
    }
    public void t101() {
        /*
         * topological sort
         * 
         * 0   1   2
         * |\  |  /|
         * | \ | / |
         * |  \|/  |
         * 3   4   5
         * |  /|   |  
         * | / |   |
         * |/  |   |
         * 6   |   |
         * |\  |  /
         * | \ | /
         * |  \|/
         * |   7
         * |  /|\
         * | / | \
         * |/  |  \
         * 8   9  10 
         * |   |\  |
         * |   | | |
         * |   | | |
         * 11  12|13
         *       | |
         *       | |
         *        \|
         *        14
         *     
         */
    }
    public void t102() {
        /*
         * Tarjan Bridge
         * 
         * 
         * 0---1---2
         *  \ / \ /
         *   x   x
         *  / \ / \
         * 3---4---5
         * |    
         * |  
         * |   
         * 6---7---8
         *  \  |  /|
         *   \ | / |
         *    \|/  |
         *     9  10
         * 
         * 
         * 0    
         * 1    
         * 2    
         * 3    
         * 4    
         * 5    
         * 6    
         * 7    
         * 8    
         * 9    
         * 10    
         * 
         */
        Map<Integer, Set<Integer>> map = new HashMap<>();
        {
            map.put(0, new HashSet<>(Arrays.asList(1,4)));
            map.put(1, new HashSet<>(Arrays.asList(0,2,3,5)));
            map.put(2, new HashSet<>(Arrays.asList(1,4)));
            map.put(3, new HashSet<>(Arrays.asList(1,4,6)));
            map.put(4, new HashSet<>(Arrays.asList(0,2,3,5)));
            map.put(5, new HashSet<>(Arrays.asList(1,4)));
            map.put(6, new HashSet<>(Arrays.asList(3,7,9)));
            map.put(7, new HashSet<>(Arrays.asList(6,8,9)));
            map.put(8, new HashSet<>(Arrays.asList(7,9,10)));
            map.put(9, new HashSet<>(Arrays.asList(6,7,8)));
            map.put(10, new HashSet<>(Arrays.asList(8)));
        }
        GraphClass.Graph graph = new GraphClass.Graph(map);
        p("PRINT ORIGINAL\n");
        graph.printGraph();
        GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
        algos.findBridge(graph);
    }
    public void t103() {
        p("matrix random test\n");
        Utils u = new Utils();
        int numRows = 5;
        int numCols = 9;
        int min = -10;
        int max = 10;
        List<List<Integer>> list = u.getRandomMatrix(numRows, numCols, min, max);
        for(int i = 0; i < list.size(); i++) {
            List<Integer> row = list.get(i);
            for(int j = 0; j < row.size(); j++) {
                p("%3d ", row.get(j));
            }
            p("\n");
        }
        p("\n");
    }
    public void t104() {
        p("findMinCostPath\n");
        int numRow = 5;
        int numCol = 6;
        Utils u = new Utils();
        List<List<Integer>> list = u.getRandomMatrix(numRow, numCol, 0, 9);
        u.printMatrix(list);
        Matrices.MiscAlgos algos = new Matrices.MiscAlgos();
        List<Matrices.Pair> listPairs = algos.findMinCostPath(list);
        p("\n");
        p("PRINT PATH\n");
        for(Matrices.Pair p: listPairs) {
            p("(%d,%d) ", p.x, p.y);
        }
        p("\n");
        p("\n");
    }
    public void t105() {
        // gaussian distribution test
        int min = 0;
        int max = 20;
        int num = 50000;
        HashMap<Integer, AtomicInteger> map = new HashMap<>();
        List<Integer> list = u.getIntListNormal(num, min, max);
        for(Integer v: list) {
            if(map.get(v) == null) {
                map.put(v, new AtomicInteger(1));
            }
            else {
                map.get(v).getAndIncrement();
            }
        }
        List<Integer> keys = new ArrayList<>(map.keySet());
        Collections.sort(keys);
        for(Integer k: keys) {
            p("%4d: cnt:%d\n", k, map.get(k).get());
        }
    }
    public void t106() {
        p("increasing subsequence list with noise\n");
        int size = 15;
        List<Integer> list = new ArrayList<>();
        int offset = 20;
        int noisepos = 5;
        int noiseneg = 5;
        for(int i = 0; i < size; i++) {
            int min = i - noiseneg + offset;
            int max = i + noisepos + offset;
            if(i != 0 && i % 5 == 0) {
                noisepos--;
                noiseneg += 3;
            }
            int v = u.getInt(min, max);
            list.add(v);
        }
        for(int i = 0; i < size; i++) {
            p("%3d ", list.get(i));
        }
        p("\n");
    }
    public void t107() {
        p("longest increasing subsequence\n");
        List<Integer> list = 
            Arrays.asList(23,25,18,19,28,28,21,22,20,27,30,24,30,36,29);
        Misc.MiscAlgos algos = new Misc.MiscAlgos();
        List<Integer> listLIS = 
            algos.longestIncreasingSubsequenceNaive(list);
        p("LIS input\n");
        System.out.println(list);
        p("LIS result\n");
        System.out.println(listLIS);
    }
    public void t108() {
        p("longest increasing subsequence\n");
        List<Integer> list = new ArrayList<>();
        int size   = 20;
        int noise = 5;
        for(int i = 0; i < size; i++) {
            int min = i - noise;
            int max = i + noise;
            int v = u.getInt(min, max);
            list.add(v);
        }
        Misc.MiscAlgos algos = new Misc.MiscAlgos();
        List<Integer> listLIS = algos.longestIncreasingSubsequenceNaive(list);
        p("LIS input\n");
        System.out.println(list);
        p("LIS result\n");
        System.out.println(listLIS);
    }
    public void t109() {
    	p("base64 encode\n");
    	try {
        	final String s = "The brown fox jumped over the fence.\r\n";
        	final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        	baos.write(s.getBytes(StandardCharsets.UTF_8));
        	final String nl = "\r\n";
        	baos.write(nl.getBytes(StandardCharsets.UTF_8));
        	byte [] baRaw = baos.toByteArray();
        	byte [] baB64 = Base64.getEncoder().encode(baRaw);
        	p("ORIGINAL: %s%s", s, nl);
        	p("RAW\n");
        	for(int i = 0; i < baRaw.length; i++) {
        		p("%2x ", baRaw[i]);
        	}
        	p("\n");
        	p("B64\n");
        	for(int i = 0; i < baB64.length; i++) {
        		p("%2x ", baB64[i]);
        	}
        	p("\n");
        	
        	baos.reset();
        	baos.write(s.getBytes(StandardCharsets.UTF_8));
        	baRaw = baos.toByteArray();
        	p("ENCODE DIFFERENTLY\n");
        	for(int i = 0; i < baRaw.length; i++) {
        		p("%2x ", baRaw[i]);
        	}
        	p("\n");
        	baB64 = Base64.getEncoder().encode(baRaw);
        	baos.reset();
        	baos.write(baB64);
        	baos.write(nl.getBytes(StandardCharsets.UTF_8));
        	baRaw = baos.toByteArray();
        	p("ENCODE DIFFERENTLY\n");
        	for(int i = 0; i < baRaw.length; i++) {
        		p("%2x ", baRaw[i]);
        	}
        	p("\n");
    	} catch(final IOException e) {
    		e.printStackTrace();
    	}
    }
    public void t110() {
    	p("t110 DAG graph\n");
    	GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
    	GraphClass.Graph g = new GraphClass.Graph();
    	int idsrc = 0;
    	int iddst = 9;
    	int numNodes = 11;
    	for(int i = 0; i < numNodes; i++) {
    		g.addNode(i);
    	}
    	g.addEdge(0,2,true);
    	g.addEdge(1,2,true);
    	g.addEdge(1,3,true);
    	g.addEdge(2,3,true);
    	g.addEdge(2,4,true);
    	g.addEdge(3,4,true);
    	g.addEdge(3,5,true);
    	g.addEdge(4,5,true);
    	g.addEdge(5,6,true);
    	g.addEdge(5,7,true);
    	g.addEdge(6,7,true);
    	g.addEdge(6,8,true);
    	g.addEdge(6,9,true);
    	g.addEdge(7,8,true);
    	g.addEdge(7,9,true);
    	g.addEdge(7,10,true);
    	g.addEdge(8,9,true);
    	g.addEdge(8,10,true);

    	g.printGraph();
    	List<Integer> lres;
    	lres = algos.topologicalSortRecursive(g);
    	if(lres == null) {
    		p("TOPOSORT IS NULL, NOT DAG\n");
    		return;
    	}
    	p("TOPOSORT\n");
    	for(int i = 0; i < lres.size(); i++) {
    		p("%d ", lres.get(i));
    	}
    	p("\n");
    	lres = algos.findBFSPath(g, idsrc, iddst);
    	p("BFS %d -> %d\n", idsrc, iddst);
    	for(int i = 0; i < lres.size(); i++) {
    		p("%d ", lres.get(i));
    	}
    	p("\n");
    }
    public void t111() {
    	p("t111 non DAG graph\n");
    	GraphClass.GraphAlgos algos = new GraphClass.GraphAlgos();
    	GraphClass.Graph g = new GraphClass.Graph();
    	int idsrc = 0;
    	int iddst = 9;
    	int numNodes = 11;
    	for(int i = 0; i < numNodes; i++) {
    		g.addNode(i);
    	}
    	g.addEdge(0,2,true);
    	g.addEdge(1,2,true);
    	g.addEdge(1,3,true);
    	g.addEdge(2,3,true);
    	g.addEdge(2,4,true);
    	g.addEdge(3,4,true);
    	g.addEdge(3,5,true);
    	g.addEdge(4,5,true);
    	g.addEdge(5,6,true);
    	g.addEdge(5,7,true);
    	g.addEdge(6,7,true);
    	g.addEdge(6,8,true);
    	g.addEdge(6,9,true);
    	g.addEdge(7,8,true);
    	g.addEdge(7,9,true);
    	g.addEdge(7,10,true);
    	g.addEdge(8,4,true);
    	g.addEdge(8,9,true);
    	g.addEdge(8,10,true);

    	g.printGraph();
    	List<Integer> lres;
    	lres = algos.topologicalSortRecursive(g);
    	if(lres == null) {
    		p("TOPOSORT IS NULL, NOT DAG\n");
    		return;
    	}
    	p("TOPOSORT\n");
    	for(int i = 0; i < lres.size(); i++) {
    		p("%d ", lres.get(i));
    	}
    	p("\n");
    	lres = algos.findBFSPath(g, idsrc, iddst);
    	p("BFS %d -> %d\n", idsrc, iddst);
    	for(int i = 0; i < lres.size(); i++) {
    		p("%d ", lres.get(i));
    	}
    	p("\n");
    }
    public void t112() {
        p("grouping\n");
        List<Integer> l = Arrays.asList(2,1,3,3,5,1,2,4,8,3,4,5);
        Misc.MiscAlgos t = new Misc.MiscAlgos();
        List<List<Integer>> ll = t.groupListMaxIncreasingSubGroups(l);
        p("INPUT: ");
        System.out.println(l);
        p("OUTPUT: \n");
        for(List<Integer> res: ll) {
            System.out.println(res);
        }
    }
    public void t113() {
        p("lis\n");
        int [] a = {2,1,3,3,5,1,2,4,8,3,4,5};
        Misc.MiscAlgos t = new Misc.MiscAlgos();
        List<Integer> res = t.lis(a);
    }
    public void t114() {
        Misc.MiscAlgos t = new Misc.MiscAlgos();
        List<List<Integer>> llist = new ArrayList<>();
        int sum = 4;
        List<Integer> input = Arrays.asList(1,2,3);
        t.change(sum, input, llist);
        for(List l: llist) {
            pl(l);
        }
    }
    public void t115() {
    	// iterative pre and post traversal
    }
    public void t116() {
    	// reverse leaves
    	class ReverseLeaves {
    		
    	}
    }
}
class Utils {
    char [] charset;
    static void print(String f, Object ...a) {
        System.out.printf(f, a);
    }
    static void p(String f, Object ...a) {
        print(f, a);
    }
    Random rand = new Random();
    Utils() {
        reset();
    }
    public int [][] copy(int [][] a) {
        int numRows = a.length;
        int numCols = a[0].length;
        int [][] o = new int[numRows][numCols];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                o[i][j] = a[i][j];
            }
        }
        return o;
    }
    public void copyList(List<Integer> src, List<Integer> dst) {
        dst.clear();
        for(Integer i: src)
            dst.add(i);
    }
    public List<List<Integer>> copy(List<List<Integer>> list) {
        int numRows = list.size();
        List<List<Integer>> o = new ArrayList<>();
        for(int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>(list.get(i));
            o.add(row);
        }
        return o;
    }
    public int [][] 
    initMatrixArray(int numRows, int numCols, int val) {
        int [][] a = new int[numRows][numCols];
        for(int i = 0; i < numRows; i++) {
            for(int j = 0; j < numCols; j++) {
                a[i][j] = val;
            }
        }
        return a;
    }
    public void printMatrix(int [][] a) {
        p("PRINT MATRIX\n");
    }
    public void printMatrix(List<List<Integer>> list) {
        p("PRINT MATRIX\n");
        int numRows = list.size();
        for(int i = 0; i < numRows; i++) {
            List<Integer> row = list.get(i);
            for(int j = 0; j < row.size(); j++) {
                p("%3d ", row.get(j));
            }
            p("\n");
        }
    }
    public List<Integer>
    initList(int numCols, int val) {
        List<Integer> list = new ArrayList<>();
        for(int i = 0; i < numCols; i++)
            list.add(val);
        return list;
    }
    public List<List<Integer>> 
    initMatrixList(int numRows, int numCols, int val) {
        List<List<Integer>> list = new ArrayList<>();
        for(int i = 0; i < numRows; i++) {
            List<Integer> row = initList(numCols, val);
            list.add(row);
        }
        return list;
    }
    public List<List<Integer>> 
    getRandomMatrix(int numRows, int numCols, int min, int max) {
        List<List<Integer>> list = new ArrayList<>(numRows);
        int diff = max - min + 1;
        for(int i = 0; i < numRows; i++) {
            List<Integer> cols = new ArrayList<>(numCols);
            for(int j = 0; j < numCols; j++) {
                int v = rand.nextInt(diff) + min;
                cols.add(v);
            }
            list.add(cols);
        }
        return list;
    }
    public List<List<Integer>>
    matrixToList(int [][] a) {
        List<List<Integer>> list = new ArrayList<>();
        int numRows = a.length;
        int numCols = a[0].length;
        for(int i = 0; i < numRows; i++) {
            List<Integer> row = new ArrayList<>();
            for(int j = 0; j < numCols; j++) {
                row.add(a[i][j]);
            }
            list.add(row);
        }
        return list;
    }
    public int [][]
    listToMatrix(List<List<Integer>> list) {
        int numRows = list.size();
        int numCols = list.get(0).size();
        int [][] a = new int[numRows][numCols];
        for(int i = 0; i < numRows; i++){ 
            List<Integer> row = list.get(i);
            for(int j = 0; j < numCols; j++) {
                a[i][j] = row.get(j);
            }
        }
        return a;
    }
    public String getRandString(String stringSet, int sizeStr) {
        StringBuilder sb = new StringBuilder();
        int sizeSet = stringSet.length();
        for(int j = 0; j < sizeStr; j++) {
            int idx = rand.nextInt(sizeSet);
            sb.append(stringSet.charAt(idx));
        }
        String word = sb.toString();
        return word;
    }
    public String getRandString(int sizeStr) {
        StringBuilder sb = new StringBuilder();
        int sizeSet = charset.length;
        for(int i = 0; i < sizeStr; i++) {
            int idx = rand.nextInt(sizeSet);
            sb.append(charset[idx]);
        }
        String word = sb.toString();
        return word;
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    Comparable min(Comparable ...c) {
        if(c.length == 0) {
            return null;
        }
        Comparable min = c[0];
        for(Comparable v: c) {
            if(min.compareTo(v) > 0) {
                min = v;
            }
        }
        return min;
    }
    @SuppressWarnings({ "rawtypes" })
    void printMinInts(Comparable ... c) {
        Comparable min = min(c);
        if(min == null) {
            p("null\n");
            return;
        }
        p("min:%2d; ", min);
        for(Comparable v: c) {
            p("%d ", v);
        }
        p("\n");
    }
    Integer getIntNotInList(List<Integer> l, int min, int max) {
        // gets a value that is within min:max and not in l, null if not found.
        Set<Integer> set = new HashSet<>();
        for(Integer v: l) {
            set.add(v);
        }
        for(int i = min; i < max; i++) {
            if(!set.contains(i)) {
                return i;
            }
        }
        return null;
    }
    void setCharset(String s) {
        charset = s.toCharArray();
    }
    char [] getCharset() {
        return charset;
    }
    void reset() {
        charset = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    }
    void shuffle(char [] a, int numShuffles) {
        for(int i = 0; i < numShuffles; i++) {
            shuffle(a);
        }
    }
    void shuffle(int [] a, int numShuffles) {
        for(int i = 0; i < numShuffles; i++) {
            shuffle(a);
        }
    }
    void shuffle(char [] a) {
        for(int i = a.length; i > 0; i--) {
            int idx = rand.nextInt(i);
            char c = a[idx];
            a[idx] = a[i-1];
            a[i-1] = c;
        }
    }
    void shuffle(int [] a) {
        for(int i = a.length; i > 0; i--) {
            int idx = rand.nextInt(i);
            int c = a[idx];
            a[idx] = a[i-1];
            a[i-1] = c;
        }        
    }
    /**
    * normal distribution of 0 is -10:10
    * 10 distribution is -50:50
    * 2 distribution is -10:10
    */
    int getIntNormal(int min, int max) {
        double range = (max - min + 1)/10.0;
        int median = (min + max) / 2;
        return getIntNormal(range, median, min, max);
    }
    int getIntNormal(double range, int median, int min, int max) {
        /*
        * double * 1    -> -5:5
        * double * 10   -> -50:50
        * double * 100  -> -500:500
        */
        boolean found = false;
        for(int i = 0; i < 1000 && !found; i++) {
            int v = (int)(rand.nextGaussian() * range) + median;
            if(min <= v && v <= max) {
                return v;
            }
        }
        return median;
    }
    List<Integer> getIntListNormal(int size, int min, int max) {
        /*
        * double * median = 1    -> -5:5
        * double * median = 10   -> -50:50
        * double * median = 100  -> -500:500
        * 
        */
        List<Integer> list = new ArrayList<>();
        double range = (max - min + 1)/10.0;
        int median = (min + max) / 2;
        for(int i = 0; i < size; i++) {
            int v = getIntNormal(range, median, min, max);
            list.add(v);
        }
        return list;
    }
    int getInt(int min, int max) {
        int range = max - min + 1;
        return rand.nextInt(range) + min;
    }
    int [] getAryInt(int sz, int min, int max) {
        int [] a = new int[sz];
        int range = max - min + 1;
        for(int i = 0; i < sz; i++) {
            a[i] = rand.nextInt(range) + min;
        }
        return a;
    }
    List<Integer> getListInt(int sz, int min, int max) {
        List<Integer> l = new ArrayList<>();
        int range = max - min + 1;
        for(int i = 0; i < sz; i++) {
            int v = rand.nextInt(range) + min;
            l.add(v);
        }
        return l;
    }
    char [] getAryChar(int sz) {
        char [] a = new char[sz];
        int range = charset.length;
        for(int i = 0; i < sz; i++) {
            a[i] = charset[rand.nextInt(range)];
        }
        return a;
    }
    void sort(char [] a) {
        quicksort(a, 0, a.length-1);
    }
    void quicksort(char [] a, int l, int h) {
        if(l > h) {
            return;
        }
        int idx = quicksortPartition(a, l, h);
        quicksort(a, l, idx-1);
        quicksort(a, idx+1, h);
    }
    int quicksortPartition(char [] a, int l, int h) {
        int m = (l + h) / 2;
        char c = a[m];
        while(true) {
            while(a[l] <= c && l < h) {
                l++;
            }
            while(a[h] >= c && h > l) {
                h--;
            }
            if(h <= l) {
                break;
            }
            swap(a, l, h);
            // this is to handle duplicates
            if(a[h] == a[l]) {
                l++;
            }
        }
        return h;
    }
    void sort(int [] a) {
        quicksort(a, 0, a.length-1);
    }
    void mergesort(int [] a) {
        int [] acopy = new int[a.length];
        copyArray(a, acopy);
        mergesort(a, acopy, 0, a.length-1);
        copyArray(acopy, a);
    }
    void mergesort(int [] a, int [] acopy, int idxBeg, int idxEnd) {
        if(idxBeg >= idxEnd) {
            return;
        }
        int idxM = (idxBeg + idxEnd) / 2;
        if((idxEnd - idxBeg) >= 2) {
            mergesort(a, acopy, idxBeg, idxM);
            mergesort(a, acopy, idxM+1, idxEnd);
        }
        mergesortMerge(a, acopy, idxBeg, idxM+1, idxEnd);
    }
    void mergesortMerge(int [] a, int [] acopy, int idxBeg, int idxM, int idxEnd) {
        if(idxBeg >= idxEnd) {
            return;
        }
        // idxBeg is beginning of subset array a
        // idxM is beginning of subset array b
        // get either a or b, whichever one is smallest, for sorting.
        int i = idxBeg;
        int j = idxM;
        print("BEFORE: B:%2d M:%2d E:%2d\n", idxBeg, idxM, idxEnd);
        print("BEFORE: ");
        printArray(a, idxBeg, idxEnd);
        for(int k = idxBeg; k <= idxEnd; k++) {
            if((i < idxM) && (j > idxEnd || a[i] <= a[j])) {
                acopy[k] = a[i++];
            }
            else {
                acopy[k] = a[j++];
            }
        }
        copyArray(acopy, a, idxBeg, idxEnd);
        print("AFTER:  ");
        printArray(a, idxBeg, idxEnd);
    }
    void printArray(int [] a, int idxB, int idxE) {
        for(int i = idxB; i <= idxE; i++) {
            print("%3d ", a[i]);
        }
        print("\n");
    }
    void copyArray(int [] a, int [] acopy) {
        for(int i = 0; i < a.length; i++) {
            acopy[i] = a[i];
        }
    }
    void copyArray(int [] a, int [] acopy, int idxBeg, int idxEnd) {
        for(int i = idxBeg; i <= idxEnd; i++) {
            acopy[i] = a[i];
        }
    }
    void quicksortHoare(int [] a, int l, int h) {
        if(h <= l) {
            return;
        }
        int idx = quicksortHoarePartition(a, l, h);
        quicksortHoare(a, l, idx-1);
        quicksortHoare(a, idx+1, h);
    }
    int quicksortHoarePartition(int [] a, int l, int h) {
        int m = (l+h)/2;
        int c = a[m];
        int i = l - 1;
        for(int j = l; j < h; j++) {
            if(a[i] <= c) {
                i++;
                swap(a, i, j);
            }
        }
        swap(a, i + 1, h);
        return i;
    }
    void quicksort(int [] a, int l, int h) {
        if(l > h) {
            return;
        }
        int idx = quicksortPartition(a, l, h);
        quicksort(a, l, idx-1);
        quicksort(a, idx+1, h);
    }
    int quicksortPartition(int [] a, int l, int h) {
        int m = (l + h) / 2;
        int c = a[m];
        while(true) {
            while(a[l] < c && l < h) {
                l++;
            }
            while(a[h] > c && h > l) {
                h--;
            }
            if(h <= l) {
                break;
            }
            swap(a, l, h);
            // this is to handle duplicates
            if(a[h] == a[l]) {
                l++;
            }
        }
        return h;
    }
    void swap(int [] a, int i, int j) {
        if(i < 0 || i >= a.length || j < 0 || j >= a.length) {
            return;
        }
        int c = a[i];
        a[i] = a[j];
        a[j] = c;
    }
    void swap(char [] a, int i, int j) {
        if(i < 0 || i >= a.length || j < 0 || j >= a.length) {
            return;
        }
        char c = a[i];
        a[i] = a[j];
        a[j] = c;
    }
}

class SConcurrency<T> {
    ExecutorService executorService = null;
    CallbackClass callbackClass = null;
    Random r = new Random();
    public static void p(String f, Object ...a) {
        System.out.printf(f, a);
    }
    public SConcurrency(int sizeThreadPool) {
        executorService = Executors.newFixedThreadPool(sizeThreadPool);
    }
    static class Multithreading {
        static class ACircularQueue<E> {
            int sz;
            List<E> l;
            ConcurrentHashMap<E, Integer> mapResults;
            Lock lock;
            Condition cnd;
            Condition cndWr;
            Condition cndRd;
            int h = -1;
            int t = 0;
            final TimeUnit tunit = TimeUnit.MILLISECONDS;
            public void print(String format, Object ... args) {
                System.out.printf(format, args);
            }
            public ACircularQueue(int size) {
                sz = size;
                l = new ArrayList<>(sz);
                for(int i = 0; i < sz; i++) {
                    l.add(null);
                }
                mapResults = new ConcurrentHashMap<>();
                lock = new ReentrantLock();
                cnd = lock.newCondition();
                cndWr = lock.newCondition();
                cndRd = lock.newCondition();
            }
            public void put(List<E> list, long expire, String id) {
                long timeRemaining = expire;
                long timePrv, timeCur;
                try {
                    lock.lock();
                    if(h == -1) {
                        h = 0;
                    }
                    for(int i = 0; i < list.size(); i++) {
                        while(((h + 1) % sz) == t) {
                            timePrv = System.currentTimeMillis();
                            cndWr.await(timeRemaining, tunit);
                            timeCur = System.currentTimeMillis();
                            timeRemaining = timeRemaining - (timeCur - timePrv);
                            if(timeRemaining <= 0) {
                                return;
                            }
                        }
                        E e = list.get(i);
                        l.set(h, e);
                        h = (h + 1) % sz;
                        cndRd.signalAll();
                    }
                } catch(Exception e) {
                } finally {
                    lock.unlock();
                }
            }
            public List<E> get(int size, long expire, String id) {
                List<E> list = new ArrayList<>();
                long timeRemaining = expire;
                long timePrv, timeCur;
                try {
                    lock.lock();
                    for(int i = 0; i < size; i++) {
                        while(h == -1 || t == h) {
                            timePrv = System.currentTimeMillis();
                            cndRd.await(timeRemaining, tunit);
                            timeCur = System.currentTimeMillis();
                            timeRemaining = timeRemaining - (timeCur - timePrv);
                            if(timeRemaining <= 0) {
                                return list;
                            }
                        }
                        E e = l.get(t);
                        t = (t + 1) % sz;
                        list.add(e);
                        cndWr.signalAll();
                    }
                } catch(Exception e) {
                } finally {
                    lock.unlock();
                }
                return list;
            }
            public void put(E e, String id) {
                try {
                    lock.lock();
                    if(h == -1) {
                        h = 0;
                    }
                    while(((h + 1) % sz) == t) {
                        cnd.await();
                    }
                    l.set(h, e);
                    h = (h + 1) % sz;
                    cnd.signalAll();
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                } catch(Exception ex) {
                    ex.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
            public E get(String id) {
                E e = null;
                try {
                    lock.lock();
                    while(h == -1 || t == h) {
                        cnd.await();
                    }
                    l.get(t);
                    t = (t + 1) % sz;
                    cnd.signalAll();
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                } catch(Exception ex) {
                    ex.printStackTrace();
                } finally {
                    lock.unlock();
                }
                return e;
            }
            public void putResult(E e) {
                mapResults.put(e, 1);
            }
            public ConcurrentHashMap<E, Integer> getResultMap() {
                return mapResults;
            }
        }

        static class ACircularQueueConsumer implements Runnable {
            ACircularQueue<Integer> q;
            String name;
            int szPerConsume;
            public ACircularQueueConsumer(
                    ACircularQueue<Integer> queue, String id, int sizePerConsume) {
                q = queue;
                name = id;
                szPerConsume = sizePerConsume;
            }
            @Override
            public void run() {
                boolean stop = false;
                while(!stop) {
                    List<Integer> l = q.get(szPerConsume, 10000, name);
                    if(l.size() == 0) {
                        stop = true;
                    }
                    for(Integer i: l) {
                        if(i == null) {
                            continue;
                        }
                        q.putResult(i);
                    }
                }
            }
        }

        static class ACircularQueueProducer implements Runnable {
            ACircularQueue<Integer> q;
            String name;
            int idxStart;
            int szProduce;
            public ACircularQueueProducer(
                    ACircularQueue<Integer> queue, 
                    String id, 
                    int startIndex, 
                    int sizeToProduce) {
                this.q = queue;
                name = id;
                idxStart = startIndex;
                szProduce = sizeToProduce;
            }
            @Override
            public void run() {
                int idxEnd = idxStart + szProduce;
                List<Integer> l = new ArrayList<>();
                for(int i = idxStart; i < idxEnd; i++) {
                    l.add(new Integer(i));
                }
                q.put(l, 10000, name);
            }
        }

        static class ASimpleSemaphore {
            boolean acquired = false;
            public ASimpleSemaphore() {
                
            }
            public void acquire() {
                try {
                    synchronized(this) {
                        while(acquired) {
                            wait();
                        }
                        acquired = true;
                        notifyAll();
                    }
                } catch(InterruptedException ex) {
                    ex.printStackTrace();
                } finally {
                    
                }
            }
            public synchronized void release() {
                try {
                    synchronized(this) {
                        while(!acquired) {
                            wait();
                        }
                        acquired = false;
                        notifyAll();
                    }
                } catch(InterruptedException ex) {
                    ex.printStackTrace(); 
                } finally {
                    
                }
            }
        }

        static class ASimpleSemaphoreUser implements Runnable {
            int numLoops;
            ASimpleSemaphore semaphore;
            String id;
            public static void print(String format, Object ... args) {
                System.out.printf(format, args);
            }
            public ASimpleSemaphoreUser(int numLoops, String id, ASimpleSemaphore semaphore) {
                this.numLoops = numLoops;
                this.semaphore = semaphore;
                this.id = id;
            }
            @Override
            public void run() {
                for(int i = 0; i < numLoops; i++) {
                    semaphore.acquire();
                    print("%s acquired %2d\n", id, i);
                    semaphore.release();
                    print("%s released %2d\n", id, i);
                }
            }
        }
    }
    public void createTasks(
            int numTasks, 
            AtomicInteger numFail, 
            AtomicInteger numPass, 
            Object caller) {
        callbackClass = new CallbackClass(numTasks, numFail, numPass, caller, this);
        List<FutureTask<T>> list = new ArrayList<>();
        for(int i = 0; i < numTasks; i++) {
            final int sleepInterval = r.nextInt(5);
            final int randNum = r.nextInt(numTasks);
            TaskCallable callable = 
                    new TaskCallable(randNum, sleepInterval, callbackClass);
            FutureTask<T> future = 
                    new FutureTask<>(callable);
            list.add(future);
        }
        for(FutureTask<T> future: list) {
            executorService.submit(future);
        }
    }
    public void callShutdown() {
        p("SHUTDOWN EXECUTORSERVICE\n");
        executorService.shutdown();
    }
    class TaskCallable implements Callable<T> {
        final CallbackClass callback;
        final int counter;
        final int sleepInterval;
        final Random r = new Random();
        @Override 
        public T call() {
            for(int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(sleepInterval);
                } catch(InterruptedException e) {
                }
            }
            for(int i = 0; i < counter; i++) {
                boolean isSleep = r.nextBoolean();
                if(isSleep) {
                }
            }
            final boolean isPass = counter %2 == 0;
            callback.update(isPass);
            return null;
        }
        public TaskCallable(int num, int sleepInterval, CallbackClass callback) {
            this.callback = callback;
            this.counter = num;
            this.sleepInterval = sleepInterval;
        }
    }
    static class TestConcurrency {
        public void t00() {
            int sizeThreadPool = 100;
            int sizeNumJobs = 10000;
            final AtomicInteger numFail = new AtomicInteger(0);
            final AtomicInteger numPass = new AtomicInteger(0);
            final SConcurrency<Integer> concurrent = new SConcurrency<>(sizeThreadPool);
            concurrent.createTasks(sizeNumJobs, numFail, numPass, this);
        }
        public void printPassFail(AtomicInteger numPass, AtomicInteger numFail) {
            p("TestConcurrency NUMPASS:%d NUMFAIL:%d\n", numPass.get(), numFail.get());
        }
        public void test() {
            t00();
        }
    }
    class CallbackClass {
        final AtomicInteger numFail;
        final AtomicInteger numPass;
        final AtomicInteger numTotal;
        final int sizeStop;
        final Object caller;
        final Object callerShutdown;
        public CallbackClass(
                int sizeStop, 
                AtomicInteger numFail, 
                AtomicInteger numPass,
                Object caller,
                Object callerShutdown) {
            this.sizeStop = sizeStop;
            this.numFail = numFail;
            this.numPass = numPass;
            this.caller = caller;
            this.callerShutdown = callerShutdown;
            numTotal = new AtomicInteger(0);
        }
        public void update(boolean isPass) {
            if(isPass) {
                numPass.incrementAndGet();
            } else {
                numFail.incrementAndGet();
            }
            int total = numTotal.incrementAndGet();
            if(total >= sizeStop) {
                if(caller instanceof TestConcurrency) {
                    TestConcurrency t = (TestConcurrency)caller;
                    t.printPassFail(numPass, numFail);
                }
                if(callerShutdown instanceof SConcurrency) {
                    ((SConcurrency<?>)callerShutdown).callShutdown();
                }
            }
        }
    }
}

class Search {
    public Integer binarySearch(List<Integer> l, Integer v) {
        Collections.sort(l);
        return binarySearch(l, v, 0, l.size() - 1);
    }
    private Integer binarySearch(List<Integer> l, Integer v, Integer min, Integer max) {
        int mid = (min + max) / 2;
        if(mid == min || mid == max) {
            return null;
        }
        Integer resultMid = l.get(mid);
        if(resultMid == v) {
            return mid;
        }
        if(resultMid < v) {
            return binarySearch(l, v, mid + 1, max);
        }
        else {
            return binarySearch(l, v, min, mid - 1);
        }
    }
    public List<Integer> getRange(List<Integer> l, Integer min, Integer max) {
        List<Integer> lreturn = new ArrayList<>();
        return lreturn;
    }
    static class Obj1 {
        int cost;
        int value;
        public Obj1(int cost, int value) {
            this.cost = cost;
            this.value = value;
        }
        public int cost() {
            return cost;
        }
        public int value() {
            return value;
        }
    }
    static class DynamicProgram {
        public List<Obj1> getHighestCost(List<Obj1> listin, int max) {
            return null;
        }
        public List<Obj1> getHighestValue(List<Obj1> listin, int max) {
            return null;
        }
    }
    static class NodePerson {
        Integer id;
        Integer intvalue;
        String firstname;
        String lastname;
        String stringvalue;
        public NodePerson(
                String firstname, 
                String lastname, 
                int intvalue, 
                String stringvalue) {
            this.firstname = firstname;
            this.lastname = lastname;
            this.intvalue = intvalue;
            this.stringvalue = stringvalue;
            this.id = firstname.hashCode() + lastname.hashCode() + intvalue + stringvalue.hashCode();
        }
    }
    static class NodeMachine {
        HashMap<Integer, NodePerson> map = new HashMap<>();
    }
    static class DistributedHashTable {
        LinkedHashMap<Integer, Integer> lhmap = new LinkedHashMap<>();
        Random rand = new Random();
        int numNodes = 0;
        public DistributedHashTable(int size) {
            numNodes = size;
        }
        public void init() {
            
        }
        public void putRandomItem() {
            
        }
        public void put(NodePerson nodePerson) {
            
        }
        protected void generatePrimeNumbers(int numPrimeNumbers) {
            
        }
        public void addNodes(int sz) {
            
        }
        public void put(String key, String value) {
            
        }
        public void put(String value) {
            
        }
        public void get(String key) {
            
        }
        public void deleteNodes(int sz) {
            
        }
        public void print() {
            
        }
    }
}


class Misc {
    Utils u = new Utils();
    public static void print(String f, Object ...o) {
        System.out.printf(f, o);
    }
    public static void p(String f, Object ...o) {
        System.out.printf(f, o);
    }
    static class MiscAlgos {
        public boolean debug = true;
        public boolean debugbasic = true;
        public int ctrglobal = 0;
        Utils u = new Utils();
        public List<Integer> lis(int [] a) {
            List<Integer> l = new ArrayList<>();
            int max = a.length;
            int [] aidx = new int[max];
            for(int i = 0; i < max; i++) {
                aidx[i] = 0;
            }
            for(int i = 0; i < max; i++) {
                int v = a[i];
                int idx = binarysearchGT(a, v);
                
            }
            return l;
        }
        public int binarysearchGT(int [] a, int v) {
            if(a.length == 0) {
                return 1;
            }
            int min = 0;
            int max = a.length - 1;
            while(min <= max) {
                int mid = (min+max)/2;
                if(a[mid] == v) {
                    return mid+1;
                }
                if(a[mid] < v) {
                }
            }
            return 0;
        }
        public List<List<Integer>> groupListMaxIncreasingSubGroups(List<Integer> l) {
            List<List<Integer>> ll = new ArrayList<>();
            List<List<Integer>> llref = new ArrayList<>();
            groupListMaxIncreasingSubGroups(l, 0, 0, ll, llref);
            return llref;
        }
        private boolean groupListMaxIncreasingSubGroups(
            List<Integer> l, 
            int idx, 
            int prvSum, 
            List<List<Integer>> ll,
            List<List<Integer>> llref) 
        {
            if(idx >= l.size())
                return true;
            int curSum = 0;
            List<Integer> lcur = new ArrayList<>();
            for(int i = idx; i < l.size(); i++) {
                int v = l.get(i);
                lcur.add(v);
                curSum += v;
                if(curSum < prvSum) {
                    continue;
                }
                ll.add(lcur);
                glmisgCopyList(ll, llref);
                boolean res = groupListMaxIncreasingSubGroups(
                    l, i+1, curSum, ll, llref);
                ll.remove(ll.size()-1);
                if(!res) {
                    return false;
                }
            }
            ll.add(lcur);
            glmisgCopyList(ll, llref);
            ll.remove(ll.size()-1);
            if(curSum < prvSum) {
                return false;
            }
            return true;
        }
        /** helper for groupListMaxIncreasingSubGroups */
        private void glmisgCopyList(
            List<List<Integer>> ll,
            List<List<Integer>> llref) 
        {
            if(ll.size() > llref.size()) {
                llref.clear();
                for(List<Integer> lcopy: ll) {
                    llref.add(lcopy);
                }
            }
        }
        public List<Integer> longestIncreasingSubsequenceNaive(List<Integer> listin) {
            AtomicInteger max = new AtomicInteger(0);
            List<Integer> list = new ArrayList<>();
            longestIncreasingSubsequenceNaive(0, listin, list, max);
            if(debug) {
                p("max is %d\n", max.get());
                System.out.println(list);
            }
            if(debugbasic) {
                p("runtime:%d\n", ctrglobal);
            }
            return list;
        }
        private void longestIncreasingSubsequenceNaive(
            int i,
            List<Integer> listin,
            List<Integer> list,
            AtomicInteger max
        ) {
            if(i >= listin.size())
                return;
            ctrglobal++;
            if(debug) {
                p("i:%4d: ", i);
                System.out.println(list);
            }
            
            if(i == 0) {
                list.add(listin.get(i));
                longestIncreasingSubsequenceNaive(i+1, listin, list, max);
            }
            else {
                int cur = listin.get(i);
                int prv = (list.size() == 0) ? cur-1 : list.get(list.size() - 1);
                if(prv < cur) {
                    // plain increasing
                    list.add(cur);
                    longestIncreasingSubsequenceNaive(i+1, listin, list, max);
                }
                else if(prv > cur) {
                    // see where to branch off by doing binary search for v < cur
                    // for now, just do linear search.
                    List<Integer> listnew = new ArrayList<>();
                    for(int k = 0; k < list.size(); k++) {
                        if(list.get(k) >= cur)
                            break;
                        listnew.add(list.get(k));
                    }
                    listnew.add(cur);
                    longestIncreasingSubsequenceNaive(i+1, listin, list, max);
                    longestIncreasingSubsequenceNaive(i+1, listin, listnew, max);
                    if(listnew.size() > list.size()) {
                        u.copyList(listnew, list);
                    }
                }
                else {
                    // equal, do not add
                    longestIncreasingSubsequenceNaive(i+1, listin, list, max);
                }
            }
            if(list.size() > max.get()) {
                max.set(list.size());
            }
        }
        public List<Integer> longestIncreasingSubsequenceNaiveSimpler(
            List<Integer> listin) {
            AtomicInteger max = new AtomicInteger(0);
            List<Integer> list = new ArrayList<>();
            return list;
        }
        public List<Integer> longestIncreasingSubsequenceNaiveDP(
            List<Integer> listin) {
            AtomicInteger max = new AtomicInteger(0);
            List<Integer> a = u.initList(listin.size(), 1);
            for(int i = 0; i < listin.size(); i++) {
                for(int j = 0; j < i; j++) {
                    if(listin.get(i) > listin.get(j) && 
                        a.get(i) < (a.get(j) + 1)) 
                    {
                        a.set(i, a.get(j) + 1);
                    }
                }
            }
            for(int i = 0; i < listin.size(); i++) {
                if(max.get() < a.get(i))
                    max.set(a.get(i));
            }
            return a;
        }
        public List<Integer> longestIncreasingSubsequenceNaiveLogN(
            List<Integer> listin) {
            AtomicInteger max = new AtomicInteger(0);
            List<Integer> list = new ArrayList<>();
            return list;
        }

        public List<Integer> longestIncreasingSubsequence(List<Integer> listin) {
            /*
            * use a tree representation to look at sequences
            * then use overlapping subsets
            *
            * --------------------------------------------------------------
            * eg: 25  20  22  20  26  21  27  35  31  23  25  25  31  32  36
            *     ----------------------------------------------------------
            *      0   1   2   3   4   5   6   7   8   9  10  11  12  13  14
            *     ----------------------------------------------------------
            *     25              26      27+ 35                          36
            *                               +-----31                  32  36
            *     
            *           20  22+     26      27+ 35
            *               |               +-----31                  32  36  7 winner
            *               +-------------------------23  25      31  32  36  7 winner
            *     
            *         20              21+         31                  32  36
            *                           +-------------23  25      31  32  36  7 winner
            *     
            * --------------------------------------------------------------
            * eg: 18  20  24  27  24  22  24  28  21  35  22  37  43  39  28
            *     ----------------------------------------------------------
            *      0   1   2   3   4   5   6   7   8   9  10  11  12  13  14
            *     ----------------------------------------------------------
            *     18  20+ 24  27              28      35      37+ 43
            *           |                                       +-----39
            *           |                                       
            *           +-------------22  24  28      35      37+ 43
            *           |                                       +-----39
            *           |                                       
            *           +-------------------------21+ 35      37+ 43
            *                                       |           +-----39
            *                                       |           
            *                                       +-----22  37+ 43
            *                                                   +-----39
            *                                                   
            * --------------------------------------------------------------
            * eg: 23  25  18  19  28  28  21  22  20  27  30  24  30  36  29
            *     ----------------------------------------------------------
            *      0   1   2   3   4   5   6   7   8   9  10  11  12  13  14
            *     ----------------------------------------------------------
            *     23  25+         28                      30          36
            *           +-----------------------------27  30          36
            *     
            *             18  19+ 28                      30          36
            *                   +---------21  22+     27  30          36     7 winner
            *                   |               +-------------24  30  36
            *                   +-----------------20+ 27  30          36
            *                                       +---------24  30  36
            *     
            */
            List<Integer> list = longestIncreasingSubsequenceNaive(listin);
            return list;
        }
        public List<Integer> longestCommonSubsequence(List<Integer> list1, List<Integer> list2) {
            /*
             * use DP and visualize as tree
             * 
             * list1:  2 1 3 2 7 3 1 4 9 7 5 6 2 7 2 8 1 9
             * list2   4 3 4 8 5 2 6 7 4 7 3 8 3 9
             * 
             *       00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17
             *        2  1  3  2  7  3  1  4  9  7  5  6  2  7  2  8  1  9
             *       -----------------------------------------------------
             * 00 4|  0  0  0  0  0  0  0  1  1  1  1  1  1  1  1  1  1  1                       
             * 01 3|  0  0  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1  1                       
             * 02 4|  0  0  1  1  1  1  1  2  2  2  2  2  2  2  2  2  2  2                     
             * 03 8|  0  0  1  1  1  1  1  2  2  2  2  2  2  2  2  3  3  3                                   
             * 04 5|  0  0  1  1  1  1  1  2  2  2  3  3  3  3  3  3  3  3                     
             * 05 2|  1  1  1  2  2  2  2  2  2  2  3  3  3  3  4  4  4  4                     
             * 06 6|  1  1  1  2  2  2  2  2  2  2  3  4  4  4  4  4  4  4                     
             * 07 7|  1  1  1  2  3  3  3  3  3  3  3  4  4  5  5  5  5  5                     
             * 08 4|  1  1  1  2  3  3  3  4  4  4  4  4  4  5  5  5  5  5  	                   
             * 09 7|  1  1  1  2  3  3  3  4  4  5  5  5  5  5  5  5  5  5   3 2 7 4 7 
             * 10 3|  1  1  2  2  3  4  4  4  4  5  5  5  5  5  5  5  5  5                        
             * 11 8|  1  1  2  2  3  4  4  4  4  5  5  5  5  5  5  6  6  6   3 2 7 4 7 8                 
             * 12 3|  1  1  2  2  3  4  4  4  4  5  5  5  5  5  5  6  6  6                    
             * 13 9|  1  1  2  2  3  4  4  4  5  5  5  5  5  5  5  6  6  7   3 2 7 4 7 8 9      
             * 
             * 
             * can DP be represented in tree?
             * 
             * list1 1 2 3 8 4 5
             * list2 2 1 2 4 5
             * should be 1 2 4 5
             * 
             * 
             * 1 2 3 8 4 5
             *   2     4 5
             * 1 2     4 5
             * 
             */
            List<Integer> list = new ArrayList<>();
            return list;
        }
        /**
        * 2,3,4,5,6,7,8,9,11     sum 20
        *   
        *       02  03  04  05  06  07  08  09  11
        *   02  02  05  06  07  08  09  10  11  13
        *   03  05  03  07  08  09  10  11  12  14
        *   04  06  07  04  09  10  11  12  13  15
        *   05  07  08  09  05  11  12  13  14  15
        *   06  08  09  10  11  06  13  14  15  17
        *   07  09  10  11  12  13  07  15  16  18
        *   08  10  11  12  13  14  15  08  17  19
        *   09  11  12  13  14  15  16  17  09  20
        *   11  13  14  15  16  16  18  19  20  11
        *
        *
        *       02  03  04  05  06  07  08  09  11
        *   02  02  
        *   03  05  03  
        *   04  06  07  04  
        *   05  07  08  09  05  
        *   06  08  09  10  11  06  
        *   07  09  10  11  12  13  07  
        *   08  10  11  12  13  14  15  08  
        *   09  11  12  13  14  15  16  17  09  
        *   11  13  14  15  16  16  18  19  20  11
        */
        public List<List<Integer>> subsetSum(List<Integer> listin, int sum) {
            List<List<Integer>> llist = new ArrayList<>();
            return llist;
        }
        public void change(
            int sum,
            List<Integer> input,
            List<List<Integer>> llist) 
        {
            changeIterative(sum, input, llist);
        }
        private void changeIterative(
            int sum,
            List<Integer> input,
            List<List<Integer>> llist) 
        {
            class StackData {
                public int tmpsum = 0;
                public int idx = 0;
                public int ctr = 0;
                public StackData(int tmpsum, int idx, int ctr) {
                    this.tmpsum = tmpsum;
                    this.idx = idx;
                    this.ctr = ctr;
                }
            }
            int size = input.size();
            Stack<StackData> stack = new Stack<>();
            List<Integer> list = new ArrayList<>();
        
            /*
            * id    slevel  tmpsum  idx ctr list
            * 0     0       0       0   0   N
            * 1     1       1       0   0   1
            * 2     2       2       0   0   1 1
            * 3     3       3       0   0   1 1 1
            * 4     4       4       0   0   1 1 1 1
            * 3     3       3       0   1   1 1 1 2
            * 
            */
            int testctr = 0;
            stack.push(new StackData(0,0,0));
            while(stack.size() != 0 && testctr < 50) {
                StackData data = stack.pop();
                p("data tmpsum:%2d idx:%2d ctr:%2d\n", data.tmpsum, data.idx, data.ctr);
                System.out.println(list);
                if(data.tmpsum == sum) {
                    llist.add(new ArrayList<>(list));
                    list.remove(list.size()-1);
                }
                else if (data.tmpsum < sum) {
                    int v = input.get(data.idx);
                    int tmpsum = data.tmpsum + v;
                    if(tmpsum <= sum) {
                        int ctr = data.ctr;
                        list.add(v);
                        if((ctr+1) < size) {
                            data.ctr++;
                            stack.push(data);
                            stack.push(new StackData(tmpsum, ctr, ctr));
                        }
                        else {
                            list.remove(list.size()-1);
                        }
                    }
                }
                testctr++;
                p("testctr %d\n", testctr);
            }
        }
    }
    /**
     * Fibonacci Heap is a collection of trees satisfying
     * min heap property. This enables lazy merge.
     * 
     * Merge is done by concatenating two lists of trees.
     * Decrease key sometimes cuts a node from parent and
     * forms new tree.
     *
     */
    static class FibonacciHeaps {
        public List<Integer> getFibonacci(int size) {
            List<Integer> list = new ArrayList<>();
            int prv0 = 0;
            int prv1 = 1;
            int cur;
            for(int i = 0; i < size; i++) {
                list.add(prv0);
                cur = prv0 + prv1;
                prv0 = prv1;
                prv1 = cur;
            }
            return list;
        }
    }
    static class MiscAPIs {
        class CmpObj1 implements Comparable<CmpObj1> {
            Integer i;
            String s;
            boolean isMaxCmp;
            public CmpObj1(int i, String s) {
                this(i, s, false);
            }
            public CmpObj1(int i, String s, boolean isMaxCmp) {
                this.i = i;
                this.s = s;
                this.isMaxCmp = isMaxCmp;
            }
            public int compareTo(CmpObj1 obj) {
                return (isMaxCmp) ? (this.i - obj.getInt()) : (obj.getInt() - this.i);
            }
            public int getInt() {
                return i;
            }
            public void print() {
                p("id:%2d name:%s\n", i, s);
            }
        }
        public void testPriorityQueueObj1() {
            //p("Test priority queue insert and removes\n");
            List<Integer> list = Arrays.asList(8,10,4,6,7,5,1,3,9,2);
            List<CmpObj1> listObj = new ArrayList<>();
            System.out.println(list);
            for(Integer i: list) {
                CmpObj1 o = new CmpObj1(i, null);
                listObj.add(o);
            }
            PriorityQueue<CmpObj1> q = new PriorityQueue<>(5);
            for(CmpObj1 i: listObj) {
                q.add(i);
            }
            CmpObj1 v;
            while((v = q.poll()) != null) {
                p("%2d ", v.getInt());
            }
            p("\n");
            p("\n");
            p("Now change priority\n");
            q = new PriorityQueue<>(5, new Comparator<CmpObj1>() {
                public int compare(CmpObj1 v0, CmpObj1 v1) {
                    return v1.getInt() - v0.getInt();
                }
            });
            for(CmpObj1 i: listObj) {
                q.add(i);
            }
            while((v = q.poll()) != null) {
                p("%2d ", v.getInt());
            }
            p("\n");
            p("\n");
            p("Now change priority again\n");
            q = new PriorityQueue<>(5, new Comparator<CmpObj1>() {
                public int compare(CmpObj1 v0, CmpObj1 v1) {
                    return v0.getInt() - v1.getInt();
                }
            });
            for(CmpObj1 i: listObj) {
                q.add(i);
            }
            while((v = q.poll()) != null) {
                p("%2d ", v.getInt());
            }
        }
        class Obj2 {
            Integer i;
            String s;
            public Obj2(int i, String s) {
                this.i = i;
                this.s = s;
            }
            public int getInt() {
                return i;
            }
            public void print() {
                p("id:%2d name:%s\n", i, s);
            }
        }
        public void testPriorityQueueObj2() {
            //p("Test priority queue insert and removes\n");
            List<Integer> list = Arrays.asList(8,10,4,6,7,5,1,3,9,2);
            List<Obj2> listObj = new ArrayList<>();
            System.out.println(list);
            for(Integer i: list) {
                Obj2 o = new Obj2(i, null);
                listObj.add(o);
            }
            
            PriorityQueue<Obj2> q;
            
            q = new PriorityQueue<>(5, new Comparator<Obj2>() {
                public int compare(Obj2 v0, Obj2 v1) {
                    return v0.getInt() - v1.getInt();
                }
            });
            for(Obj2 i: listObj) {
                q.add(i);
            }
            Obj2 v;
            while((v = q.poll()) != null) {
                p("%2d ", v.getInt());
            }
            p("\n");
            p("\n");
            
            p("Now change priority\n");
            q = new PriorityQueue<>(5, new Comparator<Obj2>() {
                public int compare(Obj2 v0, Obj2 v1) {
                    return v1.getInt() - v0.getInt();
                }
            });
            for(Obj2 i: listObj) {
                q.add(i);
            }
            while((v = q.poll()) != null) {
                p("%2d ", v.getInt());
            }
            p("\n");
            p("\n");
        }
        public void testPriorityQueue() {
            //p("Test priority queue insert and removes\n");
            List<Integer> list = Arrays.asList(8,10,4,6,7,5,1,3,9,2);
            System.out.println(list);
            PriorityQueue<Integer> q = new PriorityQueue<>(list);
            Integer v;
            while((v = q.poll()) != null) {
                p("%2d ", v);
            }
            p("\n");
            p("\n");
            p("Now change priority\n");
            q = new PriorityQueue<>(5, new Comparator<Integer>() {
                public int compare(Integer v0, Integer v1) {
                    return v1 - v0;
                }
            });
            for(Integer i: list) {
                q.add(i);
            }
            while((v = q.poll()) != null) {
                p("%2d ", v);
            }
            p("\n");
            p("\n");
            p("Now change priority again\n");
            q = new PriorityQueue<>(5, new Comparator<Integer>() {
                public int compare(Integer v0, Integer v1) {
                    return v0 - v1;
                }
            });
            for(Integer i: list) {
                q.add(i);
            }
            while((v = q.poll()) != null) {
                p("%2d ", v);
            }
        }
        public void testSetCopies() {
            /**
             * testSetCopies takes a list, copies it, and starts deleting from copy.
             * Afterward, check that copy is different from original set, which 
             * still has full set of entries.
             */
            List<Integer> list = Arrays.asList(0,1,2,3,4,5,6,7,8,9);
            Set<Integer> set = new HashSet<>(list);
            Set<Integer> setCopy = new HashSet<>(set);
            
            p("remove 9,8,7,0,1,2\n");
            setCopy.remove(9);
            setCopy.remove(8);
            setCopy.remove(7);
            setCopy.remove(0);
            setCopy.remove(1);
            setCopy.remove(2);
            
            p("print original set: ");
            System.out.println(set);
            p("print modified set: ");
            System.out.println(setCopy);
        }
    }
    public int convertRomanLessThan10(String s) {
        if(s.equals("I")) {
            return 1;
        } else if(s.equals("II")) {
            return 2;
        } else if(s.equals("III")) {
            return 3;
        } else if(s.equals("IV")) {
            return 4;
        } else if(s.equals("V")) {
            return 5;
        } else if(s.equals("VI")) {
            return 6;
        } else if(s.equals("VII")) {
            return 7;
        } else if(s.equals("VIII")) {
            return 8;
        } else if(s.equals("IX")) {
            return 9;
        } else if(s.equals("X")) {
            return 10;
        }
        return 0;
    }

    public void printComb(int [] a, int i, List<Integer> list) {
        for(int j = i; j < a.length; j++) {
            list.add(a[j]);
            for(int k = 0; k < list.size(); k++) {
                print("%2d ", list.get(k));
            }
            print("\n");
            printComb(a, j+1, list);
            list.remove(list.size() - 1);
        }
    }
    public int change(int amount, int [] coins) {
        LinkedList<Integer> ll = new LinkedList<>();
        int cnt = countChange(amount, coins, 0, ll, 0);
        return cnt;
    }
    private int countChange(int amount, int [] coins, int idxCoin, 
        LinkedList<Integer> ll, int dbg_level) {
        int cnt = 0;
        for(int i = idxCoin; i < coins.length; i++) {
            int curAmount = amount - coins[i];
            if(curAmount < 0) {
                continue;
            }
            //print("dbg_level:%2d push c:%2d amount:%2d idx:%2d\n", 
            //    dbg_level, coins[i], amount, idxCoin);
            ll.push(coins[i]);
            if(curAmount == 0) {
                cnt++;
                print("dbg_level:%2d ", dbg_level);
                int tmpVal = 0;
                for(int k = ll.size()-1; k >= 0; k--) {
                    int tmpCoin = ll.get(k).intValue();
                    tmpVal += tmpCoin;
                    print("c,v:%d,%d ", tmpCoin, tmpVal);
                }
                print("\n");
            } else {
                cnt += countChange(curAmount, coins, i, ll, dbg_level+1);
            }
            ll.pop();
        }
        return cnt;
    }

    public void testOpenReadWriteCloseFiles() {
        /**
        * test reading existing files serially, file if not exist.
        * test write new file, which is copy of existing file, close it.
        * then read the new file, write to it, then close it.
        */
    }
    public String testWriteTestFile(String s) throws IOException {
        /**
         * Write format file:
         * \s*\d+:\d+,\d+,\d+\n
         * \s*\d+:\d+,\d+,\d+\n
         * \s*\d+:\d+,\d+,\d+\n
         */
        final int numlines = 20;
        final int numIntPerLine = 10;
        String filename = s;
        File file = new File(s);
        int ctr = 0;
        while(file.exists()) {
            filename = String.format("%s.%d", s, ctr++);
            file = new File(filename);
        }
        PrintWriter printWriter = null;
        FileWriter fileWriter = null;
        ctr = 0;
        try {
            fileWriter = new FileWriter(file);
            printWriter = new PrintWriter(fileWriter);
            for(int i = 0; i < numlines; i++) {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("%3d:", i+1));
                for(int j = 0; j < numIntPerLine; j++) {
                    String tmp = (j == 0) ?
                        String.format("%d", ctr) :
                        String.format(",%d", ctr);
                    sb.append(tmp);
                    ctr++;
                }
                sb.append("\n");
                printWriter.print(sb.toString());
            }
        } catch(Exception e) {
        } finally {
            if(printWriter != null) 
                printWriter.close();
            if(fileWriter != null)
                fileWriter.close();
        }
        return filename;
    }
    public void testReadTestFile(String s) throws IOException {
        /**
         * Read format file:
         * \s*\d+:\d+,\d+,\d+\n
         * \s*\d+:\d+,\d+,\d+\n
         * \s*\d+:\d+,\d+,\d+\n
         */
        File file = new File(s);
        if(!file.exists()) {
            p("File %s does not exist\n", s);
            return;
        }
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(file);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            int ctr = 0;
            int numLines = 0;
            while((line = bufferedReader.readLine()) != null) {
                String [] as = line.split(":");
                if(as.length != 2) {
                    String errmsg = 
                            String.format("ERR parsing %s:%s", s, line);
                    throw new Exception(errmsg);
                }
                String s1 = as[0].trim();
                String s2 = as[1].trim();
                as = s2.split(",");
                p("%s\n",line);
                numLines = Integer.parseInt(s1);
                ctr += as.length;
            }
            p("READ FILE %s\n", s);
            p("numlines:%3d numNumbers:%d\n", numLines, ctr);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(fileReader != null)
                fileReader.close();
            if(bufferedReader != null)
                bufferedReader.close();
        }
    }
    public void testWriteFileFromMemoryBuffer(String s) {
    }
    public void testOpenRWCloseFileSerial() {
    }
    public void testOpenRWCloseFileRandom() {
    }
    public void testOpenRWCloseFileInMemoryRandom() {
    }
    public void distributionGaussian() {
        /* gaussian distribution of 0-99 with cluster around 50. */
        Random r = new Random();
        //final int min = 0;
        //final int max = 100;
        final int median = 50;
        final int numcases = 1000000;
        TreeMap<Integer, AtomicInteger> m = new TreeMap<>();

        // test gaussian alone to see distribution
        {
            for(int i = 0; i < numcases; i++){
                // for 10, it is -50:50
                // so adding median of 50 -> 0:100 w/ standard deviation
                int v = (int)(r.nextGaussian() * 10.0) + median;
                AtomicInteger ai = m.get(v);
                if(ai == null){ 
                    ai = new AtomicInteger(1);
                    m.put(v, ai);
                } else {
                    ai.getAndIncrement();
                }
            }
            //p("DISTRIBUTION GAUSSIAN\n");
            // sort the keys then get
            //Set<Integer> keys = m.keySet();
            //for(Integer k: keys) {
            //    //p("KEY:%5d CTR:%d\n", k, m.get(k).get());
            //}
        }
        m.clear();
        // how do you make a smaller distribution?
        {
            for(int i = 0; i < numcases; i++){
                // for 10, it is -50:50, add median 50 -> 0:100
                // for 2, it should be -10:10, add median 10 -> 0:20
                // to get 0:10 do 1, add median 5 -> 0:10
                int v = (int)(r.nextGaussian() * 2.0) + 10;
                AtomicInteger ai = m.get(v);
                if(ai == null){ 
                    ai = new AtomicInteger(1);
                    m.put(v, ai);
                } else {
                    ai.getAndIncrement();
                }
            }
            p("DISTRIBUTION GAUSSIAN\n");
            // sort the keys then get
            Set<Integer> keys = m.keySet();
            for(Integer k: keys) {
                p("KEY:%5d CTR:%d\n", k, m.get(k).get());
            }
 
        }
        m.clear();
        // how do you make a smaller distribution?
        {
            for(int i = 0; i < numcases; i++){
                // for 10, it is -50:50, add median 50 -> 0:100
                // for 2, it should be -10:10, add median 10 -> 0:20
                // to get 0:10 do 1, add median 5 -> 0:10
                int v = (int)(r.nextGaussian() * 1.0) + 5;
                AtomicInteger ai = m.get(v);
                if(ai == null){ 
                    ai = new AtomicInteger(1);
                    m.put(v, ai);
                } else {
                    ai.getAndIncrement();
                }
            }
            p("DISTRIBUTION GAUSSIAN\n");
            // sort the keys then get
            Set<Integer> keys = m.keySet();
            for(Integer k: keys) {
                p("KEY:%5d CTR:%d\n", k, m.get(k).get());
            }
 
        }

    }
    public void distributionUniform() {
        /** test uniform distribution. */
        Random r = new Random();
        final int max = 10;
        final int numcases = 1000000;
        TreeMap<Integer, AtomicInteger> m = new TreeMap<>();

        {
            for(int i = 0; i < numcases; i++){
                int v = r.nextInt(max);
                AtomicInteger ai = m.get(v);
                if(ai == null){ 
                    ai = new AtomicInteger(1);
                    m.put(v, ai);
                } else {
                    ai.getAndIncrement();
                }
            }
            p("DISTRIBUTION UNIFORM\n");
            // sort the keys then get
            Set<Integer> keys = m.keySet();
            for(Integer k: keys) {
                int cnt = m.get(k).get();
                double pct = (double)cnt / (double)numcases;
                p("KEY:%5d CTR:%10d PCT:%.2f\n", k, cnt, pct);
            }
        }
 
    }
    public void testCRCDistribution(int numStrings, int numNodes) {
        Utils u = new Utils();
        Map<Long, String> m = new HashMap<>();
        int szmin = 10;
        int szmax = 20;
        CRC32 crc = new CRC32();
        for(int i = 0; i < numStrings; i++) {
            int sz = u.getInt(szmin, szmax);
            String s = u.getRandString(sz);
            crc.reset();
            crc.update(s.getBytes());
            long crcval = crc.getValue();
            m.put(crcval, s);
        }
        // now find distribution, given n nodes
        long longmax = Integer.MAX_VALUE;
        long szPartition = longmax / numNodes;
        int [] a = new int[numNodes];
        long [] tablePartition = new long[numNodes];
        long szPartitionMax = szPartition;
        // initialize tables
        for(int i = 0; i < numNodes; i++) {
            a[i] = 0;
            tablePartition[i] = szPartitionMax;
            szPartitionMax += szPartition;
        }
        // count frequency of keys into buckets
        for(Map.Entry<Long, String> kv: m.entrySet()) {
            long k = kv.getKey();
            //p("key:0x%x\n", k);
            for(int i = 0; i < numNodes; i++) {
                if(k < tablePartition[i]) {
                    a[i]++;
                    break;
                }
            }
        }
        p("TABLE DENSITY CRC DISTRIBUTION\n");
        p("PARTITION SIZE: 0x%x NUMNODES:%d\n", szPartition, numNodes);
        for(int i = 0; i < numNodes; i++) {
            p("table %2d density:%d\n", i, a[i]);
        }
    }
    static class Line {
        public int x1, x2;
        public int y1, y2;
        public static void print(String format, Object ...args) {
            System.out.printf(format, args);
        }
        public Line(int x1, int y1, int x2, int y2) {
            // autoadjust x1 > x2
            if(x1 > x2) {
                this.x1 = x1;
                this.x2 = x2;
                this.y1 = y1;
                this.y2 = y2;
            }
            else {
                this.x2 = x1;
                this.x1 = x2;
                this.y2 = y1;
                this.y1 = y2;
            }
        }
        public void print() {
            print("x1:%3d y1:%3d x2:%3d y2:%3d\n", x1,y1,x2,y2);
        }
        public boolean intersects(Line l) {
            boolean isXIntersect = false;
            boolean isYIntersect = false;
            print("x1:%2d x2:%2d l.x1:%2d l.x2:%2d\n", x1, x2, l.x1, l.x2);
            print("y1:%2d y2:%2d l.y1:%2d l.y2:%2d\n", y1, y2, l.y1, l.y2);
            // x1 > x2 for both, so check if
            // l.x1 between this x1 and x2 or l.x2 between this x1 and x2 or
            // this x1 between l.x1 and l.x2 or this x2 between l.x1 and l.x2
            if((x1 >= l.x1 && x2 <= l.x1) || (x1 >= l.x2 && x2 <= l.x2) ||
               (x1 <= l.x1 && x1 >= l.x2) || (x2 <= l.x1 && x2 >= l.x2)) {
                isXIntersect = true;
            }
            // y1 no order to y2 so do 8 checks:
            // l.y1 between y1 and y2 -> 2 cases
            // l.y2 between y1 and y2 -> 2 cases
            // y1 between l.y1 and l.y2 -> 2 cases
            // y2 between l.y1 and l.y2 -> 2 cases
            if((y1 >= l.y1 && y2 <= l.y1) || (y1 >= l.y2 && y2 <= l.y2) ||
               (y2 >= l.y1 && y1 <= l.y1) || (y2 >= l.y2 && y1 <= l.y2) ||
               (y1 <= l.y1 && y1 >= l.y2) || (y2 <= l.y1 && y2 >= l.y2) ||
               (y2 <= l.y1 && y2 >= l.y2) || (y1 <= l.y1 && y1 >= l.y2)) {
                isYIntersect = true;
            }
            print("xintersect:%s yintersect:%s\n", isXIntersect, isYIntersect);
            boolean isIntersect = isXIntersect && isYIntersect;
            return isIntersect;
        }
    }
    static class Ledger {
        /*
        * src is obj that credits to dst, or debits self
        * dst is obj that debits from dst, or credits self
        * owes method is where caller credits the dst, but not yet completed.
        * owed method is where called debits the dst, but is pending.
        * credit is where caller is credited
        * debit is where caller is debited
        */
        public static void p(String f, Object ...a) {
            System.out.printf(f, a);
        }
        public static class Person {
            public String name;
            public int credit;
            public List<Transaction> listPendingCredit;
            public List<Transaction> listPendingDebit;
            public List<Transaction> listCompleted;
            public Person(String name) {
                this.name = name;
                credit = 0;
                resetAllTransactions();
            }
            public void print() {
                printSummary();
                printTransactions();
            }
            public void printSummary() {
                p("%6s credit:%d\n", name, credit);
            } 
            public void printTransactions() {
                p(">>> pending credits\n");
                for(Transaction t: listPendingCredit) {
                    t.print();
                }
                p(">>> pending debits\n");
                for(Transaction t: listPendingDebit) {
                    t.print();
                }
                p("------------------------------\n");
            }
            public void setCredit(int credit) {
                this.credit = credit;
            }
            public void resetPendingTransactions() {
                listPendingCredit = new LinkedList<>();
                listPendingDebit= new LinkedList<>();
            }
            public void resetAllTransactions() {
                resetPendingTransactions();
                listCompleted = new LinkedList<>();
            }
            /* credit this person. */
            public void addPendingCredit(Person p, int amount) {
                Transaction t = new Transaction(p, this, amount, null);
                addPendingCredit(t);
            }
            public void addPendingCredit(Transaction t) {
                listPendingCredit.add(t);
            }
            /* debit this person. */
            public void addPendingDebit(Transaction t) {
                listPendingDebit.add(t);
            }
            public void addPendingDebit(Person p, int amount) {
                Transaction t = new Transaction(this, p, amount, null);
                addPendingDebit(t);
            }
            public void consolidate() {
            }
            public int getCredit() {
                return credit;
            }
            public boolean credit(int credit) {
                this.credit += credit;
                return true;
            }
            public boolean debit(int debit) {
                this.credit -= debit;
                return true;
            }
            public boolean canDebit(int debit) {
                return ((debit >= 0) && (debit <= credit));
            }
            public void consolidatePendingTransactions() {
                //for(Transaction t: listPendingCredit) {
                //}
                // trace to beginning of credit line, if any
                //for(Transaction t: listPendingCredit) {
                //}
            }
            public void consolidatePendingTransaction() {
            }
            public boolean removePendingCredit(Person p, int amount) {
                //for(Transaction t: listPendingCredit) {
                //}
                return false;
            }
            public void settle() {
                settleAllSimple();
            }
            public void settleAllSimple() {
                for(Transaction t: listPendingDebit) {
                    debit(t.amount);
                    t.dst.credit(t.amount);
                }
                listPendingCredit.clear();
                listPendingDebit.clear();
            }
        }
        public static class Transaction {
            public int amount;
            public Person src;
            public Person dst;
            public String description;
            public Transaction(Person src, Person dst, int amount, String description) {
                this.src = src;
                this.dst = dst;
                this.amount = amount;
                this.description = description;
            }
            public void print() {
                p("%6s to %6s amount %5d for %s\n", 
                        src.name, dst.name, amount, description);
            }
        }
        public List<Transaction> listPending = new LinkedList<>();
        public List<Transaction> listCompleted = new LinkedList<>();
        public Set<Person> set = new HashSet<>();
        public void addPeople(Person payer, Person payee) {
            if(payer != null) {
                set.add(payer);
            }
            if(payee != null) {
                set.add(payee);
            }
        }
        public void addTransaction(Person src, Person dst, int amount) {
            addPeople(src, dst);
            if(src == dst) {
                return;
            }
            src.addPendingDebit(dst, amount);
            dst.addPendingCredit(src, amount);
            Transaction t = new Transaction(src, dst, amount, null);
            listPending.add(t);
        }
        public void addTransaction(Person src, List<Person> ldst, int totalAmount) {
            int amount = totalAmount / ldst.size();
            for(Person dst: ldst) {
                addTransaction(src, dst, amount);
            }
        }
        public void addTransaction(List<Person> lsrc, Person dst, int totalAmount) {
            int amount = totalAmount / lsrc.size();
            for(Person src: lsrc) {
                addTransaction(src, dst, amount);
            }
        }
        public void printTransactions() {
            printTransactionsLedgerPending();
        }
        public void printTransactionsLedgerPending() {
            for(Transaction t: listPending) {
                t.print();
            }
        }
        public void printTransactionsPeoplePending() {
            for(Person p: set) {
                p.print();
            }
        }
        public void consolidatePendingTransactions() {
            for(Person p: set) {
                p.consolidatePendingTransactions();
            }
            printTransactionsPeoplePending();
        }
        public void settle() {
            //settleSimple();
            settlePeople();
        }
        public void settlePeople() {
            for(Person p: set) {
                p.settleAllSimple();
            }
            for(Person p: set) {
                p.printSummary();
            }
        }
        public void printSummaryPeople() {
            for(Person p: set) {
                p.printSummary();
            }
        }
        public void settleBetter() {
            Map<Person, Integer> mapOriginalCredit = new HashMap<>();
            for(Person p: set) {
                mapOriginalCredit.put(p, p.getCredit());
            }
            {
                
            }
            boolean isSuccess = true;
            if(isSuccess) {
                listPending.clear();
                for(Person p: set) {
                    p.print();
                }
            }
            else {
                for(Map.Entry<Person, Integer> kv: mapOriginalCredit.entrySet()) {
                    Person p = kv.getKey();
                    int originalCredit = kv.getValue();
                    p.setCredit(originalCredit);
                }
            }
        }
        public void settleSimple() {
            Map<Person, Integer> mapOriginalCredit = new HashMap<>();
            for(Person p: set) {
                mapOriginalCredit.put(p, p.getCredit());
            }
            boolean isSuccess = true;
            {
                for(Transaction t: listPending) {
                    if(t.src == t.dst) {
                        continue;
                    }
                    if(t.src.canDebit(t.amount)) {
                        t.src.debit(t.amount);
                        t.dst.credit(t.amount);
                    } else {
                        isSuccess = false;
                        break;
                    }
                }        
            }
            if(isSuccess) {
                listPending.clear();
                for(Person p: set) {
                    p.printSummary();
                }
            }
            else {
                for(Map.Entry<Person, Integer> kv: mapOriginalCredit.entrySet()) {
                    Person p = kv.getKey();
                    int originalCredit = kv.getValue();
                    p.setCredit(originalCredit);
                }
            }
        }
    }
    static class Sudoko {
        public void print(String format, Object ...args) {
            System.out.printf(format, args);
        }
        class SudokoMatrix {
            public void print(String format, Object ...args) {
                System.out.printf(format, args);
            }
            private int n;
            private int max;
            int [][] a;
            int numPopulated;
            SudokoMatrix(int sz) {
                a = new int[n][n];
                max = n * n;
                numPopulated = 0;
            }
            public boolean populate(List<Integer> list) {
                if(list == null || list.size() > max) {
                    return false;
                }
                int ctr = 0;
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        Integer v = list.get(ctr);
                        if(v == null) {
                            v = -1;
                        } else {
                            numPopulated++;
                        }
                        a[i][j] = v;
                        ctr++;
                    }
                }
                return true;
            }
            public int get(int i, int j) throws Exception {
                if(i >= n || i < 0 || j >= n || j < 0) {
                    throw new 
                        Exception(String.format("invalid matrix %d %d", i, j));
                }
                return a[i][j];
            }
            public List<Integer> getRow(int i) throws Exception {
                if(i < 0 || i >= n) {
                    throw new Exception("getRow out of range");
                }
                List<Integer> list = new ArrayList<>();
                for(int j = 0; j < n; j++) {
                    list.add(a[i][j]);
                }
                return list;
            }
            public List<Integer> getCol(int i) throws Exception {
                if(i < 0 || i >= n) {
                    throw new Exception("getCol out of range");
                }
                List<Integer> list = new ArrayList<>();
                for(int j = 0; j < n; j++) {
                    list.add(a[j][i]);
                }
                return list;
            }
            public void set(int i, int j, int v) throws Exception {
                if(i >= n || i < 0 || j >= n || j < 0 || a[i][j] != -1) {
                    throw new 
                        Exception(String.format("invalid matrix %d %d", i, j));
                }
                a[i][j] = v;
                numPopulated++;
            }
            public boolean isComplete() {
                if(numPopulated == max) {
                    return true;
                }
                return false;
            }
            public boolean validate() {
                if(!isComplete()) {
                    return false;
                }
                Set<Integer> set = new HashSet<>();
                for(int i = 1; i <= max; i++) {
                    set.add(i);
                }
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        if(!set.contains(a[i][j])) {
                            return false;
                        }
                        set.remove(a[i][j]);
                    }
                }
                if(set.size() != 0) {
                    return false;
                }
                // check sequences. sequences only valid across many matrix
                {
                    // row check
                    {
                        for(int i = 0; i < n; i++) {
                            //int prv = a[i][0];
                            //boolean isIncreasing = false;
                            for(int j = 1; j < n; j++) {
                                
                            }
                        }
                    }
                    // col check
                    {
                        for(int i = 0; i < n; i++) {
                            for(int j = 0; j < n; j++) {
                            }
                        }
                    }
                    // diagonal check
                    {
                    }
                }
                return true;
            }
            public void print() {
                for(int i = 0; i < n; i++) {
                    for(int j = 0; j < n; j++) {
                        print("%2d ", a[i][j]);
                    }
                    print("\n");
                }
            }
        }
        private int n;
        private int max;
        List<List<SudokoMatrix>> listMatrix = new ArrayList<>();
        public Sudoko(int n) {
            this.n = n;
            this.max = n * n;
        }
        public boolean addMatrix(int idx, List<Integer> list) {
            return true;
        }
        public boolean validate() {
            // do basic check
            for(int i = 0; i < n; i++) {
                List<SudokoMatrix> listMatrixRow = listMatrix.get(i);
                for(int j = 0; j < n; j++) {
                    SudokoMatrix matrix = listMatrixRow.get(j);
                    if(!matrix.validate()) {
                        return false;
                    }
                }
            }
            Set<Integer> setReference = new HashSet<>();
            for(int i = 1; i <= max; i++) {
                setReference.add(i);
            }
            // do sequence checks on rows and columns
            for(int i = 0; i < n; i++) {
                //Set<Integer> set = (HashSet<Integer>)setReference.clone(); // does not work
                Set<Integer> set = new HashSet<>(setReference); 
                List<SudokoMatrix> listrow = listMatrix.get(i);
                for(int j = 0; j < n; j++) {
                    SudokoMatrix matrix = listrow.get(j);
                    for(int k = 0; k < n; k++) { // for each row in matrix
                        List<Integer> list = null;
                        try {
                            list = matrix.getRow(k);
                        } catch(Exception e) {
                            return false;
                        }
                        for(int l = 0; l < n; l++) { // for each val in row
                            if(!set.contains(list.get(l))) {
                                return false;
                            }
                            set.remove(list.get(l));
                        }
                    }
                    if(set.size() != 0) {
                        return false;
                    }
                }
            }
            return false;
        }
    }
}

class Strings {
    /**
     * regex expressions supported:
     * 
     *        .        wildcard
     *     *        0 or more matches
     *     ?        1 or more matches
     *     a*        a 0 or more matches
     *     a?        a 1 or more matches
     *     \\        escaped symbol
     *     \\(a|d)+    text or digits 1 or more
     *     \\(a|d)*    text or digits 0 or more
     *     \\       backslash
     * @param regex
     * @param text
     * @return
     */
    public static void p(String f, Object ...a) {
        System.out.printf(f, a);
    }
    static class SimpleObject {
        public String id;
        public String f1;
        public String f2;
        public SimpleObject(String id, String field1, String field2) {
            this.id = id;
            this.f1 = field1;
            this.f2 = field2;
        }
        public void print() {
            p("SimpleObject id:%10s f1:%10s f2:%10s\n", id, f1, f2);
        }
    }
    /**
     * rolling hash is used for rabin karp search of long text,
     * with O(n) on avg, unless hash function is bad and hits all time.
     */
    static class RollingHash {
        int sizeWindow;
        int prvval;
        int curval;
        int prime;
        char prvchar;
        boolean isvalid = false;
        public RollingHash(int sizeStr) {
            sizeWindow = sizeStr;
            prvval = 0;
            curval = 0;
            prime = 31;
            prvchar = '\0';
        }
        public int computeHash(String strWindow) {
            if(strWindow.length() != sizeWindow) {
                return -1;
            }
            if(!isvalid) {
                curval = 0;
                int base = prime;
                for(int i = sizeWindow - 1; i >= 0; i--) {
                    char c = strWindow.charAt(i);
                    curval = curval * base + c * prime;
                    base = base * prime;
                    curval = curval % 0xffffffff;
                    base = base % 0xffffffff;
                }
                prvchar = strWindow.charAt(0);
                isvalid = true;
            } else {
                int base = pow(prime, sizeWindow);
                int valDelta = curval - base * prvchar;
                int c = strWindow.charAt(sizeWindow-1);
                curval = prime * (valDelta + c);
                prvchar = strWindow.charAt(0);
            }
            return curval;
        }
        int pow(int base, int pow) {
            int v = 1;
            if(pow <= 0) {
                return 1;
            }
            for(int i = 1; i <= pow; i++) {
                v = v * base;
                v = v % 0xffffffff;
            }
            return v;
        }
    }
    static class KMP {
        int [] table;
        char [] a;
        public void print(String format, Object ...args) {
            System.out.printf(format, args);
        }
        /*
         * 00 01 02 03 04 05
         *  a  b  a  b  a  a
         * -1 00 00 01 02 03
         */
        private void constructTable() {
            table[0] = -1;
            table[1] = 0;
            int pos = 2;
            int cnd = 0;
            int sz = table.length;
            while(pos < sz) {
                if(a[pos-1] == a[cnd]) {
                    table[pos++] = ++cnd;
                }
                else if(cnd > 0) {
                    cnd = table[cnd];
                }
                else {
                    table[pos] = 0;
                    pos++;
                }
            }
            for(int i = 0; i < sz; i++) {
                print("%02d ", i);
            }
            print("\n");
            for(int i = 0; i < sz; i++) {
                print("%2s ", a[i]);
            }
            print("\n");
            for(int i = 0; i < sz; i++) {
                print("%02d ", table[i]);
            }
            print("\n");
        }
        public KMP(String pat) {
            a = pat.toCharArray();
            table = new int[a.length];
            constructTable();
        }
        /*
         * 00 01 02 03 04 05
         *  a  b  a  b  a  a
         * -1 00 00 01 02 03
         */
        public int match(String str) {
            char [] astr = str.toCharArray();
            int szs = astr.length;
            int szp = a.length;
            {
                print("\n");
                for(int i = 0; i < szp; i++) {
                    print("%02d ", i);    // print idx
                }
                print("\n");
                for(int i = 0; i < szp; i++) {
                    print("%2s ", a[i]); // print pat
                }
                print("\n");
                for(int i = 0; i < szs; i++) {
                    print("%02d ", i);  // print idx
                }
                print("\n");
                for(int i = 0; i < szs; i++) {
                    print("%2s ", astr[i]); // print str
                }
                print("\n");
            }

            int m = 0;
            int i = 0;
            while((m + i) < szs) {
                if(a[i] == astr[m+i]) {
                    if(i == (szp-1)) {
                        return m;
                    }
                    i++;
                }
                else if(table[i] > -1) {
                    m = m + i - table[i];
                    i = table[i];
                    // print pattern shifted to m+i
                    for(int k = 0; k < m; k++) {
                        print("   ");
                    }
                    for(int k = 0; k < szp; k++) {
                        print("%2s ", a[k]); // print pat
                    }
                    print("\n");
                }
                else {
                    m++;
                    i = 0;
                    // print pattern shifted to m+i
                    for(int k = 0; k < m; k++) {
                        print("   ");
                    }
                    for(int k = 0; k < szp; k++) {
                        print("%2s ", a[k]); // print pat
                    }
                    print("\n");
                }
            }
            return -1;
        } 
    }
    public List<String> regex(String regex, String text) {
        List<String> l = new ArrayList<>();
        char [] aRE = regex.toCharArray();
        char [] aTXT = text.toCharArray();
        regex(aRE, aTXT, 0, 0, 0, l);
        return l;
    }
    private int getNextIdx(char [] re, int idxCur) {
        if(((idxCur+1) > 0) && ((idxCur+1) < re.length)) {
            return idxCur + 1;
        }
        return -1;
    }
    private boolean isAlpha(char c) {
        boolean isAlpha = ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'));
        return isAlpha;
    }
    private boolean isNumeric(char c) {
        boolean isNumeric = c >= '0' &&  c <= '9';
        return isNumeric;
    }
    protected boolean isAlphaNumeric(char c) {
        boolean isAlpha = isAlpha(c);
        if(isAlpha) {
            return true;
        }
        boolean isNumeric = isNumeric(c);
        return isNumeric;
    }
    private void regex(
        char [] re, 
        char [] txt, 
        int ire, 
        int itxt, 
        int itxtbeg, 
        List<String> l) {
        if(ire >= re.length) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%05d: ", itxtbeg));
            for(int j = itxtbeg; j < itxt; j++) {
                sb.append(txt[j]);
            }
            String s = sb.toString();
            l.add(s);
            return;
        }
        if(itxt >= txt.length) {
            return;
        }
        char c0 = re[ire];
        char c1 = txt[itxt];
        if(c0 == '.') {
            
        }
        else if(c0 == '*') {
            
        }
        else if(c0 == '?') {
            ire++;
            itxt++;
            regex(re, txt, ire, itxt, itxtbeg, l);
        }
        else if(c0 == '\\') {
            int i1 = getNextIdx(re, ire);
            if(i1 == -1) {
                return;
            }
            if(re[i1] == 'a') {
                int i2 = getNextIdx(re, i1);
                if(i2 == -1) {
                    //if(txt[itxt] )
                }
                else if(re[i2] == '*') {
                    
                }
                else if(re[i2] == '?') {
                    
                }
            }
            else if(re[i1] == 'd') {
                int i2 = getNextIdx(re, i1);
                if(i2 == -1) {
                    
                }
                else if(re[i2] == '*') {
                    
                }
                else if(re[i2] == '?') {
                    
                }
            }
            else {
                itxtbeg++;
                regex(re, txt, 0, itxtbeg, itxtbeg, l);
            }
        }
        else if(c0 == c1) {
            ire++;
            itxt++;
            regex(re, txt, 0, itxt, itxt, l);
        }
        else if(c0 != c1) {
            itxtbeg++;
            regex(re, txt, 0, itxtbeg, itxtbeg, l);
        }
    }
    public int levenshteinEdit(String s1, String s2) {
        return levenshteinEdit1(s1, s2);
    }
    public int levenshteinEdit1(String s1, String s2) {
        int sz1 = s1.length();
        int sz2 = s2.length();
        int type = 2;
        if(type == 1) 
            return levenshteinEditRecursive(s1, s2, sz1-1, sz2-1);
        return levenshteinEditMatrix(s1,s2,sz1,sz2);
    }
    private int levenshteinEditRecursive(
            String s1, String s2, int sz1, int sz2) {
        if(sz1 == 0) return sz2;
        if(sz2 == 0) return sz1;
        int cost = 0;
        if(s1.charAt(sz1) != s2.charAt(sz2)) {
            cost = 1;
        }
        int v1 = levenshteinEditRecursive(s1, s2, sz1 - 1, sz2);
        int v2 = levenshteinEditRecursive(s1, s2, sz1, sz2 - 1);
        int v3 = levenshteinEditRecursive(s1, s2, sz1 - 1, sz2 - 1) + cost;
        if(v1 < v2 && v1 < v3) {
            return v1;
        }
        if(v2 < v3) {
            return v2;
        }
        return v3;
    }
    private int levenshteinEditMatrix(
            String s1, String s2, int sz1, int sz2
            ) {
        int [][] a = new int[sz1][sz2];
        for(int i = 0; i < sz1; i++) {
            for(int j = 0; j < sz2; j++) {
                int cost = 0;
                if(s1.charAt(i) != s2.charAt(j)) {
                    cost = 1;
                }
                int iprv = (i == 0) ? i : i-1;
                int jprv = (j == 0) ? j : j-1;
                if(i == 0 && j == 0) {
                    a[i][j] = cost;
                }
                else if(i == 0) {
                    a[i][j] = cost + a[i][jprv];
                }
                else if(j == 0) {
                    a[i][j] = cost + a[iprv][j];
                }
                else {
                    int min = a[iprv][jprv];
                    if(a[i][jprv] < min) {
                        min = a[i][jprv];
                    }
                    if(a[iprv][j] < min) {
                        min = a[iprv][j];
                    }
                    a[i][j] = cost + min;
                }
            }
        }
        // print row of column chars
        p("   ");
        for(int i = 0; i < sz2; i++) {
            p("%2s ", s2.charAt(i));
        }
        p("\n");
        for(int i = 0; i < sz1; i++) {
            p("%2s ", s1.charAt(i));
            for(int j = 0; j < sz2; j++) {
                p("%2d ", a[i][j]);
            }
            p("\n");
        }
        p("%-10s %-10s = %d\n\n", s1, s2, a[sz1-1][sz2-1]);
        return a[sz1-1][sz2-1];
    }
    public int levenshteinEdit2(String s1, String s2) {
        int sz1 = s1.length();
        int sz2 = s2.length();
        int [][] a = new int[sz1][sz2];
        for(int i = 0; i < sz1; i++) {
            for(int j = 0; j < sz2; j++) {
                a[i][j] = 0;
            }
        }
        return 0;
    }
    public String longestCommonSubstring(String s1, String s2, boolean isdebug) {
        /**
         * find the longest pattern between s1 and s2. 
         * Use suffix tree or DP.
         * For suffix tree, build generalized suffix tree
         * for both strings, then find the deepest internal node
         * for both.
         * ST is O(kn)? 
         * DP is O(mn)
         * 
         * keep only last and current row of DP to save mem.
         * store only non zero values in rows using hash instead of array.
         */
        int sz1 = s1.length();
        int sz2 = s2.length();
        int [][]a = new int[sz1][sz2];
        for(int i = 0; i < sz1; i++) {
            char c1 = s1.charAt(i);
            for(int j = 0; j < sz2; j++) {
                char c2 = s2.charAt(j);
                if(c1 == c2) {
                    if(i == 0 || j == 0) {
                        a[i][j] = 1;
                    } else {
                        a[i][j] = a[i-1][j-1] + 1;
                    }
                } else {
                    a[i][j] = 0;
                }
            }
        }
        if(isdebug) {
            // row of s2 chars
            p("      ");
            for(int j = 0; j < sz2; j++) {
                p("%2s ", s2.charAt(j));
            }
            p("\n");
            // row of idx for s2
            p("      ");
            for(int j = 0; j < sz2; j++) {
                p("%2s ", j);
            }
            p("\n");
            // row of lines
            p("      ");
            for(int j = 0; j < sz2; j++) {
                p("---", j);
            }
            p("\n");

            for(int i = 0; i < sz1; i++) {
                // first is char s1 based on row
                p("%2s %2d|", s1.charAt(i), i);
                
                for(int j = 0; j < sz2; j++) {
                    p("%2d ", a[i][j]);
                }
                p("\n");
            }
        }
        return null;
    }
    public String longestCommonSubsequence(String s1, String s2, boolean isdebug) {
        /**
         * find the longest common subsequence s1 and s2.
         */
        int sz1 = s1.length();
        int sz2 = s2.length();
        int [][]a = new int[sz1][sz2];
        
        for(int i = 0; i < sz1; i++) {
            char c1 = s1.charAt(i);
            for(int j = 0; j < sz2; j++) {
                char c2 = s2.charAt(j);
                int v = (c1 == c2) ? 1 : 0;
                if(i == 0 && j == 0) {
                    a[i][j] = v;
                } else if(i == 0) {
                    a[i][j] = a[i][j-1] + v;
                } else if(j == 0) {
                    a[i][j] = a[i-1][j] + v;
                } else {
                    if(v == 1) {
                        a[i][j] = a[i-1][j-1] + v;
                    } else {
                        a[i][j] = (a[i-1][j] < a[i][j-1]) ? 
                                a[i][j-1] : a[i-1][j];
                    }
                }
            }
        }

        if(isdebug) {
            // row of s2 chars
            p("      ");
            for(int j = 0; j < sz2; j++) {
                p("%2s ", s2.charAt(j));
            }
            p("\n");
            // row of idx for s2
            p("      ");
            for(int j = 0; j < sz2; j++) {
                p("%2s ", j);
            }
            p("\n");
            // row of lines
            p("      ");
            for(int j = 0; j < sz2; j++) {
                p("---", j);
            }
            p("\n");

            for(int i = 0; i < sz1; i++) {
                // first is char s1 based on row
                p("%2s %2d|", s1.charAt(i), i);
                
                for(int j = 0; j < sz2; j++) {
                    p("%2d ", a[i][j]);
                }
                p("\n");
            }
        }
        return null;
    }
    public String longestIncreasingSubsequence(String s) {
        /**
         * find the longest increasing subsequence s.
         */
        return null;
    }
}
class Trees {
    public static void print(String f, Object ...a) {
        System.out.printf(f, a);
    }
    public static void p(String f, Object ...a) {
        System.out.printf(f, a);
    }
    static class Nodes {
        public static class NodeB <E> {
            public NodeB<E> l;
            public NodeB<E> r;
            public E e;
            public NodeB(E e) {
                this.e = e;
            }
        }
        public static class NodeTrie <E> {
            List<NodeTrie<E>> nodeList;
            public NodeTrie(int size) {
                nodeList = new ArrayList<>();
                for(int i = 0; i < size; i++) {
                    nodeList.add(null);
                }
            }
        }
        public static class NodeCharTree {
            Map<Character, NodeCharTree> map;
            int size;
            public NodeCharTree(int size) {
                map = new HashMap<>();
                this.size = size;
            }
            public Map<Character, NodeCharTree> getMap() {
                return map;
            }
            public boolean put(String string) {
                return put(this, string, 0);
            }
            private boolean put(
                NodeCharTree n,
                String string,
                int idx) 
            {
                if(idx >= string.length()) {
                    return false;
                }
                Character c = string.charAt(idx);
                Map<Character, NodeCharTree> m = n.getMap();
                NodeCharTree mInner = m.get(c);
                boolean hasMatch = true;
                if(mInner == null) {
                    mInner = new NodeCharTree(size);
                    m.put(c, mInner);
                    hasMatch = false;
                }
                if((idx + 1) == string.length()) {
                    return hasMatch;
                }
                boolean hasMatchInner = put(mInner, string, idx+1); 
                if(hasMatch && hasMatchInner) {
                    return true;
                }
                return false;
            }
            public boolean get(String string) {
                return get(this, string, 0);
            }
            private boolean get(NodeCharTree n, String string, int idx) {
                if(string == null || idx >= string.length()) {
                    return false;
                }
                Character c = string.charAt(idx);
                Map<Character, NodeCharTree> m = n.getMap();
                NodeCharTree nInner = m.get(c);
                if(nInner == null) {
                    return false;
                }
                if((idx + 1) == string.length()) {
                    return true;
                }
                return get(nInner, string, idx+1);
            }
        }
    }
    static class Recursion {

        public void print(String format, Object ...args) {
            System.out.printf(format, args);
        }

        public void getListFactors(
            int v,
            List<Integer> l, 
            Map<Integer,Map<Integer,Integer>> t, 
            List<List<Integer>> lf) 
        {
            // loop through 2 to v/2 and get list of factor combinations equaling v
            // eg v = 24
            //      1*24
            //      2*12
            //          2*2*6
            //          2*2*2*3
            //      3*8
            //          3*2*4
            //          3*2*2*2
            //      4*6
            //          4*2*3
            //
            // l    list of factors collected til no further, then add to lf
            // t    table of factor pairs for a given number, eg 12 -> (2,6),(3,4)
            // lf   list of list of factors, where prod of list elements = v
            
            // add base case
            lf.add(Arrays.asList(1,v));

            // calculate the others
            for(int i = 2; i <= v/2; i++) {
                l.clear();
                if(v % i == 0) {
                    l.add(i);
                    int d = v / i;
                    
                    // add first pair
                    l.add(d);
                    List<Integer> listNew = new ArrayList<>(l);
                    lf.add(listNew);
                    l.remove(l.size() - 1);

                    // remove second part and see if more chains possible
                    getListFactorsChain(v, d, l, t, lf);
                    l.remove(l.size() - 1);
                }
            }
        }

        public void getListFactorsChain(
            int v, 
            int d, 
            List<Integer> l, 
            Map<Integer, Map<Integer,Integer>> t, 
            List<List<Integer>> lf) 
        {
            // recursively produce all values of d from table t values
            // get map of d, iterate through keys, recursively call method 
            // on values. if d doesnt exist, then add list to lf and return
            Map<Integer,Integer> m = t.get(d);
            if(m == null) {
                return;
            }
            for(Map.Entry<Integer,Integer> kv : m.entrySet()) {
                Integer k1 = kv.getKey();
                Integer v1 = kv.getValue();
                l.add(k1);

                l.add(v1);
                List<Integer> listNew = new ArrayList<>(l);
                lf.add(listNew);
                l.remove(l.size() - 1);

                if(t.get(v1) != null) {
                    getListFactorsChain(v, v1, l, t, lf);                
                }
                l.remove(l.size() - 1);
            }
        }

        public void createFactorTable(Map<Integer, Map<Integer,Integer>> map, int v) {
            if(v >= 0) {
                // for each number up to v, what are all the possible factor pairs?
                // ie:  4:  2*2
                //     16:  2*8,4*4
                //     24:  2*12,3*8,4*6
                for(int i = 1; i < v; i++) {
                    for(int j = 2; j <= i/2; j++) {
                        if(i % j != 0) {
                            continue;
                        }
                        // get i*j=v set, check to see if already in map
                        int iDiv = i / j;
                        Map<Integer,Integer> m = map.get(i);
                        Map<Integer,Integer> mOrig = m;
                        if(m == null) {
                            m = new HashMap<Integer,Integer>();
                        }
                        if(!m.containsKey(j) && !m.containsKey(iDiv)) {
                            m.put(j, iDiv);
                        }
                        if(mOrig == null) {
                            map.put(i, m);
                        }
                    }
                    if(map.get(i) == null) {
                        map.put(i, null);
                    }
                }
            }
        }

        public void printFactorTable(Map<Integer, Map<Integer,Integer>> map) {
            for(Map.Entry<Integer, Map<Integer,Integer>> kv: map.entrySet()) {
                Integer i = kv.getKey();
                Map<Integer,Integer> m = kv.getValue();
                print("i:%3d\t", i);
                if(m == null) {
                    print("PRIME");
                } else {
                    for(Map.Entry<Integer,Integer> kvi: m.entrySet()) {
                        print("(%2d,%2d) ", kvi.getKey(), kvi.getValue());
                    }
                }
                print("\n");   
            }
        }

        public void createFactorTable2(Map<Integer,Integer> m, int v) {
            if(v >= 0) {
                for(int i = 1; i < v; i++) {
                    if(v % i != 0) {
                        continue;
                    }
                    int iDiv = v / i;
                    if(!m.containsKey(i) || !m.containsKey(iDiv)) {
                        m.put(i,iDiv);
                        m.put(iDiv,i);
                    }
                }
            }
        }

        public int maxCutProductOfParts(int len) {
            return 0;
        }
        public int binarySearchIterative(int [] a, int idxMin, int idxMax, int searchVal) {
            int min = idxMin;
            int max = idxMax;
            int mid = (min+max)/2;
            int newMid = -1;
            while(mid != newMid) {
                if(a[mid] == searchVal) {
                    break;
                }
                else if(a[mid] < searchVal) {
                    newMid = (mid+min) / 2;
                    max = mid;
                }
                else {
                    newMid = (mid+max) / 2;
                    min = mid;
                }
            }
            return a[mid];
        }
        public int binarySearch(int [] a, int min, int max, int v) {
            if(max < min) {
                return -1;
            }
            int mid = (min + max) / 2;    
            if(a[mid] > v) {
                return binarySearch(a, min, mid - 1, v);
            }
            else if(a[mid] < v) {
                return binarySearch(a, mid + 1, max, v);
            }
            return a[mid];
        }
        public int binarySearchRecursive(int [] a, int idxMin, int idxMax, int idxMid, int searchVal) {
            print("min:%2d max:%2d mid:%2d val:%2d\n", idxMin, idxMax, idxMid, searchVal);
            if(idxMid < 0 || idxMid > idxMax || idxMin == idxMid || idxMax == idxMid) {
                return -1;
            }
            if(a[idxMid] == searchVal) {
                return a[idxMid];
            }
            else if(a[idxMid] > searchVal) {
                int newMid = (idxMid + idxMin) / 2;
                return binarySearchRecursive(a, idxMin, idxMid, newMid, searchVal);
            }
            else {
                int newMid = (idxMid + idxMax) / 2;
                return binarySearchRecursive(a, idxMid, idxMax, newMid, searchVal);
            }
        }
        public int findMaxLength(int [] a, int k, int min, int max, int mid) {
            if(mid == min || mid == max) {
                return 0;
            }
            int numRemaining = k;
            print("min:%d max:%d mid:%d\n", min, max, mid);
            for(int i = 0; i < a.length; i++) {
                int curPieces = a[i]/mid;
                numRemaining = numRemaining - curPieces;
            }
            if(numRemaining == 0) {
                print("numRemaining = 0 @ %d\n", mid);
                int newmid = (mid + max) / 2;
                if(mid == newmid) {
                    return mid;
                }
                int biggerLength = findMaxLength(a, k, mid, max, newmid);
                if(biggerLength == 0) {
                    return mid;
                } else {
                    return biggerLength;
                }
            }
            else if(numRemaining > 0) { // too big, make smaller
                int newmid = (mid+ min) / 2;
                return findMaxLength(a, k, min, mid, newmid);
            }
            else if(numRemaining < 0) { // too small, make bigger
                int newmid = (mid+ max) / 2;
                return findMaxLength(a, k, mid, max, newmid);
            }
            return 0;
        }
        public int findMaxLength2(int [] a, int k, int min, int max) {
            if(min > max) {
                return 0;
            }
            int numRemaining = k;
            int mid = (min + max) / 2;
            for(int i = 0; i < a.length; i++) {
                int curPieces = a[i]/mid;
                numRemaining = numRemaining - curPieces;
            }
            if(numRemaining == 0) {
                int biggerLength = findMaxLength2(a, k, mid + 1, max);
                if(biggerLength == 0) {
                    return mid;
                } 
                return biggerLength;
            }
            else if(numRemaining > 0) { 
                return findMaxLength2(a, k, min, mid - 1);
            }
            else if(numRemaining < 0) {
                return findMaxLength2(a, k, mid + 1, max);
            }
            return 0;
        }
        public void genBinStringsNoConsecutiveOnes(
            int max, int idx, char [] a, List<String> list) {
            if(idx >= a.length) {
                String s = new String(a);
                list.add(s);
                return;
            }
            a[idx] = '0';
            genBinStringsNoConsecutiveOnes(max, idx+1, a, list);
            a[idx] = '1';
            if((idx+1) >= a.length) {
                genBinStringsNoConsecutiveOnes(max, idx+1, a, list);
            } else {
                a[idx+1] = '0';
                genBinStringsNoConsecutiveOnes(max, idx+2, a, list);
            }
        }
    }
    static class SCombinations {

        /**
         *     permutation: 
         *         no repetition:        n!/(n-k)!
         *         repetition:            k^n
         *         multiset:            n!/(m1!m2!...m*!)
         * 
         *     combination:
         *         no repetition:        n!/(k!(n-k)!)
         *         repetition:            (n+k-1)!/(k!(n-1)!)
         * 
         * @param s
         */
        public void permutations(String s) {
            p("STRING IN: %s\n", s);
            List<String> list = new ArrayList<>();
            permutations(s, list);
            for(int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                p("%3d: %s\n", i, string);
            }
        }

        public void permutations(
                String s, 
                List<String> listResults) {
            int sizeStr = s.length();
            boolean [] used = new boolean[sizeStr];
            for(int size = 1; size <= s.length(); size++) {
                for(int i = 0; i < sizeStr; i++) {
                    used[i] = false;
                }
                char [] a = new char[size];
                permutations(s, a, 0, used, listResults);
            }
        }
        
        public void permutations(
                String s, 
                int sizeChoose) {
            p("STRING IN: %s\n", s);
            List<String> list = new ArrayList<>();
            permutations(s, sizeChoose, list);
            for(int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                p("%3d: %s\n", i, string);
            }
        }
        
        public void permutations(String s, int sizeChoose, List<String> listResults) {
            int sizeStr = s.length();
            boolean [] used = new boolean[sizeStr];
            for(int i = 0; i < sizeStr; i++) {
                used[i] = false;
            }
            char [] a = new char[sizeChoose];
            permutations(s, a, 0, used, listResults);
        }
        
        private void permutations(
                String s, 
                char [] a, 
                int idxa, 
                boolean [] used, 
                List<String> list) {
            if(idxa == a.length) {
                String newstring = new String(a);
                list.add(newstring);
                return;
            }
            for(int i = 0; i< s.length(); i++) {
                if(used[i]) {
                    continue;
                }
                used[i] = true;
                char c = s.charAt(i);
                a[idxa] = c;
                permutations(s, a, idxa + 1, used, list);
                used[i] = false;
            }
        }
        
        public void combinations(String s) {
            p("STRING IN: %s\n", s);
            List<String> list = new ArrayList<>();
            combinations(s, list);
            for(int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                p("%3d: %s\n", i, string);
            }
        }
        
        public void combinations(
                String s, 
                int sizeChoose) {
            p("STRING IN: %s\n", s);
            List<String> list = new ArrayList<>();
            combinations(s, sizeChoose, list);
            for(int i = 0; i < list.size(); i++) {
                String string = list.get(i);
                p("%3d: %s\n", i, string);
            }        
        }

        public void combinations(
                String s, 
                List<String> listResults) {
            for(int i = 1; i <= s.length(); i++) {
                char [] a = new char[i];
                combinations(s, a, 0, 0, listResults);            
            }
            
        }
        public void combinations(
                String s, 
                int sizeChoose, 
                List<String> listResults) {    
            char [] a = new char[sizeChoose];
            combinations(s, a, 0, 0, listResults);
        }
        
        private void combinations(
                String s,
                char [] a,
                int idxs,
                int idxa,
                List<String> list) {
            if(idxa == a.length) {
                String newstring = new String(a);
                list.add(newstring);
                return;
            }
            for(int i = idxs; i < s.length(); i++) {
                char c = s.charAt(i);
                a[idxa] = c;
                combinations(s, a, i + 1, idxa + 1, list);
            }
        }
    }
    public static class TreeM {
        
    }
    public static class TreeBinary {
        static class Node {
            Node l = null;
            Node r = null;
            Integer v = null;
            public Node(Integer v) {
                this.v = v;
            }
        }
        static class Pair {
            Node n;
            int h;
            Pair() {
                n = null;
                h = 0;
            }
            Pair(Node n, int h) {
                this.n = n;
                this.h = h;
            }
        }
        Node root;
        int maxHeight = 0;
        int numNodes = 0;
        public void generateRandomTree(int sz) {
            int [] a = new int[sz];
            Utils u = new Utils();
            for(int i = 0; i < sz; i++) {
                a[i] = i+1;
            }
            u.shuffle(a);
            root = null;
            for(int i = 0; i < sz; i++) {
                add(a[i]);
            }
        }
        public void setRoot(Node n) {
            root = n;
        }
        public List<Node> getPreOrder() {
            List<Node> l = new ArrayList<>();
            getPreOrder(root, l);
            return l;
        }
        public List<Node> getPostOrder() {
            List<Node> l = new ArrayList<>();
            getPostOrder(root, l);
            return l;
        }
        public List<Node> getInOrder() {
            List<Node> l = new ArrayList<>();
            getInOrder(root, l);
            return l;
        }
        public List<Node> getLevelOrder() {
            List<Node> l = new ArrayList<>();
            getLevelOrder(root, l);
            return l;
        }
        private void getPreOrder(Node n, List<Node> l) {
            if(n == null) {
                return;
            }
            l.add(n);
            getPreOrder(n.l, l);
            getPreOrder(n.r, l);
        }
        private void getInOrder(Node n, List<Node> l) {
            if(n == null) {
                return;
            }
            getInOrder(n.l, l);
            l.add(n);
            getInOrder(n.r, l);
        }
        private void getPostOrder(Node n, List<Node> l) {
            if(n == null) {
                return;
            }
            getPostOrder(n.l, l);
            getPostOrder(n.r, l);
            l.add(n);
        }
        private void getLevelOrder(Node root, List<Node> l) {
            if(root == null) {
                return;
            }
            LinkedList<Node> lq = new LinkedList<>();
            lq.add(root);
            while(!lq.isEmpty()) {
                Node n = lq.pop();
                l.add(n);
                if(n.l != null) {
                    lq.push(n.l);
                }
                if(n.r != null) {
                    lq.push(n.r);
                }
            }
        }
        public void printPreOrder() {
            List<Node> l = getPreOrder();
            for(Node n: l) {
                print("%d ", n.v);
            }
            print("\n");
        }
        public void printInOrder() {
            List<Node> l = getInOrder();
            for(Node n: l) {
                print("%d ", n.v);
            }
            print("\n");
        }
        public void printPostOrder() {
            List<Node> l = getPostOrder();
            for(Node n: l) {
                print("%d ", n.v);
            }
            print("\n");
        }
        public void printLevelOrder() {
            List<Node> l = getLevelOrder();
            for(Node n: l) {
                print("%d ", n.v);
            }
            print("\n");
        }
        public void printGetRange(int min, int max) {
            List<Node> l = new ArrayList<>();
            getRange(root, min, max, l);
            for(Node n: l) {
                print("%d ", n.v);
            }
            print("\n");
        }
        public boolean isEqual(Node n, Node other) {
            if(n == null && other == null) {
                return true;
            }
            if(n != null && other == null || n == null && other != null) {
                return false;
            }
            if(n.v != other.v) {
                return false;
            }
            boolean bl = isEqual(n.l, other.l);
            if(!bl) {
                return false;
            }
            boolean br = isEqual(n.r, other.r);
            if(!br) {
                return false;
            }
            return true;
        }
        public void getRange(Node n, int min, int max, List<Node> l) {
            if(n == null) {
                return;
            }
            if(n.v >= min) {
                getRange(n.l, min, max, l);
            }
            if(n.v >= min && n.v <= max) {
                l.add(n);
            }
            if(n.v <= max) {
                getRange(n.r, min, max, l);
            }
        }
        private Node add(Node n, int v, int curHeight) {
            if(n == null) {
                maxHeight = (maxHeight > curHeight) ? maxHeight : curHeight;
                return new Node(v);
            }
            if(v <= n.v.intValue()) {
                n.l = add(n.l, v, curHeight + 1);
            } else {
                n.r = add(n.r, v, curHeight + 1);
            }
            return n;
        }
        public void add(int v) {
            root = add(root, v, 0);
            numNodes++;
        }
        int getMaxWidth() {
            AtomicInteger max = new AtomicInteger(0);
            getMaxWidth(root, max);
            return max.get();
        }
        int getMaxWidth(Node n, AtomicInteger max) {
            if(n == null) {
                return 0;
            }
            if(n.l == null && n.r == null) {
                return 1;
            }
            int wl = getMaxWidth(n.l, max);
            int wr = getMaxWidth(n.r, max);
            int w = wl + wr;
            if(w > max.get()) {
                max.set(w);
            }
            if(wl > wr) {
                return (wl + 1);
            }
            return (wr + 1);
        }
        int getMaxHeight() {
            return maxHeight;
        }
        int getMaxHeight(Node n) {
            return getMaxHeight(n, 0);
        }
        int getMaxHeight(Node n, int curHeight) {
            if(n == null) {
                return 0;
            }
            if(n.l == null && n.r == null) {
                return 1;
            }
            int hl = getMaxHeight(n.l, curHeight+1);
            int hr = getMaxHeight(n.r, curHeight+1);
            if(hl > hr) {
                return hl;
            }
            return hr;
        }
        int findLCA(int v0, int v1) {
            AtomicInteger ctr = new AtomicInteger(0);
            Node n = findLCA(v0, v1, root, ctr);
            if(n == null) {
                print("LCA of %d and %d not found\n",
                        v0, v1);
                return -1;
            }
            print("LCA of %d and %d is %d\n",
                    v0, v1, n.v);
            return n.v;
        }
        Node findLCA(int v0, int v1, Node n, AtomicInteger ctr) {
            Node c = null;
            if(n == null) {
                return null;
            }
            if(n.v == v0) {
                ctr.incrementAndGet();
                c = n;
            }
            if(n.v == v1) {
                ctr.incrementAndGet();
                c = n;
            }
            if(ctr.get() == 2) {
                return c;
            }
            Node l = findLCA(v0, v1, n.l, ctr);
            Node r = findLCA(v0, v1, n.r, ctr);
            if(c != null) {
                return c;
            }
            if(l != null && r != null) {
                return n;
            }
            if(l != null) {
                return l;
            }
            if(r != null) {
                return r;
            }
            return null;
        }
        void printTree() {
            // number of lines allocated for tree
            int szList = (int)Math.pow(2.0, (double)(maxHeight));
            szList = 2*szList-1;
            List<Pair> list = new ArrayList<>(szList);
            int idxBeg = 0;
            int idxEnd = szList-1;
            for(int i = 0; i < szList; i++) {
                list.add(null);
            }
            int h = 1;
            
            printTree(root, list, idxBeg, idxEnd, h);// populate the lines
            for(int i = 0; i < list.size(); i++) { // print array
                print("%2d", i);
                Pair p = list.get(i);
                if(p == null) {
                    print("\n");
                    continue;
                }
                for(int j = 0; j < p.h; j++) { // for spacing, 
                    print("    ");
                }
                if(p.n != null) { // for value
                    Node n = p.n;
                    print("%2d\n", n.v);
                } else {
                    print("\n");
                }
            }
        }
        void printTree(Node n, List<Pair> list, int idxBeg, int idxEnd, int h) {
            if(idxBeg > idxEnd || idxBeg < 0 || idxEnd >= list.size()) {
                return;
            }
            Pair pair = new Pair(n, h);
            int idxMid = (idxEnd - idxBeg) / 2 + idxBeg;
            list.set(idxMid, pair);
            if(n == null) {
                return;
            }
            printTree(n.l, list, idxMid+1, idxEnd, h+1);
            printTree(n.r, list, idxBeg, idxMid-1, h+1);
        }

    }
    static class TreeRange {
        class Pair {
            Node n;
            int h;
            Pair() {
                n = null;
                h = 0;
            }
            Pair(Node n, int h) {
                this.n = n;
                this.h = h;
            }
        }
        class Triplet {
            Node n;
            int h;
            int idx;
            Triplet(Node n, int h, int idx) {
                this.n = n;
                this.h = h;
                this.idx = idx;
            }
        }
        class Quad {
            Node n;
            int h;
            int idxBeg;
            int idxEnd;
            Quad(Node n, int h, int idxBeg, int idxEnd) {
                this.n = n;
                this.h = h;
                this.idxBeg = idxBeg;
                this.idxEnd = idxEnd;
            }
        }
        class Node {
            Node l;
            Node r;
            int beg;
            int end;
            int max;
            int min;
            Node(int beg, int end) {
                this.beg = beg;
                this.end = end;
                this.min = beg;
                this.max = end;
                l = null;
                r = null;
            }
            int cmp(Node n) {
                if(n.beg == beg) {
                    return 0;
                }
                if(n.beg < beg) {
                    return -1;
                }
                return 1;
            }
            void print() {
                print("beg:%3d end:%3d max:%3d\n", beg, end, max);
            }
            void print(String format, Object ...args) {
                System.out.printf(format, args);
            }
        } 
        Node root;
        int numNodes;
        int maxHeight;
        TreeRange() {
            root = null;
            numNodes = 0;
            maxHeight = 0;
        }
        void add(int min, int max) {
            Node n = new Node(min, max);
            root = addReturn(root, n, 0);
            print("--- added %2d %2d\n", min, max);
            numNodes++;
        }
        protected void addVoid(Node p, Node n, int curHeight) {
            if(p == null) {
                p = n;
                return;
            }
            if(n.beg <= p.beg) {
                if(p.l == null) {
                    p.l = n;
                } else {
                    addVoid(p.l, n, curHeight+1);
                }
            } else {
                if(p.r == null) {
                    p.r = n;
                } else {
                    addVoid(p.r, n, curHeight+1);
                }
            }
            if(n.max > p.max) {
                p.max = n.max;
            }
            if(curHeight > maxHeight) {
                maxHeight = curHeight;
            }
        }
        private Node addReturn(Node p, Node n, int curHeight) {
            if(p == null) {
                return n;
            }
            print("addReturn pmin:%2d pmax:%2d nmin:%2d nmax:%2d\n", 
                p.beg, p.end, n.beg, n.end);
            if(n.beg <= p.beg) {
                p.l = addReturn(p.l, n, curHeight+1);
            } else {
                p.r = addReturn(p.r, n, curHeight+1);
            }
            if(n.max > p.max) {
                p.max = n.max;
            }
            if(curHeight > maxHeight) {
                maxHeight = curHeight;
            }
            return p;
        }
        void remove(int min) {
        }
        void removeRange(int min, int max) {
        }
        void print() {
            print("\nPRINT RECURSIVE\n\n");
            printRecursive();
            //print("\nPRINT LOOP\n\n");
            //printLoop();
        }
        void printRecursive() {
            // 2 = 2^2 * 2
            // 3 = 2^3 * 2
            // n = 2^n * 2
            int szList = (int)Math.pow(2.0, (double)(maxHeight+1));
            szList = 2*szList-1;
            List<Pair> list = new ArrayList<>(szList);
            int idxBeg = 0;
            int idxEnd = szList-1;
            print("list size before:%d\n", list.size());
            for(int i = 0; i < szList; i++) {
                list.add(null);
            }
            print("list size after :%d\n", list.size());
            int h = 1;
            printRecursive(root, list, idxBeg, idxEnd, h);
            for(int i = 0; i < list.size(); i++) {
                Pair p = list.get(i);
                print("%2d", i);
                if(p == null) {
                    print("\n");
                    continue;
                }
                for(int j = 0; j < p.h; j++) {
                    print("\t");
                }
                if(p.n != null) {
                    print("(%2d,%2d,%2d)\n", p.n.beg, p.n.end, p.n.max);
                } else {
                    print("\n");
                }
            }
        }
        void printRecursive(Node n, List<Pair> list, int idxBeg, int idxEnd, int h) {
            if(idxBeg > idxEnd || idxBeg < 0 || idxEnd >= list.size()) {
                return;
            }
            Pair pair = new Pair(n, h);
            int idxMid = (idxEnd - idxBeg) / 2 + idxBeg;
            list.set(idxMid, pair);
            if(n == null) {
                return;
            }
            print("---print nbeg:%2d nend:%2d idxBeg:%2d idxEnd:%2d mid:%2d h:%d\n", 
                n.beg, n.end, idxBeg, idxEnd, idxMid, h);
            printRecursive(n.l, list, idxMid+1, idxEnd, h+1);
            printRecursive(n.r, list, idxBeg, idxMid-1, h+1);
        }
        void printLoop() {
            // DOES NOT WORK

            int szList = (int)Math.pow(2.0, (double)(maxHeight+1)) * 2 - 1;
            List<Triplet> list = new ArrayList<>();
            for(int i = 0; i < szList; i++) {
                list.add(null);
            }
            int idx = szList / 2;
            LinkedList<Triplet> ll = new LinkedList<>();
            ll.offer(new Triplet(root, 1, idx));
            while(ll.size() != 0) {
                Triplet tc = ll.poll();
                if(tc.n == null) {
                    print("TC NULL idx:%2d h:%2d\n", tc.idx, tc.h);
                } else {
                    print("TC nbeg:%2d idx:%2d h:%2d\n", tc.n.beg, tc.idx, tc.h);
                }
                list.set(tc.idx, tc);
                if(tc.n == null) {
                    continue;
                }
                Node l = tc.n.l;
                Node r = tc.n.r;
                int h = tc.h + 1;
                int div = (int)Math.pow(2.0, (double)h);
                int off = szList / div;
                ll.offer(new Triplet(l, h, tc.idx - off + 1));
                ll.offer(new Triplet(r, h, tc.idx + off + 1));
            }
            for(int i = 0; i < list.size(); i++) {
                Triplet t = list.get(i);
                print("%2d", i);
                for(int j = 0; j < t.h; j++) {
                    print("\t");
                }
                if(t.n != null) {
                    Node n = t.n;
                    print("(%2d,%2d,%2d)", n.beg, n.end, n.max);
                }
                print("\n");
            }
        }
        void print(String format, Object ...args) {
            System.out.printf(format, args);
        } 
    }
}

class Matrices {
    public static void p(String f, Object ...a) {
        System.out.printf(f, a);
    }
    public static void print(String f, Object ...a) {
        System.out.printf(f, a);
    }
    
    public static class Pair {
        public int x;
        public int y;
        public Pair(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public int x() {
            return x;
        }
        public int y() {
            return y;
        }
    }
    static class MiscAlgos {
        Utils u = new Utils();
        /**
         * findMinCostPath from top left corner to bottom right.
         * 
         * use DP and update matrix if cost is less.
         * 
         * @param a
         * @return
         */
        public List<Pair> findMinCostPath(List<List<Integer>> a) {
            int numRows = a.size();
            int numCols = a.get(0).size();
            int [][] acost = u.initMatrixArray(numRows, numCols, Integer.MAX_VALUE);
            LinkedList<Pair> path = new LinkedList<>();
            LinkedList<Pair> pathMin = new LinkedList<>();
            AtomicInteger minCost = new AtomicInteger(Integer.MAX_VALUE);
            int [][] visited = u.initMatrixArray(numRows, numCols, -1);
            findMinCostPath(a, path, pathMin, 0, 0, 0, acost, visited, minCost);
            return pathMin;
        }
        public void 
        findMinCostPath(
            List<List<Integer>> a, 
            LinkedList<Pair> path, 
            LinkedList<Pair> pathMin, 
            int i,
            int j,
            int prvCost,
            int [][] acost, 
            int [][] visited,
            AtomicInteger minCost) {

            if(i >= visited.length || j >= visited[0].length || visited[i][j] != -1) {
                return;
            }

            {
                visited[i][j] = 1;
                path.add(new Pair(i,j));
            }
            int curCost = prvCost + a.get(i).get(j);
            acost[i][j] = prvCost + a.get(i).get(j);
            
            if((i+1) == visited.length && (j+1) == visited[0].length) {
                if(acost[i][j] < minCost.get()) {
                    minCost.set(acost[i][j]);
                    pathMin.clear();
                    p("min cost so far: %d\n", acost[i][j]);
                    for(Pair p: path) {
                        pathMin.add(p);
                    }
                }
            }
            //else if(acost[i][j] != -1 && curCost < acost[i][j]) 
            else 
            {
                acost[i][j] = curCost;
                // right
                findMinCostPath(a, path, pathMin, i, j+1, curCost, acost, visited, minCost);
                // down
                findMinCostPath(a, path, pathMin, i+1, j, curCost, acost, visited, minCost);
                // diagonal
                findMinCostPath(a, path, pathMin, i+1, j+1, curCost, acost, visited, minCost);
            }
            
            {
                path.pollLast();
                visited[i][j] = -1;
            }
        }
        public List<Pair> findMaxCostPath(List<List<Integer>> a) {
            List<Pair> list = new ArrayList<>();
            return list;
        }

        public List<Pair> findMinHealthCost(List<List<Integer>> a) {
            List<Pair> list = new ArrayList<>();
            return list;
        }

        public List<Pair> findShortestPathWithObstacles(List<List<Integer>> a) {
            List<Pair> list = new ArrayList<>();
            return list;
        }
        
        public List<Integer> findLongestIncreasingSubsequence(List<Integer> a) {
            List<Integer> list = new ArrayList<>();
            return list;
        }
    }

    static class CompressedColumns {
    }

    static class CompressedRows {
    }

    static class SimpleCompressedMatrix {
        /** SimpleCompressedMatrix is simple compressed row implementation. */
        static class Row {
            int idxRow = -1;
            List<Integer> listIdxCol = new ArrayList<>();
            public List<Integer> list = new ArrayList<>();
            public Row(int rowNum) {
                this.idxRow = rowNum;
            }
            public int rowNum() {
                return idxRow;
            }
            public void rowNum(int rowNum) {
                this.idxRow = rowNum;
            }
            public List<Integer> getListColumnIndexVals() {
                return listIdxCol;
            }
            public List<Integer> getListVals() {
                return list;
            }
            public void setRow(List<Integer> listIdx, List<Integer> vals) {
                this.listIdxCol = listIdx;
                this.list = vals;
            }
            public Integer get(Integer idxCol) {
                int size = list.size();
                for(int i = 0; i < size; i++) {
                    Integer idx = listIdxCol.get(i);
                    if(idx == idxCol)
                        return list.get(i);
                }
                return null;
            }
            /**
             * If idx matches existing col, then replace it with val.
             * Else add the val to col.
             * 
             * @param idxCol
             * @param val
             */
            public void add(Integer idxCol, Integer val) {
                int size = list.size();
                boolean found = false;
                Integer prv = -1;
                Integer idxTarget = null;
                for(int i = 0; i < size && !found; i++) {
                    Integer idx = listIdxCol.get(i);
                    if(idxCol == idx) {
                        list.set(i, val);
                        return;
                    }
                    else if(prv < idxCol && idxCol < idx) {
                        idxTarget = i;
                        found = true;
                    }
                    prv = idx;
                }
                if(found) {
                    listIdxCol.add(idxTarget, idxCol);
                    list.add(idxTarget, val);
                    size = list.size();
                    for(int i = idxTarget + 1; i < size; i++) {
                        Integer idx = listIdxCol.get(i);
                        listIdxCol.set(i, idx + 1);
                    }
                }
                else {
                    listIdxCol.add(idxCol);
                    list.add(val);
                }
            }
            public boolean remove(Integer idxCol) {
                int size = list.size();
                boolean found = false;
                Integer idxTarget = null;
                Integer prv = -1;
                for(int i = 0; i < size && !found; i++) {
                    Integer idx = listIdxCol.get(i);
                    if(idxCol == idx) {
                        idxTarget = i;
                        listIdxCol.remove(i);
                        list.remove(i);
                        found = true;
                    }
                    else if(prv < idxCol && idxCol < idx) {
                        idxTarget = i;
                        found = true;
                    }
                    prv = idx;
                }
                if(found) {
                    size = list.size();
                    for(int i = idxTarget; i < size; i++) {
                        Integer idx = listIdxCol.get(i);
                        listIdxCol.set(i, idx - 1);
                    }
                }
                return false;
            }
            public void printFull() {
                p("printFull\n");
                if(list.size() == 0)
                    return;
                p("idx:");
                for(int i = 0, j = 0; i < list.size(); i++) {
                    Integer idx = listIdxCol.get(i);
                    while(j <= idx) {
                        p("%3d ", j);
                        j++;
                    }
                }
                p("\n");
                p("val:");
                for(int i = 0, j = 0; i < list.size(); i++) {
                    Integer idx = listIdxCol.get(i);
                    Integer val = list.get(i);
                    while(j <= idx) {
                        if(j != idx) {
                            p("%3d ", 0);
                        } else {
                            p("%3d ", val);
                        }
                        j++;
                    }
                }
                p("\n");
            }
            public void printCompressed() {
                p("printCompressed\n");
                p("idx:");
                for(int i = 0; i < list.size(); i++) {
                    p("%3d ", listIdxCol.get(i));
                }
                p("\n");
                p("val:");
                for(int i = 0; i < list.size(); i++) {
                    p("%3d ", list.get(i));
                }
                p("\n");
            }
        }
        
        int sizeRow;
        int sizeCol;
        List<Row> list = new ArrayList<>();
        
        public SimpleCompressedMatrix() {
        }

        public Integer get(int idxRow, int idxCol) {
            Row row = getRow(idxRow);
            if(row == null)
                return null;
            Integer val = row.get(idxCol);
            return val;
        }

        public Row getRow(int idxRow) {
            if(idxRow >= sizeRow)
                return null;
            for(int i = 0; i < list.size(); i++) {
                Row row = list.get(i);
                if(idxRow == row.rowNum()) 
                    return row;
            }
            return null;
        }
        
        public List<Integer> getCol(int idxCol) {
            return null;
        }

        public void deleteRow(int row) {
        }

        public void deleteCol(int col) {
        }

        public void delete(int row, int col) {
        }
        
        public int getSizeCol() {
            return sizeCol;
        }
        
        public int getSizeRow() {
            return sizeRow;
        }

        public void put(int idxRow, int idxCol, int v) {
            Row row = getRow(idxRow);
            if(row == null) {
                row = new Row(idxRow);
            }
            row.add(idxCol, v);

            if(idxRow > sizeRow)
                sizeRow = idxRow;
            if(idxCol > sizeCol)
                sizeCol = idxCol;
        }
        
        public void putRow(int row, List<Integer> listRow) {
        }
        
        public void putColumn(int col, List<Integer> listCol) {
        }
        
        public void putMatrix(int [][] a) {
        }
        
        public int getSize() {
            return 0;
        }
        
        public void printFull() {
        }
        
        public void printCompressed() {
        }
    }

    static class UncompressedMatrix {
        int sizeRow;
        int sizeCol;
        List<List<Integer>> listValues;
        public UncompressedMatrix() {
            listValues = new ArrayList<>();
            sizeRow = 0;
            sizeCol = 0;
        }
        public Integer get(int row, int col) {
            if(row > listValues.size()) {
                return null;
            }
            List<Integer> list = listValues.get(row);
            if(col > list.size()) {
                return null;
            }
            return list.get(col);
        }
        public List<Integer> getRow(int row) {
            if(row > listValues.size()) {
                return null;
            }
            return listValues.get(row);
        }
        public List<Integer> getCol(int col) {
            // need to copy elements of each row, since this is row based
            List<Integer> list = new ArrayList<>();
            if(col > sizeCol)
                return null;
            for(int i = 0; i < listValues.size(); i++) {
                List<Integer> listRow = listValues.get(i);
                list.add(listRow.get(col));
            }
            return list;
        }
        public void deleteRow(int row) {
            if(row >= sizeRow) {
                return;
            }
            listValues.remove(row);
        }
        public void deleteCol(int col) {
            if(col >= sizeCol) {
                return;
            }
            for(int i = 0; i < sizeRow; i++) {
                listValues.get(i).remove(col);
            }
        }
        public void delete(int row, int col) {
            if(row >= sizeRow || col >= sizeCol) {
                return;
            }
            listValues.get(row).set(col, 0);
        }
        public int getSizeCol() {
            return sizeCol;
        }
        public int getSizeRow() {
            return sizeRow;
        }
        public void put(int row, int col, int v) {
            // expand rows, if needed
            if(row >= sizeRow) {
                int numNewRows = row - sizeRow + 1;
                for(int i = 0; i < numNewRows; i++) {
                    List<Integer> list = new ArrayList<>();
                    for(int j = 0; j < sizeCol; j++) {
                        list.add(0);
                    }
                    listValues.add(list);
                }
                sizeRow = row + 1;
            }
            // expand columns, if needed
            if(col >= sizeCol) {
                int numNewCols = col - sizeCol + 1;
                for(int i = 0; i < sizeRow; i++) {
                    List<Integer> list = listValues.get(i);
                    for(int j = 0; j < numNewCols; j++) {
                        list.add(0);
                    }
                }
                sizeCol = col + 1;
            }
            listValues.get(row).set(col, v);
        }
        public void putRow(int row, List<Integer> listRow) {
            List<Integer> listCopy = new ArrayList<>(listRow);
            
            if(row >= sizeRow) {
                // add rows and adjust column to existing size or expand
                int numNewRows = row - sizeRow;
                for(int i = 0; i < numNewRows; i++) {
                    List<Integer> listRowGeneric = new ArrayList<>();
                    for(int j = 0; j < sizeCol; j++) {
                        listRowGeneric.add(0);
                    }
                    listValues.add(listRowGeneric);
                }
                listValues.add(listCopy);
            }
            else {
                // adjust column to existing size or expand
                listValues.add(row, listCopy);
            }
            sizeRow = listValues.size();

            if(listRow.size() >= sizeCol) {
                int numNewCols = listRow.size() - sizeCol + 1;
                for(int i = 0; i < sizeRow; i++) {
                    List<Integer> listRowCur = listValues.get(i);
                    for(int j = 0; j < numNewCols; j++) {
                        listRowCur.add(0);
                    }
                }
                sizeCol = listRow.size();
            }
        }
        public void putColumn(int col, List<Integer> listCol) {
            // add columns if needed
            if(col >= sizeCol) {
                int numNewCols = col - sizeCol;
                for(int i = 0; i < sizeRow; i++) {
                    List<Integer> listRow = listValues.get(i);
                    for(int j = 0; j < numNewCols; j++) {
                        listRow.add(0);
                    }
                }
                sizeCol = col + 1;
            }

            // add rows if needed
            int numRows = listCol.size();
            if(numRows > sizeRow) {
                int numNewRows = numRows - sizeRow;
                for(int i = 0; i < numNewRows; i++) {
                    List<Integer> listRow = new ArrayList<>();
                    for(int j = 0; j < sizeCol; j++) {
                        listRow.add(0);
                    }
                    listValues.add(listRow);
                }
                sizeRow = numRows;
            }

            // now add each element to corresponding row
            for(int i = 0; i < sizeRow; i++) {
                List<Integer> listRow = listValues.get(i);
                listRow.set(col, listCol.get(i));
            }
        }
        public void putMatrix(int [][] a) {
            listValues.clear();
            sizeRow = a.length;
            sizeCol = a[0].length;
            for(int i = 0; i < sizeRow; i++) {
                List<Integer> listRow = new ArrayList<>();
                for(int j = 0; j < sizeCol; j++) {
                    listRow.add(a[i][j]);
                }
                listValues.add(listRow);
            }
        }
        public int getSize() {
            int size = sizeRow * sizeCol;
            return size;
        }
        public void print() {
            p("PRINT MATRIX\n");
            p("    ");
            for(int i = 0; i < sizeCol; i++) {
                p("%3d ", i);
            }
            p("\n");
            p("    ");
            for(int i = 0; i < sizeCol; i++) {
                p("----");
            }
            p("\n");
            for(int i = 0; i < sizeRow; i++) {
                p("%3d|", i);
                List<Integer> listRow = listValues.get(i);
                for(int j = 0; j < sizeCol; j++) {
                    p("%3d ", listRow.get(j));
                }
                p("\n");
            }
            p("\n");
        }
    }

    static class CompressedMatrices {
        
    }

    static class ArrayAlgos {
        public void print(String f, Object ...a) {
            System.out.printf(f, a);
        }
        public void findBiggestValley(int [] a) {
            // find biggest valley between peaks where volume is biggest

            int min = a[0];
            int max = a[0];
            for(int i = 0; i < a.length; i++) {
                if(a[i] < min) {
                    min = a[i];
                }
                if(a[i] > max) {
                    max = a[i];
                }
            }
            // print
            {
                print("   ");
                for(int i = 0; i < max; i++) {
                    print("%2d", i);
                }
                print("\n");
                print("   %s\n", new String(new char[max]).replace("\0", "--"));
                for(int i = 0; i < a.length; i++) {
                    int sz = a[i];
                    print("%2d:%s\n", i, new String(new char[sz]).replace("\0", " *"));
                }            
            }
            int peakl = 0;
            int peakr = 0;
            int valley = 0;
            int maxVolume = 0;
            int prv = a[0];
            int cur = a[0];
            boolean isDown = false;
            boolean foundAValley = false;
            int peakl1 = 0;
            int peakr1 = 0;
            int valley1 = a[0];
            int state = 0;
            for(int i = 0; i < a.length; i++) {
                cur = a[i];
                if(cur != prv) {
                    isDown = (prv > cur) ? true : false;
                }
                if(state == 0) {      // left peak
                    if(peakl1 < cur) {
                        peakl1 = cur;
                    }
                    if(isDown) {
                        state = 1;
                    }
                }
                else if(state == 1) { // valley
                    if(valley1 > cur) {
                        valley1 = cur;
                    }
                    if(!isDown) {
                        state = 2;
                    }
                }
                else if(state == 2) { // right peak
                    if(peakr1 < cur) {
                        peakr1 = cur;
                    }
                    if(isDown) {
                        state = 3;
                    }
                }
                else if(state == 3) {
                    // get volume
                    int volume = 0;
                    int tmpMaxH = (peakl1 < peakr1) ? peakr1 : peakl1;
                    for(int j = peakl1; j <= peakr1; j++) {
                        if(a[j] < tmpMaxH) {
                            volume += (tmpMaxH - a[j]);
                        }
                    }
                    if(maxVolume < volume) {
                        maxVolume = volume;
                    }
                    state = 0;
                }
                if(state == 3) {
                    boolean isUpdate = !foundAValley;
                    // get volume
                    int volume = 0;
                    int tmpMaxH = (peakl1 < peakr1) ? peakr1 : peakl1;
                    for(int j = peakl1; j <= peakr1; j++) {
                        if(a[j] < tmpMaxH) {
                            volume += (tmpMaxH - a[j]);
                        }
                    }
                    if(maxVolume < volume) {
                        isUpdate = true;
                    }
                    if(isUpdate) {
                        peakl = peakl1;
                        peakr = peakr1;
                        peakl1 = peakr;
                        valley = valley1;
                        maxVolume = volume;
                        foundAValley = true;
                    }
                }
                prv = cur;
            }
            print("idx: l:%2d v:%2d r:%2d vol:%2d\n", 
                peakl, valley, peakr, maxVolume);
        }
        public void findL2RViews(int [] a) {
            /*
             * given array a, from left to right, find all items which are 
             * not obstructed by a bigger value on the right.
             */
            print("original list: ");
            for(int i = 0; i < a.length; i++) {
                print("%2d ", a[i]);
            }
            print("\n");
            LinkedList<Integer> ll = new LinkedList<>();
            for(int i = 0; i < a.length; i++) {
                if(i != 0) {
                    int h = a[i];
                    while(ll.size() != 0 && ll.peek() < h) {
                        ll.pop();
                    }
                    ll.push(h);
                } else {
                    ll.push(a[i]);
                }
            }
            print("final list: ");
            while(ll.size() != 0) {
                int h = ll.pollLast();
                print("%2d ", h);
            }
            print("\n");
        }
    }
    static class Array {
        int [][] a;
        int x;
        int y;
        int sz;
        public Array() {
            a = null;
            x = 0;
            y = 0;
            sz = 0;
        }
        public void generateRandomArray(int x, int y) {
            this.sz = x * y;
            this.x = x;
            this.y = y;
            this.a = new int[x][y];
            for(int i = 0, ctr = 0; i < x; i++) {
                for(int j = 0; j < y; j++) {
                    a[i][j] = ctr++;
                }
            }
        }
        public int getX() {
            return x;
        }
        public int getY() {
            return y;
        }
        public void printMatrix() {
            for(int i = 0; i < x; i++) {
                for(int j = 0; j < y; j++) {
                    print("%2d ", a[i][j]);
                }
                print("\n");
            }
        }
        public void printBorders(int x, int y) {
            if(x >= this.x || y >= this.y) {
                return;
            }
            for(int i = -1; i <= 1; i++) {
                int xtmp = x + i;
                if(xtmp < 0 || xtmp >= this.x) {
                    continue;
                }
                for(int j = -1; j <= 1; j++) {
                    int ytmp = y + j;
                    if(ytmp < 0 || ytmp >= this.y) {
                        continue;
                    }
                    print("%2d ", a[xtmp][ytmp]);
                }
                print("\n");
            }
        }
        public void printWrapAround(int x, int y) {
            if(x >= this.x || y >= this.y) {
                return;
            }
            for(int i = -1; i <= 1; i++) {
                int xtmp = x + i;
                if(xtmp == -1) {
                    xtmp = this.x-1;
                }
                else if(xtmp == this.x) {
                    xtmp = 0;
                }
                for(int j = -1; j <= 1; j++) {
                    int ytmp = y + j;
                    if(ytmp == -1) {
                        ytmp = this.y - 1;
                    }
                    else if(ytmp == this.y) {
                        ytmp = 0;
                    }
                    print("%2d ", a[xtmp][ytmp]);
                }
                print("\n");
            }
        }
    }
}

class GraphClass {
    public static void p(String f, Object ...a) {
        System.out.printf(f, a);
    }

    static class Node implements Comparable<Node> {
        private final int id;
        private final String name;
        public Node(int id) {
            this(id, null);
        }
        public Node(int id, String name) {
            this.id = id;
            this.name = name;
        }
        public String name() {
            return name;
        }
        public int id() {
            return id;
        }
        public Node clone() {
            return new Node(id, name);
        }
        @Override
        public int compareTo(Node node) {
            if(id < node.id())
                return -1;
            if(id > node.id())
                return 1;
            return 0;
        }
    }

    static class Edge implements Comparable<Edge> {
        private int src;
        private int dst;
        private int weight;
        private boolean isWeighted;
        public Edge() {
            this(-1, -1, 1, false);
        }
        public Edge(int dst) {
            this(-1, dst, 1, false);
        }
        public Edge(int dst, int weight) {
            this(-1, dst, weight, true);
        }
        private Edge(int src, int dst, int weight, boolean isWeighted) {
            this.src = src;
            this.dst = dst;
            this.weight = weight;
            this.isWeighted = isWeighted;
        }
        public Edge src(int src) {
            this.src = src;
            return this;
        }
        public Edge dst(int dst) {
            this.dst = dst;
            return this;
        }
        public Edge weight(int weight) {
            this.weight = weight;
            this.isWeighted = true;
            return this;
        }
        public int srcId() {
            return src;
        }
        public int dstId() {
            return dst;
        }
        public int id() {
            return dst;
        }
        public int weight() {
            return weight;
        }
        public Edge clone() {
            return new Edge(src, dst, weight, isWeighted);
        }
        @Override
        public int compareTo(Edge edge) {
            if(weight < edge.weight())
                return -1;
            if(weight > edge.weight())
                return 1;
            return 0;
        }
    }

    static class Graph implements Comparable<Graph> {
        final Map<Integer, Node> nodes;
        final Map<Node, Map<Integer, Edge>> mapEdges;
        Map<Integer, Set<Integer>> mapOBSimple_ = new HashMap<>();
        Map<Integer, Set<Integer>> mapIBSimple_ = new HashMap<>();
        boolean isWeighted;
        public Graph() {
            nodes = new HashMap<>();
            mapEdges = new HashMap<>();
        }
        public Graph(
            Map<Integer, Node> srcNodes, 
            Map<Node, Map<Integer, Edge>> srcEdges) {
            nodes = new HashMap<>();
            mapEdges = new HashMap<>();
            copySets(srcNodes, srcEdges, nodes, mapEdges);
        }
        public Graph(Map<Integer, Set<Integer>> map) {
            nodes = new HashMap<>();
            mapEdges = new HashMap<>();
            load(map);
        }
        public void reset() {
            nodes.clear();
            mapEdges.clear();
        }
        public int sizeNodes() {
            return nodes.size();
        }
        public Graph copy() {
            return new Graph(nodes, mapEdges);
        }
        public void copySets(
            Map<Integer, Node> nodesSrc,
            Map<Node, Map<Integer, Edge>> edgesSrc,
            Map<Integer, Node> nodesDst,
            Map<Node, Map<Integer, Edge>> edgesDst) {
            // make a clone of each src id/node mapping
            for(Map.Entry<Integer, Node> kv: nodesSrc.entrySet()) {
                int k = kv.getKey();
                Node node = kv.getValue().clone();
                nodesDst.put(k, node);
            }
            // make a clone of each edge map
            for(Map.Entry<Node, Map<Integer, Edge>> kvNodeToEdges: edgesSrc.entrySet()) {
                Node nodeSrc = kvNodeToEdges.getKey();
                Map<Integer, Edge> mapEdges = kvNodeToEdges.getValue();

                // make a new map of edges for this Node
                Map<Integer, Edge> newEdges = new HashMap<>();
                for(Map.Entry<Integer, Edge> kvIdEdge: mapEdges.entrySet()) {
                    int idEdge = kvIdEdge.getKey();
                    Edge edgeSrc = kvIdEdge.getValue();
                    newEdges.put(idEdge, edgeSrc.clone());
                }

                // get the dst map Object, not the src object.
                int idSrc = nodeSrc.id();
                Node nodeDst = nodesDst.get(idSrc);
                edgesDst.put(nodeDst, newEdges);
            }
        }
        /**
         * compareTo:
         * 
         * if all nodes are equal and all edges are equal then return 0.
         * 
         * if this has less nodes then return -1.
         * if this has more nodes then return 1
         * if all nodes equal but this has less edges then return -2
         * if all nodes equal but this has more edges then return 2
         * if node id mismatch, return -3
         * if edge mismatch for any node, return -4
         * if edge sizes match but edge ids mismatch, return -5
         */
        @Override
        public int compareTo(Graph g) {
            List<Integer> keyListThis  = new ArrayList<>(nodes.keySet());
            Set<Integer> keySetThat  = g.getAllNodeIds();
            if(keyListThis.size() < keySetThat.size())
                return -1;
            if(keyListThis.size() > keySetThat.size())
                return 1;

            for(Integer id: keyListThis) {
                if(!keySetThat.contains(id)) {
                    return -3;
                }                
            }
            for(Integer id: keyListThis) {
                Set<Integer> setEdgesThis = this.getEdgesIds(id);
                Set<Integer> setEdgesThat = g.getEdgesIds(id);
                if(setEdgesThis.size() != setEdgesThat.size())
                    return -4;
                for(Integer iddst: setEdgesThis) {
                    if(!setEdgesThat.contains(iddst))
                        return -5;
                }
            }
            return 0;
        }
        public void load(Map<Integer, Set<Integer>> map) {
            Set<Integer> setKeys = map.keySet();
            loadAllNodes(setKeys);
            loadAllEdges(map);
        }
        public void loadAllNodes(Set<Integer> ids) {
            nodes.clear();
            mapEdges.clear();
            for(Integer id: ids) {
                Node node = new Node(id);
                nodes.put(id, node);
                mapEdges.put(node, new HashMap<Integer, Edge>());
            }
        }
        public boolean loadAllEdges(Map<Integer, Set<Integer>> edges) {
            mapEdges.clear();
            for(Map.Entry<Integer, Set<Integer>> kv: edges.entrySet()) {
                int idSrc = kv.getKey();
                Set<Integer> idsDst = kv.getValue();
                Node nodeSrc = nodes.get(idSrc);
                if(nodeSrc == null) {
                    mapEdges.clear();
                    return false;
                }
                Map<Integer, Edge> mapEdge = new HashMap<>();
                for(Integer idDst: idsDst) {
                    if(nodes.get(idDst) == null) {
                        mapEdges.clear();
                        return false;
                    }
                    Edge edgeSrc = new Edge(idDst);
                    mapEdge.put(idDst, edgeSrc);
                }
                mapEdges.put(nodeSrc, mapEdge);
            }
            return true;
        }
        public Set<Integer> getAllHeadIds() {
            Set<Integer> setNodes = new HashSet<>(nodes.keySet());
            Set<Integer> set = new HashSet<>(nodes.keySet());
            // for each id, get its edges. exclude edges from set.
            // the remainder will be heads.
            for(Integer idsrc: setNodes) {
                Set<Integer> edges = getEdgesIds(idsrc);
                for(Integer iddst: edges) {
                    if(set.contains(iddst))
                        set.remove(iddst);
                }
            }
            return set;
        }
        public Set<Integer> getAllTailIds() {
            Set<Integer> setNodes = new HashSet<>(nodes.keySet());
            Set<Integer> set = new HashSet<>();
            // for each id, get its edges. exclude edges from set.
            // the remainder will be heads.
            for(Integer idsrc: setNodes) {
                Set<Integer> edges = getEdgesIds(idsrc);
                if(edges.isEmpty())
                	set.add(idsrc);
            }
            return set;
        }
        public Node getNode(Integer id) {
            return nodes.get(id);
        }
        public void addNode(Integer id) {
            if(!nodes.containsKey(id)) {
                Node node = new Node(id);
                nodes.put(id, node);
                mapEdges.put(node, new HashMap<Integer,Edge>());
            }
        }
        public boolean addEdge(Node node, Edge edge) {
            if(!mapEdges.containsKey(node))
                return false;
            Integer idEdge = edge.dstId();
            if(mapEdges.get(node).containsKey(idEdge))
                return false;
            mapEdges.get(node).put(idEdge, edge);
            return true;
        }
        public boolean addEdge(Integer src, Integer dst, boolean isDirected) {
            return addEdge(src, dst, 1, isDirected);
        }
        public boolean addEdge(Integer src, Integer dst, int weight, boolean isDirected) {
            if(!nodes.containsKey(src) || !nodes.containsKey(dst)) {
                return false;
            }
            Node nodeSrc = nodes.get(src);
            Map<Integer, Edge> kvSrc = mapEdges.get(nodeSrc);
            if(!kvSrc.containsKey(dst)) {
                Edge edgeSrc = new Edge(dst, weight);
                kvSrc.put(dst, edgeSrc);
            }
            if(!isDirected) {
                Node nodeDst = nodes.get(dst);
                Map<Integer, Edge> kvDst = mapEdges.get(nodeDst);
                if(!kvDst.containsKey(src)) {
                    Edge edgeDst = new Edge(src, weight);
                    kvDst.put(src, edgeDst);
                }
            }
            return true;
        }
        public void addEdgeSet(Integer src, Set<Integer> dsts) {
            for(Integer dst: dsts) {
                addEdge(src, dst, false);
            }
        }
        public Set<Integer> getAllNodeIds() {
            Set<Integer> set = new HashSet<>(nodes.keySet());
            return set;
        }
        public Set<Node> getNodes() {
            Set<Node> set = new HashSet<>(nodes.values());
            return set;
        }
        public Set<Edge> getEdges(Integer id) {
            if(!nodes.containsKey(id)) {
                return null;
            }
            Node node = nodes.get(id);
            if(!mapEdges.containsKey(node)) {
                return null;
            }
            Set<Edge> set = new HashSet<>(mapEdges.get(node).values());
            return set;
        }
        /**
         * getEdgesIds returns the set of edge ids for a node id.
         * 
         * @param id
         * @return
         */
        public Set<Integer> getEdgesIds(Integer id) {
            if(!nodes.containsKey(id))
                return null;
            Node node = nodes.get(id);
            if(!mapEdges.containsKey(node))
                return null;
            Map<Integer, Edge> map = mapEdges.get(node);
            Set<Integer> set = new HashSet<>(map.keySet());
            return set;
        }
        public Map<Node, Map<Integer, Edge>> getAllEdges() {
            Map<Node, Map<Integer, Edge>> map = new HashMap<>();
            for(Map.Entry<Node, Map<Integer, Edge>> kv: mapEdges.entrySet()) {
                map.put(kv.getKey(), new HashMap<>(kv.getValue()));
            }
            return map;
        }
        public void printGraph() {
            /**
             * prints form 
             * NODE:ID1 EDGES: ID1,ID2,IDn
             * NODE:ID2 EDGES: ID1,ID2,IDn
             */
            p("PRINT BASIC GRAPH\n");
            for(Node node: nodes.values()) {
                Collection<Edge> edges = mapEdges.get(node).values();
                boolean isFirst = true;
                p("NODE:%2d; EDGES:", node.id());
                for(Edge edge: edges) {
                    if(isFirst) {
                        isFirst = false;
                        p("%2d", edge.dstId());
                    } else {
                        p(",%2d", edge.dstId());
                    }
                }
                p("\n");
            }
            p("\n");
        }
    }
    
    static class GraphAlgos {
        Random r;
        public GraphAlgos() {
            r = new Random();
        }
        public Graph getMST(Graph g) {
            /**
             * Generates a new MST Graph based on input graph.
             * 
             * Use Kruskal greedy, find min spanning forest, using
             * disjoint set data structure.
             * 
             */
            int algorithmToUse = 0;

            if(algorithmToUse == 0)
                return getMSTPrim(g);
            else if(algorithmToUse == 1)
                return getMSTKruskal(g);
            else
                return getMSTPrim(g);
        }
        /**
         * Generates a new MST Graph based on input graph.
         * 
         * Use Kruskal greedy, find min spanning forest, using
         * disjoint set data structure.
         */
        private Graph getMSTKruskal(Graph g) {
            Graph newGraph = new Graph();
            return newGraph;
        }
        /**
         * Generates a new MST Graph based on input.
         * 
         * Use Prim greedy.
         * O(E log N)
         * 
         * init tree with single node (arbitrary).
         * grow tree by one edge. of edges that connect tree to 
         * nodes not yet in tree, find min edge weight.
         * repeat until all vertices are in tree.
         */
        private Graph getMSTPrim(Graph g) {
            // pick arbitrary node
            int numNodes = g.sizeNodes();
            int idx = r.nextInt(numNodes);
            final Set<Integer> setNodes = g.getAllNodeIds();
            Set<Integer> setIdsRemaining = new HashSet<>(setNodes);
            PriorityQueue<Edge> pq = new PriorityQueue<>();
            Graph newGraph = new Graph();
            newGraph.loadAllNodes(setNodes);

            getMSTPrimHelper(g, newGraph, setIdsRemaining, pq, idx);
            return newGraph;
        }
        private void getMSTPrimHelper(
            Graph g, 
            Graph newGraph, 
            Set<Integer> setIdsRemaining,
            PriorityQueue<Edge> pq, /* min priority queue is default. */
            int idx) {
            /*
             * do DFS. choose an edge from priority queue that is
             * both min and also not yet added to newGraph/still
             * exists in setIdsRemaining.
             */
            Node node = g.getNode(idx);
            if(node == null)
                return;
            if(!setIdsRemaining.contains(idx))
                return;
            
            int ctr = 0;
            int idSrc = idx;
            while(setIdsRemaining.size() != 0 && ctr < 10000) {
                setIdsRemaining.remove(idSrc);

                // get all edges from this node, add to priority queue.
                Collection<Edge> edges = g.getEdges(idSrc);
                for(Edge edge: edges) {
                    Edge newEdge = new Edge()
                        .src(idSrc).dst(edge.dstId()).weight(edge.weight());
                    pq.add(newEdge);
                }
                
                // get the next edge with lowest weight that is not in
                // new graph, and is still in remaining id set.
                int idDst = -1;
                Edge edge = null;
                boolean foundEligibleEdge = false;
                while(!foundEligibleEdge) {
                    if(pq.size() == 0) 
                        return;
                    edge = pq.poll();
                    idDst = edge.dstId();
                    idSrc = edge.srcId();
                    if(setIdsRemaining.contains(idDst))
                        foundEligibleEdge = true;
                }
                if(foundEligibleEdge) {
                    newGraph.addEdge(idSrc, idDst, edge.weight(), false);
                    getMSTPrimHelper(g, newGraph, setIdsRemaining, pq, idDst);
                }
                ctr++;
            }
        }
        public void getAllMST(Graph g) {
            
        }
        public List<Integer> findShortestPath(Graph g, int src, int dst) {
            List<Integer> list = new ArrayList<>();
            return list;
        }
        public List<Integer> findMinPath(Graph g, int src, int dst) {
            List<Integer> list = new ArrayList<>();
            return list;
        }
        public List<Integer> findBFSPath(Graph g, int src, int dst) {
            List<Integer> list = new ArrayList<>();
            int sz = g.sizeNodes();
            int [] a = new int[sz];
            for(int i = 0; i < sz; i++) {
                a[i] = -1;
            }
            LinkedList<Integer> ll = new LinkedList<>();
            ll.add(src);
            while(ll.size() != 0) {
                Integer idsrc = ll.poll();
                if(idsrc == dst) {
                    int cur = idsrc;
                    while(a[cur] != -1) {
                        list.add(cur);
                        cur = a[cur];
                    }
                    list.add(cur);
                    break;
                }
                Set<Integer> setEdges = g.getEdgesIds(idsrc);
                for(Integer iddst: setEdges) {
                    if(a[iddst] == -1) {
                        a[iddst] = idsrc;
                        ll.add(iddst);
                    }
                }
            }
            // now invert the list if list is not empty.
            if(list.isEmpty()) 
            	return list;
            int idxH = 0;
            int idxT = list.size()-1;
            while(idxH < idxT) {
            	Integer h = list.get(idxH);
            	Integer t = list.get(idxT);
            	list.set(idxH, t);
            	list.set(idxT, h);
            	idxH++;
            	idxT--;
            }
            return list;
        }
        public List<Integer> findDFSPath(Graph g, int src, int dst) {
            List<Integer> list = new ArrayList<>();
            int sz = g.sizeNodes();
            int [] a = new int[sz];
            for(int i = 0; i < sz; i++) {
                a[i] = -1;
            }
            findDFSPath(g, src, dst, a, list);
            return list;
        }
        private void 
        findDFSPath(Graph g, int src, int dst, int [] a, List<Integer> list) {
            if(src == dst) {
                int cur = src;
                while(a[cur] != -1) {
                    list.add(cur);
                    cur = a[cur];
                }
                return;
            }
            Set<Integer> setEdges = g.getEdgesIds(src);
            for(Integer iddst: setEdges) {
                if(a[iddst] == -1) {
                    a[iddst] = src;
                    findDFSPath(g, iddst, dst, a, list);
                }
            }
        }
        public List<Integer> findAllPaths(Graph g, int src, int dst) {
            List<Integer> list = new ArrayList<>();
            return list;
        }
        /**
         * Tarjan Bridge.
         * 
         * @param g
         * @return
         */
        public List<Integer> findBridge(Graph g) {
            int numNodes = g.sizeNodes();
            AtomicInteger numBridges = new AtomicInteger(0);
            AtomicInteger cnt = new AtomicInteger(0);
            int [] pre = new int[numNodes]; // order in which dfs examines v
            int [] low = new int[numNodes]; // lowest preorder of any vertex connected to v
            List<Integer> list = new ArrayList<>();
            for(int i = 0; i < numNodes; i++) {
                pre[i] = -1;
                low[i] = -1;
            }
            for(int i = 0; i < numNodes; i++) {
                if(pre[i] == -1) {
                    findBridge(g, numBridges, cnt, pre, low, i, i, list);
                }
            }
            return list;
        }
        private void findBridge(
            Graph g, 
            AtomicInteger numBridges,
            AtomicInteger cnt,
            int [] pre,
            int [] low,
            int parent,
            int v,
            List<Integer> list
        ) {
            /* 
             * 	low[src] = pre[src] = count++
             * 
             * 	for each dst = edge of src
             * 		if visited
             * 			low[src] = min(low[src], pre[src])
             * 		else
             * 			find(...)
             * 			low[src] = min(low[src], low[dst])
             * 			if low[dst] == pre[dst]
             * 				is a bridge
             * 
             */
            pre[v] = cnt.getAndIncrement();
            low[v] = pre[v];
            Set<Integer> setEdgeIds = g.getEdgesIds(v);
            for(Integer iddst: setEdgeIds) {
                if(pre[iddst] == -1) {
                    findBridge(g, numBridges, cnt, pre, low, v, iddst, list);
                    low[v] = Math.min(low[v], low[iddst]);
                    if(low[iddst] == pre[iddst]) {
                        numBridges.getAndIncrement();
                        p("%d - %d is bridge\n", v, iddst);
                        list.add(iddst);
                    }
                }
                else if(iddst != parent) {
                    low[v] = Math.min(low[v], pre[iddst]);
                }
            }
        }
        public List<Integer> findAllBridges(Graph g) {
            List<Integer> list = new ArrayList<>();
            return list;
        }
        public List<Integer> isDirectedAcyclic(Graph g) {
            List<Integer> list = new ArrayList<>();
            int sz = g.sizeNodes();
            int [] a = new int[sz];
            for(int i = 0; i < sz; i++) {
                a[i] = -1;
            }
            // find all nodes that do not have any inbound
            // if non exist, then it is not acyclic
            Set<Integer> setHeads = g.getAllHeadIds();
            for(Integer idsrc: setHeads) {
                isDirectedAcyclic(g, idsrc, a, list);
            }
            return list;
        }
        private void 
        isDirectedAcyclic(Graph g, int src, int [] a, List<Integer> list) {
            Set<Integer> setEdges = g.getEdgesIds(src);
            for(Integer iddst: setEdges) {
                if(a[iddst] == -1) {
                    a[iddst] = src;
                    isDirectedAcyclic(g, iddst, a, list);
                } else {
                    int cur = iddst;
                    while(a[cur] != -1) {
                        list.add(cur);
                        cur = a[cur];
                    }
                    return;
                }
            }
        }
        /**
         * topological sorting.
         * if is undirected then false.
         * if is directed but cyclic then false.
         * else true.
         * 
         * tsort is dfs of all nodes.
         * 
         * @param g
         * @return
         */
        public List<Integer> topologicalSortRecursive(Graph g) {
            List<Integer> list = new ArrayList<>();
            int sz = g.sizeNodes();
            // find all nodes with no incoming
            Set<Integer> setHeads = g.getAllHeadIds();
            Set<Integer> setTails = g.getAllTailIds();
            if(StandaloneTest.debug_) {
                p("ALL HEADS\n");
                for(Integer v: setHeads) {
                	p("%d ", v);
                }
                p("\n");
                p("ALL TAILS\n");
                for(Integer v: setTails) {
                	p("%d ", v);
                }
                p("\n");
            }
            
            // check if dag while doing dfs
            int [] visited = new int[sz];
            for(int i = 0; i < sz; i++) {
                visited[i] = -1;
            }
        	// do iteratively
            for(Integer id: setHeads) {
            	Set<Integer> setEdges = g.getEdgesIds(id);
            	
            }
            // do recursively
            Set<Integer> setStack = new HashSet<>();
            for(Integer id: setHeads) {
            	if(!topologicalSort(g, visited, id, list, setStack))
            		return null;
            }
            return list;
        }
        private boolean topologicalSort(
        	Graph g, 
        	int [] visited, 
        	int id, 
        	List<Integer> l, 
        	Set<Integer> setStack) {
        	if(setStack.contains(id)) {
        		return false;
        	}
        	if(visited[id] != -1) {
        		return true;
        	}
        	setStack.add(id);
        	visited[id] = 1;
        	l.add(id);
        	Set<Integer> setEdges = g.getEdgesIds(id);
        	for(Integer iddst: setEdges) {
        		if(!topologicalSort(g, visited, iddst, l, setStack)) {
        			return false;
        		}
        	}
        	setStack.remove(id);
        	return true;
        }
        public List<Integer> topologicalSortIterative(Graph g) {
            List<Integer> list = new ArrayList<>();
            int sz = g.sizeNodes();
            // find all nodes with no incoming
            Set<Integer> setHeads = g.getAllHeadIds();
            if(StandaloneTest.debug_) {
                p("ALL HEADS\n");
                for(Integer v: setHeads) {
                	p("%d ", v);
                }
                p("\n");
            }
            
            // check if dag while doing dfs
            int [] visited = new int[sz];
            for(int i = 0; i < sz; i++) {
                visited[i] = -1;
            }
        	// do iteratively
            Set<Integer> setStack = new HashSet<>();
            Stack<Integer> stack = new Stack<>();
            LinkedList<Integer> ll = new LinkedList<>(setHeads);
            
            while(!ll.isEmpty()) {
            	Integer id = ll.poll();
            	stack.push(id);
            	Set<Integer> setEdges = g.getEdgesIds(id);
            	for(Integer iddst: setEdges) {
            		stack.push(iddst);
            	}
            	
            	if(setStack.contains(id)) {
            		return null;
            	}
            	if(visited[id] != -1) {
            		continue;
            	}
            	visited[id] = 1;
            	setStack.add(id);
            	stack.push(id);
            	setStack.remove(id);
            }
            
            for(Integer id: setHeads) {
            	
            	setStack.add(id);
            	stack.push(id);
            	
            }
            return list;
        }

        /**
         * generateRandomGraph.
         * 
         * @param numNodes number of nodes.
         * @param isDirected is directed.
         * @param isDAG is acyclic, valid only if isDirected = true
         * @param maxInbound number of inbound edges per node, -1 if invalid.
         * @param maxOutbound nubmer of outbound edges per node, -1 if invalid.
         * @param numHeads number of start nodes with no inbound, -1 if invalid.
         * @param numTails number of end nodes with no outbound, -1 if invalid.
         */
        public Graph
        generateRandomGraph(
                int numNodes,
                boolean isDirected,
                boolean isDAG,
                boolean isSelfAllowed,
                int maxInbound,
                int maxOutbound, 
                int numHeads,
                int numTails) {
            {
                // validate if parameters are doable
                if(numHeads != -1 && numTails != -1) {
                    int numHT = numHeads + numTails;
                    if(numHT > numNodes) 
                        return null;
                }
                if(maxInbound != -1 && maxOutbound != -1) {
                    if(maxInbound > numNodes || maxOutbound > numNodes) 
                        return null;
                    if(maxInbound == 0 || maxOutbound == 0) 
                        return null;
                }
            }
            if(!isDirected) {
                return generateUndirectedRandomGraph(numNodes, maxInbound);                
            }
            return generateDirectedRandomGraph(
                numNodes, isDAG, isSelfAllowed, maxInbound, maxOutbound, numHeads, numTails);
        }

        /**
         * Graph generateDirectedRandomGraph
         * 
         * @param numNodes
         * @param isDAG
         * @param maxIbound
         * @param maxObound
         * @param numH
         * @param numT
         * @return
         */
        private Graph
        generateDirectedRandomGraph(
            int numNodes,
            boolean isDAG,
            boolean isSelfAllowed,
            int maxIbound,
            int maxObound,
            int numH,
            int numT
        ) {
            LinkedHashMap<Integer, Set<Integer>> mapob = new LinkedHashMap<>();
            LinkedHashMap<Integer, Set<Integer>> mapib = new LinkedHashMap<>();
            LinkedHashMap<Integer, Set<Integer>> map = new LinkedHashMap<>();
            
            for(int i = 0; i < numNodes; i++) {
                map.put(i, new HashSet<Integer>());
                mapib.put(i, new HashSet<Integer>());
                mapob.put(i, new HashSet<Integer>());
            }
            
            Set<Integer> setHeads = new HashSet<>();
            Set<Integer> setTails = new HashSet<>();
            // select head and tail sets
            {
                for(int i = 0; i < numH; i++) {
                    setHeads.add(i);
                }
                for(int i = 0; i < numT; i++) {
                    int j = numNodes - 1 - i;
                    setTails.add(j);
                }
            }

            /*
             * for !DAG case
             * 
             * randomly select numH nodes as heads
             * randomly select numT nodes as tails
             * 
             * for each node as src
             *     if node is in tails
             *         continue
             *     get candidate set as dst set
             *         for each node
             *             if node is in heads
             *                 continue
             *             if node not connected to src
             *             and dst numinbound < maxinbound
             *                 add node to set
             *     numconnections = rand(1, max)
             *     for i to numconnections
             *         choose random node from dst set
             *         delete node from dst set
             *         connect
             */
            if(!isDAG){
                Set<Integer> setNodes = new HashSet<>(map.keySet());
                
                // add edges for each src node
                for(Integer idsrc: setNodes) {
                    if(setTails.contains(idsrc))
                        continue;
                    
                    /*
                     * gather all valid nodes
                     * 
                     * 1. remove heads from set
                     * 2. remove from set if max inbound reached
                     * 3. remove from set if already part of outbound
                     */
                    Set<Integer> setValid = new HashSet<>(map.keySet());
                    List<Integer> listDst = new ArrayList<>(map.keySet());
                    for(int i = 0; i < listDst.size(); i++) {
                        Integer iddst = listDst.get(i);
                        if(setHeads.contains(iddst)) 
                            setValid.remove(iddst);
                        else if(mapib.get(iddst).size() >= maxIbound) 
                            setValid.remove(iddst);
                        else if(mapob.get(idsrc).contains(iddst)) 
                            setValid.remove(iddst);
                        else if(!isSelfAllowed && idsrc == iddst)
                            setValid.remove(iddst);
                    }

                    // target number of edges to create for this src node
                    int numEdgesTarget = (setValid.size() < maxObound) ?
                        r.nextInt(setValid.size()) + 1:
                        r.nextInt(maxObound) + 1;
                        
                    // convert set to list and randomly choose from it
                    List<Integer> listTargetDst = new ArrayList<>(setValid);
                    Set<Integer> setSrc = map.get(idsrc);
                    for(int i = 0; i < numEdgesTarget; i++) {
                        int idxDstTarget = r.nextInt(listTargetDst.size());
                        int idxDstValue = listTargetDst.get(idxDstTarget);
                        setSrc.add(idxDstValue);
                        mapob.get(idsrc).add(idxDstValue);
                        mapib.get(idxDstValue).add(idsrc);
                        listTargetDst.remove(idxDstTarget);
                    }
                }
            }
            /*
             * The DAG case is same as !DAG case, except select
             * dst nodes that are greater than current src node,
             * while !DAG selects dst nodes from entire set.
             */
            if(isDAG){
                List<Integer> listNodes = new ArrayList<>(map.keySet());
                
                // add edges for each src node
                for(int k = 0; k < listNodes.size(); k++) {
                    Integer idsrc = listNodes.get(k);
                    if(setTails.contains(idsrc))
                        continue;
                    
                    /*
                     * gather all valid nodes after src idx because DAG
                     * 
                     * 1. remove heads from set
                     * 2. remove from set if max inbound reached
                     * 3. remove from set if already part of outbound
                     */
                    Set<Integer> setValid = new HashSet<>(map.keySet());
                    for(int i = 0; i < listNodes.size(); i++) {
                        Integer iddst = listNodes.get(i);
                        if(i <= k) 
                            setValid.remove(iddst);
                        else if(setHeads.contains(iddst))
                            setValid.remove(iddst);
                        else if(mapib.get(iddst).size() >= maxIbound) 
                            setValid.remove(iddst);
                        else if(mapob.get(idsrc).contains(iddst)) 
                            setValid.remove(iddst);
                        else if(!isSelfAllowed && idsrc == iddst)
                            setValid.remove(iddst);
                    }

                    // target number of edges to create for this src node
                    if(setValid.size() == 0) {
                        continue;
                    }
                    int numEdgesTarget = (setValid.size() < maxObound) ?
                        r.nextInt(setValid.size()) + 1:
                        r.nextInt(maxObound) + 1;
                        
                    // convert set to list and randomly choose from it
                    List<Integer> listTargetDst = new ArrayList<>(setValid);
                    Set<Integer> setSrc = map.get(idsrc);
                    for(int i = 0; i < numEdgesTarget; i++) {
                        int idxDstTarget = r.nextInt(listTargetDst.size());
                        int idxDstValue = listTargetDst.get(idxDstTarget);

                        setSrc.add(idxDstValue);
                        mapob.get(idsrc).add(idxDstValue);
                        mapib.get(idxDstValue).add(idsrc);
                        listTargetDst.remove(idxDstTarget);
                    }
                }
            }

            // convert map of src node->set of dst node to Graph
            Graph graph = new Graph(map);
            return graph;
        }

        /**
         * Graph generateUndirectedRandomGraph
         * 
         * @param numNodes
         * @param maxConnectivity
         * @return
         */
        private Graph
        generateUndirectedRandomGraph(int numNodes, int maxConnectivity) {
            LinkedHashMap<Integer, Set<Integer>> map = new LinkedHashMap<>();
            
            for(int i = 0; i < numNodes; i++) {
                map.put(i, new HashSet<>());
            }
            
            /*
             * for each node as src
             *     look for candidate dst to connect to
             *     for each node as dst
             *         if dst does not already have src
             *         and if dst num connections < max
             *             add to candidate list
             *             self loop is possible
             *     numConnections = rand(1,max)
             *     for i to numConnections
             *         randomly choose dst from candidate list
             *         delete dst from list
             */
            int maxBounds = maxConnectivity - 1;
            for(int idsrc = 0; idsrc < numNodes; idsrc++) {
                Set<Integer> setEdgesSrc = map.get(idsrc);
                if(setEdgesSrc.size() >= maxConnectivity){
                    continue;
                }

                List<Integer> listValid = new ArrayList<>();
                
                // get all nodes where num edges < maxConnectionPerNode
                // self selection is possible
                for(Map.Entry<Integer, Set<Integer>> kv: map.entrySet()) {
                    Integer iddst = kv.getKey();
                    Set<Integer> set = kv.getValue();
                    if(set.contains(idsrc))
                        continue;
                    if(set.size() >= maxConnectivity) 
                        continue;
                    listValid.add(iddst);
                }
                
                // get random num connections and connect to valid set
                // and remove from valid set once used.
                int numConnections = r.nextInt(maxBounds) + 1;
                for(int i = 0; i < numConnections; i++) {
                    if(listValid.size() == 0)
                        break;
                    int targetIdx = r.nextInt(listValid.size());
                    int targetId = listValid.get(targetIdx);
                    setEdgesSrc.add(targetId);
                    Set<Integer> setEdgesDst = map.get(targetId);
                    setEdgesDst.add(idsrc);
                    listValid.remove(targetIdx);
                }
            }
            Graph graph = new Graph(map);
            return graph;
        }
        
    }
}
class GraphOld {
    static class Graph {
        public static void print(String f, Object ...a) {
            System.out.printf(f, a);
        }
        public static void p(String f, Object ...a) {
            System.out.printf(f, a);
        }
        int id;
        Random rand = null;
        Set<Node> setNodes;
        Set<NodeWeighted> setNodesWeighted;
        List<Integer> v;
        Set<Edge> edges;
        
        public Graph() {
            reset();
            rand = new Random();
            v = new ArrayList<>();
            edges = new HashSet<>();
        }
        
        public void reset() {
            id = 0;
            setNodes = new LinkedHashSet<>();
            setNodesWeighted = new LinkedHashSet<>();
        }

        class Edge {
            NodeWeighted nDst;
            int wt;
            public Edge(NodeWeighted dst, int wt) {
                nDst = dst;
                this.wt = wt;
            }
            public Edge(NodeWeighted dst) {
                nDst = dst;
                wt = 0;
            }
            @Override
            public boolean equals(Object o) {
                if(o == this) {
                    return true;
                }
                if(!(o instanceof Edge)) {
                    return false;
                }
                Edge e = (Edge)o;
                if(nDst.equals(e.nDst)) {
                    return true;
                }
                return false;
            }
            @Override
            public int hashCode() {
                return Objects.hash(nDst);
            }
        }

        class NodeWeighted {
            Set<Edge> edges = new HashSet<>();
            Set<NodeWeighted> edgeNodes = new HashSet<>();
            int idNode = 0;
            int numOut = 0;
            int numIn = 0;
            public NodeWeighted() {
                idNode = id++;
            }
            public int numEdges() {
                return edges.size();
            }
            public int numInbound() {
                return numIn;
            }
            public int numOutbound() {
                return numOut;
            }
            public void incNumInbound() {
                numIn++;
            }
            public void incNumOutbound() {
                numOut++;
            }
            public void 
            addEdge(
                NodeWeighted n, 
                boolean isDirected,
                int weight)
            {
                if(edgeNodes.contains(n)) {
                    return;
                }
                Edge edge = new Edge(n, weight);
                edges.add(edge);
                edgeNodes.add(n);
                incNumOutbound();
                n.incNumInbound();
                if(!isDirected) {
                    n.addEdge(this, true, weight);
                }
            }
        }

        class Node {
            Set<Node> edges = new HashSet<>();
            int idNode = 0;
            int numOut = 0;
            int numIn = 0;
            public Node() {
                this(id++);
            }
            public Node(int id) {
                idNode = id;
            }
            public int numEdges() {
                return edges.size();
            }
            public int numInbound() {
                return numIn;
            }
            public int numOutbound() {
                return numOut;
            }
            public void incNumInbound() {
                numIn++;
            }
            public void incNumOutbound() {
                numOut++;
            }
            public void addEdge(Node n, boolean isDirected) {
                if(edges.contains(n)) {
                    return;
                }
                edges.add(n);
                incNumOutbound();
                n.incNumInbound();
                if(!isDirected) {
                    n.addEdge(this, true);
                }
            }
            public List<Node> getEdges() {
                List<Node> l = new ArrayList<>();
                for(Node n: edges) {
                    l.add(n);
                }
                return l;
            }
            public boolean hasEdge(Node n) {
                return edges.contains(n);
            }
        }
        
        public void
        loadGraphFromFile(String filename) {
            /**
             * File format
             * 
             * GRAPH\n
             * nodename1:adjnode1,adjnode2,adjnode3
             * nodename2:adjnode1,adjnode2,adjnode3
             * nodename3:adjnode1,adjnode2,adjnode3
             * ...
             */
            File file = new File(filename);
            if(!file.exists()) {
                p("File %s does not exist\n", filename);
                return;
            }
            FileReader fr = null;
            BufferedReader br = null;
            try {
                fr = new FileReader(file);
                br = new BufferedReader(fr);
                String line;
                int linestate = 0;
                Map<Integer, List<Integer>> map = new HashMap<>();
                List<String> listGraph = new ArrayList<>();
                while((line = br.readLine()) != null) {
                    if(linestate == 0) {
                        String cmd = line.trim();
                        if(cmd.length() == 0) {
                            continue;
                        }
                        if(!"graph".equals(cmd.toLowerCase())) {
                            throw new Exception(String.format(
                                "FILE %s expecting GRAPH as first line", 
                                filename));
                        }
                        linestate++;
                    }
                    else {
                        // parse nodename1:adjnode1,adjnode2,adjnode3
                        // read in nodename1 into map and adjnode into set
                        String [] a = line.trim().split(":");
                        if(a.length != 2) {
                            throw new Exception(String.format(
                                "FILE %s parse line:%s",
                                filename, line));
                        }
                        int vector = Integer.parseInt(a[0]);
                        List<Integer> list = new ArrayList<>();
                        map.put(vector, list);
                        a = a[1].split(",");
                        for(int i = 0; i < a.length; i++) {
                            int edge = Integer.parseInt(a[i]);
                            list.add(edge);
                        }
                        listGraph.add(line);
                    }
                }
                // validate the read map
                for(Map.Entry<Integer, List<Integer>> kv: map.entrySet()) {
                    List<Integer> edges = kv.getValue();
                    for(Integer edge: edges) {
                        if(map.get(edge) == null) {
                            for(String lineread: listGraph) {
                                p("%s\n", lineread);
                            }
                            throw new Exception(String.format(
                                "Invalid graph edge %d", edge));
                        }
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fr != null)
                        fr.close();
                    if(br != null)
                        br.close();
                } catch(IOException e) {
                    
                }
            }
        }
        
        public void
        loadGraphList(Map<Integer, List<Integer>> map) {
            /**
             * load the GraphBasic with input map of ID, set of Edges.
             * 
             * map
             *     id of src node
             *  set of ids of dst node
             */
            Map<Integer, Node> mapNodes = new HashMap<>();
            
            // load all nodes first
            for(Integer i: map.keySet()) {
                
                Node n = new Node(i);
                mapNodes.put(i, n);
                setNodes.add(n);
            }
            
            // now load edges, so node lookups will not fail
            for(Integer i: map.keySet()) {
                Node nsrc = mapNodes.get(i);
                List<Integer> set = map.get(i);
                for(Integer j: set) {
                    Node ndst = mapNodes.get(j);
                    // if invalid edge
                    if(ndst == null) {
                        reset();
                        print("ERROR invalidDataSet:\n");
                        // print debug list of all entries
                        for(Map.Entry<Integer, List<Integer>> kv: map.entrySet()) {
                            print("k:%2d v:", kv.getKey());
                            for(Integer v: kv.getValue()) {
                                print("%2d ", v);
                            }
                            print("\n");
                        }
                        return;
                    }
                    nsrc.addEdge(ndst, false);
                }
            }
        }
            
        public void
        generateRandomGraph(
                int numNodes,
                boolean isDirected,
                boolean isDAG) {
            generateRandomGraph(numNodes, isDirected, isDAG, 2, 2);
        }

        public void
        generateRandomGraph(
                int numNodes,
                boolean isDirected) {
            if(isDirected) {
                generateRandomGraph(numNodes, isDirected, false, 2, 2);
            } else {
                generateRandomGraph(numNodes, isDirected, false, 2, 2);
            }
        }

        public void 
        generateRandomGraph(
                int numNodes,
                boolean isDirected,
                boolean isDAG,
                int maxConnectivityPerNode) {
            int maxOutbound = maxConnectivityPerNode/2;
            int maxInbound = maxConnectivityPerNode - maxOutbound;
            generateRandomGraph(numNodes, isDirected, isDAG, maxInbound, maxOutbound);
        }
        
        public void
        generateRandomGraph(
                int numNodes,
                boolean isDirected,
                boolean isDAG,
                int maxInbound,
                int maxOutbound) {
            
            setNodes.clear();
            int maxConnectivityPerNode = maxInbound + maxOutbound;
            List<Node> l = new ArrayList<>();

            for(id = 0; id < numNodes;) {
                Node n = new Node();
                setNodes.add(n);
                l.add(n);
            }

            for(int i = 0; i < numNodes; i++) {
                Node n = l.get(i);
                int numEdgesRemaining = maxConnectivityPerNode - 
                    n.numInbound() - n.numOutbound();

                if(numEdgesRemaining == 0) {
                    numEdgesRemaining++;
                }

                for(int j = 0; j <= numEdgesRemaining; j++) {
                    int idxTargetNode = 0;
                    if(isDirected && isDAG) {
                        if((j + 1) == numNodes) {
                            continue;
                        }
                        int offset = numNodes - i;
                        idxTargetNode = rand.nextInt(offset) + i + 1;
                        if(idxTargetNode == j || idxTargetNode >= numNodes) {
                            continue;
                        }
                    } else {
                        idxTargetNode = rand.nextInt(numNodes);
                    }
                    Node nDst = l.get(idxTargetNode);
                    if(nDst.numEdges() >= maxConnectivityPerNode) {
                        continue;
                    }
                    n.addEdge(nDst, isDirected);
                }
            }
        }

        public void
        generateRandomGraph(
                int numNodes,
                boolean isDirected,
                boolean isDAG,
                int maxInbound,
                int maxOutbound, 
                int numHeads,
                int numTails) {
            
            setNodes.clear();
            
            int numHeadsTails = numHeads + numTails;
            if(numHeadsTails >= numNodes) {
                return;
            }
            
            int maxConnectivityPerNode = maxInbound + maxOutbound;
            List<Node> l = new ArrayList<>();

            for(id = 0; id < numNodes;) {
                Node n = new Node();
                setNodes.add(n);
                l.add(n);
            }

            for(int i = 0; i < numNodes; i++) {
                Node n = l.get(i);
                int numEdgesRemaining = maxConnectivityPerNode - 
                    n.numInbound() - n.numOutbound();

                if(numEdgesRemaining == 0) {
                    numEdgesRemaining++;
                }

                for(int j = 0; j <= numEdgesRemaining; j++) {
                    int idxTargetNode = 0;
                    if(isDirected && isDAG) {
                        if((j + 1) == numNodes) {
                            continue;
                        }
                        int offset = numNodes - i;
                        idxTargetNode = rand.nextInt(offset) + i + 1;
                        if(idxTargetNode == j || idxTargetNode >= numNodes) {
                            continue;
                        }
                    } else {
                        idxTargetNode = rand.nextInt(numNodes);
                    }
                    Node nDst = l.get(idxTargetNode);
                    if(nDst.numEdges() >= maxConnectivityPerNode) {
                        continue;
                    }
                    n.addEdge(nDst, isDirected);
                }
            }
        }
        
        public void
        printGraph() {
            printGraph(setNodes);
        }

        public void
        printGraph(Set<Node> set) {
            p("PRINT GRAPH\n");
            for(Node n: set) {
                print("ID:%2d; EDGES:", n.idNode);
                List<Node> l = n.getEdges();
                boolean isFirst = true;
                for(Node e: l) {
                    if(isFirst) {
                        print("%2d", e.idNode);
                        isFirst = false;
                    } else {
                        print(",%2d", e.idNode);
                    }
                }
                print("\n");
            }
        }
        
        public void
        printAllMSTSetsUnweighted() {
        }

        public void
        printMinSpanGraphUnweighted() {
            // generates first MST, not all MST sets
            // this is for unweighted edgeds. there should be separate method
            // for weighted edges.
            // get list of all heads and do queue from there, and keep track
            // of all used nodes.

            Map<Integer, Node> mapMST = new HashMap<>();
            
            for(Node n: setNodes) {
                Node newNode = new Node(n.idNode);
                mapMST.put(n.idNode, newNode);

                List<Node> edges = n.getEdges();
                for(Node edge: edges) {
                    int id = edge.idNode;
                    if(!mapMST.containsKey(id)) {
                        Node edgeNode = new Node(id);
                        mapMST.put(id, edgeNode);
                        newNode.addEdge(edgeNode, false);
                    } else if(newNode.numOutbound() == 0){
                        Node edgeNode = mapMST.get(id);
                        if(!edgeNode.hasEdge(newNode)) {
                            newNode.addEdge(edgeNode, false);
                        }
                    }
                }
            }

            // copy the map entries to a set
            Set<Node> setMST = new HashSet<>();
            for(Map.Entry<Integer, Node> kv: mapMST.entrySet()) {
                Node n = kv.getValue();
                setMST.add(n);
            }
            printGraph(setMST);
        }

        public void
        printMinSpanGraphUnweightedDirected() {
            // generates first MST, not all MST sets
            // this is for unweighted edgeds. there should be separate method
            // for weighted edges.
            // get list of all heads and do queue from there, and keep track
            // of all used nodes.

            LinkedList<Node> q = new LinkedList<>();
            Map<Integer, Node> mapMST = new HashMap<>();

            // add all nodes with no inbound edges
            for(Node n: setNodes) {
                if(n.numInbound() == 0) {
                    q.add(n);
                }
            }
            
            while(q.size() != 0) {
                Node n = q.removeFirst();

                Node newNode = new Node(n.idNode);
                mapMST.put(n.idNode, newNode);
                boolean hasNoOutbound = true;

                List<Node> edges = n.getEdges();
                for(Node edge: edges) {
                    int id = edge.idNode;
                    if(!mapMST.containsKey(id)) {
                        Node edgeNode = new Node(id);
                        mapMST.put(id, edgeNode);

                        newNode.addEdge(edgeNode, true);
                        q.add(edge);
                        
                        hasNoOutbound = false;
                    }
                }
                if(hasNoOutbound) {
                    for(Node edge: edges) {
                        int id = edge.idNode;
                        Node edgeNode = mapMST.get(id);
                        newNode.addEdge(edgeNode, true);
                        break;
                    }
                }
            }

            // copy the map entries to a set
            Set<Node> setMST = new HashSet<>();
            for(Map.Entry<Integer, Node> kv: mapMST.entrySet()) {
                Node n = kv.getValue();
                setMST.add(n);
            }
            printGraph(setMST);
        }
        
        public void
        printAllChains() {
            printAllChains(setNodes);
        }

        public void
        printAllChains(Set<Node> set) {
            List<Node> listHeads = new ArrayList<>();
            for(Node n: set) {
                if(n.numInbound() == 0) {
                    listHeads.add(n);
                }
            }
            List<List<Node>> listChain = new ArrayList<>();
            for(Node n: listHeads) {
                boolean [] visited = new boolean[set.size()];
                for(int i = 0; i < set.size(); i++) {
                    visited[i] = false;
                }
                List<Node> list = new ArrayList<>();
                list.add(n);
                dfsChain(n, listChain, list, visited);
            }
            print("print all chains:\n");
            for(List<Node> list: listChain) {
                for(Node n: list) {
                    print("%2d ", n.idNode);
                }
                print("\n");
            }
        }
        
        public void
        dfsChain(Node n, 
                List<List<Node>> listChain, 
                List<Node> list, 
                boolean [] visited) {
            if(n.numOutbound() == 0) {
                List<Node> listCopy = new ArrayList<>(list);
                listChain.add(listCopy);
            }
            List<Node> listEdges = n.getEdges();
            for(Node e: listEdges) {
                if(visited[e.idNode]) {
                    continue;
                }
                list.add(e);
                visited[e.idNode] = true;
                dfsChain(e, listChain, list, visited);
                visited[e.idNode] = false;
                list.remove(list.size() - 1);
            }
        }
        
        public void 
        printTopologicalSort() {
            printTopologicalSort(setNodes);
        }

        public void 
        printTopologicalSort(Set<Node> set) {
            List<Node> listHeads = new ArrayList<>();
            boolean [] visited = new boolean[set.size()];
            for(int i = 0; i < set.size(); i++) {
                visited[i] = false;
            }
            for(Node n: set) {
                if(n.numInbound() == 0) {
                    listHeads.add(n);
                }
            }
            List<Node> listSorted = new ArrayList<>();
            boolean isDAG = false;
            for(Node n: listHeads) {
                isDAG = dfs(n, listSorted, visited);
                if(!isDAG) {
                    break;
                }
            }
            print("topological sort:\n");
            if(!isDAG) {
                print("is cyclic");
            }
            else {
                for(Node node: listSorted) {
                    print("%2d ", node.idNode);
                }
            }
            print("\n");
        }
        
        public boolean
        dfs(Node n, List<Node> l, boolean [] visited) {
            int id = n.idNode;
            if(visited[id]) {
                return false;
            }
            visited[id] = true;
            l.add(n);
            List<Node> edges = n.getEdges();
            for(Node e: edges) {
                boolean isDAG = dfs(e, l, visited);
                if(!isDAG) {
                    return isDAG;
                }
            }
            return true;
        }
        
        public int findLongestPathMatrix(int [][] a, List<Integer> l) {
            boolean [][] b = new boolean [a.length][a[0].length];
            for(int i = 0; i < a.length; i++) {
                for(int j = 0; j < a[0].length; j++) {
                    b[i][j] = false;
                }
            }
            for(int i = 0; i < a.length; i++) {
                for(int j = 0; j < a[0].length; j++) {
                    if(b[i][j]) {
                        continue;
                    }
                    List<Integer> ltmp = new ArrayList<>();
                    findLongestPathMatrix(a, b, 0, 0, l, ltmp, 0);
                }
            }
            return 0;
        }
        private int findLongestPathMatrix(
            int [][] a, 
            boolean [][] b, 
            int i, 
            int j, 
            List<Integer> l, 
            List<Integer> ltmp, 
            int cnt) 
        {
            b[i][j] = true;
            l.add(a[i][j]);
            int max = 0;
            if((i-1) >= 0) {  // N
                if(a[i-1][j] == (a[i][j]+1) || a[i-1][j] == (a[i][j]-1)) {
                    //findLongestPathMatrix(a, i-1, j, l, cnt+1);
                }
            }
            if((j+1) < a[0].length) { // E
                if(a[i][j+1] == (a[i][j]+1) || a[i][j+1] == (a[i][j]-1)) {
                    //findLongestPathMatrix(a, i, j+1, l, cnt+1);
                }
            }
            if((i+1) < a.length) { // S
                if(a[i+1][j] == (a[i][j]+1) || a[i+1][j] == (a[i][j]-1)) {
                    //findLongestPathMatrix(a, i+1, j, l, cnt+1);
                }
            }
            if((j-1) >= 0) { // W
                if(a[i][j-1] == (a[i][j]+1) || a[i][j-1] == (a[i][j]-1)) {
                    //findLongestPathMatrix(a, i, j-1, l, cnt+1);
                }
            }
            {
                //int tmpNS = (tmpN > tmpS) ? tmpN : tmpS;
                //int tmpEW = (tmpE > tmpW) ? tmpE : tmpW;
                //int tmp   = (tmpNS > tmpEW) ? tmpNS : tmpEW;
                //if(tmp > cnt) {
                //}    
                //max = (max > tmp) ? max : tmp;
            }
            l.remove(l.size() - 1);
            return max;
        }
    }
}

