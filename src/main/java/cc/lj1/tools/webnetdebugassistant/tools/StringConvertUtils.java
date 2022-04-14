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

    public static String toHexString(byte[] buffer, int offset, int size) {
        if(buffer == null)
            return "";
        StringBuilder stringBuilder = new StringBuilder();
        for(int i = offset, maxSize = Math.min(buffer.length, offset + size); i < maxSize; i++) {
            stringBuilder.append(String.format("%02x", buffer[i] & 0xFF));
//            stringBuilder.append(Integer.toHexString(buffer[i] & 0xFF));
        }
        return stringBuilder.toString();
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
