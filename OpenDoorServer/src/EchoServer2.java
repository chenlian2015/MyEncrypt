
import java.net.*;
import java.nio.ByteBuffer;
import java.io.*;

public class EchoServer2 extends Thread {
	protected Socket clientSocket;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(10008);
			System.out.println("Connection Socket Created");
			try {
				while (true) {
					new EchoServer2(serverSocket.accept());
				}
			} catch (IOException e) {
				System.err.println("Accept failed.");
				System.exit(1);
			}
		} catch (IOException e) {
			System.err.println("Could not listen on port: 10008.");
			System.exit(1);
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.err.println("Could not close port: 10008.");
				System.exit(1);
			}
		}
	}

	private EchoServer2(Socket clientSoc) {
		clientSocket = clientSoc;
		start();
	}

	public void run() {
	
		try {
			
			OutputStream out = clientSocket.getOutputStream();
			InputStream in = clientSocket.getInputStream();

			byte pack[] = new byte[200];
			int nReaded = in.read(pack);
			ByteBuffer bb = ByteBuffer.allocate(nReaded);
			bb.put(pack, 0, nReaded);
			
			String commandName = DoorServerCommand.getCommandName(bb.array());
			if(DoorServerCommand.COMMNAD_NAME_OP.equals(commandName))
			{
				DoorServerCommand.OpenDoorServerCommandStream openDoorServer = new DoorServerCommand.OpenDoorServerCommandStream(); 
				openDoorServer.parserCommand(bb.array());
				System.out.println(openDoorServer.toString());
				
				DoorServerCommand.OpenDoorReturnParser openDoorReturnParser = new DoorServerCommand.OpenDoorReturnParser();
				openDoorReturnParser.init(openDoorServer.getCommandName(), (byte)1);
				out.write(openDoorReturnParser.getBytes());
				
			}else if(DoorServerCommand.COMMNAD_NAME_SP.equals(commandName))
			{
				DoorServerCommand.SetPropertyStreamServer serPropertyStreamServer = new DoorServerCommand.SetPropertyStreamServer();
				serPropertyStreamServer.parserCommand(bb.array());
				System.out.println(serPropertyStreamServer.toString());
				
				DoorServerCommand.PropertySetReturnParser propertySet = new DoorServerCommand.PropertySetReturnParser();
				propertySet.init(serPropertyStreamServer.getCommandName(), serPropertyStreamServer.getPropertyCode(), (byte)1);
				out.write(propertySet.getBytes());
			}
			out.flush();
			out.close();
			in.close();
			clientSocket.close();
		} catch (IOException e) {
			System.err.println("Problem with Communication Server");
			System.exit(1);
		}
	}
}