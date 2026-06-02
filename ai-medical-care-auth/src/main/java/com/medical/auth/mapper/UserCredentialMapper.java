package com.medical.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.medical.auth.model.entity.UserCredential;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户认证凭证 Mapper
 *
 * @author Architect Team
 */
@Mapper
public interface UserCredentialMapper extends BaseMapper<UserCredential> {

    /**
     * 根据用户名查询
     */
    @Select("SELECT * FROM user_credential WHERE username = #{username} AND deleted = 0")
    UserCredential selectByUsername(String username);
}
