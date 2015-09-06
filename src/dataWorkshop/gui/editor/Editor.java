package dataWorkshop.gui.editor;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.MySocket;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingFactory;
import dataWorkshop.data.ViewTemplate;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.gui.ProjectPane;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.DataFieldTextPane;
import dataWorkshop.gui.data.view.InfoView;
import dataWorkshop.gui.data.view.SingleLineView;
import dataWorkshop.gui.data.view.TreeView;
import dataWorkshop.gui.dialogs.AboutDialog;
import dataWorkshop.gui.dialogs.ChooseEncodingDialog;
import dataWorkshop.gui.dialogs.ChooseSocketDialog;
import dataWorkshop.gui.dialogs.DataClipboardDialog;
import dataWorkshop.gui.dialogs.DataViewOptionDialog;
import dataWorkshop.gui.dialogs.DataViewQueryDialog;
import dataWorkshop.gui.dialogs.DiffDataDialog;
import dataWorkshop.gui.dialogs.EncodingConverterDialog;
import dataWorkshop.gui.dialogs.ErrorDialogFactory;
import dataWorkshop.gui.dialogs.ExportDataViewDialog;
import dataWorkshop.gui.dialogs.FindAndReplaceDialog;
import dataWorkshop.gui.dialogs.ListDialog;
import dataWorkshop.gui.dialogs.ManualDialog;
import dataWorkshop.gui.dialogs.PreferencesDialog;
import dataWorkshop.gui.dialogs.SocketDialog;
import dataWorkshop.gui.dialogs.StructureDefinitionPaletteDialog;
import dataWorkshop.gui.dialogs.TextClipboardDialog;
import dataWorkshop.gui.event.DataChangeEvent;
import dataWorkshop.gui.event.DataModelListener;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.gui.event.StateChangeListener;
import dataWorkshop.logging.Logger;
import dataWorkshop.xml.MyFont;
import dataWorkshop.xml.XMLSerializeFactory;
import dataWorkshop.xml.XMLSerializeable;

/**
 * <p>
 * DataWorkshop - a binary data editor 
 * <br>
 * Copyright (C) 2000, 2004  Martin Pape (martin.pape@gmx.de)
 * <br>
 * <br>
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * <br>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <br>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * </p>
 */
public class Editor extends JFrame implements ChangeListener, DataModelListener, LocaleStrings, StateChangeListener, XMLSerializeable
{
	public final static String CLASS_NAME = "Editor";

	public final static String SINGLE_LINE_VIEWS_TAG = "SingleLineViews";
	public final static String DATA_FIELD_EDITOR_ACTIONS_TAG = "DataFieldEditorActions";
	public final static String DATA_TREE_VIEW_ACTIONS_TAG = "DataTreeViewActions";

	public final static String DIALOGS_TAG = "Dialogs";

	public final static String WIDTH = "width";
	public final static String HEIGHT = "height";

	public final static String LOOK_AND_FEEL_TAG = "LookAndFeel";

	public final static String TRANSFORM_MENU_TAG = "TransformMenu";
	public final static String CALCULATE_MENU_TAG = "CalculateMenu";

	//Data Action
	public static String NEW_DATA_ACTION = "New Data";
	public static String OPEN_FILE_ACTION = "Open File";
	public static String OPEN_SOCKET_ACTION = "Open Socket";

	public static String SAVE_FILE_ACTION = "Save File";
	public static String SAVE_FILE_AS_ACTION = "Save File As ";
	public static String WRITE_SOCKET_ACTION = "Write to Socket";
	public static String CLOSE_PROJECT_ACTION = "Close";
	
	public static String IMPORT_DATA_ACTION = "Import Data";
	public static String EXPORT_DATA_ACTION = "Export Data";
	
	public static String EXIT_ACTION = "Exit";

	//Edit Menu
	public static String EDIT = "Edit";
	public static String UNDO_ACTION = "Undo";
	public static String REDO_ACTION = "Redo";
	public static String CUT_ACTION = "Cut";
	public static String COPY_ACTION = "Copy";
	public static String PASTE_ACTION = "Paste";
	public static String DELETE_ACTION = "Delete";
	public static String UNSELECT_ACTION = "Unselect";
	public static String SELECT_ALL_ACTION = "Select All";
	public static String DEFINE_FIND_AND_REPLACE_ACTION = "Define Find & Replace";
	public static String FIND_NEXT_ACTION = "Find Next";
	public static String FIND_PREVIOUS_ACTION = "Find Previous";
	public static String FIND_ALL_ACTION = "Find All";
	public static String REPLACE_ACTION = "Replace";
	public static String REPLACE_ALL_ACTION = "Replace All";

	//Tools Menu
	public static String TOOLS = "Tools";
	public static String DIFF_DATA_ACTION = "Diff Data";
	public static String DATA_CLIPBOARD_ACTION = "Data Clipboard";
	public static String ENCODING_CONVERTER_ACTION = "Encoding Converter";
	public static String TEXT_CLIPBOARD_ACTION = "Text Clipboard";
	public static String STRUCTURE_PALETTE_ACTION = "Structure Definition Palette";
	public static String PREFERENCES_ACTION = "Preferences";

	//Data Filter Menu
	public static String DATA_TRANSFORMATIONS = "Transformations";
	public static String DATA_CALCULATIONS = "Calculations";

	//View Menu
	public static String DATA_VIEWS = "Views";
	public static String EXPORT_DATA_VIEW_ACTION = "Export View";
	public static String EXPORT_DATA_VIEW_AS_ACTION = "Export View As";
	public static String DATA_VIEW_QUERY_ACTION = "Query View";
	public static String CONFIGURE_DATA_VIEW_ACTION = "Configure View";

	public static String OPEN_VIEW_DEFINITION_ACTION = "Open View Definition";
	public static String SAVE_VIEW_DEFINITION_AS_ACTION = "Save View Definition As";
	public static String CLONE_VIEW_DEFINITION_ACTION = "Clone View Definition";

	public static String NEW_STRUCTURE_ACTION = "New Structure";
	public static String OPEN_STRUCTURE_ACTION = "Open Structure";
	public static String SAVE_STRUCTURE_ACTION = "Save Structure";
	public static String SAVE_STRUCTURE_AS_ACTION = "Save Structure As";
	public static String COMPILE_STRUCTURE_ACTION = "Compile Structure";

	//Help Menu
	public static String HELP = "Help";
	public static String USER_MANUAL_ACTION = "User Manual";
	public static String ABOUT_ACTION = "About";

	//Submenus
	static String BYTE_ORDER = "Change Byte Order";
	static String BIT_OPERATION = "Bit Operation";
	static String ASCII_OPERATION = "Ascii";
	static String FILL_OPERATION = "Fill";
	static String ARITHMETIC = "Arithmetic";
	static String OTHER = "Other";
	static String CHECKSUM = "Checksum";
	static String CONVERSION = "Conversion";

	DataTransformationMenu transformerMenu = new DataTransformationMenu();
	{
		transformerMenu.add(ARITHMETIC, "inc.xml");
		transformerMenu.add(ARITHMETIC, "dec.xml");
		transformerMenu.add(ASCII_OPERATION, "rot13Encoder.xml");
		transformerMenu.add(ASCII_OPERATION, "rot13Decoder.xml");
		transformerMenu.add(ASCII_OPERATION, "toUpperCase.xml");
		transformerMenu.add(ASCII_OPERATION, "asciiToUnicode.xml");
		transformerMenu.add(BIT_OPERATION, "rotateRight.xml");
		transformerMenu.add(BIT_OPERATION, "rotateLeft.xml");
		transformerMenu.add(BIT_OPERATION, "shiftRight.xml");
		transformerMenu.add(BIT_OPERATION, "shiftLeft.xml");
		transformerMenu.add(BIT_OPERATION, "not.xml");
		transformerMenu.add(BIT_OPERATION, "negate.xml");
		transformerMenu.add(BYTE_ORDER, "swapWord.xml");
		transformerMenu.add(BYTE_ORDER, "swapDWord.xml");
		transformerMenu.add(BYTE_ORDER, "swapQWord.xml");
		transformerMenu.add(FILL_OPERATION, "fillWithZeros.xml");
		transformerMenu.add(FILL_OPERATION, "fillWithOnes.xml");
	};

	DataTransformationMenu calculatorsMenu = new DataTransformationMenu();
	{
		calculatorsMenu.add(CHECKSUM, "crc32.xml");
		calculatorsMenu.add(CONVERSION, "base64Decode.xml");
		calculatorsMenu.add(CONVERSION, "base64Encode.xml");
		calculatorsMenu.add(CONVERSION, "hexDecode.xml");
		calculatorsMenu.add(CONVERSION, "hexEncode.xml");
		calculatorsMenu.add(CONVERSION, "binaryDecode.xml");
		calculatorsMenu.add(CONVERSION, "binaryEncode.xml");
	}

	/**
	 *  Singelton Pattern
	 */
	private static Editor instance;

	ArrayList openSockets = new ArrayList();

	JTabbedPane projectsPane;

	/**
	 *  Fonts
	 */
	MyFont font;

	DataEncoding[] singleLineConverters = { DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED)};

	transient InfoView infoView;
	transient SingleLineView[] singleLineViews;
	transient JProgressBar progressBar;
	transient JPanel statusBarPane;

	/*
	 *  This is only needed for rebuild
	 *  In workbench.xml the number of singleLineViews is stored as the length of the DataConverter array
	 */
	transient int numberSingleLineViews;

	JFileChooser fileChooser = new JFileChooser();

	javax.swing.filechooser.FileFilter standardFilter = fileChooser.getFileFilter();
	transient ExtensionFileFilter strucFilter;

	transient private HashMap actions = new HashMap();
	transient private HashMap transformerActions = new HashMap();
	transient private HashMap calculatorsActions = new HashMap();
	transient private static JPopupMenu editorPopup = null;

	private ImageIcon ERROR_ICON = new ImageIcon("images/error16x16.gif");

	private Logger logger;

	/******************************************************************************
	 *	Constructors
	 */
	public Editor()
	{
		super();
		logger = Logger.getLogger(this.getClass());
		font = new MyFont(new Font("monospaced", Font.PLAIN, 12));
	}

	/******************************************************************************
	 *	ChangeListener
	 */
	//The active tab has changed
	public void stateChanged(ChangeEvent e)
	{
		//ProjectPane p = getActiveProject();
		//projectsPane.setTitleAt(projectsPane.getSelectedIndex(), p.getShortName());
		DataModel dataModel = getActiveDataModel();
		if (dataModel == null)
		{
			dataModel = new DataModel();
		}
		infoView.setDataModel(dataModel);
		for (int i = 0; i < singleLineViews.length; i++)
		{
			singleLineViews[i].setDataModel(dataModel);
		}
		updateProgressBar();
		updateActions();
		updateTitle();
	}

	/******************************************************************************
	 *	SelectionListener Interface
	 */
	public void selectionChanged(DataSelectionEvent e)
	{
		updateActions();
	}

	public void dataChanged(DataChangeEvent e)
	{
		updateActions();
	}

	/******************************************************************************
	 *	StateChangeListener Interface
	 */
	public void stateChanged(StateChangeEvent e)
	{
		if (e.getSource() instanceof DataModel)
		{
			updateActions();
			updateTitle();
		}
		else if (e.getSource() instanceof ProjectPane)
		{
			updateProgressBar();
		}
	}

	/******************************************************************************
	 *	XMLSerializable Interface
	 */
	public String getClassName()
	{
		return CLASS_NAME;
	}

	public void serialize(Element context)
	{
		Dimension dim = getSize();
		XMLSerializeFactory.setAttribute(context, WIDTH, dim.getWidth());
		XMLSerializeFactory.setAttribute(context, HEIGHT, dim.getHeight());

		XMLSerializeFactory.serialize(context, LOOK_AND_FEEL_TAG, UIManager.getLookAndFeel().getClass().getName());
		XMLSerializeFactory.serialize(context, font);
		XMLSerializeFactory.serialize(context, SINGLE_LINE_VIEWS_TAG, getSingleLineDataConverters());

		//
		//  Windows
		//
		Element windowsElement = XMLSerializeFactory.addElement(context, DIALOGS_TAG);
		XMLSerializeFactory.serialize(windowsElement, AboutDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, DataClipboardDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, DataViewQueryDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, DiffDataDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, EncodingConverterDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, ExportDataViewDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, FindAndReplaceDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, ManualDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, PreferencesDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, StructureDefinitionPaletteDialog.getInstance());
		XMLSerializeFactory.serialize(windowsElement, TextClipboardDialog.getInstance());

		XMLSerializeFactory.serialize(context, TRANSFORM_MENU_TAG, transformerMenu);
		XMLSerializeFactory.serialize(context, CALCULATE_MENU_TAG, calculatorsMenu);

		XMLSerializeFactory.serialize(context, DATA_FIELD_EDITOR_ACTIONS_TAG, DataFieldTextPane.getKeyBindings());
		XMLSerializeFactory.serialize(context, DATA_TREE_VIEW_ACTIONS_TAG, TreeView.getKeyBindings());
	}

	public void deserialize(Element context)
	{
		setSize(new Dimension((int) XMLSerializeFactory.getAttributeAsDouble(context, WIDTH), (int) XMLSerializeFactory.getAttributeAsDouble(context, HEIGHT)));
		setLookAndFeel(XMLSerializeFactory.deserializeAsString(context));
		font = (MyFont) XMLSerializeFactory.deserializeFirst(context);
		singleLineConverters = (DataEncoding[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, SINGLE_LINE_VIEWS_TAG)).toArray(new DataEncoding[0]);

		//
		//  Windows
		//
		Element windowsElement = XMLSerializeFactory.getElement(context, DIALOGS_TAG);

		XMLSerializeFactory.deserializeFirst(windowsElement, AboutDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, DataClipboardDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, DataViewQueryDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, DiffDataDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, EncodingConverterDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, ExportDataViewDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, FindAndReplaceDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, ManualDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, PreferencesDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, StructureDefinitionPaletteDialog.getInstance());
		XMLSerializeFactory.deserializeFirst(windowsElement, TextClipboardDialog.getInstance());

		//We have to deserialize the Menus before the dialogs because the dialogs use the menus
		transformerMenu = (DataTransformationMenu) XMLSerializeFactory.deserializeFirst(context, TRANSFORM_MENU_TAG);
		calculatorsMenu = (DataTransformationMenu) XMLSerializeFactory.deserializeFirst(context, CALCULATE_MENU_TAG);

		DataFieldTextPane.setKeyBindings((MyKeyBinding[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, DATA_FIELD_EDITOR_ACTIONS_TAG)).toArray(new MyKeyBinding[0]));
		TreeView.setKeyBindings((MyKeyBinding[]) Arrays.asList(XMLSerializeFactory.deserializeAll(context, DATA_TREE_VIEW_ACTIONS_TAG)).toArray(new MyKeyBinding[0]));
	}

	/******************************************************************************
	 *	Public Methods
	 */
	public static Editor getInstance()
	{
		if (instance == null)
		{
			instance = new Editor();
		}
		return instance;
	}

	/**
	 *  This code is usually in the Constructor. We moved it here to make it possible to change
	 *  the variables before doing the init stuff. Usage is: getInstance(), then do
	 *  a deseserialization if necessary, then do init() and build the editor.
	 *
	 * By keeping this code in the Constructor we would have done the editor building twice.
	 * Once by getInstance() then after deserialization by doing a rebuild of the editor.
	 *
	 * We cannot use a different constructor (Editor() and Editor(XML stuff) ) while deserializing
	 * because some classes we instanciate need to access Editor.getInstance().
	 *
	 *  MPA 2002-07-26 (horrible explanation)
	 *
	 *  !! This must only be called once
	 */
	public void build()
	{
		DataModel dataModel = new DataModel();

		addAction(ActionFactory.createOpenSocket());
		addAction(ActionFactory.createNewData());
		addAction(ActionFactory.createOpenFile());
		addAction(ActionFactory.createSaveFile());
		addAction(ActionFactory.createSaveFileAs());
		addAction(ActionFactory.createCloseProject());
		addAction(ActionFactory.createExit());
		addAction(ActionFactory.createWriteToSocket());

		addAction(ActionFactory.createDefineFindAndReplace());
		addAction(ActionFactory.createFindNext());
		addAction(ActionFactory.createFindPrevious());
		addAction(ActionFactory.createFindAll());
		addAction(ActionFactory.createReplace());
		addAction(ActionFactory.createReplaceAll());

		addAction(ActionFactory.createUndo());
		addAction(ActionFactory.createRedo());
		addAction(ActionFactory.createCut());
		addAction(ActionFactory.createCopy());
		addAction(ActionFactory.createPaste());
		addAction(ActionFactory.createDelete());
		addAction(ActionFactory.createUnselect());
		addAction(ActionFactory.createSelectAll());

		addAction(ActionFactory.createOpenViewDefinition());
		addAction(ActionFactory.createSaveViewDefinitionAs());

		addAction(ActionFactory.createAddNewStructure());
		addAction(ActionFactory.createOpenStructure());
		addAction(ActionFactory.createSaveStructure());
		addAction(ActionFactory.createSaveStructureAs());
		addAction(ActionFactory.createCompileStructure());

		addAction(ActionFactory.createExportDataView());
		addAction(ActionFactory.createExportDataAsView());
		addAction(ActionFactory.createCloneView());
		addAction(ActionFactory.createQueryView());

		addAction(ActionFactory.createPreferences());
		addAction(ActionFactory.createConfigureDataView());

		addAction(ActionFactory.createDiffData());
		addAction(ActionFactory.createDataClipboard());
		addAction(ActionFactory.createEncodingConverter());
		addAction(ActionFactory.createImportData());
		addAction(ActionFactory.createExportData());
		addAction(ActionFactory.createTextClipboard());
		addAction(ActionFactory.createStructurePalette());

		addAction(ActionFactory.createUserManual());
		addAction(ActionFactory.createAbout());

		strucFilter = new ExtensionFileFilter(DataWorkshop.STRUCTURE_EXTENSION, "View Structures");

		//
		//Create the menu bar
		//
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		//File menu
		JMenu menu = new JMenu(DATA);
		menuBar.add(menu);
		addActionToMenu(menu, NEW_DATA_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, OPEN_FILE_ACTION);
		addActionToMenu(menu, OPEN_SOCKET_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, SAVE_FILE_ACTION);
		addActionToMenu(menu, SAVE_FILE_AS_ACTION);
		addActionToMenu(menu, WRITE_SOCKET_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, CLOSE_PROJECT_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, IMPORT_DATA_ACTION);
		addActionToMenu(menu, EXPORT_DATA_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, EXIT_ACTION);

		//Edit menu
		menu = new JMenu(EDIT);
		menuBar.add(menu);
		addActionToMenu(menu, UNDO_ACTION);
		addActionToMenu(menu, REDO_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, CUT_ACTION);
		addActionToMenu(menu, COPY_ACTION);
		addActionToMenu(menu, PASTE_ACTION);
		addActionToMenu(menu, DELETE_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, UNSELECT_ACTION);
		addActionToMenu(menu, SELECT_ALL_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, DEFINE_FIND_AND_REPLACE_ACTION);
		addActionToMenu(menu, FIND_NEXT_ACTION);
		addActionToMenu(menu, FIND_PREVIOUS_ACTION);
		addActionToMenu(menu, FIND_ALL_ACTION);
		addActionToMenu(menu, REPLACE_ACTION);
		addActionToMenu(menu, REPLACE_ALL_ACTION);

		menuBar.add(createTransformersMenu(dataModel, transformerActions));
		menuBar.add(createCalculatorsMenu(dataModel, calculatorsActions));

		//View menu
		menu = new JMenu(DATA_VIEWS);
		menuBar.add(menu);
		addActionToMenu(menu, EXPORT_DATA_VIEW_ACTION);
		addActionToMenu(menu, EXPORT_DATA_VIEW_AS_ACTION);
		addActionToMenu(menu, DATA_VIEW_QUERY_ACTION);
		addActionToMenu(menu, CONFIGURE_DATA_VIEW_ACTION);
		menu.addSeparator();
		addActionToMenu(menu , OPEN_VIEW_DEFINITION_ACTION);
		addActionToMenu(menu, SAVE_VIEW_DEFINITION_AS_ACTION);
		addActionToMenu(menu, CLONE_VIEW_DEFINITION_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, NEW_STRUCTURE_ACTION);
		addActionToMenu(menu, OPEN_STRUCTURE_ACTION);
		addActionToMenu(menu, SAVE_STRUCTURE_ACTION);
		addActionToMenu(menu, SAVE_STRUCTURE_AS_ACTION);
		addActionToMenu(menu, COMPILE_STRUCTURE_ACTION);

		//Tools Menu
		menu = new JMenu(TOOLS);
		menuBar.add(menu);
		addActionToMenu(menu, DATA_CLIPBOARD_ACTION);
		addActionToMenu(menu, TEXT_CLIPBOARD_ACTION);
		addActionToMenu(menu, ENCODING_CONVERTER_ACTION);
		addActionToMenu(menu, STRUCTURE_PALETTE_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, DIFF_DATA_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, PREFERENCES_ACTION);

		//Force HelpMenu to right border
		menuBar.add(Box.createHorizontalGlue());

		//Build Help menu
		menu = new JMenu(HELP);
		menuBar.add(menu);
		addActionToMenu(menu, USER_MANUAL_ACTION);
		menu.addSeparator();
		addActionToMenu(menu, ABOUT_ACTION);

		projectsPane = new JTabbedPane();
		getContentPane().add(projectsPane, BorderLayout.CENTER);
		projectsPane.addChangeListener(this);

		infoView = new InfoView(dataModel);
		infoView.setEnabled(false);

		progressBar = new JProgressBar(JProgressBar.HORIZONTAL, 0, 0);
		progressBar.setStringPainted(true);
		progressBar.setValue(0);
		progressBar.setVisible(false);

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		bottom.add(progressBar);
		bottom.add(Box.createRigidArea(new Dimension(12, 0)));
		bottom.add(infoView);

		statusBarPane = new JPanel();
		statusBarPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		statusBarPane.setLayout(new BoxLayout(statusBarPane, BoxLayout.Y_AXIS));
		singleLineViews = new SingleLineView[singleLineConverters.length];
		for (int i = 0; i < singleLineViews.length; i++)
		{
			singleLineViews[i] = new SingleLineView(singleLineConverters[i], dataModel, true);
			singleLineViews[i].setEnabled(false);
			statusBarPane.add(singleLineViews[i]);
		}
		statusBarPane.add(Box.createRigidArea(new Dimension(0, 6)));
		statusBarPane.add(bottom);
		getContentPane().add(statusBarPane, BorderLayout.SOUTH);

		updateActions();
		updateTitle();
	}

	public Font getPlainFont()
	{
		return font.getFont().deriveFont(Font.PLAIN);
	}

	public Font getBoldFont()
	{
		return font.getFont().deriveFont(Font.BOLD);
	}

	public ImageIcon getErrorIcon()
	{
		return ERROR_ICON;
	}

	/*
	 *  Return EditorPopup with actions linked to this.dataModel
	 */
	public JPopupMenu getEditorPopup()
	{
		DataModel dataModel = getActiveDataModel();
		if (editorPopup == null)
		{
			editorPopup = new JPopupMenu();
			editorPopup.add(getAction(UNDO_ACTION));
			editorPopup.add(getAction(REDO_ACTION));
			editorPopup.addSeparator();
			editorPopup.add(getAction(CUT_ACTION));
			editorPopup.add(getAction(COPY_ACTION));
			editorPopup.add(getAction(PASTE_ACTION));
			editorPopup.add(getAction(DELETE_ACTION));
			editorPopup.addSeparator();
			editorPopup.add(getAction(UNSELECT_ACTION));
			editorPopup.add(getAction(SELECT_ALL_ACTION));
			editorPopup.addSeparator();
			editorPopup.add(createTransformersMenu(dataModel, transformerActions));
			editorPopup.add(createCalculatorsMenu(dataModel, calculatorsActions));
		}
		return editorPopup;
	}

	/*
	 *  Return new PopupMenu
	 */
	public JPopupMenu createEditorPopup(HashMap actions, DataModel dataModel)
	{
		JPopupMenu popupMenu = new JPopupMenu();
		Action a;
		a = ActionFactory.createUndo();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		a = ActionFactory.createRedo();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		popupMenu.addSeparator();
		a = ActionFactory.createCut();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		a = ActionFactory.createCopy();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		a = ActionFactory.createPaste();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		a = ActionFactory.createDelete();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		popupMenu.addSeparator();

		a = ActionFactory.createUnselect();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		a = ActionFactory.createSelectAll();
		((DataModelAction) a).setDataModel(dataModel);
		actions.put(a.getValue(Action.NAME), a);
		popupMenu.add(a);
		popupMenu.addSeparator();

		JMenu subMenu = new JMenu(DATA_TRANSFORMATIONS);
		popupMenu.add(subMenu);
		transformerMenu.populateTransformerMenu(subMenu, dataModel, actions);

		subMenu = new JMenu(DATA_CALCULATIONS);
		popupMenu.add(subMenu);
		calculatorsMenu.populateTransformerMenu(subMenu, dataModel, actions);

		return popupMenu;
	}

	public void fireActionEvent(String action)
	{
		getAction(action).actionPerformed(new ActionEvent(this, 0, action));
	}

	public DataEncoding[] getSingleLineDataConverters()
	{
		DataEncoding[] converters = new DataEncoding[singleLineViews.length];
		for (int i = 0; i < singleLineViews.length; i++)
		{
			converters[i] = singleLineViews[i].getDataConverter();
		}
		return converters;
	}

	public DataModel getActiveDataModel()
	{
		if (getSelectedProject() == null)
		{
			return null;
		}
		return getSelectedProject().getDataModel();
	}

	public void setLookAndFeel(String lookAndFeelClassname)
	{
		try
		{
			UIManager.setLookAndFeel(lookAndFeelClassname);
			SwingUtilities.updateComponentTreeUI(this);
			if (editorPopup != null)
			{
				SwingUtilities.updateComponentTreeUI(editorPopup);
			}
			fileChooser = new JFileChooser();
		}
		catch (Exception e)
		{
			ErrorDialogFactory.show(LOOK_AND_FEEL_ERROR, e, this);
		}
	}

	public void rebuild()
	{
		Container contentPane = getContentPane();
		infoView.setDataModel(new DataModel());
		for (int i = 0; i < singleLineViews.length; i++)
		{
			singleLineViews[i].setDataModel(new DataModel());
		}
		contentPane.remove(statusBarPane);

		DataModel dataModel = getActiveDataModel();
		boolean enabled = true;
		if (dataModel == null)
		{
			dataModel = new DataModel();
			enabled = false;
		}
		infoView = new InfoView(dataModel);
		infoView.setEnabled(enabled);
		progressBar = new JProgressBar();

		JPanel bottom = new JPanel();
		bottom.setLayout(new BoxLayout(bottom, BoxLayout.X_AXIS));
		bottom.add(progressBar);
		bottom.add(Box.createRigidArea(new Dimension(12, 0)));
		bottom.add(infoView);

		DataEncoding[] oldConverters = getSingleLineDataConverters();
		DataEncoding[] converters = new DataEncoding[numberSingleLineViews];
		if (converters.length > oldConverters.length)
		{
			for (int i = 0; i < oldConverters.length; i++)
			{
				converters[i] = oldConverters[i];
			}
			for (int i = oldConverters.length; i < converters.length; i++)
			{
				converters[i] = DataEncodingFactory.getInstance().get(DataEncodingFactory.HEX_8_UNSIGNED);
			}
		}
		else
		{
			for (int i = 0; i < converters.length; i++)
			{
				converters[i] = oldConverters[i];
			}
		}

		statusBarPane = new JPanel();
		statusBarPane.setLayout(new BoxLayout(statusBarPane, BoxLayout.Y_AXIS));
		statusBarPane.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0));
		singleLineViews = new SingleLineView[converters.length];
		for (int i = 0; i < singleLineViews.length; i++)
		{
			singleLineViews[i] = new SingleLineView(converters[i], dataModel, true);
			singleLineViews[i].setEnabled(enabled);
			statusBarPane.add(singleLineViews[i]);
		}
		statusBarPane.add(Box.createRigidArea(new Dimension(0, 6)));
		statusBarPane.add(bottom);
		contentPane.add(statusBarPane, BorderLayout.SOUTH);

		AboutDialog.rebuild();
		ChooseEncodingDialog.rebuild();
		ChooseSocketDialog.rebuild();
		DataClipboardDialog.rebuild();
		DataViewOptionDialog.rebuild();
		DataViewQueryDialog.rebuild();
		DiffDataDialog.rebuild();
		EncodingConverterDialog.rebuild();
		ExportDataViewDialog.rebuild();
		FindAndReplaceDialog.rebuild();
		ListDialog.rebuild();
		PreferencesDialog.rebuild();
		ManualDialog.rebuild();
		PreferencesDialog.rebuild();
		SocketDialog.rebuild();
		StructureDefinitionPaletteDialog.rebuild();
		TextClipboardDialog.rebuild();

		ProjectPane p;
		for (int i = 0; i < projectsPane.getComponentCount(); i++)
		{
			p = (ProjectPane) projectsPane.getComponentAt(i);
			p.rebuild();
		}

		updateProgressBar();
		forceRepaint();
	}

	public void setNumberSingleLineViews(int number)
	{
		this.numberSingleLineViews = number;
	}

	public int getFontSize()
	{
		return font.getFont().getSize();
	}

	public void setFontSize(int fontSize)
	{
		Font oldFont = font.getFont();
		font = new MyFont(new Font(oldFont.getName(), oldFont.getStyle(), fontSize));
	}

	/******************************************************************************
	 *	Protected Methods
	 */
	protected void addSocket(MySocket socket)
	{
		openSockets.add(socket);
	}

	protected MySocket[] getOpenSockets()
	{
		return (MySocket[]) openSockets.toArray(new MySocket[0]);
	}

	protected void removeSocket(MySocket socket)
	{
		openSockets.remove(socket);
	}

	protected void removeProjectPane(ProjectPane p)
	{
		p.getDataModel().removeDataModelListener(this);
		p.getDataModel().removeStateChangeListener(this);
		p.removeStateChangeListener(this);
		projectsPane.remove(p);
		/**
		 *	:KLUDGE:Martin Pape:May 4, 2003 
		 *	We have to generate a stateChanged event here so all the updates are done correctly
		 *  (as a new tab is automatically selected if we remove the currently active tab). Why is this
		 *  not done by the projectsPane object ? 
		 */
		projectsPane.setSelectedIndex(projectsPane.getSelectedIndex());
		if (projectsPane.getComponentCount() == 0)
		{
			infoView.setEnabled(false);
			for (int i = 0; i < singleLineViews.length; i++)
			{
				singleLineViews[i].setEnabled(false);
			}
		}
	}

	/**
	 *  Add ProjectPane
	 *  and make active
	 */
	protected void addProjectPane(ProjectPane p)
	{
		int count = projectsPane.getComponentCount();
		p.getDataModel().addStateChangeListener(this);
		p.getDataModel().addDataModelListener(this);
		p.addStateChangeListener(this);
		projectsPane.add(p.getShortName(), p);
		projectsPane.setSelectedComponent(p);
		if (count == 0)
		{
			/**
			 *  If this is the first project added we need to validate editor so the bounds on projectPane
			 *  are set correctly. ProjectPane uses its bounds to calculate the size of each view.
			 */
			validate();
			infoView.setEnabled(true);
			for (int i = 0; i < singleLineViews.length; i++)
			{
				singleLineViews[i].setEnabled(true);
			}
		}
	}

	protected ProjectPane getSelectedProject()
	{
		return (ProjectPane) projectsPane.getSelectedComponent();
	}
	
	protected ProjectPane[] getProjects()
	{
		Component[] c = projectsPane.getComponents();
		ProjectPane[] projects = new ProjectPane[c.length];
		for (int i = 0; i < c.length; i++)
		{
			projects[i] = (ProjectPane) c[i];
		}
		return projects;
	}

	protected void setSelectedProject(ProjectPane p)
	{
		projectsPane.setSelectedComponent(p);
	}

	protected File openStructure()
	{
		return open(DataWorkshop.getInstance().getFile(DataWorkshop.STRUCTURE_DIR), strucFilter, STRUC_OPEN_DIALOG);
	}

	protected File saveStructure(File dir)
	{
		return save(dir, strucFilter, STRUC_SAVE_DIALOG);
	}

	protected File openFile()
	{
		return open(DataWorkshop.getInstance().getFile(DataWorkshop.WORKING_DIR), standardFilter, FILE_OPEN_DIALOG);
	}

	protected File saveFile()
	{
		return save(DataWorkshop.getInstance().getFile(DataWorkshop.WORKING_DIR), standardFilter, FILE_SAVE_DIALOG);
	}

	protected File saveFile(File dir)
	{
		return save(dir, standardFilter, FILE_SAVE_DIALOG);
	}

	protected StreamResult transformDocument(Document doc, Transformer transformer)
	{
		try
		{
			long start = System.currentTimeMillis();
			DOMSource source = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			transformer.transform(source, result);
			logger.profile("XLS - Transformation", start);
			return result;
		}
		catch (TransformerException e)
		{
			System.out.println(e);
		}
		return null;
	}

	protected boolean writeToFile(File file, String s)
	{
		try
		{
			FileWriter out = new FileWriter(file);
			out.write(s);
			out.close();
		}
		catch (IOException e)
		{
			logger.warning(ERROR_SAVE_FILE + " " + file);
			return false;
		}
		return true;
	}

	protected int showYesNoCancelDialog(String text)
	{
		return JOptionPane.showConfirmDialog(this, text, DATA_WORKSHOP, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	}

	protected void forceRepaint()
	{
		getRootPane().revalidate();
		getRootPane().repaint();
	}

	protected RootStatement[] chooseRootStatements(ViewTemplate[] signatures) {
		ViewTemplate signature;
		if (signatures.length == 1)
		{
			signature = signatures[0];
		}
		else
		{
			ListDialog dialog = ListDialog.getInstance();
			signature = (ViewTemplate) dialog.show(this, "Choose View Template", signatures, signatures[0]);
		}

		ArrayList openStructures = new ArrayList();
		String[] structureNames = signature.getStructures();
		for (int i = 0; i < structureNames.length; i++)
		{
			File file = new File(DataWorkshop.getInstance().getFile(DataWorkshop.STRUCTURE_DIR), structureNames[i]);
			Object o = XMLSerializeFactory.getInstance().deserialize(file);
			if (o != null && o instanceof RootStatement)
			{
				((RootStatement) o).setFile(file);
				openStructures.add(o);
			}
			else
			{
				ErrorDialogFactory.show(OPEN_STRUCTURE_MESSAGE.format(new Object[] { file }), this);
			}
		}
		return (RootStatement[]) openStructures.toArray(new RootStatement[0]);
	}

	/******************************************************************************
	 *	Private Methods
	 */
	private JMenu createTransformersMenu(DataModel dataModel, HashMap actions)
	{
		JMenu menu = new JMenu(DATA_TRANSFORMATIONS);
		transformerMenu.populateTransformerMenu(menu, dataModel, actions);
		return menu;
	}

	private JMenu createCalculatorsMenu(DataModel dataModel, HashMap actions)
	{
		JMenu menu = new JMenu(DATA_CALCULATIONS);
		calculatorsMenu.populateCalculatorsMenu(menu, dataModel, actions);
		return menu;
	}

	private Action getAction(String s)
	{
		return (Action) actions.get(s);
	}

	private File open(File dir, javax.swing.filechooser.FileFilter filter, String title)
	{
		File file = fileDialog(dir, filter, title, "Open");

		if (file == null)
		{
			return null;
		}

		if (!file.canRead())
		{
			JOptionPane.showMessageDialog(this, "Could not read " + file.getAbsolutePath(), "ERROR", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		return file;
	}

	private File save(File dir, javax.swing.filechooser.FileFilter filter, String title)
	{
		File file = fileDialog(dir, filter, title, "Save");

		if (file == null)
		{
			return null;
		}

		if (file.exists())
		{
			if (!file.canWrite())
			{
				JOptionPane.showMessageDialog(this, "Could not write to " + file.getAbsolutePath(), "ERROR", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		else
		{
			File parent = file.getParentFile();
			if (!parent.canWrite())
			{
				JOptionPane.showMessageDialog(this, "Could not write to " + file.getAbsolutePath(), "ERROR", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}

		return file;
	}

	/**
	 *	File Dialog
	 *
	 *	@param Object to write, the Directory, Extensions, DialogTitle
	 *	@return the new Directory
	 *
	 *	Open a SaveFileChooserDialog.
	 *	Save serialized Object under choosen name.
	 *
	 */
	private File fileDialog(File dir, javax.swing.filechooser.FileFilter filter, String title, String buttonText)
	{
		fileChooser.setCurrentDirectory(dir);
		if (dir.isDirectory())
		{
			fileChooser.setSelectedFile(null);
		}
		fileChooser.setFileFilter(filter);
		fileChooser.setDialogTitle(title);
		fileChooser.rescanCurrentDirectory();
		int i = fileChooser.showDialog(this, buttonText);
		if (i == JFileChooser.APPROVE_OPTION)
		{
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	private void updateTitle()
	{
		ProjectPane project = getSelectedProject();
		String name;
		if (project != null)
		{
			name = project.getName();
		}
		else
		{
			name = DATA_WORKSHOP + " " + VERSION;
		}
		setTitle(name);
	}

	private void updateProgressBar()
	{
		ProjectPane p = getSelectedProject();
		if (p != null)
		{
			progressBar.setString(p.getStatus());
			int max = p.getProgressMaximum();
			int value = p.getProgressValue();
			progressBar.setMaximum(max);
			progressBar.setValue(value);
			if (value >= max)
			{
				progressBar.setVisible(false);
			}
			else
			{
				progressBar.setVisible(true);
			}
		}
		else
		{
			progressBar.setVisible(false);
		}
	}

	private void updateActions()
	{
		Iterator it = actions.values().iterator();
		Action a;
		ProjectPane project = getSelectedProject();
		DataModel dataModel = null;
		if (project != null)
		{
			dataModel = project.getDataModel();
		}
		while (it.hasNext())
		{
			a = (Action) it.next();
			if (a instanceof DataModelAction)
			{
				((DataModelAction) a).setDataModel(dataModel);
			}
			else if (a instanceof ProjectAction)
			{
				((ProjectAction) a).setProjectPane(project);
			}
		}

		it = transformerActions.values().iterator();
		while (it.hasNext())
		{
			((TransformerAction) it.next()).setDataModel(dataModel);
		}

		it = calculatorsActions.values().iterator();
		while (it.hasNext())
		{
			((CalculatorAction) it.next()).setDataModel(dataModel);
		}
	}

	private void addActionToMenu(JMenu menu, String action)
	{
		Action a = getAction(action);
		JMenuItem item = menu.add(a);
		Object acc = a.getValue(Action.ACCELERATOR_KEY);
		if (acc != null)
		{
			item.setAccelerator((KeyStroke) acc);
		}
	}

	private void addAction(Action a)
	{
		String actionName = (String) a.getValue(Action.NAME);
		if (actions.containsKey(actionName))
		{
			throw new RuntimeException(actionName + " not unique");
		}
		actions.put(actionName, a);
	}
}