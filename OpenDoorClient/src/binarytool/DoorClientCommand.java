package binarytool;

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
		WIFI_BROADCASE_TURNEL, // 0为自动,1-11为其他信道－byte
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

	public static final int nIPLength = 8;
	public static final int intLength = 4;
	public static final int floatLength = 4;
	public static final int byteLength = 1;
	
	public static final int nPackLength = 2;
	public static final int nCommandNameLength = 2;
	public static final int nAesEncryptedPwd = 16;
	public static final int nPropertyCode = 1;
	public static final int nStatus = 1;
	public static final int nPropertyValue = -1;// 不确定长

	public static final String COMMNAD_NAME_SP = "SP";
	public static final String COMMNAD_NAME_GP = "GP";
	public static final String COMMNAD_NAME_OP = "OD";

	public static byte[] getOpenDoorCommandStream(String parCommandName, byte[] parPassword) {
		OpenDoorCommandStream openDoorCommandStream = new OpenDoorCommandStream();
		return openDoorCommandStream.init(parCommandName, parPassword);
	}

	public static byte[] getGetPropertyStream(String parCommandName, PropertyIndex parPropertyType) {
		GetPropertyCommandStream getPropertyStream = new GetPropertyCommandStream();
		byte tmp[];
		tmp = getPropertyStream.init(parCommandName, parPropertyType);
		return tmp;
	}

	public static byte[] getSetPropertyStream(String parCommandName, byte[] parPassword, PropertyIndex parPropertyType,
			String parPropertyValue) {
		SetPropertyCommandStream setPropertyStream = new SetPropertyCommandStream();
		return setPropertyStream.init(parCommandName, parPassword, parPropertyType, parPropertyValue);
	}

	public static SetPropertyReturnParser parseSetPropertyReturn(byte[] array) {
		SetPropertyReturnParser returnParser = new SetPropertyReturnParser();
		if (returnParser.init(array)) {
			return returnParser;
		}
		return null;
	}

	public static OpenDoorReturnParser parseOpenDoorReturn(byte[] array) {
		OpenDoorReturnParser openDoorReturnParser = new OpenDoorReturnParser();
		if (openDoorReturnParser.init(array)) {
			return openDoorReturnParser;
		}
		return null;
	}

	// 开门命令数据流
	static class OpenDoorCommandStream {
	
		public byte[] init(String parCommandName, byte[] parPassword) {

			short wholeLength = (short)(DoorClientCommand.nPackLength + nCommandNameLength + parPassword.length);
			ByteBuffer bb = ByteBuffer.allocate(wholeLength);
			bb.position(0);

			bb.putShort(wholeLength);
			bb.put(ByteStringTool.convertAsciiStringToBytes(parCommandName));
			bb.put(parPassword);

			return bb.array();
		}

	}
	

	// 开门 返回数据流解析器
	static public class OpenDoorReturnParser {
		private short packLength;// 2
		private String commandName;// 2
		private byte status;

		// 包长度校验
		public static final int packLengthCheck = 5;

		public String toString() {
			return "OpenDoorReturnParser =>packLength:" + packLength + "/commandName:" + commandName + "/status:"
					+ getStatus();
		}

		public boolean init(byte[] array) {
			
			ByteBuffer bb = ByteBuffer.allocate(array.length);
			bb.put(array);
			bb.position(0);

			packLength = bb.getShort();

			if (packLength != array.length || (packLengthCheck != packLength)) {
				return false;
			}

			byte name[] = new byte[nCommandNameLength];
			bb.get(name, 0, DoorClientCommand.nCommandNameLength);
			try {
				commandName = new String(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}

			if (!(DoorClientCommand.COMMNAD_NAME_OP.equals(commandName)
					|| DoorClientCommand.COMMNAD_NAME_SP.equals(commandName))) {
				return false;
			}

			setStatus(bb.get());
			return true;
		}

		public byte getStatus() {
			return status;
		}

		public void setStatus(byte status) {
			this.status = status;
		}
	}


	
	// 设置硬件属性 返回数据流解析器
	static public class SetPropertyReturnParser {
		private short packLength;// 2
		private String commandName;// 2
		private byte propertyCode;// 1
		private byte status;

		// 包长度校验
		public static final int packLengthCheck = 6;

		public String toString() {
			return "PropertySetReturnParser => packLength:" + packLength + "/commandName:" + commandName
					+ "/propertyCode:" + propertyCode + "/status:" + getStatus();
		}

		public boolean init(byte[] array) {
			ByteBuffer bb = ByteBuffer.allocate(array.length);
			bb.put(array);
			bb.position(0);

			packLength = bb.getShort();
			if (packLength != array.length || (packLengthCheck != packLength)) {
				return false;
			}

			byte name[] = new byte[nCommandNameLength];
			bb.get(name, 0, DoorClientCommand.nCommandNameLength);
			try {
				commandName = new String(name, "UTF-8");
			} catch (UnsupportedEncodingException e) {
			}

			if (!(DoorClientCommand.COMMNAD_NAME_OP.equals(commandName)
					|| DoorClientCommand.COMMNAD_NAME_SP.equals(commandName))) {
				return false;
			}

			propertyCode = bb.get();
			setStatus(bb.get());
			return true;
		}

		public byte getStatus() {
			return status;
		}

		public void setStatus(byte status) {
			this.status = status;
		}
	}

	// 读取硬件属性 命令数据流
	static class GetPropertyCommandStream {
		
		public byte[] init(String parCommandName, PropertyIndex parPropertyType) {

		   short wholeLength = (short) (DoorClientCommand.nPackLength + nCommandNameLength
					+ DoorClientCommand.nPropertyCode);

			ByteBuffer bb = ByteBuffer.allocate(wholeLength);
			bb.position(0);
			bb.putShort(wholeLength);
			bb.put(ByteStringTool.convertAsciiStringToBytes(parCommandName));
			bb.put((byte) parPropertyType.ordinal());
			
			return bb.array();
		}
	}

	// 读取硬件属性 返回数据流
	public static class GetPropertyReturnParser {
		private short packLength;
		private String commandName;
		private byte propertyCode;
		private String value = "";

		public boolean init(byte data[], int length) throws UnsupportedEncodingException {
			ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
			byteBuffer.put(data, 0, length);
			byteBuffer.position(0);

			packLength = byteBuffer.getShort();
			if (packLength != length) {
				return false;
			}

			byte name[] = new byte[nCommandNameLength];
			byteBuffer.get(name);
			commandName = new String(name, "UTF-8");

			setPropertyCode(byteBuffer.get());

			int valueLength = packLength - byteBuffer.position();
			byte valueByte[] = new byte[valueLength];
			byteBuffer.get(valueByte);
			getPropertyValue(valueByte);

			return true;
		}

		public boolean getPropertyValue(byte data[]) throws UnsupportedEncodingException {
			PropertyIndex type = PropertyIndex.values()[getPropertyCode()];
			// byte
			if (type == PropertyIndex.DOOR_TYPE || type == PropertyIndex.COMMUNITY_ID || type == PropertyIndex.FLOOR_ID
					|| type == PropertyIndex.UNIT_ID || type == PropertyIndex.WIFI_BROADCASE_TURNEL) {
				if(data.length != byteLength)
				{
					return false;
				}
				setValue("" + data[0]);
			}

			// String
			if (type == PropertyIndex.WIFI_SSID || type == PropertyIndex.DOOR_PASSWORD
					|| type == PropertyIndex.MQTT_PASSWORD || type == PropertyIndex.MQTT_LOGIN_NAME) {
				setValue(new String(data, "UTF-8"));
			}

			// float 整形部分和小数部分都为不得大于2位
			if (type == PropertyIndex.WIFI_AP_FREQUENCY_RANGE) {

				if(data.length != floatLength)
				{
					return false;
				}
				
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
				byteBuffer.put(data);
				byteBuffer.position(0);

				short low;
				short high;
				high = byteBuffer.getShort();
				low = byteBuffer.getShort();
				setValue(high + "." + low);
			}

			// int
			if (type == PropertyIndex.SCAN_RANGE || type == PropertyIndex.WIFI_CONNECTED_MAX
					|| type == PropertyIndex.BEIJING_TIME || type == PropertyIndex.BLUETOOTH_BROADCAST_TIME
					|| type == PropertyIndex.BLUETOOTH_EMISSION_POWER || type == PropertyIndex.LOCKAGAIN_TIME
					|| type == PropertyIndex.BLUETOOTH_CONNECT_TIME || type == PropertyIndex.MQTT_PORT) {

				if(data.length != intLength)
				{
					return false;
				}
				
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
				byteBuffer.put(data);
				byteBuffer.position(0);
				setValue("" + byteBuffer.getInt());
			}

			// ip "23.23.23.123"
			if (type == PropertyIndex.MQTT_IP) {
				
				if(data.length != nIPLength)
				{
					return false;
				}
				
				ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
				byteBuffer.put(data);
				byteBuffer.position(0);
				
				
				setValue(getValue() + ""+byteBuffer.getShort());
				setValue(getValue() + "."+byteBuffer.getShort());
				setValue(getValue() + "."+byteBuffer.getShort());
				setValue(getValue() + "."+byteBuffer.getShort());
			}
			return true;
		}

		public byte getPropertyCode() {
			return propertyCode;
		}

		public void setPropertyCode(byte propertyCode) {
			this.propertyCode = propertyCode;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	// 设置硬件属性 命令数据流
	static class SetPropertyCommandStream {


		public byte[] init(String parCommandName, byte parPassword[], PropertyIndex parPropertyType,
				String parPropertyValue) {

			byte propertyValue[] = initPropertyValue(parPropertyValue, parPropertyType);
			
			short wholeLength = (short)(DoorClientCommand.nPackLength + DoorClientCommand.nCommandNameLength + DoorClientCommand.nAesEncryptedPwd
					+ DoorClientCommand.nPropertyCode + propertyValue.length);

			ByteBuffer bb = ByteBuffer.allocate(wholeLength);
			
			bb.putShort(wholeLength);
			bb.put(ByteStringTool.convertAsciiStringToBytes(parCommandName));
			bb.put(parPassword);
			bb.put((byte) parPropertyType.ordinal());
			bb.put(propertyValue);
			
			return bb.array();

		}


		public byte[] initPropertyValue(String value, PropertyIndex type) {

			byte propertyValue[] = null;
			// byte
			if (type == PropertyIndex.DOOR_TYPE || type == PropertyIndex.COMMUNITY_ID || type == PropertyIndex.FLOOR_ID
					|| type == PropertyIndex.UNIT_ID || type == PropertyIndex.WIFI_BROADCASE_TURNEL) {
				propertyValue = new byte[1];
				propertyValue[0] = Byte.parseByte(value);
			}

			// String
			if (type == PropertyIndex.WIFI_SSID || type == PropertyIndex.DOOR_PASSWORD
					|| type == PropertyIndex.MQTT_PASSWORD || type == PropertyIndex.MQTT_LOGIN_NAME) {
				propertyValue = ByteStringTool.convertAsciiStringToBytes(value);
			}

			// float 整形部分和小数部分都为不得大于2位
			if (type == PropertyIndex.WIFI_AP_FREQUENCY_RANGE) {
				String frequency[] = value.split("\\.");
				short high = Short.parseShort(frequency[0]);
				short low = Short.parseShort(frequency[1]);
				ByteBuffer byteBuffer = ByteBuffer.allocate(4);
				byteBuffer.putShort(high);
				byteBuffer.putShort(low);
				propertyValue = byteBuffer.array();
			}

			// int
			if (type == PropertyIndex.SCAN_RANGE || type == PropertyIndex.WIFI_CONNECTED_MAX
					|| type == PropertyIndex.BEIJING_TIME || type == PropertyIndex.BLUETOOTH_BROADCAST_TIME
					|| type == PropertyIndex.BLUETOOTH_EMISSION_POWER || type == PropertyIndex.LOCKAGAIN_TIME
					|| type == PropertyIndex.BLUETOOTH_CONNECT_TIME || type == PropertyIndex.MQTT_PORT) {

				ByteBuffer bf = ByteBuffer.allocate(4);
				bf.putInt(Integer.parseInt(value));
				propertyValue = bf.array();
			}

			// ip "23.23.23.123"
			if (type == PropertyIndex.MQTT_IP) {
				String[] ip = value.split("\\.");
				if (ip.length != 4) {
					return null;
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
			return propertyValue;
		}

	}
}
