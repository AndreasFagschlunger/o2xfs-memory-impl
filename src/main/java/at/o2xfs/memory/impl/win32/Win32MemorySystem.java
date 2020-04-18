package at.o2xfs.memory.impl.win32;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.o2xfs.common.Hex;
import at.o2xfs.common.Library;
import at.o2xfs.memory.core.Address;
import at.o2xfs.memory.core.MemorySystem;
import at.o2xfs.memory.databind.MemoryMapper;
import at.o2xfs.memory.databind.ReadableMemory;

public enum Win32MemorySystem implements MemorySystem {

	INSTANCE;

	static {
		Library.loadLibrary("o2xfs-memory-impl");
	}

	private static final Logger LOG = LogManager.getLogger(Win32MemorySystem.class);

	static final Address NULL = Address.build(new byte[sizeof()]);

	private final MemoryMapper mapper;

	private final Map<Win32Address, Integer> allocatedMemory;

	private Win32MemorySystem() {
		mapper = new MemoryMapper();
		allocatedMemory = new HashMap<>();
	}

	private native byte[] allocate0(byte[] bytes);

	private native void free0(byte[] ptr);

	private native byte[] read0(byte[] ptr, int offset, int length);

	byte[] read(Address address, int offset, int length) {
		LOG.debug("address={},offset={},length={}", address, offset, length);
		if (NULL.equals(address)) {
			throw new NullPointerException("Can't read from NULL");
		}
		byte[] result = read0(address.getValue(), offset, length);
		LOG.debug("result={}", Hex.encode(result));
		return result;
	}

	public Win32Address allocate(byte[] value) {
		Win32Address result = Win32Address.build(allocate0(value));
		allocatedMemory.put(result, Integer.valueOf(value.length));
		return result;
	}

	public ReadableMemory dereference(Address address) {
		return new Win32Memory(this, address);
	}

	@Override
	public void free(Address address) {
		Integer size = allocatedMemory.remove(address);
		if (size != null) {
			free0(address.getValue());
		}
	}

	@Override
	public Address nullValue() {
		return NULL;
	}

	@Override
	public Address write(Object value) {
		LOG.debug("write: value={}", value);
		Address result = null;
		try (Win32MemoryGenerator gen = new Win32MemoryGenerator(this)) {
			mapper.write(gen, value);
			result = gen.allocate();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return result;
	}

	@Override
	public <T> T read(Address address, Class<T> valueType) {
		LOG.debug("read: address={},valueType={}", address, valueType);
		return mapper.read(new Win32Memory(this, address), valueType);
	}

	private static native int sizeof();
}
