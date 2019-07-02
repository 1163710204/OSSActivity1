public class pixel {
    //像素类
    final byte pixelVal;//像素值，取值范围[0,255]
    private int num = 0;//图像中该像素值的像素点的数量

    pixel(byte pixelVal) {
        this.pixelVal = pixelVal;
    }

    /**
     * 将该像素值所对应的像素点的数量加一
     */
    public void addNum() {
        this.num++;
    }

    public int getNum() {
        return num;
    }
}
