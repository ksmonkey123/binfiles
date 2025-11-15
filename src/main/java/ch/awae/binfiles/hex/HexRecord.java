package ch.awae.binfiles.hex;

import java.util.Arrays;

public record HexRecord(int address, int type, byte[] data) {
    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }

}
