package org.discord.utils;

import java.util.Random;

@SuppressWarnings("unused")
public class ArrayUtils {
	public static <T> T randomPick(T[] arr, Random random) {
		return arr[random.nextInt(arr.length)];
	}
	public static boolean contains(char[] arr, char value) {
		for (char c : arr) {
			if (c == value) return true;
		}
		return false;
	}
	public static <T> boolean contains(T[] arr, T value) {
		for (T t : arr) {
			if (t == value) return true;
		}
		return false;
	}
}
