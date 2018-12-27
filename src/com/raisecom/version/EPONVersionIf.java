package com.raisecom.version;

import com.raisecom.nms.platform.cnet.ObjService;

/**
 * Created by ligy-008494 on 2018/11/13.
 */
public interface EPONVersionIf {
    public String getVersion(ObjService devInfo, VersionElement verInfo);
}
