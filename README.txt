TinyUML is a tool to create UML2 diagrams.

The current version is 0.25
It is possible to save and open model files now, as well as to create a number
of diagram element types.
NOTE: At this moment, it is not recommended to save any important data.
----- Because persistence is implemented using Serialization, files will not
      be compatible with future releases.

-- Wei-ju Wu April 9th, 2007

Platform
--------
At least Java SE 6 is required to compile and execute the application.

Starting the software
---------------------
The application is delivered as an executable jar file, so on many systems,
a double click on the tinyuml.jar file should be sufficient.
Alternatively, it can be started using

java -jar tinyuml.jar

in the distribution directory

Changes in Release 0.25 "Vivi" milestone 5/?/2007
--------------------------------------------------
- multiple diagrams
- one central undo manager
- sequence diagrams

Changes in Release 0.13_02 5/09/2007
------------------------------------
- fixed diagram resizing effects (issue 1711662)
- fixed SVG export issue in combination with clipping (issue 1714230)
- resize snapping improved

Changes in Release 0.13_01 5/01/2007
------------------------------------
- fixed NullPointerException in diagram resizing (issue 1708509)
- recursively send move notifications in children of composite nodes now (issue 1710869)

Changes in Release 0.13 4/23/2007
---------------------------------
- multiplicities in associations
- association names
- reading directions
- nesting elements in packages
- reworked action event handling
- enhanced test suite

Changes in Release 0.12 "Cait Sith" 4/9/2007
---------------------------------------------
- selection of multiple elements
- editing the points of a connection
- display native menu bar on Mac OS X
- context menus
- changing draw order of elements
- resetting connection points
- converting direct connections to rectilinear and vice versa
- navigable associations
- help contents menu
- new, open, quit with confirmation on modified projects
- manual resizing of the diagram area

Changes in Release 0.10 "Quina" 3/26/2007
-----------------------------------------
- saving and reading files implemented using serialization
- inheritance, composition, aggregation and interface realization types added
- connections are created using the Prototype pattern

Changes in Release 0.09 "Chocobo" 3/19/2007
-------------------------------------------
- class editor (attributes, methods, stereotypes)
- connections and nodes are deleted in a clean way
- selections change when the nodes are enlarged
- editor tab changes when diagram name is changed
- use Swing UndoManager
- code refactorings

Changes in Release "Moogle" 3/12/2007
-------------------------------------
- changed license to GPL
- internal issue tracking system Mantis established
- code refactorings
- more unit tests
- implements class and component elements based on a compartment class
- introduces rectilinear connections
- create associations based on rectilinear connections
- multiline editing in notes
- note connections can be drawn

Changes in Release "Kupo!" - 3/4/2007
-------------------------------------
This is an internal milestone release that contains a cleanup of the code,
and among other additions, includes a german localization of the user interface.

- a non-editable Note element was added for experimenting with multi-line
  layout
- editing labels is reversible
- correct resizing the parent component after editing label
- diagram name can be be edited through its label now
- file name extensions are now added to exported files if necessary and
  overwriting existing files must be confirmed
- exported graphics files do not include the selections anymore
- german localization file added
- class elements can be created

Changes in Release "Kupo!" (Prerelease) - 3/1/2007
-------------------------------------
This is a preliminary release to act as a better preview for functionality than
"Gogo". Because it includes label editing, component elements and graphics
export, this version could even be used for simple component and package
diagrams

- move elements within bounds and resize diagram according to moves
- delete elements
- diagram name display
- resizing
- edit labels
- Export PNG
- improved SVG export
- components
- tree element removed
- application starts with an empty editor
- note button added

Release "Gogo" - 2/25/2007
--------------------------
This release includes

Software design:

- event framework
- initial drawing and selection framework
- initial UML element model
- initial file format

User interface:

- undo
- spring loaded buttons
- resource management
- internationalization
- key binding for cancellation

Editing diagrams:

- alignment to grid
- zooming
- selection
- moving package elements
- draw package elements
- draw dependencies, associations, inheritance

Persistency/Export:
- Load models
- Export SVG

Project monitoring:
- Testing environment
- Mock framework integration (jMock)
- Maven added as a build tool including PMD, JDepend, Checkstyle and Cobertura

