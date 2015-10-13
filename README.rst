
.. ATTENTION::
   Please note, work in progress

MEGA SDK CRUD Java Example
==========================
MEGA Ltd provides a Software Development Kit (SDK) for its cloud storage services for a variety of high-level languages, including Java.

This README describes how to install the MEGA SDK Java bindings and run a basic code example application from Ubuntu. Familiarity with Bash command terminal or similar is recommended.

1 Installation
--------------

.. @TODO Script automating the following steps here

1.1 Prepare System
``````````````````

.. code:: bash

    sudo apt-get install build-essential autoconf libtool git-core

1.2 Install Dependencies
````````````````````````

.. code:: bash

    sudo apt-get install libcrypto++-dev zlib1g-dev libsqlite3-dev libssl-dev libc-ares-dev libcurl4-openssl-dev libfreeimage-dev libreadline6-dev swig2.0 default-jdk

1.3 Build & Compile 
```````````````````

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


2 Example Code
--------------

.. code:: bash
    
    git clone https://github.com/jaimesBooth/MEGASDKCRUDJavaExample.git

.. code:: bash

    mkdir -p MEGASDKCRUDJavaExample/libs

.. code:: bash
    
    cp bindings/java/.libs/libmegajava.so MEGASDKCRUDJavaExample/libs

.. code:: bash
    
    mkdir -p MEGASDKCRUDJavaExample/src/nz/mega/sdk
    
.. code:: bash

    cp bindings/java/nz/mega/sdk/*.java MEGASDKCRUDJavaExample/src/nz/mega/sdk

2.1 Enter Credentials
`````````````````````
The ``credentials.txt`` file is used to automate the login process. Place the user's MEGA login details on a separate line as indicated.

.. code:: bash
    
    gedit ~/sdk/MEGASDKCRUDJavaExample/credentials.txt

Save and close the text editor.

2.2 Run Example Code
````````````````````
The example code can be run either from a Terminal (2.2.1) **or** from an IDE (2.2.2).

.. code:: bash

    cd MEGASDKCRUDJavaExample

2.2.1 From Terminal
'''''''''''''''''''

.. code:: bash
    
    mkdir bin/

.. code:: bash
    
    javac -d bin -sourcepath src src/nz/mega/megacrudexample/MEGACRUD.java

.. code:: bash

    java -cp bin nz.mega.megacrudexample.MEGACRUD

2.2.2 From an IDE
'''''''''''''''''

Remove the Android specific bindings:
    
.. code:: bash

    rm src/nz/mega/sdk/AndroidGfxProcessor.java
    
.. code:: bash

    rm src/nz/mega/sdk/MegaApiAndroid.java

.. code:: bash

    rm src/nz/mega/sdk/MegaUtilsAndroid.java

Import the ``MEGASDKCRUDJavaExample`` project into your favourite Java IDE, for example https://www.jetbrains.com/idea/. Build and run ``MEGACRUD.java``.

Done
----
Congratulations! You have successfully completed these instructions.

.. NOTE::
    This guide was tested on Ubuntu 15.04 and is adapted from: https://github.com/meganz/sdk/blob/master/README.md and https://help.ubuntu.com/community/CompilingEasyHowTo
