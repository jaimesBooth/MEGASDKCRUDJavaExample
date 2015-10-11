package nz.mega.megacrudexample;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

import nz.mega.sdk.*;

import static java.nio.file.Files.deleteIfExists;

/**
 * A CRUD (Create, Read, Upload, Download) practice example which demonstrates
 * important MEGA SDK Java binding transfer methods.
 * <p>
 * based on
 * https://github.com/meganz/sdk/blob/master/examples/python/crud_example.py
 * and
 * https://github.com/meganz/sdk/blob/master/examples/java/src/nz/mega/bindingsample/CrudExample.java
 * <p>
 * This Class implements the MEGA listener interface so that events can be listened for and trigger a reaction.
 *
 * @author jaimesbooth 20150703
 * @version jaimesbooth 20150710
 * Added methods based on https://github.com/meganz/sdk/blob/master/examples/python/crud_example.py
 * @version jaimesbooth 20150711
 * Translated Python code to Java and ignored listener.
 * @version jaimesbooth 2015.07.18 Fix login not working using synchronised and notify().
 * @version jaimesbooth 2015.07.21 Login really working now from correct placement of MegaAPIJava.fetchnodes().
 * @version jaimesbooth 2015.07.22 Replaced repo for a cleaner project. Ascertained .remove() triggers
 * a MegaRequest.TYPE.REMOVE, not MegaRequest.TYPE.DELETE. Refactor CRUD into separate methods.
 * @version jaimesbooth 2015.08.10 Review and refactor comments.
 * @version jaimesbooth 2015.08.11 Fix Intermittent test file not deleted from tmp Sandbox folder by making sure all
 * methods calling the megaApiJava object are Synchronized. Add comments to implemented interface methods.
 * Review and refactor code and comments.
 * @version jaimesbooth 2015.08.18 Implement Clean up: remove /sandbox and delete returned test file.
 * Revised user feedback. Changed transferred test file to README.rst.
 * @version jaimesbooth 2015.09.29 Added new @override methods from MegaListenerInterface
 * @TODO Handle non-final synchronized megaApiJava? Multiple versions of megaApiJava objects could be instantiated.
 */
public class MEGACRUD implements MegaListenerInterface {
    /*
     * An appKey is required to access MEGA services using the MEGA SDK.
     * You can generate an appKey for your app for free @ https://mega.co.nz/#sdk
     */
    private static final String APP_KEY = "pU5ShQxB";

    /*
     * The megaApiJava object which provides access to the various MEGA storage functionality.
     */
    private MegaApiJava megaApiJava = null;

    /*
     * User's login details.
     */
    private String userEmail;
    private String password;

    /*
     * A mega node (file or folder) object which will hold the directory to do work in.
     */
    MegaNode currentWorkingDirectory = null;

    /*
     * A string for passing filenames between methods. Specifically to the listener methods.
     */
    private String fileName = "NullFileName";

    /**
     * Creates a new MEGACRUD object.
     * <p>
     * The megaApiJava object is created specifying this application's appKey. The user is logged in and the CRUD
     * examples are run.
     *
     * @throws FileNotFoundException
     */
    public MEGACRUD() throws FileNotFoundException {
        this.userEmail = "";
        this.password = "";

        if (this.megaApiJava == null) {
            // Base path to store local cache
            String path = System.getProperty("user.dir");
            this.megaApiJava = new MegaApiJava(APP_KEY, path);
            // Add the MEGACRUD listener object to listen for events when interacting with MEGA Services
            this.megaApiJava.addRequestListener(this);
        }

        getUserCredentials();

        // Run through the CRUD example functionality
        makeCRUD();
    }

    /**
     * Gets the user's MEGA account credentials.
     * <p>
     * Username is the users MEGA registered email address.
     * The user's credentials need to be read from a credentials.txt which must be
     * created in the root of this project. It should contain the user's
     * email address on the first line and their password on the second line.
     *
     * @throws FileNotFoundException
     */
    private void getUserCredentials() throws FileNotFoundException {

        /*
         * The user's credentials need to be read from the credentials.txt file which must be
         * created in the root of this project and contain the user's
         * email address on the first line and their password on the second line.
         */
        Scanner userInputScanner = new Scanner(new BufferedReader(new FileReader("credentials.txt")));
        userEmail = userInputScanner.next();
        password = userInputScanner.next();


        /*
         * Alternatively, the user's credentials can be requested at runtime
         * by commenting out the above scanner code and using the console.
         * This console will only be displayed when this application is run
         * from the system's terminal.
         */
//        Console console = System.console();
//        String userEmail = console.readLine("Email: ");
//        char[] passwordArray = console.readPassword("Password: ");
//        String password = new String(passwordArray);
    }

    /**
     * Sequential collection of relevant CRUD (Create, Read, Upload, Delete)  
     * operations on the user's Mega account via the Mega API.
     */
    private void makeCRUD() {

        login();

        getAccountDetails();

        makeTempDir();

        changeToTempDir();

        create();

        read();

        update();

        delete();

        logout();
    }

    /**
     * Logs in to the user's mega account using stored user credential fields.
     *
     */
    public void login() {
        System.out.println("");
        System.out.println("*** start: login ***");
        /*
         * Synchronized so that there can be only one object.
         * First, it is not possible for two invocations of synchronized methods on the same object to interleave.
         * When one thread is executing a synchronized method for an object, all other threads that invoke
         * synchronized methods for the same object block (suspend execution) until the first thread is
         * done with the object.
         * Second, when a synchronized method exits, it automatically establishes a happens-before
         * relationship with any subsequent invocation of a synchronized method for the same object.
         * This guarantees that changes to the state of the object are visible to all threads.
         * http://docs.oracle.com/javase/tutorial/essential/concurrency/syncmeth.html
         */
        synchronized(megaApiJava) {
            // Log in
            megaApiJava.login(userEmail, password);
            // Wait for the login process to complete.
            // The login request is not finished if the onRequestFinished()
            // method of the implemented Listener interface has not been called.
            // Once the request is finished, the listener method calls fetch nodes and notifies the megaApiJava
            // object to continue the megaApiJava thread.
            try {
                megaApiJava.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // The user has just logged in, so fetch the nodes of of the users account object so that the
            // MEGA API functionality can be used
            megaApiJava.fetchNodes();
            try {
                megaApiJava.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (megaApiJava.isLoggedIn() == 0) {
            System.out.println("Not logged in. Exiting.");
            return; // Halt if not logged in
        }
        else if (megaApiJava.isLoggedIn() > 0) {
            System.out.println(megaApiJava.getMyEmail() + " logged in");
        }

        // Set the current working directory to the logged in user's root directory
        currentWorkingDirectory = megaApiJava.getRootNode();

        System.out.println("*** done: login ***");
    }

    /*
     * Get the details of the logged in user.
     */
    public void getAccountDetails() {
        System.out.println("");
        System.out.println("*** start: User Details ***");
        // Confirm login successful by accessing the logged in user's email address
        // from the MegaAPI
        System.out.println("User email: " + megaApiJava.getMyEmail());
        // Load the user's account details. The various details will be accessed
        // from in the  implemented onRequestFinish Listener method once account details
        // request is finished.
        synchronized(megaApiJava) {
            // Get the account details of this MegaCrud object
            megaApiJava.getAccountDetails();
            // If the account details request is not finished, as ascertained by the onRequestFinished()
            // method of the implemented Listener interface, wait for the account details process to complete.
            try {
                megaApiJava.wait();
                // onRequestFinish() Listener handles notifying user of account details
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("*** done: User Details ***");
    }

    /*
     * Makes a temporary directory called sandbox.
     */
    public void makeTempDir() {
        System.out.println("");
        System.out.println("*** start: create temp directory ***");

        // Initialize the current working directory
        MegaNode checkForSandboxFolder = megaApiJava.getNodeByPath("sandbox", currentWorkingDirectory);
        if (checkForSandboxFolder == null) {
            // Wait for create folder process to complete.
            synchronized (megaApiJava) {
                // Sandbox folder does not exist, so create it.
                megaApiJava.createFolder("sandbox", currentWorkingDirectory);
                try {
                    megaApiJava.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Temporary directory /sandbox created in " + currentWorkingDirectory.getName());
        }
        else {
            // Sandbox folder exists. No further action required.
            System.out.println("Path already exists " + megaApiJava.getNodePath(checkForSandboxFolder));
        }
        System.out.println("*** done: create temp directory ***");
    }

    /*
     * Changes the current working directory to the temporary directory /sandbox.
     */
    public void changeToTempDir() {
        System.out.println("");
        System.out.println("*** start: change to temp directory ***");
        MegaNode node = megaApiJava.getNodeByPath("sandbox", currentWorkingDirectory);
        // Check to make sure sandbox directory exists
        if (node == null) {
            // sandbox does not exist
            System.out.println("No such file or directory: sandbox");
        }
        else if (node.getType() == MegaNode.TYPE_FOLDER) {
            currentWorkingDirectory = node;
            System.out.println("Working directory changed to /" + currentWorkingDirectory.getName());
        }
        else {
            System.out.println("Not a directory: sandbox");
        }
        System.out.println("*** done: change to temp directory ***");
    }

    /*
     * Uploads a file to the user's mega account.
     */
    public void create() {
        // Upload a file (create).
        System.out.println("");
        System.out.println("*** start: upload ***");
        // Wait for create file process to complete.
        synchronized (megaApiJava) {
            megaApiJava.startUpload("README.rst", currentWorkingDirectory, this);
            try {
                megaApiJava.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("*** done: upload ***");
    }

    /*
     * Downloads a file from the user's mega account.
     */
    public void read() {
        System.out.println("");
        System.out.println("*** start: download ***");
        MegaNode fileToDownload = megaApiJava.getNodeByPath("README.rst", currentWorkingDirectory);
        if (fileToDownload != null) {
            // Wait for download file process to complete.
            synchronized (megaApiJava) {
                megaApiJava.startDownload(fileToDownload, "README_returned.rst", this);
                try {
                    megaApiJava.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("Node not found: README.rst");
        }
        System.out.println("*** done: download ***");
    }

    /*
     * Uploads a file to the user's mega account.
     *
     * Note: A new upload  with the same name won't overwrite,
     * but create a new node with same name!
     */
    public void update() {
        System.out.println("");
        System.out.println("*** start: update ***");
        MegaNode oldNode = megaApiJava.getNodeByPath("README.rst", currentWorkingDirectory);
        // Wait for upload file process to complete.
        synchronized (megaApiJava) {
            // Upload a second file with the same name. A new node will be created with the same name!
            megaApiJava.startUpload("README.rst", currentWorkingDirectory, this);
            try {
                megaApiJava.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (oldNode != null) {
            // Remove the old node with the same name.
            // Take note of the name of the old node so that the listener can notify the user.
            fileName = oldNode.getName();
            // Wait for remove file process to complete.
            synchronized (megaApiJava) {
                megaApiJava.remove(oldNode);
                try {
                    megaApiJava.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Pre-existing file " + oldNode.getName() + " in /" +
                    currentWorkingDirectory.getName() + " removed.");
        } else {
            System.out.println("No existing file conflict, no old node needs removing");
        }
        System.out.println("*** done: update ***");
    }

    /*
     * Deletes a file from the user's mega account.
     */
    public void delete() {
        // Delete a file (delete).
        System.out.println("");
        System.out.println("*** start: delete ***");
        // Specify file node to delete.
        MegaNode node = megaApiJava.getNodeByPath("README.rst", currentWorkingDirectory);
        if (node != null) {
            // Make note of file being removed so it can be reported from the listener method
            fileName = node.getName();
            // Wait for remove file process to complete.
            synchronized (megaApiJava) {
                megaApiJava.remove(node);
                try {
                    megaApiJava.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("Node not found: " + node.getName());
        }
        System.out.println("*** done: delete ***");
    }

    /*
     * Logs the user out of their mega account.
     */
    public void logout() {
        System.out.println("");
        System.out.println("*** start: logout ***");

        // Clean up: remove /sandbox
        // Change to root of Mega Drive
        currentWorkingDirectory = megaApiJava.getRootNode();
        MegaNode sandboxNode = megaApiJava.getNodeByPath("sandbox", currentWorkingDirectory);
        // Make note of file being removed so it can be reported from the listener method
        fileName = sandboxNode.getName();
        // Wait for remove folder process to complete.
        synchronized (megaApiJava) {
            megaApiJava.remove(sandboxNode);
            try {
                megaApiJava.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Get path to returned local test file
        Path path = Paths.get(System.getProperty("user.dir"), "README_returned.rst");
        // Delete returned test file from local directory
        try {
            deleteIfExists(path);
            System.out.println("Test file README_returned.rst deleted from local directory");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Wait for log out process to complete.
        synchronized (megaApiJava) {
            megaApiJava.logout();
            try {
                megaApiJava.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("*** done: logout ***");
    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by finished requests.
     * <p>
     * Used in this example application to confirm that MEGA API calls are completed, wake (notify) the waiting
     * synchronized megaApi thread and notify the user of the event that has been completed.
     * This is a wide catching listener method which can be used to handle most MEGA API requests.
     * This method does not receive TYPE.DELETE requests.
     *
     * @param api The MEGA Java API object which called the request event
     * @param request The MEGA Request event type which triggered this listener
     * @param e The MEGA Error generated by the request. @TODO confirm no error returns null
     */
    @Override
    public void onRequestFinish(MegaApiJava api, MegaRequest request, MegaError e) {

        // identify the MegaRequest type which has finished and triggered this event
        int requestType = request.getType();

        if (requestType == MegaRequest.TYPE_LOGIN) {
            System.out.println("User login request finished; Result: " + e.toString() + " ");
        } else if (requestType == MegaRequest.TYPE_FETCH_NODES) {
            System.out.println("Node Fetch request finished; Result: " + e.toString() + " ");
        } else if (requestType == MegaRequest.TYPE_ACCOUNT_DETAILS) {
            // Notify user of account details.
            MegaAccountDetails accountDetails = request.getMegaAccountDetails();
            System.out.println("Storage: " + accountDetails.getStorageUsed()
                    + " of " + accountDetails.getStorageMax()
                    + " (" + String.valueOf((int)(100.0 * accountDetails.getStorageUsed()
                    / accountDetails.getStorageMax()))
                    + " %)");
            System.out.println("Pro level: " + accountDetails.getProLevel());
            System.out.println("Account Details request finished; Result: " + e.toString() + " ");
        } else if (requestType == MegaRequest.TYPE_UPLOAD) {
            System.out.println("Upload requestFinish entered");
        } else if (requestType == MegaRequest.TYPE_REMOVE) {
            System.out.println("File removed: " + fileName + "; Result: " + e.toString() + " ");
            fileName = "nullFileName";
        } else if (requestType == MegaRequest.TYPE_LOGOUT) {
            System.out.println("Log out completed; Result: " + e.toString() + " ");
        }

        // Wake (notify()) the paused (synchronized.wait()) megaApiJava object.
        synchronized (megaApiJava) {
            megaApiJava.notify();
        }
    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by the start of MEGA API Java transfer events.
     *
     * @param api The MEGA Java API object which called the started transfer event
     * @param transfer The MEGA Transfer event type which triggered this listener
     */
    @Override
    public void onTransferStart(MegaApiJava api, MegaTransfer transfer) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by the finish of transfer events.
     *
     * @param api The MEGA Java API object which called the finished transfer event
     * @param transfer The MEGA Transfer event type which triggered this listener
     * @param e The MEGA Error generated by the transfer event. @TODO confirm no error returns null
     */
    @Override
    public void onTransferFinish(MegaApiJava api, MegaTransfer transfer, MegaError e) {
        System.out.println("Transfer finished (" + transfer.getFileName() +
                "); Result: " + e.toString() + " ");
        // Signal the other thread we're done.
        synchronized (megaApiJava){
            megaApiJava.notify();
        }

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by update events.
     *
     * @param api The MEGA Java API object which called the upload event
     * @param transfer The MEGA Transfer event type which triggered this listener
     */
    @Override
    public void onTransferUpdate(MegaApiJava api, MegaTransfer transfer) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by temporary error events.
     *
     * @param api The MEGA Java API object which called the event resulting in temporary error
     * @param transfer The MEGA Transfer event type which triggered this listener
     * @param e The MEGA Error generated by the event. @TODO confirm no error returns null
     */
    @Override
    public void onTransferTemporaryError(MegaApiJava api, MegaTransfer transfer, MegaError e) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by data transfers.
     *
     * @param api The MEGA Java API object which called the data transfer event
     * @param transfer The MEGA Transfer event type which triggered this listener
     * @param buffer The buffered byte array of the data transfer
     * @return False by default
     */
    @Override
    public boolean onTransferData(MegaApiJava api, MegaTransfer transfer, byte[] buffer) {
        return false;

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by the start of requests.
     *
     * @param api The MEGA Java API object which called the request event
     * @param request The MEGA Request event type which triggered this listener
     */
    @Override
    public void onRequestStart(MegaApiJava api, MegaRequest request) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by request updates.
     *
     * @param api The MEGA Java API object which called the request event
     * @param request The MEGA Request event type which triggered this listener
     */
    @Override
    public void onRequestUpdate(MegaApiJava api, MegaRequest request) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by temporary request errors.
     *
     * @param api The MEGA Java API object which called the request event
     * @param request The MEGA Request event type which triggered this listener
     * @param e The MEGA Error generated by the event. @TODO confirm no error returns null
     */
    @Override
    public void onRequestTemporaryError(MegaApiJava api, MegaRequest request, MegaError e) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by users updates.
     *
     * @param api The MEGA Java API object which called the users update
     * @param users The Array List of users
     */
    @Override
    public void onUsersUpdate(MegaApiJava api, ArrayList<MegaUser> users) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered by nodes updates.
     *
     * @param api The MEGA Java API object which called the nodes update
     * @param nodes The Array List of nodes
     */
    @Override
    public void onNodesUpdate(MegaApiJava api, ArrayList<MegaNode> nodes) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered when a reload is needed.
     *
     * @param api The MEGA Java API object which called the needed reload
     */
    @Override
    public void onReloadNeeded(MegaApiJava api) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered when the account is updated.
     *
     * @param api The MEGA Java API object which called the account update
     */
    @Override
    public void onAccountUpdate(MegaApiJava api) {

    }

    /**
     * Implemented MEGA Listener Interface method which is triggered when contacts are updated.
     *
     * @param api The MEGA Java API object which called the needed reload
     * @param requests The list of MEGA Request events
     */
    @Override
    public void onContactRequestsUpdate(MegaApiJava api, ArrayList<MegaContactRequest> requests) {

    }

    /**
     * The main method of this application.
     *
     * @param args Command-line arguments passed to the main method
     */
    public static void main(String[] args) {

        try {
            MEGACRUD listener = new MEGACRUD();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
