package at.o2xfs.memory.impl.win32;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import at.o2xfs.common.ByteArrayBuffer;
import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.core.util.BaseMemoryGenerator;

public class Win32MemoryGenerator extends BaseMemoryGenerator {

	private static final int INITIAL_CAPACITY = 128;

	private final Win32MemorySystem memorySystem;
	private final List<ByteArrayBuffer> buffers;
	private Address address;

	public Win32MemoryGenerator(Win32MemorySystem memorySystem) {
		this.memorySystem = Objects.requireNonNull(memorySystem);
		buffers = new ArrayList<>();
		buffers.add(new ByteArrayBuffer(INITIAL_CAPACITY));
	}

	private ByteArrayBuffer getBuffer() {
		return buffers.get(buffers.size() - 1);
	}

	@Override
	public void close() throws IOException {
		if (isClosed()) {
			return;
		} else if (buffers.size() != 1) {
			throw new IOException("");
		}
		ByteArrayBuffer buffer = buffers.remove(buffers.size() - 1);
		address = memorySystem.allocate(buffer.toByteArray());
	}

	@Override
	public boolean isClosed() {
		return buffers.isEmpty();
	}

	@Override
	public void write(byte[] src) {
		getBuffer().append(src);
	}

	@Override
	public void writeNull() {
		write(Win32MemorySystem.NULL.getValue());
	}

	@Override
	public void startPointer() {
		buffers.add(new ByteArrayBuffer(INITIAL_CAPACITY));
	}

	@Override
	public void endPointer() {
		ByteArrayBuffer buffer = buffers.remove(buffers.size() - 1);
		if (buffer.length() == 0) {
			writeNull();
		} else {
			write(memorySystem.allocate(buffer.toByteArray()).getValue());
		}
	}

	public Address allocate() throws IOException {
		close();
		return address;
	}
}
