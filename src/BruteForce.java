import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.*;

public class BruteForce {
    private static ArrayList<TreeSet<Integer>> solutions;
    private static boolean[][] matrix;
    private static int num_columns = 0;
    private static int num_rows = 0;
    private static int num_pieces = 0;
    private static ArrayList<String> colors;
    private static String[] block_name = new String[] {"I","F","L","Z","P","T","U","V","W","X","Y","N"};
    public static String get_name(int block) {
        if (block >= 0 && block < block_name.length) return block_name[block];
        return new Character((char)('a' + block)).toString();
    }
    public static int getRandom(int min, int max) {
        return (int)(Math.random() * (max - min) + min);
    }
    public static void main(String[] args) {
        System.out.println("Name of data file: ");
        Scanner in = new Scanner(System.in);
        String filename = in.nextLine();
        File file = new File(filename);
        if (!file.exists()) {
            System.err.println("Unable to locate file: " + file.getAbsolutePath());
            return;
        }
        colors = new ArrayList<String>();
        colors.add("░");
        colors.add("▒");
        colors.add("▓");
        read(file);
    }
    public static void read(File file) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            System.err.println("Error encountered trying to read from file " + file.getAbsolutePath());
            e.printStackTrace();
            return;
        }
        if (reader == null) return;

        // initialize variables
        num_pieces = num_rows = num_columns = 0;
        ArrayList<ArrayList<Integer>> pentaminoes  = new ArrayList<ArrayList<Integer>>();
        try {
            String[] dimensions = reader.readLine().split("\\s+");
            num_pieces = Integer.parseInt(reader.readLine());
            num_rows = Integer.parseInt(dimensions[0]);
            num_columns = Integer.parseInt(dimensions[1]);
            String pentamino;
            while ((pentamino = reader.readLine()) != null) {
                String[] split = pentamino.split("\\s+");
                ArrayList<Integer> piece = new ArrayList<Integer>();
                for (String atom : split) {
                    piece.add(Integer.parseInt(atom));
                }
                pentaminoes.add(piece);
            }
        } catch (Exception e) {
            System.err.println("Encountered error trying to parse file " + file.getAbsolutePath());
            e.printStackTrace();
        }

        // make meaningful objects out of them
        char c = 'A' - 1;
        ArrayList<Pentomino> blocks = new ArrayList<Pentomino>();
        for (ArrayList<Integer> piece : pentaminoes) {
            String name = new Character((char)(++c)).toString();
            Pentomino norm = new Pentomino(piece,name);
            Pentomino ninety = new Pentomino(norm);
            Pentomino one_eighty = new Pentomino(ninety);
            Pentomino two_seventy = new Pentomino(one_eighty);
            if (!blocks.contains(norm)) blocks.add(norm);
            if (!blocks.contains(ninety)) blocks.add(ninety);
            if (!blocks.contains(one_eighty)) blocks.add(one_eighty);
            if (!blocks.contains(two_seventy)) blocks.add(two_seventy);
            Pentomino flipped_norm = norm.flipped();
            Pentomino flipped_90 = ninety.flipped();
            Pentomino flipped_180 = one_eighty.flipped();
            Pentomino flipped_270 = two_seventy.flipped();
            if (!blocks.contains(flipped_90)) blocks.add(flipped_90);
            if (!blocks.contains(flipped_180)) blocks.add(flipped_180);
            if (!blocks.contains(flipped_270)) blocks.add(flipped_270);
            if (!blocks.contains(flipped_norm)) blocks.add(flipped_norm);
        }
        System.out.println("Pentominoes:\n");
        for (Pentomino block : blocks) {
            System.out.println(block + "\n");
        }
        int positions = 0;
        for (Pentomino block : blocks) {
            int inc = (((num_columns + 1) - block.width)) * ((num_rows + 1) - block.height);
            if (inc > 0) positions += inc;
        }
        //System.out.println(positions);
        //System.out.println("rows: " + rows + ", columns: " + columns);
        matrix = new boolean[positions][num_rows * num_columns + num_pieces];
        int i = 0;
        for (Pentomino block : blocks) {
            int block_num = block.name.charAt(0) - 'A';
            for (int row = 0; row < (num_rows + 1) - block.height; row++) {
                for (int column = 0; column < (num_columns + 1) - block.width; column++) {
                    for (int y = 0; y < block.height; y++) {
                        set(i,block_num,block.rows[y],block.width,column, row + y);
                    }
                    i++;
                }
            }
        }
        solutions = new ArrayList<TreeSet<Integer>>();
        recurse(0,0,num_columns * num_rows + num_pieces, new TreeSet<Integer>(), new TreeSet<Integer>(), new TreeSet<Integer>());
        int k = 0;
        ArrayList<StringBuilder> table = new ArrayList<StringBuilder>();
        for (int m = 0; m < num_rows; m++) {
            StringBuilder row = new StringBuilder(num_columns);
            for (int j = 0; j < num_columns; j++) {
                row.append("░");
            }
            table.add(row);
        }
        for (TreeSet<Integer> solution : solutions) {
            System.out.println("\n\nSolution " + ++k + ":\n");
            ArrayList<StringBuilder> new_table = new ArrayList<StringBuilder>(table);
            int piece = 0;
            for (int s : solution) {
                String rand_color = get_name(piece);
                //System.out.println(Arrays.toString(matrix[s]));
                for (int j = num_pieces; j < num_columns * num_rows + num_pieces; j++) {
                    if (matrix[s][j])
                        new_table.get((j - num_pieces) / num_columns).replace((j - num_pieces) % num_columns, (j - num_pieces) % num_columns + 1, rand_color);
                }
                piece++;
            }
            for (StringBuilder row : new_table) System.out.println(row);
        }
    }
    public static void set(int r, int block_num, int line, int length, int column, int row) {
        int k = 0;
        matrix[r][block_num] = true;
        int m = 0;
        while (m < length) {
            if ((line & (1 << m++)) > 0) matrix[r][11 + (column + (length - k)) + row * num_columns] = true;
            k++;
        }
    }
    public static boolean get(int c, int r) {
        return matrix[c][r];
    }
    public static int recurse(int depth, int min_col, int max_col, TreeSet<Integer> ignorednum_columns, TreeSet<Integer> ignorednum_rows, TreeSet<Integer> solution) {
        if (ignorednum_columns.size() >= num_columns * num_rows + num_pieces && ignorednum_rows.size() >= matrix.length) {
            solutions.add(solution);
            return depth - 1;
        }
        TreeMap<Integer,TreeMap<Integer,TreeSet<Integer>>> lowest = new TreeMap<Integer,TreeMap<Integer,TreeSet<Integer>>>();
        for (int c = min_col; c < max_col; c++) {
            if (!ignorednum_columns.contains(c)) {
                TreeSet<Integer> ones = new TreeSet<Integer>();
                for (int r = 0; r < matrix.length; r++) {
                    if (!ignorednum_rows.contains(r)) {
                        if (get(r, c)) {
                            ones.add(r);
                        }
                    }
                }
                TreeMap<Integer,TreeSet<Integer>> map = lowest.get(ones.size());
                if (map == null) map = new TreeMap<Integer,TreeSet<Integer>>();
                map.put(c,ones);
                if (!lowest.containsKey(ones.size())) lowest.put(ones.size(),map);
            }
        }
        Iterator<Integer> iterator = lowest.keySet().iterator();
        if (iterator.hasNext()) {
            int k = iterator.next();
            if (k == 0) return depth - 1;
            //System.out.println(k + " out of " + lowest.keySet() + ", " + solution);
            TreeMap<Integer, TreeSet<Integer>> columnnum_rows = lowest.get(k);
            int c = columnnum_rows.keySet().iterator().next();
            TreeSet<Integer> rows = columnnum_rows.get(c);
            for (int r : rows) {
                TreeSet<Integer> removednum_columns = new TreeSet<Integer>();
                TreeSet<Integer> removednum_rows = new TreeSet<Integer>(ignorednum_rows);
                TreeSet<Integer> new_solution = new TreeSet<Integer>(solution);
                new_solution.add(r);
                for (int c2 = min_col; c2 < max_col; c2++) {
                    if (!ignorednum_columns.contains(c2)) {
                        if (!removednum_columns.contains(c2) && get(r, c2)) {
                            removednum_columns.add(c2);
                            for (int r2 = 0; r2 < matrix.length; r2++) {
                                if (!ignorednum_rows.contains(r2)) {
                                    if (!removednum_rows.contains(r2) && get(r2, c2)) removednum_rows.add(r2);
                                }
                            }
                        }
                    }
                }
                for (int c2 : ignorednum_columns) {
                    if (!removednum_columns.contains(c2)) removednum_columns.add(c2);
                }
                if (removednum_columns.size() > 0 && removednum_rows.size() > 0) {
                    if (depth >= 0) {
                        int f;
                        if ((f = recurse(depth + 1, 0, num_columns * num_rows + num_pieces, removednum_columns, removednum_rows, new_solution)) < depth) return f;
                    }
                    else recurse(depth + 1, 0, num_columns * num_rows + num_pieces, removednum_columns, removednum_rows, new_solution);
                }
                else System.err.println("wtf");
            }
        }
        return depth - 1;
    }
}
