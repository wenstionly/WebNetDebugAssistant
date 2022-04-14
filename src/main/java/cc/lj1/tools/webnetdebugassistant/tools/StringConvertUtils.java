package cc.lj1.tools.webnetdebugassistant.tools;

public class StringConvertUtils {
    public static String toHexString(byte[] buffer) {
        if(buffer == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for(byte b : buffer) {
            stringBuilder.append(Integer.toHexString(b & 0xFF));
        }
        return stringBuilder.toString();
    }

    private final static char[] HEXDIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    public static String toHexString(byte[] buffer, int offset, int size) {
        if(buffer == null || offset >= buffer.length)
            return "";
        if(offset + size > buffer.length)
            size = buffer.length - offset;
        char[] out = new char[size << 1];
        int outIdx = 0;
        while(size-- > 0) {
            final byte v = buffer[offset++];
            out[outIdx++] = HEXDIGITS[(v >> 4) & 0x0F];
            out[outIdx++] = HEXDIGITS[v & 0x0F];
        }
        return new String(out);
    }

    public static byte[] fromHexString(String string) {
        if(string == null || string.equals(""))
            return null;
        string.replace(" ", "");
        byte[] payload = new byte[string.length() / 2];
        for(int i = 0, l = payload.length; i < l; i++) {
            try {
                payload[i] = (byte)(0xFF & Integer.parseInt(string.substring(i * 2, i * 2 + 2), 16));
            }
            catch (Exception e) {
                return null;
            }
        }
        return payload;
    }

    public static String fromByteArray(byte[] raw) {
        if(raw == null)
            return null;
        if(raw.length <= 0)
            return "";
        try {
            return new String(raw, "UTF-8");
        }
        catch (Exception e) {
            return null;
        }
    }
}
