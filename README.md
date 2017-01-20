![这里写图片描述](https://github.com/soulrelay/ImageLoaderUtil/blob/master/app/src/main/res/raw/catalog.png)

## <font color=#C4573C size=5 face="黑体">前言</font>
>* 图片加载是Android开发中最最基础的功能，为了降低开发周期和难度，我们经常会选用一些图片加载的开源库
>* [选取第三方SDK需要谨慎](http://blog.csdn.net/s003603u/article/details/53257859)
>* [二次封装](http://blog.csdn.net/s003603u/article/details/53257965)

<font color=#ff9866 size=4 face="黑体">注意：所有改动更新会同步到</font>[GitHub](https://github.com/soulrelay/ImageLoaderUtil)
## <font color=#C4573C size=5 face="黑体">主流图片加载库的对比</font>
>* 共同点
   * 使用简单：一句话实现图片的获取和显示
   * 可配置性高：可配置各种解码、缓存、下载机制
   * 自适应程度高：根据系统性能调整配置策略（如CPU核数决定最大并发数、内存决定内存缓存大小、网络状态变化调整最大并发数）
   * 多级缓存
   * 支持多种数据源
   * 支持多种Displayer
   * 兼容性好（可以配合okhttp等库进行使用）
### <font color=#ff9866 size=4 face="黑体">Android-Universal-Image-Loader</font>
>* 简介
      * 作者：nostra13
      * 面世时间：2011
      * star数（截止到发稿）：14509
      * [https://github.com/nostra13/Android-Universal-Image-Loader](https://github.com/nostra13/Android-Universal-Image-Loader)
>*   优点
       * 支持下载进度监听（ImageLoadingListener）
       * 可在View滚动中暂停图片加载（PauseOnScrollListener）
       * 默认实现多种内存缓存算法（最大最先删除，使用最少最先删除，最近最少使用，先进先删除，当然自己也可以配置缓存算法）
>* 缺点
      * 从2015.11.27之后不再维护，项目中不建议使用
 
### <font color=#ff9866 size=4 face="黑体">Picasso</font>
>* 简介
      * 作者：JakeWharton（Square）
      * 面世时间：2012
      * star数（截止到发稿）：12076
      * [https://github.com/square/picasso](https://github.com/square/picasso)
>*   优点
       * 包较小（100k）
       * 取消不在视野范围内图片资源的加载
       * 使用最少的内存完成复杂的图片转换
       * 自动添加二级缓存
       * 任务调度优先级处理
       * 并发线程数根据网络类型调整
       * 图片的本地缓存交给同为Square出品的okhttp处理，控制图片的过期时间
>* 缺点
      * 功能较为简单
      * 自身无实现“本地缓存”

### <font color=#ff9866 size=4 face="黑体">Glide</font>
>* 简介
      * 作者：Sam sjudd (Google)
      * 面世时间：2013
      * star数（截止到发稿）：12067
      * [https://github.com/bumptech/glide](https://github.com/bumptech/glide)
>*   优点
       * 多种图片格式的缓存，适用于更多的内容表现形式（如Gif、WebP、缩略图、Video）
       * 生命周期集成（根据Activity或者Fragment的生命周期管理图片加载请求）
       * 高效处理Bitmap（bitmap的复用和主动回收，减少系统回收压力）
       * 高效的缓存策略，灵活（Picasso只会缓存原始尺寸的图片，Glide缓存的是多种规格），加载速度快且内存开销小（默认Bitmap格式的不同，使得内存开销是Picasso的一半）
>* 缺点
      * 方法较多较复杂，因为相当于在Picasso上的改进，包较大（500k），影响不是很大

### <font color=#ff9866 size=4 face="黑体">Fresco</font>
>* 简介
      * 作者：Facebook
      * 面世时间：2015
      * star数（截止到发稿）：11235
      * [https://github.com/facebook/fresco](https://github.com/facebook/fresco)
>*   优点
       * 最大的优势在于5.0以下(最低2.3)的bitmap加载。在5.0以下系统，Fresco将图片放到一个特别的内存区域(Ashmem区)
       * 大大减少OOM（在更底层的Native层对OOM进行处理，图片将不再占用App的内存）
       * 适用于需要高性能加载大量图片的场景
>* 缺点
      * 包较大（2~3M）
      * 用法复杂
      * 底层涉及c++领域，阅读源码深入学习难度大

## <font color=#C4573C size=5 face="黑体">按需选择图片加载库</font>
>* 图片加载需要支持Gif，之前项目中使用的Android-Universal-Image-Loader不支持Gif且Android-Universal-Image-Loader已经停止维护，遂决定替换图片加载库
>* 分析完优缺点最终选择Glide的其它理由：
  * Glide是在Picasso的基础上进行改进的（支持Gif，内存开销小），虽然500k左右的包大小相对于Picasso较大，但是这个数量级的影响可以接受
  * 初衷是想一直维持图片的原始ImageView，而 Fresco需要在布局文件中将图片控件声明为库中自定义的SimpleDraweeView，如果切库还需要更改组件，代价会很高
  * Google推荐（亲儿子），在Google很多开源项目中广泛使用

>*  但不可避免的是，Glide在使用的过程中依然存在着许多坑需要我们去填！

## <font color=#C4573C size=5 face="黑体">如何更好地封装图片加载库</font>
### <font color=#ff9866 size=4 face="黑体">为什么要封装？</font>
先从现在面对的情形来看，项目中使用图片加载的地方都是使用的类似下面的语句
```
ImageLoader.getInstance().displayImage(imageUrl, imageView，options);
```
然而现在ImageLoader已经停止维护且已经无法满足项目需求，我们需要替换，这时你会发现如果换库的话，所有涉及到的地方都要修改（Android-Universal-Image-Loader已经和图片加载的业务逻辑严重地耦合在一起了），工作量可见一斑，这就是不封装在切库时面临的窘境！
那怎么解决那？
计算机史上有个万能的解决方案就是，如果原有层面解决不了问题，那么就请再加一层！

```
/**
 * Created by soulrelay on 2016/10/11 13:42.
 * Class Note:
 * use this class to load image,single instance
 */
public class ImageLoaderUtil {

    //图片默认加载类型 以后有可能有多种类型
    public static final int PIC_DEFAULT_TYPE = 0;

    //图片默认加载策略 以后有可能有多种图片加载策略
    public static final int LOAD_STRATEGY_DEFAULT = 0;

    private static ImageLoaderUtil mInstance;
    
    private BaseImageLoaderStrategy mStrategy;

    public ImageLoaderUtil() {
        mStrategy = new GlideImageLoaderStrategy();
    }

    //单例模式，节省资源
    public static ImageLoaderUtil getInstance() {
        if (mInstance == null) {
            synchronized (ImageLoaderUtil.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderUtil();
                    return mInstance;
                }
            }
        }
        return mInstance;
    }

    /**
     * 统一使用App context
     * 可能带来的问题：http://stackoverflow.com/questions/31964737/glide-image-loading-with-application-context
     *
     * @param url
     * @param placeholder
     * @param imageView
     */
    public void loadImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadImage(imageView.getContext(), url, placeholder, imageView);
    }

    public void loadGifImage(String url, int placeholder, ImageView imageView) {
        mStrategy.loadGifImage(url, placeholder, imageView);
    }

    public void loadImage(String url, ImageView imageView) {
        mStrategy.loadImage(url, imageView);
    }

  /**
     * 展示图片加载进度
     */
    public void loadImageWithProgress(String url, ImageView imageView, ProgressLoadListener listener) {
        mStrategy.loadImageWithProgress(url,imageView,listener);
    }

    public void loadGifWithProgress(String url, ImageView imageView, ProgressLoadListener listener) {
        mStrategy.loadGifWithProgress(url,imageView,listener);
    }

    /**
     * 策略模式的注入操作
     *
     * @param strategy
     */
    public void setLoadImgStrategy(BaseImageLoaderStrategy strategy) {
        mStrategy = strategy;
    }

    /**
     * 清除图片磁盘缓存
     */
    public void clearImageDiskCache(final Context context) {
        mStrategy.clearImageDiskCache(context);
    }

    /**
     * 清除图片内存缓存
     */
    public void clearImageMemoryCache(Context context) {
        mStrategy.clearImageMemoryCache(context);
    }

    /**
     * 根据不同的内存状态，来响应不同的内存释放策略
     *
     * @param context
     * @param level
     */
    public void trimMemory(Context context, int level) {
        mStrategy.trimMemory(context, level);
    }

    /**
     * 清除图片所有缓存
     */
    public void clearImageAllCache(Context context) {
        clearImageDiskCache(context.getApplicationContext());
        clearImageMemoryCache(context.getApplicationContext());
    }

    /**
     * 获取缓存大小
     *
     * @return CacheSize
     */
    public String getCacheSize(Context context) {
        return mStrategy.getCacheSize(context);
    }


}
```
所有需要图片显示的地方使用如下方法进行调用：

>* 入口唯一，所有图片加载都在ImageLoaderUtil这一个地方统一管理，使用了[单例模式](http://blog.csdn.net/s003603u/article/details/51982140)(据说单元素的枚举类型已经成为实现Singleton的最佳方法，你可以试试 )，
>* 高效地封装减少了切库(只需要切换图片加载策略)带来的代价，默认采用GlideImageLoaderStrategy

总结：外部表现一致，内部灵活处理原则。

```
/**
 * 图片加载库的封装演示案例
 * Created by soulrelay on 2016/12/11 19:18
 */
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.iv_normal)
    ImageView ivNormal;
    @BindView(R.id.iv_gif)
    ImageView ivGif;
    @BindView(R.id.iv_gif1)
    ImageView ivGif1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        ImageLoaderUtil.getInstance().loadImage("http://image.sports.baofeng.com/25a3dbb0c99c5e48e52e60941ed230be", R.drawable.bg_default_video_common_small, ivNormal);
        ImageLoaderUtil.getInstance().loadImage("http://image.sports.baofeng.com/19ce5d6ac3b4fff255196f200b1d3079", R.drawable.bg_default_video_common_small, ivGif);
        ImageLoaderUtil.getInstance().loadGifImage("http://image.sports.baofeng.com/19ce5d6ac3b4fff255196f200b1d3079", R.drawable.bg_default_video_common_small, ivGif1);

    }

}
```

效果图如下所示：
![这里写图片描述](http://img.blog.csdn.net/20161202172529289)
### <font color=#ff9866 size=4 face="黑体">使用策略模式封装图片加载策略</font>
如果你对策略模式不是很熟，请先参考[策略模式和状态模式](http://blog.csdn.net/s003603u/article/details/52033391)
首先我们需要抽象出一个图片加载的基础接口BaseImageLoaderStrategy 
基本功能主要包括
>* 正常加载图片
>* 针对于GIF图片的特殊加载
>* 加载图片的进度回调
>* 清除缓存
>* 获取缓存大小等
>* 其它特殊需求自己封装，最好不要破坏策略模式的整体结构

```
/**
 * Created by soulrelay on 2016/10/11.
 * Class Note:
 * abstract class/interface defined to load image
 * (Strategy Pattern used here)
 */
public interface BaseImageLoaderStrategy {
    //无占位图
    void loadImage(String url, ImageView imageView);

    void loadImage(String url, int placeholder, ImageView imageView);

    void loadImage(Context context, String url, int placeholder, ImageView imageView);

    void loadGifImage(String url, int placeholder, ImageView imageView);

    void loadImageWithProgress(String url, ImageView imageView, ProgressLoadListener listener);

    void loadGifWithProgress(String url, ImageView imageView, ProgressLoadListener listener);

    //清除硬盘缓存
    void clearImageDiskCache(final Context context);
    //清除内存缓存
    void clearImageMemoryCache(Context context);
    //根据不同的内存状态，来响应不同的内存释放策略
    void trimMemory(Context context, int level);
    //获取缓存大小
    String getCacheSize(Context context);

}
```
需要说明的一点是：
>* 当封装的方法参数比较少时可以按照上述方式进行抽象，如果需要传递的参数较多，可以考虑使用建造者模式[建造者模式](http://blog.csdn.net/s003603u/article/details/51967809)
>* 例如封装一个ImageLoaderConfiguration，包含如下参数等等，将一个复杂对象的构建与它的表示分离，使得同样的构建过程可以创建不同的表示
  * type 图片加载的类型（大图、小图、中图）
  * url 需要解析的url
  * placeHolder 当没有成功加载的时候显示的图片
  * imgView ImageView的实例
  * loadStrategy 加载策略
>* 当然这里我没有使用建造模式，考虑到目前使用的对象还不算复杂（传参比较简单），而且如果使用建造者模式有可能每次都要new一个新的对象实例，虽然开销可以接受
>* 使用ImageLoaderUtil的过程中，注意内存泄露的问题（静态单例的生命周期与App一样，当一个单例的对象长久不用时，不会被垃圾收集机制回收）

然后基于每个图片库的各自方式来进行相应策略的封装，需要使用哪种策略，只需要通过ImageLoaderUtil的setLoadImgStrategy(BaseImageLoaderStrategy strategy)方法将相应的策略注入，相关类图关系如下所示：

![这里写图片描述](http://img.blog.csdn.net/20161204205952522)

不同的图片加载库实现不同的图片加载策略
这里只是给出Glide的图片加载策略类GlideImageLoaderStrategy作为参考
>* Glide依赖v4包，且需要配置android.permission.INTERNET和android.permission.WRITE_EXTERNAL_STORAGE（忘记配置权限，图片加载不出来，还看不出什么异常）
>* 其中部分方法使用到了RequestListener的回调（这里是因为项目中的一些特殊需求而添加，如统计图片首次加载时长来测试一下图片cdn服务器的速度等）
>* 在使用Glide的过程中遇到了一些问题，部分已经在注释中说明
>* 之所以针对gif单独封装，是因为在使用的过程中发现，当在列表中加载大量gif会有OOM的问题，所以通过asGif进行特殊标明，即使这样也会出现类似问题，同时暂时通过skipMemoryCache(true)跳过内存缓存，之后有更好的办法会继续补充，各位看官如有良策，希望可以不吝赐教
>* Glide本身不提供图片的progress回调，所以关于进度回调的解决方案参照的是
[ProgressGlide](https://github.com/shangmingchao/ProgressGlide)，并做了些许改动集成到项目中
>* 期间发现了一个很好的问题[Android的App中线程池的使用，具体使用多少个线程池？](https://www.zhihu.com/question/37804956)，其中一个答主的关于图片加载库线程池策略的分析很好，值得体会，简单摘录如下：
  * UIL的线程池处理非常简单粗暴，没有根据CPU数量来选择，也没有根据网络状况的变化进行调整;
  *  Picasso的线程池会根据网络状况的变化进行调整，在Wifi下线程数为4,而4G下线程数为3, 3G下为2， 2G下为1，默认状况为3；
 *  Glide加载缓存未命中的线程池会根据根据CPU的数量和Java虚拟机中可用的处理器数量来选择合适的线程数，但是最多不超过4;而加载缓存命中的图片的线程池默认大小为1.

```
/**
 * Created by soulrelay on 2016/10/11 13:48.
 * Class Note:
 * using {@link Glide} to load image
 */
public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy {

    @Override
    public void loadImage(String url, int placeholder, ImageView imageView) {
        loadNormal(imageView.getContext(), url, placeholder, imageView);
    }

    @Override
    public void loadImage(Context context, String url, int placeholder, ImageView imageView) {
        loadNormal(context, url, placeholder, imageView);
    }

    /**
     * 无holder的gif加载
     *
     * @param url
     * @param imageView
     */
    @Override
    public void loadImage(String url, ImageView imageView) {
        Glide.with(imageView.getContext()).load(url).dontAnimate()
                .placeholder(imageView.getDrawable())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }

    @Override
    public void loadGifImage(String url, int placeholder, ImageView imageView) {
        loadGif(imageView.getContext(), url, placeholder, imageView);
    }

    @Override
    public void loadImageWithProgress(String url, final ImageView imageView, final ProgressLoadListener listener) {
        Glide.with(imageView.getContext()).using(new ProgressModelLoader(new ProgressUIListener() {
            @Override
            public void update(final int bytesRead, final int contentLength) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.update(bytesRead, contentLength);
                    }
                });
            }
        })).load(url).asBitmap().dontAnimate().
                listener(new RequestListener<Object, Bitmap>() {
                    @Override
                    public boolean onException(Exception e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        listener.onException();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        listener.onResourceReady();
                        return false;
                    }
                }).into(imageView);
    }

    @Override
    public void loadGifWithProgress(String url, final ImageView imageView, final ProgressLoadListener listener) {
        Glide.with(imageView.getContext()).using(new ProgressModelLoader(new ProgressUIListener() {
            @Override
            public void update(final int bytesRead, final int contentLength) {
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        listener.update(bytesRead, contentLength);
                    }
                });
            }
        })).load(url).asGif().skipMemoryCache(true).dontAnimate().
                listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                        listener.onException();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        listener.onResourceReady();
                        return false;
                    }
                }).into(imageView);
    }

    @Override
    public void clearImageDiskCache(final Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.get(context.getApplicationContext()).clearDiskCache();
                    }
                }).start();
            } else {
                Glide.get(context.getApplicationContext()).clearDiskCache();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearImageMemoryCache(Context context) {
        try {
            if (Looper.myLooper() == Looper.getMainLooper()) { //只能在主线程执行
                Glide.get(context.getApplicationContext()).clearMemory();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void trimMemory(Context context, int level) {
        Glide.get(context).trimMemory(level);
    }

    @Override
    public String getCacheSize(Context context) {
        try {
            return CommonUtils.getFormatSize(CommonUtils.getFolderSize(Glide.getPhotoCacheDir(context.getApplicationContext())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * load image with Glide
     */
    private void loadNormal(final Context ctx, final String url, int placeholder, ImageView imageView) {
        /**
         *  为其添加缓存策略,其中缓存策略可以为:Source及None,None及为不缓存,Source缓存原型.如果为ALL和Result就不行.然后几个issue的连接:
         https://github.com/bumptech/glide/issues/513
         https://github.com/bumptech/glide/issues/281
         https://github.com/bumptech/glide/issues/600
         modified by xuqiang
         */

        //去掉动画 解决与CircleImageView冲突的问题 这个只是其中的一个解决方案
        //使用SOURCE 图片load结束再显示而不是先显示缩略图再显示最终的图片（导致图片大小不一致变化）
        final long startTime = System.currentTimeMillis();
        Glide.with(ctx).load(url).dontAnimate()
                .placeholder(placeholder)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        })
                .into(imageView);
    }

    /**
     * load image with Glide
     */
    private void loadGif(final Context ctx, String url, int placeholder, ImageView imageView) {
        final long startTime = System.currentTimeMillis();
        Glide.with(ctx).load(url).asGif().dontAnimate()
                .placeholder(placeholder).skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).listener(new RequestListener<String, GifDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        })
                .into(imageView);
    }

}

```
## <font color=#C4573C size=5 face="黑体">源码地址</font>
[ImageLoaderUtil](https://github.com/soulrelay/ImageLoaderUtil)

## <font color=#C4573C size=5 face="黑体">部分参考链接</font>
[http://www.jianshu.com/p/97994c9693f9](http://www.jianshu.com/p/97994c9693f9)
[https://www.zhihu.com/question/37804956](https://www.zhihu.com/question/37804956)
[http://www.jianshu.com/p/e26130a93289](http://www.jianshu.com/p/e26130a93289)
[http://www.cnblogs.com/android-blogs/p/5737611.html](http://www.cnblogs.com/android-blogs/p/5737611.html)
## <font color=#C4573C size=5 face="黑体">更新</font>

### <font color=#ff9866 size=4 face="黑体">2016-12-09 ll You must not call setTag() on a view Glide is targeting</font>



项目中在使用Glide图片加载框架时遇到该错误
报错原因大致是因为Glide加载的iamgeView调用了setTag()方法导致的错误，因为Glide已经默认为ImageView设置的Tag

相关解决方案已经在Glide 3.6.0[（issue #370）](https://github.com/bumptech/glide/issues/370)被引进，实测可行
在AndroidManifest.xml中加入

```
<application
        android:name=".App">
```
然后在App中添加如下代码：

```
public class App extends Application {
    @Override public void onCreate() {
        super.onCreate();
        ViewTarget.setTagId(R.id.glide_tag);
    }
}
```
在src/main/values/ids.xml添加如下代码：

```
<resources>
    <item type="id" name="glide_tag" />
</resources>
```


### <font color=#ff9866 size=4 face="黑体">2016-12-13 ll  添加loadGifWithPrepareCall方法</font>
2016.12.13

只想知道图片是否准备完毕（包括来自网络或者sdcard），区别于loadImageWithProgress和loadGifWithProgress的进度回调

Tips：使用Glide加载图片注意ImageView的Scaletype的设置
```
public interface BaseImageLoaderStrategy {
    void loadGifWithPrepareCall(String url, ImageView imageView, SourceReadyListener listener);
}
```

```
public class GlideImageLoaderStrategy implements BaseImageLoaderStrategy {

       @Override
    public void loadGifWithPrepareCall(String url, ImageView imageView, final SourceReadyListener listener) {
        Glide.with(imageView.getContext()).load(url).asGif().dontAnimate()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).
                listener(new RequestListener<String, GifDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        listener.onResourceReady(resource.getIntrinsicWidth(),resource.getIntrinsicHeight());
                        return false;
                    }
                }).into(imageView);
    }
}

```

```
public class ImageLoaderUtil {
 public void loadGifWithPrepareCall(String url, ImageView imageView, SourceReadyListener listener) {
        mStrategy.loadGifWithPrepareCall(url,imageView,listener);
    }
}
```
### <font color=#ff9866 size=4 face="黑体">2016-12-26 ll  更新loadGifWithProgress方法  </font>
#### <font color=#ff9866 size=4 face="黑体">2017-1-10 ll  统一加载图片进度回调方法为loadImageWithProgress，弃用并删除loadGifWithProgress方法 </font>
具体细节查看GitHub最新代码
### <font color=#ff9866 size=3 face="黑体">2016-12-26 ll  自定义GlideModule 并将 Glide与okhttp3集成 </font>
 1.自定义一个GlideModule 
```
/**
 * DES：自定义一个GlideModule
 * <p>
 * GlideModule 是一个抽象方法，全局改变 Glide 行为的一个方式，
 * 通过全局GlideModule 配置Glide，用GlideBuilder设置选项，用Glide注册ModelLoader等。
 * <p>
 */
public class MyGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
        int maxMemory = (int) Runtime.getRuntime().maxMemory();//获取系统分配给应用的总内存大小
        int memoryCacheSize = maxMemory / 8;//设置图片内存缓存占用八分之一
        //设置内存缓存大小
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        builder.setBitmapPool(new LruBitmapPool(memoryCacheSize));
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        // register ModelLoaders here.
    }
}
```
  2.AndroidManifest.xml注册
```
<manifest ...>
    <!-- ... permissions -->
    <application ...>
             <!-- 自定义GlideModule -->
        <meta-data
            android:name="com.baofeng.soulrelay.utils.imageloader.MyGlideModule"
            android:value="GlideModule" />
        <!-- 自定义GlideModule -->
        <!-- ... activities and other components -->
    </application>
</manifest>
```
3、 Glide与OkHttp3集成

```
   compile 'com.squareup.okhttp3:okhttp:3.4.2'
   compile 'com.github.bumptech.glide:okhttp3-integration:1.4.0@aar'
```
4、添加混淆处理

```
#--------------------Glide-----------------------#
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

-keepnames class com.baofeng.soulrelay.utils.imageloader.MyGlideModule
# or more generally:
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class com.bumptech.glide.integration.okhttp3.OkHttpGlideModule

#--------------------Glide-----------------------#
```

### <font color=#ff9866 size=4 face="黑体">2017-1-6 ll  GIF帧显示不完全 2017-1-10补充说明PS</font>
相关问题在[issues1649](https://github.com/bumptech/glide/issues/1649)中被提到和解决（目前glide:3.7.0的确存在这个问题）
具体解决方法是：
 glide:3.8.0-SNAPSHOT修复了关于GIF展示的一些bug，实测可用
 Gradle配置修改如下：
 >* Add the snapshot repo to your list of repositories:
```
repositories {
  jcenter()
  maven {
    name 'glide-snapshot'
    url 'http://oss.sonatype.org/content/repositories/snapshots'
  }
}
```


>* And then change your dependencies to the v3 snapshot version:

```
dependencies {
  compile 'com.github.bumptech.glide:glide:3.8.0-SNAPSHOT'
  compile 'com.github.bumptech.glide:okhttp-integration:1.5.0-SNAPSHOT'
}
```
#### <font color=#ff9866 size=3 face="黑体">2017-1-10补充说明PS</font>
PS：提供一个gif图 帧提取工具[GIFFrame.exe](http://download.csdn.net/detail/s003603u/9733492)
据我分析，那些没有显示完整的GIF图片，里面的部分帧图片本身就不是完整的，但是之前的Glide并没有做很好的处理，所以显示效果有缺陷，当然最新的3.8.0-SNAPSHOT解决了这个问题，但是在显示的时候仍有瑕疵（有一些重叠，当然我觉得这也跟gif图的做工有关）
### <font color=#ff9866 size=4 face="黑体">2017-1-6 ll  You cannot start a load for a destroyed activity</font>
完整异常信息：
```
FATAL EXCEPTION: main
Process: com.sports.baofeng, PID: 9170
java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity
at com.bumptech.glide.d.k.b(SourceFile:134)
at com.bumptech.glide.d.k.a(SourceFile:102)
at com.bumptech.glide.d.k.a(SourceFile:87)
at com.bumptech.glide.i.c(SourceFile:629)
at com.storm.durian.common.utils.imageloader.b.a(SourceFile:1194)
at com.storm.durian.common.utils.imageloader.c.a(SourceFile:52)
at com.sports.baofeng.specialtopic.SpecialTopicDetailFixActivity.a(SourceFile:311)
at com.sports.baofeng.specialtopic.SpecialTopicDetailFixActivity.a(SourceFile:1347)
at com.sports.baofeng.specialtopic.d.a(SourceFile:1052)
at com.sports.baofeng.specialtopic.c$1.a(SourceFile:1064)
at com.storm.durian.common.b.b$1.onPostExecute(SourceFile:57)
at android.os.AsyncTask.finish(AsyncTask.java:651)
at android.os.AsyncTask.access$500(AsyncTask.java:180)
at android.os.AsyncTask$InternalHandler.handleMessage(AsyncTask.java:668)
at android.os.Handler.dispatchMessage(Handler.java:102)
at android.os.Looper.loop(Looper.java:158)
at android.app.ActivityThread.main(ActivityThread.java:7225)
at java.lang.reflect.Method.invoke(Native Method)
at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:1230)
at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:1120)

```
以上异常出现的几率为random
初步分析原因是：进入页面然后又迅速退出，导致AsyncTask中的onPostExecute在调用Glide加载图片时出现如上异常，当然也跟对AsyncTask的管理有关
同样的问题参考[issues138](https://github.com/bumptech/glide/issues/138)
简单的摘要：

> you fire an async task and then finish() in which case you just need to pass getApplicationContext instead of this when creating the callback/asynctask

按照上面这个理解的话，如果是在AsyncTask中的onPostExecute执行时
调用Glide加载图片，context最好使用ApplicationContext

对[ImageLoaderUtil](https://github.com/soulrelay/ImageLoaderUtil)做如下更新，添加方法
>* BaseImageLoaderStrategy
```
 //这里的context指定为ApplicationContext
    void loadImageWithAppCxt(String url, ImageView imageView);
```

>* GlideImageLoaderStrategy
```
  @Override
    public void loadImageWithAppCxt(String url, ImageView imageView) {
        Glide.with(imageView.getContext().getApplicationContext()).load(url).dontAnimate()
                .placeholder(imageView.getDrawable())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imageView);
    }
```
>* ImageLoaderUtil
```
 public void loadImageWithAppCxt(String url, ImageView imageView) {
        mStrategy.loadImageWithAppCxt(url,imageView);
    }
```

### <font color=#ff9866 size=4 face="黑体">2017-1-10 ll  简单说说图片适配的问题</font>
过多的概念不赘述，可以先参考[Android屏幕适配全攻略(最权威的官方适配指导)](http://blog.csdn.net/zhaokaiqiang1992/article/details/45419023)
这里主要描述一种现象，明白的话自然觉得很简单！

```
    <ImageView
                android:id="@+id/iv_gif"
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:scaleType="centerInside"
                 />
```
假设现在要加载一张200px * 200px的GIF图片（图片基于1280 * 720），这张图片的宽高设置为wrap_content，如果在1920 * 1080分辨率的手机上显示，相对于1280 * 720（假设屏幕尺寸相同），在视觉效果上会显得小，这其实是Android系统基于手机像素密度的一种自适配，单一变化条件下，1920 * 1080分辨率的手机的像素密度是1280 * 720的1.5倍
假设如果系统的自适配让你觉得在高分辨率手机上显得图片过小（像素密度高，200个像素显示起来就比较挤），可以通过自己的计算来改变这种现象
ImageLoaderUtil提供如下加载成功回调的方法（并且会把图片的宽高告诉你）：这里有个参数设置，看需求来计算，粗略点可以只使用宽度比例来算，如下面的例子显示，参数为AppParams.screenWidth / 720，当然也可以获取屏幕密度，1920 * 1080的屏幕密度为3，1280 * 720的为2，所以参数可以设置为AppParams.density/2（在两种分辨率上看着视觉上一样）

```
    ImageLoaderUtil.getInstance().loadGifWithPrepareCall(url, mImageView, new SourceReadyListener() {
                @Override
                public void onResourceReady(int width, int height) {
                    ViewGroup.LayoutParams params = mImageView.getLayoutParams();
                      params.height = height * AppParams.screenWidth / 720;
                    params.width = width * AppParams.screenWidth / 720;
                    mImageView.setLayoutParams(params);
                    progressBar.setVisibility(View.GONE);
                }
            });
```

### <font color=#ff9866 size=4 face="黑体">2017-1-10 ll  添加saveImage方法，实现图片的本地自定义保存功能</font>
已同步到[GitHub ImageLoaderUtil](https://github.com/soulrelay/ImageLoaderUtil)
>* ImageLoaderUtil相关接口：

```
  /**
     * @param context
     * @param url 图片url
     * @param savePath 保存路径
     * @param saveFileName 保存文件名
     * @param listener 文件保存成功与否的监听器
     */
 public void saveImage(Context context, String url, String savePath, String saveFileName, ImageSaveListener listener) {
        mStrategy.saveImage(context, url, savePath, saveFileName, listener);
    }
```

>*  在工作线程中调用示例如下：

```
ImageLoaderUtil.getInstance().saveImage(getActivity(), url,
                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/bfsports",
                        "bfsports" + System.currentTimeMillis(), new ImageSaveListener() {
                            @Override
                            public void onSaveSuccess() {
                                handler.obtainMessage(MSG_PIC_SAVE_SUCC).sendToTarget();
                            }

                            @Override
                            public void onSaveFail() {
                                handler.obtainMessage(MSG_PIC_SAVE_FAIL).sendToTarget();
                            }
                        });
```

