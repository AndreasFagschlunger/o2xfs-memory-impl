package at.o2xfs.memory.impl.win32;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.databind.ReadableMemory;

public final class Win32Memory implements ReadableMemory {

	private final Address address;
	private int offset = 0;

	Win32Memory(Address address) {
		this.address = Objects.requireNonNull(address, "address must not be null");
	}

	public Address getAddress() {
		return address;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public Win32Memory dereference() {
		return new Win32Memory(Address.build(read(address.getValue().length)));
	}

	public void free() {
		Win32MemorySystem.INSTANCE.free(this);
	}

	@Override
	public byte[] read(int length) {
		byte[] result = Win32MemorySystem.INSTANCE.read(this, length);
		offset += length;
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("address", address).append("offset", offset).toString();
	}
}
