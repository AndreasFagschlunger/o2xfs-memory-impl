package at.o2xfs.memory.impl.win32;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import at.o2xfs.common.Bits;

public final class Address {

	private final byte[] value;

	private Address(byte[] value) {
		if (value.length == 0) {
			throw new IllegalArgumentException("value must not be empty");
		}
		this.value = value.clone();
	}

	public byte[] getValue() {
		return value.clone();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(value).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Address) {
			Address address = (Address) obj;
			return new EqualsBuilder().append(value, address.value).isEquals();
		}
		return false;
	}

	@Override
	public String toString() {
		return Long.toHexString(Bits.getLong(value));
	}

	public static Address build(byte[] value) {
		return new Address(value);
	}
}
