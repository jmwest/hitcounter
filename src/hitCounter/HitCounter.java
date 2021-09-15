package hitCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class HitCounter implements ActionListener, MouseListener {

	/**************************
	 * Final Static Variables *
	 **************************/
	
    final static int rgbHLBlue=new Color(127, 100, 228).getRGB();
    final static int rgbHLGreen=new Color(100, 228, 127).getRGB();
    final static int rgbHLRed=new Color(228, 127, 100).getRGB();
    final static int rgbHLGold=new Color(235, 200, 115).getRGB();
    
    final static int splitWidth=150;
    final static int hitWidth=50;
    final static int diffWidth=50;
    final static int pbWidth=50;

    /*****************************
     * Declare private variables *
     *****************************/
    
    private String userSaveFilePathString = System.getProperty("user.dir") + System.getProperty("file.separator")
											+ "resources" + System.getProperty("file.separator") + "usersave.txt";
	
    private boolean hasSavedPB = true;
    
    private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenuItem saveMenuItem;
	private JMenuItem incHitMenuItem;
	private JMenuItem decHitMenuItem;
	private JMenuItem nextSplitMenuItem;
	
	private JFrame frame;
	private JPanel panel;
	private JPanel splitsPanel;
	private JScrollPane scrollPane;
	
	private JButton resetRunButton;
	private JButton setPBButton;
	
	private JTextPane splitNameHeader;
	private JTextPane currentHitsHeader;
	private JTextPane hitDifferenceHeader;
	private JTextPane pBHitsHeader;
	
	private ArrayList<JTextPane> splitNameTextArrayList;
	private ArrayList<JTextPane> splitCurrentHitsArrayList;
	private ArrayList<JTextPane> splitHitDifferenceArrayList;
	private ArrayList<JTextPane> pBHitsArrayList;
	
	private JTextPane totalTextPane;
	private JTextPane totalHitsTextPane;
	private JTextPane totalDifferenceTextPane;
	private JTextPane totalPBHitsTextPane;
	
	private ArrayList<String> splitNames;
	private ArrayList<String> pBSplitArrayList;
	private ArrayList<String> pBCumulativeArrayList;
	
	private int pBTotalHits = 0;
	private int focusedPaneHits = 0;
	private int currentSplit = 0;
	
	/****************************
	 * Public Class Constructor *
	 ****************************/
	
	public HitCounter() {

		// Menu setup
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		
		// Create MenuItems and handle them.
		saveMenuItem = new JMenuItem("Save...");
		saveMenuItem.addActionListener(this);
		
		fileMenu.add(saveMenuItem);
		
		incHitMenuItem = new JMenuItem("Add Hit");
		incHitMenuItem.addActionListener(this);
		incHitMenuItem.setAccelerator(KeyStroke.getKeyStroke('h'));
		
		decHitMenuItem = new JMenuItem("Remove Hit");
		decHitMenuItem.addActionListener(this);
		decHitMenuItem.setAccelerator(KeyStroke.getKeyStroke('u'));

		nextSplitMenuItem = new JMenuItem("Next Split");
		nextSplitMenuItem.addActionListener(this);
		nextSplitMenuItem.setAccelerator(KeyStroke.getKeyStroke('n'));

		editMenu.add(incHitMenuItem);
		editMenu.add(decHitMenuItem);
		editMenu.add(nextSplitMenuItem);
		
		//
		frame = new JFrame();
		panel = new JPanel();
		splitsPanel = new JPanel();
		scrollPane = new JScrollPane();
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));

		resetRunButton = new JButton("Reset Run");
		resetRunButton.addActionListener(this);
		
		resetRunButton.setMinimumSize(new Dimension(100, 20));
		resetRunButton.setPreferredSize(new Dimension(100, 20));
		resetRunButton.setMaximumSize(new Dimension(100, 20));
		
		setPBButton = new JButton("Set Run as PB");
		setPBButton.addActionListener(this);
		
		setPBButton.setMinimumSize(new Dimension(100, 20));
		setPBButton.setPreferredSize(new Dimension(100, 20));
		setPBButton.setMaximumSize(new Dimension(100, 20));
		
		splitNameTextArrayList = new ArrayList<JTextPane>();
		splitCurrentHitsArrayList = new ArrayList<JTextPane>();
		splitHitDifferenceArrayList = new ArrayList<JTextPane>();
		pBHitsArrayList = new ArrayList<JTextPane>();
		
		splitNames = new ArrayList<String>();
		pBSplitArrayList = new ArrayList<String>();
		pBCumulativeArrayList = new ArrayList<String>();
		
		loadZOOTSplits();
		loadProgram();
		
		if (!hasSavedPB) {
			
			for (int i = 0; i < splitNames.size(); i++) {
				
				pBSplitArrayList.add("0");
			}
		}
		
		// Need to load PB info before here
		makePBCumulativeArrayList();
		
		// Need Split names & PB Info before here.
		createSplitTextPanes(splitNames);
		
		// 
		pBTotalHits = Integer.parseInt(pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1));
		String totalHitDiffString = getHitDifference("0", String.valueOf(pBTotalHits));
		createHeaderAndTotalTextPanes("0", totalHitDiffString, String.valueOf(pBTotalHits));
		
		// Set Panels
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setOpaque(true);
		panel.setLayout(createTopLeveGroupLayout(panel));
		panel.setBackground(Color.black);
		
		splitsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		splitsPanel.setOpaque(false);
		splitsPanel.setLayout(createSplitsGroupLayout(splitsPanel));
		
		int splitPanelWidth = splitWidth + hitWidth + diffWidth + pbWidth + 10*5;
		int splitPanelHeight = (27*splitNames.size());
		setPanelDimensions(splitsPanel, new Dimension(splitPanelWidth, splitPanelHeight));

		System.out.println("Split #: " + String.valueOf(splitNames.size()));
		
		// Set ScrollPane
		scrollPane.setPreferredSize(new Dimension(splitPanelWidth, 400));
		scrollPane.setViewportView(splitsPanel);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		
		// Set Frame
		frame.setJMenuBar(menuBar);
		frame.addMouseListener(this);
		//frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Hit Counter");
		frame.getContentPane().add(panel);
		//frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**********************
	 * Callback Functions *
	 **********************/
	
	public interface Callback {
		void nextsplitcallback(int pos);
		void highlightcallback();
	};

	
	/****************************
	 * Load Default ZOOT Splits *
	 ****************************/
	
	private void loadZOOTSplits() {
		
		splitNames.add("Ghoma");
		splitNames.add("Zeldo");
		splitNames.add("Saria");
		splitNames.add("Lizolfos 1");
		splitNames.add("Lizolfos 2");

		splitNames.add("Dodongo");
		splitNames.add("Boomerang");
		splitNames.add("Big Octo");
		splitNames.add("Baranade");
		splitNames.add("Stalfos 1");

		splitNames.add("Stalfos 2");
		splitNames.add("Phantom Ganon");
		splitNames.add("Nut Sack 1");
		splitNames.add("Vulvagina");
		splitNames.add("Lens of Truth");

		splitNames.add("Iron Boots");
		splitNames.add("Dark Link");
		splitNames.add("Morpha");
		splitNames.add("Hover Boots");
		splitNames.add("Bongos");

		splitNames.add("Gerudo Card");
		splitNames.add("Requiem");
		splitNames.add("Silver Gauntlets");
		splitNames.add("Nabooru");
		splitNames.add("Twinrova");

		splitNames.add("Forest Trial");
		splitNames.add("Water Trial");
		splitNames.add("Shadow Trial");
		splitNames.add("Fire Trial");
		splitNames.add("Light Trial");

		splitNames.add("Spirit Trial");
		splitNames.add("Ganon Dinalfos");
		splitNames.add("Ganon Stalfos");
		splitNames.add("B&W Knuckles");
		splitNames.add("Ganondorf");

		splitNames.add("Collapse");
		splitNames.add("Ganon");

		return;
	}
	
	/******************************************
	 * Functions to create UI variable arrays *
	 ******************************************/
	
	//
	private void createSplitTextPanes(ArrayList<String> splits) {
		
		Color splitColor = new Color(rgbHLBlue);
		
		for (int i = 0; i < splits.size(); i++) {
			
			if (i > 0) {
				splitColor = Color.white;
			}
			
			// Create Alignments
			SimpleAttributeSet left = new SimpleAttributeSet();
			StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
			
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			
			// Split Name
			JTextPane splitNameTextPane = new JTextPane();
			
			setJTextPaneDimensions(splitNameTextPane, 100, splitWidth, splitWidth, 15);
			setTextPaneAttributes(left, splitNameTextPane, splits.get(i), true, false, splitColor);
			splitNameTextPane.addMouseListener(this);
			splitNameTextPane.setName(splits.get(i));

			splitNameTextArrayList.add(splitNameTextPane);
			
			// Hit Count
			JTextPane hitCountTextPane = new JTextPane();
			hitCountTextPane.setName(splits.get(i));
			hitCountTextPane.addMouseListener(this);
			hitCountTextPane.setName(splits.get(i));

			hitCountTextPane.addFocusListener(new FocusListener() {
				
				public void focusGained(FocusEvent e) {
					JTextPane currentPane = (JTextPane) e.getSource();
					focusedPaneHits = Integer.parseInt(currentPane.getText());
				}
			    
				public void focusLost(FocusEvent e) {
					parseTextPaneInput();
					updateHitDifference((JTextPane) e.getSource());
			    }
			});
			
			setJTextPaneDimensions(hitCountTextPane, hitWidth, hitWidth, hitWidth, 15);
			setTextPaneAttributes(center, hitCountTextPane, "0", true, false, splitColor);

			splitCurrentHitsArrayList.add(hitCountTextPane);
			
			// Hit Difference
			JTextPane hitDifferenceTextPane = new JTextPane();
			hitDifferenceTextPane.setName(splits.get(i));
			hitDifferenceTextPane.addMouseListener(this);
			hitDifferenceTextPane.setName(splits.get(i));

			String hitDifference = getHitDifference("0", pBSplitArrayList.get(i));

			setJTextPaneDimensions(hitDifferenceTextPane, diffWidth, diffWidth, diffWidth, 15);
			setTextPaneAttributes(center, hitDifferenceTextPane, hitDifference, false, false, splitColor);
			
			splitHitDifferenceArrayList.add(hitDifferenceTextPane);

			// PB
			JTextPane pBTextPane = new JTextPane();
			pBTextPane.setName(splits.get(i));
			pBTextPane.addMouseListener(this);
			pBTextPane.setName(splits.get(i));

			String pBText = pBSplitArrayList.get(i) + "(" + pBCumulativeArrayList.get(i) + ")";
			
			setJTextPaneDimensions(pBTextPane, pbWidth, pbWidth, pbWidth, 15);
			setTextPaneAttributes(center, pBTextPane, pBText, false, false, splitColor);

			pBHitsArrayList.add(pBTextPane);
		}
		
		return;
	}
	
	//
	private void createHeaderAndTotalTextPanes(String totalHitsString, String totalDiffString, String pBString) {
		
		splitNameHeader = new JTextPane();
		currentHitsHeader = new JTextPane();
		hitDifferenceHeader = new JTextPane();
		pBHitsHeader = new JTextPane();
		
		totalTextPane  = new JTextPane();
		totalHitsTextPane = new JTextPane();
		totalDifferenceTextPane = new JTextPane();
		totalPBHitsTextPane = new JTextPane();
		
		// Create Alignments
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		//
		setJTextPaneDimensions(splitNameHeader, 100, splitWidth, splitWidth, 20);
		setJTextPaneDimensions(currentHitsHeader, hitWidth, hitWidth, hitWidth, 20);
		setJTextPaneDimensions(hitDifferenceHeader, diffWidth, diffWidth, diffWidth, 20);
		setJTextPaneDimensions(pBHitsHeader, pbWidth, pbWidth, pbWidth, 20);

		setJTextPaneDimensions(totalTextPane, 100, splitWidth, splitWidth, 20);
		setJTextPaneDimensions(totalHitsTextPane, hitWidth, hitWidth, hitWidth, 20);
		setJTextPaneDimensions(totalDifferenceTextPane, diffWidth, diffWidth, diffWidth, 20);
		setJTextPaneDimensions(totalPBHitsTextPane, pbWidth, pbWidth, pbWidth, 20);

		//
		setTextPaneAttributes(left, splitNameHeader, "Splits", false, true, Color.white);
		setTextPaneAttributes(center, currentHitsHeader, "Now", false, true, Color.white);
		setTextPaneAttributes(center, hitDifferenceHeader, "Diff", false, true, Color.white);
		setTextPaneAttributes(center, pBHitsHeader, "PB", false, true, Color.white);

		setTextPaneAttributes(left, totalTextPane, "Total", false, true, new Color(rgbHLGold));
		setTextPaneAttributes(center, totalHitsTextPane, totalHitsString, false, true, new Color(rgbHLGold));
		setTextPaneAttributes(center, totalDifferenceTextPane, totalDiffString, false, true, new Color(rgbHLGold));
		setTextPaneAttributes(center, totalPBHitsTextPane, pBString, false, true, new Color(rgbHLGold));

		return;
	}
	
	/************************************
	 * Swing Component Helper Functions *
	 ************************************/
	
	//
	private void setTextPaneAttributes(SimpleAttributeSet alignment, JTextPane pane, String paneText,
										boolean editable, boolean bold, Color foregroundColor) {
		
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attributeSet, foregroundColor);
		StyleConstants.setBackground(attributeSet, new Color(1.0f, 1.0f, 1.0f, 0.0f));	
		StyleConstants.setBold(attributeSet, bold);
		pane.setCharacterAttributes(attributeSet, true);
		
		pane.setText(paneText);
		pane.setOpaque(false);
		pane.setEditable(editable);
		
		StyledDocument paneDoc = pane.getStyledDocument();
		paneDoc.setParagraphAttributes(0, paneDoc.getLength(), alignment, false);
		
		return;
	}
	
	//
	private void setJTextPaneDimensions(JTextPane textPane, int minwidth, int prefwidth, int maxwidth, int height) {
		
		textPane.setMinimumSize(new Dimension(minwidth, height));
		textPane.setPreferredSize(new Dimension(prefwidth, height));
		textPane.setMaximumSize(new Dimension(maxwidth, height));
		
		return;
	}
	
	//
	private void setPanelDimensions(JPanel panel, Dimension dim) {
		
		panel.setMinimumSize(dim);
		panel.setPreferredSize(dim);
		panel.setMaximumSize(dim);
		
		return;
	}
	
	//
	private void updateHitDifference(JTextPane sourceHitPane) {
		
		if (Integer.parseInt(sourceHitPane.getText()) == focusedPaneHits) {
			return;
		}
		
		// Create Alignments
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		//
		String sourceName = sourceHitPane.getName();
		
		JTextPane diffPane = getDiffPane(sourceName);
		
		String pbString = "";
		JTextPane splitPane = null;
		JTextPane pbPane = null;
		
		
		for (int i = 0; i < pBHitsArrayList.size(); i++) {
			
			if (pBHitsArrayList.get(i).getName().equals(sourceName)) {
				splitPane = splitNameTextArrayList.get(i);
				pbPane = pBHitsArrayList.get(i);
				pbString = pBSplitArrayList.get(i);
			}
		}
		
		String changedDiffString = getHitDifference(sourceHitPane.getText(), pbString);
		int nowHits = Integer.parseInt(sourceHitPane.getText());
		int pbHits = Integer.parseInt(pbString);
		Color splitColor = new Color(determineHighlightColor(nowHits, pbHits));
		
		int currentTotalHits = 0;
		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
			currentTotalHits = currentTotalHits + Integer.parseInt(splitCurrentHitsArrayList.get(i).getText());
		}
		
		int pbTotalHits = 0;
		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
			pbTotalHits = pbTotalHits + Integer.parseInt(pBSplitArrayList.get(i));
		}
		
		// Make changes to UI
		
		Color pbcolor = new Color(determineHighlightColor(currentTotalHits, pbTotalHits));
		
		setTextPaneAttributes(center, totalHitsTextPane, String.valueOf(currentTotalHits), false, true, pbcolor);
		
		String changedTotalDiffString = getHitDifference(String.valueOf(currentTotalHits), String.valueOf(pbTotalHits));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, changedTotalDiffString, false, true, pbcolor);
		
		if (!totalTextPane.getForeground().equals(pbcolor)) {
			setTextPaneAttributes(left, totalTextPane, totalTextPane.getText(), false, true, pbcolor);
			setTextPaneAttributes(center, totalPBHitsTextPane, totalPBHitsTextPane.getText(), false, true, pbcolor);		
		}		
		
		setTextPaneAttributes(center, diffPane, changedDiffString, false, false, splitColor);
		
		if (!splitPane.getForeground().equals(splitColor)) {
			setTextPaneAttributes(left, splitPane, splitPane.getText(), true, false, splitColor);
			setTextPaneAttributes(center, sourceHitPane, sourceHitPane.getText(), true, false, splitColor);
			setTextPaneAttributes(center, pbPane, pbPane.getText(), false, false, splitColor);
		}
		
		return;
	}
	
	//
	private JTextPane getDiffPane(String name) {
		
		for (int i = 0; i < splitHitDifferenceArrayList.size(); i++) {
			
			if (splitHitDifferenceArrayList.get(i).getName().equals(name)) {
				return splitHitDifferenceArrayList.get(i);
			}
		}
		
		return null;
	}
	
	//
	private String getHitDifference(String hits, String splitPB) {
		
		int diff = Integer.parseInt(hits) - Integer.parseInt(splitPB);
		
		return String.valueOf(diff);
	}
	
	//
	private void makePBCumulativeArrayList() {
		
		int cumulativePB = 0;
		
		pBCumulativeArrayList.clear();
		
		for (int i = 0; i < pBSplitArrayList.size(); i++) {
			
			cumulativePB = cumulativePB + Integer.parseInt(pBSplitArrayList.get(i));
			
			pBCumulativeArrayList.add(String.valueOf(cumulativePB));
		}
		
		return;
	}
	
	//
	private void parseTextPaneInput() {
		// TODO check if user inputs invalid string
	}
	
	/*****************************
	 * Layout Creation Functions *
	 *****************************/
	
	//
	private GroupLayout createSplitsGroupLayout(JPanel panel) {
		
		GroupLayout layout = new GroupLayout(panel);

		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);
		
		/*
		 *  Horizontal Alignment
		 */
		ParallelGroup topLevelParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		SequentialGroup rowSequentialGroup = layout.createSequentialGroup();
		
		//FIRST COLUMN
		ParallelGroup firstColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		// Iterate over the ArrayList to create horizontal layout groupings
		for (int i = 0; i < splitNameTextArrayList.size(); i++) {
							
			firstColumnParallelGroup.addComponent(splitNameTextArrayList.get(i));			
		}
		
		rowSequentialGroup.addGroup(firstColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		// SECOND COLUMN
		ParallelGroup secondColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);

		// Iterate over the ArrayList to create next parallel grouping
		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
							
			secondColumnParallelGroup.addComponent(splitCurrentHitsArrayList.get(i));			
		}

		rowSequentialGroup.addGroup(secondColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		// THIRD COLUMN
		ParallelGroup thirdColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		// Iterate over the ArrayList to create next parallel grouping
		for (int i = 0; i < splitHitDifferenceArrayList.size(); i++) {
							
			thirdColumnParallelGroup.addComponent(splitHitDifferenceArrayList.get(i));			
		}

		rowSequentialGroup.addGroup(thirdColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		// FOURTH COLUMN
		ParallelGroup fourthColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);

		// Iterate over the ArrayList to create next parallel grouping
		for (int i = 0; i < pBHitsArrayList.size(); i++) {
							
			fourthColumnParallelGroup.addComponent(pBHitsArrayList.get(i));			
		}

		rowSequentialGroup.addGroup(fourthColumnParallelGroup);
		
		topLevelParallelGroup.addGroup(rowSequentialGroup);
		
		// Finalize Horizontal Layout
		layout.setHorizontalGroup(topLevelParallelGroup);
		
		/*
		 *  Vertical Alignment
		 */
		SequentialGroup columnSequentialGroup = layout.createSequentialGroup();
		
		// Iterate over the ArrayLists to create vertical layout groupings
		for (int i = 0; i < splitNameTextArrayList.size(); i++) {
			
			ParallelGroup rowParallelGroup = layout.createParallelGroup(Alignment.CENTER);
			
			rowParallelGroup.addComponent(splitNameTextArrayList.get(i));
			rowParallelGroup.addComponent(splitCurrentHitsArrayList.get(i));
			rowParallelGroup.addComponent(splitHitDifferenceArrayList.get(i));
			rowParallelGroup.addComponent(pBHitsArrayList.get(i));
						
			columnSequentialGroup.addGroup(rowParallelGroup);
			columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 2, 2);
		}
		
		// Finalize Vertical Layout
		layout.setVerticalGroup(columnSequentialGroup);
		
		return layout;
	}
	
	//
	private GroupLayout createTopLeveGroupLayout(JPanel panel) {
		
		GroupLayout layout = new GroupLayout(panel);

		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);
		
		/*
		 *  Horizontal Alignment
		 */
		ParallelGroup topLevelParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		// Button Group
		SequentialGroup buttonSequentialGroup = layout.createSequentialGroup();
		
		buttonSequentialGroup.addComponent(resetRunButton);
		buttonSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 30, 50);
		buttonSequentialGroup.addComponent(setPBButton);

		// Header Group
		SequentialGroup headerSequentialGroup = layout.createSequentialGroup();
		
		headerSequentialGroup.addComponent(splitNameHeader);
		headerSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		headerSequentialGroup.addComponent(currentHitsHeader);
		headerSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		headerSequentialGroup.addComponent(hitDifferenceHeader);
		headerSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		headerSequentialGroup.addComponent(pBHitsHeader);

		// Totals Group
		SequentialGroup totalSequentialGroup = layout.createSequentialGroup();
		
		totalSequentialGroup.addComponent(totalTextPane);
		totalSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		totalSequentialGroup.addComponent(totalHitsTextPane);
		totalSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		totalSequentialGroup.addComponent(totalDifferenceTextPane);
		totalSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		totalSequentialGroup.addComponent(totalPBHitsTextPane);

		// Add all Groups and Components to top level
		topLevelParallelGroup.addGroup(buttonSequentialGroup);
		topLevelParallelGroup.addGroup(headerSequentialGroup);
		topLevelParallelGroup.addComponent(scrollPane);
		topLevelParallelGroup.addGroup(totalSequentialGroup);
		
		// Finalize Horizontal Layout
		layout.setHorizontalGroup(topLevelParallelGroup);
		
		/*
		 *  Vertical Alignment
		 */
		SequentialGroup columnSequentialGroup = layout.createSequentialGroup();
		
		// Button Group
		ParallelGroup buttonParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		buttonParallelGroup.addComponent(resetRunButton);
		buttonParallelGroup.addComponent(setPBButton);
		
		// Header Group
		ParallelGroup headerParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		headerParallelGroup.addComponent(splitNameHeader);
		headerParallelGroup.addComponent(currentHitsHeader);
		headerParallelGroup.addComponent(hitDifferenceHeader);
		headerParallelGroup.addComponent(pBHitsHeader);
		
		// Totals Group
		ParallelGroup totalParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		totalParallelGroup.addComponent(totalTextPane);
		totalParallelGroup.addComponent(totalHitsTextPane);
		totalParallelGroup.addComponent(totalDifferenceTextPane);
		totalParallelGroup.addComponent(totalPBHitsTextPane);
		
		
		// Add all Groups and Components to top level
		columnSequentialGroup.addGroup(buttonParallelGroup);
		columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 30, 30);
		columnSequentialGroup.addGroup(headerParallelGroup);
		columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 5, 5);
		columnSequentialGroup.addComponent(scrollPane);
		columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 15, 15);
		columnSequentialGroup.addGroup(totalParallelGroup);
		
		// Finalize Vertical Layout
		layout.setVerticalGroup(columnSequentialGroup);
		
		return layout;
	}

	/******************************************
	 *  Button and Menu Item action functions *
	 *  and helper functions				  *
	 ******************************************/

	//
	private void resetRun() {
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		setTextPaneAttributes(center, totalHitsTextPane, "0", false, true, Color.white);
		
		String diffTotalText = getHitDifference("0", pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, diffTotalText, false, true, Color.white);
		
		for (int i = splitCurrentHitsArrayList.size() - 1; i >= 0; i--) {
			
			focusedPaneHits = -1;

			splitCurrentHitsArrayList.get(i).setText("0");
			updateHitDifference(splitCurrentHitsArrayList.get(i));
		}
		
		currentSplit = 0;
		highlightRow(splitNameTextArrayList.get(0), null);
		
		return;
	}
	
	//
	private void setPB() {
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		pBSplitArrayList.clear();
		focusedPaneHits = -1;

		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
			
			pBSplitArrayList.add(splitCurrentHitsArrayList.get(i).getText());
		}
		
		makePBCumulativeArrayList();
		
		for (int i = 0; i < pBHitsArrayList.size(); i++) {
			
			String pBText = pBSplitArrayList.get(i) + "(" + pBCumulativeArrayList.get(i) + ")";
			
			setTextPaneAttributes(center, pBHitsArrayList.get(i), pBText, false, false, Color.white);
		}
		
		String pBTotalText = pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1);
		
		setTextPaneAttributes(center, totalPBHitsTextPane, pBTotalText, false, true, Color.white);
		
		resetRun();
		
		return;
	}
	
	//
	private void incrementCurrentSplitHit() {
		
		JTextPane currentHitPane = splitCurrentHitsArrayList.get(currentSplit);
		String updatedHitString = String.valueOf(Integer.parseInt(currentHitPane.getText()) + 1);
		currentHitPane.setText(updatedHitString);
				
		focusedPaneHits = -1;
		updateHitDifference(splitCurrentHitsArrayList.get(currentSplit));
				
		return;
	}
	
	//
	private void decrementCurrentSplitHit() {
		
		JTextPane currentHitPane = splitCurrentHitsArrayList.get(currentSplit);
		int updatedHitInt = Integer.parseInt(currentHitPane.getText()) - 1;
		
		if (updatedHitInt < 0) {
			updatedHitInt = 0;
		}
		
		String updatedHitString = String.valueOf(updatedHitInt);
		currentHitPane.setText(updatedHitString);
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		focusedPaneHits = -1;
		updateHitDifference(splitCurrentHitsArrayList.get(currentSplit));
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		return;
	}
	
	//
	private void goToNextSplit(Callback callback, int scrollpos) {
		
		Callback highlightCallback = new Callback() {
			public void nextsplitcallback(int pos) {
				return;
			}
			
			public void highlightcallback() {
				callback.nextsplitcallback(scrollpos);
				return;
			}
		};
		
		currentSplit = currentSplit + 1;

		if (currentSplit < splitNameTextArrayList.size()) {

			highlightRow(splitNameTextArrayList.get(currentSplit), highlightCallback);
		}
		else {
			currentSplit = splitNameTextArrayList.size() - 1;
		}
		
		return;
	}
	
	//
	private void findCurrentSplit(JComponent source) {
		
		for (int i = 0; i < splitNameTextArrayList.size(); i++) {

			if (splitNameTextArrayList.get(i).getName() == source.getName()) {
				currentSplit = i;
				break;
			}
		}
		
		return;
	}
	
	//
	private void highlightRow(JComponent source, Callback callback) {
		
		// Create Alignments
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		boolean atCurrentSplit = false;
		int currentSplitpos = -1;
				
		for (int i = splitNameTextArrayList.size() - 1; i >= 0; i--) {

			JTextPane splitPane = splitNameTextArrayList.get(i);
			JTextPane hitPane = splitCurrentHitsArrayList.get(i);
			JTextPane diffPane = splitHitDifferenceArrayList.get(i);
			JTextPane pbPane = pBHitsArrayList.get(i);
			
			if (splitNameTextArrayList.get(i).getName() == source.getName()) {
				
				currentSplitpos = i;
				
				atCurrentSplit = true;
			}
			else if (atCurrentSplit) {
				
				int hits = Integer.parseInt(hitPane.getText());
				int pbhits = Integer.parseInt(pBSplitArrayList.get(i));
				Color splitcolor = new Color(determineHighlightColor(hits, pbhits));

				if (!splitPane.getForeground().equals(splitcolor)) {
					setTextPaneAttributes(left, splitPane, splitPane.getText(), true, false, splitcolor);
					setTextPaneAttributes(center, hitPane, hitPane.getText(), true, false, splitcolor);
					setTextPaneAttributes(center, diffPane, diffPane.getText(), false, false, splitcolor);
					setTextPaneAttributes(center, pbPane, pbPane.getText(), false, false, splitcolor);
				}
			}
			else {
				
				if (!splitPane.getForeground().equals(Color.white)) {
					setTextPaneAttributes(left, splitPane, splitPane.getText(), true, false, Color.white);
					setTextPaneAttributes(center, hitPane, hitPane.getText(), true, false, Color.white);
					setTextPaneAttributes(center, diffPane, diffPane.getText(), false, false, Color.white);
					setTextPaneAttributes(center, pbPane, pbPane.getText(), false, false, Color.white);
				}
				
			}
		}
		
		// Do this here to keep current split on the screen
		Color splitcolor = new Color(rgbHLBlue);

		JTextPane currentsplitPane = splitNameTextArrayList.get(currentSplitpos);
		JTextPane currenthitPane = splitCurrentHitsArrayList.get(currentSplitpos);
		JTextPane currentdiffPane = splitHitDifferenceArrayList.get(currentSplitpos);
		JTextPane currentpbPane = pBHitsArrayList.get(currentSplitpos);
		
		if (!currentsplitPane.getForeground().equals(splitcolor)) {
			setTextPaneAttributes(left, currentsplitPane, currentsplitPane.getText(), true, false, splitcolor);
			setTextPaneAttributes(center, currenthitPane, currenthitPane.getText(), true, false, splitcolor);
			setTextPaneAttributes(center, currentdiffPane, currentdiffPane.getText(), false, false, splitcolor);
			setTextPaneAttributes(center, currentpbPane, currentpbPane.getText(), false, false, splitcolor);
		}
		
		if (currentSplitpos < splitNameTextArrayList.size() - 1) {
			JTextPane nextsplitPane = splitNameTextArrayList.get(currentSplitpos + 1);
			setTextPaneAttributes(left, nextsplitPane, nextsplitPane.getText(), true, false, Color.white);
		}

		// Callback
		if (callback != null) {
			callback.highlightcallback();
		}
		
		return;
	}
	
	//
	private int determineHighlightColor(int nowhits, int pbhits) {
		
		if (nowhits == 0) {
			return rgbHLGold;
		}
		else if (nowhits <= pbhits) {
			return rgbHLGreen;
		}
		else {
			return rgbHLRed;
		}
		
	}
	
	/***************************
	 * Save and Load Functions *
	 ***************************/
	
	//
	private void loadProgram() {
		
		System.out.println("Load savefile: " + userSaveFilePathString);

		File saveFile = new File(userSaveFilePathString);
		
		try (BufferedReader br = new BufferedReader(new FileReader(saveFile))) {
		    String line;
		    
		    while ((line = br.readLine()) != null) {
		    	// process the line.
		    	pBSplitArrayList.add(line);
		    }
		}
		catch (Exception e) {
			System.out.println("Excepton Occured: " + e.toString());
			hasSavedPB = false;
		}
		
		return;
	}
	
	//
	private void saveProgram() {
		
		File userSave = new File(userSaveFilePathString);
		
		if (!userSave.exists()) {
			try {
				File directory = new File(userSave.getParent());
				if (!directory.exists()) {
					directory.mkdirs();
				}
				userSave.createNewFile();
			} catch (IOException e) {
				System.out.println("Excepton Occured: " + e.toString());
			}
		}
		
		try {
			FileWriter saveWriter;
			saveWriter = new FileWriter(userSave.getAbsoluteFile(), false);
 
			// Writes text to a character-output stream
			BufferedWriter bufferWriter = new BufferedWriter(saveWriter);
			for (int i = 0; i < pBSplitArrayList.size(); i++) {

				bufferWriter.write(pBSplitArrayList.get(i));
				bufferWriter.newLine();
			}
			
			// Close FileWriter
			bufferWriter.close();
 
		} catch (IOException e) {
			System.out.println("Excepton Occured: " + e.toString());
		}
		
		return;
	}
	
	/************
	 *** Main ***
	 ************/
	
	public static void main(String[] args) {

		new HitCounter();
	}

	/************************
	 * Overridden Functions *
	 ************************/
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == saveMenuItem) {
			saveProgram();
		}
		else if (e.getSource() == incHitMenuItem) {
			incrementCurrentSplitHit();
		}
		else if (e.getSource() == decHitMenuItem) {
			decrementCurrentSplitHit();
		}
		else if (e.getSource() == nextSplitMenuItem) {
			BoundedRangeModel scrollmodel = scrollPane.getVerticalScrollBar().getModel();
			int scrollpos = scrollmodel.getValue();

			Callback fixViewCallback = new Callback() {
				public void nextsplitcallback(int pos) {

					scrollPane.getViewport().setViewPosition(new Point(0, pos));
					return;
				}
				
				public void highlightcallback() {
					return;
				}
			};

			goToNextSplit(fixViewCallback, scrollpos);
		}
		else if (e.getSource() == resetRunButton) {
			resetRun();
		}
		else if (e.getSource() == setPBButton) {
			setPB();
		}
		
		return;
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (e.getSource() instanceof JComponent) {
			
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					JComponent source = (JComponent) e.getSource();

					findCurrentSplit(source);
					highlightRow(source, null);
				}
			};
			
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

}
