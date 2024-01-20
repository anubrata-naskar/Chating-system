import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.swing.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class UserGUI extends JFrame {

    public JTextField nameField, ipField, portField;
    public JTextArea chatHistoryTextArea;

    public DefaultListModel<String> friendsListModel;
    public Map<String, String> friendsIPMap;
    public Map<String, String> friendsPortMap;

    public UserGUI() {
        setTitle("Let'ssss Chat");
        setSize(1200, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        friendsIPMap = new HashMap<>();
        friendsPortMap = new HashMap<>();
        initComponents();
    }

    public void initComponents() {
        // Create a panel for the components with GridLayout
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // Create panel for Chat History
        JPanel chatHistoryPanel = new JPanel(new BorderLayout());
        chatHistoryTextArea = new JTextArea();
        JScrollPane chatHistoryScrollPane = new JScrollPane(chatHistoryTextArea);
        chatHistoryScrollPane.setBorder(BorderFactory.createTitledBorder("Chat History"));
        chatHistoryPanel.add(chatHistoryScrollPane, BorderLayout.CENTER);
        mainPanel.add(chatHistoryPanel);

        // Create panel for Friends
        JPanel friendsPanel = new JPanel(new BorderLayout());
        friendsListModel = new DefaultListModel<>();
        JList<String> friendsList = new JList<>(friendsListModel);
        JScrollPane friendsScrollPane = new JScrollPane(friendsList);
        friendsScrollPane.setBorder(BorderFactory.createTitledBorder("Friends"));
        friendsPanel.add(friendsScrollPane, BorderLayout.CENTER);
        mainPanel.add(friendsPanel);

        // Attach a mouse listener to the friends list
        friendsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = friendsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        String selectedFriend = friendsListModel.getElementAt(index);
                        String fPort = friendsPortMap.get(selectedFriend);
                        Integer ifPort = Integer.valueOf(fPort);

                        //try
                        try (ServerSocket serverSocket = new ServerSocket(ifPort)) {
                            System.out.println("Peer Chat is running and waiting for connections on port : "+ifPort);

                            // Accept incoming connections in a separate thread
                            new Thread(() -> {
                                while (true) {
                                    try {
                                        Socket clientSocket = serverSocket.accept();
                                        new Thread(() -> handleClient(clientSocket)).start();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }).start();
                            }

                         catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    //end-try

                        showSendMessageDialog(selectedFriend);
                        System.out.println("Mouse - " + selectedFriend);
                    }
                }
            }
        });


        // Create panel for input fields and buttons with FlowLayout
        JPanel inputPanel = new JPanel(new FlowLayout());

        // Create input fields
        nameField = new JTextField(10);
        ipField = new JTextField(10);
        portField = new JTextField(5);

        // Create buttons
        JButton addButton = new JButton("Add");

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addFriend();
            }
        });

        inputPanel.add(new JLabel("Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("IP:"));
        inputPanel.add(ipField);
        inputPanel.add(new JLabel("Port:"));
        inputPanel.add(portField);
        inputPanel.add(addButton);

        // Add input panel to the frame
        add(mainPanel);
        add(inputPanel, BorderLayout.SOUTH);
    }

 
    //try
    public  void handleClient(Socket clientSocket) {  //receiving message print to the screen
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String peerMessage;
            while ((peerMessage = reader.readLine()) != null) {
                System.out.println("Received from " + clientSocket.getInetAddress() + ": " + peerMessage);
                chatHistoryTextArea.append("Received from " + clientSocket.getInetAddress() + ": " + peerMessage + "\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //end-try


    public void addFriend() {
        String name = nameField.getText();
        String ip = ipField.getText();
        String port = portField.getText();

        if (!name.isEmpty() && !ip.isEmpty() && !port.isEmpty()) {
            // friendsListModel.addElement(name + " (" + ip + ":" + port + ")");
            friendsListModel.addElement(name);
            friendsIPMap.put(name, ip);
            friendsPortMap.put(name, port);
            nameField.setText("");
            ipField.setText("");
            portField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields");
        }
    }

    public void showSendMessageDialog(String selectedFriend) {
        String friendIP = friendsIPMap.get(selectedFriend);
        String friendPort = friendsPortMap.get(selectedFriend);

        // System.out.println("Sending message to " + friendIP + ":" + friendPort + " -
        // " +" : ");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JTextArea messageTextArea = new JTextArea(5, 20);
        JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
        panel.add(new JLabel("Enter your message:"));
        panel.add(messageScrollPane);

        int result = JOptionPane.showConfirmDialog(null, panel, "Send Message to " + selectedFriend,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String message = messageTextArea.getText();
            if (!message.isEmpty()) {
                sendToFriend(friendIP, friendPort, message);
                // System.out.println("Sending message to " + friendIP + ":" + friendPort + " -
                // " + message + " : "+selectedFriend);
                chatHistoryTextArea.append("You to " + selectedFriend + ": " + message + "\n");
            }
        }
    }

    public void sendToFriend(String friendIP, String friendPort, String message) {
        // Implement sending a message logic to the friend's IP and port
        // You can use friendIP, friendPort, etc.
        // For simplicity, let's just print the details

        // int iIP = Integer.parseInt(friendIP);
        // int iPort = Integer.parseInt(friendPort);
        // Integer iIP = Integer.valueOf(friendIP);
        Integer iPort = Integer.valueOf(friendPort);
        System.out.println("Sending message to " + friendIP + ":" + iPort + " - " + message);
        sendMessage(friendIP, iPort, message);
        // System.out.println("Sending message to " + friendIP + ":" + friendPort + " -
        // " + message);
    }

    public static void sendMessage(String peerIP, int peerPort, String message) { // sending message
        // System.out.println("Received message - "+peerIP+" , "+peerPort+" -
        // "+message);
        try (
                Socket socket = new Socket(peerIP, peerPort);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {
            // Send the message to the specified peer
            writer.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        SwingUtilities.invokeLater(() -> new UserGUI().setVisible(true));

    }
}