import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * 处理过程说明：
 * 写入水印：计数->分组->写入
 * 读取水印：计数->分组->读取
 */

/**
 * 写入水印说明：
 * 水印信息必须是20比特
 * 每一个像素值组（表示为一个group对象）嵌入1比特信息
 * 若嵌入信息与group的状态值不符，则进行对应改变，否则不做改变
 * 对应改变体现在write方法的线程中
 */

public class process {
    //处理过程类
    static int NumOfPixel = group.NumOfPixel;//分组大小
    static int LengthOfMsg = 20;//水印信息长度
    static int minDifference = 50;//每组最少需要被修改的像素点的数量
    byte[] pixelInfo;//图像灰度信息
    List<pixel> pixels = null;//将像素信息转换成像素类的列表

    /**
     * 对pixels进行预处理*/ {
        pixels = new ArrayList<>();
        for (byte i = 0; i < 256; i++) {
            pixels.add(new pixel(i));
        }
//        Collections.sort(pixels);
        //这里如果担心列表中的元素不是按照像素值大小排列的，可以将pixel类实现compare接口使其可排列，然后调用上一行的方法对列表排序
    }

    /**
     * 计数每个像素值的数量
     *
     * @param pixelInfo:灰度值数组
     * @param amount:数组长度
     */
    private void count(byte[] pixelInfo, int amount) {
        this.pixelInfo = pixelInfo;
        for (int i = 0; i < amount; i++) {
            int temp = pixelInfo[i];
            if (i < 0 || i > 255) {
                throw new RuntimeException("像素值错误");
            }
            pixels.get(temp).addNum();
        }
    }

    /**
     * 将像素值按照pixel.txt中所规定的进行分组，分组既适用于写水印也适用于读水印
     * 分组说明见group类
     *
     * @return 返回分出来的像素值组的列表，列表长度理论上为20
     */
    public List<group> Segmentation() {
        List<group> result = new ArrayList<>();
        try {
            Scanner in = new Scanner(new File("pixel.txt"));
            while (in.hasNextLine()) {
                List<pixel> pixelGroup = new ArrayList<>();
                for (int i = 0; i < 2 * NumOfPixel; i++) {//每10个像素值分为一组，txt中相邻的10个为一组
                    int index = in.nextInt();
                    pixelGroup.add(pixels.get(index));
                }
                group g = new group(pixelGroup);
                result.add(g);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入水印
     *
     * @param groups:一个group的列表，要求该列表的长度必须是20
     * @param secretMsg：由秘密信息构成的字节数组，每一个元素表示水印信息的一个比特
     * @return 没有返回值
     */
    public void write(List<group> groups, byte[] secretMsg) {
        for (int i = 0; i < LengthOfMsg; i++) {//按位处理
            group g = groups.get(i);
            byte msgBit = secretMsg[i];
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    byte stat = g.write(msgBit);//得到修改方式
                    int count = 0;
                    switch (stat) {
                        case 0:
                            //不做改变
                            break;
                        case 1:
                            Set<Byte> 被修改的像素 = new HashSet<>();
                            for (int j = 0; j < NumOfPixel; j++)
                                被修改的像素.add(g.group1[j].pixelVal);
                            for (int i = 0; i < pixelInfo.length; i++) {
                                if (被修改的像素.contains(pixelInfo[i])) {
                                    pixelInfo[i] += NumOfPixel;
                                    count++;
                                }
                                if (count == minDifference)
                                    break;
                            }
                            break;
                        case 2:
                            Set<Byte> 需要修改的像素 = new HashSet<>();
                            for (int j = 0; j < NumOfPixel; j++)
                                需要修改的像素.add(g.group2[j].pixelVal);
                            for (int i = 0; i < pixelInfo.length; i++) {
                                if (需要修改的像素.contains(pixelInfo[i])) {
                                    pixelInfo[i] -= NumOfPixel;
                                    count++;
                                }
                                if (count == minDifference)
                                    break;
                            }
                            break;
                    }
                }
            }.start();
        }
    }

    /**
     * 读取水印
     *
     * @param groups:传入一个group的列表，要求该列表的长度必须是20
     * @return 返回一个比特数组，存储秘密信息
     */
    public byte[] read(List<group> groups) {
        byte[] secretMsg = new byte[LengthOfMsg];
        for (int i = 0; i < LengthOfMsg; i++)
            secretMsg[i] = groups.get(i).stat;
        return secretMsg;
    }
}
