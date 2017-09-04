import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public class Solution extends JPanel {
    protected int width = 100;
    protected int height = 100;
    protected int columns;
    protected int rows;
    protected int num_pieces;
    ArrayList<DLY> solution;
    public Solution(int num_pieces, int columns, int rows, int width, int height, ArrayList<DLY> solution) {
        this.num_pieces = num_pieces;
        this.width = width;
        this.height = height;
        this.columns = columns;
        this.rows = rows;
        this.solution = solution;
        this.setSize((columns + 1) * 10, (rows + 1) * 100);
        this.setPreferredSize(new Dimension(columns * width + 2, rows * height + 2));
        this.setBackground(Color.BLACK);
    }
    public Solution(int num_pieces, int columns, int rows, ArrayList<DLY> solution) {
        this(num_pieces, columns,rows,100,100,solution);
    }
    public Color getColor(int p) {
        p++;
        float n = (float)Math.log(num_pieces + 1)/(float)Math.log(3);
        float h = p % n;

        float t = p / n;

        float s = t % n;

        t = t / n;

        float b = t;
        return new Color(h / n, s / n, b / n);
        //return new Color(Color.HSBtoRGB(h / n, s / n, b / n));
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int i = 0;
        for (DLY s : solution) {
            Iterator<DLY> control_iterator = s.control.iterator();
            g.setColor(getColor(control_iterator.next().header.num));
            while (control_iterator.hasNext()) {
                DLY current = control_iterator.next();

                if (current != null) {
                    int column = (int)Math.abs(current.header.num - num_pieces) / columns;
                    column *= width;
                    int row = (int)(current.header.num - num_pieces) % columns;
                    row *= height;
                    g.fillRect(row + 1,column + 1,width,height);
                }
            }
            i++;
        }
        g.setColor(Color.WHITE);
        g.drawRect(0,0,columns * width + 2,rows * height + 2);
    }
}
