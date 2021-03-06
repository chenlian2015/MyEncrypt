

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class DoorClientCommand {
	public enum PropertyIndex {
		WIFI_SSID, // -string
		SCAN_RANGE, // 单位米-int
		WIFI_CONNECTED_MAX, // WIFI最大连接数-int
		BEIJING_TIME, // 单位秒-int
		DOOR_PASSWORD, // 门禁密码-string
		WIFI_AP_FREQUENCY_RANGE, // ap频段-byte[4](hight:2byte low:2byte)
		WIFI_BROADCASE_TURNEL, // 0为自动,1-11为其他信道
		BLUETOOTH_BROADCAST_TIME, // -int
		BLUETOOTH_EMISSION_POWER, // -int
		LOCKAGAIN_TIME, // -int
		BLUETOOTH_CONNECT_TIME, // -int
		DOOR_TYPE, //// -byte
		COMMUNITY_ID, // 小区ID-byte
		FLOOR_ID, // 楼号ID-byte
		UNIT_ID, // 单元ID-byte
		MQTT_IP, // -byte[8]
		MQTT_PORT, // -int
		MQTT_PASSWORD, // -string
		MQTT_LOGIN_NAME// -string
	}

	public static final int nPackLength = 2;
	public static final int nCommandNameLength = 2;
	public static final int nAesEncryptedPwd =  24;//需要base64加密，所以16位变成了24位
	public static final int nPropertyCode = 1;
	public static final int nStatus = 1;
	public static final int nPropertyValue = -1;// 不确定长

	
	public static final String COMMNAD_NAME_SP = "SP";
	public static final String COMMNAD_NAME_OP = "OP";
	
	public static byte[] getOpenDoorCommandStream(String parCommandName, String parPassword, String parPropertyValue,
			PropertyIndex parPropertyType)
	{
		OpenDoorCommandStream openDoorCommandStream = new OpenDoorCommandStream();
		return openDoorCommandStream.init(parCommandName, parPassword, parPropertyValue, parPropertyType);
	}
	
	public static byte[] getSetPropertyStream(String parCommandName, String parPassword,
			PropertyIndex parPropertyType, String parPropertyValue)
	{
		SetPropertyStream setPropertyStream = new SetPropertyStream();
		return setPropertyStream.init(parCommandName, parPassword, parPropertyType, parPropertyValue);
	}
	
	public static PropertySetReturnParser parseSetPropertyReturn(byte[] array)
	{
		PropertySetReturnParser returnParser = new PropertySetReturnParser();
		if(returnParser.init(array))
		{
			return returnParser;
		}
		return null;
	}
	
	public static  OpenDoorReturnParser parseOpenDoorReturn(byte[] array)
	{
		OpenDoorReturnParser openDoorReturnParser = new OpenDoorReturnParser();
		if(openDoorReturnParser.init(array))
		{
			return openDoorReturnParser;
		}
		return null;
	}
	
	
	static class OpenDoorCommandStream
	{
		private byte packLength[];// 2
		private byte commandName[];// 2
		private byte aseEncryptedPwd[];// 16
		
		public boolean checkCommandLengthOk() {
			return (packLength.length == nPackLength) && (commandName.length == nCommandNameLength)
					&& (aseEncryptedPwd.length == nAesEncryptedPwd);
		}
		
		public byte[] init(String parCommandName, String parPassword, String parPropertyValue,
				PropertyIndex parPropertyType) {
			
			initCommandName(parCommandName);
			initAesEncryptedPwd(parPassword);

			int wholeLength = DoorClientCommand.nPackLength + commandName.length + aseEncryptedPwd.length;
			initPackLength((short) wholeLength);
			
			if (checkCommandLengthOk()) {

				ByteBuffer bb = ByteBuffer.allocate(wholeLength);

				int nIndexStart = 0;
				bb.put(packLength, nIndexStart, packLength.length);
				nIndexStart += packLength.length;

				bb.put(commandName, nIndexStart, commandName.length);
				nIndexStart += commandName.length;

				bb.put(aseEncryptedPwd, nIndexStart, aseEncryptedPwd.length);
				nIndexStart += aseEncryptedPwd.length;

				return bb.array();
			}
			
			return null;
		}
		
		public void initPackLength(short nLength) {
			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.putShort(nLength);
			packLength = bb.array();
		}

		public void initCommandName(String parCommandName) {
			commandName = ByteStringTool.convertAsciiStringToBytes(parCommandName);
		}

		public void initAesEncryptedPwd(String password) {
			aseEncryptedPwd = ByteStringTool.convertAsciiStringToBytes(password);
		}
		
	}
	
	static public class OpenDoorReturnParser {
		private short packLength;// 2
		private String commandName;// 2
		private byte status;

		//包长度校验
		public static final int packLengthCheck = 5;

		public String toString()
		{
			return "OpenDoorReturnParser =>packLength:"+packLength+"/commandName:"+commandName+"/status:"+status;
		}
		
		public boolean init(byte[] array) {
			ByteBuffer bb = ByteBuffer.allocate(array.length);
			bb.put(array);

			int nIndexStart = 0;
			packLength = bb.getShort(nIndexStart);
			nIndexStart += nPackLength;
			if(packLength != array.length || (packLengthCheck != packLength))
			{
				return false;
			}
			
			byte name[] = new byte[nCommandNameLength];
			bb.get(name, nIndexStart, DoorClientCommand.nCommandNameLength);
			try {
				commandName = new String(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}

			if(!(DoorClientCommand.COMMNAD_NAME_OP.equals(commandName) || 
					DoorClientCommand.COMMNAD_NAME_SP.equals(commandName)))
			{
				return false;
			}
			nIndexStart += name.length;
			

	
			status = bb.get(nIndexStart);
			nIndexStart += nStatus;
			
			return true;
		}
	}
	
	static public class PropertySetReturnParser {
		private short packLength;// 2
		private String commandName;// 2
		private byte propertyCode;// 1
		private byte status;

		//包长度校验
		public static final int packLengthCheck = 6;
		public String toString()
		{
			return "PropertySetReturnParser => packLength:"+packLength+"/commandName:"+commandName+"/propertyCode:"+propertyCode+"/status:"+status;
		}
		
		public boolean init(byte[] array) {
			ByteBuffer bb = ByteBuffer.allocate(array.length);
			bb.put(array);

			int nIndexStart = 0;
			packLength = bb.getShort(nIndexStart);
			nIndexStart += nPackLength;
			if(packLength != array.length || (packLengthCheck != packLength))
			{
				return false;
			}
			
			byte name[] = new byte[nCommandNameLength];
			bb.get(name, nIndexStart, DoorClientCommand.nCommandNameLength);
			try {
				commandName = new String(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}

			if(!(DoorClientCommand.COMMNAD_NAME_OP.equals(commandName) || 
					DoorClientCommand.COMMNAD_NAME_SP.equals(commandName)))
			{
				return false;
			}
			nIndexStart += name.length;
			

			propertyCode = bb.get(nIndexStart);
			nIndexStart += nPropertyCode;

			status = bb.get(nIndexStart);
			nIndexStart += nStatus;
			
			return true;
		}
	}

	static class SetPropertyStream {

		private byte packLength[];// 2
		private byte commandName[];// 2
		private byte aseEncryptedPwd[];// 16
		private byte propertyCode;// 1
		private byte propertyValue[];// n

		public boolean checkCommandLengthOk() {
			return (packLength.length == nPackLength) && (commandName.length == nCommandNameLength)
					&& (aseEncryptedPwd.length == nAesEncryptedPwd);
		}

		public byte[] init(String parCommandName, String parPassword,
				PropertyIndex parPropertyType, String parPropertyValue) {

			initCommandName(parCommandName);
			initAesEncryptedPwd(parPassword);
			initPropertyCode((byte) parPropertyType.ordinal());
			initPropertyValue(parPropertyValue, parPropertyType);

			int wholeLength = DoorClientCommand.nPackLength + commandName.length + aseEncryptedPwd.length + DoorClientCommand.nPropertyCode + propertyValue.length;
			initPackLength((short) wholeLength);

			if (checkCommandLengthOk()) {

				ByteBuffer bb = ByteBuffer.allocate(wholeLength);

				int nIndexStart = 0;
				bb.put(packLength, nIndexStart, packLength.length);
				nIndexStart += packLength.length;

				bb.put(commandName, nIndexStart, commandName.length);
				nIndexStart += commandName.length;

				bb.put(aseEncryptedPwd, nIndexStart, aseEncryptedPwd.length);
				nIndexStart += aseEncryptedPwd.length;

				bb.put(nIndexStart, propertyCode);
				nIndexStart += 1;

				bb.put(propertyValue, nIndexStart, propertyValue.length);
				nIndexStart += propertyValue.length;

				return bb.array();
			}

			return null;
		}

		public void initPackLength(short nLength) {
			ByteBuffer bb = ByteBuffer.allocate(2);
			bb.putShort(nLength);
			packLength = bb.array();
		}

		public void initCommandName(String parCommandName) {
			commandName = ByteStringTool.convertAsciiStringToBytes(parCommandName);
		}

		public void initAesEncryptedPwd(String password) {
			aseEncryptedPwd = ByteStringTool.convertAsciiStringToBytes(password);
		}

		public void initPropertyCode(byte index) {
			propertyCode = index;
		}

		public boolean initPropertyValue(String value, PropertyIndex type) {
			if (type == PropertyIndex.WIFI_SSID) {
				int nLength = value.length();
				propertyValue = new byte[nLength];
				for (int i = 0; i < nLength; i++) {
					char c = value.charAt(i);
					byte b = (byte) c;
					propertyValue[i] = b;
				}
			}

			if (type == PropertyIndex.SCAN_RANGE) {
				ByteBuffer bf = ByteBuffer.allocate(4);
				bf.putInt(Integer.parseInt(value));
				byte[] scanBinary = bf.array();
			}

			if (type == PropertyIndex.MQTT_IP) {
				String[] ip = value.split(".");
				if (ip.length != 4) {
					return false;
				}

				propertyValue = new byte[8];
				int nIndex = 0;
				for (String it : ip) {
					short st = Short.parseShort(it);
					ByteBuffer bf = ByteBuffer.allocate(4);
					bf.putShort(st);
					byte ipN[] = bf.array();
					propertyValue[nIndex++] = ipN[0];
					propertyValue[nIndex++] = ipN[1];
				}
			}
			return true;
		}

	}
}
