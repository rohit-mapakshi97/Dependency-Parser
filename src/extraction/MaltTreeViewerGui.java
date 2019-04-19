//package se.vxu.msi.malteval.treeviewer;
//
//import java.awt.BorderLayout;
//import java.awt.Font;
//import java.awt.GraphicsEnvironment;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.ComponentEvent;
//import java.awt.event.ComponentListener;
//import java.util.HashSet;
//import java.util.TreeMap;
//import java.util.TreeSet;
//import java.util.Vector;
//
//import javax.swing.Box;
//import javax.swing.BoxLayout;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JOptionPane;
//import javax.swing.JPanel;
//import javax.swing.JSplitPane;
//
//import edu.umd.cs.piccolo.util.PAffineTransform;
//import edu.umd.cs.piccolo.util.PBounds;
//
//import se.vxu.msi.malteval.MaltEvalAPI;
//import se.vxu.msi.malteval.corpus.MaltTreebank;
//import se.vxu.msi.malteval.exceptions.MaltEvalException;
//import se.vxu.msi.malteval.treeviewer.core.CurrentSentenceList;
//import se.vxu.msi.malteval.treeviewer.gui.CurrentSentenceListComponent;
//import se.vxu.msi.malteval.treeviewer.gui.MaltTreeViewerMenu;
//import se.vxu.msi.malteval.treeviewer.gui.MaltTreeViewerToolBar;
//import se.vxu.msi.malteval.treeviewer.gui.NavigationPanel;
//import se.vxu.msi.malteval.treeviewer.gui.TreeViewer;
//import se.vxu.msi.malteval.treeviewer.gui.ViewMode;
//import se.vxu.msi.malteval.util.DataContainer;
//
//public class MaltTreeViewerGui extends JFrame implements ActionListener, ComponentListener {
//	private static final long serialVersionUID = 1L;
//
//	final private String versionName = "MaltEval Tree Viewer 1.0.1";
//	private static String fontFamily;
//
//	private JSplitPane jSplitPane;
//	private NavigationPanel navigationPanel;
//
//	private Vector<TreeViewer> treeViewers;
//
//	private Vector<MaltTreebank> parsedTreebanks;
//	private MaltTreebank gsTreebank;
//	private Vector<String> parsedFileNames;
//	private String gsFileName;
//	private Vector<DataContainer> dataContainersUAS;
//	private Vector<DataContainer> dataContainersLA;
//
//	private TreeSet<Integer> erroneousSentences;
//
//	private int currentSentence;
//	private int sentenceCount;
//
//	private TreeMap<String, JPanel> images;
//	private CurrentSentenceListComponent currentSentenceListComponent;
//
//	public MaltTreeViewerGui(String[] parsedFiles, String parsedFileFormat, String[] gsFiles, String gsFileFormat) throws MaltEvalException {
//		try {
//			if (gsFiles != null && gsFiles.length > 1) {
//				throw new MaltEvalException("MaltTreebankViewer does not support that more than one gold standard file is specified.", this
//						.getClass());
//			}
//			if ((gsFiles == null || gsFiles.length == 0) && (parsedFiles == null || parsedFiles.length == 0)) {
//				throw new MaltEvalException("At least one file (gold standard or parsed file) must be specified for MaltTreebankViewer",
//						this.getClass());
//			}
//			parsedTreebanks = new Vector<MaltTreebank>();
//			parsedFileNames = new Vector<String>();
//			dataContainersUAS = new Vector<DataContainer>();
//			dataContainersLA = new Vector<DataContainer>();
//			erroneousSentences = new TreeSet<Integer>();
//			fontFamily = null;
//			if (gsFiles != null) {
//				for (int i = 0; i < gsFiles.length; i++) {
//					gsTreebank = new MaltTreebank(gsFiles[i], gsFileFormat, null, null, true, true, true);
//					gsFileName = "Gold-standard:  " + gsFiles[i];
//					sentenceCount = gsTreebank.getSentenceCount();
//					findUsableFonts(gsTreebank);
//				}
//			}
//			if (parsedFiles != null) {
//				for (int i = 0; i < parsedFiles.length; i++) {
//					parsedTreebanks.add(new MaltTreebank(parsedFiles[i], parsedFileFormat, null, null, true, true, true));
//					parsedFileNames.add("Parsed " + (i + 1) + ":  " + parsedFiles[i]);
//					sentenceCount = parsedTreebanks.lastElement().getSentenceCount();
//					findUsableFonts(parsedTreebanks.lastElement());
//					if (gsTreebank != null) {
//						dataContainersUAS.add(MaltEvalAPI.simpleMaltEvalEvaluation(parsedTreebanks.lastElement(), gsTreebank, "UAS",
//								"Token", "accuracy"));
//						dataContainersLA.add(MaltEvalAPI.simpleMaltEvalEvaluation(parsedTreebanks.lastElement(), gsTreebank, "LA", "Token",
//								"accuracy"));
//						computeErroneousSentence();
//					} else {
//						dataContainersUAS.add(null);
//						dataContainersLA.add(null);
//					}
//				}
//
//			}
//			initGui();
//			currentSentenceListComponent = new CurrentSentenceListComponent(this);
////			navigationPanel.setLeftComponent(currentSentenceListComponent);
//
//			refresh();
//			setTitle("Sentence View");
//			setSize(1200, 800);
//			setVisible(true);
//			setDefaultCloseOperation(EXIT_ON_CLOSE);
//			changeSentence(0, null, true);
//		} catch (Exception e) {
//			JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//			e.printStackTrace();
//			throw new MaltEvalException(e.getMessage(), this.getClass());
//		}
//	}
//
//	public void computeErroneousSentence() throws MaltEvalException {
//		int i;
//		DataContainer dataContainers = MaltEvalAPI.simpleMaltEvalEvaluation(parsedTreebanks.lastElement(), gsTreebank, "LAS", "Sentence",
//				"exactmatch");
//		for (i = 0; i < dataContainers.getRowCount(); i++) {
//			if (((Integer) dataContainers.getEvaluationData(i, 0)) == 0 || ((Integer) dataContainers.getEvaluationData(i, 0)) == 0) {
//				erroneousSentences.add(Integer.parseInt(dataContainers.getRowHeaders(i)));
//			}
//		}
//	}
//
//	public void resetSentence() {
//		for (TreeViewer mediator : treeViewers) {
//			mediator.setCurrentSentence(currentSentence, null);
//		}
//	}
//
//	public void changeSentence(int sentenceId, HashSet<Integer> selectedWords, boolean updateSencenceList) {
//		if (0 <= sentenceId && sentenceId < sentenceCount) {
//			currentSentence = sentenceId;
//
//			for (TreeViewer treeViewer : treeViewers) {
//				treeViewer.setCurrentSentence(currentSentence, selectedWords);
//			}
//			if (updateSencenceList) {
//				double d = (((double) sentenceId) / sentenceCount)
//						* (currentSentenceListComponent.getVerticalScrollBar().getMaximum() - currentSentenceListComponent.getVerticalScrollBar().getMinimum());
//				currentSentenceListComponent.getVerticalScrollBar().setValue((int) d);
//			}
//		}
//	}
//
//	public void changeVerticalPosition(int value) {
//		for (TreeViewer mediator : treeViewers) {
//			mediator.setCurrentVericalBar(value);
//		}
//	}
//
//	public void changeHorizontalPosition(int value) {
//		for (TreeViewer mediator : treeViewers) {
//			mediator.setCurrentHorizontalBar(value);
//		}
//	}
//	
//	public void changeViewMode(int value) {
//		for (TreeViewer treeViewer : treeViewers) {
//			treeViewer.changeViewer(value);
//			treeViewer.setCurrentSentence(currentSentence, null);
//		}
//		int i = 0;
//		for (String imageName : images.keySet()) {
//			putImagesJPanel(imageName, getMediators().get(i).getViewMode().getJPanelToImage());
//			i++;
//		}
//	}
//	
//	public CurrentSentenceList getSentenceList() {
//		return treeViewers.lastElement().getSentenceList();
//	}
//	
//	public void changeViewPosition(PBounds viewPosition, ViewMode viewMode, boolean scale) {
//		for (TreeViewer mediator : treeViewers) {
//			if (mediator.getViewMode() != viewMode) {
//				mediator.getViewMode().changeViewPosition(viewPosition, scale);
//			}
//		}
//	}
//	
//	public void changeViewPositionByTransformation(PAffineTransform transformation, ViewMode viewMode) {
//		for (TreeViewer mediator : treeViewers) {
//			if (mediator.getViewMode() != viewMode) {
//				mediator.getViewMode().changeViewPositionByTransformation(transformation);
//			}
//		}
//	}
//
//	public void initGui() throws Exception {
////		add(new MaltTreeViewerToolBar(this), BorderLayout.NORTH);
////		jSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
////		jSplitPane.setDividerSize(3);
////		jSplitPane.setDividerLocation(300);
////		jSplitPane.addComponentListener(this);
////		add(jSplitPane);
//		JPanel jPanel = new JPanel();
//		jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));
//		treeViewers = new Vector<TreeViewer>();
//		images = new TreeMap<String, JPanel>();
//		
//		if (gsTreebank != null) {
//			treeViewers.add(new TreeViewer(this));
//			treeViewers.lastElement().importSentences(gsTreebank, null, null);
////			JLabel label = new JLabel(gsFileName);
//			jPanel.add(Box.createVerticalStrut(2));
////			jPanel.add(label);
//			jPanel.add(Box.createVerticalStrut(2));
//			jPanel.add(treeViewers.lastElement().getSplitPane());
//			images.put("Gold-standard", getMediators().lastElement().getViewMode().getJPanelToImage());
//		}
//		for (int i = 0; i < parsedTreebanks.size(); i++) {
//			treeViewers.add(new TreeViewer(this));
//			treeViewers.lastElement().importSentences(parsedTreebanks.get(i), dataContainersUAS.get(i), dataContainersLA.get(i));
//			JLabel label = new JLabel(parsedFileNames.get(i));
//			jPanel.add(Box.createVerticalStrut(2));
//			jPanel.add(label);
//			jPanel.add(Box.createVerticalStrut(2));
//			jPanel.add(treeViewers.lastElement().getSplitPane());
//			images.put("Parsed " + (i + 1), getMediators().lastElement().getViewMode());
//		}
////		navigationPanel = new NavigationPanel(this);
////		jSplitPane.setBottomComponent(navigationPanel);
////		jSplitPane.setTopComponent(jPanel);
////		setJMenuBar(new MaltTreeViewerMenu(this));
//                add(jPanel);
//		pack();
//		refresh();
//	}
//
//	public void actionPerformed(ActionEvent e) {
//	}
//
//	private void refresh() {
//		getContentPane().validate();
//		getContentPane().repaint();
//	}
//
//	public void componentHidden(ComponentEvent e) {
//	}
//
//	public void componentMoved(ComponentEvent e) {
//	}
//
//	public void componentResized(ComponentEvent e) {
//		if (getHeight() < 300) {
//			setSize(getWidth(), 300);
//		}
//		jSplitPane.setDividerLocation(jSplitPane.getHeight() - ((NavigationPanel) jSplitPane.getBottomComponent()).getMaxButtonHeight());
//	}
//
//	public void componentShown(ComponentEvent e) {
//	}
//
//	private void findUsableFonts(MaltTreebank treebank) {
//		int i, j;
//		if (fontFamily == null) {
//			Font[] allfonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
//			Vector<Font> usableFonts = new Vector<Font>();
//			for (Font font : allfonts) {
//				usableFonts.add(font);
//			}
//			for (i = 0; i < treebank.getSentenceCount(); i++) {
//				for (j = 0; j < usableFonts.size(); j++) {
//					if (usableFonts.get(j).canDisplayUpTo(treebank.getSentence(i).toString()) != -1) {
//						usableFonts.remove(j);
//						j--;
//					}
//				}
//			}
//			switch (usableFonts.size()) {
//			case 0:
//				fontFamily = "Arial";
//				return;
//			case 1:
//				fontFamily = usableFonts.get(0).getFamily();
//				return;
//			default:
//				for (Font font : usableFonts) {
//					if (font.getFamily().contains("Arial")) {
//						fontFamily = font.getFamily();
//						return;
//					}
//				}
//				fontFamily = usableFonts.get(0).getFamily();
//				return;
//			}
//		}
//	}
//	
//	public void putImagesJPanel(String imageName, JPanel jPanel) {
//		images.put(imageName, jPanel);
//	}
//	
//	public JPanel getImagesJPanel(String imageName) {
//		return images.get(imageName);
//	}
//
//	public static String getFontFamily() {
//		return fontFamily;
//	}
//
//	public TreeSet<Integer> getErroneousSentences() {
//		return erroneousSentences;
//	}
//
//	public int getCurrentSentence() {
//		return currentSentence;
//	}
//
//	public Vector<MaltTreebank> getParsedTreebanks() {
//		return parsedTreebanks;
//	}
//
//	public MaltTreebank getGsTreebank() {
//		return gsTreebank;
//	}
//
//	public Vector<TreeViewer> getMediators() {
//		return treeViewers;
//	}
//
//	public Vector<String> getParsedFileNames() {
//		return parsedFileNames;
//	}
//
//	public String getGsFileName() {
//		return gsFileName;
//	}
//
//	public int getSentenceCount() {
//		return sentenceCount;
//	}
//	
//	public CurrentSentenceListComponent getCurrentSentenceListComponent() {
//		return currentSentenceListComponent;
//	}
//}
