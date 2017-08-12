package com.yinhe.bighomework.utils;

import java.io.UnsupportedEncodingException;

public class ParseUtils {

	//UTF-8 code
	public static byte[] getBytes(byte[] srcBytes,int start ,int length){
		int startBit=start%8;
		int startBytePosition=0;
		int endBytePosition=0;
		
		byte startByte;
		byte endByte;
		int byteLen=0;
		if(startBit==0){
			startBytePosition=start/8+1;
			byteLen=length/8;
			startByte=srcBytes[startBytePosition];
			endBytePosition=startBytePosition+byteLen-1;
			endByte=srcBytes[endBytePosition];
		}else{
			startBytePosition=start/8;
			byteLen=length/8+1;
			startByte=(byte) (((byte)(srcBytes[startBytePosition]&0Xff)<<(8-startBit)&0xFF)>>(8-startBit));
			endBytePosition=startBytePosition+byteLen-1;
			endByte=(byte) ((((byte)(srcBytes[endBytePosition]&0xff)>>(startBit))&0xFF)<<(startBit));
		}
		
		System.out.println(" srcBytes startByte:"+String.format("%02x", srcBytes[startBytePosition]));
		System.out.println("srcBytes endByte:"+String.format("%02x", srcBytes[endBytePosition]&0xff));
		System.out.println("startByte:"+String.format("%02x", startByte));
		System.out.println("endByte:"+String.format("%02x", endByte));
		byte[] dstBytes=new byte[byteLen];
		dstBytes[0]=startByte;
		dstBytes[byteLen-1]=endByte;
		
		for(int i=startBytePosition, j=1;i<endBytePosition&&j<byteLen-2;i++,j++){
			dstBytes[j]=srcBytes[i];
		}
		return dstBytes;
	}
	
	public static String bytesToString(byte[] arr){
		try {
			String result=new String(arr, "GB08303");
			return result;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static int bytesToInteger(byte[] arr){
		if(arr.length==4){
			int result=0;
			result=(((byte)(arr[0]&0xff))<<24)+(((byte)(arr[1]&0xff))<<16)
					+(((byte)(arr[2]&0xff))<<8)+((byte)(arr[3]&0xff));
			System.out.println("bytesToInteger:"+result);
			return result;
		}
		return -1;
	}
	
	
	public static int byteToHexInteger(byte b){
		int result=Integer.parseInt(String.format("%02x", b));
		System.out.println("byteToHexInteger:"+result);
		return result;
	}
	
	@Deprecated
	public static String byteToBinarySequence(byte[] byteArr) {
		StringBuilder sb = new StringBuilder();
		for (byte b : byteArr) {
			sb.append(Integer.toBinaryString((b & 0xff) + 0x100).substring(1));
		}
		return sb.toString();
	}

	@Deprecated
	public static String getBits(String str, int start, int lenght) {
		String result = str.substring(start, start + lenght);
		return result;
	}


	@Deprecated
	public static int binarySequenceToNumber(String strBinary) {
		int result = Integer.valueOf(strBinary, 2);
		return result;
	}
	
	
	@Deprecated
	public static Long binarySequenceToLongNumber(String strBinary) {
		Long result = Long.valueOf(strBinary, 2);
		return result;
	}
	
	@Deprecated
	public static String binarySequenceToWords(String strBinary) {

		byte[] byteArr = new byte[strBinary.length() / 8];
		int count = 0;
		for (int i = 0; i < strBinary.length() / 8; i++) {
			int temp = Integer.valueOf(strBinary.substring(i * 8, i * 8 + 8), 2);
			byteArr[count++] = (byte) (temp & 0xff);
		}
		String str=null;
		try {
			str = new String(byteArr, "GB18030");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return str;
	}

}
