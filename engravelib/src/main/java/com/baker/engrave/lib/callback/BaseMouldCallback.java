package com.baker.engrave.lib.callback;

import com.baker.engrave.lib.bean.Mould;

import java.util.List;

/**
 * Create by hsj55
 * 2020/3/10
 */
public interface BaseMouldCallback {
    /**
     * 根据mouldId查询mould信息回调
     * @param mould
     */
    void mouldInfo(Mould mould);

    /**
     * 根据queryId分页查询mould信息回调
     * @param list
     */
    void mouldList(List<Mould> list);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    void onMouldError(int errorCode, String message);
}
