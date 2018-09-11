import lombok.Data;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Function: wedo-cube. <br>
 * Date : 2018年09月07日 15:25 <br>
 *
 * @author : changwentao
 */
public class BItProtocal {
    private static char D = 'd';
    private static char L = 'l';
    private static char I = 'i';
    private static char E = 'e';
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void main(String[] args) throws IOException {
        List<Character> headers = new ArrayList<>(16);
        String fileName = "摩天营救.Skyscraper.2018.1080p.WEBRip.AAC2.0.x264-中英双字-RARBT1.torrent";
        File file = new File("C:\\Users\\changwentao\\Desktop\\" + fileName);
        System.out.println("文件名：" + fileName);
        System.out.println("BT文件大小：" + file.length() + "字节");
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(file));

        int total = br.available();
        byte[] orgin = new byte[total];
        br.read(orgin);
        br.close();

        byte[] infoHashByte = new byte[0];
        br = new BufferedInputStream(new FileInputStream(file));
        int header = br.read();
        Map<String, Object> obj = new HashMap<>(16);
        String lastLKey = null;
        String lastDKey = null;
        headers.add((char) header);
        boolean onSubMap = false;
        while (header != -1) {
            ReadIntObj readIntObj;
            byte[] byteChar;
            while (header == D) {
                readIntObj = readLength(br);
                if (readIntObj.getLastBit() == -1) {
                    header = -1;
                    break;
                }
                if (readIntObj.getLastBit() == E) {
                    headers.remove(headers.size() - 1);
                    header = headers.get(headers.size() - 1);
                    break;
                }
                Map<String, Object> _obj = onSubMap ? (Map<String, Object>) obj.get(lastDKey) : obj;

                byteChar = new byte[readIntObj.getValue()];
                String key = null;
                if (br.read(byteChar, 0, readIntObj.getValue()) != -1) {
                    key = new String(byteChar, StandardCharsets.UTF_8);
                    System.out.print("\r\n" + key + ":");
                }
                if (key.equals("info")) {
                    infoHashByte = new byte[br.available() - 1];
                    System.arraycopy(orgin, total - br.available(), infoHashByte, 0, br.available() - 1);
                }
                readIntObj = readLength(br);

                if (readIntObj.getLastBit() == I) {
                    ReadStringObj object = readFloat(br);
                    System.out.println(object.getValue());
                    _obj.put(key, object.getValue());
                } else if (readIntObj.getLastBit() == L) {
                    header = L;
                    lastLKey = key;
                    _obj.put(key, new ArrayList<String>());
                    break;
                } else if (readIntObj.getLastBit() == D) {
                    header = D;
                    headers.add((char) header);
                    lastDKey = key;
                    onSubMap = true;
                    Map<String, Object> newMap = new HashMap<>();
                    _obj.put(key, newMap);
                } else if (readIntObj.getLastBit() == E) {
                    //pop
                    headers.remove(headers.size() - 1);
                } else {
                    byteChar = new byte[readIntObj.getValue()];
                    if (br.read(byteChar, 0, readIntObj.getValue()) != -1) {
                        if (key.equals("pieces")) {
                            _obj.put(key, byteChar);
                        } else {
                            String object = new String(byteChar, StandardCharsets.UTF_8);
                            System.out.println(object);
                            _obj.put(key, object);
                        }
                    }
                }

            }
            int listHeader;
            headers.add((char) header);
            while (header == L) {
                listHeader = br.read();
                if (listHeader == E) {
                    headers.remove(headers.size() - 1);
                    header = headers.get(headers.size() - 1);
                    break;
                }
                if (listHeader == D) {
                    Map<String, Object> _obj = new HashMap<>(16);
                    List list = (List) ((Map) obj.get(lastDKey)).get(lastLKey);

                    while (listHeader == D) {
                        readIntObj = readLength(br);

                        byteChar = new byte[readIntObj.getValue()];
                        String key = null;
                        if (br.read(byteChar, 0, readIntObj.getValue()) != -1) {
                            key = new String(byteChar, StandardCharsets.UTF_8);
                            System.out.print("\r\n" + key + ":");
                        }
                        if (key.equals("info")) {
                            infoHashByte = new byte[br.available() - 1];
                            System.arraycopy(orgin, total - br.available(), infoHashByte, 0, br.available() - 1);
                        }
                        readIntObj = readLength(br);

                        if (readIntObj.getLastBit() == I) {
                            ReadStringObj object = readFloat(br);
                            System.out.println(object.getValue());
                            _obj.put(key, object.getValue());
                        } else if (readIntObj.getLastBit() == L) {
                            readIntObj = readLength(br);
                            byteChar = new byte[readIntObj.getValue()];
                            if (br.read(byteChar, 0, readIntObj.getValue()) != -1) {
                                String object = new String(byteChar, StandardCharsets.UTF_8);
                                System.out.println(object);
                                List<Object> _list = new ArrayList<>(1);
                                _list.add(object);
                                _obj.put(key, _list);
                                br.read();
                                br.read();
                            }
                            break;
                        } else if (readIntObj.getLastBit() == E) {
                            //pop
                            headers.remove(headers.size() - 1);
                        } else {
                            byteChar = new byte[readIntObj.getValue()];
                            if (br.read(byteChar, 0, readIntObj.getValue()) != -1) {

                                String object = new String(byteChar, StandardCharsets.UTF_8);
                                System.out.println(object);
                                _obj.put(key, object);
                            }
                        }
                    }
                    list.add(_obj);
                    continue;
                }
                if (listHeader == L) {
                    headers.add((char) listHeader);
                    readIntObj = readLength(br);
                    byteChar = new byte[readIntObj.getValue()];
                    String item = null;
                    if (br.read(byteChar, 0, readIntObj.getValue()) != -1) {
                        item = new String(byteChar, StandardCharsets.UTF_8);
                        System.out.print("\r\n" + item);
                    }
                    ((ArrayList<String>) obj.get(lastLKey)).add(item);
                }
                listHeader = br.read();
                if (listHeader == E) {
                    headers.remove(headers.size() - 1);
                }
            }
            onSubMap = false;
        }
        byte[] byteChar = (byte[]) obj.get("pieces");
        System.out.println("");
        for (int index = 1; index <= byteChar.length; index++) {
            System.out.print(Integer.toHexString(byteChar[index - 1] & 0xFF));
            if (index % 20 == 0) {
                System.out.println("");
            }
        }

        String infoHash = DigestUtils.sha1Hex(infoHashByte);
        System.out.println("种子infohash：" + infoHash.toUpperCase());
        //UDP
        //传入0表示让操作系统分配一个端口号
        //
        String peerId = DigestUtils.sha1Hex(System.currentTimeMillis() / 1000 + UUID.randomUUID().toString());
        String o = URLEncoder.encode("EA608838D31F516B487672819852FAE3F909EA44" , "UTF-8");
        String udpData = "info_hash=" + o + "&peer_id=000000000000" +
                peerId.substring(0, 8).toUpperCase() + "&port=80&compact=1&uploaded=0&downloaded=0&left=0&event=started";
       /* try (DatagramSocket socket = new DatagramSocket(0)) {
            socket.setSoTimeout(100000);
            String HOSTNAME = "tracker.cypherpunks.ru";
            InetAddress host = InetAddress.getByName(HOSTNAME);
            //指定包要发送的目的地
            DatagramPacket request = new DatagramPacket(udpData.getBytes(), udpData.getBytes().length, host, 6969);
            //为接受的数据包创建空间
            DatagramPacket response = new DatagramPacket(new byte[1024], 420);
            socket.send(request);
            socket.receive(response);
            String result = new String(response.getData(), 0, response.getLength(), "ASCII");
            System.out.println(result);
        } catch (IOException e) {138852	33.334650	192.168.2.199	34.234.152.9	HTTP	404	GET /announce?info_hash=%EA%60%888%D3%1FQkHvr%81%98R%FA%E3%F9%09%EAD&peer_id=%2DSD0100%2D%5C%D8%1A%14%929%2B%F5%EB%97l%B4&ip=192.168.2.146&port=12022&uploaded=652755084&downloaded=652755084&left=2076433128&numwant=200&key=30520&compact=1 HTTP/1.0
            e.printStackTrace();
        }*/
        HttpURLConnection connection = null;
        InputStream is = null;
        BufferedReader br2 = null;
        String result = null;// 返回结果字符串
        String httpUrl = "http://tracker.vanitycore.co:6969/announce?info_hash=%EA%60%888%D3%1FQkHvr%81%98R%FA%E3%F9%09%EAD&peer_id=%2DSD0100%2D%5C%D8%1A%14%929%2B%F5%EB%97l%B4&port=80&compact=1&uploaded=0&downloaded=0&left=2729188212&event=started";
        try {
            System.out.println(httpUrl);
            // 创建远程url连接对象
            URL url = new URL(httpUrl);
            // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-agent","Bittorrent");
            // 设置连接方式：get
            connection.setRequestMethod("GET");
            // 设置连接主机服务器的超时时间：15000毫秒
            connection.setConnectTimeout(15000);
            // 设置读取远程返回的数据时间：60000毫秒
            connection.setReadTimeout(60000);
            // 发送请求
            connection.connect();
            System.out.println(connection.getResponseCode());
            // 通过connection连接，获取输入流
            if (connection.getResponseCode() == 200) {
                is = connection.getInputStream();
                // 封装输入流is，并指定字符集
                br2 = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                // 存放数据
                StringBuffer sbf = new StringBuffer();
                String temp = null;
                while ((temp = br2.readLine()) != null) {
                    sbf.append(temp);
                    sbf.append("\r\n");
                }
                result = sbf.toString();
                System.out.println(result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != br2) {
                try {
                    br2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            connection.disconnect();// 关闭远程连接
        }
    }

    private static ReadIntObj readLength(BufferedInputStream br) throws IOException {
        int bit = br.read();
        int value = 0;
        while (bit >= '0' && bit <= '9') {
            char i = (char) bit;
            int val = Integer.parseInt(i + "");
            value = value * 10 + val;
            bit = br.read();
        }
        ReadIntObj readIntObj = new ReadIntObj();
        readIntObj.setLastBit(bit);
        readIntObj.setValue(value);
        return readIntObj;
    }

    private static ReadStringObj readFloat(BufferedInputStream br) throws IOException {
        int bit = br.read();
        StringBuilder value = new StringBuilder();
        while (bit != E) {
            char i = (char) bit;
            int val = Integer.parseInt(i + "");
            value.append(val);
            bit = br.read();
        }
        ReadStringObj readFloatObj = new ReadStringObj();
        readFloatObj.setLastBit(bit);
        readFloatObj.setValue(value.toString());
        return readFloatObj;
    }
}

@Data
class ReadIntObj {
    private int value;
    private int lastBit;
}

@Data
class ReadStringObj {
    private String value;
    private int lastBit;
}