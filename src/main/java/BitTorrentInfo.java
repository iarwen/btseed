import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhao.wu on 2016/12/8.
 */
@Data
public class BitTorrentInfo {
    public static List<String> keyList;
    static{
        String[] keys = {"announce", "announce-list", "creation date", "comment", "created by",
                "info", "length", "md5sum", "name", "piece length","pieces", "files", "path"};
        keyList = Arrays.asList(keys);
    }

    private String announce;
    private List<String> announceList;
    private long creationDate;
    private String comment;
    private String createBy;
    private Info info;

    public BitTorrentInfo() {
    }

    //getter and setter  and tostring

    public void setValue(String key, Object value) throws Exception {
        if(!keyList.contains(key)){
            throw new Exception("not contains this key: " + key);
        }else{
            switch (key){
                case "announce":this.setAnnounce(value.toString());break;
                case "announce-list":this.getAnnounceList().add(value.toString());break;
                case "creation date":this.setCreationDate(Long.parseLong(value.toString()));break;
                case "comment":this.setComment(value.toString());break;
                case "created by":this.setCreateBy(value.toString());break;
                case "length":
                    List<Files> filesList1 = this.getInfo().getFiles();
                    if(filesList1 != null){
                        Files files = this.getInfo().getFiles().get(filesList1.size()-1);
                        files.setLength(Long.parseLong(value.toString()));
                    }else {
                        this.getInfo().setLength(Long.parseLong(value.toString()));
                    }
                    break;
                case "md5sum":
                    List<Files> filesList2 = this.getInfo().getFiles();
                    if(filesList2 != null){
                        Files files = this.getInfo().getFiles().get(filesList2.size()-1);
                        files.setMd5sum(value.toString());
                    }else {
                        this.getInfo().setMd5sum(value.toString());
                    }
                    break;
                case "name":
                    this.getInfo().setName(value.toString());
                    break;
                case "piece length":
                    this.getInfo().setPiecesLength(Long.parseLong(value.toString()));
                    break;
                case "pieces":
                    this.getInfo().setPieces((byte[])value);
                    break;
                case "path":
                    List<Files> filesList3 = this.getInfo().getFiles();
                    Files files3 = filesList3.get(filesList3.size()-1);
                    files3.getPath().add(value.toString());
                    break;
            }
        }
    }    
}