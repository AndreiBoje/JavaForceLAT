## Description
ForceLAT is an open source project meant to help other people to create and export images of any type of graph thru the use of a custom parser.This was originally meant to help draw finite
state machine diagrams so there are more features helping with that.

## How to run it
Clone this repo into your IDE (preferably InteliJ IDEA) and run the `build.gradle` file then run the project.
Alternatively,if you don't want to build it from source yourself,you can use the executable provided in the **build** folder.

## Usage & GUI Overview
The GUI is mainly divided in 2 parts (the canvas and the command line area) and a common menu options bar at the top.The `canvas` is situated 
at the left and the `command line area` at the right.<br/> Commands will be parsed and displayed in realtime so you don't have to worry about pushing a "run" button anywhere.
<br/> **REMARK 1** : commands should be written one per line.<br/>
**REMARK 2** : the CLI won't show you where there's an error so be aware of argument count and spelling errors. <br/>
**REMARK 3** : the order in which options are specified does not matter.


* Keyboard/Mouse commands table 

| COMMAND          | EFFECT        |
| -------------    |:-------------:|
| CTRL + LMB       | Place node at mouse position |
| CTRL + RMB       | Delete node at mouse position |
| LMB DRAG       | Drag node at mouse position |
| SHIFT + LMB DRAG       | Pan inside the canvas |

* Commands & Usage
### `dragstep`
Description: *Adjust the snap factor of a node when dragging it.*<br/>
Command schema:`dragstep  <value>` <br/>
Available options: no specific options <br/>
Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `dragstep 20` <br/> <- this will snap the node at every 20px
### `opts`
Description: *Assign custom options to a node or a list of nodes. `opts` can have multiple options chained together separated by spaces.* <br/>
Command schema:`opts -nodes <list of nodes separated by comma> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Radius        |radius [number] |
| Color        |color [hex] |
| Text size       | tsize [number] |
| Node ring width      | width [number] |
| Fill entire node     | fill [no argument!] |
| Hide node text      | notext [no argument!] |
| Hide index identifier      | noindex [no argument!] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `opts -nodes 2 -color 00aaffff -noindex` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `opts -nodes 0,3,1,2 -width 2.1 -notext` <br/>
### `alias`
Description: *Assign an alias to a node or a list of nodes (change the text displayed in the node).By default,
each node is alised using it's index identifier.The number of `nodes` provided should be equal to the number of `aliases` to be specified* <br/>
Command schema:`alias -nodes <list of nodes separated by comma> -text <list of aliases separated by comma>` <br/>
Available options: no specific options. <br/>
Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `alias -nodes 2 -text myAliasFor2` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `alias -nodes 0,3,2 -text aliasFor0,aliasFor3,aliasFor2` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 3. `alias -nodes 5,3 -text aliasFor5,aliasFor3,aliasForSomethingElse` **<- Incorrect,too many arguments** <br/>
### `ucon`
Description: *Connect the first node specified to the rest of the nodes in the list.`ucon` can have multiple options chained together separated by spaces.If the `text` option is
specified,every line created should get a text value.Text values are separated by **spaces**.*<br/>
Command schema:`ucon -nodes <list of nodes separated by comma> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Puts text     | text [string] |
| Put arrow        |arrow [number] |
| Flip arrow direction     | flip [no argument!] |
| Color       | color [hex] |
| Text angle      | tangle [number degrees] |
| Text size     | tsize [number]|
| Flip text about line      | tflip [no argument!] |
| Text height     | theight [number] |
| Line width    | lwidth [number] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `ucon -nodes 2,1 -lwidth 4 -arrow` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `ucon -nodes 0,3,1,2 -width 2.1 -text textFor03 textFor31 textFor12`<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 3. `ucon -nodes 0 -width 2.1 -text textFor0 -tangle 30`<br/>
### `flow`
Description: *Connect the first node in the list to the second,the second to the third, and so on.`flow` can have multiple options chained together separated by spaces.*<br/>
Command schema:`flow -nodes <list of nodes separated by comma> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Puts text     | text [string] |
| Put arrow        |arrow [number] |
| Color       | color [hex] |
| Text angle      | tangle [number degrees] |
| Text size     | tsize [number]|
| Flip text about line      | tflip [no argument!] |
| Text height     | theight [number] |
| Line width    | lwidth [number] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `flow -nodes 0,2,4 -theight 10 -text textFor01 textFor24` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `flow -nodes 0,3,1,2 -color 3344ffff`<br/>
### `bcon`
Description: *Connect the first node to the second one with 2 directed arrows.`bcon` can have multiple options chained together separated by spaces.*<br/>
Command schema:`bcon -nodes <node1,node2> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Puts text     | text [string] |
| Color       | color [hex] |
| Text angle      | tangle [number degrees] |
| Text size     | tsize [number]|
| Text height     | theight [number] |
| Line width    | lwidth [number] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `bcon -nodes 0,2 -color 112233ff -text thisIsText` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `bcon -nodes 0,3,1,2 -color 3344ffff`<br/> **<-INCORRECT,too many nodes specified** <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `bcon -nodes 5,3` <br/>
### `jpr`
Description: *Connect the first node to the second node with a line that rises from the nodes at an angle.`jpr` can have multiple options chained together separated by spaces.*<br/>
Command schema:`jpr -nodes <node1,node2> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Puts text     | text [string] |
| Put arrow        |arrow [number] |
| Flip arrow direction     | flip [no argument!] |
| Color       | color [hex] |
| Line extend factor      | extf [number] |
| Rise angle     | rangle [number degrees] |
| Text angle      | tangle [number degrees] |
| Text size     | tsize [number]|
| Flip text about line      | tflip [no argument!] |
| Text height     | theight [number] |
| Line width    | lwidth [number] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `jpr -nodes 0,2 -rangle 90 -arrow -extf 40` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `jpr -nodes 1,5 -tflip -text thisIsText`<br/>
### `self`
Description: *Connect a node to itself.`self` can have multiple options chained together separated by spaces.*<br/>
Command schema:`self -node <node> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Puts text     | text [string] |
| Color       | color [hex] |
| Text angle      | tangle [number degrees] |
| Text size     | tsize [number]|
| Flip text about line      | tflip [no argument!] |
| Text height     | theight [number] |
| Angle about node     | angle [number degrees] |
| Line width    | lwidth [number] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `self -node 3` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `self -nodes 5 -tangle 30 -text thisIsText`<br/>
### `start`
Description: *Mark a node as being the starting node of a finite state machine.`start` can have multiple options chained together separated by spaces.*<br/>
Command schema:`start -node <node> ( -<option name> <option args> )*` <br/>
Available options: <br/>
| CHANGES          | OPTION SCHEMA        |
| -------------    |:-------------:|
| Color       | color [hex] |
| Line extend factor      | length [number] |
| Angle about node     | angle [number degrees] |
| Line width    | lwidth [number] |

Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `start -node 0` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `start -nodes 5 -angle 180 -lwidth 3`<br/>
### `final`
Description: *Mark a list of nodes as being the final nodes of a finite state machine.*<br/>
Command schema:`final -nodes <list of nodes separated by comma>` <br/>
Available options: no specific options <br/>
Examples: <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 1. `final -node 0` <br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 2. `final -node 4,2,1` <br/>





