
			DATAWORKSHOP 1.1.1

What is it?
-----------
DataWorkshop is a flexible binary editor with support for user defined structures. This is the open source release of the DataWorkshop version 1.1.1.


Why Open Source
---------------
I am unable to invest any significant amount of time in the code for the forseeable future. So instead of
letting the files rot on my hardrive I decided to relase DataWorkshop as open
source. Hopefully someone becomes interested enough in this project to continue it. But even if the main project dies there are some very interesting routines in my code for manipulating binary data.


Build DataWorkshop
------------------
In the root directory is a file called build.xml. This is the ant build script I used to compile DataWorkshop. Assuming you have installed ant correctly 'ant build' should compile you an executable version of DataWorkshop.


Homepage
--------
http://www.dataworkhop.de


TODO (Features)
----
- add readonly flag to datamodel
- add comment attribute to structure definition elements, and display comment as tooltip in data view
- extend sign (boolean) into separate class (2-complement sign, unsigned, 1-complement sign usw)
- add offset to static views to make view moveable over the data
- dynamically add menu item to view menu for each data view (as ButtonGroup to select views)
- some sort of history for Find & Replace
- graphical XPath View Query (TreeSelection ?)
- Bookmarks for BitRanges (we could use a new static view similiar to the result of find all)
- open, save projectPane (save all views + DataViewOptions and data and dataModel (undos))
- add PopupMenu to Project tabs
- add variables to Structures which can be initialised by user and used in modficationPane and instead of pointer / static
- make all data actions into threads, running inside ProjectPane
- show DataConverter Mappings (some of them are not bijective and the users need to know which of them)
- replace the standard icons for TreeNodes in JTree with icons for Structures and DataFrames
- catch and display to user all critical exceptions which are not caught in the code (e.g. OutOfMemoryException)
- Graphic IndexFields Views
- extend EditorOptionsDialog to allow complete graphical modification of options.xml
 -> key bindings
 -> signatures
- Macro recording and scripts
- Printing Support
- Implement CommandLine e.g cat somedata.bin | Dataworkshop filter blah > final.bin
- turn dataModel into proxy, so it is possible to edit very large files, which are only partially read into memory
- read in data from xml tagged files to do complex conversions (e.g. EBDIC-String (0 delimited) to Ascii Strings (0 delimited))
- binary operations (xor, and) with operand (use popup dialog)
- make options.xml/workbench.xml files to configure Editor to only use hex to display all numbers (for the real hacker)
- make XMLSerializeFactory more stable, so it does not crash if xml file is invalid and gives to user some hint as to the cause of the error
- use JNI interface to do capture real network traffic
- DataConverter: BCD (packed and unpacked)
- add ToolBar
- Action to change endianess of whole structure
- change long to BigInteger in DataNumber and DataEncoding
- user should be able to scroll in SingleLineView left /right
- make DataViewQuery context sensitive to selected frame in BrowserView
- user should be able to start cell editing with any valid key, and the key pressed should be treated as the first input
- Use smaller Font for Labels and Menus (e.g. like in Netbeans) to save space
- Patches (xml format) can be applied to data
- Read in property file (perhaps as part of dataworkshop.xml or editor.xml)
- redesign structure view (just insert node, move node up/down actions, icons), make nodes in structure tree moveable via mouse
- add more checksum DataTransformations
- single step mode for structure debugger
- Search & Replace too slow
- import/export data to slow



TODO (Refactoring)
------------------
- NumberPane (use own NumberPaneModel instead of SpinnerModel, Spinner does not know about unvalidated data in DataEncodingField)
- clean up static initializer/singleton/xml stuff in Editor and EditorOptions
- centralize all layout information (e.g. Box.createRigidArea(new Dimension(6,0))) which is used to configure layout
- make StructurePath class, enclosing a String[] of childnames (at the moment String[] is handled directly)
- used default data representation (configurable by user) in delimiterConfiguration gui (to save screen space)
- ProjectPane should be abstract superclass for FileProjectPane and SocketProjectPane
- rename "lock" checkbox to "use-selection-offset"
- Integerformat (Should I really display all number in the same format (e.g. hex) or only certain numbers?)



Known Bugs
----------
- limit Undo/Redo Buffer size
- Debug different Look And Feels
- LayoutPane should not consume the VK_ENTER event so the event is consumes by the OK button in the dialog. But we must still do the validation of the entered text.
- DataTextArea does not does not respect focus traversel
- Delete Trailing Space in DataField
- Respect 0 before delimiter in IntegerFormat(no padding) (e.g. 0.7 instead of .7)
- !! Ctrl-C does not work when TreeView has focus !!
- Not possible to click inside larger DataEncoding with exact caret positioning. Caret is always set to first input point.
- DataViews do not get their data model removed and are thus not garbage collected
- When SingleLineView is gains focus DynamicDataViews with locked offset do not get a datamodel.selectioChange action to force a recompile.
- DataFieldTextPane should not consume Tab and Shift_Tab events


Some Notes on Design
--------------------
Sorry for the lack of documentation. Below are some design notes which might be useful

o DataConverters are Singletons for memory reasons

o Difference DataFilter / DataConverter
  DataTransformer: Mapping Data -> Data (bitsize may change)
  DataConverter: Mapping Data -> String & String -> Data

o Difference editor.xml / dataWorkshop.xml
  editor.xml: Gui options
  dataWorkshop.xml: General program options (used for GUI & CommandLine)

o URIs in XML Documents are relativ to the DataWorkshop install directory


Structure Compilation:
------------------------
Before compilation the structure has to be validated.
The compiler assumes a valid structure without null pointers and invalid structure references.


XMLSerializeFactory:
-----------------------
During deserialization the deserialized objects are removed from the document
- reduces memory footprint
- namespace conflicts during deserialization do no occur 
	(e.g. the superclass changes the serialization implementation and introduces the same 
	the as the subclass causing an error. The subclass should not know about the serialization of the superclass and therefore cannot prevent the error)



























