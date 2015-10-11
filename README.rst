
.. ATTENTION::
   Please note, work in progress

MEGA SDK CRUD Java Example
==========================
MEGA Ltd provides a Software Development Kit (SDK) for its cloud storage services for a variety of high-level languages, including Java.

This README describes how to install the MEGA SDK Java bindings and run a basic code example application from Ubuntu. Familiarity with Bash command terminal or similar is recommended.

Installation
------------

.. @TODO Script automating the following steps here

Prepare System
``````````````

.. code:: bash

    sudo apt-get install build-essential autoconf libtool git-core

Install Dependencies
`````````````````````

.. code:: bash

    sudo apt-get install libcrypto++-dev zlib1g-dev libsqlite3-dev libssl-dev libc-ares-dev libcurl4-openssl-dev libfreeimage-dev libreadline6-dev swig2.0 default-jdk

Build & Compile 
```````````````

.. code:: bash
    
    git clone https://github.com/meganz/sdk
  
.. code:: bash

    cd sdk

.. code:: bash

    sh autogen.sh

.. code:: bash
    
    ./configure --enable-java --with-java-include-dir=/usr/lib/jvm/java-7-openjdk-i386/include/

.. code:: bash
    
    make

.. code:: bash

    sudo make install


Run Example Code
----------------

.. code:: bash
    
    git clone https://code.developers.mega.co.nz/jaimesbooth/MegaCRUDJavaPractice.git

.. code:: bash

    mkdir -p MegaCRUDJavaPractice/libs

.. code:: bash
    
    cp bindings/java/.libs/libmegajava.so MegaCRUDJavaPractice/libs

.. code:: bash
    
    mkdir -p MegaCRUDJavaPractice/src/nz/mega/sdk
    cp bindings/java/nz/mega/sdk/*.java MegaCRUDJavaPractice/src/nz/mega/sdk

Enter Credentials
`````````````````
The ``credentials.txt`` file is used to automate the login process. Place the user's MEGA login details on a separate line as indicated.

.. code:: bash
    
    gedit ~/sdk/MEGASDKCRUDJavaExample/credentials.txt

Run MEGACRUDJavaExample
```````````````````````
The example code can be run either from the terminal (A) **or** from an IDE (B).

A. From Terminal
'''''''''''''''''

.. code:: bash
    
    mkdir -p MegaCRUDJavaPractice/bin
  
.. code:: bash

    cd MegaCRUDJavaPractice

.. code:: bash
    
    javac -d bin -sourcepath src src/nz/mega/megacrudexample/MEGACRUD.java

.. code:: bash

    java -cp bin nz.mega.megacrudexample.MEGACRUD

B. From an IDE
''''''''''''''

Remove the Android specific bindings:
 
.. code:: bash

    rm MegaCRUDJavaPractice/java/src/nz/mega/sdkMegaUtilsAndroid.java
    
.. code:: bash

    rm MegaCRUDJavaPractice/java/src/nz/mega/AndroidGfxProcessor.java
    
.. code:: bash

    rm MegaCRUDJavaPractice/java/src/nz/mega/MegaApiAndroid.java

Import the ``MegaCRUDJavaPractice`` project into your favourite Java IDE, for example https://www.jetbrains.com/idea/. Build and run ``MEGACRUD.java``.

Congratulations! You have successfully completed these instructions.

.. NOTE::
    This guide was tested on Ubuntu 15.04 and is adapted from: https://github.com/meganz/sdk/blob/master/README.md and https://help.ubuntu.com/community/CompilingEasyHowTo
    