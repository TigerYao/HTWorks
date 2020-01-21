package com.huatu.handheld_huatu.business.play.fragment;

import android.view.View;
import android.widget.ImageView;

import com.huatu.handheld_huatu.R;
import com.huatu.handheld_huatu.base.BaseListFragment;
import com.huatu.handheld_huatu.base.BaseResponseModel;
import com.huatu.handheld_huatu.base.NetResponse;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineBean;
import com.huatu.handheld_huatu.business.play.bean.CourseOutlineItemBean;
import com.huatu.handheld_huatu.network.ServiceProvider;
import com.huatu.handheld_huatu.utils.DisplayUtil;
import com.huatu.handheld_huatu.utils.Method;
import com.huatu.handheld_huatu.utils.RxUtils;
import com.huatu.handheld_huatu.view.CommonAdapter;
import com.huatu.handheld_huatu.view.MultiItemTypeSupport;

import java.util.ArrayList;

import retrofit2.http.DELETE;

/**
 * Created by saiyuan on 2018/7/30.
 */
@Deprecated
public class CourseSyllabusListFragment extends BaseListFragment<CourseOutlineItemBean> {
    private String courseId;
    private int courseType;
    private int curPage;
    private int floor = 3;
    protected final ArrayList<CourseOutlineItemBean> fatherList = new ArrayList<>();

    @Override
    protected void onInitView() {
        super.onInitView();
        args = getArguments();
        courseId = args.getString("course_id");
        compositeSubscription = RxUtils.getNewCompositeSubIfUnsubscribed(compositeSubscription);
        pageSize = 500;
        listView.setDividerHeight(0);
    }

    @Override
    public void initAdapter() {
        mAdapter = new CommonAdapter<CourseOutlineItemBean>(dataList, new MultiItemTypeSupport<CourseOutlineItemBean>() {
            @Override
            public int getLayoutId(int position, CourseOutlineItemBean item) {
                if (item.type == 0) {
                    return R.layout.item_course_syllabus_stage_layout;
                } else if(item.type == 1) {
                    return R.layout.item_course_syllabus_course_layout;
                } else {
                    return R.layout.item_course_syllabus_lesson_layout;
                }
            }

            @Override
            public int getViewTypeCount() {
                return 3;
            }

            @Override
            public int getItemViewType(int position, CourseOutlineItemBean item) {
                return item.type;
            }
        }) {
            @Override
            public void convert(final ViewHolder holder, final CourseOutlineItemBean item, final int position) {
                if (item.type == 0) {
                    holder.setText(R.id.course_syllabus_stage_name, item.title);
                    holder.setText(R.id.course_syllabus_stage_time,
                            "学习时长（" + item.studyLength + "）");
                    holder.setText(R.id.course_syllabus_stage_lesson_num,
                            item.classHour + "课时");
                    ImageView arrowView = holder.getView(R.id.course_syllabus_stage_arrow);
                    if(item.hasChildren == 1) {
                        arrowView.setVisibility(View.VISIBLE);
                    } else {
                        arrowView.setVisibility(View.GONE);
                    }
                    if (item.isExpand) {
                        arrowView.setRotation(0);
                    } else {
                        arrowView.setRotation(180);
                    }
                    arrowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.isExpand) {
                                item.isExpand = false;
                            } else {
                                item.isExpand = true;
                            }
                            if(Method.isListEmpty(item.childList)) {
                                CourseSyllabusListFragment.this.getData(item.id, true);
                            } else {
                                setSuitDataList();
                            }
                        }
                    });
                } else if (item.type == 1) {
                    holder.setText(R.id.course_syllabus_course_name, item.title);
                    holder.setText(R.id.course_syllabus_course_num,
                            item.classHour + "课时");
                    ImageView arrowView = holder.getView(R.id.course_syllabus_course_arrow);
                    if(item.hasChildren == 1) {
                        arrowView.setVisibility(View.VISIBLE);
                    } else {
                        arrowView.setVisibility(View.GONE);
                    }
                    if (item.isExpand) {
                        arrowView.setRotation(0);
                    } else {
                        arrowView.setRotation(180);
                    }
                    arrowView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (item.isExpand) {
                                item.isExpand = false;
                            } else {
                                item.isExpand = true;
                            }
                            if(Method.isListEmpty(item.childList)) {
                                CourseSyllabusListFragment.this.getData(item.id, true);
                            } else {
                                setSuitDataList();
                            }
                        }
                    });
                    if(floor == 2) {
                        holder.getConvertView().setPadding(DisplayUtil.dp2px(15),
                                DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(12));
                    }
                } else if (item.type == 2) {
                    holder.setText(R.id.course_syllabus_lesson_index, item.serialNumber + "");
                    holder.setText(R.id.course_syllabus_lesson_name, item.title);
                    holder.setText(R.id.course_syllabus_lesson_time, item.videoLength);
                    holder.setText(R.id.course_syllabus_lesson_teacher, item.teacher);
                    if(floor != 3) {
                        holder.getConvertView().setPadding(DisplayUtil.dp2px(15),
                                DisplayUtil.dp2px(10), DisplayUtil.dp2px(10), DisplayUtil.dp2px(12));
                    }
                }
            }
        };
    }

    private void setSuitDataList() {
        dataList.clear();
        for (CourseOutlineItemBean courseChild : fatherList) {
            dataList.add(courseChild);
            if (courseChild.isExpand && !Method.isListEmpty(courseChild.childList)) {
                for (CourseOutlineItemBean lesson : courseChild.childList) {
                    dataList.add(lesson);
                    if (lesson.isExpand && !Method.isListEmpty(lesson.childList)) {
                        dataList.addAll(lesson.childList);
                    }
                }
            }
        }
        if(dataList.isEmpty()) {
            showEmptyView();
        } else {
            hideEmptyView();
        }
        mAdapter.setDataAndNotify(dataList);
    }

    @Override
    public boolean isBottomButtons() {
        return false;
    }

    @Override
    public View getBottomLayout() {
        return null;
    }

    @Override
    public boolean hasToolbar() {
        return false;
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public void onRefresh() {
        getData(0, true);
    }

    @Override
    public void onLoadMore() {
        getData(0, false);
    }

    @Override
    protected void onLoadData() {
        getData(0, true);
    }

    private void getData(final int parentId, final boolean isRefresh) {
        mActivity.showProgress();
        ServiceProvider.getOutlineDetail(compositeSubscription, courseId, 1, pageSize, parentId,0, new NetResponse(){
            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mActivity.hideProgress();
            }

            @Override
            public void onSuccess(BaseResponseModel model) {
                super.onSuccess(model);
                mActivity.hideProgress();
                CourseOutlineBean syllabusBean = (CourseOutlineBean) model.data;
                if(parentId == 0) {
                    if(isRefresh) {
                        fatherList.clear();
                    }
                    fatherList.addAll(syllabusBean.list);
                    if(syllabusBean.list.get(0).type == 0) {
                        floor = 3;
                    } else if(syllabusBean.list.get(0).type == 1) {
                        floor = 2;
                    } else if(syllabusBean.list.get(0).type == 2) {
                        floor = 1;
                    }
                } else {
                    boolean isAdd = false;
                    for(int i = 0; i < fatherList.size(); i++) {
                        CourseOutlineItemBean data = fatherList.get(i);
                        if(data.id == parentId) {
                            data.childList = new ArrayList<>();
                            data.childList.addAll(syllabusBean.list);
                            isAdd = true;
                        } else if(!Method.isListEmpty(data.childList)) {
                            for(int j = 0; j < data.childList.size(); j++) {
                                CourseOutlineItemBean childData = data.childList.get(i);
                                if(childData.id == parentId) {
                                    childData.childList = new ArrayList<>();
                                    childData.childList.addAll(syllabusBean.list);
                                    isAdd = true;
                                    break;
                                }
                            }
                        }
                        if(isAdd) {
                            break;
                        }
                    }
                }
                CourseSyllabusListFragment.this.onSuccess(dataList, true);
                setSuitDataList();
            }
        });
    }
}
