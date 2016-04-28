package com.sorry.stalker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;


    public class ObservableHorizontalScrollView extends HorizontalScrollView{

        private Runnable scrollerTask;
        private int initialPosition;

        private int newCheck = 100;
        private static final String TAG = "MyScrollView";

        public interface OnScrollStoppedListener{
            void onScrollStopped();
        }

        private OnScrollStoppedListener onScrollStoppedListener;

        public ObservableHorizontalScrollView(Context context, AttributeSet attrs) {
            super(context, attrs);

            scrollerTask = new Runnable() {

                public void run() {

                    int newPosition = getScrollY();
                    if(initialPosition - newPosition == 0){//has stopped

                        if(onScrollStoppedListener!=null){

                            onScrollStoppedListener.onScrollStopped();
                        }
                    }else{
                        initialPosition = getScrollY();
                        ObservableHorizontalScrollView.this.postDelayed(scrollerTask, newCheck);
                    }
                }
            };
        }

        public void setOnScrollStoppedListener(ObservableHorizontalScrollView.OnScrollStoppedListener listener){
            onScrollStoppedListener = listener;
        }

        public void startScrollerTask(){

            initialPosition = getScrollY();
            ObservableHorizontalScrollView.this.postDelayed(scrollerTask, newCheck);
        }

    }