import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class dataFormat {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String oldData = simpleDateFormat.format(new Date());
        Date newData = simpleDateFormat.parse(oldData);
        System.out.println("oldData:"+oldData+"      "+"newData:"+newData);
    }
}
