import lombok.Data;

import java.util.List;

/**
 * Created by zhao.wu on 2016/12/8.
 */
@Data
public class Files{
    private long length;
    private String md5sum;
    private List<String> path;

    public Files() {
    }
    //getter and setter  and tostring
}