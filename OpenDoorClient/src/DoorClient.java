import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

import binarytool.AES;
import binarytool.DoorClientCommand;
import binarytool.DoorClientCommand.OpenDoorReturnParser;
import binarytool.DoorClientCommand.PropertySetReturnParser;

public class DoorClient {
	
	public static void setPropertyTest(OutputStream out, InputStream in) throws IOException
	{
		byte[] password = AES.encrypt(AES.key, AES.initVector, "123abcAB".getBytes("UTF-8"));
		byte returnParser[] = DoorClientCommand.getSetPropertyStream(DoorClientCommand.COMMNAD_NAME_SP, password, DoorClientCommand.PropertyIndex.DOOR_PASSWORD, "Babc123A");
		out.write(returnParser);
		out.flush();
		
		byte setPropertyReturn[] = new byte[PropertySetReturnParser.packLengthCheck];
		//in.read(setPropertyReturn, 0, PropertySetReturnParser.packLengthCheck);
		int nReaded = in.read(setPropertyReturn);
		PropertySetReturnParser PropertySetReturnParser =  DoorClientCommand.parseSetPropertyReturn(setPropertyReturn);
		System.out.println(PropertySetReturnParser.toString());
	}
	
	
	public static void openDoorTest(OutputStream out, InputStream in) throws IOException
	{
		byte password[] = AES.encrypt(AES.key, AES.initVector, "123abcAB".getBytes("UTF-8"));
		byte returnParser[] = DoorClientCommand.getOpenDoorCommandStream(DoorClientCommand.COMMNAD_NAME_OP, password);
		out.write(returnParser);
		out.flush();
		
		byte setPropertyReturn[] = new byte[OpenDoorReturnParser.packLengthCheck];
		//in.read(setPropertyReturn, 0, OpenDoorReturnParser.packLengthCheck);
		int nReaded = in.read(setPropertyReturn);
		OpenDoorReturnParser PropertySetReturnParser =  DoorClientCommand.parseOpenDoorReturn(setPropertyReturn);
		System.out.println(PropertySetReturnParser.toString());
	}
	
	public static void main(String[] args) throws IOException {

		String serverHostname = new String("127.0.0.1");

		Socket echoSocket = null;
		OutputStream out = null;
		InputStream in = null;

		try {
			echoSocket = new Socket(serverHostname, 10008);
			out = echoSocket.getOutputStream();
			in = echoSocket.getInputStream();
			setPropertyTest(out, in);
			in.close();
			out.close();
			echoSocket.close();
			
			
			echoSocket = new Socket(serverHostname, 10008);
			out = echoSocket.getOutputStream();
			in = echoSocket.getInputStream();
			
			openDoorTest(out, in);
			out.close();
			in.close();
			echoSocket.close();
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: " + serverHostname);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
			System.exit(1);
		}

	
	}
}
