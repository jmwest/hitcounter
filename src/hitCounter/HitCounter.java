package hitCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.LayoutStyle;
import javax.swing.OverlayLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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

	final static int splitPanelWidth = splitWidth + hitWidth + diffWidth + pbWidth + 10*5;
	final static int scrollPaneHeight = 400;
	private int splitPanelHeight = 0;
	
    /*****************************
     * Declare private variables *
     *****************************/
    
    // File paths
    final static String filePathString = System.getProperty("user.dir") + System.getProperty("file.separator") // -> Next Line
											+ "resources" + System.getProperty("file.separator");
    final static String userSaveFilePathString = filePathString + "usersave.txt";
    final static String titleFilePathString = filePathString + "upload_title.txt";
    final static String defaultBackgroundImageString = "The-Legend-of-Zelda-Ocarina-of-Time-Title-Screen.png";
	
    private boolean hasSavedPB = true;
    
    // MenuBar Items
    private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu editMenu;
	private JMenu actionMenu;
	
	//		File Menu Items
	private JMenuItem saveMenuItem;
	private JMenu loadMenu;
	private ArrayList<JMenuItem> loadMenuItems;
	
	//		Edit Menu Items
	private JMenuItem addBackgroundItem;

	//		Action Menu Items
	private JMenuItem incHitMenuItem;
	private JMenuItem decHitMenuItem;
	private JMenuItem nextSplitMenuItem;
	
	// View Items
	private JFrame frame;
	private JPanel panel;
	private JPanel comboPanel;
	private JPanel splitsPanel;
	private JLabel transparencyLabel;
	private JLabel backgroundLabel;
	private JScrollPane scrollPane;
	
	// Buttons
	private JButton resetRunButton;
	private JButton setPBButton;
	private JButton uploadNewTitleButton;
	
	// Header Panes
	private JTextPane splitNameHeader;
	private JTextPane currentHitsHeader;
	private JTextPane hitDifferenceHeader;
	private JTextPane pBHitsHeader;
	
	// Total Panes
	private JTextPane totalTextPane;
	private JTextPane totalHitsTextPane;
	private JTextPane totalDifferenceTextPane;
	private JTextPane totalPBHitsTextPane;
	
	// Split Related Variables
	private ArrayList<String> splitTitles;
	private int currentTitle = 0;
	private ArrayList<ArrayList<SplitRow>> splitRowArrayList;
	
	private ArrayList<ArrayList<String>> splitNames;
	private ArrayList<ArrayList<String>> pBSplitArrayList;
	private ArrayList<ArrayList<String>> pBCumulativeArrayList;
	
	private ArrayList<Integer> currentSplits;
	
	private ArrayList<BufferedImage> backgroundImages;
	private ArrayList<String> backgroundImgFileNames;
	
	// Attribute Sets
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
		setUpMenuBar();
		
		// View setup
		setUpViewComponents();

		// Instantiate ArrayLists
		splitTitles = new ArrayList<String>();
		splitRowArrayList = new ArrayList<ArrayList<SplitRow>>();
		
		splitNames = new ArrayList<ArrayList<String>>();
		pBSplitArrayList = new ArrayList<ArrayList<String>>();
		pBCumulativeArrayList = new ArrayList<ArrayList<String>>();
		
		currentSplits = new ArrayList<Integer>();
		
		backgroundImages = new ArrayList<BufferedImage>();
		backgroundImgFileNames = new ArrayList<String>();
		
		// Load user save
		loadProgram();
		
		if (!hasSavedPB) {
			
			splitNames.add(new ArrayList<String>());
    		pBSplitArrayList.add(new ArrayList<String>());
    		pBCumulativeArrayList.add(new ArrayList<String>());
    		
    		currentSplits.add(0);
    		backgroundImages.add(openResourceImageFile(defaultBackgroundImageString));
    		backgroundImgFileNames.add(defaultBackgroundImageString);
    		
			loadZOOTSplits();
						
			for (int i = 0; i < splitNames.get(currentTitle).size(); i++) {
				
				pBSplitArrayList.get(currentTitle).add("0");
			}
		}
		
		// Add Load Menu Items
		setUpLoadMenuItems();
		
		// Need to load PB info before here
		for (int i = 0; i < splitTitles.size(); i++) {
			makePBCumulativeArrayList(pBCumulativeArrayList.get(i), pBSplitArrayList.get(i));
		}
		
		// Need Split names & PB Info before here.
		setUpSplitRows();
		
		// Set up header and total panes
		setUpHeaderAndTotalPanes(pBCumulativeArrayList.get(currentTitle));

		// Set Panels
		setPanels();

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
	public int determineHighlightColor(SplitRow row, ArrayList<SplitRow> splitRowArray) {
		
		int rowNum = 0;
		for (int i = 0; i < splitRowArray.size(); i++) {
			
			if (row.getName().equals(splitRowArray.get(i).getName())) {
				rowNum = i;
			}
		}
		
		int nowhits = Integer.valueOf(row.getHits());
		int pbhits = Integer.valueOf(row.getPB());
		
		if (rowNum == currentSplits.get(currentTitle)) {
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
	public void highlightRows(JComponent source, ArrayList<SplitRow> splitrowarray, // -> Next Line
							  ArrayList<String> pbcumulativelist, Callback callback) {
		
		// Create Alignments
		SimpleAttributeSet left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		boolean atCurrentSplit = false;
		int currentSplitpos = -1;
				
		for (int i = splitrowarray.size() - 1; i >= 0; i--) {

			SplitRow row = splitrowarray.get(i);
			
			if (i == currentSplits.get(currentTitle)) {
				
				currentSplitpos = i;
				
				atCurrentSplit = true;
			}
			else if (atCurrentSplit) {
				
				Color splitcolor = new Color(determineHighlightColor(splitrowarray.get(i), splitrowarray));

				if (!row.getColor().equals(splitcolor)) {
					row.highlightRow(splitcolor, pbcumulativelist.get(i));
				}
			}
			else {
				
				if (!row.getColor().equals(Color.white)) {
					row.highlightRow(Color.white, pbcumulativelist.get(i));
				}
				
			}
		}
		
		// Do this here to keep current split on the screen
		Color splitcolor = new Color(rgbHLBlue);

		SplitRow currentRow = splitrowarray.get(currentSplitpos);
		
		if (!currentRow.getColor().equals(splitcolor)) {
			currentRow.highlightRow(splitcolor, pbcumulativelist.get(currentSplitpos));
		}
		
		if (currentSplitpos < splitrowarray.size() - 1) {
			SplitRow nextsplitRow = splitrowarray.get(currentSplitpos + 1);
			nextsplitRow.highlightRow(Color.white, pbcumulativelist.get(currentSplitpos + 1));
		}

		// Callback
		if (callback != null) {
			callback.highlightcallback();
		}
		
		return;
	}

	//
	public void updateHitDifferences(SplitRow sourceRow, ArrayList<SplitRow> splitrowarray, // -> Next Line
			  						 ArrayList<String> pbcumulativelist) {
		
		//
		int currentTotalHits = 0;
		int pbTotalHits = Integer.parseInt(pbcumulativelist.get(pbcumulativelist.size() - 1));
		
		for (int i = 0; i < splitrowarray.size(); i++) {
			currentTotalHits = currentTotalHits + Integer.parseInt(splitrowarray.get(i).getHits());
		}
		
		// Make changes to UI
		Color pbcolor = new Color(determineTotalHighlightColor(currentTotalHits, pbTotalHits));
		
		setTextPaneAttributes(center, totalHitsTextPane, String.valueOf(currentTotalHits), false, true, pbcolor);
		
		String changedTotalDiffString = getHitDifference(String.valueOf(currentTotalHits), String.valueOf(pbTotalHits));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, changedTotalDiffString, false, true, pbcolor);
		
		setTextPaneAttributes(left, totalTextPane, totalTextPane.getText(), false, true, pbcolor);
		setTextPaneAttributes(center, totalPBHitsTextPane, String.valueOf(pbTotalHits), false, true, pbcolor);	
		
		return;
	}
	
	/*******************************
	 * Getter and Setter Functions *
	 *******************************/
	
	//
	public void setCurrentSplitFromRow(SplitRow currentrow, ArrayList<SplitRow> currentsplitrow) {
		
		for (int i = 0; i < currentsplitrow.size(); i++) {
			
			if (currentrow.getName().equals(currentsplitrow.get(i).getName())) {
				currentSplits.set(currentTitle, i);
			}
		}
		
		return;
	}
	
	public void setCurrentSplit(int current) {
		
		currentSplits.set(currentTitle, current);
		
		return;
	}
	
	public int getCurrentSplit() {
		return currentSplits.get(currentTitle);
	}
	
	/****************************
	 * Load Default ZOOT Splits *
	 ****************************/
	
	//
	private void loadZOOTSplits() {
		
		splitTitles.add("Ocarina of Time");
		
		splitNames.get(currentTitle).add("Ghoma");
		splitNames.get(currentTitle).add("Zeldo");
		splitNames.get(currentTitle).add("Saria");
		splitNames.get(currentTitle).add("Lizolfos 1");
		splitNames.get(currentTitle).add("Lizolfos 2");

		splitNames.get(currentTitle).add("Dodongo");
		splitNames.get(currentTitle).add("Boomerang");
		splitNames.get(currentTitle).add("Big Octo");
		splitNames.get(currentTitle).add("Baranade");
		splitNames.get(currentTitle).add("Stalfos 1");

		splitNames.get(currentTitle).add("Stalfos 2");
		splitNames.get(currentTitle).add("Phantom Ganon");
		splitNames.get(currentTitle).add("Nut Sack 1");
		splitNames.get(currentTitle).add("Vulvagina");
		splitNames.get(currentTitle).add("Lens of Truth");

		splitNames.get(currentTitle).add("Iron Boots");
		splitNames.get(currentTitle).add("Dark Link");
		splitNames.get(currentTitle).add("Morpha");
		splitNames.get(currentTitle).add("Hover Boots");
		splitNames.get(currentTitle).add("Bongos");

		splitNames.get(currentTitle).add("Gerudo Card");
		splitNames.get(currentTitle).add("Requiem");
		splitNames.get(currentTitle).add("Silver Gauntlets");
		splitNames.get(currentTitle).add("Nabooru");
		splitNames.get(currentTitle).add("Twinrova");

		splitNames.get(currentTitle).add("Forest Trial");
		splitNames.get(currentTitle).add("Water Trial");
		splitNames.get(currentTitle).add("Shadow Trial");
		splitNames.get(currentTitle).add("Fire Trial");
		splitNames.get(currentTitle).add("Light Trial");

		splitNames.get(currentTitle).add("Spirit Trial");
		splitNames.get(currentTitle).add("Ganon Dinalfos");
		splitNames.get(currentTitle).add("Ganon Stalfos");
		splitNames.get(currentTitle).add("B&W Knuckles");
		splitNames.get(currentTitle).add("Ganondorf");

		splitNames.get(currentTitle).add("Collapse");
		splitNames.get(currentTitle).add("Ganon");

		return;
	}
	
	/******************************************
	 * Functions to create UI variable arrays *
	 ******************************************/
	
	//
	private void setUpMenuBar() {
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		editMenu = new JMenu("Edit");
		actionMenu = new JMenu("Action");
		
		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(actionMenu);
		
		/**************************************
		 *  Create MenuItems and handle them. *
		 **************************************/
		
		// File Menu Items 
		saveMenuItem = new JMenuItem("Save...");
		saveMenuItem.addActionListener(this);
		loadMenu = new JMenu("Load");
		loadMenu.addActionListener(this);
		loadMenuItems = new ArrayList<JMenuItem>();
		
		fileMenu.add(saveMenuItem);
		fileMenu.add(loadMenu);
		
		// Edit Menu Items
		addBackgroundItem = new JMenuItem("Change Background");
		addBackgroundItem.addActionListener(this);

		editMenu.add(addBackgroundItem);
		
		// Action Menu Items
		incHitMenuItem = new JMenuItem("Add Hit");
		incHitMenuItem.addActionListener(this);
		incHitMenuItem.setAccelerator(KeyStroke.getKeyStroke('h'));
		
		decHitMenuItem = new JMenuItem("Remove Hit");
		decHitMenuItem.addActionListener(this);
		decHitMenuItem.setAccelerator(KeyStroke.getKeyStroke('u'));

		nextSplitMenuItem = new JMenuItem("Next Split");
		nextSplitMenuItem.addActionListener(this);
		nextSplitMenuItem.setAccelerator(KeyStroke.getKeyStroke('n'));

		actionMenu.add(incHitMenuItem);
		actionMenu.add(decHitMenuItem);
		actionMenu.add(nextSplitMenuItem);
		
		return;
	}
	
	//
	private void setUpLoadMenuItems() {
		
		for (int i = 0; i < splitTitles.size(); i++) {
			
			JMenuItem item = new JMenuItem(splitTitles.get(i));
			item.addActionListener(this);
			item.setActionCommand(String.valueOf(i));
			
			loadMenuItems.add(item);
			loadMenu.add(item);
		}
		
		return;
	}
	
	//
	private void setUpViewComponents() {
		
		// Set Frame and Panels
		frame = new JFrame();
		panel = new JPanel();
		comboPanel = new JPanel();
		splitsPanel = new JPanel();
		transparencyLabel = new JLabel();
		scrollPane = new JScrollPane();
		
		// Set ScrollPane
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));

		// Set Buttons
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
		
		uploadNewTitleButton = new JButton("Upload Run");
		uploadNewTitleButton.addActionListener(this);
		
		uploadNewTitleButton.setMinimumSize(new Dimension(100, 20));
		uploadNewTitleButton.setPreferredSize(new Dimension(100, 20));
		uploadNewTitleButton.setMaximumSize(new Dimension(100, 20));
		
		return;
	}
	
	//
	private void setUpSplitRows() {
		
		for (int i = 0; i < splitNames.size(); i++) {
			
			splitRowArrayList.add(new ArrayList<SplitRow>());

			createSplitRows(splitNames.get(i), splitRowArrayList.get(i), pBSplitArrayList.get(i), pBCumulativeArrayList.get(i));
		}
		
		return;
	}
	
	//
	private void setPanels() {
		
		// Set Panel
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setOpaque(true);
		panel.setLayout(createTopLeveGroupLayout(panel));
		panel.setBackground(Color.black);
		
		// Set ComboPanel
		comboPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		comboPanel.setOpaque(true);
		
		OverlayLayout comboLayout = new OverlayLayout(comboPanel);
		comboPanel.setLayout(comboLayout);

		// Set Splits Panel
		splitsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		splitsPanel.setOpaque(false);
		splitsPanel.setLayout(createSplitsGroupLayout(splitsPanel, splitRowArrayList.get(currentTitle)));
		splitsPanel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));

		splitPanelHeight = (27*splitNames.get(currentTitle).size());
		setPanelDimensions(splitsPanel, new Dimension(splitPanelWidth, splitPanelHeight));

		System.out.println("Split #: " + String.valueOf(splitNames.get(currentTitle).size()));
		
		// Set Transparency Label
		transparencyLabel = new JLabel();
		transparencyLabel.setOpaque(true);
		transparencyLabel.setMinimumSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		transparencyLabel.setPreferredSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		transparencyLabel.setMaximumSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		transparencyLabel.setBackground(new Color(0.0f, 0.0f, 0.0f, 0.5f));
		transparencyLabel.setAlignmentX(0.5f);
		transparencyLabel.setAlignmentY(0.5f);
		
		// Set BackgroundLabel
		backgroundLabel = new JLabel(getScaledLabelIcon(backgroundImages.get(currentTitle), splitPanelWidth, scrollPaneHeight));
		backgroundLabel.setOpaque(true);
		backgroundLabel.setMinimumSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		backgroundLabel.setPreferredSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		backgroundLabel.setMaximumSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		backgroundLabel.setBackground(new Color(0.0f, 0.0f, 0.0f, 1.0f));
		backgroundLabel.setAlignmentX(0.5f);
		backgroundLabel.setAlignmentY(0.5f);
		
		// Add Components to ComboPanel
		comboPanel.add(scrollPane);
		comboPanel.add(transparencyLabel);
		comboPanel.add(backgroundLabel);
		
		// Set ScrollPane
		scrollPane.setPreferredSize(new Dimension(splitPanelWidth, scrollPaneHeight));
		scrollPane.setViewportView(splitsPanel);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		
		return;
	}
	
	//
	private void setUpHeaderAndTotalPanes(ArrayList<String> currentsplitpbcum) {
		
		String pBTotalHits = currentsplitpbcum.get(currentsplitpbcum.size() - 1);
		String totalHitDiffString = getHitDifference("0", pBTotalHits);
		createHeaderAndTotalTextPanes("0", totalHitDiffString, pBTotalHits);
		
		return;
	}
	
	//
	private void createSplitRows(ArrayList<String> splits, ArrayList<SplitRow> currentGameSplits, // -> Next Line
								 ArrayList<String> currentPBSplitArray, ArrayList<String> currentPBCumulativeArray) {
		
		Color splitColor = new Color(rgbHLBlue);
		
		for (int i = 0; i < splits.size(); i++) {
			
			if (i == 1) {
				splitColor = Color.white;
			}
			
			// Split Row
			String hitDiff = getHitDifference("0", currentPBSplitArray.get(i));
			
			SplitRow splitrow = new SplitRow(this, splits.get(i), hitDiff, currentPBSplitArray.get(i),  // -> Next Line
											 currentPBCumulativeArray.get(i), splitColor, currentGameSplits,  // -> Next Line
											 currentPBCumulativeArray);
			
			splitrow.addMouseListener(this);
			splitrow.setName(splits.get(i));
			splitrow.setOpaque(false);

			currentGameSplits.add(splitrow);
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
	
	//
	private void makePBCumulativeArrayList(ArrayList<String> pbcumulativelist, ArrayList<String> pbsplitlist) {
		
		int cumulativePB = 0;
		
		pbcumulativelist.clear();
		
		for (int i = 0; i < pbsplitlist.size(); i++) {
			
			cumulativePB = cumulativePB + Integer.parseInt(pbsplitlist.get(i));
			
			pbcumulativelist.add(String.valueOf(cumulativePB));
		}
		
		return;
	}
	
	/************************************
	 * Swing Component Helper Functions *
	 ************************************/
	
	//
	private void setTextPaneAttributes(SimpleAttributeSet alignment, JTextPane pane, String paneText, // -> Next Line
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
	private String getHitDifference(String hits, String splitPB) {
		
		int diff = Integer.parseInt(hits) - Integer.parseInt(splitPB);
		
		return String.valueOf(diff);
	}
	
	//
	private void loadDifferentTitle(int titlenumber) {
		
		currentTitle = titlenumber;
		
		splitsPanel.removeAll();
		
		splitsPanel.setLayout(createSplitsGroupLayout(splitsPanel, splitRowArrayList.get(currentTitle)));
		
		int splitPanelWidth = splitWidth + hitWidth + diffWidth + pbWidth + 10*5;
		int splitPanelHeight = (27*splitNames.get(currentTitle).size());
		setPanelDimensions(splitsPanel, new Dimension(splitPanelWidth, splitPanelHeight));
		
		changeBackgroundImage();
		
		System.out.println("Split #: " + String.valueOf(splitNames.get(currentTitle).size()));

		splitsPanel.validate();
		
		panel.repaint();
		panel.revalidate();
		
		updateHitDifferences(splitRowArrayList.get(currentTitle).get(currentSplits.get(currentTitle)), // -> Next Line
							 splitRowArrayList.get(currentTitle), pBCumulativeArrayList.get(currentTitle));
		
		return;
	}
	
	//
	private ImageIcon getScaledLabelIcon(BufferedImage srcImg, int width, int height) {
		
		BufferedImage scldImg = getScaledImage(srcImg, width, height);
		
		ImageIcon icon = new ImageIcon(scldImg);
		
		return icon;
	}
	
	//
	private BufferedImage getScaledImage(BufferedImage srcImg, int w, int h) {
		
		if (w < 1) {
			w = 100;
		}
		
		if (h < 1) {
			h = 100;
		}
		
		BufferedImage newImg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics g = newImg.createGraphics();
		g.drawImage(srcImg, 0, 0, w, h, null);
		g.dispose();
		
		return newImg;
	}
	
	/*****************************
	 * Layout Creation Functions *
	 *****************************/
	
	//
	private GroupLayout createSplitsGroupLayout(JPanel panel, ArrayList<SplitRow> splitrowarray) {
		
		GroupLayout layout = new GroupLayout(panel);

		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);
		
		/*
		 *  Horizontal Alignment
		 */
		ParallelGroup rowParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
	
		// Iterate over the ArrayList to create horizontal layout groupings
		for (int i = 0; i < splitrowarray.size(); i++) {
							
			rowParallelGroup.addComponent(splitrowarray.get(i));			
		}

		// Finalize Horizontal Layout
		layout.setHorizontalGroup(rowParallelGroup);
		
		/*
		 *  Vertical Alignment
		 */
		SequentialGroup columnSequentialGroup = layout.createSequentialGroup();
		
		// Iterate over the ArrayLists to create vertical layout groupings
		for (int i = 0; i < splitrowarray.size(); i++) {
				
			columnSequentialGroup.addComponent(splitrowarray.get(i));
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
		buttonSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 50);
		buttonSequentialGroup.addComponent(setPBButton);
		buttonSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 20, 50);
		buttonSequentialGroup.addComponent(uploadNewTitleButton);

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
		topLevelParallelGroup.addComponent(comboPanel);
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
		buttonParallelGroup.addComponent(uploadNewTitleButton);
		
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
		columnSequentialGroup.addComponent(comboPanel);
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
	private void resetRun(ArrayList<SplitRow> splitrowarray, ArrayList<String> pbcumulativelist, // -> Next Line
						  ArrayList<String> pbsplitlist) {
		
		Color pbcolor = new Color(rgbHLGold);

		setTextPaneAttributes(center, totalHitsTextPane, "0", false, true, pbcolor);
		
		String diffTotalText = getHitDifference("0", pbcumulativelist.get(pbcumulativelist.size() - 1));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, diffTotalText, false, true, pbcolor);
		setTextPaneAttributes(left, totalTextPane, totalTextPane.getText(), false, true, pbcolor);
		setTextPaneAttributes(center, totalPBHitsTextPane, totalPBHitsTextPane.getText(), false, true, pbcolor);	
		
		for (int i = splitrowarray.size() - 1; i >= 0; i--) {
			
			splitrowarray.get(i).resetRowHits();
		}
		
		currentSplits.set(currentTitle, 0);
		highlightRows(splitrowarray.get(0), splitrowarray, pbcumulativelist, null);
		
		return;
	}

	//
	private void setPB(ArrayList<SplitRow> splitrowarray, ArrayList<String> pbcumulativelist, // -> Next Line
					   ArrayList<String> pbsplitlist) {
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		pbsplitlist.clear();

		for (int i = 0; i < splitrowarray.size(); i++) {
			
			pbsplitlist.add(splitrowarray.get(i).getHits());
		}
		
		makePBCumulativeArrayList(pbcumulativelist, pbsplitlist);
		
		for (int i = 0; i < splitrowarray.size(); i++) {
						
			splitrowarray.get(i).setPB(pbsplitlist.get(i), pbcumulativelist.get(i));
		}
		
		String pBTotalText = pbcumulativelist.get(pbcumulativelist.size() - 1);
		
		setTextPaneAttributes(center, totalPBHitsTextPane, pBTotalText, false, true, Color.white);
		
		resetRun(splitrowarray, pbcumulativelist, pbsplitlist);
		
		return;
	}
	
	//
	private void changeBackgroundImage() {
		
		backgroundLabel.setIcon(getScaledLabelIcon(backgroundImages.get(currentTitle), splitPanelWidth, scrollPaneHeight));
		
		return;
	}
	
	//
	private void incrementCurrentSplitHit(ArrayList<SplitRow> splitrowarray, ArrayList<String> pbcumulativelist) {
		
		SplitRow currentRow = splitrowarray.get(currentSplits.get(currentTitle));
		currentRow.setHits(currentRow.getHitsFromPane());

		String updatedHitString = String.valueOf(Integer.parseInt(currentRow.getHits()) + 1);
		currentRow.setHits(updatedHitString);
		
		updateHitDifferences(currentRow, splitrowarray, pbcumulativelist);
		
		menuBar.requestFocusInWindow();
		
		return;
	}
	
	//
	private void decrementCurrentSplitHit(ArrayList<SplitRow> splitrowarray, ArrayList<String> pbcumulativelist) {
		
		SplitRow currentRow = splitrowarray.get(currentSplits.get(currentTitle));
		currentRow.setHits(currentRow.getHitsFromPane());
		
		int updatedHitInt = Integer.parseInt(currentRow.getHits()) - 1;
		
		if (updatedHitInt < 0) {
			updatedHitInt = 0;
		}
		
		String updatedHitString = String.valueOf(updatedHitInt);
		currentRow.setHits(updatedHitString);
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		updateHitDifferences(currentRow, splitrowarray, pbcumulativelist);
		
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		menuBar.requestFocusInWindow();

		return;
	}
	
	//
	private void goToNextSplit(Callback callback, int scrollpos, ArrayList<SplitRow> splitrowarray, // -> Next Line
							   ArrayList<String> pbcumulativelist) {
		
		Callback highlightCallback = new Callback() {
			public void nextsplitcallback(int pos) {
				return;
			}
			
			public void highlightcallback() {
				callback.nextsplitcallback(scrollpos);
				return;
			}
		};
		
		splitrowarray.get(currentSplits.get(currentTitle)) // -> Next Line
					 .setHits(splitrowarray.get(currentSplits.get(currentTitle)).getHitsFromPane());
		
		currentSplits.set(currentTitle, currentSplits.get(currentTitle) + 1);

		if (currentSplits.get(currentTitle) < splitrowarray.size()) {

			highlightRows(splitrowarray.get(currentSplits.get(currentTitle)), splitrowarray, pbcumulativelist, highlightCallback);
		}
		else {
			currentSplits.set(currentTitle, splitrowarray.size() - 1);
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
		    
		    if((line = br.readLine()) != null) {
		    	
		    	if (line.equals("--------------------")) {
					
		    		while ((line = br.readLine()) != null) {
		    			
		    			if (line.equals("")) { break; }
		    			
		    			backgroundImgFileNames.add(line);
		    			System.out.println(line);
		    			
		    			File bgfile = new File(filePathString + line);
		    			BufferedImage bgimg = openImageFile(bgfile);
		    			backgroundImages.add(bgimg);
		    		}
		    		
		    		line = br.readLine();
				}
		    	
		    	if (line.equals("----------")) {
					
		    		splitNames.add(new ArrayList<String>());
		    		pBSplitArrayList.add(new ArrayList<String>());
		    		pBCumulativeArrayList.add(new ArrayList<String>());
		    		
		    		currentSplits.add(0);
		    		backgroundImages.add(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));

		    		line = br.readLine();
		    		splitTitles.add(line);
		    		
		    		int readcounter = 0;
		    		int splitcounter = 0;
		    		while ((line = br.readLine()) != null) {
				    	// process the line.
		    			if (line.equals("")) {
		    				
		    				if (splitNames.get(splitcounter).size() != pBSplitArrayList.get(splitcounter).size()) {
		    					System.out.println("Error in reading save file: This Split # != pb #");
		    					System.out.println("The line: " + java.util.regex.Matcher.quoteReplacement(line));
		    					throw new RuntimeException();
							}
		    			}
		    			else if (line.equals("----------")) {
		    				
		    				if (splitNames.get(splitcounter).size() != pBSplitArrayList.get(splitcounter).size()) {
		    					System.out.println("Error in reading save file: Previous Split # != pb #");
		    					System.out.println("The line: " + java.util.regex.Matcher.quoteReplacement(line));
		    					throw new RuntimeException();
							}
		    				
				    		line = br.readLine();
				    		splitTitles.add(line);
		    				
							readcounter = 0;
							splitcounter = splitcounter + 1;
							
				    		splitNames.add(new ArrayList<String>());
				    		pBSplitArrayList.add(new ArrayList<String>());
				    		pBCumulativeArrayList.add(new ArrayList<String>());
				    		
				    		currentSplits.add(0);
				    		backgroundImages.add(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));
						}
		    			else if ((readcounter % 2) == 0) {
							
		    				splitNames.get(splitcounter).add(line);
		    				
		    				readcounter = readcounter + 1;
						}
		    			else {
		    				
					    	pBSplitArrayList.get(splitcounter).add(line);
					    	
		    				readcounter = readcounter + 1;
		    			}
				    }
				}
		    	else {
		    		
		    		splitNames.add(new ArrayList<String>());
		    		pBSplitArrayList.add(new ArrayList<String>());
		    		pBCumulativeArrayList.add(new ArrayList<String>());
		    		
		    		currentSplits.add(0);
		    		backgroundImages.add(new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR));

		    		loadZOOTSplits();
		    		
		    		// Load first line
			    	pBSplitArrayList.get(currentTitle).add(line);
		    		
			    	// Load the rest of the lines
				    while ((line = br.readLine()) != null) {
				    	// process the line.
				    	pBSplitArrayList.get(currentTitle).add(line);
				    }
		    	}
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
			
			bufferWriter.write("--------------------");
			bufferWriter.newLine();
			
			for (int i = 0; i < backgroundImgFileNames.size(); i++) {
				bufferWriter.write(backgroundImgFileNames.get(i));
				bufferWriter.newLine();
			}
			
			bufferWriter.newLine();
			
			for (int i = 0; i < splitRowArrayList.size(); i++) {

				bufferWriter.write("----------");
				bufferWriter.newLine();
				
				bufferWriter.write(splitTitles.get(i));
				bufferWriter.newLine();

				for (int j = 0; j < splitRowArrayList.get(i).size(); j++) {
					
					bufferWriter.write(splitRowArrayList.get(i).get(j).getSplitName());
					bufferWriter.newLine();
					
					bufferWriter.write(splitRowArrayList.get(i).get(j).getPB());
					bufferWriter.newLine();
				}
				
				bufferWriter.newLine();
			}
			
			// Close FileWriter
			bufferWriter.close();
 
		} catch (IOException e) {
			System.out.println("Excepton Occured: " + e.toString());
			e.printStackTrace();
		}
		
		return;
	}
	
	//
	private void saveImageToFile(String imagefilename, BufferedImage image) {
		
		File imagefile = new File(filePathString + imagefilename);
		
		if (!imagefile.exists()) {
			File directory = new File(imagefile.getParent());
			if (!directory.exists()) {
				directory.mkdirs();
			}
		
			try {
				System.out.println("Wrote Image to: " + filePathString + imagefilename);
			    ImageIO.write(image, "png", imagefile);
			} catch (IOException e) {
				System.out.println(e);
				e.printStackTrace();
			}
		}
		
		return;
	}
	
	//
	private void uploadNewTitle(int titlenum) {
		
		System.out.println("Load savefile: " + titleFilePathString);

		File titleFile = new File(titleFilePathString);
		
		try (BufferedReader br = new BufferedReader(new FileReader(titleFile))) {
		    String line;
		    
		    if((line = br.readLine()) != null) {
		    	
	    		splitNames.add(new ArrayList<String>());
	    		pBSplitArrayList.add(new ArrayList<String>());
	    		pBCumulativeArrayList.add(new ArrayList<String>());
	    		
	    		currentSplits.add(0);
	    		
	    		splitTitles.add(line);
	    		
	    		if((line = br.readLine()) != null) {
	    			backgroundImgFileNames.add(line);
	    			
	    			File bgfile = new File(filePathString + line);
	    			BufferedImage bgimg = openImageFile(bgfile);
	    			backgroundImages.add(bgimg);
	    		}
	    		
	    		while ((line = br.readLine()) != null) {
			    	// process the line.
	    			if (line.equals("")) {
	    				
	    				if (splitNames.get(titlenum).size() != pBSplitArrayList.get(titlenum).size()) {
	    					System.out.println("Error in reading save file: This Split # != pb #");
	    					System.out.println("The line: " + java.util.regex.Matcher.quoteReplacement(line));
	    					throw new RuntimeException();
						}
	    			}
	    			else {
						
	    				splitNames.get(titlenum).add(line);
				    	pBSplitArrayList.get(titlenum).add("0");
				    	pBCumulativeArrayList.get(titlenum).add("0");
					}	
			    }
		    }
		}
		catch (Exception e) {
			System.out.println("Excepton Occured: " + e.toString());
		}
		
		return;
	}
	
	//
	private void addBackgroundImageToSplit() {
		
		File bgfile = getBackgroundImageFromFile();
		
		if (bgfile == null) { return; }
		
		BufferedImage bgimg = openImageFile(bgfile);
		backgroundImages.set(currentTitle, bgimg);
		backgroundImgFileNames.set(currentTitle, bgfile.getName());
		
		changeBackgroundImage();
		
		saveImageToFile(bgfile.getName(), bgimg);

		panel.repaint();
		panel.revalidate();
		
		return;
	}
	
	//
	private File getBackgroundImageFromFile() {
		
		JFileChooser jfc = new JFileChooser();
	    jfc.showDialog(null,"Please Select the Image File");
	    jfc.setVisible(true);
	    
	    if (jfc.getSelectedFile() == null) {
	    	return null;
	    }
	    
	    File file = jfc.getSelectedFile();
	    System.out.println("File name " + file.getName());
		
		return file;
	}
	
	//
	private BufferedImage openResourceImageFile(String filename) {
		
		System.out.println("Filename pass to openResourceImageFile: " + filename);
		InputStream imageStream = this.getClass().getClassLoader().getResourceAsStream(filename);
		
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);

		if (imageStream != null) {

			try {
	            image = ImageIO.read(imageStream);
			} catch (Exception ex) {
				System.out.println(ex);
				ex.printStackTrace();
			}
		}
		else {
            System.out.println("file " + filename + " does not exist");
            
    		File file = new File(filePathString + filename);
            image = openImageFile(file);
		}
		
		return image;
	}
	
	//
	private BufferedImage openImageFile(File file) {
		
		BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
		
		try {
			System.out.println("Canonical path of target image: " + file.getCanonicalPath());
            if (!file.exists()) {
                System.out.println("file " + file + " does not exist");
            }
            image = ImageIO.read(file);
		} catch (Exception ex) {
			System.out.println(ex);
			ex.printStackTrace();
		}
		
		return image;
	}
	
	/************
	 *** Main ***
	 ************/
	
	public static void main(String[] args) {

		try {
			new HitCounter();
		} catch (Exception e) {
			System.out.println("Exception Occurred: " + e.getMessage());
			e.printStackTrace();
			System.exit(1);
		}
	}

	/************************
	 * Overridden Functions *
	 ************************/
	
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == saveMenuItem) {
			saveProgram();
		}
		else if (isLoadMenuItem(e)) {
			
			int loadTitleNum = Integer.parseInt(((JMenuItem) e.getSource()).getActionCommand());
			
			if (loadTitleNum != currentTitle) {
				
				loadDifferentTitle(loadTitleNum);
			}
		}
		else if (e.getSource() == addBackgroundItem) {
			addBackgroundImageToSplit();
		}
		else if (e.getSource() == incHitMenuItem) {
			incrementCurrentSplitHit(splitRowArrayList.get(currentTitle), pBCumulativeArrayList.get(currentTitle));
		}
		else if (e.getSource() == decHitMenuItem) {
			decrementCurrentSplitHit(splitRowArrayList.get(currentTitle), pBCumulativeArrayList.get(currentTitle));
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

			goToNextSplit(fixViewCallback, scrollpos, splitRowArrayList.get(currentTitle), // -> Next Line
						  pBCumulativeArrayList.get(currentTitle));
		}
		else if (e.getSource() == resetRunButton) {
			resetRun(splitRowArrayList.get(currentTitle), pBCumulativeArrayList.get(currentTitle), // -> Next Line
					 pBSplitArrayList.get(currentTitle));
		}
		else if (e.getSource() == setPBButton) {
			setPB(splitRowArrayList.get(currentTitle), pBCumulativeArrayList.get(currentTitle), // -> Next Line
				  pBSplitArrayList.get(currentTitle));
		}
		else if (e.getSource() == uploadNewTitleButton) {
			
			int increasedsize = splitTitles.size();
			uploadNewTitle(increasedsize);
			splitRowArrayList.add(new ArrayList<SplitRow>());
			createSplitRows(splitNames.get(increasedsize), splitRowArrayList.get(increasedsize), // -> Next Line
							pBSplitArrayList.get(increasedsize), pBCumulativeArrayList.get(increasedsize));
			loadDifferentTitle(increasedsize);
			
			JMenuItem item = new JMenuItem(splitTitles.get(increasedsize));
			item.addActionListener(this);
			item.setActionCommand(String.valueOf(increasedsize));
			
			loadMenuItems.add(item);
			loadMenu.add(item);
		}
		
		return;
	}
	
	// actionPerformed helper function
	private boolean isLoadMenuItem(ActionEvent e) {
		
		for (int i = 0; i < loadMenuItems.size(); i++) {
			
			if (e.getSource() == loadMenuItems.get(i)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		if (e.getSource() instanceof JComponent) {
			
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					JComponent source = (JComponent) e.getSource();

					highlightRows(source, splitRowArrayList.get(currentTitle), // -> Next Line
								  pBCumulativeArrayList.get(currentTitle), null);
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
