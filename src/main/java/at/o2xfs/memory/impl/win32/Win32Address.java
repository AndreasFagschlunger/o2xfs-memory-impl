package at.o2xfs.memory.impl.win32;

import at.o2xfs.common.Hex;
import at.o2xfs.memory.core.Address;

public class Win32Address extends Address {

	private Win32Address(byte[] value) {
		super(value);
	}

	private byte[] reverse(byte[] result) {
		for (int i = 0; i < result.length / 2; i++) {
			byte temp = result[i];
			result[i] = result[result.length - i - 1];
			result[result.length - i - 1] = temp;
		}
		return result;
	}

	@Override
	public String toString() {
		return Hex.encode(reverse(getValue()));
	}

	public static Win32Address build(byte[] value) {
		return new Win32Address(value);
	}
}
