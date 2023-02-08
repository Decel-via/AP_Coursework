import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Client {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // keeps track of all clients for sending messages
    private Socket socket;
    private BufferedReader bufferedReader; // used to read data/messages from our client
    private BufferedWriter bufferedWriter; // used to send data/messages to other clients to our client
    private String username; //client user


    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); // this allows us to send characters
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // this allows us to read characters
            this.username = username; //assigns a client the username they enter

        }catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);

        }

    }

    public void sendMessage(){  // note: this is what the client handler is waiting for
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()){
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username+": " + messageToSend);
                bufferedWriter.newLine(); // creates a new line
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromGroupChat;

                while (socket.isConnected()){
                    try {
                        msgFromGroupChat = bufferedReader.readLine(); // reads the message
                        System.out.println(msgFromGroupChat); // outputs message
                    } catch (IOException e) {
                        closeEverything(socket, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter){

        try {
            if(bufferedReader!= null ) {
                bufferedReader.close();
            }
            if(bufferedWriter!= null) {
                bufferedWriter.close();
            }
            if(socket!= null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();
        Socket socket = new Socket("localhost", 1234);
        Client client = new Client(socket,username);
        if (Global.listOfNames.size()== 0) {
            System.out.println("SERVER: User " + client.username + " has become coordinator");
            client.username = client.username+ " (coordinator)";
            Global.listOfNames.add(client.username);
            System.out.println(Global.listOfNames);
        }

        client.listenForMessage();
        client.sendMessage();
    }
}
