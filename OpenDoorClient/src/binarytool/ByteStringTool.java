package binarytool;

public class ByteStringTool {
	
	public static byte [] convertAsciiStringToBytes(String value)
	{
		byte result[] = new byte[value.length()];
		for(int i=0; i<value.length(); i++)
		{
			result[i] = (byte)value.charAt(i);
		}
		return result;
	}
}
