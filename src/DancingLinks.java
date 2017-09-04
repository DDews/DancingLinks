import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class DancingLinks {
    private static boolean debugging = false;
    private static long start_time;
    private static int solutions = 0;
    private static DLX matrix;
    protected static boolean done = true;
    protected static boolean firstSolution = false;
    protected static boolean stepping = false;
    protected static boolean showing_attempts = false;
    protected static Thread thread;
    private static ArrayList<DLX> headers;
    private static int num_columns = 0;
    private static int num_rows = 0;
    private static int num_pieces = 0;
    private static ArrayList<String> colors;
    private static GUI window;
    private static String[] block_name = new String[]{"I", "F", "L", "Z", "P", "T", "U", "V", "W", "X", "Y", "N"};

    public static String get_name(int block) {
        if (block >= 0 && block < block_name.length) return block_name[block];
        return new Character((char) ('a' + block)).toString();
    }

    public static int getRandom(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            String filename = args[0];
            File file = new File(filename);
            if (!file.exists()) {
                JOptionPane.showMessageDialog(window,"Unable to locate file: " + file.getAbsolutePath());
                return;
            }
            read(file);
        }
        else window = new GUI(10,6, 12);
    }

    public static String time(long timeInMilliSeconds) {
        long seconds = timeInMilliSeconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        return days + ":" + hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
    }
    public static void read(File file) {
        num_pieces = 0;
        solutions = 0;
        colors = new ArrayList<String>();
        colors.add("░");
        colors.add("▒");
        colors.add("▓");
        headers = new ArrayList<DLX>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(window,"Error encountered trying to read from file " + file.getAbsolutePath() + "\n" + e.toString());
            e.printStackTrace();
            return;
        }
        if (reader == null) return;
        // initialize variables
        num_pieces = num_rows = num_columns = 0;
        ArrayList<ArrayList<Integer>> pentaminoes = new ArrayList<ArrayList<Integer>>();
        try {
            String[] dimensions = reader.readLine().split("\\s+");
            num_pieces = Integer.parseInt(reader.readLine());
            num_columns = Integer.parseInt(dimensions[1]);
            num_rows = Integer.parseInt(dimensions[0]);
            if (num_rows * num_columns != 60) throw new IllegalStateException("Cannot have a rectangle that use all 12 pentomino pieces with dimensions " + num_columns + " by " + num_rows + ". The area must be exactly 60.");
            String pentamino;
            while ((pentamino = reader.readLine()) != null) {
                String[] split = pentamino.split("\\s+");
                ArrayList<Integer> piece = new ArrayList<Integer>();
                for (String atom : split) {
                    piece.add(Integer.parseInt(atom));
                }
                pentaminoes.add(piece);
            }
        } catch(IllegalStateException e) {
            e.printStackTrace();
            return;
        } catch(Exception e) {
            JOptionPane.showMessageDialog(window,"Encountered error trying to parse file " + file.getAbsolutePath() + "\n" + e.toString());
            e.printStackTrace();
            return;
        }

        if (window != null) window.recreate(num_columns,num_rows,num_pieces);
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
        debugMsg("Pentominoes:\n");
        for (Pentomino block : blocks) {
            debugMsg(block + "\n");
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
        /*
        DLX current = matrix;

        do {
            debugMsg(current);
            current = current.next();
        } while (current != matrix);
        */
        window.recreate(num_columns,num_rows,num_pieces);
        int last_block = -1;
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
                        /*

                        // For testing; display the piece on the board in this position
                        ArrayList<DLY> test = new ArrayList<DLY>();
                        test.add(last_link);
                        printSolution(test);

                        */
                        i++;
                    }

                }
            }
            if (last_block != block_num) {
                if (headers.get(block_num).data != null) {
                    ArrayList<DLY> piece = new ArrayList<DLY>();
                    piece.add(headers.get(block_num).data);
                    showSolution(piece);
                    last_block = block_num;
                }
            }
        }

    }

    public static void start() {
        done = false;
        window.recreate(num_columns,num_rows,num_pieces);
        solutions = 0;
        //start on the first position on the board.
        start_time = System.currentTimeMillis();
        matrix = headers.get(12);
        DLX stop = headers.get(0);
        recurse(matrix, matrix.data, stop,0, new ArrayList<DLY>());
        done = true;
    }
    public static DLY set(DLY to_append, int r, int block_num, int line, int width, int row, int column) {
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
        /*
        DLY c = last.next();

        System.out.print(block_num + "[");
        System.out.print(last + ", ");
        while (c != last) {
            System.out.print(c.toString());
            if (c.next() != last) System.out.print(", ");
            c = c.next();
        }
        debugMsg("]");
        */
        return last.left;
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
                        JOptionPane.showMessageDialog(window,"collision... of " + rand_color + " overwriting " + table.get(row).substring(column, column + 1));
                    table.get(row).replace(column, column + 1, rand_color);
                }
                piece++;
            }
        }
        for (StringBuilder row : table) debugMsg(row);
    }
    public static void showSolution(ArrayList<DLY> solution) {
        window.add(solution,solutions);
    }
    public static void printDepth(int depth, DLX original, int i) {
        StringBuilder out = new StringBuilder(50);
        int total = 0;

        int n = Math.round(((float)i / original.size) * 50);
        window.setProgress(n);
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
        debugMsg(out);
    }
    public static void finish() {
        window.finish("Found " + solutions + " in " + time(System.currentTimeMillis() - start_time));
    }
    public static String rowPiece(DLY obj) {
        return get_name(((DLY)obj.control.iterator().next()).header.num);
    }
    private static void debugMsg(Object msg) {
        if (debugging) System.out.println(msg);
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
            else {
                return depth - 1;
            }
            current = current.next();
        } while (current != null && current != node && current != stop);
        node = found;
        first_down = node.data;
        if (!first_down.alive) {
            if (node.size <= 0) return depth - 1;
        }
        DLY row = first_down;
        DLX original = node;
        int iteration = 0;
        if (node.size == 0) {
            return depth - 1;
        }
        do {
            if (stepping) try {
                thread.suspend();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (depth == 0) {
                debugMsg(get_name(((DLY) row.control.iterator().next()).header.num));
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
                DLY selected_box = next_column;
                do {
                    // add to selected row
                    selected_row.add(selected_box);
                    // and go to next one
                    selected_box = selected_box.next();
                } while (selected_box != next_column);
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
                            DLY next_right = next_down;
                            do {
                                // from now on skip this link on this row
                                next_right.ghost();
                                // add for backtracking
                                removed.add(next_right);
                                // go to the next one
                                next_right = next_right.next();
                                // until we run out of nodes
                            } while (next_right != null);
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
                if (node.next() != null) {
                    if (showing_attempts) window.showAttempt(new_solution);
                    result = recurse(node, node.data, null, depth + 1, new_solution);
                }
                else {
                    // otherwise we have found one solution
                    debugMsg("Solution #" + ++solutions);
                    printSolution(new_solution);
                    showSolution(new_solution);
                    result = depth - 1;
                    if (firstSolution) thread.suspend();
                }
            }
            else {
                // if we have used all the pieces, print this solution
                debugMsg("Solution #" + ++solutions);
                printSolution(new_solution);
                showSolution(new_solution);
                result = depth - 1;
                if (firstSolution) thread.suspend();
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
            if ((depth > 1 && result < depth) || result < 0) return result;
            // if not, start back at the column we were on originally
            node = original;
            // and select the next row in that column, to try again
            row = row.nextDown();

            // until we run out of rows in this column

        } while (row != null && row != first_down);
        if (depth == 0) finish();
        // we tried every possibility in this column. backtrack
        return depth - 1;
    }
}
