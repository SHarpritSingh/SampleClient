//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//
//class UDPClient {
//	public static void main(String args[]) throws Exception {
//		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(
//				System.in));
//		DatagramSocket clientSocket = new DatagramSocket(9877);
//		InetAddress IPAddress = InetAddress.getByName("localhost");
//		byte[] sendData = new byte[1024];
//		byte[] receiveData = new byte[1024];
//		while (true) {
//			String sentence = inFromUser.readLine();
//			sendData = sentence.getBytes();
//			DatagramPacket sendPacket = new DatagramPacket(sendData,
//					sendData.length, IPAddress, 9876);
//			clientSocket.send(sendPacket);
//			DatagramPacket receivePacket = new DatagramPacket(receiveData,
//					receiveData.length);
//			clientSocket.receive(receivePacket);
//			String modifiedSentence = new String(receivePacket.getData());
//			System.out.println("FROM SERVER:" + modifiedSentence);
//			// clientSocket.close();
//		}
//	}
//}

//: c15:MultiJabberClient.java
// From 'Thinking in Java, 2nd ed.' by Bruce Eckel
// www.BruceEckel.com. See copyright notice in CopyRight.txt.
// Client that tests the MultiJabberServer
// by starting up multiple clients.
import java.net.*;
import java.io.*;

class JabberClientThread extends Thread {
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private static int counter = 0;
  private int id = counter++;
  private static int threadcount = 0;
  public static int threadCount() { 
    return threadcount; 
  }
  public JabberClientThread(InetAddress addr) {
    System.out.println("Making client " + id);
    threadcount++;
    System.out.println("thredcount ++: "+threadcount);
    try {
      socket = 
        new Socket(addr,8080);
    } catch(IOException e) {
      System.err.println("Socket failed");
      // If the creation of the socket fails, 
      // nothing needs to be cleaned up.
    }
    try {    
      in = 
        new BufferedReader(
          new InputStreamReader(
            socket.getInputStream()));
      // Enable auto-flush:
      out = 
        new PrintWriter(
          new BufferedWriter(
            new OutputStreamWriter(
              socket.getOutputStream())), true);
      start();
    } catch(IOException e) {
      // The socket should be closed on any 
      // failures other than the socket 
      // constructor:
      try {
        socket.close();
      } catch(IOException e2) {
        System.err.println("Socket not closed");
      }
    }
    // Otherwise the socket will be closed by
    // the run() method of the thread.
  }
  public void run() {
    try {
      for(int i = 0; i < 25; i++) {
        out.println("Client " + id + ": " + i);
        String str = in.readLine();
        System.out.println(str);
      }
      out.println("END");
    } catch(IOException e) {
      System.err.println("IO Exception");
    } finally {
      // Always close it:
      try {
        socket.close();
      } catch(IOException e) {
        System.err.println("Socket not closed");
      }
      threadcount--; // Ending this thread
    }
  }
}

public class Client {
  static final int MAX_THREADS = 40;
  public static void main(String[] args) 
      throws IOException, InterruptedException {
    InetAddress addr = 
      InetAddress.getByName("localhost");
   // while(true) {
      System.out.println("thread count : "+JabberClientThread.threadCount() );
    	if(JabberClientThread.threadCount() 
         < MAX_THREADS)
        new JabberClientThread(addr);
      Thread.currentThread().sleep(5000);
    //}
  }
} 