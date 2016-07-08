import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import binarytool.AES;
import binarytool.DoorClientCommand;
import binarytool.DoorClientCommand.OpenDoorReturnParser;
import binarytool.DoorClientCommand.SetPropertyReturnParser;

public class DoorClient {
	
	private static final String serverHostname = new String("127.0.0.1");
	private static final int port = 10008;
	
	//123abcAB 
	static byte[] password = {86, 37, 41, 20, 98, -35, -78, 61, 118, 48, -118, 28, -7, 113, 57, 69};
	
	public static Map<DoorClientCommand.PropertyIndex, Byte> setProperty(Map<DoorClientCommand.PropertyIndex, String> par) throws IOException
	{
		Socket echoSocket = new Socket(serverHostname, port);
		OutputStream out = echoSocket.getOutputStream();
		InputStream in = echoSocket.getInputStream();

		Map<DoorClientCommand.PropertyIndex, Byte> retult = new HashMap<DoorClientCommand.PropertyIndex, Byte>();
		
		Set set = par.keySet();
		Iterator<DoorClientCommand.PropertyIndex> it = set.iterator();
		while(it.hasNext())
		{
			DoorClientCommand.PropertyIndex key = it.next();
			String value = par.get(key);
			byte returnParser[] = DoorClientCommand.getSetPropertyStream(DoorClientCommand.COMMNAD_NAME_SP, password, key, value);
			out.write(returnParser);
			out.flush();
			
			byte setPropertyReturn[] = new byte[SetPropertyReturnParser.packLengthCheck];
			in.read(setPropertyReturn, 0, SetPropertyReturnParser.packLengthCheck);
			
			SetPropertyReturnParser PropertySetReturnParser =  DoorClientCommand.parseSetPropertyReturn(setPropertyReturn);
			
			retult.put(key,PropertySetReturnParser.getStatus());
		}

		in.close();
		out.close();
		echoSocket.close();
		return retult;
	}
	
	public static Map<DoorClientCommand.PropertyIndex, String> getProperty(Set<DoorClientCommand.PropertyIndex> indexSet) throws UnknownHostException, IOException
	{
		
		Socket echoSocket = new Socket(serverHostname, port);
		OutputStream out = echoSocket.getOutputStream();
		InputStream in = echoSocket.getInputStream();


		Iterator<DoorClientCommand.PropertyIndex> it = indexSet.iterator();
		while(it.hasNext())
		{
			
			DoorClientCommand.PropertyIndex n = it.next();
			out.write(DoorClientCommand.getGetPropertyStream(DoorClientCommand.COMMNAD_NAME_GP, n));
			out.flush();
			
			byte returnData[] = new byte[200];
			int nByteReaded = in.read(returnData);
			DoorClientCommand.GetPropertyReturnParser getPropertyReturnParser =  new DoorClientCommand.GetPropertyReturnParser();
			
			if(getPropertyReturnParser.init(returnData, nByteReaded))
			{
				System.out.println(getPropertyReturnParser.getPropertyCode()+"/value="+getPropertyReturnParser.getValue());
			}

		}
		
		in.close();
		out.close();
		echoSocket.close();
		
		return null;
	}
	
	public static byte openDoor() throws IOException
	{
		Socket echoSocket = new Socket(serverHostname, port);
		OutputStream out = echoSocket.getOutputStream();
		InputStream in = echoSocket.getInputStream();
		
		byte password[] = AES.encrypt(AES.key, AES.initVector, "123abcAB".getBytes("UTF-8"));
		byte returnParser[] = DoorClientCommand.getOpenDoorCommandStream(DoorClientCommand.COMMNAD_NAME_OP, password);
		out.write(returnParser);
		out.flush();
		
		byte setPropertyReturn[] = new byte[OpenDoorReturnParser.packLengthCheck];

		int nReaded = in.read(setPropertyReturn);
		OpenDoorReturnParser PropertySetReturnParser =  DoorClientCommand.parseOpenDoorReturn(setPropertyReturn);
		
		in.close();
		out.close();
		echoSocket.close();
		
		return PropertySetReturnParser.getStatus();
	}
	
	public static void main(String[] args) throws IOException {

		try {
			Map<DoorClientCommand.PropertyIndex, String> par = new HashMap<DoorClientCommand.PropertyIndex, String>();
			par.put(DoorClientCommand.PropertyIndex.DOOR_PASSWORD, "odpwd123");
			par.put(DoorClientCommand.PropertyIndex.BEIJING_TIME, "100");
			par.put(DoorClientCommand.PropertyIndex.MQTT_IP, "100.123.34.56");
			par.put(DoorClientCommand.PropertyIndex.WIFI_AP_FREQUENCY_RANGE, "23.6");
			
			setProperty(par);
			
			Set<DoorClientCommand.PropertyIndex> indexSet = new HashSet<DoorClientCommand.PropertyIndex>();
			indexSet.add(DoorClientCommand.PropertyIndex.DOOR_PASSWORD);
			indexSet.add(DoorClientCommand.PropertyIndex.BEIJING_TIME);
			indexSet.add(DoorClientCommand.PropertyIndex.MQTT_IP);
			indexSet.add(DoorClientCommand.PropertyIndex.WIFI_AP_FREQUENCY_RANGE);
			
			getProperty(indexSet);
			
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: " + serverHostname);
			System.exit(1);
		}

	}
}
