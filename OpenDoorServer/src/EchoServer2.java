
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.io.*;

public class EchoServer2 extends Thread {
	

	public static Map<Byte, byte[]> HardDataStored = new HashMap<Byte, byte[]>();
			
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
			if (DoorServerCommand.COMMNAD_NAME_OP.equals(commandName)) {
				DoorServerCommand.OpenDoorServerCommandStream openDoorServer = new DoorServerCommand.OpenDoorServerCommandStream();
				openDoorServer.parserCommand(bb.array());
				System.out.println(openDoorServer.toString());

				DoorServerCommand.OpenDoorReturnParser openDoorReturnParser = new DoorServerCommand.OpenDoorReturnParser();
				openDoorReturnParser.init(openDoorServer.getCommandName(), (byte) 1);
				out.write(openDoorReturnParser.getBytes());
				out.flush();

			} else if (DoorServerCommand.COMMNAD_NAME_SP.equals(commandName)) {
				while (true) {
					
					DoorServerCommand.SetPropertyStreamServer serPropertyStreamServer = new DoorServerCommand.SetPropertyStreamServer();
					serPropertyStreamServer.parserCommand(bb.array());
					HardDataStored.put(serPropertyStreamServer.getPropertyCode(), serPropertyStreamServer.getPropertyValue());
					System.out.println(serPropertyStreamServer.toString());

					DoorServerCommand.PropertySetReturnParser propertySet = new DoorServerCommand.PropertySetReturnParser();
					propertySet.init(serPropertyStreamServer.getCommandName(),
							serPropertyStreamServer.getPropertyCode(), (byte) 1);
					out.write(propertySet.getBytes());
					out.flush();

					nReaded = in.read(pack);
					if (-1 != nReaded) {
						bb = ByteBuffer.allocate(nReaded);
						bb.position(0);
						bb.put(pack, 0, nReaded);
					} else {
						break;
					}
				}
			}
			else if (DoorServerCommand.COMMNAD_NAME_GP.equals(commandName))
			{
				while(true)
				{
					DoorServerCommand.GetPropertyStreamServer getPropertyStreamServer = new DoorServerCommand.GetPropertyStreamServer();
					getPropertyStreamServer.parseCommand(bb.array());
					
					byte propertyCode = (byte)(getPropertyStreamServer.getPropertyCode().ordinal());
					byte valueBytes[] = HardDataStored.get(propertyCode);
					DoorServerCommand.GetPropertyServerReturn getPropertyServerReturn = new DoorServerCommand.GetPropertyServerReturn();
					
					out.write(getPropertyServerReturn.init(propertyCode, valueBytes));
					out.flush();

					nReaded = in.read(pack);
					if (-1 != nReaded) {
						bb = ByteBuffer.allocate(nReaded);
						bb.position(0);
						bb.put(pack, 0, nReaded);
					} else {
						break;
					}
				}
			}
		} catch (IOException e) {
			System.err.println("Problem with Communication Server");
			System.exit(1);
		}
	}
}