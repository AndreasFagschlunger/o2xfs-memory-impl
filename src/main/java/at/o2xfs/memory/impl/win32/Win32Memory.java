package at.o2xfs.memory.impl.win32;

import java.util.Objects;

import org.apache.commons.lang3.builder.ToStringBuilder;

import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.core.BaseReadableMemory;

public final class Win32Memory extends BaseReadableMemory {

	private final Win32MemorySystem memorySystem;
	private final Address address;
	private int offset = 0;

	Win32Memory(Win32MemorySystem memorySystem, Address address) {
		this.memorySystem = Objects.requireNonNull(memorySystem, "memorySystem must not be null");
		this.address = Objects.requireNonNull(address, "address must not be null");
	}

	public Address getAddress() {
		return address;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public Address nextAddress() {
		return Win32Address.build(read(address.getValue().length));
	}

	@Override
	public Win32Memory nextReference() {
		Address reference = nextAddress();
		if (Win32MemorySystem.NULL.equals(reference)) {
			return null;
		}
		return new Win32Memory(memorySystem, reference);
	}

	@Override
	public byte[] read(int length) {
		byte[] result = memorySystem.read(address, offset, length);
		offset += length;
		return result;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("address", address).append("offset", offset).toString();
	}
}
