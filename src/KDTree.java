import java.io.*;
import java.util.ArrayList;

public class KDTree {

    private Node node;

    private KDTree() {
    }

    public void setKdTree(Node node) {
        this.node = node;
    }

    /**
     * 计算给定维度的方差
     *
     * @param data      数据
     * @param dimension 维度
     * @return 方差
     */
    private double variance(ArrayList<double[]> data, int dimension) {
        double vSum = 0;
        double sum = 0;
        for (double[] d : data) {
            sum += d[dimension];
            vSum += d[dimension] * d[dimension];
        }
        int n = data.size();
        return vSum / n - Math.pow(sum / n, 2);
    }

    /**
     * 取排序后的中间位置数值
     *
     * @param data      数据
     * @param dimension 维度
     * @return
     */
    private double median(ArrayList<double[]> data, int dimension) {
        double[] d = new double[data.size()];
        int i = 0;
        for (double[] k : data) {
            d[i++] = k[dimension];
        }
        return findPos(d, 0, d.length - 1, d.length / 2);
    }

    private double[][] maxMin(ArrayList<double[]> data, int dimensions) {
        double[][] mm = new double[2][dimensions];
        //初始化 第一行为min，第二行为max
        for (int i = 0; i < dimensions; i++) {
            mm[0][i] = mm[1][i] = data.get(0)[i];
            for (int j = 1; j < data.size(); j++) {
                double[] d = data.get(j);
                if (d[i] < mm[0][i]) {
                    mm[0][i] = d[i];
                } else if (d[i] > mm[1][i]) {
                    mm[1][i] = d[i];
                }
            }
        }
        return mm;
    }

    /**
     * 使用快速排序，查找排序后位置在point处的值
     * 比Array.sort()后取对应位置值，大约快30%
     *
     * @param data  数据
     * @param low   参加排序的最低点
     * @param high  参加排序的最高点
     * @param point 位置
     * @return
     */
    private double findPos(double[] data, int low, int high, int point) {
        int lowT = low;
        int highT = high;
        double v = data[low];
        ArrayList<Integer> same = new ArrayList<Integer>((int) ((high - low) * 0.25));
        while (low < high) {
            while (low < high && data[high] >= v) {
                if (data[high] == v) {
                    same.add(high);
                }
                high--;
            }
            data[low] = data[high];
            while (low < high && data[low] < v)
                low++;
            data[high] = data[low];
        }
        data[low] = v;
        int upper = low + same.size();
        if (low <= point && upper >= point) {
            return v;
        }

        if (low > point) {
            return findPos(data, lowT, low - 1, point);
        }

        int i = low + 1;
        for (int j : same) {
            if (j <= low + same.size())
                continue;
            while (data[i] == v)
                i++;
            data[j] = data[i];
            data[i] = v;
            i++;
        }

        return findPos(data, low + same.size() + 1, highT, point);
    }

    /**
     * 循环构建树
     *
     * @param node       节点
     * @param data       数据
     * @param dimensions 数据的维度
     */
    private void build(Node node, ArrayList<double[]> data, int dimensions) {
        if (data.size() == 1) {
            node.isLeaf = true;
            node.value = data.get(0);
            return;
        }

        //if (data.size() == 0) return;

        //选择方差最大的维度
        node.partitionDimension = -1;
        double var = -1;
        double tmpVar = 0;
        for (int i = 0; i < dimensions; i++) {
            tmpVar = variance(data, i);
            if (tmpVar > var) {
                var = tmpVar;
                node.partitionDimension = i;
            }
        }
        //如果方差=0，表示所有数据都相同，判定为叶子节点
        if (var == 0) {
            node.isLeaf = true;
            node.value = data.get(0);
            return;
        }

        //选择分割的值
        node.partitionValue = median(data, node.partitionDimension);
        double[][] maxMin = maxMin(data, dimensions);
        node.min = maxMin[0];
        node.max = maxMin[1];

        int size = (int) (data.size() * 0.55);
        ArrayList<double[]> left = new ArrayList<>(size);
        ArrayList<double[]> right = new ArrayList<>(size);
        for (double[] d : data) {
            if (d[node.partitionDimension] < node.partitionValue) {
                left.add(d);
            } else {
                right.add(d);
            }
        }
        if (left.size() == 0) {
            double min = Integer.MAX_VALUE;
            for (int i = 0, len = right.size(); i < len; i++) {
                if (right.get(i)[node.partitionDimension] < min) {
                    min = right.get(i)[node.partitionDimension];
                }
            }
            node.partitionValue = min + 0.0000001;
            ArrayList<double[]> tmp = right;
            left = new ArrayList<>(size);
            right = new ArrayList<>(size);
            for (double[] d : tmp) {
                if (d[node.partitionDimension] < node.partitionValue) {
                    left.add(d);
                } else {
                    right.add(d);
                }
            }
        }
        if (right.size() == 0) {
            double max = Integer.MIN_VALUE;
            for (int i = 0, len = left.size(); i < len; i++) {
                if (left.get(i)[node.partitionDimension] > max) {
                    max = left.get(i)[node.partitionDimension];
                }
            }
            node.partitionValue = max - 0.0000001;
            ArrayList<double[]> tmp = left;
            left = new ArrayList<>(size);
            right = new ArrayList<>(size);
            for (double[] d : tmp) {
                if (d[node.partitionDimension] < node.partitionValue) {
                    left.add(d);
                } else {
                    right.add(d);
                }
            }
        }
        Node leftNode = new Node();
        Node rightNode = new Node();
        node.left = leftNode;
        node.right = rightNode;
        build(leftNode, left, dimensions);
        build(rightNode, right, dimensions);
    }

    public static void main(String[] args) {
        ArrayList<double[]> list = new ArrayList<>();
        String line = null;
        FileReader file = null;
        BufferedReader in = null;
        try {
            file = new FileReader("sql//map_pad.txt");
            in = new BufferedReader(file);
            while ((line = in.readLine()) != null) {
                String[] str = line.split(",");
                double[] tmp = new double[3];
                tmp[0] = Double.valueOf(str[0]);
                tmp[1] = Double.valueOf(str[1]);
                tmp[2] = Double.valueOf(str[2]);
                list.add(tmp);
            }
        } catch (FileNotFoundException e) {
            System.out.println("文件不存在，请重新确认！");
        } catch (IOException e) {
            System.out.println("文件读取错误！");
        }
        Node node = new Node();
        KDTree tree = new KDTree();
        tree.setKdTree(node);
        tree.build(tree.node, list, 2);
        File f = new File("kdTree.txt");
        FileOutputStream out;
        try {
            out = new FileOutputStream(f);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(node);
            objOut.flush();
            objOut.close();
            System.out.println("成功写入文件！");
        } catch (IOException e) {
            System.out.println("写入文件失败！");
            e.printStackTrace();
        }
    }
}
