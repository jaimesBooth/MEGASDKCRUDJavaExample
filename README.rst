.. http://docutils.sourceforge.net/docs/user/rst/quickref.html

.. ATTENTION::
   Please note, work in progress

MEGA CRUD Java Practice Example
===============================
The following instructions outline the steps required to get the `MEGA CRUD Java Practice Example`__ code built and running in Ubuntu using bash terminal commands.

.. NOTE::
    This guide was tested on Ubuntu 15.04 and is adapted from: https://github.com/meganz/sdk/blob/master/README.md and https://help.ubuntu.com/community/CompilingEasyHowTo 

.. _MEGACRUD: https://code.developers.mega.co.nz/jaimesbooth/MegaCRUDJavaPractice

__ MEGACRUD_

Installation
------------

Prepare System to Build Packages:
`````````````````````````````````
The tools for building and compiling software need to be installed:

.. code:: bash

    sudo apt-get install build-essential autoconf libtool
    
As the project code is hosted on a Git Version Control System, Git should be installed:

.. code:: bash

    sudo apt-get install git-core

Install MEGA SDK Java Binding Dependencies
``````````````````````````````````````````
The MEGA Software Development Kit (SDK) Java Bindings are dependent on the following packages, which need to be installed: 

.. sudo apt-get install libtinfo-dev

.. .. code:: bash

    sudo apt-get install libcrypto++-dev
    sudo apt-get install zlib1g-dev
    sudo apt-get install libsqlite3-dev
    sudo apt-get install libssl-dev
    sudo apt-get install libc-ares-dev
    sudo apt-get install libcurl4-openssl-dev
    sudo apt-get install libfreeimage-dev
    sudo apt-get install libreadline6-dev
    sudo apt-get install swig2.0

.. code:: bash

    sudo apt-get install libcrypto++-dev zlib1g-dev libsqlite3-dev libssl-dev libc-ares-dev libcurl4-openssl-dev libfreeimage-dev libreadline6-dev swig2.0

A Java JDK is required to build and compile the MEGA SDK, for example:

.. code:: bash

    sudo apt-get install default-jdk


Build the MEGA SDK Java Bindings
````````````````````````````````
Get the MEGA SDK
::::::::::::::::

Clone the MEGA SDK repository to a local directory of your choice:

.. code:: bash
    
    git clone https://github.com/meganz/sdk
 
Change the current working directory to the cloned SDK directory:
  
.. code:: bash

    cd sdk

Build & Compile 
:::::::::::::::

.. code:: bash

    sh autogen.sh

Configure
'''''''''

.. Configure while pointing to Java headers: http://tecadmin.net/install-oracle-java-8-jdk-8-ubuntu-via-ppa/


.. code:: bash
    
    ./configure --enable-java

Alternatively, you may have to point to your installed Java headers, for example:

..    ./configure --enable-java --with-java-include-dir=/usr/lib/jvm/java-8-oracle/include/

.. code:: bash
    
    ./configure --enable-java --with-java-include-dir=/usr/lib/jvm/java-7-openjdk-i386/include/

Make
''''

.. code:: bash
    
    make

Install
'''''''

.. code:: bash

    sudo make install

Use the MEGA SDK Java Bindings
``````````````````````````````
Now that you have built and compiled the MEGA SDK Java Bindings, they can be used. 

Get the MEGACRUDExample Code
::::::::::::::::::::::::::::

Clone the MEGACRUDExample repository to a local directory of your choice:

.. code:: bash
    
    git clone https://code.developers.mega.co.nz/jaimesbooth/MegaCRUDJavaPractice.git

Copy MEGASDK Java Library & Bindings to MEGACRUDExample Project
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
  
Copy the MEGA SDK Java library ``libmegajava.so`` into a new ``/libs`` folder in the MEGACRUDExample project folder.

create ``/libs`` in the MEGACRUDExample folder:


.. code:: bash

    mkdir -p MegaCRUDJavaPractice/libs

Copy the MEGA SDK Java library into the MEGACRUDExample project folder:

.. code:: bash
    
    cp bindings/java/.libs/libmegajava.so MegaCRUDJavaPractice/libs

Copy the Java classes into the ``/src/nz/mega/sdk`` folder of the MEGACRUDExample project:

.. .. code:: bash
    
    mkdir -p MegaCRUDJavaPractice/java/src/nz/mega/sdk
    cp bindings/java/nz/mega/sdk/*.java MegaCRUDJavaPractice/src/nz/mega/sdk

.. code:: bash
    
    mkdir -p MegaCRUDJavaPractice/src/nz/mega/sdk
    cp bindings/java/nz/mega/sdk/*.java MegaCRUDJavaPractice/src/nz/mega/sdk

Complete the ``credentials.txt`` File
:::::::::::::::::::::::::::::::::::::
The ``credentials.txt`` file is used to automate the login process. Fill in the ``credentials.txt`` located in the root of the project with the user's MEGA login details: email and password. Place each detail on a separate line as indicated in ``credentials.txt``.

Run the MEGACRUDJavaExample
:::::::::::::::::::::::::::

From an IDE
'''''''''''

Remove the Android specific bindings from ``MegaCRUDJavaPractice/java/src/nz/mega/sdk``:
 * MegaUtilsAndroid.java
 * AndroidGfxProcessor.java
 * MegaApiAndroid.java

Import the ``MegaCRUDJavaPractice`` project into your favourite Java IDE, for example https://www.jetbrains.com/idea/. Build and run ``MEGACRUD.java``.

.. @TODO Terminal run is currently not working. Needs to be investigated.

From the Terminal
'''''''''''''''''
.. ``MEGACRUD.java`` is pre-configured to run from an IDE, however, the code can be easily adapted to run from the terminal. In the method ``MEGACRUD.getUserCredentials()`` located in ``MegaCRUDJavaPractice/java/src/nz/mega/megacrudexample/MEGACRUD.java``, comment out:

.. .. code:: java

        Scanner userInputScanner = new Scanner(new BufferedReader(new FileReader("credentials.txt")));
        userEmail = userInputScanner.next();
        password = userInputScanner.next();
        
.. and un-comment:

.. .. code:: java

    //        Console console = System.console();
    //        String userEmail = console.readLine("Email: ");
    //        char[] passwordArray = console.readPassword("Password: ");
    //        String password = new String(passwordArray);

.. Resulting in the following code:

.. .. code:: java

        /*
         * The user's credentials need to be read from the credentials.txt file which must be
         * created in the root of this project and contain the user's
         * email address on the first line and their password on the second line.
         */
    //        Scanner userInputScanner = new Scanner(new BufferedReader(new FileReader("credentials.txt")));
    //        userEmail = userInputScanner.next();
    //        password = userInputScanner.next();


        /*
         * Alternatively, the user's credentials can be requested at runtime
         * by commenting out the above scanner code and using the console.
         * This console will only be displayed when this application is run
         * from the system's terminal.
         */
        Console console = System.console();
        String userEmail = console.readLine("Email: ");
        char[] passwordArray = console.readPassword("Password: ");
        String password = new String(passwordArray);

Create a directory for the application do be compiled to. From the terminal:

.. code:: bash
    
    mkdir -p MegaCRUDJavaPractice/bin
    
Change the current working directory to the ``MegaCRUDJavaPractice`` directory:
  
.. code:: bash

    cd MegaCRUDJavaPractice

Compile the example project with the java compiler:

.. code:: bash
    
    javac -d bin -sourcepath src src/nz/mega/megacrudexample/MEGACRUD.java

Run the example project:

.. code:: bash

    java -cp bin nz.mega.megacrudexample.MEGACRUD
