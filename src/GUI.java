import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

public class GUI extends JFrame {
    private int rows;
    private boolean paused = false;
    private int columns;
    private int num_pieces;
    private GUI f;
    private int width = 20;
    private Thread thread;
    private boolean shouldScroll = false;
    private int height = 20;
    private int num_shown = 5;
    JCheckBox showSolutions = new JCheckBox("Show Solutions");
    JProgressBar progressBar = new JProgressBar(JProgressBar.HORIZONTAL,50);
    JScrollPane scrollPane = new JScrollPane();
    JPanel list = new JPanel();
    JButton open = new JButton("Open");
    JButton play = new JButton("Play");
    JButton pause = new JButton("Pause");
    JButton step = new JButton("Step");
    JCheckBox showAttempts = new JCheckBox("Show Attempts");
    JCheckBox firstSolution = new JCheckBox("Pause on Solution");
    JPanel attempt = new JPanel();
    JPanel barPane;
    JPanel buttonsPane;
    JPanel solutionsPane;
    JPanel contentPane;
    JPanel attemptPanel;
    JFileChooser fileChooser = new JFileChooser();
    public GUI(int columns, int rows, int num_pieces) {
        f = this;
        this.num_pieces = num_pieces;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.columns = columns;
                f.rows = rows;
                int max_shown = 5;
                if (rows * height * num_shown > 1500) max_shown = 1500 / (rows * height);
                if (max_shown < 1) max_shown = 1;
                setTitle("Pentomino Solver");
                setSize(columns * width + 20, rows * height * max_shown + 20);
                setLayout(new BoxLayout(f,BoxLayout.Y_AXIS));
                setBackground(Color.BLACK);
                progressBar.setStringPainted(true);
                list.setBounds(0,10,columns * width + 20, rows * height * max_shown);
                list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
                list.setAlignmentX(Component.LEFT_ALIGNMENT);
                list.setBackground(Color.BLACK);
                scrollPane = new JScrollPane();
                scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                scrollPane.setBounds(0, 0, columns * width + 20, rows * height * max_shown);
                scrollPane.setSize(columns * width + 20, rows * height * max_shown);
                scrollPane.getViewport().add(list);
                scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                    public void adjustmentValueChanged(AdjustmentEvent e) {
                        if (e.getValueIsAdjusting()) {
                            if (!paused && !DancingLinks.done && e.getAdjustable().getValue() > e.getAdjustable().getMaximum() * 0.95) shouldScroll = true;
                            else shouldScroll = false;
                        }
                        if (shouldScroll) e.getAdjustable().setValue(e.getAdjustable().getMaximum());
                    }
                });



                contentPane = new JPanel();
                contentPane.setLayout(new GridLayout(1,2));
                contentPane.setPreferredSize(new Dimension(columns * width * 2 + 40, rows * height * max_shown));

                solutionsPane = new JPanel();
                solutionsPane.setLayout(new BoxLayout(solutionsPane,BoxLayout.Y_AXIS));
                solutionsPane.setPreferredSize(new Dimension(columns * width + 20, rows * height * max_shown));
                barPane = new JPanel();
                barPane.add(progressBar);
                barPane.setBounds(0,0,columns * width + 20,20);
                barPane.setPreferredSize(new Dimension(columns * width + 20,20));
                barPane.setSize(new Dimension(columns * width, 20));
                barPane.setMaximumSize(new Dimension(columns * width + 20, 20));
                solutionsPane.add(barPane);
                solutionsPane.add(scrollPane);

                buttonsPane = new JPanel();
                buttonsPane.setLayout(new BoxLayout(buttonsPane,BoxLayout.Y_AXIS));
                buttonsPane.setPreferredSize(new Dimension(columns * width, rows * height * max_shown));
                buttonsPane.add(open);

                open.addActionListener(new ActionListener() {

                    @Override
                    public synchronized void actionPerformed(ActionEvent e) {
                        if (DancingLinks.done) {
                            fileChooser.setFileFilter(new FileNameExtensionFilter("Pentomino input files (.txt)", "txt"));
                            File workingDirectory = new File(System.getProperty("user.dir"));
                            fileChooser.setCurrentDirectory(workingDirectory);
                            int returnValue = fileChooser.showOpenDialog(GUI.this);
                            if (returnValue == JFileChooser.APPROVE_OPTION) {
                                File file = fileChooser.getSelectedFile();
                                new Thread(new Runnable() {

                                    @Override
                                    public void run() {
                                        DancingLinks.read(file);
                                    }
                                }).start();
                            }
                        } else JOptionPane.showMessageDialog(f,"Please wait for the solutions to be found for this problem.");
                    }
                });
                play.addActionListener(new ActionListener() {

                    @Override
                    public synchronized void actionPerformed(ActionEvent e) {
                        if (num_pieces > 0) {
                            if (thread != null) {
                                if (DancingLinks.stepping) DancingLinks.stepping = false;
                                if (!DancingLinks.done) resume();
                                else {
                                    list.removeAll();
                                    start();
                                }
                            }
                            else {
                                start();
                            }
                        }
                    }
                });
                step.addActionListener(new ActionListener() {

                    @Override
                    public synchronized void actionPerformed(ActionEvent e) {
                        if (DancingLinks.stepping) {
                            if (thread != null) resume();
                            else {
                                start();
                            }
                        } else {
                            showAttempts.setSelected(true);
                            if (!attemptPanel.isVisible()) attemptPanel.setVisible(true);
                            DancingLinks.showing_attempts = true;
                            DancingLinks.stepping = true;
                        }
                    }
                });
                showSolutions.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (showSolutions.isSelected()) list.setVisible(true);
                        else list.setVisible(false);
                    }
                });
                showSolutions.setSelected(true);
                showAttempts.addActionListener(new ActionListener() {

                    @Override
                    public synchronized void actionPerformed(ActionEvent e) {
                        if (showAttempts.isSelected()) {
                            DancingLinks.showing_attempts = true;
                            attemptPanel.setVisible(true);
                        } else {
                            DancingLinks.showing_attempts = false;
                            attemptPanel.removeAll();
                            attemptPanel.setVisible(false);
                        }
                    }
                });
                pause.addActionListener(new ActionListener() {

                    @Override
                    public synchronized void actionPerformed(ActionEvent e) {
                        if (thread != null && thread.isAlive()) {
                            paused = true;
                            thread.suspend();
                        }
                    }
                });
                firstSolution.addActionListener(new ActionListener() {

                    @Override
                    public synchronized void actionPerformed(ActionEvent e) {
                        if (firstSolution.isSelected()) {
                            DancingLinks.firstSolution = true;
                        } else DancingLinks.firstSolution = false;
                    }
                });
                buttonsPane.add(play);
                buttonsPane.add(pause);
                buttonsPane.add(step);
                buttonsPane.add(showSolutions);
                buttonsPane.add(showAttempts);
                buttonsPane.add(firstSolution);

                attemptPanel = new JPanel();
                attemptPanel.setSize(new Dimension(columns * width + 20, rows * height));
                attemptPanel.setLayout(new BoxLayout(attemptPanel, BoxLayout.Y_AXIS));
                attemptPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                attemptPanel.setBackground(Color.BLACK);
                buttonsPane.add(attemptPanel);

                contentPane.add(buttonsPane);
                contentPane.add(solutionsPane);
                progressBar.setString("0 Pentominoes");
                f.setContentPane(contentPane);
                f.pack();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.setAlwaysOnTop(true);
                f.setVisible(true);
            }
        });
    }
    private void resume() {
        paused = false; thread.resume();
    }
    public void add(ArrayList<DLY> solution, int num) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (num != 0) {
                    JLabel label = new JLabel("Solution #" + num);
                    label.setSize(width * 100, 10);
                    label.setPreferredSize(new Dimension(width * 100, 10));
                    label.setForeground(Color.WHITE);
                    list.add(label);
                }
                Solution new_solution = new Solution(num_pieces, columns, rows, width, height, solution);
                list.add(new_solution);
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
    private void start() {
        open.setEnabled(false);
        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                DancingLinks.start();
            }
        });
        DancingLinks.thread = thread;
        thread.start();
    }
    public void setProgress(int n) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setString(null);
                progressBar.setValue(n);
            }
        });
    }
    public void recreate(int columns, int rows, int num_pieces) {
        this.num_pieces = num_pieces;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.columns = columns;
                f.rows = rows;
                setTitle("Pentomino Solver");
                int max_shown = 5;
                if (rows * height * num_shown > 1500) max_shown = 1500 / (rows * height);
                if (max_shown < 1) max_shown = 1;
                setSize(columns * width + 20, rows * height * max_shown + 20);
                scrollPane.setSize(columns * width + 20, rows * height * max_shown);
                contentPane.setSize(new Dimension(columns * width * 2 + 40, rows * height * max_shown));
                contentPane.setPreferredSize(new Dimension(columns * width * 2 + 40, rows * height * max_shown));
                solutionsPane.setPreferredSize(new Dimension(columns * width + 20, rows * height * max_shown));
                solutionsPane.setSize(new Dimension(columns * width + 20, rows * height * max_shown));
                barPane.setBounds(0, 0, columns * width + 20, 20);
                barPane.setPreferredSize(new Dimension(columns * width + 20, 20));
                barPane.setSize(new Dimension(columns * width, 20));
                barPane.setMaximumSize(new Dimension(columns * width + 20, 20));
                buttonsPane.setPreferredSize(new Dimension(columns * width, rows * height * max_shown));
                buttonsPane.setSize(new Dimension(columns * width, rows * height * max_shown));
                attemptPanel.setSize(new Dimension(columns * width + 20, rows * height));
                list.setBounds(0, 10, columns * width + 20, rows * height * max_shown);
                list.setSize(columns * width + 20, rows * height * max_shown);
                list.removeAll();
                list.revalidate();
                attemptPanel.revalidate();
                scrollPane.revalidate();
                contentPane.revalidate();
                solutionsPane.revalidate();
                barPane.revalidate();
                buttonsPane.revalidate();
                buttonsPane.repaint();
                barPane.repaint();
                solutionsPane.repaint();
                scrollPane.repaint();
                attemptPanel.repaint();
                list.repaint();
                progressBar.setString(num_pieces + " Pentominoes Loaded");
                f.revalidate();
                f.pack();
                f.repaint();
            }
        });
    }
    public void showAttempt(ArrayList<DLY> solution) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                attemptPanel.removeAll();
                Solution attempt = new Solution(num_pieces, columns, rows, width, height, solution);
                attemptPanel.add(attempt);
                attemptPanel.revalidate();
                attemptPanel.repaint();
            }
        });
    }
    public void finish(String msg) {
        progressBar.setValue(50);
        progressBar.setString(msg);
        open.setEnabled(true);
    }
}
