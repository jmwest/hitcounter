package hitCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
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
import javax.swing.GroupLayout;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
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

    final static int rgbHLBlue=new Color(13, 213, 255).getRGB();

    private String userSaveFilePathString = System.getProperty("user.dir") + System.getProperty("file.separator")
											+ "resources" + System.getProperty("file.separator") + "usersave.txt";
	
    private boolean hasSavedPB = true;
    
    private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem saveMenuItem;
	
	private JFrame frame;
	private JPanel panel;
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
	
	// Public Class Constructor
	public HitCounter() {

		// Menu setup
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		
		menuBar.add(fileMenu);
		
		// Create MenuItems and handle them.
		saveMenuItem = new JMenuItem("Save...");
		saveMenuItem.addActionListener(this);
		
		fileMenu.add(saveMenuItem);
		
		//
		frame = new JFrame();
		panel = new JPanel();
		scrollPane = new JScrollPane();
		
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		
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
		
		// Set Panel
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setOpaque(false);
		panel.setLayout(createGroupLayout(panel));
		
		int panelWidth = 10*2 + 150 + 100 + 100 + 100 + 10*4 + 10;
		int panelHeight = 10 + (20 + 2)*(splitNames.size() + 12) + 30 + 15*2 + 10;
		setPanelDimensions(panel, new Dimension(panelWidth, panelHeight));

		// Set ScrollPane
		scrollPane.setPreferredSize(new Dimension(panelWidth, 700));
		scrollPane.setViewportView(panel);
		
		// Set Frame
		frame.setJMenuBar(menuBar);
		frame.addMouseListener(this);
		//frame.add(panel, BorderLayout.CENTER);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Hit Counter");
		frame.getContentPane().add(scrollPane);
		frame.pack();
		frame.setVisible(true);
	}
	
	// Load ZOoT Splits
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
	
	//
	private void createSplitTextPanes(ArrayList<String> splits) {
		
		for (int i = 0; i < splits.size(); i++) {
			
			// Create Alignments
			SimpleAttributeSet left = new SimpleAttributeSet();
			StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
			
			SimpleAttributeSet center = new SimpleAttributeSet();
			StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
			
			// Split Name
			JTextPane splitNameTextPane = new JTextPane();
			
			setJTextPaneDimensions(splitNameTextPane, 100, 150, 200, 15);
			setTextPaneAttributes(left, splitNameTextPane, splits.get(i), true, false);
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
					updateHitDifference((JTextPane) e.getSource());
			    }
			});
			
			setJTextPaneDimensions(hitCountTextPane, 40, 40, 40, 15);
			setTextPaneAttributes(center, hitCountTextPane, "0", true, false);

			splitCurrentHitsArrayList.add(hitCountTextPane);
			
			// Hit Difference
			JTextPane hitDifferenceTextPane = new JTextPane();
			hitDifferenceTextPane.setName(splits.get(i));
			hitDifferenceTextPane.addMouseListener(this);
			hitDifferenceTextPane.setName(splits.get(i));

			String hitDifference = getHitDifference("0", pBSplitArrayList.get(i));

			setJTextPaneDimensions(hitDifferenceTextPane, 40, 40, 40, 15);
			setTextPaneAttributes(center, hitDifferenceTextPane, hitDifference, false, false);
			
			splitHitDifferenceArrayList.add(hitDifferenceTextPane);

			// PB
			JTextPane pBTextPane = new JTextPane();
			pBTextPane.setName(splits.get(i));
			pBTextPane.addMouseListener(this);
			pBTextPane.setName(splits.get(i));

			String pBText = pBSplitArrayList.get(i) + "(" + pBCumulativeArrayList.get(i) + ")";
			
			setJTextPaneDimensions(pBTextPane, 60, 60, 60, 15);
			setTextPaneAttributes(center, pBTextPane, pBText, false, false);

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
		setJTextPaneDimensions(splitNameHeader, 100, 100, 100, 20);
		setJTextPaneDimensions(currentHitsHeader, 100, 100, 100, 20);
		setJTextPaneDimensions(hitDifferenceHeader, 100, 100, 100, 20);
		setJTextPaneDimensions(pBHitsHeader, 100, 100, 100, 20);

		setJTextPaneDimensions(totalTextPane, 100, 150, 200, 20);
		setJTextPaneDimensions(totalHitsTextPane, 40, 40, 40, 20);
		setJTextPaneDimensions(totalDifferenceTextPane, 40, 40, 40, 20);
		setJTextPaneDimensions(totalPBHitsTextPane, 60, 60, 60, 20);

		//
		setTextPaneAttributes(center, splitNameHeader, "Splits", false, true);
		setTextPaneAttributes(center, currentHitsHeader, "Current", false, true);
		setTextPaneAttributes(center, hitDifferenceHeader, "Diff", false, true);
		setTextPaneAttributes(center, pBHitsHeader, "PB", false, true);

		setTextPaneAttributes(left, totalTextPane, "Total", false, true);
		setTextPaneAttributes(center, totalHitsTextPane, totalHitsString, false, true);
		setTextPaneAttributes(center, totalDifferenceTextPane, totalDiffString, false, true);
		setTextPaneAttributes(center, totalPBHitsTextPane, pBString, false, true);

		return;
	}
	
	private void setTextPaneAttributes(SimpleAttributeSet alignment, JTextPane pane, String paneText,
										boolean editable, boolean bold) {
		
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		StyleConstants.setForeground(attributeSet, Color.black);
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
	
	private void setPanelDimensions(JPanel panel, Dimension dim) {
		
		panel.setMinimumSize(dim);
		panel.setPreferredSize(dim);
		panel.setMaximumSize(dim);
		
		return;
	}
	
	private void updateHitDifference(JTextPane sourceHitPane) {
		
		if (Integer.parseInt(sourceHitPane.getText()) == focusedPaneHits) {
			return;
		}
		
		// Create Alignments
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		//
		String sourceName = sourceHitPane.getName();
		
		JTextPane diffPane = getDiffPane(sourceName);
		
		String pbString = "";
		
		for (int i = 0; i < pBHitsArrayList.size(); i++) {
			
			if (pBHitsArrayList.get(i).getName().equals(sourceName)) {
				pbString = pBSplitArrayList.get(i);
			}
		}
		
		String changedDiffString = getHitDifference(sourceHitPane.getText(), pbString);
		
		setTextPaneAttributes(center, diffPane, changedDiffString, false, false);
		
		int currentTotalHits = 0;
		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
			currentTotalHits = currentTotalHits + Integer.parseInt(splitCurrentHitsArrayList.get(i).getText());
		}
		
		int pbTotalHits = 0;
		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
			pbTotalHits = pbTotalHits + Integer.parseInt(pBSplitArrayList.get(i));
		}
		
		setTextPaneAttributes(center, totalHitsTextPane, String.valueOf(currentTotalHits), false, true);
		
		String changedTotalDiffString = getHitDifference(String.valueOf(currentTotalHits), String.valueOf(pbTotalHits));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, changedTotalDiffString, false, true);
				
		sourceHitPane.setCaretPosition(0);
		
		return;
	}
	
	private JTextPane getDiffPane(String name) {
		
		for (int i = 0; i < splitHitDifferenceArrayList.size(); i++) {
			
			if (splitHitDifferenceArrayList.get(i).getName().equals(name)) {
				return splitHitDifferenceArrayList.get(i);
			}
		}
		
		return null;
	}
	
	private String getHitDifference(String hits, String splitPB) {
		
		int diff = Integer.parseInt(hits) - Integer.parseInt(splitPB);
		
		return String.valueOf(diff);
	}
	
	private void makePBCumulativeArrayList() {
		
		int cumulativePB = 0;
		
		pBCumulativeArrayList.clear();
		
		for (int i = 0; i < pBSplitArrayList.size(); i++) {
			
			cumulativePB = cumulativePB + Integer.parseInt(pBSplitArrayList.get(i));
			
			pBCumulativeArrayList.add(String.valueOf(cumulativePB));
		}
		
		return;
	}
	
	private GroupLayout createGroupLayout(JPanel panel) {
		
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
		firstColumnParallelGroup.addComponent(splitNameHeader);
		
		// Iterate over the ArrayList to create horizontal layout groupings
		for (int i = 0; i < splitNameTextArrayList.size(); i++) {
							
			firstColumnParallelGroup.addComponent(splitNameTextArrayList.get(i));			
		}
		
		firstColumnParallelGroup.addComponent(totalTextPane);
		
		rowSequentialGroup.addGroup(firstColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		// SECOND COLUMN
		ParallelGroup secondColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		secondColumnParallelGroup.addComponent(currentHitsHeader);
		
		// Iterate over the ArrayList to create next parallel grouping
		for (int i = 0; i < splitCurrentHitsArrayList.size(); i++) {
							
			secondColumnParallelGroup.addComponent(splitCurrentHitsArrayList.get(i));			
		}
		
		secondColumnParallelGroup.addComponent(totalHitsTextPane);
		
		rowSequentialGroup.addGroup(secondColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		// THIRD COLUMN
		ParallelGroup thirdColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		thirdColumnParallelGroup.addComponent(hitDifferenceHeader);
		
		// Iterate over the ArrayList to create next parallel grouping
		for (int i = 0; i < splitHitDifferenceArrayList.size(); i++) {
							
			thirdColumnParallelGroup.addComponent(splitHitDifferenceArrayList.get(i));			
		}
		
		thirdColumnParallelGroup.addComponent(totalDifferenceTextPane);
		
		rowSequentialGroup.addGroup(thirdColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		// FOURTH COLUMN
		ParallelGroup fourthColumnParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		fourthColumnParallelGroup.addComponent(pBHitsHeader);
		
		// Iterate over the ArrayList to create next parallel grouping
		for (int i = 0; i < pBHitsArrayList.size(); i++) {
							
			fourthColumnParallelGroup.addComponent(pBHitsArrayList.get(i));			
		}
		
		fourthColumnParallelGroup.addComponent(totalPBHitsTextPane);

		rowSequentialGroup.addGroup(fourthColumnParallelGroup);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		/* 
		 * Add SetPBButton Layout Group and rowSequentialGroup
		 * to TopLevelParallelGroup
		 */		
		SequentialGroup buttonSequentialGroup = layout.createSequentialGroup();
		
		buttonSequentialGroup.addComponent(resetRunButton);
		buttonSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 30, 50);
		buttonSequentialGroup.addComponent(setPBButton);

		topLevelParallelGroup.addGroup(buttonSequentialGroup);
		
		topLevelParallelGroup.addGroup(rowSequentialGroup);
		
		// Finalize Horizontal Layout
		layout.setHorizontalGroup(topLevelParallelGroup);
		
		/*
		 *  Vertical Alignment
		 */
		SequentialGroup columnSequentialGroup = layout.createSequentialGroup();
		
		ParallelGroup buttonParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		buttonParallelGroup.addComponent(resetRunButton);
		buttonParallelGroup.addComponent(setPBButton);
		
		columnSequentialGroup.addGroup(buttonParallelGroup);
		columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 30, 30);

		// Set Layout for Header
		ParallelGroup headerParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		headerParallelGroup.addComponent(splitNameHeader);
		headerParallelGroup.addComponent(currentHitsHeader);
		headerParallelGroup.addComponent(hitDifferenceHeader);
		headerParallelGroup.addComponent(pBHitsHeader);
		
		columnSequentialGroup.addGroup(headerParallelGroup);
		columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 15, 15);
		
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
		
		columnSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED, 20, 20);

		// Set Layout for Total row
		ParallelGroup totalParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		totalParallelGroup.addComponent(totalTextPane);
		totalParallelGroup.addComponent(totalHitsTextPane);
		totalParallelGroup.addComponent(totalDifferenceTextPane);
		totalParallelGroup.addComponent(totalPBHitsTextPane);
		
		columnSequentialGroup.addGroup(totalParallelGroup);

		// Finalize Vertical Layout
		layout.setVerticalGroup(columnSequentialGroup);
		
		return layout;
	}
	
	private void setJTextPaneDimensions(JTextPane textPane, int minwidth, int prefwidth, int maxwidth, int height) {
		
		textPane.setMinimumSize(new Dimension(minwidth, height));
		textPane.setPreferredSize(new Dimension(prefwidth, height));
		textPane.setMaximumSize(new Dimension(maxwidth, height));
		
		return;
	}
	
	private void resetRun() {
		
		SimpleAttributeSet center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		setTextPaneAttributes(center, totalHitsTextPane, "0", false, true);
		
		String diffTotalText = getHitDifference("0", pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1));
		
		setTextPaneAttributes(center, totalDifferenceTextPane, diffTotalText, false, true);
		
		for (int i = splitCurrentHitsArrayList.size() - 1; i >= 0; i--) {
			
			focusedPaneHits = -1;

			splitCurrentHitsArrayList.get(i).setText("0");
			updateHitDifference(splitCurrentHitsArrayList.get(i));
		}
		
		highlightRow(splitNameTextArrayList.get(0));
		
		return;
	}
	
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
			
			setTextPaneAttributes(center, pBHitsArrayList.get(i), pBText, false, false);
		}
		
		String pBTotalText = pBCumulativeArrayList.get(pBCumulativeArrayList.size() - 1);
		
		setTextPaneAttributes(center, totalPBHitsTextPane, pBTotalText, false, true);
		
		resetRun();
		
		return;
	}
	
	private void highlightRow(JComponent source) {
		
		for (int i = 0; i < panel.getComponentCount(); i++) {

			if (panel.getComponent(i).getName() == source.getName()) {
				JTextPane pane = (JTextPane) panel.getComponent(i);

				pane.setOpaque(true);
				pane.setBackground(new Color(rgbHLBlue));
				pane.setForeground(Color.WHITE);
			}
			else if (panel.getComponent(i).getName() != null) {
				JTextPane pane = (JTextPane) panel.getComponent(i);

				pane.setBackground(Color.WHITE);
				pane.setForeground(Color.BLACK);
				pane.setOpaque(false);
			}
		}
		
		return;
	}
	
	/*
	 * Save and Load Functions
	 */
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
	
	// Main
	public static void main(String[] args) {

		new HitCounter();
	}

	// Overridden Functions
	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == saveMenuItem) {
			saveProgram();
		}
		else if (e.getSource() == resetRunButton) {
			resetRun();
		}
		else if (e.getSource() == setPBButton) {
			setPB();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		JComponent source = (JComponent) e.getSource();
		
		System.out.println("X: " + String.valueOf(e.getX()));
		System.out.println("Y: " + String.valueOf(e.getY()));
		
		System.out.println("Name: " + String.valueOf(source.getName()));
		
		highlightRow(source);
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
