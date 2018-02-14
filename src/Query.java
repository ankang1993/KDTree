import java.io.*;
import java.util.Stack;

public class Query {
    private Node node;

    public void setNode(Node node) {
        this.node = node;
    }

    private double distance(double[] a, double[] b) {
//        double sum = 0;
//        for (int i = 0; i < a.length; i++) {
//            sum += Math.pow(a[i] - b[i], 2);
//        }
//        return sum;
        return mapDistance(a[1], a[0], b[1], b[0]);
    }

    /**
     * 计算地球上任意两点(经纬度)距离
     *
     * @param long1 第一点经度
     * @param lat1  第一点纬度
     * @param long2 第二点经度
     * @param lat2  第二点纬度
     * @return double
     */
    private double mapDistance(double long1, double lat1, double long2, double lat2) {
        double a, b, R;
        R = 6378137; // 地球半径
        lat1 = lat1 * Math.PI / 180.0;
        lat2 = lat2 * Math.PI / 180.0;
        a = lat1 - lat2;
        b = (long1 - long2) * Math.PI / 180.0;
        double d;
        double sa2, sb2;
        sa2 = Math.sin(a / 2.0);
        sb2 = Math.sin(b / 2.0);
        d = 2 * R
                * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)
                * Math.cos(lat2) * sb2 * sb2));
        return d;
    }

    /**
     * 在max和min表示的超矩形中的点和点a的最小距离
     *
     * @param a   点a
     * @param max 超矩形各个维度的最大值
     * @param min 超矩形各个维度的最小值
     * @return 超矩形中的点和点a的最小距离
     */
    private double minDistance(double[] a, double[] max, double[] min) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max[i])
                sum += Math.pow(a[i] - max[i], 2);
            else if (a[i] < min[i]) {
                sum += Math.pow(min[i] - a[i], 2);
            }
        }

        return sum;
    }

    private double[][] query(double[] input) {
        Stack<Node> stack = new Stack<>();
        while (!node.isLeaf) {
            if (input[node.partitionDimension] < node.partitionValue) {
                stack.add(node.right);
                node = node.left;
            } else {
                stack.push(node.left);
                node = node.right;
            }
        }
        /**
         * 首先按树一路下来，得到一个相对较近的距离，再找比这个距离更近的点
         */
        double[] dis = new double[2];
        dis[0] = distance(input, node.value);
        dis[1] = Double.MAX_VALUE;
        double[][] near = new double[2][2];
        near[0] = node.value;
        near = queryRec(input, dis, stack, near);
        return near;
    }

    private double[][] queryRec(double[] input, double[] dis, Stack<Node> stack, double[][] near) {
        double[] nearest = near[0];
        double[] nearer = near[1];
        Node node = null;
        double tDis;
        while (stack.size() != 0) {
            node = stack.pop();
            if (node.isLeaf) {
                tDis = distance(input, node.value);
                if (tDis < dis[0]) {
                    dis[0] = tDis;
                    nearer = nearest;
                    nearest = node.value;
                } else if (tDis < dis[1]) {
                    dis[1] = tDis;
                    nearer = node.value;
                }
            } else {
            /*
             * 得到该节点代表的超矩形中点到查找点的最小距离mindistance
             * 如果minDistance<distance[1]表示有可能在这个节点的子节点上找到更近的点
             * 否则不可能找到
             */
                double minDistance = minDistance(input, node.max, node.min);
                if (minDistance < dis[1]) {
                    while (!node.isLeaf) {
                        if (input[node.partitionDimension] < node.partitionValue) {
                            stack.add(node.right);
                            node = node.left;
                        } else {
                            stack.push(node.left);
                            node = node.right;
                        }
                    }
                    tDis = distance(input, node.value);
                    if (tDis < dis[0]) {
                        dis[0] = tDis;
                        nearer = nearest;
                        nearest = node.value;
                    } else if (tDis < dis[1]) {
                        dis[1] = tDis;
                        nearer = node.value;
                    }
                }
            }
        }
        double[][] ret = new double[2][2];
        ret[0] = nearest;
        ret[1] = nearer;
        return ret;
    }

    public static void main(String[] args) {
        Object temp = null;
        File file = new File("C:/OMP/kdTree.txt");
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            temp = objIn.readObject();
            objIn.close();
            System.out.println("成功读取文件！");
        } catch (IOException e) {
            System.out.println("读取文件失败！");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Query q = new Query();
        q.setNode((Node) temp);
//        Scanner s = new Scanner(System.in);
//        System.out.println("请输入需要查找点的横坐标：");
//        dot[0] = Double.valueOf(s.nextLine());
//        System.out.println("请输入需要查找点的纵坐标：");
//        dot[1] = Double.valueOf(s.nextLine());
        String line = null;
        FileReader f = null;
        BufferedReader br = null;
        FileWriter fw = null;
        try {
            f = new FileReader("C:/OMP/points.txt");
            br = new BufferedReader(f);
            fw = new FileWriter("C:/OMP/points_out.txt");
            BufferedWriter out = new BufferedWriter(fw);
            while ((line = br.readLine()) != null) {
                if (line.length() == 0) continue;
                String[] str = line.split(",");
                double[] dot = new double[2];
                dot[0] = Double.valueOf(str[11]);
                dot[1] = Double.valueOf(str[10]);
                double[][] result = q.query(dot);
                double error = 0;
                if ((result[0][0] == dot[0] && result[0][1] == dot[1])
                        || (result[1][0] == dot[0] && result[1][1] == dot[1])
                        || ((result[0][0] - dot[0]) / (result[0][1] - dot[1]) == (result[1][0] - dot[0]) / (result[1][1] - dot[1]))) {
                    out.write(line + "," + dot[1] + "," + dot[0]);
                    error = q.mapDistance(dot[1], dot[0], Double.valueOf(str[7]), Double.valueOf(str[8]));
                }
                else {
                    double[] crossNode = new double[2];
                    crossNode[0] = 0.5 * ((dot[1] - result[0][1]) * (result[1][0] - result[0][0]) / (result[1][1] - result[0][1]) + result[0][0] + dot[0]);
                    crossNode[1] = 0.5 * ((dot[0] - result[0][0]) * (result[1][1] - result[0][1]) / (result[1][0] - result[0][0]) + result[0][1] + dot[1]);
                    out.write(line + "," + crossNode[1] + "," + crossNode[0]);
                    error = q.mapDistance(crossNode[1], crossNode[0], Double.valueOf(str[7]), Double.valueOf(str[8]));
                }
                error /= 1000;
                out.write("," + error + "," + (Double.valueOf(str[9]) - error) + "\r\n");
            }
            out.close();
            fw.close();
            br.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在，请重新确认！");
        } catch (IOException e) {
            System.out.println("文件读取错误！");
        }
        System.out.println("匹配成功！");
//        System.out.println("距离您输入点距离最近的两个点为：");
//        System.out.println(result[0][0] + "," + result[0][1]);
//        System.out.println(result[1][0] + "," + result[1][1]);
//        if (result[0][2] == result[1][2]) {
//            System.out.println("距离最近的两个点位于同一条道路上，道路ID为：");
//            System.out.println((int) result[0][2]);
//            double[] crossNode = new double[2];
//            crossNode[0] = 0.5 * ((dot[1] - result[0][1]) * (result[1][0] - result[0][0]) / (result[1][1] - result[0][1]) + result[0][0] + dot[0]);
//            crossNode[1] = 0.5 * ((dot[0] - result[0][0]) * (result[1][1] - result[0][1]) / (result[1][0] - result[0][0]) + result[0][1] + dot[1]);
//            System.out.println("查询点对应在道路上的点坐标为：");
//            System.out.println(crossNode[0] + "," + crossNode[1]);
//        } else {
//            System.out.println("距离最近的两个点位于不同道路上，道路ID分别为：");
//            System.out.println(result[0][2] + " , " + result[1][2]);
//        }
    }
}