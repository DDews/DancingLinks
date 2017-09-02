import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

public class DancingLinks {
    private static long start_time;
    private static int solutions = 0;
    private static DLX matrix;
    private static ArrayList<DLX> headers;
    private static int num_columns = 0;
    private static int num_rows = 0;
    private static int num_pieces = 0;
    private static ArrayList<String> colors;
    private static String[] block_name = new String[]{"I", "F", "L", "Z", "P", "T", "U", "V", "W", "X", "Y", "N"};

    public static String get_name(int block) {
        if (block >= 0 && block < block_name.length) return block_name[block];
        return new Character((char) ('a' + block)).toString();
    }

    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
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
        headers = new ArrayList<DLX>();
        read(file);
    }

    public static String time(long timeInMilliSeconds) {
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
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
        start_time = System.currentTimeMillis();
        // initialize variables
        num_pieces = num_rows = num_columns = 0;
        ArrayList<ArrayList<Integer>> pentaminoes = new ArrayList<ArrayList<Integer>>();
        try {
            String[] dimensions = reader.readLine().split("\\s+");
            num_pieces = Integer.parseInt(reader.readLine());
            num_columns = Integer.parseInt(dimensions[1]);
            num_rows = Integer.parseInt(dimensions[0]);
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
            String name = new Character((char) (++c)).toString();
            Pentomino norm = new Pentomino(piece, name);
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
        matrix = new DLX(0);
        int i = 0;
        DLX last = matrix;
        headers.add(matrix);
        for (int k = 1; k < num_columns * num_rows + 12; k++) {
            DLX col = new DLX(k);
            last.add(col);
            headers.add(col);
            last = col;
        }
        last.add(matrix);
        DLX current = matrix;
        do {
            System.out.println(current);
            current = current.next();
        } while (current != matrix);
        for (Pentomino block : blocks) {
            int block_num = block.name.charAt(0) - 'A';
            for (int column = 0; column < (num_columns + 1) - block.width; column++) {
                for (int row = 0; row < (num_rows + 1) - block.height; row++) {
                    DLY last_link = null;
                    for (int y = 0; y < block.height; y++) {
                        if (last_link == null) last_link = set(null,i, block_num, block.rows[y], block.width, row + y, column);
                        else {
                            set(last_link,i, block_num, block.rows[y], block.width, row + y, column);
                        }
                    }
                    if (last_link != null) {
                        TreeSet<DLY> control = new TreeSet<DLY>();
                        last_link.control = control;
                        control.add(last_link);
                        DLY money = last_link.next();
                        while (money != last_link) {
                            control.add(money);
                            money.control = control;
                            money = money.next();
                        }
                        // For testing; display the piece on the board in this position
                        ArrayList<DLY> test = new ArrayList<DLY>();
                        test.add(last_link);
                        printSolution(test);
                        i++;
                    }

                }
            }
        }
        solutions = 0;
        for (DLX column : headers) {
            System.out.println(column);
        }
        //start on the first position on the board.
        matrix = headers.get(12);
        DLX stop = headers.get(0);
        recurse(matrix, matrix.data, stop,0, new ArrayList<DLY>());
        System.out.println("Dancing links time: " + time(System.currentTimeMillis() - start_time));
    }

    public static DLY set(DLY to_append, int r, int block_num, int line, int width, int row, int column) {
        System.out.println(r + ", " + block_num + ", " + Integer.toBinaryString(line) + ", " + width + ", " + row + ", " + column + ": ");
        int k = 0;
        int m = 0;
        DLY first = to_append;
        if (first == null) first = new DLY<Integer>(headers.get(block_num), new Integer(r));
        DLY<Integer> last = first;
        while (m < width) {
            if ((line & (1 << m)) > 0) {
                if (last == null)
                    last = new DLY<Integer>(headers.get(num_pieces - 1 + (column + (width - k)) + row * num_columns), new Integer(r));
                else {
                    DLY current = new DLY<Integer>(headers.get(num_pieces - 1 + (column + (width - k)) + row * num_columns), new Integer(r));
                    last.addLeft(current);
                    last = current;
                }
            }
            m++;
            k++;
        }
        DLY c = last.next();
        System.out.print(block_num + "[");
        System.out.print(last + ", ");
        while (c != last) {
            System.out.print(c.toString());
            if (c.next() != last) System.out.print(", ");
            c = c.next();
        }
        System.out.println("]");
        return c.left;
    }
    public static void printSolution(ArrayList<DLY> solution) {
        ArrayList<StringBuilder> table = new ArrayList<StringBuilder>();
        for (int m = 0; m < num_rows; m++) {
            StringBuilder row = new StringBuilder(num_columns);
            for (int j = 0; j < num_columns; j++) {
                row.append(".");
            }
            table.add(row);
        }
        int piece = 0;
        for (DLY s : solution) {
            Iterator<DLY> control_iterator = s.control.iterator();
            DLY base = control_iterator.next();
            while (control_iterator.hasNext()) {
                DLY current = control_iterator.next();
                String rand_color = get_name(base.header.num);
                if (current != null) {
                    int row = Math.abs(current.header.num - num_pieces) / num_columns;

                    int column = (current.header.num - num_pieces) % num_columns;
                    if (!table.get(row).substring(column, column + 1).equals("."))
                        System.err.println("collision... of " + rand_color + " overwriting " + table.get(row).substring(column, column + 1));
                    table.get(row).replace(column, column + 1, rand_color);
                }
                piece++;
            }
        }
        for (StringBuilder row : table) System.out.println(row);
    }
    public static void printDepth(int depth, DLX original, int i) {
        StringBuilder out = new StringBuilder(50);
        int total = 0;

        int n = Math.round(((float)i / original.size) * 50);
        for (int j = 0; j < n; j++) {
            out.append(colors.get(2));
            total++;
        }
        /*int k = Math.round(((float)depth / num_pieces) * 50);
        for (int j = 0; j < k; j++) {
            out.append(colors.get(1));
            total++;
        }*/
        for (; total < 50; total++) out.append(colors.get(0));
        System.out.println(out);
    }
    public static String rowPiece(DLY obj) {
        return get_name(((DLY)obj.control.iterator().next()).header.num);
    }
    public static int recurse(DLX node, DLY first_down, DLX stop, int depth, ArrayList<DLY> solution) {
        DLX current = node;
        DLX found = node;
        int smallest = Integer.MAX_VALUE;
        do {
            if (node.alive) {
                if (current.size < smallest) {
                    smallest = current.size;
                    found = current;
                }
            }
            current = current.next();
        } while (current != null && current != node && current != stop);
        node = found;
        if (!first_down.alive) {
            if (rowPiece(solution.get(0)).equals("U") && rowPiece(first_down).equals("X") && depth == 2) System.err.println(get_name(((DLY)first_down.control.iterator().next()).header.num) + " uhhhh: " + depth + ", " + node.size + ", " + node.num);
            if (node.size <= 0) return depth - 1;
        }
        DLY row = first_down;
        DLX original = node;
        int iteration = 0;
        if (node.size == 0) {
            return depth - 1;
        }
        do {
            if (depth == 0) {
                System.out.println(get_name(((DLY) row.control.iterator().next()).header.num));
            }
            if (depth == 0) printDepth(depth,original,iteration++);
            boolean breaking = false;
            LinkedBlockingQueue<Object> removed = new LinkedBlockingQueue<Object>();
            ArrayList<DLY> control_rows = new ArrayList<DLY>();
            // start with the current selected row
            DLY next_column = row;
            if (next_column != null) {
                ArrayList<DLY> selected_row = new ArrayList<DLY>();
                // for every object in this row
                for (Object obj : next_column.control) {
                    DLY column = (DLY)obj;
                    // add it to the selection
                    selected_row.add(column);
                }
                // for every selected column box in that selected row
                for (DLY column : selected_row) {
                    // add this to the removed stack to add it back later
                    removed.add(column.header);
                    // if we are currently on this header, go to the next
                    if (column.header == node) node = node.next();

                    // from now on, skip this header
                    column.header.ghost();
                    // add the header's control row
                    control_rows.add(column);

                    // now from this column box, start going down
                    DLY next_down = column;
                    do {
                        // don't remove the selected row's box again!
                        if (next_down != column) {
                            // for every object in this row
                            for (Object obj : next_down.control) {
                                DLY row_box = (DLY) obj;

                                // if it is alive,
                                if (row_box.alive) {
                                    // from now on, skip it
                                    row_box.ghost();
                                    // add it to the stack for easy backtracking
                                    removed.add(row_box);
                                }
                            }
                        } else {
                            // skip this selected column's row
                            column.ghost();
                            // add it to the backtracking
                            removed.add(column);
                        }
                        // continue to do this until you reach the end
                        next_down = next_down.nextDown();
                    } while (next_down != null);
                }
            }
            // check if we should return a single answer or not
            int result;
            // make a unique solution path
            ArrayList<DLY> new_solution = new ArrayList<DLY>(solution);
            // add this selected row to the unique solution path
            new_solution.add(row);
            // if we haven't found all the pieces,
            if (new_solution.size() < num_pieces) {
                // and there are more columns to traverse, recurse
                if (node.next() != null) result = recurse(node, node.data, stop, depth + 1, new_solution);
                else {
                    // otherwise we have found one solution
                    System.out.println("Solution #" + ++solutions);
                    printSolution(new_solution);
                    result = depth - 1; // I am guessing using the last 2 pieces won't generate another solution, as this emplies that this piece is the same as another
                }
            }
            else {
                // if we have used all the pieces, print this solution
                System.out.println("Solution #" + ++solutions);
                printSolution(new_solution);
                result = depth - 1; // I am guessing using the last piece will not make more solutions, as this emplies that this piece can be interchanged with another
            }
            // backtrack
            for (Object obj : removed) {
                // if the node we are reviving is a column header,
                if (obj.getClass() == DLX.class) {
                    DLX removed_column = (DLX)obj;
                    // revive the column header
                    removed_column.resurrect();
                } else if (obj.getClass() == DLY.class) {
                    // otherwise, revive the row
                    DLY removed_row = (DLY)obj;
                    removed_row.resurrect();
                }
            }
            // now for every revived column header
            for (DLY control_row : control_rows) {
                // set its control row back to what it was
                control_row.header.data = control_row;
            }

            // if the result of our last recursion wants us to backtrack, backtrack
            if (depth > 1 && result < depth) return result;
            // if not, start back at the column we were on originally
            node = original;
            // and select the next row in that column, to try again
            row = row.nextDown();

            // until we run out of rows in this column
        } while (row != null && row != first_down);

        // we tried every possibility in this column. backtrack
        return depth - 1;
    }
}
