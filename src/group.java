import java.util.List;

/**
 * 分组说明：
 * 10个相邻像素点一大组
 * 每5个一小组
 */

public class group {
    //像素分组类
    static final int NumOfPixel = 5;//每个小组中的像素数量
    pixel[] group1 = new pixel[NumOfPixel];//一个分组中的第一小组
    pixel[] group2 = new pixel[NumOfPixel];//第二小组
    int amount1 = 0;//第一小组中的像素点的总数量
    int amount2 = 0;//同上
    byte stat = 1;//状态，取值范围{1,0}，1表示amount1>amount2，0表示amount2>=amount1


    /**
     * 构造函数需要传入一个像素对象的队列，要求该队列的长度是NumOfPixel的2倍
     */
    group(List<pixel> pixelList) {
        int len = pixelList.size();
        if (len != 2 * NumOfPixel)//可有可无但我就是想写的检查
            throw new RuntimeException("传入像素点数量异常");
        //以下两个循环执行分组并计数
        for (int i = 0; i < NumOfPixel; i++) {
            pixel p = pixelList.get(i);
            group1[i] = p;
            amount1 += p.getNum();
        }
        for (int i = NumOfPixel; i < len; i++) {
            pixel p = pixelList.get(i);
            group2[i - NumOfPixel] = p;
            amount2 += p.getNum();
        }
        //若第二小组的数量较多，将类型设置为0型
        if (amount2 > amount1)
            this.stat = 0;
    }

    /**
     * 写入函数，具体的写入方法需要在process类的write方法中实现，这里仅返回修改方式代号
     * 0代表不做改变
     * 1代表需要将第一小组的值部分转到第二小组
     * 2代表需要将第二小组的值部分转到第一小组
     *
     * @param secretmsg:嵌入这一组像素的秘密信息的比特值（1比特）
     */
    public byte write(byte secretmsg) {
        switch (secretmsg) {
            case 0:
                if (stat == 0)
                    return 0;
                return 2;
            case 1:
                if (stat == 1)
                    return 0;
                return 1;
            default:
                throw new RuntimeException("秘密信息不是二进制信息");//可有可无的检查
        }
    }
}
