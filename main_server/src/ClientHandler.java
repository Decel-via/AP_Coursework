import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable{  //Instances will be excuted by a seprate thread

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>(); // keeps track of all clients for sending messages
    private Socket socket;
    private BufferedReader bufferedReader; // used to read data/messages from our client
    private BufferedWriter bufferedWriter; // used to send data/messages to other clients to our client
    private String clientusername; //client user

    public ClientHandler(Socket socket){ // accepts the socket
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));  // this allows us to send characters
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));  // this allows us to read characters
            this.clientusername = bufferedReader.readLine(); //assigns a client the username they enter
            clientHandlers.add(this);
            broadcastMessage("SERVER: " + clientusername + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }


    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); // program will stop until a user sends a message
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }
    public void broadcastMessage(String messageToSend) { // send message to the whole server
        for (ClientHandler clientHandler : clientHandlers){ // represent each client handler for each client
            try {
                if(!clientHandler.clientusername.equals(clientusername)) { // applies to everyone expect writer
                   clientHandler.bufferedWriter.write(messageToSend); // send the message
                   clientHandler.bufferedWriter.newLine(); // creates a new line
                   clientHandler.bufferedWriter.flush();  //  buffer  is full, so it can be sent down the output stream
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }

        }
        }
    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientusername + " has left the chat!");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler(); // closses all streams
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
    }


