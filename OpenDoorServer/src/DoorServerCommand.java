import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;


public class DoorServerCommand {

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
	public static final int nAesEncryptedPwd =  16;
	public static final int nPropertyCode = 1;
	public static final int nStatus = 1;
	public static final int nPropertyValue = -1;// 不确定长

	public static final String COMMNAD_NAME_SP = "SP";
	public static final String COMMNAD_NAME_OP = "OP";

	public static String getCommandName(byte arr[]) {
		int nIndexStart = 0;
		ByteBuffer byteBuffer = ByteBuffer.allocate(arr.length);
		byteBuffer.put(arr);

		nIndexStart += nPackLength;
		byte name[] = new byte[nCommandNameLength];
		byteBuffer.position(nIndexStart);
		byteBuffer.get(name, 0, nCommandNameLength);

		String commandName = "";
		try {
			commandName = new String(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}

		return commandName;
	}

	static class OpenDoorServerCommandStream {
		private short packLength;// 2
		private String commandName;// 2
		private byte[] aseEncryptedPwd;// 16

		public void parserCommand(byte arr[]) {
			
			ByteBuffer byteBuffer = ByteBuffer.allocate(nPackLength + nCommandNameLength + nAesEncryptedPwd);
			byteBuffer.put(arr);
			
			byteBuffer.position(0);
			packLength = byteBuffer.getShort();
			

			byte name[] = new byte[nCommandNameLength];
			byteBuffer.get(name, 0, nCommandNameLength);
			try {
				setCommandName(new String(name, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			
			aseEncryptedPwd = new byte[nAesEncryptedPwd];
			byteBuffer.get(aseEncryptedPwd, 0, nAesEncryptedPwd);
			
		}

		public String toString() {
			
			byte aseEncryptedPwdTmp[] = AES.decrypt(AES.key, AES.initVector, aseEncryptedPwd);
			try {
				return "OpenDoorServerCommandStream => packLength:" + packLength + "/commandName:" + getCommandName()
						+ "/aseEncryptedPwd:" + new String(aseEncryptedPwdTmp, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}

		public String getCommandName() {
			return commandName;
		}

		public void setCommandName(String commandName) {
			this.commandName = commandName;
		}

		public byte[] getAseEncryptedPwd() {
			return aseEncryptedPwd;
		}

		public void setAseEncryptedPwd(byte []aseEncryptedPwd) {
			this.aseEncryptedPwd = aseEncryptedPwd;
		}
	}

	public static class SetPropertyStreamServer {
		private short packLength;// 2
		private String commandName;// 2
		private byte[] aseEncryptedPwd;// 16
		private byte propertyCode;// 1
		private byte propertyValue[];// n

		public void parserCommand(byte arr[]) {

			
			ByteBuffer byteBuffer = ByteBuffer.allocate(arr.length);
			byteBuffer.put(arr);
			byteBuffer.position(0);

			packLength = byteBuffer.getShort();
			

			byte name[] = new byte[nCommandNameLength];
			byteBuffer.get(name, 0, nCommandNameLength);
			try {
				setCommandName(new String(name, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
			

			aseEncryptedPwd = new byte[nAesEncryptedPwd];
			byteBuffer.get(aseEncryptedPwd, 0, nAesEncryptedPwd);
			
			

			setPropertyCode(byteBuffer.get());
			

			int propertyValueLength = packLength - byteBuffer.position();
			propertyValue = new byte[propertyValueLength];
			byteBuffer.get(propertyValue, 0, propertyValueLength);
		}

		public String toString() {
			byte aseEncryptedPwdTmp [];
			aseEncryptedPwdTmp = AES.decrypt(AES.key, AES.initVector, aseEncryptedPwd);
			try {
				return "SetPropertyStreamServer => packLength:" + packLength + "/commandName:" + getCommandName()
						+ "/aseEncryptedPwd:" + new String(aseEncryptedPwdTmp, "UTF-8") + "/propertyCode:" + getPropertyCode() + "/propertyValue:"
						+ new String(propertyValue, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "";
		}

		public String getCommandName() {
			return commandName;
		}

		public void setCommandName(String commandName) {
			this.commandName = commandName;
		}

		public byte getPropertyCode() {
			return propertyCode;
		}

		public void setPropertyCode(byte propertyCode) {
			this.propertyCode = propertyCode;
		}
	}
	
	static public class OpenDoorReturnParser {
		private short packLength;// 2
		private String commandName;// 2
		private byte status;

		//包长度校验
		public static final int packLengthCheck = 5;
		
		public void init(String parCommandName, byte parStatus)
		{
			packLength = packLengthCheck;
			this.commandName = parCommandName;
			this.status = parStatus;
		}
		
		public byte[] getBytes()
		{
			ByteBuffer byteBuffer = ByteBuffer.allocate(packLength);
			
			byteBuffer.putShort(packLength);
			

			byteBuffer.put(ByteStringTool.convertAsciiStringToBytes(commandName), 0, commandName.length());
			

			byteBuffer.put(status);
			return byteBuffer.array();
			
		}
	}
	
	static public class PropertySetReturnParser {
		private short packLength;// 2
		private String commandName;// 2
		private byte propertyCode;// 1
		private byte status;

		//包长度校验
		public static final int packLengthCheck = 6;
		public void init(String parCommandName, byte parPropertyCode, byte parStatus)
		{
			packLength = 6;
			this.commandName = parCommandName;
			this.propertyCode = parPropertyCode;
			this.status = parStatus;
		}
		
		public byte[] getBytes()
		{
			ByteBuffer byteBuffer = ByteBuffer.allocate(packLength);
			
			byteBuffer.putShort(packLength);
			

			byteBuffer.put(ByteStringTool.convertAsciiStringToBytes(commandName), 0, commandName.length());
			
			
			byteBuffer.put(propertyCode);
			
			
			byteBuffer.put(status);
			return byteBuffer.array();
			
		}
	}
}
