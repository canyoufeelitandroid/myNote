package com.com.swipmenulib.test;

/**
 * Created by 64088 on 2017/3/6.
 */

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.example.notebooktest.R;

/**
 * ��Item�໬ɾ���˵���
 * �̳���ViewGroup��ʵ�ֻ�������ɾ����ѡ���Ч����
 * ˼·���������ƽ�item���󻬶���
 * ��onMeasureʱ ����һ��Item��Ϊ��Ļ���
 * �������Ļ�϶���໬ɾ���˵���������һ���ྲ̬View���ͱ��� ViewCache���洢���ǵ�ǰ�������һ�״̬��CstSwipeMenuItemViewGroup��
 * ÿ��Touchʱ�Աȣ��������Touch�Ĳ���һ��View����ô��ViewCache�ָ���ͨ״̬�����������µ�CacheView
 * ֻҪ��һ���໬�˵����ڴ�״̬�� �Ͳ�����㲼�����»�����
 * <p/>
 * ƽ������ʹ�õ���Scroller,20160811������ƽ�������������Զ������ˣ���Ϊ����������(���ü�������ͬ)
 * <p/>
 * 20160824,fix ����ָһ���ҵ��������ֻ�ӵ�һ������(ʹ��һ���ྲ̬��������)
 * other:
 * 1 �˵����ڲ໬ʱ�����س����¼�
 * 2 ����໬ʱ ��� �ĳ�ͻ
 * 3 ͨ�� isIos ���������Ƿ���IOS����ʽ������Ĭ���Ǵ򿪵ġ�
 * 4 ͨ�� isSwipeEnable ���������Ƿ����һ��˵���Ĭ�ϴ򿪡���ĳЩ����������item��û�б༭Ȩ�޵��û������һ���
 * 5 2016 09 29 add,��ͨ������ isLeftSwipe֧�����һ�
 * 6 2016 10 21 add , ����viewChache �� get()�������������ڣ�������ⲿ�հ״�ʱ���ر�����չ���Ĳ໬�˵���
 * 7 2016 10 22 fix , �����ؼ���Ȳ���ȫ��ʱ��bug��
 * 2016 10 22 add , ��QQ���໬�˵�չ��ʱ��������໬�˵�֮������򣬹رղ໬�˵���
 * 8 2016 11 03 add,�ж���ָ��ʼ��㣬����������ڻ����ˣ�������һ�е���¼���
 * 9 2016 11 04 fix �����¼��Ͳ໬�ĳ�ͻ��
 * 10 2016 11 09 add,����GridLayoutManager�����Ե�һ����Item(��ContentItem)�Ŀ��Ϊ�ؼ���ȡ�
 * 11 2016 11 14 add,֧��padding,�Һ����ƻ������ϻ��»�����˲���֧��ContentItem��margin���ԡ�
 * 2016 11 14 add,�޸Ļص��Ķ�������ƽ����
 * 2016 11 14 fix,΢Сλ�Ƶ�move���ػص���bug
 * 2016 11 18,fix ��ItemView���ڸ߶ȿɱ�����
 * 2016 12 07,fix ��ֹ�໬ʱ(isSwipeEnable false)������¼����ܸ��š�
 * 2016 12 09,fix ListView���ٻ�������ɾ��ʱ��ż�ֲ˵�����ʧ��bug��
 * Created by zhangxutong .
 * Date: 16/04/24
 */
public class SwipeMenuLayout extends ViewGroup {
    private static final String TAG = "zxt/SwipeMenuLayout";

    private int mScaleTouchSlop;//Ϊ�˴������¼��ĳ�ͻ
    private int mMaxVelocity;//���㻬���ٶ���
    private int mPointerId;//��㴥��ֻ���һ����ָ���ٶ�
    private int mHeight;//�Լ��ĸ߶�
    //�Ҳ�˵�����ܺ�(��󻬶�����)
    private int mRightMenuWidths;

    //�����ж��ٽ�ֵ���Ҳ�˵���ȵ�40%�� ��ָ̧��ʱ��������չ����û��������menu
    private int mLimit;

    private View mContentView;//2016 11 13 add ���洢contentView(��һ��View)

    //private Scroller mScroller;//��ǰitem�Ļ������������������������Զ�����
    //��һ�ε�xy
    private PointF mLastP = new PointF();
    //2016 10 22 add , ��QQ���໬�˵�չ��ʱ��������໬�˵�֮������򣬹رղ໬�˵���
    //����һ������ֵ������dispatch�����ÿ��downʱ��Ϊtrue��moveʱ�жϣ�����ǻ�����������Ϊfalse��
    //��Intercept������upʱ���ж���������������Ϊtrue ˵���ǵ���¼�����رղ˵���
    private boolean isUnMoved = true;

    //2016 11 03 add,�ж���ָ��ʼ��㣬����������ڻ����ˣ�������һ�е���¼���
    //up-down�����꣬�ж��Ƿ��ǻ���������ǣ�������һ�е���¼�
    private PointF mFirstP = new PointF();
    private boolean isUserSwiped;

    //�洢���ǵ�ǰ����չ����View
    private static SwipeMenuLayout mViewCache;

    //��ֹ��ֻ��ָһ���ҵ�flag ��ÿ��down���жϣ� touch�¼��������
    private static boolean isTouching;

    private VelocityTracker mVelocityTracker;//�����ٶȱ���
    private android.util.Log LogUtils;

    /**
     * �һ�ɾ�����ܵĿ���,Ĭ�Ͽ�
     */
    private boolean isSwipeEnable;

    /**
     * IOS��QQʽ������Ĭ�Ͽ�
     */
    private boolean isIos;

    private boolean iosInterceptFlag;//IOS�����£��Ƿ������¼���flag

    /**
     * 20160929add ���һ��Ŀ���,Ĭ���󻬴򿪲˵�
     */
    private boolean isLeftSwipe;

    public SwipeMenuLayout(Context context) {
        this(context, null);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeMenuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    public boolean isSwipeEnable() {
        return isSwipeEnable;
    }

    /**
     * ���ò໬���ܿ���
     *
     * @param swipeEnable
     */
    public void setSwipeEnable(boolean swipeEnable) {
        isSwipeEnable = swipeEnable;
    }


    public boolean isIos() {
        return isIos;
    }

    /**
     * �����Ƿ���IOS����ʽ����
     *
     * @param ios
     */
    public SwipeMenuLayout setIos(boolean ios) {
        isIos = ios;
        return this;
    }

    public boolean isLeftSwipe() {
        return isLeftSwipe;
    }

    /**
     * �����Ƿ����󻬳��˵�������false Ϊ�һ����˵�
     *
     * @param leftSwipe
     * @return
     */
    public SwipeMenuLayout setLeftSwipe(boolean leftSwipe) {
        isLeftSwipe = leftSwipe;
        return this;
    }

    /**
     * ����ViewCache
     *
     * @return
     */
    public static SwipeMenuLayout getViewCache() {
        return mViewCache;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mScaleTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMaxVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        //��ʼ���������������
        //mScroller = new Scroller(context);

        //�һ�ɾ�����ܵĿ���,Ĭ�Ͽ�
        isSwipeEnable = true;
        //IOS��QQʽ������Ĭ�Ͽ�
        isIos = true;
        //���һ��Ŀ���,Ĭ���󻬴򿪲˵�
        isLeftSwipe = true;
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout, defStyleAttr, 0);
        int count = ta.getIndexCount();
        for (int i = 0; i < count; i++) {
            int attr = ta.getIndex(i);
            //������ó�AndroidLib ��Դ�����ǳ������޷�ʹ��switch case
            if (attr == R.styleable.SwipeMenuLayout_swipeEnable) {
                isSwipeEnable = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.SwipeMenuLayout_ios) {
                isIos = ta.getBoolean(attr, true);
            } else if (attr == R.styleable.SwipeMenuLayout_leftSwipe) {
                isLeftSwipe = ta.getBoolean(attr, true);
            }
        }
        ta.recycle();


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //Log.d(TAG, "onMeasure() called with: " + "widthMeasureSpec = [" + widthMeasureSpec + "], heightMeasureSpec = [" + heightMeasureSpec + "]");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setClickable(true);//���Լ��ɵ�����Ӷ���ȡ�����¼�

        mRightMenuWidths = 0;//����ViewHolder�ĸ��û��ƣ�ÿ������Ҫ�ֶ��ָ���ʼֵ
        mHeight = 0;
        int contentWidth = 0;//2016 11 09 add,����GridLayoutManager�����Ե�һ����Item(��ContentItem)�Ŀ��Ϊ�ؼ����
        int childCount = getChildCount();

        //add by 2016 08 11 Ϊ����View�ĸߣ�����matchParent(�ο���FrameLayout ��LinearLayout��Horizontal)
        final boolean measureMatchParentChildren = MeasureSpec.getMode(heightMeasureSpec) != MeasureSpec.EXACTLY;
        boolean isNeedMeasureChildHeight = false;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            //��ÿһ����View�ɵ�����Ӷ���ȡ�����¼�
            childView.setClickable(true);
            if (childView.getVisibility() != GONE) {
                //�����ƻ������ϻ����»����򽫲���֧��Item��margin
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                //measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
                final MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
                mHeight = Math.max(mHeight, childView.getMeasuredHeight()/* + lp.topMargin + lp.bottomMargin*/);
                if (measureMatchParentChildren && lp.height == LayoutParams.MATCH_PARENT) {
                    isNeedMeasureChildHeight = true;
                }
                if (i > 0) {//��һ��������Left item���ӵڶ�����ʼ����RightMenu
                    mRightMenuWidths += childView.getMeasuredWidth();
                } else {
                    mContentView = childView;
                    contentWidth = childView.getMeasuredWidth();
                }
            }
        }
        setMeasuredDimension(getPaddingLeft() + getPaddingRight() + contentWidth,
                mHeight + getPaddingTop() + getPaddingBottom());//���ȡ��һ��Item(Content)�Ŀ��
        mLimit = mRightMenuWidths * 4 / 10;//�����жϵ��ٽ�ֵ
        //Log.d(TAG, "onMeasure() called with: " + "mRightMenuWidths = [" + mRightMenuWidths);
        if (isNeedMeasureChildHeight) {//�����View��height��MatchParent���Եģ�������View�߶�
            forceUniformHeight(childCount, widthMeasureSpec);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    /**
     * ��MatchParent����View���ø߶�
     *
     * @param count
     * @param widthMeasureSpec
     * @see android.widget.LinearLayout# ͬ������
     */
    private void forceUniformHeight(int count, int widthMeasureSpec) {
        // Pretend that the linear layout has an exact size. This is the measured height of
        // ourselves. The measured height should be the max height of the children, changed
        // to accommodate the heightMeasureSpec from the parent
        int uniformMeasureSpec = MeasureSpec.makeMeasureSpec(getMeasuredHeight(),
                MeasureSpec.EXACTLY);//�Ը����ָ߶ȹ���һ��Exactly�Ĳ�������
        for (int i = 0; i < count; ++i) {
            final View child = getChildAt(i);
            if (child.getVisibility() != GONE) {
                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
                if (lp.height == LayoutParams.MATCH_PARENT) {
                    // Temporarily force children to reuse their old measured width
                    // FIXME: this may not be right for something like wrapping text?
                    int oldWidth = lp.width;//measureChildWithMargins ����������õ�������Ҫ����һ��
                    lp.width = child.getMeasuredWidth();
                    // Remeasure with new dimensions
                    measureChildWithMargins(child, widthMeasureSpec, 0, uniformMeasureSpec, 0);
                    lp.width = oldWidth;
                }
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //LogUtils.e(TAG, "onLayout() called with: " + "changed = [" + changed + "], l = [" + l + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
        int childCount = getChildCount();
        int left = 0 + getPaddingLeft();
        int right = 0 + getPaddingLeft();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                if (i == 0) {//��һ����View������ �������Ϊȫ��
                    childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                    left = left + childView.getMeasuredWidth();
                } else {
                    if (isLeftSwipe) {
                        childView.layout(left, getPaddingTop(), left + childView.getMeasuredWidth(), getPaddingTop() + childView.getMeasuredHeight());
                        left = left + childView.getMeasuredWidth();
                    } else {
                        childView.layout(right - childView.getMeasuredWidth(), getPaddingTop(), right, getPaddingTop() + childView.getMeasuredHeight());
                        right = right - childView.getMeasuredWidth();
                    }

                }
            }
        }
        //Log.d(TAG, "onLayout() called with: " + "maxScrollGap = [" + maxScrollGap + "], l = [" + l + "], t = [" + t + "], r = [" + r + "], b = [" + b + "]");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //LogUtils.d(TAG, "dispatchTouchEvent() called with: " + "ev = [" + ev + "]");
        if (isSwipeEnable) {
            acquireVelocityTracker(ev);
            final VelocityTracker verTracker = mVelocityTracker;
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    isUserSwiped = false;//2016 11 03 add,�ж���ָ��ʼ��㣬����������ڻ����ˣ�������һ�е���¼���
                    isUnMoved = true;//2016 10 22 add , ��QQ���໬�˵�չ��ʱ������������򣬹رղ໬�˵���
                    iosInterceptFlag = false;//add by 2016 09 11 ��ÿ��DOWNʱ��Ĭ���ǲ����ص�
                    if (isTouching) {//����б��ָͷ�����ˣ���ô��return false������������move..���¼�Ҳ�������������View�ˡ�
                        return false;
                    } else {
                        isTouching = true;//��һ������ָͷ���Ͻ��ı��־��������Ȩ��
                    }
                    mLastP.set(ev.getRawX(), ev.getRawY());
                    mFirstP.set(ev.getRawX(), ev.getRawY());//2016 11 03 add,�ж���ָ��ʼ��㣬����������ڻ����ˣ�������һ�е���¼���

                    //���down��view��cacheview��һ����������������ԭ���Ұ�����Ϊnull
                    if (mViewCache != null) {
                        if (mViewCache != this) {
                            mViewCache.smoothClose();

                            iosInterceptFlag = isIos;//add by 2016 09 11 ��IOSģʽ�����Ļ����ҵ�ǰ�в໬�˵���View���Ҳ����Լ��ģ��͸������¼�����
                        }
                        //ֻҪ��һ���໬�˵����ڴ�״̬�� �Ͳ�����㲼�����»�����
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    //���һ�������id�� ��ʱ�����ж�����㣬������һ�������㻬��������
                    mPointerId = ev.getPointerId(0);
                    break;
                case MotionEvent.ACTION_MOVE:
                    //add by 2016 09 11 ��IOSģʽ�����Ļ����ҵ�ǰ�в໬�˵���View���Ҳ����Լ��ģ��͸������¼���������Ҳ���ó���
                    if (iosInterceptFlag) {
                        break;
                    }
                    float gap = mLastP.x - ev.getRawX();
                    //Ϊ����ˮƽ�����н�ֹ����ListView������ֱ����
                    if (Math.abs(gap) > 10 || Math.abs(getScrollX()) > 10) {//2016 09 29 �޸Ĵ˴���ʹ���θ����ֻ�������������
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    //2016 10 22 add , ��QQ���໬�˵�չ��ʱ������������򣬹رղ໬�˵���begin
                    if (Math.abs(gap) > mScaleTouchSlop) {
                        isUnMoved = false;
                    }
                    //2016 10 22 add , ��QQ���໬�˵�չ��ʱ������������򣬹رղ໬�˵���end
                    //���scroller��û�л������� ֹͣ��������
/*                    if (!mScroller.isFinished()) {
                        mScroller.abortAnimation();
                    }*/
                    scrollBy((int) (gap), 0);//����ʹ��scrollBy
                    //Խ������
                    if (isLeftSwipe) {//��
                        if (getScrollX() < 0) {
                            scrollTo(0, 0);
                        }
                        if (getScrollX() > mRightMenuWidths) {
                            scrollTo(mRightMenuWidths, 0);
                        }
                    } else {//�һ�
                        if (getScrollX() < -mRightMenuWidths) {
                            scrollTo(-mRightMenuWidths, 0);
                        }
                        if (getScrollX() > 0) {
                            scrollTo(0, 0);
                        }
                    }

                    mLastP.set(ev.getRawX(), ev.getRawY());
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    //2016 11 03 add,�ж���ָ��ʼ��㣬����������ڻ����ˣ�������һ�е���¼���
                    if (Math.abs(ev.getRawX() - mFirstP.x) > mScaleTouchSlop) {
                        isUserSwiped = true;
                    }

                    //add by 2016 09 11 ��IOSģʽ�����Ļ����ҵ�ǰ�в໬�˵���View���Ҳ����Լ��ģ��͸������¼���������Ҳ���ó���
                    if (!iosInterceptFlag) {//�һ����� ���ж��Ƿ�Ҫ����չ��menu
                        //��α˲ʱ�ٶ�
                        verTracker.computeCurrentVelocity(1000, mMaxVelocity);
                        final float velocityX = verTracker.getXVelocity(mPointerId);
                        if (Math.abs(velocityX) > 1000) {//�����ٶȳ�����ֵ
                            if (velocityX < -1000) {
                                if (isLeftSwipe) {//��
                                    //ƽ��չ��Menu
                                    smoothExpand();

                                } else {
                                    //ƽ���ر�Menu
                                    smoothClose();
                                }
                            } else {
                                if (isLeftSwipe) {//��
                                    // ƽ���ر�Menu
                                    smoothClose();
                                } else {
                                    //ƽ��չ��Menu
                                    smoothExpand();

                                }
                            }
                        } else {
                            if (Math.abs(getScrollX()) > mLimit) {//������жϻ�������
                                //ƽ��չ��Menu
                                smoothExpand();
                            } else {
                                // ƽ���ر�Menu
                                smoothClose();
                            }
                        }
                    }
                    //�ͷ�
                    releaseVelocityTracker();
                    //LogUtils.i(TAG, "onTouch A ACTION_UP ACTION_CANCEL:velocityY:" + velocityX);
                    isTouching = false;//û����ָ��������
                    break;
                default:
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //add by zhangxutong 2016 12 07 begin:
        //��ֹ�໬ʱ������¼����ܸ��š�
        if (isSwipeEnable) {
            switch (ev.getAction()) {
                //add by zhangxutong 2016 11 04 begin :
                // fix �����¼��Ͳ໬�ĳ�ͻ��
                case MotionEvent.ACTION_MOVE:
                    //���λ���ʱ���¼�
                    if (Math.abs(ev.getRawX() - mFirstP.x) > mScaleTouchSlop) {
                        return true;
                    }
                    break;
                //add by zhangxutong 2016 11 04 end
                case MotionEvent.ACTION_UP:
                    //Ϊ���ڲ໬ʱ��������View�ĵ���¼�
                    if (isLeftSwipe) {
                        if (getScrollX() > mScaleTouchSlop) {
                            //add by 2016 09 10 ���һ����������~ ��Ȼ��������໬�˵� �ҹ���л��
                            //�����ж�����������������ε�������������⣬�������¼��������µĵġ�����
                            if (ev.getX() < getWidth() - getScrollX()) {
                                //2016 10 22 add , ��QQ���໬�˵�չ��ʱ������������򣬹رղ໬�˵���
                                if (isUnMoved) {
                                    smoothClose();
                                }
                                return true;//true��ʾ����
                            }
                        }
                    } else {
                        if (-getScrollX() > mScaleTouchSlop) {
                            if (ev.getX() > -getScrollX()) {//�����Χ�ڲ˵��� ����
                                //2016 10 22 add , ��QQ���໬�˵�չ��ʱ������������򣬹رղ໬�˵���
                                if (isUnMoved) {
                                    smoothClose();
                                }
                                return true;
                            }
                        }
                    }
                    //add by zhangxutong 2016 11 03 begin:
                    // �ж���ָ��ʼ��㣬����������ڻ����ˣ�������һ�е���¼���
                    if (isUserSwiped) {
                        return true;
                    }
                    //add by zhangxutong 2016 11 03 end

                    break;
            }
            //ģ��IOS �����������رգ�
            if (iosInterceptFlag) {
                //IOSģʽ�������ҵ�ǰ�в˵���View���Ҳ����Լ��� ���ص���¼�����View
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    /**
     * ƽ��չ��
     */
    private ValueAnimator mExpandAnim, mCloseAnim;

    private boolean isExpand;//����ǰ�Ƿ���չ��״̬ 2016 11 03 add

    public void smoothExpand() {
        //Log.d(TAG, "smoothExpand() called" + this);
        /*mScroller.startScroll(getScrollX(), 0, mRightMenuWidths - getScrollX(), 0);
        invalidate();*/
        //չ���ͼ���ViewCache��
        mViewCache = SwipeMenuLayout.this;

        //2016 11 13 add �໬�˵�չ��������content����
        if (null != mContentView) {
            mContentView.setLongClickable(false);
        }

        cancelAnim();
        mExpandAnim = ValueAnimator.ofInt(getScrollX(), isLeftSwipe ? mRightMenuWidths : -mRightMenuWidths);
        mExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        mExpandAnim.setInterpolator(new OvershootInterpolator());
        mExpandAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExpand = true;
            }
        });
        mExpandAnim.setDuration(300).start();
    }

    /**
     * ÿ��ִ�ж���֮ǰ��Ӧ����ȡ��֮ǰ�Ķ���
     */
    private void cancelAnim() {
        if (mCloseAnim != null && mCloseAnim.isRunning()) {
            mCloseAnim.cancel();
        }
        if (mExpandAnim != null && mExpandAnim.isRunning()) {
            mExpandAnim.cancel();
        }
    }

    /**
     * ƽ���ر�
     */
    public void smoothClose() {
        //Log.d(TAG, "smoothClose() called" + this);
/*        mScroller.startScroll(getScrollX(), 0, -getScrollX(), 0);
        invalidate();*/
        mViewCache = null;

        //2016 11 13 add �໬�˵�չ��������content����
        if (null != mContentView) {
            mContentView.setLongClickable(true);
        }

        cancelAnim();
        mCloseAnim = ValueAnimator.ofInt(getScrollX(), 0);
        mCloseAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                scrollTo((Integer) animation.getAnimatedValue(), 0);
            }
        });
        mCloseAnim.setInterpolator(new AccelerateInterpolator());
        mCloseAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isExpand = false;

            }
        });
        mCloseAnim.setDuration(300).start();
        //LogUtils.d(TAG, "smoothClose() called with:getScrollX() " + getScrollX());
    }


    /**
     * @param event ��VelocityTracker���MotionEvent
     * @see VelocityTracker#obtain()
     * @see VelocityTracker#addMovement(MotionEvent)
     */
    private void acquireVelocityTracker(final MotionEvent event) {
        if (null == mVelocityTracker) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
    }

    /**
     * * �ͷ�VelocityTracker
     *
     * @see VelocityTracker#clear()
     * @see VelocityTracker#recycle()
     */
    private void releaseVelocityTracker() {
        if (null != mVelocityTracker) {
            mVelocityTracker.clear();
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }
    }

    //ÿ��ViewDetach��ʱ���ж�һ�� ViewCache�ǲ����Լ���������Լ����رղ໬�˵�����ViewCache����Ϊnull��
    // ���ɣ�1 ��ֹ�ڴ�й©(ViewCache��һ����̬����)
    // 2 �໬ɾ�����Լ������View��Recycler���գ����ã���һ��������Ļ��View��״̬Ӧ������ͨ״̬��������չ��״̬��
    @Override
    protected void onDetachedFromWindow() {
        if (this == mViewCache) {
            mViewCache.smoothClose();
            mViewCache = null;
        }
        super.onDetachedFromWindow();
    }

    //չ��ʱ����ֹ����
    @Override
    public boolean performLongClick() {
        if (Math.abs(getScrollX()) > mScaleTouchSlop) {
            return false;
        }
        return super.performLongClick();
    }

    //ƽ������ ���� �����Զ���ʵ��
/*    @Override
    public void computeScroll() {
        //�ж�Scroller�Ƿ�ִ����ϣ�
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            //֪ͨView�ػ�-invalidate()->onDraw()->computeScroll()
            invalidate();
        }
    }*/

    /**
     * ���ٹرա�
     * ���� ����໬�˵��ϵ�ѡ��,ͬʱ���������ٹر�(ɾ�� �ö�)��
     * ���������ListView���Ǳ�����õģ�
     * ��RecyclerView�����������������mAdapter.notifyItemRemoved(pos)�������õ��á�
     */
    public void quickClose() {
        if (this == mViewCache) {
            //��ȡ��չ������
            cancelAnim();
            mViewCache.scrollTo(0, 0);//�ر�
            mViewCache = null;
        }
    }

}