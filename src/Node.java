public class Node implements java.io.Serializable {
    private static final long serialVersionUID = 922L;
    //分割的维度
    int partitionDimension;
    //分割的值
    double partitionValue;
    //如果为非叶子节点，该属性为空，否则为数据
    double[] value;
    //是否为叶子
    boolean isLeaf = false;
    //左子树
    Node left;
    //右子树
    Node right;
    //每个维度的最小值
    double[] min;
    //每个维度的最大值
    double[] max;
}
