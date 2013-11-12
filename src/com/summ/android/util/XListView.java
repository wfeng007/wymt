/**
 * @file XListView.java
 * @package me.maxwin.view
 * @create Mar 18, 2012 6:28:41 PM
 * @author Maxwin
 * @description An ListView support (a) Pull down to refresh, (b) Pull up to load more.
 * 		Implement IXListViewListener, and see stopRefresh() / stopLoadMore().
 */
package com.summ.android.util;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.summ.wymt.R;

public class XListView extends ListView implements OnScrollListener {

	private float mLastY = -1; // save event y
	private Scroller mScroller; // used for scroll back
	private OnScrollListener mScrollListener; // user's scroll listener

	// the interface to trigger refresh and load more.
	private IXListViewListener mListViewListener;

	// -- header view
	private XListViewHeader mHeaderView;
	// header view content, use it to calculate the Header's height. And hide it
	// when disable pull refresh.
	private RelativeLayout mHeaderViewContent;
	private TextView mHeaderTimeView;
	private int mHeaderViewHeight; // header view's height
	private boolean mEnablePullRefresh = true;
	private boolean mPullRefreshing = false; // is refreashing.

	// -- footer view
	private XListViewFooter mFooterView;
	private boolean mEnablePullLoad;
	private boolean mPullLoading;
	private boolean mIsFooterReady = false;
	
	// total list items, used to detect is at the bottom of listview.
	private int mTotalItemCount;

	// for mScroller, scroll back from header or footer.
	private int mScrollBack;
	private final static int SCROLLBACK_HEADER = 0;
	private final static int SCROLLBACK_FOOTER = 1;

	private final static int SCROLL_DURATION = 400; // scroll back duration
	private final static int PULL_LOAD_MORE_DELTA = 50; // when pull up >= 50px
														// at bottom, trigger
														// load more.
	private final static float OFFSET_RADIO = 1.8f; // support iOS like pull
													// feature.

	/**
	 * @param context
	 */
	public XListView(Context context) {
		super(context);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initWithContext(context);
	}

	public XListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initWithContext(context);
	}

	/**
	 * 初始化
	 * @param context
	 */
	private void initWithContext(Context context) {
		
		mScroller = new Scroller(context, new DecelerateInterpolator());
		// XListView need the scroll event, and it will dispatch the event to
		// user's listener (as a proxy).
		super.setOnScrollListener(this);

		// init header view
		mHeaderView = new XListViewHeader(context);
		mHeaderViewContent = (RelativeLayout) mHeaderView
				.findViewById(R.id.xlistview_header_content);
		mHeaderTimeView = (TextView) mHeaderView
				.findViewById(R.id.xlistview_header_time);
		addHeaderView(mHeaderView);

		// init footer view
		mFooterView = new XListViewFooter(context);

		// init header height
		mHeaderView.getViewTreeObserver().addOnGlobalLayoutListener(
				new OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						mHeaderViewHeight = mHeaderViewContent.getHeight();
						getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
					}
				});
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// make sure XListViewFooter is the last footer view, and only add once.
		if (mIsFooterReady == false) {
			mIsFooterReady = true;
			addFooterView(mFooterView);
		}
		super.setAdapter(adapter);
	}

	/**
	 * enable or disable pull down refresh feature.
	 * 
	 * @param enable
	 */
	public void setPullRefreshEnable(boolean enable) {
		mEnablePullRefresh = enable;
		if (!mEnablePullRefresh) { // disable, hide the content
			mHeaderViewContent.setVisibility(View.INVISIBLE);
		} else {
			mHeaderViewContent.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 设置下拉有效
	 * enable or disable pull up load more feature.
	 * 
	 * @param enable
	 */
	public void setPullLoadEnable(boolean enable) {
		mEnablePullLoad = enable;
		if (!mEnablePullLoad) {
			mFooterView.hide(); //这里直接就hiden了。TODO 增加一种状态，下拉不更新但是click仍然有效。
			mFooterView.setOnClickListener(null); 
		} else {
			mPullLoading = false;
			mFooterView.show();
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
			// both "pull up" and "click" will invoke load more.
			mFooterView.setOnClickListener(new OnClickListener() { //这里直接提供一个onclick方法提供给
				@Override
				public void onClick(View v) {
					Log.i("xlistview", "clicked");
					startLoadMore();
				}
			});
		}
	}

	/**
	 * 一般在用户执行完数据更新以及界面render后 还原列表的刷新展示效果
	 * stop refresh, reset header view.
	 */
	public void stopRefresh() {
		if (mPullRefreshing == true) {
			mPullRefreshing = false;
			resetHeaderHeight();
		}
	}

	/**
	 * 一般在用户执行完数据更新以及界面render后 还原列表的加载展示效果
	 * stop load more, reset footer view.
	 */
	public void stopLoadMore() {
		if (mPullLoading == true) {
			mPullLoading = false;
			mFooterView.setState(XListViewFooter.STATE_NORMAL);
		}
	}

	/**
	 * 设置刷新的时间的文字。
	 * set last refresh time
	 * 
	 * @param time
	 */
	public void setRefreshTime(String time) {
		mHeaderTimeView.setText(time);
	}

	private void invokeOnScrolling() {
		if (mScrollListener instanceof OnXScrollListener) {
			OnXScrollListener l = (OnXScrollListener) mScrollListener;
			l.onXScrolling(this);
		}
	}

	/**
	 * 按照偏移量重绘header高度
	 * 同时变更状态。
	 * @param delta
	 */
	private void updateHeaderHeight(float delta) {
		mHeaderView.setVisiableHeight((int) delta
				+ mHeaderView.getVisiableHeight());
		if (mEnablePullRefresh && !mPullRefreshing) { // 未处于刷新状态，更新箭头
			if (mHeaderView.getVisiableHeight() > mHeaderViewHeight) {
				mHeaderView.setState(XListViewHeader.STATE_READY);
			} else {
				mHeaderView.setState(XListViewHeader.STATE_NORMAL);
			}
		}
		setSelection(0); // scroll to top each time
	}

	/**
	 * 重置header高度
	 * reset header view's height.
	 */
	private void resetHeaderHeight() {
		int height = mHeaderView.getVisiableHeight();
		if (height == 0) // not visible.
			return;
		// refreshing and header isn't shown fully. do nothing.
		if (mPullRefreshing && height <= mHeaderViewHeight) {
			return;
		}
		int finalHeight = 0; // default: scroll back to dismiss header.
		// is refreshing, just scroll back to show all the header.
		if (mPullRefreshing && height > mHeaderViewHeight) {
			finalHeight = mHeaderViewHeight;
		}
		
		//这个可能就是缓慢缩回的效果。
		mScrollBack = SCROLLBACK_HEADER;
		mScroller.startScroll(0, height, 0, finalHeight - height,
				SCROLL_DURATION); //0.4秒
		// trigger computeScroll
		invalidate(); //过程中不可用
	}

	/**
	 * 更新footer的高度
	 * @param delta
	 */
	private void updateFooterHeight(float delta) {
		int height = mFooterView.getBottomMargin() + (int) delta;
		if (mEnablePullLoad && !mPullLoading) {
			if (height > PULL_LOAD_MORE_DELTA) { // height enough to invoke load
													// more.
				mFooterView.setState(XListViewFooter.STATE_READY);
			} else {
				mFooterView.setState(XListViewFooter.STATE_NORMAL);
			}
		}
		mFooterView.setBottomMargin(height); //设置margin的高度

//		setSelection(mTotalItemCount - 1); // scroll to bottom
	}

	/**
	 * 重置reset高度
	 */
	private void resetFooterHeight() {
		int bottomMargin = mFooterView.getBottomMargin();
		if (bottomMargin > 0) {
			mScrollBack = SCROLLBACK_FOOTER;
			mScroller.startScroll(0, bottomMargin, 0, -bottomMargin,
					SCROLL_DURATION);
			invalidate();
		}
	}

	/**
	 * 设置整体状态
	 * 执行侦听器
	 */
	private void startLoadMore() {
		mPullLoading = true;
		mFooterView.setState(XListViewFooter.STATE_LOADING);
		if (mListViewListener != null) {
			mListViewListener.onLoadMore();
		}
	}

	/**
	 * 捕获pull操作
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		//
		if (mLastY == -1) {
			mLastY = ev.getRawY();
		}

		//处理事件
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN://按下
			mLastY = ev.getRawY(); //记录按下时的位置Y垂直位置
			break;
		case MotionEvent.ACTION_MOVE: //移动，即按下时的移动
			final float deltaY = ev.getRawY() - mLastY; //计算偏移量当前y位置与之前y位置的偏移。
			mLastY = ev.getRawY(); //将现有偏移量设置为最后一次偏移量备用。
			if (getFirstVisiblePosition() == 0 //屏幕上最上方可现实的元素为0位置元素
					&& (mHeaderView.getVisiableHeight() > 0 || deltaY > 0)) { //且headerview可现现实或下拉偏移量为正数（往下拉）
				// the first item is showing, header has shown or pull down.
				updateHeaderHeight(deltaY / OFFSET_RADIO);//更新header高度
				invokeOnScrolling(); //通知OnXScrollListener监听器 扩展用？
			} else if (getLastVisiblePosition() == mTotalItemCount - 1 //现实的屏幕的最后一个元素是 listview的最后一个元素
					&& (mFooterView.getBottomMargin() > 0 || deltaY < 0)) { //并且move偏移量为负数(往上拉)
				// last item, already pulled up or want to pull up.
				updateFooterHeight(-deltaY / OFFSET_RADIO); //重绘footer的高度。
			}
			break;
		default: //其他类型则放开拖拉
			mLastY = -1; // reset //重置最后拖拉位置记录器
			if (getFirstVisiblePosition() == 0) { //如果第一个元素显示在屏幕上 ，则认为需要刷新？FIXME 只有一个元素时呢？
				// invoke refresh 
				if (mEnablePullRefresh // 开关有效
						&& mHeaderView.getVisiableHeight() > mHeaderViewHeight) { // 当header被拉出后高度高于本身定义高度后
					
					//既然startLoadMore可以独立出来，那么刷新也可以暴露为一个public的api吧。
					mPullRefreshing = true; //状态为正在刷新 内部状态
					mHeaderView.setState(XListViewHeader.STATE_REFRESHING); //整体状态为
					if (mListViewListener != null) { //调用侦听器
						mListViewListener.onRefresh(); 
					}
					
				}
				resetHeaderHeight(); //重置header高度
			} else if (getLastVisiblePosition() == mTotalItemCount - 1) { //如果最后一个元素显示在屏幕，则是增加内容？FIXME
				// invoke load more.
				if (mEnablePullLoad // 开关有效
						&& mFooterView.getBottomMargin() > PULL_LOAD_MORE_DELTA) { // 被拉出的margin超过50px时
					startLoadMore(); //执行加载
				}
				resetFooterHeight(); //重置footer高度
			}
			break; 
		}//所有其他情况与一般listview一样
		return super.onTouchEvent(ev); //原始功能继续实现。
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			if (mScrollBack == SCROLLBACK_HEADER) {
				mHeaderView.setVisiableHeight(mScroller.getCurrY());
			} else {
				mFooterView.setBottomMargin(mScroller.getCurrY());
			}
			postInvalidate();
			invokeOnScrolling();
		}
		super.computeScroll();
	}

	@Override
	public void setOnScrollListener(OnScrollListener l) {
		mScrollListener = l;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (mScrollListener != null) {
			mScrollListener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// send to user's listener
		mTotalItemCount = totalItemCount;
		if (mScrollListener != null) {
			mScrollListener.onScroll(view, firstVisibleItem, visibleItemCount,
					totalItemCount);
		}
	}

	public void setXListViewListener(IXListViewListener l) {
		mListViewListener = l;
	}

	/**
	 * you can listen ListView.OnScrollListener or this one. it will invoke
	 * onXScrolling when header/footer scroll back.
	 */
	public interface OnXScrollListener extends OnScrollListener {
		public void onXScrolling(View view);
	}

	/**
	 * implements this interface to get refresh/load more event.
	 */
	public interface IXListViewListener {
		public void onRefresh();

		public void onLoadMore();
	}
}
