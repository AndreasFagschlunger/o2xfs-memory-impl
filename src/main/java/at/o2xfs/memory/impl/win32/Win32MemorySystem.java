package at.o2xfs.memory.impl.win32;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.o2xfs.common.Hex;
import at.o2xfs.common.Library;
import at.o2xfs.memory.databind.ReadableMemory;

public enum Win32MemorySystem {

	INSTANCE;

	static {
		Library.loadLibrary("o2xfs-memory-impl");
	}

	private static final Logger LOG = LogManager.getLogger(Win32MemorySystem.class);

	private native byte[] allocate0(byte[] bytes);

	private native void free0(byte[] ptr);

	private native byte[] read0(byte[] ptr, int offset, int length);

	void free(Win32Memory memory) {
		free0(memory.getAddress().getValue());
	}

	byte[] read(Win32Memory memory, int length) {
		LOG.debug("memory={},length={}", memory, length);
		byte[] result = read0(memory.getAddress().getValue(), memory.getOffset(), length);
		LOG.debug("result={}", Hex.encode(result));
		return result;
	}

	public ReadableMemory dereference(Address address) {
		return new Win32Memory(address);
	}

}
