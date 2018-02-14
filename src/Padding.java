import java.io.*;
import java.text.DecimalFormat;

public class Padding {
    public static void main(String[] args) {
        String line = null;
        DecimalFormat df = new DecimalFormat("0.0000000");
        try (FileReader file = new FileReader("sql//map_sorted.txt");
             FileWriter f = new FileWriter("sql//map_pad.txt");
             BufferedReader in = new BufferedReader(file);
             BufferedWriter out = new BufferedWriter(f)) {
            line = in.readLine();
            String[] str = line.split(",");
            double[] start = new double[2], end = new double[2];
            start[0] = Double.valueOf(str[1]);
            start[1] = Double.valueOf(str[2]);
            int way = Integer.valueOf(str[3]);
            out.write(start[0] + "," + start[1] + "," + way + "\r\n");
            while ((line = in.readLine()) != null) {
                str = line.split(",");
                end[0] = Double.valueOf(str[1]);
                end[1] = Double.valueOf(str[2]);
                int curWay = Integer.valueOf(str[3]);
                if (way == curWay) {
                    double slope = (end[1] - start[1]) / (end[0] - start[0]);
                    while (start[0] + 0.001 < end[0]) {
                        start[0] += 0.001;//500m
                        start[1] += 0.001 * slope;
                        out.write(df.format(start[0]) + "," + df.format(start[1]) + "," + curWay + "\r\n");
                    }
                    out.write(end[0] + "," + end[1] + "," + curWay + "\r\n");
                    start = end;
                    end = new double[2];
                }
                else {
                    out.write(end[0] + "," + end[1] + "," + curWay + "\r\n");
                    start = end;
                    end = new double[2];
                }
                way = curWay;
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在，请重新确认！");
        } catch (IOException e) {
            System.out.println("文件读取错误！");
        }
        System.out.println("填充成功！");
    }
}