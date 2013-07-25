package com.untamedears.animals.util;

public class BitUtils {
	public static boolean get(int data, int pos) {
		return (data & (1 << pos)) != 0;
	}
	
	public static int set(int data, int pos, boolean value) {
		int mask = 1 << pos;
		
		return (data & ~mask) | (value ? mask : 0);
	}
}
