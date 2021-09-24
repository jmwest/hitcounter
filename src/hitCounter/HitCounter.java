package hitCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
	
	private ArrayList<SplitRow> splitRowArrayList;
	
	private JTextPane totalTextPane;
	private JTextPane totalHitsTextPane;
	private JTextPane totalDifferenceTextPane;
	private JTextPane totalPBHitsTextPane;
	
	private ArrayList<String> splitNames;
	private ArrayList<String> pBSplitArrayList;
	private ArrayList<String> pBCumulativeArrayList;
	
	private int pBTotalHits = 0;
	private int currentSplit = 0;
	
	SimpleAttributeSet left;
	SimpleAttributeSet center;
	
	/****************************
	 * Public Class Constructor *
	 ****************************/
	
	public HitCounter() {

		// Create Alignments
		left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
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
		
		splitRowArrayList = new ArrayList<SplitRow>();
		
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
		createSplitRows(splitNames);
		
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
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Hit Counter");
		frame.getContentPane().add(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
	/**********************
	 * Callback Functions *
	 **********************/
	
	//
	public interface Callback {
		void nextsplitcallback(int pos);
		void highlightcallback();
	};

	/********************
	 * Public Functions *
	 ********************/
	
	//
	public int determineHighlightColor(SplitRow row) {
		
		int rowNum = 0;
		for (int i = 0; i < splitRowArrayList.size(); i++) {
			
			if (row.getName().equals(splitRowArrayList.get(i).getName())) {
				rowNum = i;
			}
		}
		
		int nowhits = Integer.valueOf(row.getHits());
		int pbhits = Integer.valueOf(row.getPB());
		
		if (rowNum == currentSplit) {
			return rgbHLBlue;
		}
		else if (nowhits == 0) {
			return rgbHLGold;
		}
		else if (nowhits <= pbhits) {
			return rgbHLGreen;
		}
		else {
			return rgbHLRed;
		}
		
	}
	
	//
	public int determineTotalHighlightColor(int nowhits, int pbhits) {
		
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
	
	//
	public void highlightRows(JComponent source, Callback callback) {
		
		// Create Alignments
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		boolean atCurrentSplit = false;
		int currentSplitpos = -1;
				
		for (int i = splitRowArrayList.size() - 1; i >= 0; i--) {

			SplitRow row = splitRowArrayList.get(i);
			
			if (i == currentSplit) {
				
				currentSplitpos = i;
				
				atCurrentSplit = true;
			}
			else if (atCurrentSplit) {
				
				Color splitcolor = new Color(determineHighlightColor(splitRowArrayList.get(i)));

				if (!row.getColor().equals(splitcolor)) {
					row.highlightRow(splitcolor, pBCumulativeArrayList.get(i));
				}
			}
			else {
				
				if (!row.getColor().equals(Color.white)) {
					row.highlightRow(Color.white, pBCumulativeArrayList.get(i));
				}
				
			}
		}
		
		// Do this here to keep current split on the screen
		Color splitcolor = new Color(rgbHLBlue);

		SplitRow currentRow = splitRowArrayList.get(currentSplitpos);
		
		if (!currentRow.getColor().equals(splitcolor)) {
			currentRow.highlightRow(splitcolor, pBCumulativeArrayList.get(currentSplitpos));
		}
		
		if (currentSplitpos < splitRowArrayList.size() - 1) {
			SplitRow nextsplitRow = splitRowArrayList.get(currentSplitpos + 1);
			nextsplitRow.highlightRow(Color.white, pBCumulativeArrayList.get(currentSplitpos + 1));
		}

		// Callback
		if (callback != null) {
			callback.highlightcallback();
		}
		
		return;
	}

	/*******************************
	 * Getter and Setter Functions *
	 *******************************/
	
	//
	public void setCurrentSplitFromRow(SplitRow currentRow) {
		
		for (int i = 0; i < splitRowArrayList.size(); i++) {
			
			if (currentRow.getName().equals(splitRowArrayList.get(i).getName())) {
				currentSplit = i;
			}
		}
		
		return;
	}
	
	public void setCurrentSplit(int current) {
		
		currentSplit = current;
		
		return;
	}
	
	public int getCurrentSplit() {
		return currentSplit;
	}
	
	/****************************
	 * Load Default ZOOT Splits *
	 ****************************/
	
	//
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
	private void createSplitRows(ArrayList<String> splits) {
		
		Color splitColor = new Color(rgbHLBlue);
		
		for (int i = 0; i < splits.size(); i++) {
			
			if (i == 1) {
				splitColor = Color.white;
			}
			
			// Split Row
			String hitDiff = getHitDifference("0", pBSplitArrayList.get(i));
			
			SplitRow splitrow = new SplitRow(this, splits.get(i), hitDiff, pBSplitArrayList.get(i),
											 pBCumulativeArrayList.get(i), splitColor);
			
			splitrow.addMouseListener(this);
			splitrow.setName(splits.get(i));

			splitRowArrayList.add(splitrow);
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
	public void updateHitDifferences(SplitRow sourceRow) {
		
		//
		int currentTotalHits = 0;
		int pbTotalHits = Integer.parseInt(pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1));
		
		for (int i = 0; i < splitRowArrayList.size(); i++) {
			currentTotalHits = currentTotalHits + Integer.parseInt(splitRowArrayList.get(i).getHits());
		}
		
		// Make changes to UI
		Color pbcolor = new Color(determineTotalHighlightColor(currentTotalHits, pbTotalHits));
		
		setTextPaneAttributes(center, totalHitsTextPane, String.valueOf(currentTotalHits), false, true, pbcolor);
		
		String changedTotalDiffString = getHitDifference(String.valueOf(currentTotalHits), String.valueOf(pbTotalHits));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, changedTotalDiffString, false, true, pbcolor);
		
		setTextPaneAttributes(left, totalTextPane, totalTextPane.getText(), false, true, pbcolor);
		setTextPaneAttributes(center, totalPBHitsTextPane, totalPBHitsTextPane.getText(), false, true, pbcolor);	
		
		return;
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
		ParallelGroup rowParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
	
		// Iterate over the ArrayList to create horizontal layout groupings
		for (int i = 0; i < splitRowArrayList.size(); i++) {
							
			rowParallelGroup.addComponent(splitRowArrayList.get(i));			
		}

		// Finalize Horizontal Layout
		layout.setHorizontalGroup(rowParallelGroup);
		
		/*
		 *  Vertical Alignment
		 */
		SequentialGroup columnSequentialGroup = layout.createSequentialGroup();
		
		// Iterate over the ArrayLists to create vertical layout groupings
		for (int i = 0; i < splitRowArrayList.size(); i++) {
				
			columnSequentialGroup.addComponent(splitRowArrayList.get(i));
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
		
		Color pbcolor = new Color(rgbHLGold);

		setTextPaneAttributes(center, totalHitsTextPane, "0", false, true, pbcolor);
		
		String diffTotalText = getHitDifference("0", pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, diffTotalText, false, true, pbcolor);
		setTextPaneAttributes(left, totalTextPane, totalTextPane.getText(), false, true, pbcolor);
		setTextPaneAttributes(center, totalPBHitsTextPane, totalPBHitsTextPane.getText(), false, true, pbcolor);	
		
		for (int i = splitRowArrayList.size() - 1; i >= 0; i--) {
			
			splitRowArrayList.get(i).resetRowHits();
		}
		
		currentSplit = 0;
		highlightRows(splitRowArrayList.get(0), null);
		
		return;
	}

	//
	private void setPB() {
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		pBSplitArrayList.clear();

		for (int i = 0; i < splitRowArrayList.size(); i++) {
			
			pBSplitArrayList.add(splitRowArrayList.get(i).getPB());
		}
		
		makePBCumulativeArrayList();
		
		for (int i = 0; i < splitRowArrayList.size(); i++) {
			
			String pBText = pBSplitArrayList.get(i) + "(" + pBCumulativeArrayList.get(i) + ")";
			
			splitRowArrayList.get(i).setPB(pBSplitArrayList.get(i), pBText);
		}
		
		String pBTotalText = pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1);
		
		setTextPaneAttributes(center, totalPBHitsTextPane, pBTotalText, false, true, Color.white);
		
		resetRun();
		
		return;
	}
	
	//
	private void incrementCurrentSplitHit() {
		
		SplitRow currentRow = splitRowArrayList.get(currentSplit);
		currentRow.setHits(currentRow.getHitsFromPane());

		String updatedHitString = String.valueOf(Integer.parseInt(currentRow.getHits()) + 1);
		currentRow.setHits(updatedHitString);
		
		updateHitDifferences(splitRowArrayList.get(currentSplit));
		
		menuBar.requestFocusInWindow();
		
		return;
	}
	
	//
	private void decrementCurrentSplitHit() {
		
		SplitRow currentRow = splitRowArrayList.get(currentSplit);
		currentRow.setHits(currentRow.getHitsFromPane());
		
		int updatedHitInt = Integer.parseInt(currentRow.getHits()) - 1;
		
		if (updatedHitInt < 0) {
			updatedHitInt = 0;
		}
		
		String updatedHitString = String.valueOf(updatedHitInt);
		currentRow.setHits(updatedHitString);
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		updateHitDifferences(splitRowArrayList.get(currentSplit));
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		menuBar.requestFocusInWindow();

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
		
		splitRowArrayList.get(currentSplit).setHits(splitRowArrayList.get(currentSplit).getHitsFromPane());
		
		currentSplit = currentSplit + 1;

		if (currentSplit < splitRowArrayList.size()) {

			highlightRows(splitRowArrayList.get(currentSplit), highlightCallback);
		}
		else {
			currentSplit = splitRowArrayList.size() - 1;
		}
		
		menuBar.requestFocusInWindow();
		
		return;
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

					highlightRows(source, null);
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
