import java.util.ArrayList;

public class Pentomino {
    private int hash = -1;
    public boolean[][] matrix;
    private static String[] block_name = new String[] {"I","F","L","Z","P","T","U","V","W","X","Y","N"};
    public static String get_name(int block) {
        if (block >= 0 && block < block_name.length) return block_name[block];
        return new Character((char)('a' + block)).toString();
    }
    int[] rows;
    String name;
    int height = 0;
    int width = 0;
    private Pentomino() {

    }
    /**
     * Creates a new <code>Pentomino</code> with <code>rows</code> rows, and <code>columns</code> columns, named <code>name</code>
     *
     * @param rows
     * @param columns
     * @param name
     */
    public Pentomino(int rows, int columns, String name) {
        matrix = new boolean[columns][rows];
        this.name = name;
        createnum_rows();
    }

    /**
     * Creates a <code>Pentomino</code> from a strided <code>TreeSet</code> of <code>Integer</code>s, named <code>name</code>
     *
     * @param data
     * @param name
     */
    public Pentomino(ArrayList<Integer> data, String name) {
        matrix = new boolean[5][5];
        for (int i = 0; i < data.size(); i += 2) {
            int column = data.get(i);
            int row = data.get(i + 1);
            matrix[column][row] = true;
        }
        this.name = name;
        createnum_rows();
    }

    /**
     * This creates a copy of the passed Pentomino and rotating it 90 degrees
     *
     * @param rotate the Pentomino you want rotated 90 degrees
     */
    public Pentomino(Pentomino rotate) {
        this.matrix = new boolean[rotate.matrix.length][rotate.matrix[0].length];
        for (int x = 0; x < rotate.matrix.length; x++) {
            for (int y = 0; y < rotate.matrix[0].length; y++) {
                this.matrix[y][rotate.matrix[0].length - 1 - x] = rotate.matrix[x][y];
            }
        }
        createnum_rows();
        this.name = rotate.name;
    }

    public void createnum_rows() {
        rows = null;
        boolean column_dead = true;
        boolean row_dead = true;
        int index = 0;
        int row = 0;
        int column = 0;
        rows = new int[5];
        for (int x = 0; x < matrix.length; x++) {
            for (boolean y : matrix[x]) {
                if (y) column_dead = false;
            }
            if (column_dead) column++;
        }
        for (int j = 0; j < matrix[0].length; j++) {
            for (boolean[] xs : matrix) {
                if (xs[j]) row_dead = false;
            }
            if (row_dead) row++;
        }
        int first_row = row;
        int max_found = 10;
        for (; column < matrix.length; column++) {
            rows[index] = 1;
            row_dead = true;
            int found = 0;
            for (row = first_row; row < matrix[0].length; row++) {
                rows[index] = rows[index] << 1;
                if (!row_dead) found++;
                if (matrix[column][row]) {
                    rows[index] += 1;
                    row_dead = false;
                }
            }
            if (!row_dead) {
                index++;
                if (found < max_found) max_found = found;
            }
        }
        //System.out.println(max_found);
        height = index;
        max_found = 10;
        for (int x = 0; x < height; x++) {
            int copy = rows[x];
            int zeroes = 0;
            while ((copy & 1) == 0) {
                copy = copy >> 1;
                zeroes++;
            }
            if (zeroes < max_found) max_found = zeroes;
        }
        int max_width = 10;
        for (int x = 0; x < height; x++) {
            rows[x] = rows[x] >> max_found;
            int found_width = 0;
            int copy = rows[x]; while (copy > 0) { copy = copy >> 1; found_width++; }
            if (found_width < max_width) max_width = found_width;
        }
        if (max_found == 10) max_found = 0;
        width = max_width - 1;
    }
    public Pentomino flipped() {
        Pentomino block = new Pentomino();
        block.rows = new int[height];
        block.height = this.height;
        block.width = this.width;
        block.name = this.name;
        for (int i = 0; i < height; i++) {
            int row = rows[i];
            int out = 1;
            for (int x = 0; x < width; x++) {
                out <<= 1;
                if ((row & (1 << x)) != 0) {
                    out += 1;
                }
            }
            block.rows[i] = out;
        }
        return block;
    }
    @Override
    public boolean equals(Object other) {
        Pentomino o = (Pentomino)other;
        if (o == null) return false;
        if (this.height != o.height) return false;
        if (this.width != o.width) return false;
        for (int i = 0; i < o.height; i++) {
            if (o.rows[i] != this.rows[i]) return false;
        }
        return o.name == this.name;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Block " + get_name(this.name.charAt(0) - 'A') + " (" + (this.name.charAt(0) - 'A') + "):\n");
        int i = 0;
        for (int x = 0; x < height; x++) {
            int f = rows[x];
            out.append(Integer.toBinaryString(f).substring(1).replace("1", "▓").replace("0","░"));
            if (x < height - 1) out.append("\n");
        }
        return out.toString();
    }
}
