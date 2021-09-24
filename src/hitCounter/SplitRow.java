package hitCounter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class SplitRow extends JPanel implements MouseListener {

	/*************
	 * Serial ID *
	 *************/
	
	private static final long serialVersionUID = 5526200681077926462L;
	
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
    
    private HitCounter parentCounter;
    
	private JTextPane splitNameTextPane;
	private JTextPane splitCurrentHitsPane;
	private JTextPane splitHitDifferencePane;
	private JTextPane pBHitsPane;
		
	private String splitNameString;
	private String splitHitsString;
	private String splitDiffString;
	private String pbHitString;
	private Color rowColor;
	private int focusedPaneHits = 0;
	
	SimpleAttributeSet left;
	SimpleAttributeSet center;
	
	/*****************************
	 * Public Class Constructors *
	 *****************************/
	
	//
	public SplitRow(HitCounter parent, String splitText, String splitCurrentHits, String splitHitDifference, String pbHits,
					String pbPriorHits, Color splitColor) {
		
		// Create Alignments
		left = new SimpleAttributeSet();
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		
		center = new SimpleAttributeSet();
		StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
		
		//
		parentCounter = parent;
		splitNameString = splitText;
		splitHitsString = splitCurrentHits;
		splitDiffString = splitHitDifference;
		pbHitString = pbHits;
		rowColor = splitColor;
		
		createSplitTextPanes(splitText, splitCurrentHits, splitHitDifference, pbPriorHits, splitColor);
		
		// Set Panel
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		this.setOpaque(false);
		this.setLayout(createSplitsRowLayout(this));
	}
	
	//
	public SplitRow(HitCounter parent, String splitText, String splitHitDifference, String pbText,
					String pbPriorHits, Color splitColor) {
		
		this(parent, splitText, "0", splitHitDifference, pbText, pbPriorHits, splitColor);
	}
	
	//
	public SplitRow(HitCounter parent, String splitText, Color splitColor) {
		
		this(parent, splitText, "0", "0", "0", "0", splitColor);
	}
	
	//
	public SplitRow(HitCounter parent, Color splitColor) {
		
		this(parent, "", "0", "0", "0", "0", splitColor);
	}
	
	/****************************
	 *  *
	 ****************************/
	
	private void createSplitTextPanes(String splitText, String splitCurrentHits, String splitHitDifference,
									  String pbPriorHits, Color splitColor) {
		
		// Split Name
		splitNameTextPane = new JTextPane();
		
		setJTextPaneDimensions(splitNameTextPane, 100, splitWidth, splitWidth, 15);
		setTextPaneAttributes(left, splitNameTextPane, splitText, true, false, splitColor);
		splitNameTextPane.addMouseListener(this);
		splitNameTextPane.setName(splitText);
		
		// Hit Count
		splitCurrentHitsPane = new JTextPane();
		splitCurrentHitsPane.setName(splitText);
		splitCurrentHitsPane.addMouseListener(this);
		
		SplitRow thisRow = this;

		splitCurrentHitsPane.addFocusListener(new FocusListener() {
			
			public void focusGained(FocusEvent e) {
				JTextPane currentPane = (JTextPane) e.getSource();
				focusedPaneHits = Integer.parseInt(currentPane.getText());
			}
		    
			public void focusLost(FocusEvent e) {
				parseTextPaneInput();
				updateHitDifference();
				parentCounter.highlightRows(thisRow, null);
		    }
		});
		
		setJTextPaneDimensions(splitCurrentHitsPane, hitWidth, hitWidth, hitWidth, 15);
		setTextPaneAttributes(center, splitCurrentHitsPane, "0", true, false, splitColor);
		
		// Hit Difference
		splitHitDifferencePane = new JTextPane();
		splitHitDifferencePane.setName(splitText);
		splitHitDifferencePane.addMouseListener(this);

		String hitDifference = getHitDifference("0", pbHitString);

		setJTextPaneDimensions(splitHitDifferencePane, diffWidth, diffWidth, diffWidth, 15);
		setTextPaneAttributes(center, splitHitDifferencePane, hitDifference, false, false, splitColor);
		
		// PB
		pBHitsPane = new JTextPane();
		pBHitsPane.setName(splitText);
		pBHitsPane.addMouseListener(this);

		String pBText = pbHitString + "(" + pbPriorHits + ")";
		
		setJTextPaneDimensions(pBHitsPane, pbWidth, pbWidth, pbWidth, 15);
		setTextPaneAttributes(center, pBHitsPane, pBText, false, false, splitColor);
		
		return;
	}
	
	//
	private void updateHitDifference() {
		
		if (Integer.parseInt(splitCurrentHitsPane.getText()) == focusedPaneHits) {
			return;
		}
		
		// Get new color for row
		splitDiffString = getHitDifference(splitCurrentHitsPane.getText(), pbHitString);
		Color splitColor = new Color(parentCounter.determineHighlightColor(this));
		
		// Make changes to UI
		if (!rowColor.equals(splitColor)) {
			highlightRow(splitColor);
		}
		
		parentCounter.updateHitDifferences(this);
		
		return;
	}
	
	//
	private String getHitDifference(String hits, String splitPB) {
		
		int diff = Integer.parseInt(hits) - Integer.parseInt(splitPB);
		
		return String.valueOf(diff);
	}
	
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
	private void parseTextPaneInput() {
		// TODO check if user inputs invalid string
	}
	
	/*******************
	 * Layout Function *
	 *******************/
	
	//
	private GroupLayout createSplitsRowLayout(JPanel panel) {
		
		GroupLayout layout = new GroupLayout(panel);

		layout.setAutoCreateGaps(false);
		layout.setAutoCreateContainerGaps(false);
		
		/*
		 *  Horizontal Alignment
		 */
		SequentialGroup rowSequentialGroup = layout.createSequentialGroup();
		
		rowSequentialGroup.addComponent(splitNameTextPane);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		rowSequentialGroup.addComponent(splitCurrentHitsPane);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		rowSequentialGroup.addComponent(splitHitDifferencePane);
		rowSequentialGroup.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, 10, 15);
		
		rowSequentialGroup.addComponent(pBHitsPane);
		
		// Finalize Horizontal Layout
		layout.setHorizontalGroup(rowSequentialGroup);
		
		/*
		 *  Vertical Alignment
		 */
		ParallelGroup rowParallelGroup = layout.createParallelGroup(Alignment.CENTER);
		
		rowParallelGroup.addComponent(splitNameTextPane);		
		rowParallelGroup.addComponent(splitCurrentHitsPane);		
		rowParallelGroup.addComponent(splitHitDifferencePane);		
		rowParallelGroup.addComponent(pBHitsPane);

		// Finalize Vertical Layout
		layout.setVerticalGroup(rowParallelGroup);
		
		return layout;
	}
	
	/********************
	 * Public Functions *
	 ********************/
	
	//
	public void resetRowHits() {
				
		setHits("0");
		
		return;
	}

	//
	public void highlightRow(Color splitColor) {
		
		rowColor = splitColor;
		
		setTextPaneAttributes(left, splitNameTextPane, splitNameString, true, false, rowColor);
		setTextPaneAttributes(center, splitCurrentHitsPane, splitHitsString, true, false, rowColor);
		setTextPaneAttributes(center, splitHitDifferencePane, splitDiffString, false, false, rowColor);
		setTextPaneAttributes(center, pBHitsPane, pbHitString, false, false, rowColor);
		
		return;
	}
	
	//
	public void highlightRow(Color splitColor, String pbcum) {
		
		rowColor = splitColor;
		String pbCumString = pbHitString + "(" + pbcum + ")";
		
		setTextPaneAttributes(left, splitNameTextPane, splitNameString, true, false, rowColor);
		setTextPaneAttributes(center, splitCurrentHitsPane, splitHitsString, true, false, rowColor);
		setTextPaneAttributes(center, splitHitDifferencePane, splitDiffString, false, false, rowColor);
		setTextPaneAttributes(center, pBHitsPane, pbCumString, false, false, rowColor);
		
		return;
	}
	
	/*******************************
	 * Getter and Setter Functions *
	 *******************************/
	
	//
	public String getSplitName() {
		
		return splitNameString;
	}
	
	//
	public String getPB() {
		
		return pbHitString;
	}
	
	//
	public void setPB(String pbstring, String pbcum) {
		
		pbHitString = pbstring;
		String pbCumString = pbstring + "(" + pbcum + ")";
		setTextPaneAttributes(center, pBHitsPane, pbCumString, false, false, Color.WHITE);

		return;
	}
	
	//
	public String getHits() {
		
		return splitHitsString;
	}
	
	//
	public String getHitsFromPane() {
		
		return splitCurrentHitsPane.getText();
	}
	
	//
	public void setHits(String hits) {
		
		splitHitsString = hits;
		splitCurrentHitsPane.setText(hits);
		
		splitDiffString = getHitDifference(hits, pbHitString);
		splitHitDifferencePane.setText(splitDiffString);
		
		return;
	}
	
	//
	public Color getColor() {
		
		return rowColor;
	}
	
	//
	public void setColor(Color color) {
		
		rowColor = color;
		
		return;
	}
	
	/************************
	 * Overridden Functions *
	 ************************/

	@Override
	public void mouseClicked(MouseEvent e) {
		
		SplitRow thisRow = this; 
		
		if (e.getSource() instanceof JComponent) {
			
			Runnable runnable = new Runnable() {
				
				@Override
				public void run() {
					parentCounter.setCurrentSplitFromRow(thisRow);
					parentCounter.highlightRows(thisRow, null);
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
