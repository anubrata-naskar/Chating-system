import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SendM {
    public static string sendMessage(String peerIP, int peerPort, String message) {
        System.out.println("Received message - "+peerIP+" , "+peerPort+" - "+message);

        return "OK";
        // try (
        //     Socket socket = new Socket(peerIP, peerPort);
        //     PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)
        // ) {
        //     // Send the message to the specified peer
        //     writer.println(message);

        // } catch (IOException e) {
        //     e.printStackTrace();
        // }
    }
}
