#自定义控件进阶，如何用简单的写缤纷复杂的自定义控件
---
一个安卓开发的朋友发我一个视频并向询问我视频中效果怎么实现，我当即给他说 ，这个简单，用帧动画就可以实现。然后就被他pass掉了，于是我只好祭出plan B。但是我当时没有时间，于是一边坐地铁一边发语音给他说步骤（导致楼主本打算做到漕河泾一不小心做到了徐家汇，然后掉头往漕河泾做的时候发现既然到了徐家汇，就在徐家汇转车最近，于是赶紧调转车头，再往徐家汇做）。做完之后觉得只要思路对了就很简单，于是给大家分享一下思路
##效果图
---
按照惯例，先上效果图，然后将关键代码和思路，下面是效果图
![效果图一](http://img.blog.csdn.net/20160830134721497)                 ![效果图二](http://img.blog.csdn.net/20160830134750559)

---
demo下载地址    [demo下载地址](https://github.com/xuanxuandaoren/DripMoney)
##实现思路和关键代码
---
如何写一个复杂的自定义控件呢，笔者一般采用MVC模式，即是模型(model)－视图(view)－控制器(controller)，而之前笔者写的两个自定义控件里均采用了这个方法，下面放上两个实例，大家有兴趣的也可以参考下

---
示例一，自定义日历控件 [自定义的日历控件](http://blog.csdn.net/gongziwushuang/article/details/52118049)
实例二 ，会在点击附近产生四处扩散的类似水珠沸腾的效果[demo下载](https://github.com/xuanxuandaoren/WaterBubble)

---
在本篇文章笔者则一开头的效果图为例为大家讲解如何以mvc方式写自定义控件
###把复杂的效果图分解成单个对象
我们怎么把自定义控件变得简单呢，首先就是把复杂的自定义控件分解为单个对象，下面我门就针对效果把他分解，看下图

---
![这里写图片描述](http://img.blog.csdn.net/20160830140322411)
首先我们根绝图可以把图中的效果分为两个部分，分别在图中以箭头标出，第一部分就是钱袋，第二部分就是单个的钱币
首先我们分别对两者进行分析，
###钱袋
我们通过效果图可知，钱袋就是一张不懂的图，我们只需要画一张钱袋作为背景就OK了
###钱币
钱币才是这个自定义空间控件的关键，乍看之下使人眼花缭乱，但是我们一一个钱币作为对象的话就会变的很简单了，下面我们针对钱币的对象进行分析
####首先，我们建立钱币类

```
/**
 * 钱的对象
 * Created by 玉光 on 2016-8-29.
 */
public class Money {
```
####分析钱币需要具备哪些属性
#####定义属性
 1. 可以看出，图中总共有三种钱币，那我们就要声明一个type代表钱币的类型
 2. 钱币在图中是可以动的，一般动的东西一般都会有x,y坐标
 3. 由于散钱的动作是抛物线，抛物线的公式`y=a*(x-b)^2+c`;而确定一个抛物线则需要顶点坐标和另外一个坐标，而a则代表了抛物线的倾斜度，因此 需要一个顶点坐标和a属性
 4. 最后再定义每次的水平移动的举例，然后根据`y=a*(x-b)^2+c`计算出y坐标。
分析过后声明变量

```
 /**
     * 钱币的x坐标
     */
    public float locationX;
    /**
     * 钱币的y坐标
     */
    public float locationY;
    /**
     * 钱币的类型
     */
    public int type;
    /**
     * 移动的距离
     */
    public int distance;
    /**
     * 抛物线的顶点
     */
    public Point fixedPoint;
    /**
     * 确定抛物线的路线的参数，即 y=a*(x-b)^2+c 里面的参数a
     */
    public float a;
```
#####定义构造函数，

```
    public Money(float x, float y) {
        fixedPoint = new Point();
        this.locationX = x;
        this.locationY = y;
        type = (int) (Math.random() * 3);
        fixedPoint.x =(int) ((Math.random() + 0.5f) * x);
        fixedPoint.y = (int) ((Math.random() + 1) * y / 3);
        distance = (int) ((fixedPoint.x - x) / 5);
        if (distance==0){
            updateDistance();
        }
        //因为 y=a*(x-b)^2+c  ，b就等于抛物线定点x的左边，c就等于抛物线顶点y的坐标，所以a=(y-c)/(x-b）^2
        a = (y - fixedPoint.y) / ((x - fixedPoint.x) * (x - fixedPoint.x));
    }

```
其中x,y代表钱币的初始位置，是要我们决定的，所以需要传入，顶点和类型为了显示多变性则为随机产生，其他的则是由定点，初始位置计算出来的。
#####定义方法
为了能使钱币出现移动的效果，我们定义一个移动位置的方法

```
  /**
     * 更新图片坐标的方法，每次x坐标移动distance，y经过抛物线算出
     */
    public void updateLocation() {
        locationX += distance;
        locationY = a * (locationX - fixedPoint.x) * (locationX - fixedPoint.x) + fixedPoint.y;
    }

```
x每次移动distance位置，y则有抛物线公式根据x计算出来

---
为了使模型和view分开，我们再定义一个画的方法

```
  /**
     * 画钱币的函数
     *
     * @param canvas
     * @param a 钱币a的图像
     * @param b 钱币b的图像
     * @param c 钱币c的图像
     * @param paint 画笔
     */
    public void drawMoney(Canvas canvas, Bitmap a, Bitmap b, Bitmap c, Paint paint) {
        switch (type) {
            case 0:
                canvas.drawBitmap(a, locationX, locationY, paint);
                break;
            case 1:
                canvas.drawBitmap(b, locationX, locationY, paint);
                break;
            case 2:
                canvas.drawBitmap(c, locationX, locationY, paint);
                break;
        }

    }
```
####我们建立控制类
声明一个moneymanager类

```
/**
 * 钱币的管理类
 * Created by 玉光 on 2016-8-29.
 */
public class MoneyManager {
```
定义两个函数，一个是产生十个钱币对象集合的方法，另外一个是更新集合内数据的方法

```
 /**
     * 产生储存十个钱对象的集合
     * @param x
     * @param y
     * @return
     */
    public static List<Money> createMoney( float x, float y) {
        moneys = new ArrayList<>();
        Money money = null;
        for (int i = 0; i < 10; i++) {
            money = new Money(x, y);
            moneys.add(money);

        }
        return moneys;
    }
```

---

```

    /**
     * 更新储存钱对象的集合
     * @param moneys
     * @param view
     * @param x
     * @param y
     * @return
     */
    public static List<Money> updateMoneys(List<Money> moneys, View view, float x, float y) {
        List<Money> moneyList=new ArrayList<>();
        int width =view.getMeasuredWidth();
        int height =view.getMeasuredHeight();
        //更新集合里面每个对象的坐标
        for (Money money : moneys) {
            money.updateLocation();
             //如果对象的范围还在控件内，则添加，不在则扔掉
            if (money.locationX>0&&money.locationX<width&&money.locationY>0&&money.locationY<height){
                moneyList.add(money);

            }
        }
         //用于控制动画时间的长短，往里面添加，就会有新的对象产生，就会产生源源不断的现象，不添加，则撒钱完毕
        if (number<10) {
            //在添加十个进去
            moneyList.addAll(createMoney(x, y));
            number++;
        }
        return moneyList;
    }
```
这样就可以更新money的坐标了
####建立View对象
首先创建moneyview对象

```
/**
 * 撒钱的试图
 * Created by 玉光 on 2016-8-29.
 */
public class MoneyView extends View {
```
初始化集合

```
//初始化集合
moneys = MoneyManager.createMoney(width / 2, height / 2 - moneyCationHeight / 10);
```
重写ondraw方法

```
   @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画钱袋
        canvas.drawBitmap(moneyCationScale, width / 2 - moneyCationWidth / 10, height / 2 - moneyCationHeight / 10, paint);
        //如果集合里没有内容就不在进行绘制
        if (moneys.size() <= 0) {
            //添加动画结束监听
            if (listener != null) {
                listener.onEnd();
            }
        } else {
            //如果集合里有内容字绘制集合，
            for (int i = 0; i < moneys.size(); i++) {
                moneys.get(i).drawMoney(canvas, money, money1, money2, paint);
            }
            //更新集合里面钱的坐标位置
            moneys = MoneyManager.updateMoneys(moneys, this, width / 2 - moneyWidth / 2, height / 2 - moneyCationHeight / 10 - moneyHeight / 2);
            //延时50毫秒重新绘制
            postInvalidateDelayed(50);
        }
    }
```
好了一个自定义控件基本上算是完成了，最后添加结束监听

```
  /**
     * 结束监听的接口
     */
    public interface OnEndListener {
        void onEnd();
    }

    private OnEndListener listener;

    /**
     * 设置结束监听
     *
     * @param listener
     */
    public void SetOnEndListener(OnEndListener listener) {
        this.listener = listener;

    }
```
##总结
一个复杂的动画就这样被我们完成了，复杂类型的自定控件最重要的对象的分析，首先把无关紧要的剥离出去，然后在确定对象，在写对象过程中要确定哪些属性是需要我们进行控制的以达到动画的效果，然后建立控制类，更新我们需要控制的属性，最后只需在view里面遍历集合，并调用绘制方法就可以了。
下面是自定义控件的结构图![自定义控件结构图](http://img.blog.csdn.net/20160830154105486)

---
作者有时间的时候会帮同行们实现一些网上不容易找到效果，如果你也需要帮助，不妨私信一下作者，欢迎大家一起交流和进步！
