import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GUI extends JFrame {
    private int rows;
    private int columns;
    private GUI f;
    private int width = 10;
    private int height = 10;
    JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL,50);
    JScrollPane scrollPane = new JScrollPane();
    JPanel list = new JPanel();
    public GUI(int columns, int rows) {
        f = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.columns = columns;
                f.rows = rows;
                setTitle("Pentomino Solver");
                setSize(columns * width + 25, rows * height * 100 + 20);
                setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
                setLayout(new BoxLayout(f,BoxLayout.Y_AXIS));
                setBackground(Color.BLACK);
                progressBar.setStringPainted(true);
                list.setBounds(0,10,columns * width, rows * height * 10);
                list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
                list.setBackground(Color.BLACK);
                scrollPane = new JScrollPane();
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setBounds(0, 0, columns * width + 25, rows * height * 10);
                scrollPane.setSize(columns * width + 25, rows * height * 10);
                scrollPane.getViewport().add(list);
                JPanel contentPane = new JPanel();
                contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));
                contentPane.setPreferredSize(new Dimension(columns * width, rows * height * 10));
                JPanel barPane = new JPanel();
                barPane.add(progressBar);
                barPane.setBounds(0,0,columns * width,20);
                barPane.setPreferredSize(new Dimension(columns * width,20));
                barPane.setSize(new Dimension(columns * width, 20));
                barPane.setMaximumSize(new Dimension(columns * width, 20));
                contentPane.add(barPane);
                contentPane.add(scrollPane);
                f.setContentPane(contentPane);
                f.pack();
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                f.setAlwaysOnTop(true);
                f.setVisible(true);
            }
        });
    }
    public void add(ArrayList<DLY> solution) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Solution new_solution = new Solution(columns, rows, width, height, solution);
                list.add(new_solution);
                list.add(Box.createRigidArea(new Dimension(width * 100,10)));
                list.add(Box.createVerticalGlue());
                list.revalidate();
                list.repaint();
                scrollPane.revalidate();
                scrollPane.repaint();
                f.pack();
                f.revalidate();
                f.repaint();
            }
        });
    }
    public void setProgress(int n) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setValue(n);
            }
        });
    }
}
