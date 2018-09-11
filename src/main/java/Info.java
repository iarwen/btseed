import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * Created by zhao.wu on 2016/12/8.
 */
@Data
public class Info{
    private String name;
    private byte[] pieces;
    private long piecesLength;
    private long length;
    private String md5sum;
    private List<Files> files;

    public Info() {
    }

    //getter and setter  and tostring
}