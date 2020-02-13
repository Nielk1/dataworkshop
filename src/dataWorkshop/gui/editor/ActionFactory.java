package dataWorkshop.gui.editor;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataWorkshop.DataWorkshop;
import dataWorkshop.LocaleStrings;
import dataWorkshop.MySocket;
import dataWorkshop.SocketInformation;
import dataWorkshop.Utils;
import dataWorkshop.data.BitRange;
import dataWorkshop.data.Data;
import dataWorkshop.data.DataEncoding;
import dataWorkshop.data.DataEncodingException;
import dataWorkshop.data.ViewTemplate;
import dataWorkshop.data.structure.RootStatement;
import dataWorkshop.data.transformer.FindAndReplace;
import dataWorkshop.data.view.DataFrame;
import dataWorkshop.data.view.DataViewOption;
import dataWorkshop.data.view.ExportDataViewOptions;
import dataWorkshop.data.view.ViewDefinitionFactory;
import dataWorkshop.gui.DiffDataOptions;
import dataWorkshop.gui.DynamicDataViewFrame;
import dataWorkshop.gui.ProjectPane;
import dataWorkshop.gui.SocketProjectPane;
import dataWorkshop.gui.data.DataModel;
import dataWorkshop.gui.data.view.DataView;
import dataWorkshop.gui.data.view.DynamicDataView;
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
import dataWorkshop.gui.dialogs.ManualDialog;
import dataWorkshop.gui.dialogs.NumberDialog;
import dataWorkshop.gui.dialogs.PreferencesDialog;
import dataWorkshop.gui.dialogs.SocketDialog;
import dataWorkshop.gui.dialogs.StructureDefinitionPaletteDialog;
import dataWorkshop.gui.dialogs.TextClipboardDialog;
import dataWorkshop.gui.event.DataSelectionEvent;
import dataWorkshop.gui.event.StateChangeEvent;
import dataWorkshop.xml.XMLSerializeFactory;

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
public class ActionFactory implements LocaleStrings
{

	/******************************************************************************
	 *	Constructors
	 */
	public ActionFactory()
	{
	}

	/******************************************************************************
	 *	Public Methods
	 */
	//
	//  DataModel Actions
	//
	public static Action createUndo()
	{
		Action a = new DataModelAction(Editor.UNDO_ACTION)
		{
			public void doAction()
			{
				dataModel.undo();
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(dataModel.hasUndo());
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createRedo()
	{
		Action a = new DataModelAction(Editor.REDO_ACTION)
		{
			public void doAction()
			{
				dataModel.redo();
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(dataModel.hasRedo());
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createCut()
	{
		Action a = new DataModelAction(Editor.CUT_ACTION)
		{
			public void doAction()
			{
				DataClipboardDialog.getInstance().setData(dataModel.getSelection());
				dataModel.delete();
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(dataModel.isSelection());
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createCopy()
	{
		Action a = new DataModelAction(Editor.COPY_ACTION)
		{
			public void doAction()
			{
				DataClipboardDialog.getInstance().setData(dataModel.getSelection());
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(dataModel.isSelection());
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createPaste()
	{
		Action a = new DataModelAction(Editor.PASTE_ACTION)
		{
			public void doAction()
			{
				dataModel.paste(DataClipboardDialog.getInstance().getData());
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createDelete()
	{
		Action a = new DataModelAction(Editor.DELETE_ACTION)
		{
			public void doAction()
			{
				long start = System.currentTimeMillis();
				dataModel.delete();
				DataView dataView = dataModel.getFocusedDataView();
				if (dataView != null)
				{
					dataModel.setBitRange(dataView.getValidBitRange(dataModel.getBitRange()));
				}
				logger.profile("Delete", start);
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(dataModel.isSelection());
			}
		};
		return a;
	}

	public static Action createUnselect()
	{
		Action a = new DataModelAction(Editor.UNSELECT_ACTION)
		{
			public void doAction()
			{
				dataModel.setSelectionSize(0);
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		return a;
	}

	public static Action createSelectAll()
	{
		Action a = new DataModelAction(Editor.SELECT_ALL_ACTION)
		{
			public void doAction()
			{
				dataModel.setBitRange(new BitRange(0, dataModel.getBitSize()));
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK));
		return a;
	}

	//
	//	EDIT
	//
	//Define Find & Replace
	public static Action createFindNext()
	{
		Action a = new DataModelAction(Editor.FIND_NEXT_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				ProjectPane project = editor.getSelectedProject();
				long offset = project.getDataModel().getSelectionOffset();
				Data selection = project.getDataModel().getSelection();
				Data d = project.getDataModel().getData();
				//In case last FindNext is current selection
				FindAndReplace findOptions = FindAndReplaceDialog.getInstance().getFindAndReplace();
				if (selection.equals(findOptions.getFindData()))
				{
					offset += findOptions.getStepSize();
				}
				offset = d.search(findOptions.getFindData(), true, offset, findOptions.getStepSize());
				if (offset == -1)
				{
					project.getDataModel().setSelection(project.getDataModel().getBitSize(), 0);
					JOptionPane.showMessageDialog(editor, INFO_NO_MATCH, "Find Result", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					project.getDataModel().setSelection(offset, findOptions.getFindData().getBitSize());
				}
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F3, 0));
		return a;
	}

	public static Action createFindPrevious()
	{
		Action a = new DataModelAction(Editor.FIND_PREVIOUS_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				ProjectPane project = editor.getSelectedProject();
				Data d = project.getDataModel().getData();
				FindAndReplace findOptions = FindAndReplaceDialog.getInstance().getFindAndReplace();
				long offset = project.getDataModel().getSelectionOffset();
				if (offset == -1)
				{
					offset = d.getBitSize() - 1;
				}
				else
				{
					offset--;
				}
				offset = d.search(findOptions.getFindData(), false, offset, findOptions.getStepSize());
				if (offset == -1)
				{
					project.getDataModel().setSelection(0, 0);
					JOptionPane.showMessageDialog(editor, INFO_NO_MATCH, "Find Result", JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					project.getDataModel().setSelection(offset, findOptions.getFindData().getBitSize());
					//Output.writeInformation("Match at: " + editor.getEditorOptions().offsetToString(offset));
				}
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		return a;
	}

	public static Action createFindAll()
	{
		Action a = new DataModelAction(Editor.FIND_ALL_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				ProjectPane project = editor.getSelectedProject();
				FindAndReplace findOptions = FindAndReplaceDialog.getInstance().getFindAndReplace();
				BitRange[] intervals = project.getDataModel().findAll(findOptions.getFindData(), findOptions.getStepSize());

				if (intervals.length > 0)
				{
					if (intervals.length > DataWorkshop.FIND_ALL_WARNING_LIMIT)
					{
						NumberDialog dialog =
							new NumberDialog(
								editor,
								FIND_ALL_RESULT_ABOVE_LIMIT_MESSAGE.format(new Object[] { Integer.valueOf(intervals.length)}),
								"Matches",
								intervals.length,
								DataWorkshop.getInstance().getUnsignedCount());
						dialog.setMinimum(0);
						dialog.setMaximum(intervals.length);
						dialog.setVisible(true);
						int len = (int) dialog.getNumber();
						BitRange[] intervals2 = new BitRange[len];
						for (int i = 0; i < len; i++)
						{
							intervals2[i] = intervals[i];
						}
						intervals = intervals2;
						System.out.println("Modified " + intervals.length);
					}
					DataFrame root = ViewDefinitionFactory.createDataFrame(intervals, findOptions.getFindDataEncoding());
					project.addStaticDataView(root, FIND_ALL_RESULT);
				}
				else
				{
					JOptionPane.showMessageDialog(
						editor,
						"No match found in the current selection (" + project.getDataModel().getBitRange() + ")",
						LocaleStrings.INFO,
						JOptionPane.INFORMATION_MESSAGE);
				}
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0));
		return a;
	}

	public static Action createReplace()
	{
		Action a = new DataModelAction(Editor.REPLACE_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				ProjectPane project = editor.getSelectedProject();
				Data selection = project.getDataModel().getSelection();
				FindAndReplace findOptions = FindAndReplaceDialog.getInstance().getFindAndReplace();
				if (selection.equals(findOptions.getFindData()))
				{
					project.getDataModel().paste(findOptions.getReplaceData());
					// Output.writeInformation("Replace at: " + editor.getEditorOptions().offsetToString(project.getDataModel().getSelectionOffset()));
				}
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createReplaceAll()
	{
		Action a = new DataModelAction(Editor.REPLACE_ALL_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				ProjectPane project = editor.getSelectedProject();
				DataModel dataModel = project.getDataModel();
				FindAndReplace findOptions = FindAndReplaceDialog.getInstance().getFindAndReplace();
				BitRange[] intervals = dataModel.findAll(findOptions.getFindData(), findOptions.getStepSize());

				if (intervals.length > 0)
				{
					dataModel.replace(intervals, findOptions.getReplaceData());
					String message = INFO_REPLACE_ALL + ": " + intervals.length + " times " + "\n";
					JOptionPane.showMessageDialog(editor, message, LocaleStrings.INFO, JOptionPane.INFORMATION_MESSAGE);
				}
				else
				{
					JOptionPane.showMessageDialog(editor, INFO_NO_MATCH, LocaleStrings.INFO, JOptionPane.INFORMATION_MESSAGE);
				}
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(true);
			}
		};
		return a;
	}

	public static Action createWriteToSocket()
	{
		Action a = new DataModelAction(Editor.WRITE_SOCKET_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				MySocket socket = ChooseSocketDialog.getInstance().show(editor, editor.getOpenSockets());
				if (socket != null)
				{
					try
					{
						BufferedOutputStream out = new BufferedOutputStream(socket.getSocket().getOutputStream());
						out.write(dataModel.getSelection().toByteArray());
						out.flush();
					}
					catch (UnknownHostException ee)
					{
						JOptionPane.showMessageDialog(editor, "Don't know about host: " + socket, LocaleStrings.INFO, JOptionPane.ERROR_MESSAGE);
					}
					catch (IOException ee)
					{
						JOptionPane.showMessageDialog(editor, "Could not open OutputStream to " + socket, LocaleStrings.INFO, JOptionPane.ERROR_MESSAGE);
					}
				}
			}

			public void selectionChanged(DataSelectionEvent e)
			{
				setEnabled(dataModel.isSelection());
			}
		};
		return a;
	}

	//
	//  ProjectActions
	//
	public static Action createCloseProject()
	{
		Action a = new ProjectAction(Editor.CLOSE_PROJECT_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				int choice;
				if (projectPane instanceof ProjectPane)
				{
					if (projectPane.getDataModel().hasDataChanged())
					{
						choice = editor.showYesNoCancelDialog(projectPane.getShortName() + " has been modified. Save changes ?");
						if (choice == JOptionPane.YES_OPTION)
						{
							editor.fireActionEvent(Editor.SAVE_FILE_ACTION);
						}
						else if (choice == JOptionPane.CANCEL_OPTION)
						{
							return;
						}
					}
				}
				else if (projectPane instanceof SocketProjectPane)
				{
					MySocket socket = ((SocketProjectPane) projectPane).getSocket();
					editor.removeSocket(socket);
					try
					{
						socket.getSocket().close();
					}
					catch (IOException ee)
					{
						ErrorDialogFactory.show(SOCKET_CLOSE_ERROR, ee);
					}
				}
				/**
				 * :REFACTORING:Martin Pape:Jun 24, 2003
				 */
				ProjectPane removedProjectPane = projectPane;
				editor.removeProjectPane(projectPane);
				removedProjectPane.close();
			}
		};
		return a;
	}

	public static Action createSaveFile()
	{
		Action a = new DataModelAction(Editor.SAVE_FILE_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File file = editor.getSelectedProject().getDataFile();
				if (file.exists() & file != DataWorkshop.getInstance().getFile(DataWorkshop.NO_NAME_FILE))
				{
					editor.getSelectedProject().saveData(file);
				}
				else
				{
					editor.fireActionEvent(Editor.SAVE_FILE_AS_ACTION);
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(dataModel.hasDataChanged());
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createSaveFileAs()
	{
		Action a = new ProjectAction(Editor.SAVE_FILE_AS_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File file = projectPane.getDataFile();
				if (file.exists())
				{
					file = editor.saveFile(file.getParentFile());
				}
				else
				{
					file = editor.saveFile(DataWorkshop.getInstance().getFile(DataWorkshop.WORKING_DIR));
				}
				if (file != null)
				{
					projectPane.saveData(file);
				}
				editor.forceRepaint();
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createAddNewStructure()
	{
		Action a = new ProjectAction(Editor.NEW_STRUCTURE_ACTION)
		{
			public void doAction()
			{
				projectPane.addDynamicDataView(new RootStatement());
			}
		};
		return a;
	}

	public static Action createOpenStructure()
	{
		Action a = new ProjectAction(Editor.OPEN_STRUCTURE_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File file = editor.openStructure();
				if (file != null)
				{
					Object o = XMLSerializeFactory.getInstance().deserialize(file);
					if (o != null && o instanceof RootStatement)
					{
						RootStatement structure = (RootStatement) o;
						structure.setFile(file);
						projectPane.addDynamicDataView(structure);
					}
					else
					{
						ErrorDialogFactory.show(OPEN_STRUCTURE_MESSAGE.format(new Object[] { file }), editor);
					}
				}
			}
		};
		return a;
	}

	public static Action createSaveStructure()
	{
		Action a = new ProjectAction(Editor.SAVE_STRUCTURE_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				DynamicDataViewFrame frame = (DynamicDataViewFrame) projectPane.getSelectedFrame();
				RootStatement structure = frame.getStructure();
				if (structure.isNew())
				{
					editor.fireActionEvent(Editor.SAVE_STRUCTURE_AS_ACTION);
				}
				else
				{
					XMLSerializeFactory.getInstance().serialize(structure, structure.getFile());
					frame.setChanged(false);
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				/**
				 * :TODO:Martin Pape:Jun 14, 2003
				 * check if structure has changed
				 */
				setEnabled(projectPane.isDynamicDataViewSelected());
			}
		};
		return a;
	}

	public static Action createSaveStructureAs()
	{
		Action a = new ProjectAction(Editor.SAVE_STRUCTURE_AS_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File file = editor.saveStructure(DataWorkshop.getInstance().getFile(DataWorkshop.STRUCTURE_DIR));
				if (file != null)
				{
					file = Utils.forceExtension(file, DataWorkshop.STRUCTURE_EXTENSION);
					DynamicDataViewFrame frame = (DynamicDataViewFrame) projectPane.getSelectedFrame();
					RootStatement structure = frame.getStructure();
					if (XMLSerializeFactory.getInstance().serialize(structure, file))
					{
						structure.setFile(file);
						frame.setChanged(false);
					}
					else
					{
						ErrorDialogFactory.show(SAVE_STRUCTURE_MESSAGE.format(new Object[] { file }), editor);
					}
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.isDynamicDataViewSelected());
			}
		};
		return a;
	}

	public static Action createCompileStructure()
	{
		Action a = new ProjectAction(Editor.COMPILE_STRUCTURE_ACTION)
		{
			public void doAction()
			{
				DynamicDataViewFrame frame = (DynamicDataViewFrame) projectPane.getSelectedFrame();
				//force a complete recompilation
				frame.setStructure(frame.getStructure());
			}

			public void stateChanged(StateChangeEvent e)
			{
				boolean isEnabled = false;
				if (projectPane.isDynamicDataViewSelected())
				{
					DynamicDataView view = (DynamicDataView) projectPane.getSelectedFrame();
					if (view.hasValidStructure())
					{
						isEnabled = true;
					}
				}
				setEnabled(isEnabled);
			}
		};
		return a;
	}

	public static Action createOpenViewDefinition()
	{
		Action a = new ProjectAction(Editor.OPEN_VIEW_DEFINITION_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File file = editor.openStructure();
				if (file != null)
				{
					Object o = XMLSerializeFactory.getInstance().deserialize(file);
					if (o != null && o instanceof DataFrame)
					{
						DataFrame dataFrame = (DataFrame) o;
						projectPane.addStaticDataView(dataFrame, file.getName());
					}
					else
					{
						ErrorDialogFactory.show(OPEN_VIEW_DEFINITION_MESSAGE.format(new Object[] { file }), editor);
					}
				}
			}
		};
		return a;
	}

	public static Action createSaveViewDefinitionAs()
	{
		Action a = new ProjectAction(Editor.SAVE_VIEW_DEFINITION_AS_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File file = editor.saveStructure(DataWorkshop.getInstance().getFile(DataWorkshop.STRUCTURE_DIR));
				if (file != null)
				{
					file = Utils.forceExtension(file, DataWorkshop.VIEW_DEFINITION_EXTENSION);
					DataFrame root = projectPane.getSelectedFrame().getDataFrame();
					if (!XMLSerializeFactory.getInstance().serialize(root, file))
					{
						ErrorDialogFactory.show(SAVE_VIEW_DEFINITION_MESSAGE.format(new Object[] { file }), editor);
					}
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.hasDataView());
			}
		};
		return a;
	}

	public static Action createExportDataAsView()
	{
		Action a = new ProjectAction(Editor.EXPORT_DATA_VIEW_AS_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();

				ExportDataViewOptions exportOptions = ExportDataViewDialog.getInstance().show(Editor.getInstance());

				if (exportOptions != null)
				{
					File file = editor.saveFile();
					if (file != null)
					{
						DataFrame root = projectPane.getSelectedFrame().getDataFrame();
						Document doc = ViewDefinitionFactory.renderDataToXML(root, projectPane.getDataModel().getData(), exportOptions.getDataViewOption(), exportOptions.getIntegerFormatFactory());
						StreamResult result = editor.transformDocument(doc, exportOptions.getStylesheet().getTransformer());
						if (result != null)
						{
							editor.writeToFile(file, result.getWriter().toString());
						}
					}
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.hasDataView());
			}
		};
		return a;
	}

	public static Action createExportDataView()
	{
		Action a = new ProjectAction(Editor.EXPORT_DATA_VIEW_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				DataWorkshop options = DataWorkshop.getInstance();
				File file = editor.saveFile();

				if (file != null)
				{
					DataFrame view = projectPane.getSelectedFrame().getDataFrame();
					DataViewOption viewOptions = new DataViewOption();
					viewOptions.setRenderOffset(false);
					viewOptions.setRenderSize(false);
					viewOptions.setDisplayInOneLine(true);
					viewOptions.setUnitSeparator(new char[0]);

					Document doc = ViewDefinitionFactory.renderDataToXML(view, projectPane.getDataModel().getData(), viewOptions, options.getIntegerFormatFactory());
					StreamResult result = editor.transformDocument(doc, options.getStylesheet(DataWorkshop.XML_STRIPPED).getTransformer());
					if (result != null)
					{
						editor.writeToFile(file, result.getWriter().toString());
					}
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.hasDataView());
			}
		};
		return a;
	}

	public static Action createConfigureDataView()
	{
		Action a = new ProjectAction(Editor.CONFIGURE_DATA_VIEW_ACTION)
		{
			public void doAction()
			{
				DataViewOption options = projectPane.getSelectedFrame().getDataViewOption();
				options = DataViewOptionDialog.getInstance().show(Editor.getInstance(), options);
				if (options != null)
				{
					//force rerendering
					projectPane.getSelectedFrame().setDataViewOption(options);
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.hasDataView());
			}
		};
		return a;
	}

	public static Action createDiffData()
	{
		Action a = new ProjectAction(Editor.DIFF_DATA_ACTION)
		{
			public void doAction()
			{
				ProjectPane[] projects = Editor.getInstance().getProjects();
				DataModel[] models = new DataModel[projects.length];
				String[] names = new String[projects.length];
				DataModel clip = new DataModel(DataClipboardDialog.getInstance().getData());
				models[0] = clip;
				names[0] = "Clipboard";
				int ii = 1;
				for (int i = 0; i < models.length; i++)
				{
					if (projects[i] != projectPane)
					{
						models[ii] = projects[i].getDataModel();
						names[ii] = projects[i].getShortName();
						ii++;
					}
				}
				DiffDataOptions diffOptions = DiffDataDialog.getInstance().show(Editor.getInstance(), models, names, projectPane);
				if (diffOptions != null)
				{
					BitRange[] intervals = projectPane.getDataModel().diff(diffOptions.getDataModel().getData(), diffOptions.getGranularity());
					if (intervals.length > 0)
					{
						String title = "Diff against " + diffOptions.getName();
						projectPane.addStaticDataView(ViewDefinitionFactory.createDataFrame(intervals, diffOptions.getDataConverter()), title);
					}
					else
					{
						JOptionPane.showMessageDialog(Editor.getInstance(), INFO_NO_DIFFERENCES, LocaleStrings.INFO, JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		};
		return a;
	}

	public static Action createQueryView()
	{
		Action a = new ProjectAction(Editor.DATA_VIEW_QUERY_ACTION)
		{
			public void doAction()
			{

				DataViewQueryDialog dialog = DataViewQueryDialog.getInstance();
				String query = dialog.show(Editor.getInstance());

				if (query != null)
				{
					Data data = projectPane.getDataModel().getData();
					DataFrame root = projectPane.getSelectedFrame().getDataFrame();

					long start = System.currentTimeMillis();
					Document doc = ViewDefinitionFactory.serializeForXPathQuery(root, data);
					try
					{
						NodeList list = XPathAPI.selectNodeList(doc, query);
						int len = list.getLength();
						if (len > 0)
						{
							Node node;
							DataFrame newRoot = new DataFrame("view", 0, projectPane.getDataModel().getBitSize());
							for (int i = 0; i < len; i++)
							{
								node = list.item(i);
								if (node.getNodeType() == Node.ELEMENT_NODE)
								{
									int[] path = XMLSerializeFactory.getAttributeAsIntArray((Element) node, ViewDefinitionFactory.PATH_TAG);
									if (path != null)
									{
										String xml = XMLSerializeFactory.getInstance().serialize(root.getChild(path));
										newRoot.add((DataFrame) XMLSerializeFactory.getInstance().deserialize(xml, false));
									}
								}
							}
							projectPane.addStaticDataView(newRoot, projectPane.getSelectedFrame().getTitle() + " using XPathQuery : " + query);
							logger.profile("XQuery: " + query, start);
						}
						else
						{
							JOptionPane.showMessageDialog(Editor.getInstance(), "No Match", LocaleStrings.INFO, JOptionPane.INFORMATION_MESSAGE);
						}
					}
					catch (TransformerException ee)
					{
						ErrorDialogFactory.show(DATA_VIEW_QUERY_ERROR, ee);
					}
				}
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.hasDataView());
			}
		};
		return a;
	}

	public static Action createCloneView()
	{
		Action a = new ProjectAction(Editor.CLONE_VIEW_DEFINITION_ACTION)
		{
			public void doAction()
			{
				long start = System.currentTimeMillis();
				String xml = XMLSerializeFactory.getInstance().serialize(projectPane.getSelectedFrame().getDataFrame());
				DataFrame root = (DataFrame) XMLSerializeFactory.getInstance().deserialize(xml, false);
				projectPane.addStaticDataView(root, projectPane.getSelectedFrame().getTitle());
				logger.profile("Clone View: " + projectPane.getSelectedFrame().getTitle(), start);
			}

			public void stateChanged(StateChangeEvent e)
			{
				setEnabled(projectPane.hasDataView());
			}
		};
		return a;
	}

	/******************************************************************************
	 *	Abstract Action
	 */
	public static Action createNewData()
	{
		Action a = new AbstractEditorAction(Editor.NEW_DATA_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				editor.addProjectPane(new ProjectPane(new DataModel()));
				editor.forceRepaint();
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createOpenFile()
	{
		Action a = new AbstractEditorAction(Editor.OPEN_FILE_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				File dataFile = editor.openFile();
				if (dataFile != null)
				{
					if (dataFile.length() > Data.MAX_SIZE_IN_BYTES)
					{
						ErrorDialogFactory.show(OPEN_FILE_TOO_BIG_MESSAGE.format(new Object[] { dataFile }), editor);
					}
					else
					{
						ViewTemplate[] signatures = DataWorkshop.getInstance().getDataRecognizer().getPossibleViewTemplates(dataFile);
						RootStatement[] rootStatements = editor.chooseRootStatements(signatures);
						DataModel dataModel = new DataModel();
						try
						{
							ProjectPane pane;
							pane = new ProjectPane(dataFile, dataModel);
							/**
							 *  If this is the first project added we need to validate editor so the bounds on projectPane
							 *  are set correctly. ProjectPane uses its bounds to calculate the size of each view.
							 */
							editor.addProjectPane(pane);
							editor.validate();
							pane.setDynamicDataView(rootStatements);
							pane.loadData();
						}
						catch (FileNotFoundException ee)
						{
							ErrorDialogFactory.show(OPEN_FILE_MESSAGE.format(new Object[] { dataFile }), ee, editor);
						}
					}
				}
				editor.forceRepaint();
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		return a;
	}

	public static Action createExportData()
	{
		Action a = new ProjectAction(Editor.EXPORT_DATA_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				Data data = projectPane.getDataModel().getData();
				DataEncoding encoding = ChooseEncodingDialog.getInstance().show(editor);
				if (encoding != null)
				{
					File file = editor.saveFile();
					if (file != null)
					{
						long bitOffset = 0;
						if (data.getBitSize() % encoding.getBitSize() != 0)
						{
							ErrorDialogFactory.show(IMPORT_DATA_MESSAGE.format(new Object[] { projectPane.getShortName(), Integer.valueOf(encoding.getBitSize())}), editor);
							return;
						}

						try
						{
							FileWriter writer = new FileWriter(file);
							while (bitOffset < data.getBitSize())
							{
								encoding.encode(data, bitOffset, encoding.getBitSize());
								writer.write(new String(encoding.encode(data, bitOffset, encoding.getBitSize())));
								bitOffset += encoding.getBitSize();
							}
							writer.close();
						}
						catch (FileNotFoundException e)
						{
							ErrorDialogFactory.show(OPEN_DATA_OBJECT_MESSAGE.format(new Object[] { file }), e, editor);
							return;
						}
						catch (IOException e)
						{
							ErrorDialogFactory.show(OPEN_DATA_OBJECT_MESSAGE.format(new Object[] { file }), e, editor);
							return;
						}
					}
				}
			}
		};
		return a;
	}

	public static Action createImportData()
	{
		Action a = new AbstractEditorAction(Editor.IMPORT_DATA_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				DataEncoding converter = ChooseEncodingDialog.getInstance().show(editor);
				if (converter != null)
				{
					File file = editor.openFile();
					if (file != null)
					{
						Data data = new Data();
						char[] array = new char[converter.getDotSize()];
						int dotSize = converter.getDotSize();
						long fileLength = file.length();

						if (fileLength % dotSize != 0)
						{
							ErrorDialogFactory.show(IMPORT_DATA_MESSAGE.format(new Object[] { file, Integer.valueOf(dotSize)}), editor);
							return;
						}

						long fileOffset = 0;
						try
						{
							FileReader reader = new FileReader(file);
							while (fileOffset < fileLength)
							{
								reader.read(array);
								data.append(converter.decode(array));
								fileOffset += dotSize;
							}
							reader.close();
						}
						catch (FileNotFoundException e)
						{
							ErrorDialogFactory.show(OPEN_DATA_OBJECT_MESSAGE.format(new Object[] { file }), e, editor);
							return;
						}
						catch (IOException e)
						{
							ErrorDialogFactory.show(OPEN_DATA_OBJECT_MESSAGE.format(new Object[] { file }), e, editor);
							return;
						}
						catch (DataEncodingException e)
						{
							ErrorDialogFactory.show(DATA_CONVERTER_ERROR, e, editor);
							return;
						}

						ViewTemplate[] signatures = DataWorkshop.getInstance().getDataRecognizer().getPossibleViewTemplates(data);
						RootStatement[] rootStatements = editor.chooseRootStatements(signatures);
						DataModel dataModel = new DataModel(data);
						ProjectPane pane = new ProjectPane(dataModel);
						editor.addProjectPane(pane);
						pane.setDynamicDataView(rootStatements);
					}
				}
			}
		};
		return a;
	}

	public static Action createOpenSocket()
	{
		Action a = new AbstractEditorAction(Editor.OPEN_SOCKET_ACTION)
		{
			public void doAction()
			{

				Editor editor = Editor.getInstance();
				SocketInformation info = SocketDialog.getInstance().show(editor);
				if (info != null)
				{
					try
					{
						MySocket socket = new MySocket(new Socket(info.getHost(), info.getPort()));
						editor.addSocket(socket);

						DataModel dataModel = new DataModel();
						editor.addProjectPane(new SocketProjectPane(socket, dataModel));

					}
					catch (UnknownHostException ee)
					{
						JOptionPane.showMessageDialog(editor, "Don't know about host: " + info.getHost(), LocaleStrings.INFO, JOptionPane.ERROR_MESSAGE);
					}
					catch (IOException ee)
					{
						JOptionPane.showMessageDialog(editor, "Could not open OutputStream to " + info.getHost() + ":" + info.getPort(), LocaleStrings.INFO, JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
		return a;
	}

	public static Action createExit()
	{
		Action a = new AbstractEditorAction(Editor.EXIT_ACTION)
		{
			public void doAction()
			{
				Editor editor = Editor.getInstance();
				ProjectPane[] projects = editor.getProjects();
				for (int i = 0; i < projects.length; i++)
				{
					editor.fireActionEvent(Editor.CLOSE_PROJECT_ACTION);
					//If the active Project is still the last project we can
					// can conclude the user has canceled the close action
					// and wants to cancel the whole exit action too
					if (editor.getSelectedProject() == projects[i])
					{
						return;
					}
				}
				XMLSerializeFactory.getInstance().serialize(editor, new File(SERIALIZED_EDITOR));
				XMLSerializeFactory.getInstance().serialize(DataWorkshop.getInstance(), new File(SERIALIZED_DATAWORKSHOP));
				System.exit(0);
			}
		};
		return a;
	}

	public static Action createDataClipboard()
	{
		Action a = new AbstractEditorAction(Editor.DATA_CLIPBOARD_ACTION)
		{
			public void doAction()
			{
				DataClipboardDialog.getInstance().setVisible(true);
			}
		};
		return a;
	}

	public static Action createStructurePalette()
	{
		Action a = new AbstractEditorAction(Editor.STRUCTURE_PALETTE_ACTION)
		{
			public void doAction()
			{
				StructureDefinitionPaletteDialog.getInstance().setVisible(true);
			}
		};
		return a;
	}

	public static Action createTextClipboard()
	{
		Action a = new AbstractEditorAction(Editor.TEXT_CLIPBOARD_ACTION)
		{
			public void doAction()
			{
				TextClipboardDialog.getInstance().setVisible(true);
			}
		};
		return a;
	}

	public static Action createEncodingConverter()
	{
		Action a = new AbstractEditorAction(Editor.ENCODING_CONVERTER_ACTION)
		{
			public void doAction()
			{
				EncodingConverterDialog.getInstance().setVisible(true);
			}
		};
		return a;
	}

	public static Action createPreferences()
	{
		Action a = new AbstractEditorAction(Editor.PREFERENCES_ACTION)
		{
			public void doAction()
			{
				if (PreferencesDialog.getInstance().show(Editor.getInstance(), DataWorkshop.getInstance(), Editor.getInstance()))
				{
					Editor.getInstance().rebuild();
				}
			}
		};
		return a;
	}

	public static Action createUserManual()
	{
		Action a = new AbstractEditorAction(Editor.USER_MANUAL_ACTION)
		{
			public void doAction()
			{
				ManualDialog.getInstance().show();
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		return a;
	}

	public static Action createAbout()
	{
		Action a = new AbstractEditorAction(Editor.ABOUT_ACTION)
		{
			public void doAction()
			{
				AboutDialog.getInstance().setVisible(true);
			}
		};
		return a;
	}

	public static Action createDefineFindAndReplace()
	{
		Action a = new AbstractEditorAction(Editor.DEFINE_FIND_AND_REPLACE_ACTION)
		{
			public void doAction()
			{
				FindAndReplaceDialog.getInstance().show(Editor.getInstance());
			}
		};
		a.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		return a;
	}
}
