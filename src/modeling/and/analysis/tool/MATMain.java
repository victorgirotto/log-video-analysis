package modeling.and.analysis.tool;

import au.com.bytecode.opencsv.CSVReader;
import edu.asu.mgb.gui.GUIUtil;
import edu.asu.mgb.problem.Action;
import edu.asu.mgb.problem.Problem;
import edu.asu.mgb.problem.ProblemManager;
import edu.asu.mgb.problem.State;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import modeling.and.analysis.tool.VideoView;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Elissa
 */
public class MATMain extends javax.swing.JFrame {

    /**
     * Creates new form MATMain
     */
    
    private static JFileChooser ourFileSelector = new JFileChooser();
    VideoView currentVideo;
    String vlcPath;
    enum videoSpeeds {
        fastForward,
        rewind,
        normal
    };
    Timer playTimer;
    long videoLength;
    long startTime;
    String strStartTime;
    String strStartTimeArray[];
    int selectedRowIndex = 0;
    File ourLogFile;
    //undo variables
    enum reversableActions {
        none,
        addRow,
        deleteRow,
        setTimestamp
    }
    Object lastRowEdited[] = new Object[2];
    int lastRowEditedIndex = -1;
    String lastTimestampEdited = "";
    reversableActions lastAction = reversableActions.none;
    // graph state information
    private Integer problemNum = 3;
    private Integer studentNum = 3;
    private static final ProblemManager manager;
    static {
        manager = new ProblemManager();
    }
    private static final String FILE = "logs.csv";
    
    
    public MATMain() {
        initComponents();
        
        //Load configuration settings
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream("config.properties"));
            vlcPath = prop.getProperty("vlcpath");
        } catch(Exception e)
        {
            System.out.println("No existing VLC path");
        }
        
        //set up periodic Log/Video GUI updates
         playTimer = new Timer(500, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
             try {
                //update video player progress bar
                videoLength = currentVideo.getVideoLength();
                long currentTime = currentVideo.getTimestamp();
                double progress = currentTime * 100 / videoLength;
                jProgressBar1.setValue((int) progress);

                
                //highlight current video action in log view
                boolean playingNextAction = compareVideoToLog();
                if(playingNextAction)
                {
                    selectedRowIndex++;
                    logTable.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
                }
             }
             catch (Exception e)
             {
                System.out.println("timer fail");
             }
             }
        });
         
         //populate graph panel
         try {           
            // Reading CSV file into memory
            CSVReader reader = new CSVReader(new FileReader(FILE));
            String[] nextLine;
            String action;
            
            // Iterating over each value in the file
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                action = nextLine[0];
                manager.handleAction(action);
            }
            
            // Setting the graphs
            setCombinedGraph();
            setIndividualGraph();
            
            // Setting comboboxes
            this.jcbProblems.setModel(new DefaultComboBoxModel(manager.getProblemsList()));
            this.jcbStudents.setModel(new DefaultComboBoxModel(manager.getStudentsList()));
            
        } catch (FileNotFoundException ex) {
            System.out.println("Error");
            //Logger.getLogger(GUIApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println("Error");
            //Logger.getLogger(GUIApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean compareVideoToLog()
    {
        boolean match = false;
        String strLogTime = (String) logTable.getValueAt(selectedRowIndex+1, 1);
        String strLogTimeArray[] = strLogTime.split(":");
        long logTime = (Integer.parseInt(strLogTimeArray[0]) * 3600000 + Integer.parseInt(strLogTimeArray[1]) * 60000) - startTime;
        long videoTime = currentVideo.getTimestamp();
        
        
        if(videoTime >= logTime)
        {
            match = true;
        }
        
        return match;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        logTable = new javax.swing.JTable();
        graphPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbProblems = new javax.swing.JComboBox();
        allProblemsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jcbStudents = new javax.swing.JComboBox();
        studentPanel = new javax.swing.JPanel();
        logAddActionButton = new javax.swing.JButton();
        logSetTimestampButton = new javax.swing.JButton();
        logSyncButton = new javax.swing.JButton();
        logDeleteActionButton = new javax.swing.JButton();
        loadLogButton = new javax.swing.JButton();
        loadVideoButton = new javax.swing.JButton();
        videoPanel = new javax.swing.JPanel();
        vidStopButton = new javax.swing.JToggleButton();
        vidPauseButton = new javax.swing.JToggleButton();
        vidPlayButton = new javax.swing.JToggleButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        deleteMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        contentsMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1300, 900));

        logTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Action", "Timestamp"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        logTable.setFillsViewportHeight(true);
        jScrollPane2.setViewportView(logTable);
        if (logTable.getColumnModel().getColumnCount() > 0) {
            logTable.getColumnModel().getColumn(0).setResizable(false);
            logTable.getColumnModel().getColumn(1).setResizable(false);
        }

        jSplitPane1.setDividerLocation(450);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jLabel1.setText("Problem");

        jcbProblems.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbProblems.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbProblemsActionPerformed(evt);
            }
        });

        allProblemsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout allProblemsPanelLayout = new javax.swing.GroupLayout(allProblemsPanel);
        allProblemsPanel.setLayout(allProblemsPanelLayout);
        allProblemsPanelLayout.setHorizontalGroup(
            allProblemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        allProblemsPanelLayout.setVerticalGroup(
            allProblemsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 392, Short.MAX_VALUE)
        );

        jLabel3.setText("Click on the graph and press \"T\" to pan. Press \"P\" to get information from the graph.");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(allProblemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbProblems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 97, Short.MAX_VALUE)
                        .addComponent(jLabel3)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jcbProblems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(allProblemsPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel1);

        jLabel2.setText("Student");

        jcbStudents.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbStudents.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbStudentsActionPerformed(evt);
            }
        });

        studentPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        javax.swing.GroupLayout studentPanelLayout = new javax.swing.GroupLayout(studentPanel);
        studentPanel.setLayout(studentPanelLayout);
        studentPanelLayout.setHorizontalGroup(
            studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        studentPanelLayout.setVerticalGroup(
            studentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 554, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 585, Short.MAX_VALUE))
                    .addComponent(studentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jcbStudents, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(studentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout graphPanelLayout = new javax.swing.GroupLayout(graphPanel);
        graphPanel.setLayout(graphPanelLayout);
        graphPanelLayout.setHorizontalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 724, Short.MAX_VALUE)
                .addContainerGap())
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        logAddActionButton.setText("+ Add new action");
        logAddActionButton.setPreferredSize(new java.awt.Dimension(130, 25));
        logAddActionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logAddActionButtonActionPerformed(evt);
            }
        });

        logSetTimestampButton.setText("Set timestamp");
        logSetTimestampButton.setPreferredSize(new java.awt.Dimension(130, 25));
        logSetTimestampButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logSetTimestampButtonActionPerformed(evt);
            }
        });

        logSyncButton.setText("Sync");
        logSyncButton.setEnabled(false);

        logDeleteActionButton.setLabel("- Delete action");
        logDeleteActionButton.setPreferredSize(new java.awt.Dimension(130, 60));
        logDeleteActionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logDeleteActionButtonActionPerformed(evt);
            }
        });

        loadLogButton.setText("Load log file (.csv)");
        loadLogButton.setPreferredSize(new java.awt.Dimension(140, 25));
        loadLogButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadLogButtonActionPerformed(evt);
            }
        });

        loadVideoButton.setText("Load video file");
        loadVideoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadVideoButtonActionPerformed(evt);
            }
        });

        videoPanel.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 705, Short.MAX_VALUE)
        );

        vidStopButton.setText("■");
        vidStopButton.setMaximumSize(new java.awt.Dimension(60, 30));
        vidStopButton.setMinimumSize(new java.awt.Dimension(60, 30));
        vidStopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vidStopButtonActionPerformed(evt);
            }
        });

        vidPauseButton.setText("▮▮");
        vidPauseButton.setMaximumSize(new java.awt.Dimension(60, 30));
        vidPauseButton.setMinimumSize(new java.awt.Dimension(60, 30));
        vidPauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vidPauseButtonActionPerformed(evt);
            }
        });

        vidPlayButton.setText("►");
        vidPlayButton.setMaximumSize(new java.awt.Dimension(60, 30));
        vidPlayButton.setMinimumSize(new java.awt.Dimension(60, 30));
        vidPlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vidPlayButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Set VLC path");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Save log (.csv)");
        jButton2.setPreferredSize(new java.awt.Dimension(140, 25));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jProgressBar1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jProgressBar1MouseClicked(evt);
            }
        });
        jProgressBar1.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                jProgressBar1CaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });

        jButton3.setText("Undo");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");

        openMenuItem.setMnemonic('o');
        openMenuItem.setText("Open");
        fileMenu.add(openMenuItem);

        saveMenuItem.setMnemonic('s');
        saveMenuItem.setText("Save");
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('a');
        saveAsMenuItem.setText("Save As ...");
        saveAsMenuItem.setDisplayedMnemonicIndex(5);
        fileMenu.add(saveAsMenuItem);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        editMenu.add(cutMenuItem);

        copyMenuItem.setMnemonic('y');
        copyMenuItem.setText("Copy");
        editMenu.add(copyMenuItem);

        pasteMenuItem.setMnemonic('p');
        pasteMenuItem.setText("Paste");
        editMenu.add(pasteMenuItem);

        deleteMenuItem.setMnemonic('d');
        deleteMenuItem.setText("Delete");
        editMenu.add(deleteMenuItem);

        menuBar.add(editMenu);

        helpMenu.setMnemonic('h');
        helpMenu.setText("Help");

        contentsMenuItem.setMnemonic('c');
        contentsMenuItem.setText("Contents");
        helpMenu.add(contentsMenuItem);

        aboutMenuItem.setMnemonic('a');
        aboutMenuItem.setText("About");
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loadVideoButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(vidStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vidPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vidPlayButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 510, Short.MAX_VALUE)
                    .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(loadLogButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(logSyncButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(logAddActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logDeleteActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logSetTimestampButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 495, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loadVideoButton)
                            .addComponent(jButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vidStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vidPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vidPlayButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(loadLogButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logSyncButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logAddActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logSetTimestampButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logDeleteActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton3)))
                    .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void loadVideoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadVideoButtonActionPerformed
       String mediaPath = "";
       File ourFile;
       
       //Clear current video
       videoPanel.removeAll();
       
       //Open file selection dialogue
       ourFileSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
       ourFileSelector.showOpenDialog(null);
       ourFile = ourFileSelector.getSelectedFile();
       mediaPath = ourFile.getAbsolutePath();
       
       //Add newly selected video
       try {
       //currentVideo = new VideoView("C:\\Program Files\\VideoLAN\\VLC", mediaPath, videoPanel.getWidth(), videoPanel.getHeight());
       currentVideo = new VideoView(vlcPath, mediaPath, videoPanel.getWidth(), videoPanel.getHeight());
       videoPanel.setVisible(true);
       videoPanel.add(currentVideo.getVideoDisplay(), BorderLayout.CENTER);
       //GUI updates
       currentVideo.run();
       vidPlayButton.setSelected(true);
       //start timer functionalities that occur during playback  
       startPlay();
       } catch(Exception e)
       {
           JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
           JOptionPane.showMessageDialog(parent, "Please select a valid video file.", "Playback Error", JOptionPane.ERROR_MESSAGE);
       }
       strStartTimeArray = strStartTime.split(":");
       System.out.println(strStartTimeArray[1]);
       startTime = Integer.parseInt(strStartTimeArray[0]) * 3600000 + Integer.parseInt(strStartTimeArray[1]) * 60000;       
       System.out.println(startTime);
    }//GEN-LAST:event_loadVideoButtonActionPerformed

    private void vidStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vidStopButtonActionPerformed
            //already stopped, should remain stopped
            vidStopButton.setSelected(true);
            currentVideo.executeStop();
            vidPlayButton.setSelected(false);
            vidPauseButton.setSelected(false);
            stopPlay();
    }//GEN-LAST:event_vidStopButtonActionPerformed
    
    private void loadLogButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadLogButtonActionPerformed
       String logPath = "";

       //Clear current log file
       logTable.removeAll();
       
       //Open file selection dialogue
       ourFileSelector.setFileSelectionMode(JFileChooser.FILES_ONLY);
       ourFileSelector.showOpenDialog(null);
       ourLogFile = ourFileSelector.getSelectedFile();
       logPath = ourLogFile.getAbsolutePath();
       
       //Add newly selected log file
       try 
       {
           LogView ourLog = new LogView(ourLogFile, logPath);       
           DefaultTableModel newModel;
           newModel = ourLog.getTableModel();
           logTable.setModel(newModel);
           logTable.setRowSelectionInterval(0, 0);
           logTable.setVisible(true);
           //align log timestamps w/ video time
           strStartTime = ourLog.getStartTime();        
       } catch(Exception e)
       {
           //PRINT ERROR MESSAGE
           System.out.println("Error loading log file");
       }
    }//GEN-LAST:event_loadLogButtonActionPerformed

    private void vidPlayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vidPlayButtonActionPerformed
        if(vidPauseButton.isSelected())
        {
            startPlay();
            vidPauseButton.setSelected(false);
        }
        if(vidStopButton.isSelected())
        {
            startPlay();
            vidStopButton.setSelected(false);
        }
        else
        {
            //already plauing, should remain playing
            vidPlayButton.setSelected(true);
        }
    }//GEN-LAST:event_vidPlayButtonActionPerformed

    private void startPlay() {
        currentVideo.executePlay();
        playTimer.start();
    }
    
    private void stopPlay() {
        playTimer.stop();//purge();
    }
    
    private void logAddActionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logAddActionButtonActionPerformed
        long ourTimestamp;
        int currentRow = logTable.getSelectedRow();
        Object[] newRow = new Object[2];
        DefaultTableModel ourModel = (DefaultTableModel) logTable.getModel();
        
        //save current timestamp in case of undo
        lastAction = reversableActions.addRow;
        lastRowEditedIndex = currentRow+1;
        for(int i=0; i<ourModel.getColumnCount(); i++)
        {
            lastRowEdited[i] = ourModel.getValueAt(currentRow, i);
        }
        
        try {
            ourTimestamp = currentVideo.getTimestamp();
            newRow[0] = "new action";
            newRow[1] = ourTimestamp;
        } catch (Exception e)
        {
            //if a video is not currently loaded
            ourTimestamp = 0;
            newRow[0] = "new action";
            newRow[1] = "";
        }

        //insert the new row below the currently selected row
        ourModel.insertRow(currentRow+1, newRow);
    }//GEN-LAST:event_logAddActionButtonActionPerformed

    private void logDeleteActionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logDeleteActionButtonActionPerformed
        int currentRow = logTable.getSelectedRow();
        DefaultTableModel ourModel = (DefaultTableModel) logTable.getModel();
        
        //save current timestamp in case of undo
        lastAction = reversableActions.deleteRow;
        lastRowEditedIndex = currentRow;
        for(int i=0; i<ourModel.getColumnCount(); i++)
        {
            lastRowEdited[i] = ourModel.getValueAt(currentRow, i);
        }
        
        try{
        //delete the currently selected row
        ourModel.removeRow(currentRow);
        } catch (Exception e) {
            //print error message if no row selected
            System.out.println("No row selected!");
        }
    }//GEN-LAST:event_logDeleteActionButtonActionPerformed

    private void logSetTimestampButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logSetTimestampButtonActionPerformed
        long ourTimestamp, hours, minutes;
        String strTimestamp;
        int currentRow = logTable.getSelectedRow();
        DefaultTableModel ourModel = (DefaultTableModel) logTable.getModel();
        
        //save current timestamp in case of undo
        lastAction = reversableActions.setTimestamp;
        lastTimestampEdited = (String) ourModel.getValueAt(currentRow, 1);
        lastRowEditedIndex = currentRow;
        
        try {
            ourTimestamp = currentVideo.getTimestamp() + startTime;
            //update timestamp of selected row with current video timestamp
            hours = ourTimestamp / 3600000;
            minutes = (ourTimestamp-(hours*3600000)) / 60000;
            strTimestamp = hours + ":" + minutes;
            ourModel.setValueAt(strTimestamp, currentRow, 1);      
        } catch (Exception e)
        {
            //if a video is not currently loaded
            System.out.println("Video must be loaded to set timestamp!");
        }
    }//GEN-LAST:event_logSetTimestampButtonActionPerformed

    private void vidPauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vidPauseButtonActionPerformed
        if(vidPlayButton.isSelected())
        {
            currentVideo.executePause();
            vidPlayButton.setSelected(false);
            stopPlay();
        }
        if(vidStopButton.isSelected())
        {
            vidPauseButton.setSelected(false);
        }
        else
        {
            //already paused, should remain paused
            vidPauseButton.setSelected(true);
        }
    }//GEN-LAST:event_vidPauseButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       File ourFile;
        
        //open file dialogue to let user select VLC.exe path location
       ourFileSelector.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
       ourFileSelector.setDialogTitle("Select the directory containing vlc.exe");
       ourFileSelector.showSaveDialog(null);
       ourFile = ourFileSelector.getSelectedFile();
       vlcPath = ourFile.getAbsolutePath();
       
        //save VLC path property
        Properties prop = new Properties();
        try{
            prop.setProperty("vlcpath", vlcPath);
            prop.store(new FileOutputStream("config.properties"), null);
        } catch (Exception e)
        {
            System.out.println("Unable to save settings");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jProgressBar1CaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_jProgressBar1CaretPositionChanged
    }//GEN-LAST:event_jProgressBar1CaretPositionChanged

    private void jProgressBar1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jProgressBar1MouseClicked
        //get the new position
        int mouseX = evt.getX();
        //Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
        int newPercentComplete = (int)Math.round(((double)mouseX / (double)jProgressBar1.getWidth()) * jProgressBar1.getMaximum());
        System.out.println(newPercentComplete);
        //seek in video
        currentVideo.setNewPosition(newPercentComplete);
        //update progress bar        
        jProgressBar1.setValue(newPercentComplete);

    }//GEN-LAST:event_jProgressBar1MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //save table to .csv file
        DefaultTableModel currentModel = (DefaultTableModel) logTable.getModel();
        FileWriter myWriter = new FileWriter();
        try {
            myWriter.writeToCSV(currentModel.getRowCount(), currentModel.getColumnCount(), currentModel, ourLogFile);
        } catch (IOException ex) {
            Logger.getLogger(MATMain.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error saving .csv file.");
        }
        
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DefaultTableModel currentModel;
        if(lastAction == reversableActions.none)
        {
            //maybe print some kind of error message
        }
        else if(lastAction == reversableActions.addRow)
        {
            currentModel = (DefaultTableModel) logTable.getModel();
            currentModel.removeRow(lastRowEditedIndex);
            lastAction = reversableActions.none;
        }
        else if(lastAction == reversableActions.deleteRow)
        {
            currentModel = (DefaultTableModel) logTable.getModel();
            currentModel.insertRow(lastRowEditedIndex, lastRowEdited);
            lastAction = reversableActions.none;
        }
        else if(lastAction == reversableActions.setTimestamp)
        {
            currentModel = (DefaultTableModel) logTable.getModel();
            currentModel.setValueAt(lastTimestampEdited, lastRowEditedIndex, 1);
            lastAction = reversableActions.none;
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jcbProblemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbProblemsActionPerformed
        JComboBox box = (JComboBox)evt.getSource();
        this.problemNum = (Integer)box.getSelectedItem();

        setCombinedGraph();
        setIndividualGraph();
    }//GEN-LAST:event_jcbProblemsActionPerformed

    private void jcbStudentsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbStudentsActionPerformed
        JComboBox box = (JComboBox)evt.getSource();
        this.studentNum = (Integer)box.getSelectedItem();

        setIndividualGraph();
    }//GEN-LAST:event_jcbStudentsActionPerformed

    // graph GUI methods
    public final void setCombinedGraph(){
        // Selecting a problem and obtaining its graph
        Problem p = manager.getProblem(this.problemNum);
        Graph g = p.getGraph();
            
        // Getting visualization server for all students
        VisualizationViewer<State, Action> vvAll = GUIUtil.getGraphVisualizationViewer(
                g, 
                this.allProblemsPanel.getWidth(), 
                this.allProblemsPanel.getHeight()
        );
        
        setGraph(vvAll, this.allProblemsPanel);
    }
    
    public final void setIndividualGraph(){
        // Selecting a problem and obtaining its graph
        Problem p = manager.getProblem(this.problemNum);
        Graph g = p.getGraph();
        
        // Getting visualization server for one student
        VisualizationViewer<State, Action> vvStudent = GUIUtil.getGraphVisualizationViewerByStudent(
                g, 
                this.studentNum, 
                this.studentPanel.getWidth(), 
                this.studentPanel.getHeight()
        );
        
        setGraph(vvStudent, this.studentPanel);
    }
    
    public final void setGraph(VisualizationViewer vv, JPanel panel){
        panel.removeAll();
        panel.add(vv);
    }
    
    private void changeVideoSpeed(videoSpeeds speed)
    {
        if(speed == videoSpeeds.fastForward)
        {
            currentVideo.executeFastForward();
        }
        else if(speed == videoSpeeds.rewind)
        {
            currentVideo.executeRewind();
        }
        else if(speed == videoSpeeds.normal)
        {
            currentVideo.executePlay();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MATMain.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MATMain().setVisible(true);  
            }
        });
        
        /* Add graph elements to form */
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JPanel allProblemsPanel;
    private javax.swing.JMenuItem contentsMenuItem;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JMenu editMenu;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JPanel graphPanel;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JComboBox jcbProblems;
    private javax.swing.JComboBox jcbStudents;
    private javax.swing.JButton loadLogButton;
    private javax.swing.JButton loadVideoButton;
    private javax.swing.JButton logAddActionButton;
    private javax.swing.JButton logDeleteActionButton;
    private javax.swing.JButton logSetTimestampButton;
    private javax.swing.JButton logSyncButton;
    private javax.swing.JTable logTable;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JPanel studentPanel;
    private javax.swing.JToggleButton vidPauseButton;
    private javax.swing.JToggleButton vidPlayButton;
    private javax.swing.JToggleButton vidStopButton;
    private javax.swing.JPanel videoPanel;
    // End of variables declaration//GEN-END:variables

}
