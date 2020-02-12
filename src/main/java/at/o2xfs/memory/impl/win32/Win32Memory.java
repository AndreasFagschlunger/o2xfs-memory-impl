package at.o2xfs.memory.impl.win32;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.common.Bits;
import at.o2xfs.common.ByteArrayBuffer;
import at.o2xfs.common.Hex;
import at.o2xfs.memory.databind.ReadableMemory;

public final class Win32Memory implements ReadableMemory {

	private final Address address;
	private int offset = 0;

	Win32Memory(Address address) {
		this.address = Objects.requireNonNull(address, "address must not be null");
	}

	private boolean isNull(Address address) {
		byte[] value = address.getValue();
		for (int i = 0; i < value.length; i++) {
			if (value[i] != 0) {
				return false;
			}
		}
		return true;
	}

	public Address getAddress() {
		return address;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String nextString() {
		ByteArrayBuffer buffer = new ByteArrayBuffer(32);
		do {
			buffer.append(read(1));
		} while (buffer.byteAt(buffer.length() - 1) != 0);
		return new String(buffer.buffer(), 0, buffer.length() - 1, StandardCharsets.US_ASCII);
	}

	@Override
	public long nextUnsignedLong() {
		return Bits.getInt(read(Integer.BYTES)) & 0xffffffff;
	}

	@Override
	public int nextUnsignedShort() {
		return Bits.getShort(read(Short.BYTES)) & 0xffff;
	}

	@Override
	public Win32Memory nextReference() {
		Address reference = Address.build(read(address.getValue().length));
		if (isNull(reference)) {
			return null;
		}
		return new Win32Memory(reference);
	}

	public void free() {
		Win32MemorySystem.INSTANCE.free(this);
	}

	@Override
	public byte[] read(int length) {
		System.out.print(getAddress() + ": ");
		byte[] result = Win32MemorySystem.INSTANCE.read(this, length);
		System.out.println(Hex.encode(result));
		offset += length;
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("address", address).append("offset", offset).toString();
	}
}
