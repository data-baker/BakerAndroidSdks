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
    public void mouldInfo(Mould mould);

    /**
     * 根据queryId分页查询mould信息回调
     * @param list
     */
    public void mouldList(List<Mould> list);

    /**
     * 错误信息回调
     *
     * @param errorCode
     * @param message
     */
    public void onMouldError(int errorCode, String message);
}
