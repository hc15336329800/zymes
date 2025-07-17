package cn.jb.boot.system.vo.response;

import cn.jb.boot.framework.annotation.DictTrans;
import cn.jb.boot.framework.com.DictType;
import cn.jb.boot.framework.com.entity.TreeSelect;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeptTreeSelectResponse extends TreeSelect {

    @DictTrans(type = DictType.USER_INFO)
    private String createdBy;
    @DictTrans(type = DictType.USER_INFO)
    private String directorUid;

}
