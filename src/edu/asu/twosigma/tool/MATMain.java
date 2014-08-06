package edu.asu.twosigma.tool;

import edu.asu.twosigma.tool.gui.VideoView;
import edu.asu.twosigma.tool.gui.LogView;
import edu.asu.twosigma.tool.util.FileWriter;
import au.com.bytecode.opencsv.CSVReader;
import edu.asu.twosigma.graph.coding.Coding;
import edu.asu.twosigma.graph.gui.GUIUtil;
import edu.asu.twosigma.graph.models.Action;
import edu.asu.twosigma.graph.models.ActionParsedDTO;
import edu.asu.twosigma.graph.models.Problem;
import edu.asu.twosigma.graph.models.ProblemManager;
import edu.asu.twosigma.graph.models.State;
import edu.asu.twosigma.tool.models.LogMessage;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
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
import java.util.ArrayList;
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
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.collections15.Transformer;


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
    
    private static final JFileChooser ourFileSelector = new JFileChooser();
    //video variables
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
    //log variables
    int selectedRowIndex = 0;
    ArrayList<LogMessage> ourLogMessages;
    LogView ourLog;
    File ourLogFile;
    //undo variables
    enum reversableActions {
        none,
        addRow,
        deleteRow,
        setTimestamp
    }
    Object lastRowEdited[] = new Object[7];
    int lastRowEditedIndex = -1;
    String lastTimestampEdited = "";
    reversableActions lastAction = reversableActions.none;
    // graph state information
    ActionParsedDTO dto;
    private Action currentEdge;
    private State currentVertex;
    private Integer problemNum = 1;
    private Integer studentNum = 3;
    private static final ProblemManager manager;
    static {
        manager = new ProblemManager();
    }
//    private static final String FILE = "logs/new/quinnGrayMB-log.csv";
//    private static final String FILE = "logs/new/dell-log.csv";
//    private static final String FILE = "logs/new/macMini-log.csv";
    private static final String FILE = "logs/new/whiteMB-log.csv";
    
    private VisualizationViewer<State, Action> vvStudent;
    private VisualizationViewer<State, Action> vvAll;
    
    private final Transformer currentLabelTransformer;
    
    
    public MATMain() {
        
        initComponents();
        
        currentLabelTransformer = null;
        
        //Initialize variables
        ourLogMessages = new ArrayList<>();
        
        //Load configuration settings
        Properties prop = new Properties();
        try{
            prop.load(new FileInputStream("config.properties"));
            vlcPath = prop.getProperty("vlcpath");
        } catch(IOException e)
        {
            System.out.println("No existing VLC path");
        }
        
        //set up periodic Log/Video GUI updates
         playTimer = new Timer(1000, new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
             try {
                //update video player progress bar
                videoLength = currentVideo.getVideoLength();
                long currentTime = currentVideo.getTimestamp();
                double progress = currentTime * 100 / videoLength;
                vidProgressBar.setValue((int) progress);
              
                //highlight current video action in log view
                boolean playingNextAction = compareVideoToLog();
                while(playingNextAction)
                {
                    selectedRowIndex++;
                }
                logTable.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);

                
                //highlight selected action on graph
                currentEdge.setHighlighted(false);
                currentVertex.setHighlighted(false);
                
                currentEdge = ourLogMessages.get(logTable.getSelectedRow()).getEdge();
                currentEdge.setHighlighted(true);
                currentVertex = ourLogMessages.get(logTable.getSelectedRow()).getVertex();
                currentVertex.setHighlighted(true);
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
            String action, param, timestamp = "";
            
            // Iterating over each value in the file
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                if(nextLine.length > 6){
                    action = nextLine[1];
                    param = nextLine[2];
                    dto = manager.handleAction(action, param);
                    if(dto != null){
                        Coding c = new Coding(
                                nextLine[2],
                                nextLine[3],
                                nextLine[4],
                                nextLine[5],
                                nextLine[6]
                        );
                        dto.getAction().getCoding().add(c);
                    }
                    System.out.println(dto);
                }
            }
            
            // Setting the graphs
            setCombinedGraph();
            setIndividualGraph();
            
            // Setting comboboxes
            this.jcbProblems.setModel(new DefaultComboBoxModel(manager.getProblemsList()));
            this.jcbStudents.setModel(new DefaultComboBoxModel(manager.getStudentsList()));
            
            // Setting visualization combobox
            setVisualizationCombo(this.jcbVisualization);
            
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(GUIApplication.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            //Logger.getLogger(GUIApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void setVisualizationCombo(JComboBox jcbVisualization) {
        jcbVisualization.setModel(new DefaultComboBoxModel<>(GUIUtil.getVisualizationTransformerList()));
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

        videoPanel = new javax.swing.JPanel();
        vidStopButton = new javax.swing.JToggleButton();
        vidPauseButton = new javax.swing.JToggleButton();
        vidPlayButton = new javax.swing.JToggleButton();
        vidSetVlcPathButton = new javax.swing.JButton();
        vidLoadButton = new javax.swing.JButton();
        vidProgressBar = new javax.swing.JProgressBar();
        logScrollPane = new javax.swing.JScrollPane();
        logTable = new javax.swing.JTable();
        logLoadButton = new javax.swing.JButton();
        logSaveButton = new javax.swing.JButton();
        logSyncButton = new javax.swing.JButton();
        logAddActionButton = new javax.swing.JButton();
        logDeleteActionButton = new javax.swing.JButton();
        logSetTimestampButton = new javax.swing.JButton();
        logUndoButton = new javax.swing.JButton();
        graphPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jcbProblems = new javax.swing.JComboBox();
        allProblemsPanel = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jcbVisualization = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jcbStudents = new javax.swing.JComboBox();
        studentPanel = new javax.swing.JPanel();
        logJumpToActionButton = new javax.swing.JButton();
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

        videoPanel.setBackground(new java.awt.Color(0, 0, 0));

        javax.swing.GroupLayout videoPanelLayout = new javax.swing.GroupLayout(videoPanel);
        videoPanel.setLayout(videoPanelLayout);
        videoPanelLayout.setHorizontalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        videoPanelLayout.setVerticalGroup(
            videoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
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

        vidSetVlcPathButton.setText("Set VLC path");
        vidSetVlcPathButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vidSetVlcPathButtonActionPerformed(evt);
            }
        });

        vidLoadButton.setText("Load video file");
        vidLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vidLoadButtonActionPerformed(evt);
            }
        });

        vidProgressBar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                vidProgressBarMouseClicked(evt);
            }
        });
        vidProgressBar.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                vidProgressBarCaretPositionChanged(evt);
            }
        });

        logTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Action", "Timestamp", "Solution Cards", "Vertical Proximity", "Horizontal Proximity", "Pre-Click Movement", "Post-Click Movement"
            }
        ));
        logTable.setFillsViewportHeight(true);
        logTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                logTableMouseClicked(evt);
            }
        });
        logScrollPane.setViewportView(logTable);

        logLoadButton.setText("Load log file (.csv)");
        logLoadButton.setPreferredSize(new java.awt.Dimension(140, 25));
        logLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logLoadButtonActionPerformed(evt);
            }
        });

        logSaveButton.setText("Save log (.csv)");
        logSaveButton.setPreferredSize(new java.awt.Dimension(140, 25));
        logSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logSaveButtonActionPerformed(evt);
            }
        });

        logSyncButton.setText("Sync");
        logSyncButton.setEnabled(false);

        logAddActionButton.setText("+ Add new action");
        logAddActionButton.setPreferredSize(new java.awt.Dimension(130, 25));
        logAddActionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logAddActionButtonActionPerformed(evt);
            }
        });

        logDeleteActionButton.setLabel("- Delete action");
        logDeleteActionButton.setPreferredSize(new java.awt.Dimension(130, 60));
        logDeleteActionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logDeleteActionButtonActionPerformed(evt);
            }
        });

        logSetTimestampButton.setText("Set timestamp");
        logSetTimestampButton.setPreferredSize(new java.awt.Dimension(130, 25));
        logSetTimestampButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logSetTimestampButtonActionPerformed(evt);
            }
        });

        logUndoButton.setText("Undo");
        logUndoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logUndoButtonActionPerformed(evt);
            }
        });

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
            .addGap(0, 401, Short.MAX_VALUE)
        );

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Labels");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jcbVisualization.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbVisualization.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jcbVisualizationActionPerformed(evt);
            }
        });

        jLabel3.setText("Visualize Data");

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
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox1)
                        .addGap(0, 358, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jcbVisualization, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jCheckBox1))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(jcbProblems, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                        .addGap(0, 648, Short.MAX_VALUE))
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
                .addComponent(jSplitPane1)
                .addContainerGap())
        );
        graphPanelLayout.setVerticalGroup(
            graphPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(graphPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1)
                .addContainerGap())
        );

        logJumpToActionButton.setText("Jump to action in video");
        logJumpToActionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logJumpToActionButtonActionPerformed(evt);
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vidProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(vidLoadButton)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vidSetVlcPathButton))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(vidStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vidPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(vidPlayButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(logAddActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(logDeleteActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(logSetTimestampButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(logUndoButton, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                        .addComponent(logLoadButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(logSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(logJumpToActionButton)
                                        .addGap(0, 0, Short.MAX_VALUE)))
                                .addGap(148, 148, 148)))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(logSyncButton, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(logScrollPane))
                        .addGap(18, 18, 18)))
                .addComponent(graphPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vidLoadButton)
                            .addComponent(vidSetVlcPathButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(videoPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(vidProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(vidStopButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vidPauseButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(vidPlayButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logLoadButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logSaveButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logSyncButton)
                            .addComponent(logJumpToActionButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(logScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(logAddActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logSetTimestampButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logDeleteActionButton, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(logUndoButton)))
                    .addComponent(graphPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    private void vidLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vidLoadButtonActionPerformed
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
    }//GEN-LAST:event_vidLoadButtonActionPerformed

    private void vidStopButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vidStopButtonActionPerformed
            //already stopped, should remain stopped
            vidStopButton.setSelected(true);
            currentVideo.executeStop();
            vidPlayButton.setSelected(false);
            vidPauseButton.setSelected(false);
            stopPlay();
    }//GEN-LAST:event_vidStopButtonActionPerformed
    
    private void logLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logLoadButtonActionPerformed
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
           ourLog = new LogView(ourLogFile, logPath);       
           DefaultTableModel newModel;
           newModel = ourLog.getTableModel();
           logTable.setModel(newModel);
           logTable.setRowSelectionInterval(0, 0);
           logTable.setVisible(true);
           //align log timestamps w/ video time
           strStartTime = ourLog.getStartTime();  
           
           //initialize variables to highlight selected action on graph
           currentEdge = ourLogMessages.get(0).getEdge();
           currentVertex = ourLogMessages.get(0).getVertex();
       } catch(FileNotFoundException e)
       {
           //PRINT ERROR MESSAGE
           System.out.println("Error loading log file");
       }
    }//GEN-LAST:event_logLoadButtonActionPerformed

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

    private void vidSetVlcPathButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_vidSetVlcPathButtonActionPerformed
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
        } catch (IOException e)
        {
            System.out.println("Unable to save settings");
        }
    }//GEN-LAST:event_vidSetVlcPathButtonActionPerformed

    private void vidProgressBarCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_vidProgressBarCaretPositionChanged
    }//GEN-LAST:event_vidProgressBarCaretPositionChanged

    private void vidProgressBarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_vidProgressBarMouseClicked
        int hour, minute;
        String newTimestamp;
        DefaultTableModel currentModel = (DefaultTableModel) logTable.getModel();
        //get the new position
        int mouseX = evt.getX();
        //Computes how far along the mouse is relative to the component width then multiply it by the progress bar's maximum value.
        int newPercentComplete = (int)Math.round(((double)mouseX / (double)vidProgressBar.getWidth()) * vidProgressBar.getMaximum());
        System.out.println(newPercentComplete);
        //seek in video
        currentVideo.setNewPosition(newPercentComplete);
        //update progress bar        
        vidProgressBar.setValue(newPercentComplete);
        //update selected row in log table
        int currentPosition = (int) (startTime + (currentVideo.getVideoLength() * newPercentComplete) / 100);
        hour = currentPosition / 3600000;
        minute = (currentPosition - (hour*3600000)) / 60000;
        newTimestamp = hour + ":" + minute;
        for(int row=0; row<currentModel.getRowCount(); row++)
        {
            if(currentModel.getValueAt(row, 1).equals(newTimestamp))
            {
                //set selected row
                selectedRowIndex = row;
                logTable.setRowSelectionInterval(selectedRowIndex, selectedRowIndex);
            }
        }
        
    }//GEN-LAST:event_vidProgressBarMouseClicked

    private void logSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logSaveButtonActionPerformed
        //save table to .csv file
        DefaultTableModel currentModel = (DefaultTableModel) logTable.getModel();
        FileWriter myWriter = new FileWriter();
        try {
            myWriter.writeToCSV(currentModel.getRowCount(), currentModel.getColumnCount(), currentModel, ourLog.getDateInfo(), ourLogFile);
        } catch (IOException ex) {
            Logger.getLogger(MATMain.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error saving .csv file.");
        }
        
    }//GEN-LAST:event_logSaveButtonActionPerformed

    private void logUndoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logUndoButtonActionPerformed
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
    }//GEN-LAST:event_logUndoButtonActionPerformed

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

    private void logJumpToActionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logJumpToActionButtonActionPerformed
        int currentRowIndex = logTable.getSelectedRow();
        String currentRowTimeStr = "";
        DefaultTableModel ourModel = (DefaultTableModel) logTable.getModel();
        
        //get time of currently selected action
        currentRowTimeStr = (String) ourModel.getValueAt(currentRowIndex, 1);
        
        String currentRowTimeArray[] = currentRowTimeStr.split(":");
        long currentActionTime = (Integer.parseInt(currentRowTimeArray[0]) * 3600000 + Integer.parseInt(currentRowTimeArray[1]) * 60000) - startTime;

        //seek in video
        long totalVideoLength = currentVideo.getVideoLength();
        int newPercentComplete = (int) ((currentActionTime * 100) / totalVideoLength);
        currentVideo.setNewPosition(newPercentComplete);
        //update progress bar        
        vidProgressBar.setValue(newPercentComplete);
        
        
    }//GEN-LAST:event_logJumpToActionButtonActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        setLabelTransformer(vvAll);
        setLabelTransformer(vvStudent);
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void setLabelTransformer(VisualizationViewer vv){
        boolean isSelected = jCheckBox1.isSelected();
        if(isSelected){
            // Display labels
            vv.getRenderContext().setEdgeLabelTransformer(GUIUtil.stringLabelTransformer);
            vv.getRenderContext().setVertexLabelTransformer(GUIUtil.stringLabelTransformer);
        } else {
            // Hide labels
            vv.getRenderContext().setEdgeLabelTransformer(GUIUtil.defaultLabelTransformer);
            vv.getRenderContext().setVertexLabelTransformer(GUIUtil.defaultLabelTransformer);  
        }
        vv.repaint();
    }
    
    private void logTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_logTableMouseClicked
        selectedRowIndex = logTable.getSelectedRow();
    }//GEN-LAST:event_logTableMouseClicked

    private void jcbVisualizationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jcbVisualizationActionPerformed
        JComboBox box = (JComboBox)evt.getSource();
        Transformer t = (Transformer)box.getSelectedItem();
        vvAll.getRenderContext().setEdgeFillPaintTransformer(t);
        vvAll.repaint();
    }//GEN-LAST:event_jcbVisualizationActionPerformed

    // graph GUI methods
     public final void setCombinedGraph(){
        // Selecting a problem and obtaining its graph
        Problem p = manager.getProblem(this.problemNum);
        Graph g = p.getGraph();
            
        // Getting visualization server for all students
        this.vvAll = GUIUtil.getGraphVisualizationViewer(
                g, 
                null,
                this.allProblemsPanel.getWidth(), 
                this.allProblemsPanel.getHeight(),
                null
        );
        
        setGraph(vvAll, this.allProblemsPanel);
        setLabelTransformer(vvAll);
    }
    
    public final void setIndividualGraph(){
        // Selecting a problem and obtaining its graph
        Problem p = manager.getProblem(this.problemNum);
        Graph g = p.getGraph();
        
        // Getting visualization server for one student
        this.vvStudent = GUIUtil.getGraphVisualizationViewerByStudent(
                g, 
                null,
                this.studentNum, 
                this.studentPanel.getWidth(), 
                this.studentPanel.getHeight()
        );
        
        setGraph(vvStudent, this.studentPanel);
        setLabelTransformer(vvStudent);
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
            @Override
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
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JComboBox jcbProblems;
    private javax.swing.JComboBox jcbStudents;
    private javax.swing.JComboBox jcbVisualization;
    private javax.swing.JButton logAddActionButton;
    private javax.swing.JButton logDeleteActionButton;
    private javax.swing.JButton logJumpToActionButton;
    private javax.swing.JButton logLoadButton;
    private javax.swing.JButton logSaveButton;
    private javax.swing.JScrollPane logScrollPane;
    private javax.swing.JButton logSetTimestampButton;
    private javax.swing.JButton logSyncButton;
    private javax.swing.JTable logTable;
    private javax.swing.JButton logUndoButton;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem openMenuItem;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JMenuItem saveAsMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JPanel studentPanel;
    private javax.swing.JButton vidLoadButton;
    private javax.swing.JToggleButton vidPauseButton;
    private javax.swing.JToggleButton vidPlayButton;
    private javax.swing.JProgressBar vidProgressBar;
    private javax.swing.JButton vidSetVlcPathButton;
    private javax.swing.JToggleButton vidStopButton;
    private javax.swing.JPanel videoPanel;
    // End of variables declaration//GEN-END:variables

}
