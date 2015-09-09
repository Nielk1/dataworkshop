# DataWorkshop, a binary data editor

    Data Workshop v1.1.1
    written by martin.pape@gmx.de
    http://www.dataworkshop.de

DataWorkshop is a flexible binary editor with support for user defined structures.

## Instructions for Ubuntu 14.04.3 LTS (Trusty Tahr)
Install required package:

    sudo apt-get install libxalan2-java
    ln -s /usr/share/java/xalan2.jar  ~/.ant/lib

Clone:

    git clone https://github.com/titobrasolin/dataworkshop.git
    cd dataworkshop

Build and execute:

    ant buildrelease
    cd DataWorkshop-1.1.1
    java -jar DataWorkshop.jar

Enjoy:

![Overview](https://raw.githubusercontent.com/titobrasolin/dataworkshop/master/src/doc/pics/editor.jpg)
